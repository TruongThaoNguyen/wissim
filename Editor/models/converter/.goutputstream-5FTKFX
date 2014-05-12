package models.converter;

import java.util.ArrayList;
import java.util.List;

import controllers.converter.Converter;

public class Entry {				
	private InsProc insProc;
	private List<String> arg;
	private String sperator;
	
	private boolean isAuto;
	
	/**
	 * Create new Register Entry.
	 * Parse identify with each argument. 
	 * @param insProc
	 * @param arg
	 * @param sperator
	 */
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

	/**
	 * Create new Register Entry for InsProc Null.
	 * @param insProc
	 * @param sperator
	 */
	public Entry(InsProc insProc, String sperator)
	{
		this.insProc = insProc;
		this.arg = null;
		this.sperator = sperator;
		
		isAuto = false;
	}
	
	/**
	 * Create new Register Entry without parse identify argument.
	 * @param insProc
	 * @param arg
	 */
	public Entry(InsProc insProc, List<String> arg)
	{		
		String l = arg.get(arg.size() - 1);
		CharType type = CharType.TypeOf(l.charAt(0));
		if (type == CharType.Semicolon || type == CharType.Separator) 
		{
			arg.remove(l);
			this.sperator = l;
		}
		else this.sperator = "\n";
		
		this.insProc = insProc;	
		this.arg 	 = arg;
		this.isAuto  = false;
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
