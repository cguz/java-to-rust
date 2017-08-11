package de.aschoerk.doxiaconv;

import org.apache.commons.io.IOUtils;
import org.apache.maven.doxia.ConverterException;
import org.apache.maven.doxia.DefaultConverter;
import org.apache.maven.doxia.UnsupportedFormatException;
import org.apache.maven.doxia.wrapper.InputFileWrapper;
import org.apache.maven.doxia.wrapper.OutputFileWrapper;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.WriterFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

/**
 * @author aschoerk
 */
public class DoxiaConverter {

    public static final String PREFIX = "doxiaconvsrcfile";
    public static final String SUFFIX = ".tmp";

    public static File stream2file (String s) throws IOException {
        if (s == null)
            s = "";
        final File tempFile = File.createTempFile(PREFIX, SUFFIX);
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(new StringReader(s), out);
        }
        return tempFile;
    }

    private String readFile(String file) throws IOException {

        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            return stringBuilder.toString();
        }
    }

    public String convert(String inputString, String inputformat, String outputformat) throws UnsupportedFormatException, ConverterException, IOException {

        DefaultConverter converter = new DefaultConverter();

        if (inputformat == null)
            inputformat = "twiki";
        if (outputformat == null)
            outputformat = "confluence";

        File in = stream2file(inputString);
        final File out = File.createTempFile(PREFIX, SUFFIX);
        out.deleteOnExit();

        InputFileWrapper input =
                InputFileWrapper.valueOf( in.getAbsolutePath(), inputformat, ReaderFactory.UTF_8, converter.getInputFormats() );
        OutputFileWrapper output =
                OutputFileWrapper.valueOf( out.getAbsolutePath(), outputformat, WriterFactory.UTF_8, converter.getOutputFormats() );
        converter.setFormatOutput( true );
        converter.convert( input, output );

        return readFile(out.getAbsolutePath());

    }
}
