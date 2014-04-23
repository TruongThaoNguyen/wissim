package controllers.plugins;

import java.util.EventListener;

/**
 * 
 * @author khaclinh
 *
 */
public interface PluginStateListener extends EventListener {

    /**
     * Invoked when a plugin's state (for example DISABLED, STARTED) is changed.
     */
    public void pluginStateChanged(PluginStateEvent event);

}
