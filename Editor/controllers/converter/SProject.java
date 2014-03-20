package controllers.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.Project;
import models.converter.InsProc;
import models.converter.ParseException;
import models.converter.Token;
import models.converter.TokenType;

/**
 * GlobalObject.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */
public class SProject  extends Project implements TclObject
{
	public HashMap<String, String> 		insVar;
	public HashMap<String, InsProc> 	insProc;		
	public HashMap<String, TclObject>	insObj;
	
	public SProject()
	{
		super();
		network = new SNetwork("[new Simulator]");
		
		insVar 	= new HashMap<String, String>();
		insProc = new HashMap<String, InsProc>();
		insObj 	= new HashMap<String, TclObject>();
		insObj.put("_o0", (TclObject) network);
		
		addInsProc();

		//		entryList = new ArrayList<Entry>();		
	}	
	
	public void parse(String code) throws ParseException
	{
		Scanner scanner = new Scanner(code);

		while (scanner.haveNext())
		{			
			List<String> command = scanner.scanCommand();
			
			System.out.print((scanner.getLine() + 1) + ":\t");
			for (String word : command) System.out.print(" " + word);			
			
			try 
			{
				parse(command);
			} 
			catch (ParseException e)
			{
				e.setLine(e.getLine() + scanner.getLine());
				throw e;
			} 
			catch (Exception e) 
			{
				throw new ParseException(scanner.getLine(), e.getMessage());
			}
		}
	}
	
	@Override
	public String parse(List<String> command) throws Exception
	{		
		if (command.isEmpty()) 		return null;		
		if (command.size() <= 2)	return getInsProc(null).Run(command);					
		
		String arg = Converter.parseIdentify(command.get(0));
		
		InsProc p = getInsProc(arg);
		if (p != null)
		{
			command.remove(0);
			return p.Run(command);
		}
	
		TclObject obj = insObj.get(arg);
		if (obj != null)	
		{
			command.remove(0);
			return obj.parse(command);
		}
		
		obj = new SCommonObject(arg);
		p = obj.getInsProc(Converter.parseIdentify(command.get(1)));
		if (p != null)
		{			
			insObj.put(arg, obj);
			command.remove(0);
			command.remove(0);
			return p.Run(command);
		}
		
		// Undefined insProc 
		return getInsProc(null).Run(command);
	}	

	@Override
	public String getLabel() {
		return null;
	}
	
	@Override
	public void setLabel(String label) {
		// Do nothing
	}
	
	// region ------------------- InsVar ------------------- //
	
	@Override
	public String getInsVar(String key) 
	{
		return insVar.get(key);
	}
	
	@Override
	public String setInsVar(String key, String value) {
		TclObject obj = insObj.get(value);
		if (obj != null) obj.setLabel("$" + key);
		return insVar.put(key, value);
	}

	// endregion InsVar	
	
	// region ------------- InsProc ------------- //	
		
	@Override
	public InsProc getInsProc(String key) 
	{
		return insProc.get(key);
	}
	
	@Override
	public void addInsProc(InsProc p) {
		insProc.put(p.insprocName, p);
	}
	
	protected void addInsProc()
	{			
		new InsProc(this, null) {
			@Override
			protected String run(List<String> command) throws Exception {				
				return null;
			}

			@Override
			public String print(List<String> command) {
				StringBuilder sb = new StringBuilder();
				for (String s : command) {
					sb.append(s);
				}
				return sb.toString();
			}			
		};
		
		new InsProc(this, "set") {
			@Override
			protected String run(List<String> command) throws Exception {				
				switch (command.size()) 
				{
					case 0 : throw new ParseException(ParseException.MissArgument);
					case 1 : return getInsVar(Converter.parseIdentify(command.get(0)));
					case 2 : return setInsVar(Converter.parseIdentify(command.get(0)), Converter.parseIdentify(command.get(1)));
					default: throw new ParseException(ParseException.InvalidArgument);
				}
			}

			@Override
			public String print(List<String> command) {
				String arg = parent.getInsVar(command.get(0));
				TclObject obj = Converter.global.insObj.get(arg);
				if (obj != null)
					//return command.get(0) + " " + obj.getLabel();
					return command.get(0) + " " + command.get(1);
				else
					return command.get(0) + " " + arg;
			}
			
		};
			
		new InsProc(this, "new") {
			@Override
			protected String run(List<String> command) throws Exception {
				return insprocNew(command);
			}

			@Override
			public String print(List<String> command) {
				StringBuilder sb = new StringBuilder();
				for (String string : command) {
					sb.append(string);
				}
				return sb.toString();
			}			
		};
	
		new InsProc(this, "open") {
			@Override
			protected String run(List<String> command) throws Exception {
				return insprocOpen(command);
			}

			@Override
			public String print(List<String> command) {
				StringBuilder sb = new StringBuilder();
				for (String string : command) {
					sb.append(string);
				}
				return sb.toString();
			}			
		};
	
		new InsProc(this, "for") {
			@Override
			protected String run(List<String> command) throws Exception {
				return insprocFor(command);
			}

			@Override
			public String print(List<String> command) {
				return "";
			}			
		};

		new InsProc(this, "incr") {
			@Override
			protected String run(List<String> command) throws Exception {				
				return insprocIncr(command);
			}

			@Override
			public String print(List<String> command) {
				StringBuilder sb = new StringBuilder();
				for (String string : command) {
					sb.append(string);
				}
				return sb.toString();
			}			
		};
		
		new InsProc(this, "expr") {
			@Override
			protected String run(List<String> command) throws Exception {
				return insprocExpr(command);
			}

			@Override
			public String print(List<String> command) {
				StringBuilder sb = new StringBuilder();
				for (String string : command) {
					sb.append(string);
				}
				return sb.toString();
			}			
		};
	
//		new InsProc(this, "<") {
//			@Override
//			protected String run(List<String> command) throws Exception {
//				return inspocLessThan(command);
//			}
//
//			@Override
//			public String print(List<String> command) throws Exception {
//				// TODO
//				return parent.value + " < " + command.get(0);
//			}			
//		};
	}
	
	private String insprocExpr(List<String> command) {
		// TODO:
		return "0";
	}

	private String insprocIncr(List<String> command) throws Exception {
		if (command.size() != 1) throw new ParseException(ParseException.InvalidArgument);
		String arg = Converter.parseIdentify(command.get(0));
		return insVar.put(arg, (Integer.parseInt(insVar.get(arg)) + 1) + "");
	}

	private String insprocFor(List<String> command) throws Exception {
		if (command.size() != 4) throw new ParseException(ParseException.InvalidArgument);
		
		String[] arg = new String[4];
		
		for (int i = 0; i < 4; i++)
		{
			Scanner scanner = new Scanner(command.get(i));
			List<Token> token = scanner.scanWord();
			if (scanner.haveNext() || token.size() != 1 || token.get(0).Type != TokenType.Brace) 
				throw new ParseException(ParseException.InvalidArgument);
			arg[i] = token.get(0).Value;
		}
		
		Scanner scanner = new Scanner(arg[1]);
		List<String> arg1 = scanner.scanCommand();
		if (scanner.haveNext()) throw new ParseException(ParseException.InvalidArgument);
		
		int limit = 10000;
		parse(arg[0]);
		while (Boolean.parseBoolean(parse(new ArrayList<String>(arg1))) && limit > 0)			
		{
			parse(arg[3]);			
			parse(arg[2]);
			limit--;
		}
		
		return "";
	}

	private String insprocNew(List<String> command) throws Exception {
		if (command.size() == 0)	throw new ParseException(ParseException.MissArgument);
		if (command.size() >  1)	throw new ParseException(ParseException.InvalidArgument);
		
		String arg = Converter.parseIdentify(command.get(0));
		switch (arg) 
		{
			case "Simulator":
				return "_o0";
	
			case "Topography":
				String id = Converter.newIndentify();
				TclObject newObj =  new STopographyObject("[new " + arg + "]");
				insObj.put(id, newObj);
				return id;
				
			default:
				id = Converter.newIndentify();
				newObj = new SCommonObject("[new " + arg + "]"); 
				insObj.put(id, newObj);
				return id;
		}
	}

	private String insprocOpen(List<String> command) throws Exception {
		if (command.size() == 0)	throw new ParseException(ParseException.MissArgument);
		if (command.size() > 2) 	throw new ParseException(ParseException.InvalidArgument);
		
		String arg = Converter.parseIdentify(command.get(1));
		if (!arg.equals("w") && !arg.equals("r")) throw new ParseException(ParseException.InvalidArgument);
		
		return "[open " + Converter.parseIdentify(command.get(0)) + " " + command.get(1) + "]";		
	}
	
	// endregion
}
