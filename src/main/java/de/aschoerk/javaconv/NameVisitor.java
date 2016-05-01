package de.aschoerk.javaconv;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by aschoerk on 30.04.16.
 */
public class NameVisitor extends VoidVisitorAdapter<JavaConverterData> {

    static String[] mappedNames = {
            "Double", "f64",
            "double", "f64",
            "Float", "f32",
            "float", "f32",
            "Integer", "i32",
            "int", "i32",
            "Short", "i16",
            "short", "i16",
            "Byte", "i8",
            "byte", "i8",
            "Long", "i64",
            "long", "i64",
            "NaN", "NAN",
            "NEGATIVE_INFINITY", "NEG_INFINITY",
            "POSITIVE_INFINITY", "INFINITY",
            "MIN_VALUE","MIN",
            "MAX_VALUE","MAX",
    };

    static HashMap<String, String> namesMap = new HashMap<>();
    static {
        for (int i = 0; i < mappedNames.length; i += 2) {
            namesMap.put(mappedNames[i], mappedNames[i+1]);
        }
    }

    @Override
    public void visit(NameExpr n, JavaConverterData arg) {
        n.setName(toSnakeIfNecessary(n.getName()));
        super.visit(n, arg);
    }

    private String toSnakeIfNecessary(String n) {
        System.out.println("doing: " + n);
        if (namesMap.containsKey(n))
            n = namesMap.get(n);
        String name = n;
        if (Character.isLowerCase(name.charAt(0))) {
            StringBuilder sb = new StringBuilder();
            for (Character c: name.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    sb.append("_").append(Character.toLowerCase(c));
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        return n;
    }

    @Override
    public void visit(VariableDeclaratorId n, JavaConverterData arg) {
        n.setName(toSnakeIfNecessary(n.getName()));
        super.visit(n, arg);
    }

    @Override
    public void visit(MethodReferenceExpr n, JavaConverterData arg) {
        n.setIdentifier(toSnakeIfNecessary(n.getIdentifier()));
        super.visit(n, arg);
    }

    @Override
    public void visit(MethodCallExpr n, JavaConverterData arg) {
        n.setName(toSnakeIfNecessary(n.getName()));
        super.visit(n, arg);
    }

    @Override
    public void visit(MethodDeclaration n, JavaConverterData arg) {
        n.setName(toSnakeIfNecessary(n.getName()));
        super.visit(n, arg);
    }

    @Override
    public void visit(DoubleLiteralExpr n,  JavaConverterData arg) {
        boolean isFloat = StringUtils.endsWithIgnoreCase(n.getValue(),"f");
        String value = removePlusAndSuffix(n.getValue(),"d","D","f","F");
        n.setValue(value + (isFloat ? "f32" : "f64"));
        super.visit(n,arg);
    }

    @Override
    public void visit(LongLiteralExpr n,  JavaConverterData arg) {
        String value = removePlusAndSuffix(n.getValue(),"l","L");
        n.setValue(value + "i64");
        super.visit(n,arg);
    }

    @Override
    public void visit(IntegerLiteralExpr n,  JavaConverterData arg) {
        String value = removePlusAndSuffix(n.getValue());
        n.setValue(value + "i64");
        super.visit(n,arg);
    }

    private String removePlusAndSuffix(String value,  CharSequence... searchStrings) {
        if (value.startsWith("+")) {
            value = value.substring(1);
        }
        if (StringUtils.endsWithAny(value, searchStrings)) {
            value = value.substring(0, value.length()-1);
        }
        return value;
    }


}
