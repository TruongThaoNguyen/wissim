package wissim.controller.filters;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class de to hop nhieu Filter
 * 
 * @author Tien
 *
 */
abstract public class ComposedFilter extends Filter implements IFilterObserver{
	/*Tap hop cac Filters lien quan*/
	protected Set<IFilter> filters;
	
	/*Tap cac disabled filter*/
	private Set<IFilter> disabledFilters = new HashSet<IFilter>();
	
	protected ComposedFilter(){
		filters = new HashSet<IFilter>();
	}
	
	protected ComposedFilter(IFilter...observables){
		this();
		addFilter(observables);
	}
	
	/*Them doi tuong IFilter de nhan su kien filter tu doi tuong ComposedFiltere*/
	public void addFilter(IFilter... filtersToAdd){
		for(IFilter filter: filtersToAdd){
			if(filters.add(filter)){
				filter.addFilterObserver(this);
				if(filter.isEnabled()){
					super.setEnabled(true);
				}else{
					disabledFilters.add(filter);
				}
			}
		}
	}
	
	/*Go bo filter da tung lien ket*/
	public void removeFilter(IFilter...filtersToRemove){
		boolean report = false;
		for(IFilter filter : filtersToRemove){
			if(filters.remove(filter)){
				filter.removeFilterObserver(this);
				disabledFilters.remove(filter);
				report = true;
			}
		}
		
		if(report){
			if(isEnabled() && !filters.isEmpty() 
					&&(disabledFilters.size() == filters.size())){
				super.setEnabled(false);
			} else {
				reportFilterUpdatedToObservers();
			}
		}
	}
	
	/* Lay tap filter da them vao*/
	public Set<IFilter> getFilters(){
		return new HashSet<IFilter>(filters);
	}

	@Override
	public void filterUpdated(IFilter filter) {
		// TODO Auto-generated method stub
		boolean enabled = isEnabled();
		boolean changeState = false;
		if(filter.isEnabled()){
			changeState = disabledFilters.remove(filter) && !enabled;
		}else{
			changeState = disabledFilters.add(filter) && (disabledFilters.size() == filters.size());
		}
		
		if(changeState){
			super.setEnabled(!enabled);
		} else {
			reportFilterUpdatedToObservers();
		}
	}

	@Override
	public void setEnabled(boolean enable) {
		// TODO Auto-generated method stub
		if(filters.isEmpty()){
			super.setEnabled(enable);
		}else{
			for(IFilter filter : filters){
				filter.setEnabled(enable);
			}
		}
	}
	
	protected boolean isDisabled(IFilter filter){
		return disabledFilters.contains(filter);
	}
	
}
