package controllers.converter;

import java.util.List;

import models.converter.InsProc;

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
	public abstract String parse(List<String> command) throws Exception;
	
	String getLabel();
	
	void setLabel(String label);
	
	void addInsProc(InsProc p);
	
	InsProc getInsProc(String key);	
	
	String getInsVar(String key);
	
	String setInsVar(String key, String value);	
}