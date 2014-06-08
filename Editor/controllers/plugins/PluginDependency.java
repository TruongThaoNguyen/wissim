package controllers.plugins;
/**
 * 
 * @author khaclinh
 *
 */
public class PluginDependency {

	private String pluginId;
	private PluginVersion pluginVersion;
	
	public PluginDependency(String dependency) {
		/*
		 int index = dependency.indexOf(':');
		 if (index == -1) {
			 throw new IllegalArgumentException("Illegal dependency specifier "+ dependency);
		 }
		 
		 this.pluginId = dependency.substring(0, index);
		 this.pluginVersion = PluginVersion.createVersion(dependency.substring(index + 1));
		 */
		this.pluginId = dependency;
	}

	public String getPluginId() {
		return pluginId;
	}

	public PluginVersion getPluginVersion() {
		return pluginVersion;
	}

	@Override
	public String toString() {
		return "PluginDependency [pluginId=" + pluginId + ", pluginVersion=" + pluginVersion + "]";
	}
	
}
