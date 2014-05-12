package controllers;


public final class WorkSpace 
;
	static String directory;
	static String traceFile;
	static String namTraceFile;
	
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
}
