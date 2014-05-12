package controllers.plugins;

import java.io.File;
import java.util.List;
import java.util.Set;
/**
 * 
 * @author khaclinh
 *
 */
public interface PluginManager {

    /**
     * Retrieves all plugins.
     */
    public List<PluginWrapper> getPlugins();

    /**
     * Retrieves all plugins with this state.
     */
    public List<PluginWrapper> getPlugins(PluginState pluginState);

    /**
     * Retrieves all resolved plugins (with resolved dependency).
     */
  	public List<PluginWrapper> getResolvedPlugins();

	/**
	 * Retrieves all unresolved plugins (with unresolved dependency).
	 */
  	public List<PluginWrapper> getUnresolvedPlugins();

    /**
     * Retrieves all started plugins.
     */
    public List<PluginWrapper> getStartedPlugins();

    /**
     * Retrieves the plugin with this id.
     *
     * @param pluginId
     * @return the plugin
     */
    public PluginWrapper getPlugin(String pluginId);

    /**
     * Load plugins.
     */
    public void loadPlugins();

    /**
     * Load a plugin.
     *
     * @param pluginArchiveFile
     * @return the pluginId of the installed plugin or null
     */
	public String loadPlugin(File pluginArchiveFile);

    /**
     * Start all active plugins.
     */
    public void startPlugins();

    /**
     * Start the specified plugin and it's dependencies.
     *
     * @return the plugin state
     */
    public PluginState startPlugin(String pluginId);

    /**
     * Stop all active plugins.
     */
    public void stopPlugins();

    /**
     * Stop the specified plugin and it's dependencies.
     *
     * @return the plugin state
     */
    public PluginState stopPlugin(String pluginId);

    /**
     * Unload a plugin.
     *
     * @param pluginId
     * @return true if the plugin was unloaded
     */
    public boolean unloadPlugin(String pluginId);

    /**
     * Disables a plugin from being loaded.
     *
     * @param pluginId
     * @return true if plugin is disabled
     */
    public boolean disablePlugin(String pluginId);

    /**
     * Enables a plugin that has previously been disabled.
     *
     * @param pluginId
     * @return true if plugin is enabled
     */
    public boolean enablePlugin(String pluginId);

    /**
     * Deletes a plugin.
     *
     * @param pluginId
     * @return true if the plugin was deleted
     */
    public boolean deletePlugin(String pluginId);

	public PluginClassLoader getPluginClassLoader(String pluginId);

	public <T> List<T> getExtensions(Class<T> type);

    public Set<String> getExtensionClassNames(String pluginId);

    /**
	 * The runtime mode. Must currently be either DEVELOPMENT or DEPLOYMENT.
	 */
	public RuntimeMode getRuntimeMode();

    public void addPluginStateListener(PluginStateListener listener);

    public void removePluginStateListener(PluginStateListener listener);

}
