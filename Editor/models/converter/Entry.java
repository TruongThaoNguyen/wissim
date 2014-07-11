package models.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import controllers.synchronizer.Synchronizer;

public class Entry {				
	private InsProc insProc;
	private List<String> arg;
	private String sperator;	
	
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
			int l = arg.size() - 1;
			for (int i = 0; i < l; i++)
			{
				String s;
				try {
					s = Synchronizer.parseIdentify(arg.get(i));				
				} catch (Exception e) {
					s = arg.get(i);
				}				
				this.arg.add(s);
			}
			this.arg.add(arg.get(l));
		}
		
		this.sperator = sperator;
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
	}

	public Entry(String arg)
	{
		insProc = Synchronizer.global.getInsProc(null);
		this.arg = Arrays.asList(arg);
		this.sperator = "";
	}
	
	@Override
	public String toString() {
		return insProc.Print(arg) + sperator;
	}
}
