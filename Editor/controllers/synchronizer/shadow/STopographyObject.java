package controllers.synchronizer.shadow;

import java.util.List;

import controllers.synchronizer.Converter;
import models.converter.InsProc;
import models.converter.ParseException;

/**
 * TopographyObject.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public class STopographyObject extends SCommonObject {

	public STopographyObject(String value) {
		super(value);
	}
	
	protected void addInsProc() {
		//super.addInsProc();
		
		new InsProc(this, "load_flatgrid") {			
			@Override
			public String run(List<String> command) throws Exception {
				if (command.size() != 2) throw new ParseException(ParseException.InvalidArgument);
				Converter.global.getNetwork().setWidth(Integer.parseInt(Converter.parseIdentify(command.get(0))));
				Converter.global.getNetwork().setLength(Integer.parseInt(Converter.parseIdentify(command.get(1))));		
				return "";		
			}

			@Override
			public String print(List<String> command) {
				return Converter.global.getNetwork().getWidth() + " " + Converter.global.getNetwork().getLength();
			}
		};
	}
}
