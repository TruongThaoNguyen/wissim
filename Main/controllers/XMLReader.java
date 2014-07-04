package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;

/**
 * Read Xml file.
 */
public class XMLReader {
	
	/**
	 * Parse xml file to Document Object.
	 * @param path path to document's directory
	 * @return Document parsed from xml file
	 * @throws ParsingException exception from parsing task
	 * @throws IOException exception from reading file
	 */
	public static Document open(final String path) throws ParsingException, IOException {
		InputStream input;
		Document doc = null;
		
		input = new FileInputStream(path);
        Builder parser = new Builder();
		doc = parser.build(input);			

		return doc;
	}
	
	/**
	 * Save Document Object to xml file.
	 * @param doc Document to save
	 * @param path path to save
	 * @return whether saving success or not
	 * @throws IOException exception while writing file
	 */
	public static boolean save(final Document doc, final String path) throws IOException {
		File f = new File(path);
		if (!f.exists())
		{
			f.getParentFile().mkdirs();
			f.createNewFile();
		}
		OutputStream output = new FileOutputStream(path);
        Serializer serializer = new Serializer(output, "ISO-8859-1");
        serializer.setIndent(4);
        serializer.write(doc);
        output.close();
        
        return true;
	}
}