package control.converter.tclObject;

import java.util.List;

import model.converter.ParseException;
import model.converter.Token;

abstract class InsProc {
	public abstract TclObject run(List<Token> token) throws ParseException;
}
