package models.converter;

import java.util.ArrayList;
import java.util.List;

import controllers.converter.Converter;

public class Entry {				
	private InsProc insProc;
	private List<String> arg;
	private String sperator;
	
	private boolean isAuto;
	
	public Entry(InsProc insProc, List<String> arg, String sperator)
	{		
		this.insProc = insProc;
		if (insProc.insprocName == null)
		{		
			this.arg = arg;
		}
		else
		{
			this.arg = new ArrayList<>();
			for (String string : arg) {
				String s;
				try {
					s = Converter.parseIdentify(string);				
				} catch (Exception e) {
					s = null;
				}
				if (s == null) s = string;
				this.arg.add(s);
			}
		}
		
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
