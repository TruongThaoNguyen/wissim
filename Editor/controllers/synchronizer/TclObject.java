package controllers.synchronizer;

import java.util.HashMap;
import java.util.List;

import models.converter.Entry;
import models.converter.InsProc;
import models.converter.InsVar;

/**
 * Interface for Tcl shadow objects.
 * Tcl shadow objects is "shadow" of fable tcl object.
 * this interface include general method of all shadow object.
 */
public interface TclObject 
{
	
	/**
	 * Parse a tcl command and call corresponding InstProc. 
	 * @param command Token list, first token is identification of insProc that will used
	 * @param isRecord this command will be recorded or not
	 * @return String, result of InsProc
	 * @throws Exception Parse exception
	 */	
	String parse(List<String> command, boolean isRecord) throws Exception;
	
	/**
	 * get label of this object.
	 * @return string is label of object
	 */
	String	getLabel();	
	
	/**
	 * set label of this object.
	 * @param label new label to this object
	 */
	void	setLabel(String label);
	
	/**
	 * add an InsProc to this object.
	 * Each InsProc is corresponding of a tcl command.  
	 * @param p new insproc
	 */
	void 	addInsProc(InsProc p);
	
	/**
	 * get an insproc with key. Key is name of command that corresponding with InsProc.
	 * @param key key of Insproc.
	 * @return InsProc
	 */
	InsProc getInsProc(String key);	
		
	/**
	 * set an InsVar. Label of InsVar is its value.
	 * @param key key of InsVar. (name of variable)
	 * @param value value of InsVar. (value of variable)
	 * @return an InsVar.
	 */
	InsVar 	setInsVar(String key, String value);
	
	/**
	 * set an InsVar.
	 * @param key key of InsVar. (name of variable)
	 * @param value value of InsVar. (value of variable)
	 * @param label label of InsVar.
	 * @return InsVar
	 */
	InsVar	setInsVar(String key, String value, String label);
	
	/**
	 * get an InsVar.
	 * @param key key of InsVar. (name of variable)
	 * @return InsVar
	 */
	InsVar 	getInsVar(String key);
	
	/**
	 * get all InsVar.
	 * @return HashMap that included all InsVar.
	 */
	HashMap<String, InsVar> getInsVar();
	
	/**
	 * add an Entry to register list.
	 * @param e new entry
	 */
	void addEntry(Entry e);
	
	/**
	 * add an Entry to register list.
	 * @param index index in list to be add.
	 * @param e new entry.
	 */
	void addEntry(int index, Entry e);
	
	/**
	 * get all entries.
	 * @return list of entries.
	 */
	List<Entry> getEntry();
}