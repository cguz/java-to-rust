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
import org.apache.commons.cli.*;

public class JavaConverter {

	String EXTENSION = ".rs";

	private String convert2Rust(File file, String outputDir, boolean ignoreExistingFiles, int verbosityLevel,
			boolean copyOtherFiles) throws IOException {

		// create the output directory
		File fileDir = new File(outputDir);

		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		String output = outputDir + System.getProperty("file.separator");

		// if the file exist
		if (file.exists()) {

			// if it is a directory, we go inside the directory
			if (file.isDirectory()) {

				output += file.getName().toString();

				// for each file, we call recursively convert2Rust
				File[] files = file.listFiles();
				for (int index = 0; index < files.length; index++) {
					convert2Rust(files[index], output, ignoreExistingFiles, verbosityLevel, copyOtherFiles);
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
			if (outputSplit[outputSplit.length - 1].equals("java")) {
				// convert the Java source file name to a camel-cased rust file
				output += camelToSnakeCase(outputSplit[0]) + EXTENSION;
				if (!ignoreExistingFiles || !Files.exists(Path.of(output))) {

					// read the content of the file
					String text = Files.readString(path);

					if (verbosityLevel > 0) {
						System.out.println("- " + output);
					}

					// convert the java content to rust
					String result = convert(text);

					// store the result in the file
					Files.writeString(Path.of(output), result);
				} else if (verbosityLevel > 1) {
					System.out.println("- " + output + " (ignored) because it already exists");
				}
			} else if (copyOtherFiles) {
				// copy the file to the output directory
				output += outputTemp;
				if (!ignoreExistingFiles || !Files.exists(Path.of(output))) {
					Files.copy(path, Path.of(output));
					if (verbosityLevel > 0) {
						System.out.println("- " + output);
					}
				} else if (verbosityLevel > 1) {
					System.out.println("- " + output + " (ignored) because it already exists");
				}
			}

			return "";

		} else {
			return "\nThe file does not exist!";
		}
	}

	public static String convert2Rust(String javaString) {
		return new JavaConverter().convert(javaString);
	}

	public String convert(String javaString) {
		try {
			CompilationUnit compilationUnit = createCompilationUnit(javaString);
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
		Options options = new Options();
		options.addOption("d", "input", true, "Specify the input file or directory path");
		options.addOption("o", "output", true, "Specify the output directory path (default: output)");
		options.addOption("i", "ignore-existing", false,
				"Ignore existing files in the output directory (default: false)");
		options.addOption("v", "verbosity", true, "Specify the verbosity level (default: 2)");
		options.addOption("cp", "copy-other-files", false,
				"Copy other non-java files to the output directory (default: false)");

		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption("d")) {
				String filename = cmd.getOptionValue("d");
				String outputDir = cmd.getOptionValue("o", "output");
				boolean ignoreExistingFiles = cmd.hasOption("i");
				int verbosityLevel = Integer.parseInt(cmd.getOptionValue("v", "2"));
				boolean copyOtherFiles = cmd.hasOption("cp");

				File file = new File(filename);
				JavaConverter java_converter = new JavaConverter();
				java_converter.convert2Rust(file, outputDir, ignoreExistingFiles, verbosityLevel, copyOtherFiles);
			} else {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("java -jar java-to-rust.jar", options);
			}
		} catch (org.apache.commons.cli.ParseException e) {
			System.out.println(e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar java-to-rust.jar", options);
		}
	}

}