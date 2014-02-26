package controllers.converters.tclObjects;

import java.util.List;

import models.converters.ParseException;
import models.converters.Token;

abstract class InsProc {
	public abstract TclObject run(List<Token> token) throws ParseException;
}
