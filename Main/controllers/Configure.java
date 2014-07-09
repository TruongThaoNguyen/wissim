package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import controllers.managers.ProjectManager;
import models.Project;
import models.networkcomponents.Node;
import models.networkcomponents.WirelessNode;
import models.networkcomponents.features.Area;
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
	private static String configures = getHomePath() + "/.wissim/configure.xml";
	
	/**
	 * Path to current directory.
	 */
	private static String directory;
	
	/**
	 * Path to default trace file.
	 */
	private static String traceFile;
	
	/**
	 * Path to default nam trace file.
	 */
	private static String namTraceFile;	
	
	/**
	 * Path to ns2 installed directory.
	 */
	private static String ns2Path;	// = "/home/trongnguyen/NS2/";

	/**
	 * Path to current tcl file.
	 */
	private static String tclFile;
	
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
		if (tclFile.contains("/")) {
			return tclFile;
		}
		return directory + tclFile;
	}
	
	/**
	 * set current Tcl file.
	 * @param value new file name.
	 * @return new file name.
	 */
	public static String setTclFile(final String value) {
		int i = value.lastIndexOf("/");
		if (i != -1) {			
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
	public static String setTraceFile(final String value) {
		int i = value.lastIndexOf("/");
		if (i != -1) {			
			directory = value.substring(0, i + 1);
			return traceFile = value.substring(i + 1);
		}		
		return traceFile = value;
	}

	/**
	 * name trace file's name.
	 * @return the namTraceFile
	 */
	public static String getNamTraceFile() {
		if (namTraceFile.contains("/")) {
			return namTraceFile;
		}		
		return getFilePath(directory, namTraceFile);
	}

	/**
	 * set default nam trace file.
	 * @param value new name trace file
	 * @return nam trace file
	 */
	public static String setNamTraceFile(final String value) {
		return namTraceFile = value;
	}
	
	/**
	 * get current workspace directory.
	 * @return directory
	 */
	public static String getDirectory() {
		return directory;
	}
	
	/**
	 * set current workspace directory.
	 * @param dir new directory
	 * @return directory
	 */
	public static String setDirectory(String dir) {
		if (dir.endsWith(".tcl")) {
			dir = dir.substring(0, dir.lastIndexOf("/"));
		}
		return directory = dir + "/";	
	}

	/**
	 * get path to installed ns2 directory.
	 * @return path
	 */
	public static String getNS2Path() {
		if (ns2Path == null)
		{
			try 
			{
				Document doc = XMLReader.open(configures);				
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
	public static String setNS2Path(final String path) {
		if (!path.endsWith("/")) 	ns2Path = path + "/";		
		else						ns2Path = path;
		
		Element eGraphicSettings = new Element("configure");
		Document doc = new Document(eGraphicSettings);
		
		Element e = new Element("NS2Path");
		e.addAttribute(new Attribute("value", path));
		eGraphicSettings.appendChild(e);
		
		try 
		{
			XMLReader.save(doc, configures);
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

	public static List<List<Integer>> getGroups()
	{
		ArrayList<List<Integer>> re = new ArrayList<List<Integer>>();
		
		for (Area a : Project.getGroupsList()) {
			ArrayList<Integer> su = new ArrayList<Integer>();
			re.add(su);
			
			for (Node n : ProjectManager.getProject().getNetwork().getNodeList()) {
				WirelessNode w = (WirelessNode) n;
				if (a.contains(w.getX(), w.getY())) su.add(w.getId());
			}
		}
		
		return re;
	}
}
