package controllers.converter.tclObject;

import java.util.List;

import models.converter.ParseException;

abstract class InsProc {
	public abstract TclObject run(List<String> words) throws ParseException;
}
