package controllers;


public final class Configure {
	static String directory;
	static String traceFile;
	static String namTraceFile;
	static String ns2Path = "/home/khaclinh/ns-allinone-2.34/";
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
	public static String setTclFile(String value) {		
		return tclFile = value;
	}
	
	/**
	 * @return the traceFile name
	 */
	public static String getTraceFile() {
		if (traceFile.contains("/"));
		return directory + traceFile;
	}

	/**
	 * set traceFile name
	 * @param traceFile new file name
	 */
	public static String setTraceFile(String value) {		
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
//	
//	/**
//	 * set current workspace directory
//	 * @param dir new directory
//	 * @return directory
//	 */
//	public static String setDirectory(String dir) {
//		if (dir.endsWith(".tcl")) dir = dir.substring(0, dir.lastIndexOf("/"));
//		return directory = dir + "/";
//	}

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
		
//	    try {	    	
//	    	String filePathString = "NS2_path_store";
//			File file = new File(filePathString);	
//	    if (!file.exists()) {
//			
//				file.createNewFile();
//		}
//	    FileWriter fw = new FileWriter(file.getAbsoluteFile());
//		BufferedWriter bw = new BufferedWriter(fw);
//		bw.write(nsFilePath);
//		bw.close();
//	    } catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		return ns2Path = path;
	}
}
