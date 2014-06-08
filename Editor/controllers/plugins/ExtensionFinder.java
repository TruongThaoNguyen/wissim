package controllers.plugins;

import java.util.List;
import java.util.Set;
/**
 * 
 * @author khaclinh
 *
 */
public interface ExtensionFinder {

    /**
     * Retrieves a list with all extensions found for an extension point.
     */
    public <T> List<ExtensionWrapper<T>> find(Class<T> type);

    /**
     * Retrieves a list with all extension class names found for a plugin.
     */
    public Set<String> findClassNames(String pluginId);

}
