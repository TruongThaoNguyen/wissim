package wissim.controller.filters;
/**
 * To hop nhieu Filter thong qua phep logic AND
 * 
 * @author Tien
 *
 */
public class AndFilter extends ComposedFilter{
	public AndFilter(){
		super();
	}
	
	public AndFilter(IFilter...observables){
		super(observables);
	}

	@Override
	public boolean include(Entry rowEntry) {
		// TODO Auto-generated method stub
		for(IFilter filter : filters){
			if(filter.isEnabled() && !filter.include(rowEntry)){
				return false;
			}
		}
		
		return true;
	}
	
}
