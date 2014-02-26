package controllers.converters.tclObjects;

import java.util.List;

import models.converters.ParseException;
import models.converters.Token;


public class GlobalObject extends TclObject {	
	@Override
	protected TclObject parseComment(List<Token> token) throws ParseException {
		return null;
	}
}
