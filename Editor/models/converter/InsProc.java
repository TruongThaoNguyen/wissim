package models.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import controllers.converter.Converter;
import controllers.converter.TclObject;

/**
 * InsProc.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public abstract class InsProc 
{				
	public String insprocName;
	protected TclObject parent;
	protected Entry entry;
	
	/**
	 * Create new InsProc and add this InsProc to parent's InsProc list.
	 * @param parent
	 * @param insprocName
	 */
	public InsProc(TclObject parent, String insprocName) {
		this.parent = parent;
		this.insprocName = insprocName;
		
		parent.addInsProc(this);
	}
	
	/**
	 * Run this InsProc.
	 * @param arg arguments.
	 * @param isRecord record this runtime to register.
	 * @return Result of runtime
	 * @throws Exception
	 */
	public String Run(List<String> arg, boolean isRecord) throws Exception {
		if (isRecord) record(arg);
		return run(arg);
	}

	/**
	 * Real runtime method. Implement in each individual InsProc.
	 * @param arg arguments
	 * @return runtime result
	 * @throws Exception
	 */
	protected abstract String run(List<String> arg) throws Exception;
	
	/**
	 * Print TCL command corresponding.
	 * @param arg arguments
	 * @return
	 */
	public String Print(List<String> arg) {
		return 	(parent.getLabel() == null ? "" : parent.getLabel() + " " ) + 
				(insprocName == null ? "" : insprocName + " " ) + 
				print(arg);
	}
	
	/**
	 * Default print method.
	 * @param arg arguments.
	 * @return
	 */
	protected String print(List<String> arg) {
		StringBuilder sb = new StringBuilder();
		for (String string : arg)
		{	
			TclObject o = Converter.global.getObject(string);
			if (o != null)	sb.append(o.getLabel() + " ");		
			else			sb.append(string + " ");		
		}
		return sb.toString();
	}

	/**
	 * Record the InsProc to Register.
	 * @param proc
	 * @param command
	 */
	protected void record(List<String> command)	{				
		if (command.size() > 0)
		{
			String l = command.get(command.size() - 1);
			CharType type = CharType.TypeOf(l.charAt(0));
			if (type == CharType.Semicolon || type == CharType.Separator) 
			{
				command.remove(l);
				entry = new Entry(this, command, l);				
				parent.addEntry(entry);				
			}
			else
			{
				entry = new Entry(this, command, "\n");
			}
		}
		else 
		{
			entry = new Entry(this, new ArrayList<>(Arrays.asList("\n")));
		}
		
		Converter.generateEntry.add(entry);
	}
}
