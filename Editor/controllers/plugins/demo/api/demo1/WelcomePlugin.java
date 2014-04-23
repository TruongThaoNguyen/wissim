package controllers.plugins.demo.api.demo1;

import org.apache.commons.lang.StringUtils;

import controllers.plugins.Extension;
import controllers.plugins.Plugin;
import controllers.plugins.PluginWrapper;
import controllers.plugins.RuntimeMode;
import controllers.plugins.demo.api.Greeting;

/**
 * 
 * @author khaclinh
 *
 */
public class WelcomePlugin extends Plugin {

    public WelcomePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        System.out.println("WelcomePlugin.start()");
        // for testing the development mode
        if (RuntimeMode.DEVELOPMENT.equals(wrapper.getRuntimeMode())) {
        	System.out.println(StringUtils.upperCase("WelcomePlugin"));
        }
    }

    @Override
    public void stop() {
        System.out.println("WelcomePlugin.stop()");
    }

    @Extension
    public static class WelcomeGreeting implements Greeting {

    	@Override
        public String getGreeting() {
            return "Welcome";
        }

    }

}
