package controllers;


public final class WorkSpace {
	static String directory;
	static String traceFile;
	static String namTraceFile;
	static String ns2Path = "/home/trongnguyen/NS2/ns-2.35/";
	static String tclFile;
	
	/**
	 * @return the tclFile name
	 */
	public static String getTclFile() {
		return tclFile;
	}
	
	/**
	 * set tclFile name
	 * @param tclFile new file name
	 */
	public static String setTclFile(String tclFile) {
		return WorkSpace.tclFile = tclFile;
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
	public static String setTraceFile(String traceFile) {
		return WorkSpace.traceFile = traceFile;
	}

	/**
	 * name trace file's name
	 * @return the namTraceFile
	 */
	public static String getNamTraceFile() {
		return namTraceFile;
	}

	/**
	 * @param namTraceFile the namTraceFile to set
	 */
	public static String setNamTraceFile(String namTraceFile) {
		return WorkSpace.namTraceFile = namTraceFile;
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
		// check if dir is special tcl file
		if (dir.endsWith(".tcl")) return directory = dir.substring(0, directory.lastIndexOf("/") + 1); 
		return directory = dir;
	}

	/**
	 * get path to installed ns2
	 * @return
	 */
	public static String getNS2Path() {
		if (ns2Path == null)
		{
			//	TODO:
		}
		return ns2Path;
	}
	
	public static String setNS2Path(String path) {
		// TODO
		return ns2Path = path;
	}
}
