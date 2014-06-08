package controllers.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author khaclinh
 *
 */
public class PropertiesPluginDescriptorFinder implements PluginDescriptorFinder {

	private static final Logger log = LoggerFactory.getLogger(PropertiesPluginDescriptorFinder.class);
	
	private static final String DEFAULT_PROPERTIES_FILE_NAME = "plugin.properties";
	
	private String propertiesFileName;

	public PropertiesPluginDescriptorFinder() {
		this(DEFAULT_PROPERTIES_FILE_NAME);
	}

	public PropertiesPluginDescriptorFinder(String propertiesFileName) {
		this.propertiesFileName = propertiesFileName;
	}
	
	@Override
	public PluginDescriptor find(File pluginRepository) throws PluginException {
        File propertiesFile = new File(pluginRepository, propertiesFileName);
        log.debug("Lookup plugin descriptor in '{}'", propertiesFile);
        if (!propertiesFile.exists()) {
            throw new PluginException("Cannot find '" + propertiesFile + "' file");
        }

    	InputStream input = null;
		try {
			input = new FileInputStream(propertiesFile);
		} catch (FileNotFoundException e) {
			// not happening 
		}
		
    	Properties properties = new Properties();
        try {
        	properties.load(input);
        } catch (IOException e) {
            throw new PluginException(e.getMessage(), e);
        } finally {
            try {
				input.close();
			} catch (IOException e) {
				throw new PluginException(e.getMessage(), e);
			}
        } 
        
        PluginDescriptor pluginDescriptor = new PluginDescriptor();
        
        // TODO validate !!!
        String id = properties.getProperty("plugin.id");
        if (StringUtils.isEmpty(id)) {
        	throw new PluginException("plugin.id cannot be empty");
        }
        pluginDescriptor.setPluginId(id);
        
        String clazz = properties.getProperty("plugin.class");
        if (StringUtils.isEmpty(clazz)) {
        	throw new PluginException("plugin.class cannot be empty");
        }
        pluginDescriptor.setPluginClass(clazz);
        
        String version = properties.getProperty("plugin.version");
        if (StringUtils.isEmpty(version)) {
        	throw new PluginException("plugin.version cannot be empty");
        }
        pluginDescriptor.setPluginVersion(PluginVersion.createVersion(version));
        
        String provider = properties.getProperty("plugin.provider");
        pluginDescriptor.setProvider(provider);        
        String dependencies = properties.getProperty("plugin.dependencies");
        pluginDescriptor.setDependencies(dependencies);

		return pluginDescriptor;
	}

}
