package controllers.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author khaclinh
 *
 */
public class PluginDescriptor {

	private String pluginId;
    private String pluginClass;
    private PluginVersion version;
    private String provider;
    private List<PluginDependency> dependencies;

    public PluginDescriptor() {
        dependencies = new ArrayList<PluginDependency>();
    }

    /**
     * Returns the unique identifier of this plugin.
     */
    public String getPluginId() {
        return pluginId;
    }

    /**
     * Returns the name of the class that implements Plugin interface.
     */
    public String getPluginClass() {
        return pluginClass;
    }

    /**
     * Returns the version of this plugin.
     */
    public PluginVersion getVersion() {
        return version;
    }

    /**
     * Returns the provider name of this plugin.
     */
    public String getProvider() {
        return provider;
    }

    /**
     * Returns all dependencies declared by this plugin.
     * Returns an empty array if this plugin does not declare any require.
     */
    public List<PluginDependency> getDependencies() {
        return dependencies;
    }

    @Override
	public String toString() {
		return "PluginDescriptor [pluginId=" + pluginId + ", pluginClass="
				+ pluginClass + ", version=" + version + ", provider="
				+ provider + ", dependencies=" + dependencies
				+ "]";
	}

	void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    void setPluginClass(String pluginClassName) {
        this.pluginClass = pluginClassName;
    }

    void setPluginVersion(PluginVersion version) {
        this.version = version;
    }

    void setProvider(String provider) {
        this.provider = provider;
    }
    
    void setDependencies(String dependencies) {
    	if (dependencies != null) {
    		dependencies = dependencies.trim();
    		if (dependencies.isEmpty()) {
    			this.dependencies = Collections.emptyList();
    		} else {
	    		this.dependencies = new ArrayList<PluginDependency>();
	    		String[] tokens = dependencies.split(",");    		
	    		for (String dependency : tokens) {
	    			dependency = dependency.trim();
	    			if (!dependency.isEmpty()) {
	    				this.dependencies.add(new PluginDependency(dependency));
	    			}
	    		}
	    		if (this.dependencies.isEmpty()) {
	    			this.dependencies = Collections.emptyList();
	    		}
    		}
    	} else {
    		this.dependencies = Collections.emptyList();
    	}
    }

}
