package controllers.converter.tclObject;

import java.util.List;

import controllers.converter.Parser;
import models.converter.ParseException;

/**
 * TopographyObject.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public class TopographyObject extends CommonObject {

	public TopographyObject(String value) {
		super(value);		
	}

	@Override
	protected void addInsProc() {
		super.addInsProc();
		
		new InsProc(this, "load_flatgrid") {			
			@Override
			public String run(List<String> command) throws Exception {
				record(this, command);
				return insprocLoadFlatgrid(command);
			}

			@Override
			public String print(List<String> command) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	protected String insprocLoadFlatgrid(List<String> command) throws Exception {
		if (command.size() != 2) throw new ParseException(ParseException.InvalidArgument);
		
		insVar.put("width",  Parser.parseIdentify(command.get(0)));
		insVar.put("height", Parser.parseIdentify(command.get(1)));
		
		return "";		
	}
}
