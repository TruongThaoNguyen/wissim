package controllers.helpers;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import controllers.WorkSpace;

public class Validator {
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
	
	public static boolean isWindow() {
		return WorkSpace.isWindow();
	}
	
	public static boolean isLinux() {
		return WorkSpace.isLinux();
	}
	
	public static String getHomePath() {
		return System.getProperty("user.home");
	}
}
