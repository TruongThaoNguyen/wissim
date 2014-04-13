package models.converter;

public class InsVar {
	public String Value;
	public String Label;
	
	/**
	 * Create new InsVar.
	 * @param value
	 */
	public InsVar(String value)
	{
		Value = value;
		Label = value;
	}
	
	/**
	 * Create new InsVar with value different label.
	 * @param value
	 * @param label
	 */
	public InsVar(String value, String label)
	{
		Value = value;
		Label = label;
	}

	@Override
	public String toString() {
		return Value;
	}
}
