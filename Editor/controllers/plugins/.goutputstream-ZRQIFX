package controllers.plugins;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
/**
 * 
 * @author khaclinh
 *
 */
public enum RuntimeMode {
	
	DEVELOPMENT("development"), // development
    DEPLOYMENT("deployment"); // deployment		

    private final String name;
    
	private static final Map<String, RuntimeMode> map = new HashMap<String, RuntimeMode>();
	
	static {
		for (RuntimeMode mode : RuntimeMode.values()) {
			map.put(mode.name, mode);
		}
	}
	
	private RuntimeMode(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
	
    public static RuntimeMode byName(String name) {
    	if (map.containsKey(name)) {
    		return map.get(name);
    	}

    	throw new NoSuchElementException("Cannot found PF4J runtime mode with name '" + name + 
    			"'. Must be 'development' or 'deployment'.");
    }
    
}