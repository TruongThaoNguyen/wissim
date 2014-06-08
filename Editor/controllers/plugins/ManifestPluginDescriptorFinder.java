package controllers.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author khaclinh
 *
 */
public class ManifestPluginDescriptorFinder implements PluginDescriptorFinder {

	private static final Logger log = LoggerFactory.getLogger(ManifestPluginDescriptorFinder.class);
	
	private PluginClasspath pluginClasspath;
	
	public ManifestPluginDescriptorFinder(PluginClasspath pluginClasspath) {
		this.pluginClasspath = pluginClasspath;
	}

	@Override
	public PluginDescriptor find(File pluginRepository) throws PluginException {
    	// TODO it's ok with first classes directory? Another idea is to specify in PluginClasspath the folder.
		String classes = pluginClasspath.getClassesDirectories().get(0);
        File manifestFile = new File(pluginRepository, classes + "/META-INF/MANIFEST.MF");
        log.debug("Lookup plugin descriptor in '{}'", manifestFile);
        if (!manifestFile.exists()) {
            throw new PluginException("Cannot find '" + manifestFile + "' file");
        }

    	FileInputStream input = null;
		try {
			input = new FileInputStream(manifestFile);
		} catch (FileNotFoundException e) {
			// not happening 
		}
		
    	Manifest manifest = null;
        try {
            manifest = new Manifest(input);
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
        Attributes attrs = manifest.getMainAttributes();
        String id = attrs.getValue("Plugin-Id");
        if (StringUtils.isEmpty(id)) {
        	throw new PluginException("Plugin-Id cannot be empty");
        }
        pluginDescriptor.setPluginId(id);
        
        String clazz = attrs.getValue("Plugin-Class");
        if (StringUtils.isEmpty(clazz)) {
        	throw new PluginException("Plugin-Class cannot be empty");
        }
        pluginDescriptor.setPluginClass(clazz);
        
        String version = attrs.getValue("Plugin-Version");
        if (StringUtils.isEmpty(version)) {
        	throw new PluginException("Plugin-Version cannot be empty");
        }
        pluginDescriptor.setPluginVersion(PluginVersion.createVersion(version));
        
        String provider = attrs.getValue("Plugin-Provider");
        pluginDescriptor.setProvider(provider);        
        String dependencies = attrs.getValue("Plugin-Dependencies");
        pluginDescriptor.setDependencies(dependencies);

		return pluginDescriptor;
	}
    	
}
