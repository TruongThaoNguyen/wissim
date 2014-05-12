package controllers;

public final class WorkSpace {
	public static String OS = System.getProperty("os.name").toLowerCase();
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

	public static boolean isWindow() {
		//return (OS.indexOf("win") >= 0);
		return OS.startsWith("windows");
	}
	
	public static boolean isLinux() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
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
		return directory = dir;
	}
	
	
}
