package controllers.helpers;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import views.helpers.NameErrorException;

public class Validator {
	static String OS = System.getProperty("os.name").toLowerCase();
	
	public static boolean validateIntegerValue(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			String s = "";
			s += "You entered \"" + str + "\", which is an invalid integer.\n";
			s += "Please enter an integer value";
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", s);
			return false;
		}
	}
	
	public static void validNameValue(String str) throws NameErrorException {
		//TODO
	}
	
	public static String getFilePath(String directory, String fileName) {
		String slash = "/";
		
		if (isWindow())
			slash = "\\";
		else if (isLinux())
			slash = "/";
		
		if (directory.substring(directory.length() - 1).equals(slash))
			return directory + fileName;		
		else
			return directory + slash + fileName;
	}
	
	public static void main(String[] args) {
		DirectoryDialog browseDialog = new DirectoryDialog(new Shell(), SWT.NONE);
		
		String directory = browseDialog.open();
		String fileName = "test.txt";
		
		System.out.println(getFilePath(directory, fileName));
		
		System.exit(0);
	}
	
	public static boolean isWindow() {
		return (OS.indexOf("win") > 0);
	}
	
	public static boolean isLinux() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
	}
	
	public static String getHomePath() {
		return System.getProperty("user.home");
	}
}
