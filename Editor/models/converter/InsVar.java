package models.converter;

public class InsVar {
	private String Value;
	private String Label;
	
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
		return Label;
	}

	public String getValue() {
		return Value;
	}
	
	public String setValue(Object value)
	{
		return setValue(value, true);
	}

	public String setValue(Object value, boolean isChangLable)
	{
		if (value != this.Value)
		{
			this.Value = value.toString();
			if (isChangLable) 
			{
				this.Label = value.toString();
			}
		}
		return this.Value;
	}	
	
	public String getLabel()
	{
		return Label;
	}
}
