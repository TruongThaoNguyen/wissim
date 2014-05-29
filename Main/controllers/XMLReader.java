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
import nu.xom.ValidityException;

/**
 * Parser.java
 * 
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and
 *            Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */
public class XMLReader {	
	
	/**
	 * Parse xml file to Document Object
	 * @param path
	 * @return
	 * @throws ValidityException
	 * @throws ParsingException
	 * @throws IOException
	 */
	static public Document open(String path) throws ValidityException, ParsingException, IOException {
		InputStream input;
		Document doc = null;
		
		input = new FileInputStream(path);
        Builder parser = new Builder();
		doc = parser.build(input);			

		return doc;
	}
	
	/**
	 * Save Document Object to xml file
	 * @param doc
	 * @param path
	 * @return
	 * @throws IOException
	 */
	static public boolean save(Document doc, String path) throws IOException {
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