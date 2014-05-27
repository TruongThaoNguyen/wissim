package controllers;

import java.io.IOException;

import controllers.helpers.Validator;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;

public final class Configure {
	static private String CONFIGURE = Validator.getHomePath() + "/.wissim/editor/configure.xml";
	
	static String directory;
	static String traceFile;
	static String namTraceFile;	
	static String ns2Path = "/home/trongnguyen/NS2/";
	static String tclFile;
	
	/**
	 * @return the tclFile name
	 */
	public static String getTclFile() {
		if (tclFile.contains("/")) return tclFile;
		return directory + tclFile;
	}
	
	/**
	 * set tclFile name
	 * @param tclFile new file name
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
	 * @return the traceFile name
	 */
	public static String getTraceFile() {		
		return traceFile;
	}

	/**
	 * set traceFile name
	 * @param traceFile new file name
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
		return directory + namTraceFile;
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
			try 
			{
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
	
	public static String setNS2Path(String path) {
		ns2Path = path;
		
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
}
