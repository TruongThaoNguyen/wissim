package controllers.plugins;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.plugins.util.DirectedGraph;
/**
 * 
 * @author khaclinh
 *
 */
class DependencyResolver {

	private static final Logger log = LoggerFactory.getLogger(DependencyResolver.class);
	
    private List<PluginWrapper> plugins;

	public DependencyResolver(List<PluginWrapper> plugins) {
		this.plugins = plugins;
	}

	/**
	 * Get the list of plugins in dependency sorted order.
	 */
	public List<PluginWrapper> getSortedPlugins() throws PluginException {
		DirectedGraph<String> graph = new DirectedGraph<String>();
		for (PluginWrapper pluginWrapper : plugins) {
			PluginDescriptor descriptor = pluginWrapper.getDescriptor();
			String pluginId = descriptor.getPluginId();
			List<PluginDependency> dependencies = descriptor.getDependencies();
			if (!dependencies.isEmpty()) {
				for (PluginDependency dependency : dependencies) {
					graph.addEdge(pluginId, dependency.getPluginId());
				}
			} else {
				graph.addVertex(pluginId);
			}
		}

		log.debug("Graph: {}", graph);
		List<String> pluginsId = graph.reverseTopologicalSort();

		if (pluginsId == null) {
			throw new CyclicDependencyException("Cyclic dependences !!!" + graph.toString());
		}

		log.debug("Plugins order: {}", pluginsId);
		List<PluginWrapper> sortedPlugins = new ArrayList<PluginWrapper>();
		for (String pluginId : pluginsId) {
			sortedPlugins.add(getPlugin(pluginId));
		}

		return sortedPlugins;
	}

	private PluginWrapper getPlugin(String pluginId) throws PluginNotFoundException {
		for (PluginWrapper pluginWrapper : plugins) {
			if (pluginId.equals(pluginWrapper.getDescriptor().getPluginId())) {
				return pluginWrapper;
			}
		}

		throw new PluginNotFoundException(pluginId);
	}

}
