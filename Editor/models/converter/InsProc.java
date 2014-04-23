package models.converter;

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
	protected TclObject parent;
	public String insprocName;	
	
	public InsProc(TclObject parent, String insprocName)
	{
		this.parent = parent;
		this.insprocName = insprocName;
		
		parent.addInsProc(this);
	}
	
	public String Run(List<String> command) throws Exception
	{
		record(this, command);
		return run(command);
	}
	
	protected abstract String run(List<String> command) throws Exception;
	
	public String Print(List<String> command)
	{
		return 	(parent.getLabel() == null ? "" : parent.getLabel() + " " ) + 
				(insprocName == null ? "" : insprocName + " " ) + 
				print(command);
	}
	
	protected String print(List<String> command)
	{
		StringBuilder sb = new StringBuilder();
		for (String string : command)
		{
			sb.append(string + " ");		
		}
		return sb.toString();
	}

	public void record(InsProc proc, List<String> command)
	{				
		if (command.size() > 0)
		{
			String l = command.get(command.size() - 1);
			CharType type = CharType.TypeOf(l.charAt(0));
			if (type == CharType.Semicolon || type == CharType.Separator) 
			{
				command.remove(l);
				Entry e = new Entry(proc, command, l);
				Converter.generateEntry.add(e);
				parent.addEntry(e);
			}
			else
			{
				Converter.generateEntry.add(new Entry(proc, command, "\n"));
			}
		}
		else 
		{
			Converter.generateEntry.add(new Entry(proc, "\n"));
		}
	}
//	
//	// region ------------------- Entry ------------------- //
//	
//	protected void addEntry(Entry e)
//	{
//		if (generateEntry.isEmpty())
//		{
//			if (!Converter.generateEntry.isEmpty()) Converter.generateEntry.add(new Entry());
//			Converter.generateEntry.add(e);
//		}
//		else
//		{
//			Converter.generateEntry.add(Converter.generateEntry.indexOf(generateEntry.get(generateEntry.size() - 1)), e);
//		}
//		generateEntry.add(e);
//	}	
//
//	protected void addEntry(InsProc p, List<String> arg)
//	{
//		addEntry(new Entry(p, arg));
//	}
//	
//	// endregion Entry
}
