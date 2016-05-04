package de.aschoerk.javaconv;

import org.apache.commons.lang3.StringUtils;

import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Created by aschoerk on 30.04.16.
 */
public class NameVisitor extends VoidVisitorAdapter<JavaConverterData> {


    @Override
    public void visit(DoubleLiteralExpr n,  JavaConverterData arg) {
        boolean isFloat = StringUtils.endsWithIgnoreCase(n.getValue(),"f");
        String value = removePlusAndSuffix(n.getValue(),"d","D","f","F");
        n.setValue(value);
        super.visit(n,arg);
    }

    @Override
    public void visit(LongLiteralExpr n,  JavaConverterData arg) {
        String value = removePlusAndSuffix(n.getValue(),"l","L");
        n.setValue(value);
        super.visit(n,arg);
    }

    @Override
    public void visit(IntegerLiteralExpr n,  JavaConverterData arg) {
        String value = removePlusAndSuffix(n.getValue());
        n.setValue(value);
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
