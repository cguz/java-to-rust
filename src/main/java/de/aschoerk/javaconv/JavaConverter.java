package de.aschoerk.javaconv;

import static de.aschoerk.javaconv.PartParser.createCompilationUnit;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;


public class JavaConverter {

    public static String convert2Rust(String javaString) {
        return new JavaConverter().convert(javaString);
    }


    public String convert(String javaString) {
        try {
            CompilationUnit  compilationUnit = createCompilationUnit(javaString);
            IdTrackerVisitor idTrackerVisitor = new IdTrackerVisitor();
            IdTracker idTracker = new IdTracker();
            idTrackerVisitor.visit(compilationUnit, idTracker);
            TypeTrackerVisitor typeTrackerVisitor = new TypeTrackerVisitor(idTracker);
            typeTrackerVisitor.visit(compilationUnit, null);

            RustDumpVisitor dumper = new RustDumpVisitor(true, idTracker, typeTrackerVisitor);
            dumper.visit(compilationUnit, null);
            return dumper.getSource();
        } catch (ParseException e) {
            return e.toString();
        }
    }

}