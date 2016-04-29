package de.aschoerk.javaconv;

import java.io.ByteArrayInputStream;
import java.io.StringReader;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

class JavaConverter {

    public String convert2Rust(String javaString) {

        try (StringReader sr = new StringReader(javaString)) {
            CompilationUnit res = JavaParser.parse(new ByteArrayInputStream(javaString.getBytes()));

            return "output";
        } catch (ParseException e) {
            return e.toString();
        }
    }
}