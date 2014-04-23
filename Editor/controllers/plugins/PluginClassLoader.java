package controllers.plugins;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
/**
 * 
 * @author khaclinh
 *
 */
public class PluginClassLoader extends URLClassLoader {

//	private static final String JAVA_PACKAGE_PREFIX = "java.";
//	private static final String JAVAX_PACKAGE_PREFIX = "javax.";
	private static final String PLUGIN_PACKAGE_PREFIX = "controllers.plugins.";

	private PluginManager pluginManager;
	private PluginDescriptor pluginDescriptor;

	public PluginClassLoader(PluginManager pluginManager, PluginDescriptor pluginDescriptor, ClassLoader parent) {
		super(new URL[0], parent);

		this.pluginManager = pluginManager;
		this.pluginDescriptor = pluginDescriptor;
	}

	@Override
	public void addURL(URL url) {
		super.addURL(url);
	}

	@Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
//		System.out.println(">>>" + className);

		/*
		 // javax.mail is not in JDK ?!
		// first check whether it's a system class, delegate to the system loader
		if (className.startsWith(JAVA_PACKAGE_PREFIX) || className.startsWith(JAVAX_PACKAGE_PREFIX)) {
			return findSystemClass(className);
		}
		*/

        // second check whether it's already been loaded
        Class<?> loadedClass = findLoadedClass(className);
        if (loadedClass != null) {
        	return loadedClass;
        }

        // nope, try to load locally
        try {
        	System.out.println(className);
        	return findClass(className);
        } catch (ClassNotFoundException e) {
        	// try next step
        }

        // if the class it's a part of the plugin engine use parent class loader
        if (className.startsWith(PLUGIN_PACKAGE_PREFIX)) {
        	try {
        		return PluginClassLoader.class.getClassLoader().loadClass(className);
        	} catch (ClassNotFoundException e) {
        		// try next step
        	}
        }

        // look in dependencies
        List<PluginDependency> dependencies = pluginDescriptor.getDependencies();
        for (PluginDependency dependency : dependencies) {
        	PluginClassLoader classLoader = pluginManager.getPluginClassLoader(dependency.getPluginId());
        	try {
        		return classLoader.loadClass(className);
        	} catch (ClassNotFoundException e) {
        		// try next dependency
        	}
        }

        // use the standard URLClassLoader (which follows normal parent delegation)
        return super.loadClass(className);
    }

    @Override
    public URL getResource(String name) {
        if (PluginState.DISABLED == getPlugin().getPluginState()) {
            return null;
        }

        return super.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        if (PluginState.DISABLED == getPlugin().getPluginState()) {
            return Collections.emptyEnumeration();
        }

        return super.getResources(name);
    }

    private PluginWrapper getPlugin() {
        return pluginManager.getPlugin(pluginDescriptor.getPluginId());
    }

}
