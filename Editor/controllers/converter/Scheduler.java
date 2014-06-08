package controllers.converter;

import java.util.HashMap;

public interface Scheduler {	
	void 	addEvent(double time, String arg);
	double	getEvent(String arg);
	HashMap<String, Double> getEvent();
}
