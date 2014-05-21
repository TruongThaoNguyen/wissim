package views.dialogs;

import models.networkcomponents.events.Event.EventType;

public class EventEntry {
	public EventEntry(EventType type, int time) {
		this.type = type;
		this.time = time;
	}
	
	public EventType type;
	public int time;
}