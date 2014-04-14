package models.networkcomponents.protocols;

import java.util.HashMap;

/**
 * Represents a protocol
 * @author leecom
 *
 */
public abstract class Protocol {
//	/**
//	 * Name of the protocol.
//	 */
//	protected String name;
	
	HashMap<String, String> params = new HashMap<String, String>();
	
	public abstract String getLabel();
	
	public HashMap<String, String> getParameters() { return params; }
	
	public void setParameters(HashMap<String, String> params) { this.params = params; }
	
	public void addParameter(String param, String value) {
		params.put(param, value);
	}
	
	public String getValue(String param) {
		return params.get(param);
	}
}
