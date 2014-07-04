package controllers;

import java.io.IOException;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;

/**
 * General configure.
 * @author Trongnguyen
 *
 */
public final class Configure {
	/**
	 * Path to configure storage file.
	 */
	private static String CONFIGURE = getHomePath() + "/.wissim/configure.xml";
	
	/**
	 * Path to current directory.
	 */
	static String directory;
	
	/**
	 * Path to default trace file.
	 */
	static String traceFile;
	
	/**
	 * Path to default nam trace file.
	 */
	static String namTraceFile;	
	
	/**
	 * Path to ns2 installed directory.
	 */
	static String ns2Path;	// = "/home/trongnguyen/NS2/";
	
	/**
	 * Path to current tcl file.
	 */
	static String tclFile;
	
	/**
	 * return user's home path.
	 * @return a string that is user's home path
	 */
	public static String getHomePath() {
		return System.getProperty("user.home");
	}
	
	/**
	 * get current Tcl file.
	 * @return the tclFile name
	 */
	public static String getTclFile() {
		if (tclFile.contains("/")) return tclFile;
		return directory + tclFile;
	}
	
	/**
	 * set current Tcl file.
	 * @param value new file name.
	 * @return new file name.
	 */
	public static String setTclFile(String value) {
		int i = value.lastIndexOf("/");
		if (i != -1)
		{			
			directory = value.substring(0, i + 1);
			return tclFile = value.substring(i + 1);
		}		
			
		return tclFile = value;
	}
	
	/**
	 * @return the traceFile name.
	 */
	public static String getTraceFile() {		
		return traceFile;
	}

	/**
	 * set default Trace file.
	 * @param value new file name
	 * @return file name
	 */
	public static String setTraceFile(String value) {
		int i = value.lastIndexOf("/");
		if (i != -1)
		{			
			directory = value.substring(0, i + 1);
			return traceFile = value.substring(i + 1);
		}		
		return traceFile = value;
	}

	/**
	 * name trace file's name
	 * @return the namTraceFile
	 */
	public static String getNamTraceFile() {
		if (namTraceFile.contains("/")) return namTraceFile;		
		return getFilePath(directory, namTraceFile);
	}

	/**
	 * @param namTraceFile the namTraceFile to set
	 */
	public static String setNamTraceFile(String namTraceFile) {
		return Configure.namTraceFile = namTraceFile;
	}
	
	/**
	 * get current workspace directory
	 * @return directory
	 */
	public static String getDirectory() {
		return directory;
	}
	
	/**
	 * set current workspace directory
	 * @param dir new directory
	 * @return directory
	 */
	public static String setDirectory(String dir) {
		if (dir.endsWith(".tcl")) dir = dir.substring(0, dir.lastIndexOf("/"));
		return directory = dir + "/";	
	}

	/**
	 * get path to installed ns2
	 * @return
	 */
	public static String getNS2Path() {
		if (ns2Path == null)
		{
			try {
				Document doc = XMLReader.open(CONFIGURE);				
				Element root = doc.getRootElement();				
				Element e = root.getFirstChildElement("NS2Path");
				ns2Path = e.getAttributeValue("value");
			}
			catch (ParsingException | IOException e) 
			{
				return null;
			}
		}
		return ns2Path;
	}
	
	/**
	 * set current NS2 directory.
	 * @param path path to ns2 installed directory.
	 * @return path to ns2 installed directory
	 */
	public static String setNS2Path(String path) {
		if (!path.endsWith("/")) 	ns2Path = path + "/";		
		else						ns2Path = path;
		
		Element eGraphicSettings = new Element("configure");
		Document doc = new Document(eGraphicSettings);
		
		Element e = new Element("NS2Path");
		e.addAttribute(new Attribute("value", path));
		eGraphicSettings.appendChild(e);
		
		try 
		{
			XMLReader.save(doc, CONFIGURE);
		}
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
		
		return ns2Path;
	}
	
	/**
	 * Merge directory and file name to create full path.
	 * @param directory path to directory
	 * @param fileName file name
	 * @return full path to file
	 */
	public static String getFilePath(String directory, String fileName) {
		String slash = "/";		
		
		if (directory.substring(directory.length() - 1).equals(slash))
			return directory + fileName;		
		else
			return directory + slash + fileName;
	}
}
