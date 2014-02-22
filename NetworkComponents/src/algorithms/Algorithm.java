package algorithms;

public abstract class Algorithm {
	// input of the algorithm
	protected Object input;
	
	public Algorithm(Object input) {
		this.input = input;
	}
	
	/**
	 * Execute algorithm and return an object as the result
	 * @return
	 */
	public abstract Object doAlgorithm();
	
	public void setInput(Object input) {
		this.input = input;
	}
}
