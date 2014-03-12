package controllers.converter.tclObject;

import java.util.List;

/**
 * InsProc.java
 * @Copyright (C) 2014, Sedic Laboratory, Hanoi University of Science and Technology
 * @Author Duc-Trong Nguyen
 * @Version 2.0
 */

public abstract class InsProc 
{
	public abstract String run(List<String> command) throws Exception;
}
