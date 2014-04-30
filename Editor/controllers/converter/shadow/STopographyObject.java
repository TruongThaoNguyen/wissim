package controllers.converter.shadow;

import java.util.List;

import controllers.converter.Converter;
import models.converter.InsProc;
import models.converter.ParseException;

/**
 * TopographyObject.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public class STopographyObject extends SCommonObject {

	public int width;
	public int height;

	public STopographyObject(String value) {
		super(value);
		
		width = 0;
		height = 0;
	}
	
	protected void addInsProc() {
		//super.addInsProc();
		
		new InsProc(this, "load_flatgrid") {			
			@Override
			public String run(List<String> command) throws Exception {
				if (command.size() != 2) throw new ParseException(ParseException.InvalidArgument);
				width = Integer.parseInt(Converter.parseIdentify(command.get(0)));
				height = Integer.parseInt(Converter.parseIdentify(command.get(1)));		
				return "";		
			}

			@Override
			public String print(List<String> command) {
				return width + " " + height;
			}
		};
	}
}