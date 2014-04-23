package controllers.plugins;

import java.io.File;

/**
 * 
 * @author khaclinh
 *
 */
public interface PluginDescriptorFinder {

	public PluginDescriptor find(File pluginRepository) throws PluginException;
	
}
