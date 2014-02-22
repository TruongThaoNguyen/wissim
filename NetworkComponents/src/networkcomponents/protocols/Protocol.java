package networkcomponents.protocols;

import java.util.HashMap;

/**
 * Represents a protocol
 * @author leecom
 *
 */
public abstract class Protocol {
	// name of the protocol
	String name;
	
	HashMap<String, String> params = new HashMap<String, String>();
	
	/**
	 * Initializes a protocol with name
	 * @param name
	 */
	public Protocol(String name) {
		this.name = name;
	}
	
	public String getName() { return name; }
	
	public HashMap<String, String> getParameters() { return params; }
	
	public void addParameter(String param, String value) {
		params.put(param, value);
	}
	
	public String getValue(String param) {
		return params.get(param);
	}
}
