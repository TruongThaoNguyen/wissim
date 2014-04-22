package controllers;

public final class WorkSpace {
	public static String OS = System.getProperty("os.name").toLowerCase();
	static String directory;
	
	public static boolean isWindow() {
		return (OS.indexOf("win") > 0);
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
