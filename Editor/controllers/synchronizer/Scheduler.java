package controllers.synchronizer;

import java.util.HashMap;

/**
 * Interface for schedulable objects.
 * @author trongnguyen
 */
public interface Scheduler 
{	
	/**
	 * add an event to scheduler of this object.
	 * @param time time of event
	 * @param arg argument of event
	 */
	void 	addEvent(double time, String arg);
	
	/**
	 * get time of an event from scheduler.
	 * @param arg argument of event
	 * @return time of this event. return null if arg isn't match with any event
	 */
	double	getEvent(String arg);
	
	/**
	 * get all events.
	 * @return Hashmap include all event
	 */
	HashMap<String, Double> getEvent();
}
