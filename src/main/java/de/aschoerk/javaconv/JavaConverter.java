package de.aschoerk.javaconv;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.type.Type;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.javaparser.ast.internal.Utils.isNullOrEmpty;
import static java.util.Collections.reverse;


public class JavaConverter {

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
            boolean isInitializedArray = n.getInit() != null && (n.getInit() instanceof ArrayInitializerExpr
                    || n.getInit() instanceof ArrayCreationExpr);
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

        String acceptAndCut(Node n, final Object arg) {
            int mark = printer.push();
            n.accept(this, arg);
            String result = printer.getMark(mark);
            printer.pop();
            return result;
        }

        @Override public void visit(final ArrayInitializerExpr n, final Object arg) {
            Type t = arg instanceof Type ? (Type)arg : null;
            printJavaComment(n.getComment(), arg);

            if (!isNullOrEmpty(n.getValues())) {
                if (t != null) {
                    List<Integer> dims = getDimensions(n, t);
                    StringBuilder sb = new StringBuilder();
                    sb.append(acceptAndCut(t, arg));
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

        String defaultValue(String type) {

            switch (type) {
                case "f64":
                case "f32": return "0.0";
                case "u64":
                case "u32":
                case "u16":
                case "u8":
                case "usize":
                case "i64":
                case "i32":
                case "i16":
                case "i8": return "0";
                case "bool": return "false";
                default:
                    return "None";
            }
        }

        @Override public void visit(final ArrayCreationExpr n, final Object arg) {
            printJavaComment(n.getComment(), arg);
            if (!isNullOrEmpty(n.getDimensions())) {
                String type = acceptAndCut(n.getType(), arg);
                String typeOrDefaultValue = defaultValue(type);
                if (typeOrDefaultValue.equals("None"))
                    type = "Option<" + type + ">";
                List<String> dims = n
                        .getDimensions()
                        .stream()
                        .map(e -> acceptAndCut(e, arg))
                        .collect(Collectors.toList());

                printer.print(": ");
                printer.print(getArrayDeclaration(type, dims));

                printer.print(" = ");
                printer.print(getArrayDeclaration(typeOrDefaultValue, dims));

            } else {
                printer.print(" ");
                n.getInitializer().accept(this, n.getType());
            }
        }

        private String getArrayDeclaration(String typeOrDefaultValue, List<String> dims) {
            StringBuilder sb = new StringBuilder();
            sb.append(typeOrDefaultValue);
            reverse(dims);
            for (String s: dims) {
                sb.insert(0, "[");
                sb.append("; ").append(s).append("]");
            }
            reverse(dims);
            return sb.toString();
        }

    }



    public static String convert2Rust(String javaString) {
        return new JavaConverter().convert(javaString);
    }


    public String convert(String javaString) {
        try {
            CompilationUnit res = PartParser.createCompilationUnit(javaString);
            RustDumpVisitor dumper = new MyDumpVisitor(true);
            dumper.visit(res, null);
            return dumper.getSource();
        } catch (ParseException e) {
            return e.toString();
        }
    }

}