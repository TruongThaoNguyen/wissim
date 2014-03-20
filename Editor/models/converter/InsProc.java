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
	
	protected abstract String print(List<String> command);

	public static void record(InsProc proc, List<String> command)
	{		
		String l = command.get(command.size() - 1);
		CharType type = CharType.TypeOf(l.charAt(0));
		if (type == CharType.Semicolon || type == CharType.Separator) 
		{
			command.remove(l);
			Converter.generateEntry.add(new Entry(proc, command, l));
		}
		else
		{
			Converter.generateEntry.add(new Entry(proc, command, "\n"));
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
