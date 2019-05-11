package de.aschoerk.javaconv;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Created by aschoerk on 03.05.16.
 */
public class IdTrackerVisitor extends VoidVisitorAdapter<IdTracker> {

    private boolean inAssignTarget = false;

    @Override
    public void visit(final CompilationUnit n, final IdTracker arg) {
        visitComment(n.getComment(), arg);
        if (n.getPackage() != null) {
            arg.setPackageName(n.getPackage().getPackageName());
            n.getPackage().accept(this, arg);
        }
        if (n.getImports() != null) {
            for (final ImportDeclaration i : n.getImports()) {
                arg.addImport(new Import(i.getName().toString(), i.isStatic(), i.isAsterisk()));
                i.accept(this, arg);
            }
        }
        if (n.getTypes() != null) {
            for (final TypeDeclaration typeDeclaration : n.getTypes()) {
                typeDeclaration.accept(this, arg);
            }
        }
    }

    @Override
    public void visit(ClassOrInterfaceType n, IdTracker arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, IdTracker arg) {
        arg.pushBlock(n);
        arg.addDeclaration(n.getName(), Pair.of(null,n));
        super.visit(n, arg);
        arg.popBlock();
    }

    @Override
    public void visit(EnumDeclaration n, IdTracker arg) {
        arg.pushBlock(n);
        arg.addDeclaration(n.getName(), Pair.of(null,n));
        super.visit(n, arg);
        arg.popBlock();
    }

    private void visitComment(final Comment n, final IdTracker arg) {
        if (n != null) {
            n.accept(this, arg);
        }
    }

    @Override
    public void visit(AssignExpr n, IdTracker arg) {

        visitComment(n.getComment(), arg);
        inAssignTarget = true;
        try {
            n.getTarget().accept(this, arg);
        }
        finally {
            inAssignTarget = false;
        }
        n.getValue().accept(this, arg);

    }



    @Override
    public void visit(UnaryExpr n, IdTracker arg) {
        try {
            switch (n.getOperator()) {
                case posIncrement:
                case posDecrement:
                case preIncrement:
                case preDecrement:
                    inAssignTarget = true;
                    break;
                default:
                    inAssignTarget = false;
            }
            n.getExpr().accept(this, arg);
        }
        finally {
            inAssignTarget = false;
        }
    }

    @Override
    public void visit(BlockStmt n, IdTracker arg) {
        arg.pushBlock(n);
        super.visit(n, arg);
        arg.popBlock();
    }

    @Override
    public void visit(ForStmt n, IdTracker arg) {
        arg.pushBlock(n);
        super.visit(n, arg);
        arg.popBlock();
    }

    @Override
    public void visit(ForeachStmt n, IdTracker arg) {
        arg.pushBlock(n);
        super.visit(n, arg);
        arg.popBlock();
    }

    @Override
    public void visit(MethodCallExpr n, IdTracker arg) {
        if (n.getScope() instanceof NameExpr) {
            NameExpr ne = (NameExpr)n.getScope();
            Class clazz = identifyaClass(arg, ne.getName());
            if (clazz != null) {
                String methodName = n.getName();
                Method[] ms = clazz.getMethods();
                Set<Method> candidates = new HashSet<>();
                for (Method m : ms) {
                    if(m.getName().equals(methodName)) {
                        candidates.add(m);
                    }
                }
                Method resulting = null;
                if(candidates.size() == 1) {
                    resulting = candidates.iterator().next();
                }
                else {
                    List<Method> matching = candidates.stream().filter(
                            m -> m.getParameterCount() == n.getArgs().size()
                                    || m.isVarArgs() && m.getParameterCount() <= n.getArgs().size()
                    ).collect(Collectors.toList());
                    if(matching.size() == 1) {
                        resulting = matching.get(0);
                    }
                }
                if (resulting != null) {
                    for (int i = 0; i < resulting.getParameterCount(); i++) {

                        java.lang.reflect.Parameter p = resulting.getParameters()[i];
                        if (n.getArgs().size() > i) {
                            arg.putType(n.getArgs().get(i), p.getType());
                        }
                    }
                }
            }

        }
        arg.addUsage(n.getName(),n);
        super.visit(n, arg);
    }

    @Override
    public void visit(MethodDeclaration n, IdTracker arg) {
        try {
            arg.addDeclaration(n.getName(), Pair.of(null,n));
        } catch( RuntimeException ex) {
            ; // ignore duplicate Methods with the same name. Let it be declared just once, so that self can be constructed.
        }
        if (n.getThrows() != null && n.getThrows().size() != 0) {
            arg.setHasThrows(n.getName());
        }
        arg.pushBlock(n);
        super.visit(n, arg);
        arg.popBlock();
    }

    @Override
    public void visit(NameExpr n, IdTracker arg) {
        if (inAssignTarget) {
            arg.addChange(n.getName(), n);
        } else {
            arg.addUsage(n.getName(), n);
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(QualifiedNameExpr n, IdTracker arg) {
        if (inAssignTarget) {
            arg.addChange(n.getName(), n);
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(final VariableDeclarationExpr n, final IdTracker arg) {
        TypeDescription typeDescr = getTypeDescription(arg, n.getType());
        String type = getNameOfType(n.getType());

        if (typeDescr != null && arg.isFloat(typeDescr.getClazz())) {
            arg.putType(n, typeDescr.getClazz());
            if (typeDescr.getArrayCount() > 0) {
                try {
                    Node initializer = n.getChildrenNodes().get(1).getChildrenNodes().get(1);
                    if (!(initializer instanceof MethodCallExpr)) {
                        List<Node> nodes = initializer.getChildrenNodes();
                        for (Node child : nodes) {
                            arg.putType(child, Double.TYPE);
                        }
                    }
                } catch (RuntimeException re) {

                }
            }
        }
        super.visit(n, arg);
    }


    @Override public void visit(final ExpressionStmt n, final IdTracker arg) {
        if (n.getExpression() instanceof VariableDeclarationExpr) {
            VariableDeclarationExpr ve = (VariableDeclarationExpr) n.getExpression();
            ve.getType();
        }
        super.visit(n, arg);
    }


    String getNameOfType(Type t) {
        if (t instanceof  ReferenceType) {
            ReferenceType rtype = (ReferenceType)t;
            return getNameOfType(rtype.getType());
        } else if (t instanceof ClassOrInterfaceType) {
            ClassOrInterfaceType ct = (ClassOrInterfaceType)t;
            return ((ClassOrInterfaceType) t).getName();
        }
        return null;
    }

    TypeDescription typeOf(VariableDeclaratorId n, IdTracker arg) {
        Type t = null;
        if (n.getParentNode().getParentNode() instanceof FieldDeclaration) {
            FieldDeclaration fieldDeclaration = (FieldDeclaration) n.getParentNode().getParentNode();
            t = fieldDeclaration.getType();
        } else if (n.getParentNode() instanceof Parameter) {
            Parameter p = (Parameter) n.getParentNode();
            t = p.getType();
        } else if (n.getParentNode().getParentNode() instanceof VariableDeclarationExpr) {
            VariableDeclarationExpr variableDeclarationExpr = (VariableDeclarationExpr) n.getParentNode().getParentNode();
            t = variableDeclarationExpr.getType();
        }
        if (t != null) {
            return getTypeDescription(arg, t);
        }
        return null;
    }

    private TypeDescription getTypeDescription(final IdTracker arg, final Type t) {
        String name = getNameOfType(t);
        Class clazz = identifyaClass(arg, name);
        if (t instanceof ReferenceType) {
            ReferenceType rtype = (ReferenceType)t;
            if (clazz == null) {
                clazz = getPotentialPrimitiveType(rtype.getType());
            }
            if (clazz != null)
                return new TypeDescription(rtype.getArrayCount(), clazz);
        }
        if (clazz == null) {
            clazz = getPotentialPrimitiveType(t);
        }
        if (clazz == null)
            return null;
        else {
            return new TypeDescription(0, clazz);
        }
    }

    private Class identifyaClass(final IdTracker arg, final String name) {
        Class clazz = null;
        if (name != null) {
            for (Import i : arg.getImports()) {
                if (!i.isStaticImport()) {
                    if(i.isWildcardImport()) {
                        clazz = forName(i.getImportString() + "." + name);
                    }
                    else {
                        if(i.getImportString().endsWith("." + name)) {
                            final String importString = i.getImportString();
                            clazz = forName(importString);
                        }
                    }
                }
            }
            if (clazz == null) {
                clazz = forName("java.lang." + name);
            }
            if (clazz == null) {
                clazz = forName(arg.getPackageName() + "." + name);
            }
        }
        return clazz;
    }

    private Class getPotentialPrimitiveType(final Type t) {
        if (t instanceof PrimitiveType) {
            PrimitiveType pt = (PrimitiveType)t;
            switch (pt.getType().name()) {
                case "Byte": return Byte.TYPE;
                case "Short": return Short.TYPE;
                case "Int": return Integer.TYPE;
                case "Long": return Long.TYPE;
                case "Float": return Float.TYPE;
                case "Double": return Double.TYPE;
                case "Char": return Character.TYPE;
                case "Boolean": return Boolean.TYPE;
                case "void": return Void.TYPE;
            }
        }
        return null;
    }

    private Class forName(final String importString) {
        Class clazz = null;
        try {
            clazz = Class.forName(importString);
        } catch (ClassNotFoundException e) {
            ;
        }
        return clazz;
    }

    @Override
    public void visit(VariableDeclaratorId n, IdTracker arg) {
        boolean isField = n.getParentNode().getParentNode() instanceof FieldDeclaration;
        TypeDescription clazz = typeOf(n,arg);
        arg.addDeclaration(n.getName(), Pair.of(clazz, n));
        super.visit(n, arg);
    }

    @Override
    public void visit(CatchClause n, IdTracker arg) {
        arg.pushBlock(n);
        try {
            super.visit(n, arg);
        }
        finally {
            arg.popBlock();
        }
    }

    @Override
    public void visit(final ConstructorDeclaration n, final IdTracker arg) {
        arg.pushBlock(n);
        super.visit(n, arg);
        arg.popBlock();
    }
}
