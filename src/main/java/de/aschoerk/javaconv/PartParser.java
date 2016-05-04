package de.aschoerk.javaconv;

import java.io.ByteArrayInputStream;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

/**
 * Created by aschoerk on 03.05.16.
 */
public class PartParser {
    static String encapsulateInMethod(String testString) {
        String res = "class A { void m() { " + testString + "; } }";
        System.out.println("in Method: " + res);
        return res;
    }

    static String encapsulateInClass(String testString) {
        String res = "class A { " + testString + ";  }";
        System.out.println("in Class: " + res);
        return res;
    }

    static CompilationUnit createCompilationUnit(String javaString) throws ParseException {

        StringBuffer parseExceptions = new StringBuffer();
        try {
            return tryParse(javaString);
        } catch (ParseException|StackOverflowError ex) {
            parseExceptions.append(ex.getMessage());
            try {
                return tryParse(encapsulateInClass(javaString));
            } catch (ParseException|StackOverflowError ex2) {
                parseExceptions.append("\nencapsulated in Class\n").append(ex2.getMessage());
                try {
                    return tryParse(encapsulateInMethod(javaString));
                } catch (ParseException|StackOverflowError ex3) {
                    parseExceptions.append("\nencapsulated in Method\n").append(ex3.getMessage());
                    throw new ParseException(parseExceptions.toString());
                }
            }
        }
    }

    static CompilationUnit tryParse(String javaString) throws ParseException {
        return JavaParser.parse(new ByteArrayInputStream(javaString.getBytes()));
    }
}
