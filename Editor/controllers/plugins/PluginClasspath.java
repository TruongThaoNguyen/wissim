package controllers.plugins;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author khaclinh
 *
 */
public class PluginClasspath {

	private static final String DEFAULT_CLASSES_DIRECTORY = "classes";
	private static final String DEFAULT_LIB_DIRECTORY = "lib";

	protected List<String> classesDirectories;
	protected List<String> libDirectories;

	public PluginClasspath() {
		classesDirectories = new ArrayList<String>();
		libDirectories = new ArrayList<String>();

		addResources();
	}

	public List<String> getClassesDirectories() {
		return classesDirectories;
	}

	public void setClassesDirectories(List<String> classesDirectories) {
		this.classesDirectories = classesDirectories;
	}

	public List<String> getLibDirectories() {
		return libDirectories;
	}

	public void setLibDirectories(List<String> libDirectories) {
		this.libDirectories = libDirectories;
	}

	protected void addResources() {
		classesDirectories.add(DEFAULT_CLASSES_DIRECTORY);
		libDirectories.add(DEFAULT_LIB_DIRECTORY);
	}

}
