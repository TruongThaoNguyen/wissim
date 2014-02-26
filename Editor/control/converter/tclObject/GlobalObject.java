package control.converter.tclObject;

import java.util.List;

import model.converter.ParseException;
import model.converter.Token;


public class GlobalObject extends TclObject {	
	@Override
	protected TclObject parseComment(List<Token> token) throws ParseException {
		return null;
	}
}
