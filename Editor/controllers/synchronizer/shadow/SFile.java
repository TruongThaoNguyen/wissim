package controllers.synchronizer.shadow;

public class SFile extends SCommonObject {

	public SFile() {
		super("");		
	}
	
	public String getName()
	{
		return getInsVar("name").toString();
	}
	
	public void setName(String name)
	{
		setInsVar("name", name);
	}
}
