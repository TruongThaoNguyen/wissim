package models.networkcomponents.events;

import java.util.Observable;

/**
 * Represents an event with type and raised time
 * @author leecom
 *
 */
public class Event extends Observable {
	public static enum EventType { STOP, START, ON, OFF }
	
	// time when event is raised
	double raisedTime;
	
	// type of event
	EventType type;
	
	public Event(EventType type, double raisedTime)
	{
		setRaisedTime((int) raisedTime);
		setType(type);
	}
	
	public EventType getType() { return type; }
	
	public int getRaisedTime() { return (int) raisedTime; }
	
	public void setRaisedTime(int rTime) { 
		if (rTime > 0) {
			raisedTime = rTime;
			
			setChanged();
			notifyObservers("Time");
		}
	}
	
	private void setType(EventType type) {
		this.type = type;
		
		setChanged();
		notifyObservers("RaisedTime");
	}
}
