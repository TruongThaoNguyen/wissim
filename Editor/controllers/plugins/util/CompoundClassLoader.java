package controllers.plugins.util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author khaclinh
 *
 */
public class CompoundClassLoader extends ClassLoader {

	private Set<ClassLoader> loaders = new HashSet<ClassLoader>();

	public void addLoader(ClassLoader loader) {
		loaders.add(loader);
	}

	public void removeLoader(ClassLoader loader) {
		loaders.remove(loader);
	}

	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		for (ClassLoader loader : loaders) {
			try {
				return loader.loadClass(name);
			} catch (ClassNotFoundException e) {
				// try next
			}
		}

		throw new ClassNotFoundException(name);
	}

	@Override
	public URL findResource(String name) {
		for (ClassLoader loader : loaders) {
			URL url = loader.getResource(name);
			if (url != null) {
				return url;
			}
		}

		return null;
	}

	@Override
	protected Enumeration<URL> findResources(String name) throws IOException {
		List<URL> resources = new ArrayList<URL>();
		for (ClassLoader loader : loaders) {
			resources.addAll(Collections.list(loader.getResources(name)));
		}

		return Collections.enumeration(resources);
	}

}
