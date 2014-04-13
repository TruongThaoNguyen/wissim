package controllers.converter;

import java.util.HashMap;
import java.util.List;

import models.converter.Entry;
import models.converter.InsProc;
import models.converter.InsVar;

/**
 * TclObject.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */
public interface TclObject 
{	
	/**
	 * Parse by call InstProc
	 * @param command Token list, first token is identify of insProc that will used
	 * @return TclObject, result of InsProc
	 * @throws Exception
	 */
	String parse(List<String> command) throws Exception;
	
	String	getLabel();	
	void	setLabel(String label);
	
	void 	addInsProc(InsProc p);
	InsProc getInsProc(String key);	
		
	InsVar 	setInsVar(String key, String value);
	InsVar	setInsVar(String key, String value, String label);
	InsVar 	getInsVar(String key);	
	HashMap<String, InsVar> getInsVar();
	
	void 		setEntry(Entry e);
	List<Entry> getEntry();
	
	void addEvent(Double time, String arg);
}