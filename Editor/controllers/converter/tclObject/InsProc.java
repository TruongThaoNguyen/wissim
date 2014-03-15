package controllers.converter.tclObject;

import java.util.ArrayList;
import java.util.List;

/**
 * InsProc.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public abstract class InsProc 
{	
	public static List<InsProc> 	 insProcList = new ArrayList<InsProc>();
	public static List<List<String>> commandList = new ArrayList<List<String>>();
	
	public TclObject parent;
	public String insprocName;	
	
	public InsProc(TclObject parent, String insprocName)
	{
		this.parent = parent;
		this.insprocName = insprocName;
		
		parent.insProc.put(insprocName, this);
	}
	
	protected abstract String run(List<String> command) throws Exception;
	
	public abstract String print(List<String> command) throws Exception;

	public static void record(InsProc proc, List<String> command)
	{
		insProcList.add(proc);
		commandList.add(command);
	}
}
