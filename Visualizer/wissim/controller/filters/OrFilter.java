package wissim.controller.filters;
/**
 * To hop nhieu filter thong qua phep OR
 * 
 * @author Tien
 *
 */
public class OrFilter extends ComposedFilter{
	public OrFilter(){
		super();
	}
	
	public OrFilter(IFilter... observables){
		super(observables);
	}

	@Override
	public boolean include(Entry rowEntry) {
		// TODO Auto-generated method stub
		boolean ret = true;
		for (IFilter filter : filters){
			if(filter.isEnabled()){
				if(filter.include(rowEntry)){
					return true;
				}
				
				ret = false;
			}
		}
		
		return ret;
	}
}
