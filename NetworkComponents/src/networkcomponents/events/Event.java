package networkcomponents.events;

import java.util.Observable;

/**
 * Represents an event with type and raised time
 * @author leecom
 *
 */
public abstract class Event extends Observable {
	// time when event is raised
	int raisedTime;
	
	// type of event
	int type;
	
	/**
	 * Constructor
	 * Initialize an Event object with type and RaisedTime
	 * @param type Type of event
	 * @param raisedTime Time that the event is raised
	 */
	public Event(int type, int raisedTime) {
		setRaisedTime(raisedTime);
		setType(type);
	}
	
	public int getType() { return type; }
	
	public int getRaisedTime() { return raisedTime; }
	
	public void setRaisedTime(int rTime) { 
		if (rTime > 0) {
			raisedTime = rTime;
			
			setChanged();
			notifyObservers("Time");
		}
	}
	
	private void setType(int type) {
		this.type = type;
		
		setChanged();
		notifyObservers("RaisedTime");
	}
}
