package controllers.synchronizer.shadow;

import java.util.List;

import controllers.synchronizer.Synchronizer;
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
				Synchronizer.global.getNetwork().setWidth(Integer.parseInt(Synchronizer.parseIdentify(command.get(0))));
				Synchronizer.global.getNetwork().setLength(Integer.parseInt(Synchronizer.parseIdentify(command.get(1))));		
				return "";		
			}

			@Override
			public String print(List<String> command) {
				return Synchronizer.global.getNetwork().getWidth() + " " + Synchronizer.global.getNetwork().getLength();
			}
		};
	}
}
