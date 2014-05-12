package wissim.controller.filters;
/**
 * To hop nhieu filters thong qua phep login Not
 * 
 * @author Tien
 *
 */
public class NotFilter extends AndFilter{
	public NotFilter()
	{
		super();
	}
	
	public NotFilter(IFilter... observables){
		super(observables);
	}

	@Override
	public boolean include(Entry rowEntry) {
		// TODO Auto-generated method stub
		return !isEnabled() || !super.include(rowEntry);
	}
	
	
}
