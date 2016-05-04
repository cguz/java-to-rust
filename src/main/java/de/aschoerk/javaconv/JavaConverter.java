package de.aschoerk.javaconv;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;


public class JavaConverter {

    public static String convert2Rust(String javaString) {
        return new JavaConverter().convert(javaString);
    }


    public String convert(String javaString) {
        try {
            CompilationUnit res = PartParser.createCompilationUnit(javaString);
            RustDumpVisitor dumper = new RustDumpVisitor(true);
            dumper.visit(res, null);
            return dumper.getSource();
        } catch (ParseException e) {
            return e.toString();
        }
    }

}