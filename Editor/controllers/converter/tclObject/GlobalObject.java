package controllers.converter.tclObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import models.converter.ParseException;
import models.converter.Token;
import models.converter.TokenType;
import controllers.converter.Parser;
import controllers.converter.Scanner;

/**
 * GlobalObject.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public class GlobalObject extends TclObject 
{		
	public SimulatorObject simObject;
	
	public HashMap<String, TclObject> insObj;	
	
	
	public GlobalObject(String value)
	{
		super(value);
		insObj = new LinkedHashMap<String, TclObject>();
		simObject = new SimulatorObject("Simulator");
		insObj.put("_o0", simObject);
	}	
	
	public void parse(String code) throws ParseException
	{
		Scanner scanner = new Scanner(code);

		while (scanner.haveNext())
		{			
			List<String> command = scanner.scanCommand();
			
//			System.out.print((scanner.getLine() + 1) + ":\t");
//			for (String word : command) System.out.print(word + " ");
//			System.out.println();
			
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
		if (command.isEmpty()) return null;
		
		String arg = Parser.parseIdentify(command.get(0));
		
		if (insProc.containsKey(arg))
		{
			command.remove(0);
			return insProc.get(arg).run(command);
		}
	
		TclObject obj = insObj.get(arg);
		if (obj != null)	
		{
			command.remove(0);
			return obj.parse(command);
		}
		
		obj = new CommonObject(arg);
		if (obj.insProc.containsKey(Parser.parseIdentify(command.get(1))))
		{			
			insObj.put(arg, obj);
			command.remove(0);
			return obj.parse(command);
		}
		
		// Undefined insProc 
		return insProc.get("undefined").run(command);
	}
		
	@Override
	public String printf() 
	{
		for (String i : insObj.keySet()) 
		{
			insObj.get(i).printf();
		}
		
		for (String i : insVar.keySet()) 
		{
			System.out.println("set " + i + " " + insVar.get(i));	
		}		
		
		return "";
	}
	
	// region ------------- InsProc ------------- //	
	
	@Override
	protected void addInsProc()
	{
		super.addInsProc();
		
		new InsProc(this, "clock") {
			@Override
			protected String run(List<String> command) throws Exception {
				record(this, command);
				return insprocClock(command);
			}

			@Override
			public String print(List<String> command) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}			
		};
		
		new InsProc(this, "new") {
			@Override
			protected String run(List<String> command) throws Exception {
				record(this, command);
				return insprocNew(command);
			}

			@Override
			public String print(List<String> command) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}			
		};
	
		new InsProc(this, "open") {
			@Override
			protected String run(List<String> command) throws Exception {
				record(this, command);
				return insprocOpen(command);
			}

			@Override
			public String print(List<String> command) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}			
		};
	
		new InsProc(this, "for") {
			@Override
			protected String run(List<String> command) throws Exception {
				record(this, command);
				return insprocFor(command);
			}

			@Override
			public String print(List<String> command) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}			
		};

		new InsProc(this, "incr") {
			@Override
			protected String run(List<String> command) throws Exception {				
				record(this, command);
				return insprocIncr(command);
			}

			@Override
			public String print(List<String> command) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}			
		};
		
		new InsProc(this, "expr") {
			@Override
			protected String run(List<String> command) throws Exception {
				record(this, command);
				return insprocExpr(command);
			}

			@Override
			public String print(List<String> command) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}			
		};
	}

	protected String insprocExpr(List<String> command) {
		// TODO:
		return "0";
	}

	private String insprocIncr(List<String> command) throws Exception {
		if (command.size() != 1) throw new ParseException(ParseException.InvalidArgument);
		String arg = Parser.parseIdentify(command.get(0));
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

	private String insprocNew(List<String> command) throws Exception 
	{
		if (command.size() == 0)	throw new ParseException(ParseException.MissArgument);
		if (command.size() >  1)	throw new ParseException(ParseException.InvalidArgument);
		
		String arg = Parser.parseIdentify(command.get(0));
		switch (arg) 
		{
			case "Simulator":
				return "_o0";
	
			case "Topography":
				String id = Parser.newIndentify();
				insObj.put(id, new TopographyObject(arg));
				return id;
				
			default:
				id = Parser.newIndentify();
				insObj.put(id, new CommonObject(arg));
				return id;
		}
	}

	private String insprocOpen(List<String> command) throws Exception 
	{
		if (command.size() == 0)	throw new ParseException(ParseException.MissArgument);
		if (command.size() > 2) 	throw new ParseException(ParseException.InvalidArgument);
		
		String arg = Parser.parseIdentify(command.get(1));
		if (!arg.equals("w") && !arg.equals("r")) throw new ParseException(ParseException.InvalidArgument);
		
		return Parser.parseIdentify(command.get(0));		
	}
	
	private String insprocClock(List<String> command) throws Exception 
	{
		if (command.size() == 0) throw new ParseException(ParseException.MissArgument);
		
		String arg = "[clock";
		for (String word : command) 
		{
			arg += " " + Parser.parseIdentify(word);
		}
		arg += "]";
		
		return arg;
	}

	// endregion
}
