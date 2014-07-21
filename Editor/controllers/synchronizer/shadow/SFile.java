package controllers.synchronizer.shadow;

/**
 * File Object.
 * @author trongnguyen
 */
public class SFile extends SCommonObject {

	/**
	 * create new File Object.
	 */
	public SFile() {
		super("");		
	}
	
	/**
	 * get name of file.
	 * @return name of file
	 */
	public String getName()
	{
		return getInsVar("name").toString();
	}
	
	/**
	 * set name of file.
	 * @param name name of file
	 */
	public void setName(String name)
	{
		setInsVar("name", name);
	}
}
