package models.converter;

import java.util.ArrayList;
import java.util.List;

public class Entry {				
	private InsProc insProc;
	private List<String> arg;
	private String sperator;
	
	private boolean isAuto;
	
	public Entry(InsProc insProc, List<String> arg, String sperator)
	{		
		this.insProc = insProc;
		this.arg = arg;
		this.sperator = sperator;
		
		isAuto = false;
	}

	public Entry(InsProc insProc, String sperator)
	{
		this.insProc = insProc;
		this.arg = null;
		this.sperator = sperator;
		
		isAuto = false;
	}
	
	public Entry(String command)
	{
		arg = new ArrayList<String>();
		arg.add(command);
		
		isAuto = true;
	}
	
	public String print() {
		if (isAuto)
		{
			return arg.get(0);
		}
		else
		{	
			return insProc.Print(arg) + " " + sperator;
		}
	}
}
