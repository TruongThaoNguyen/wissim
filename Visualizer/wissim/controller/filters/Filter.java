package wissim.controller.filters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.RowFilter;

/**
 * Lop Filter implement tu interface IFilter 
 * 
 * @author Tien
 *
 */
abstract public class Filter extends RowFilter implements IFilter{
	/*Tap nhung observer lien quan */
	private Set<IFilterObserver> observers = new HashSet<IFilterObserver>();
	/*Enable state*/
	private boolean enabled = true;
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return enabled;
	}
	@Override
	public void setEnabled(boolean enable) {
		// TODO Auto-generated method stub
		if(enable != this.enabled){
			this.enabled = enable;
			reportFilterUpdatedToObservers();
		}
	}
	@Override
	public void addFilterObserver(IFilterObserver observer) {
		// TODO Auto-generated method stub
		observers.add(observer);
		
	}
	@Override
	public void removeFilterObserver(IFilterObserver observer) {
		// TODO Auto-generated method stub
		observers.remove(observer);
	}
	
	/*Lay tat ca nhung observer da dang ki*/
	public Set<IFilterObserver> getFilterObservers(){
		return new HashSet<IFilterObserver>(observers);
	}
	
	/*thong bao den observer khi Filter thay doi*/
	public void reportFilterUpdatedToObservers(){
		for(IFilterObserver obs : new ArrayList<IFilterObserver>(observers)){
			obs.filterUpdated(this);
		}
	}
}
