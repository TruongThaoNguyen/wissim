package models.converter;

import java.util.List;

public class Entry {				
	private InsProc insProc;
	private List<String> arg;
	private String sperator;
	
	public Entry(InsProc insProc, List<String> arg, String sperator)
	{		
		this.insProc = insProc;
		this.arg = arg;
		this.sperator = sperator;
	}

	public String print() {
		return insProc.Print(arg) + " " + sperator;
	}
}
