package de.aschoerk.java2rust;

import static de.aschoerk.java2rust.PartParser.createCompilationUnit;
import static de.aschoerk.java2rust.utils.NamingHelper.camelToSnakeCase;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import de.aschoerk.java2rust.codegen.RustDumpVisitor;


public class JavaConverter {

	String EXTENSION = ".rs";

	private String convert2Rust(File file, String outputDir) throws IOException {

		// create the output directory
		File fileDir = new File(outputDir);
		
		if(!fileDir.exists()) {
			fileDir.mkdirs();
		}
		
    	String output = outputDir+System.getProperty("file.separator");

    	// if the file exist
		if (file.exists()) {

			// if it is a directory, we go inside the directory
			if (file.isDirectory()) {
				
				output+= file.getName().toString();
				
				// for each file, we call recursively convert2Rust
				File[] files = file.listFiles();
		        for(int index=0; index< files.length; index++) {
		        	convert2Rust(files[index], output);
		        }
		        
		        // we finish the execution
		        return "";
				
			}
			
			// if it is not a directory, it is a file
			
			// get the file as a path
			Path path = file.toPath();
			
			// get the name of the file
			String outputTemp = path.getFileName().toString();			
			String[] outputSplit = outputTemp.split("\\.");	
			
			// check the java extension of the file
			if(outputSplit[1].equals("java")) {

				// convert the Java source file name to a camel-cased rust file
				output += camelToSnakeCase(outputSplit[0]) + EXTENSION;
				
				// read the content of the file
				String text = Files.readString(path);

				System.out.println("- "+output);
				
				// convert the java content to rust
				String result = convert(text);
				
				// store the result in the file
				Files.writeString(Path.of(output), result);
			}

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
    	
    	String howToUse = "$ java -jar java-to-rust.jar -d [path_file.java | path_directory]";
    	
    	String filename = "";
    	String outputDir = "output";
    	
    	if (args.length < 1) {

    		System.out.println("Help of use:\n" + howToUse);
    		
    	}else {

			final Map<String, List<String>> params = new HashMap<>();

			List<String> options = null;
			for (int index = 0; index <args.length; index++) {

				final String a = args[index];

				if (a.charAt(0) == '-') {
					if (a.length() < 2) {
						System.err.println("Error at argument " + a);
						return;
					}

					options = new ArrayList<>();
					params.put(a.substring(1), options);

				} else {
					if (options != null){
						options.add(a);
					} else {
						System.err.println("Illegal parameter usage");
					}
				}
	    		
	    	}

			if (params.isEmpty() || !params.containsKey("d")){
				System.err.println("Error at argument ");
				return;
			}

			filename = params.get("d").get(0);				
		
			if (filename.isEmpty()) {
				System.out.println("Please specify the java file(s) as follow:\n\n" + howToUse);
				return;
			}
			File file = new File(filename);
			JavaConverter java_converter= new JavaConverter();
			java_converter.convert2Rust(file, outputDir);
    	}
    }
}