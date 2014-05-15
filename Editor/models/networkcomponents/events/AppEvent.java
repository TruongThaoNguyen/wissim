package models.networkcomponents.events;

import models.networkcomponents.protocols.ApplicationProtocol;


/**
 * Represents an event of Application
 * There are two types of event: start to send packets (START) and stop sending packets (STOP).
 * @author leecom
 *
 */
public class AppEvent extends Event {
	public static final int STOP = 0;
	public static final int START = 1;
	
	// the app which this event belongs to
	ApplicationProtocol app;
	
	AppEvent(int type, int raisedTime, ApplicationProtocol app) {
		super(type, raisedTime);		
		this.app = app;
	}
}
