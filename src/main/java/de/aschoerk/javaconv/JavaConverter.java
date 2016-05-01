package de.aschoerk.javaconv;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.type.Type;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.github.javaparser.ast.internal.Utils.isNullOrEmpty;
import static java.util.Collections.reverse;


public class JavaConverter {

    private String encapsulateInMethod(String testString) {
        String res = "class A { void m() { " + testString + "; } }";
        System.out.println("in Method: " + res);
        return res;
    }

    private String encapsulateInClass(String testString) {
        String res = "class A { " + testString + ";  }";
        System.out.println("in Class: " + res);
        return res;
    }

    static class MyDumpVisitor extends RustDumpVisitor {
        boolean commentOut = false;

        public MyDumpVisitor(boolean printComments) {
            super(printComments);
        }

        @Override
        public void visit(UnaryExpr n, Object arg) {
            printJavaComment(n.getComment(), arg);
            String unarySuffix = "";
            switch (n.getOperator()) {
                case preIncrement:
                case posIncrement:
                    unarySuffix = "+= 1";
                case preDecrement:
                case posDecrement:
                    if (unarySuffix.length() == 0)
                        unarySuffix = "-= 1";
                case positive:
                    n.getExpr().accept(this, arg);
                    printer.print(unarySuffix);
                    break;
                default:
                    super.visit(n,arg);
            }
        }

        @Override
        protected void printModifiers(final int modifiers) {
            if (ModifierSet.isPrivate(modifiers)) {
                if (commentOut) printer.print("/* private */");
            }
            if (ModifierSet.isProtected(modifiers)) {
                if (commentOut) printer.print("/* protected */");
            }
            if (ModifierSet.isPublic(modifiers)) {
                printer.print("pub ");
            }
            if (ModifierSet.isAbstract(modifiers)) {
                if (commentOut) printer.print("/* abstract */");
            }
            if (ModifierSet.isStatic(modifiers)) {
                if (commentOut) printer.print("/* static */");
            }
            if (ModifierSet.isFinal(modifiers)) {
                if (commentOut) printer.print("/* final */");
            }
            if (ModifierSet.isNative(modifiers)) {
                if (commentOut) printer.print("/* native */");
            }
            if (ModifierSet.isStrictfp(modifiers)) {
                if (commentOut) printer.print("/* strictfp */");
            }
            if (ModifierSet.isSynchronized(modifiers)) {
                if (commentOut) printer.print("/* synchronized */");
            }
            if (ModifierSet.isTransient(modifiers)) {
                if (commentOut) printer.print("/* transient */");
            }
            if (ModifierSet.isVolatile(modifiers)) {
                if (commentOut) printer.print("/* volatile */");
            }
        }

        @Override public void visit(final FieldDeclaration n, final Object arg) {
            printOrphanCommentsBeforeThisChildNode(n);

            printJavaComment(n.getComment(), arg);
            printJavadoc(n.getJavaDoc(), arg);
            if (commentOut) {
                printer.printLn("/* ");
                printMemberAnnotations(n.getAnnotations(), arg);
                printModifiers(n.getModifiers());
                n.getType().accept(this, arg);
                printer.printLn(" */");
            }

            printer.print(" ");
            for (final Iterator<VariableDeclarator> i = n.getVariables().iterator(); i.hasNext();) {
                final VariableDeclarator var = i.next();

                var.accept(this, n.getType());
                if (i.hasNext()) {
                    printer.print(", ");
                }
            }

            printer.print(";");
        }

        @Override public void visit(final VariableDeclarator n, final Object arg) {
            printJavaComment(n.getComment(), arg);
            printer.print("let ");
            n.getId().accept(this, arg);
            boolean isInitializedArray = n.getInit() != null && n.getInit() instanceof ArrayInitializerExpr;
            if (arg instanceof Type && !isInitializedArray) {
                printer.print(": ");
                Type t = (Type)arg;
                t.accept(this, null);
            }
            if (n.getInit() != null) {
                if (!isInitializedArray)
                    printer.print(" = ");
                n.getInit().accept(this, arg);
            }
        }

        @Override public void visit(final Parameter n, final Object arg) {
            printJavaComment(n.getComment(), arg);
            if (commentOut) {
                printer.print("/* ");
                printAnnotations(n.getAnnotations(), arg);
                printModifiers(n.getModifiers());
                if (n.isVarArgs()) {
                    printer.print("...");
                }
                printer.print(" */");
            }
            printer.print(" ");
            n.getId().accept(this, arg);
            printer.print(": ");
            if (n.getType() != null) {
                n.getType().accept(this, arg);
            }
        }

        List<Integer> getDimensions(ArrayInitializerExpr n, Type t) {
            List<Integer> dimensions = new ArrayList<>();
            Integer actsize = n.getValues().size();
            while (n != null) {
                dimensions.add(actsize);
                actsize = null;
                Expression firstValue = n.getValues().get(0);
                if (firstValue instanceof ArrayInitializerExpr) {
                    Integer size = null;
                    for (Expression e: n.getValues()) {
                        ArrayInitializerExpr ai = (ArrayInitializerExpr)e;
                        if (size == null) {
                            size = ai.getValues().size();
                            n = ai;
                        }
                        else {
                            if (size < ai.getValues().size()) {
                                size = ai.getValues().size();
                                n = ai;
                            }
                        }
                    }
                    actsize = size;
                } else {
                    n = null;
                }
            }
            return dimensions;
        }

        @Override public void visit(final ArrayInitializerExpr n, final Object arg) {
            Type t = arg instanceof Type ? (Type)arg : null;
            printJavaComment(n.getComment(), arg);

            if (!isNullOrEmpty(n.getValues())) {
                if (t != null) {
                    List<Integer> dims = getDimensions(n, t);
                    StringBuilder sb = new StringBuilder();
                    int mark = printer.push();
                    t.accept(this, arg);
                    sb.append(printer.getMark(mark));
                    printer.pop();
                    reverse(dims);
                    for (Integer i: dims) {
                        sb.insert(0, "[");
                        sb.append("; ").append(i).append("]");
                    }
                    printer.print(": ");
                    printer.print(sb.toString());
                    printer.print(" = ");
                }
                printer.print("[");

                for (Expression val: n.getValues()) {
                    val.accept(this, null);
                    printer.print(", ");
                }
                printer.printLn("]");

            }
        }

    }

    public static String convert2Rust(String javaString) {
        return new JavaConverter().convert(javaString);
    }


    public String convert(String javaString) {
        try (StringReader sr = new StringReader(javaString)) {
            CompilationUnit res = createCompilationUnit(javaString);
            NameVisitor nameExprVisitor = new NameVisitor();
            nameExprVisitor.visit(res, null);
            RustDumpVisitor dumper = new MyDumpVisitor(true);
            dumper.visit(res, null);
            return dumper.getSource();
        } catch (ParseException e) {
            return e.toString();
        }
    }

    private CompilationUnit createCompilationUnit(String javaString) throws ParseException {
        try {
            return tryParse(javaString);
        } catch (ParseException|StackOverflowError ex) {
            try {
                return tryParse(encapsulateInClass(javaString));
            } catch (ParseException|StackOverflowError e) {
                return tryParse(encapsulateInMethod(javaString));
            }
        }
    }

    private CompilationUnit tryParse(String javaString) throws ParseException {
        return JavaParser.parse(new ByteArrayInputStream(javaString.getBytes()));
    }
}