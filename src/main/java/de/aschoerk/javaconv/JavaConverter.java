package de.aschoerk.javaconv;

import static de.aschoerk.javaconv.PartParser.createCompilationUnit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;


public class JavaConverter {

	private String convert(File file) throws IOException {

    	String output = "";
    	String EXTENSION = ".rs";
    	
		if (file.exists()) {
			
			// get the name of the file
			Path path = file.toPath();
			output = path.getFileName().toString();
			
			String[] outputSplit = output.split("\\.");
			
			output = outputSplit[0]+EXTENSION;
			
			// get the text of the file
			String text = Files.readString(path);
			
			String result = convert(text);
			
			// store the result in the file
			Files.writeString(Path.of(output), result);
			
			System.out.println("- "+output);
			
			return "";
		}else {
    		return "\nThe file does not exist!";
		}
	}
	
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
    	
    	String howToUse = "$ java -jar java-to-rust.jar [path_file.java | path_directory]";
    	
    	String filename = "";
    	
    	if (args.length < 1) {

    		System.out.println("Help of use:\n" + howToUse);
    		
    	}else {
    	
	    	for (int index = 0; index <args.length; index++) {

				filename = args[index];
	    		
	    	}
	    	
	    	if (filename.isEmpty()) {
	    		System.out.println("Please specify the java file as follow:\n\n" + howToUse);
	    	}else {
        		File file = new File(filename);
    			JavaConverter java_converter= new JavaConverter();
    			java_converter.convert(file);
    			
	    	} 
    	}
    }
}