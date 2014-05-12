package models.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import models.Project;

/**
 * Manage states of a Project. Use for Undo/Redo/History purposes
 * @author leecom
 *
 */
public class CareTaker extends Observable {
	private List<ProjectMemento> mementoList;		// list of states 	
	private int currentState;						// keep the index of the current state
	
	/**
	 * Constructor
	 */
	public CareTaker() {
		mementoList = new ArrayList<ProjectMemento>();
		currentState = -1;
	}
	
	/**
	 * Save a state of project
	 * @param project
	 * @param desc
	 */
	public void save(Project project, String desc) {
		while (mementoList.size() > currentState + 1) {
			mementoList.remove(mementoList.get(mementoList.size() - 1));
		}
		
		ProjectMemento m = new ProjectMemento(desc);
//		m.save(project);
		mementoList.add(m);
		currentState++;
		
		System.out.println("Save state: " + desc + "; Current state: " + currentState);
		
		setChanged();
		notifyObservers(currentState);
	}
	
	/**
	 * Get state with a specified index
	 * @param index
	 * @return
	 */
//	public Project getState(int index) {
//		ProjectMemento pm = mementoList.get(index);
//		currentState = index;
//		
//		setChanged();
//		notifyObservers(currentState);
//		
//		return pm.restore();
//	}
	
	/**
	 * Get the current index
	 * @return
	 */
	public int getCurrentStateIndex() {
		return currentState;
	}
	
	/**
	 * Get last state
	 * @return
	 */
//	public Project getLastState() {
//		if (currentState > 0) {
//			currentState--;
//			System.out.println("Get State: " + currentState);
//			
//			setChanged();
//			notifyObservers(currentState);
//			
//			return mementoList.get(currentState).restore();
//		} else
//			return null;
//	}
	
	/**
	 * Get next state
	 * @return
	 */
//	public Project getNextState() {
//		if (currentState < mementoList.size() - 1) {
//			currentState++;
//			System.out.println("Get state: " + currentState);
//			
//			setChanged();
//			notifyObservers(currentState);
//			
//			return mementoList.get(currentState).restore();
//		} else {
//			return null;
//		}
//	}
	
	/**
	 * Get size of states stored
	 * @return
	 */
	public int getSize() {
		return mementoList.size();
	}
}
