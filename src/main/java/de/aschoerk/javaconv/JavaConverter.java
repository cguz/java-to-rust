package de.aschoerk.javaconv;

import static de.aschoerk.javaconv.PartParser.createCompilationUnit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
    
    public static void main(String[] args) throws IOException {
    	
    	String filename = "";
    	
    	for (int index = 0; index <args.length; index++) {
    		
    		if (args[index].equals("-f")) {
    			index++;
    			if(index < args.length)
    				filename = args[index];
    		}
    		
    	}
    	
    	if (filename.isEmpty()) {
    		System.out.println("Please specify the java file as follow:\n\njavac JavaConverter -f path_url_java_file");
    	}else {
        	if (!filename.contains(".java"))
        		System.out.println("The file should contain a java extension.");
        	else {
        		File file = new File(filename);
        		if (file.exists()) {
        			String text = Files.readString(Path.of(filename));
        			
        			JavaConverter java_converter= new JavaConverter();
        			System.out.println(java_converter.convert(text));
        		}else {
            		System.out.println("The file does not exist!");
        		}
        	}	
    	}     	
    }
}