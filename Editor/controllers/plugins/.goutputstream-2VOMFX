package controllers.plugins;

/**
 * 
 * @author khaclinh
 *
 * @param <T>
 */
public class ExtensionWrapper<T> implements Comparable<ExtensionWrapper<T>> {

	private final T instance;
	private final int ordinal;
	
	public ExtensionWrapper(T instance, int ordinal) {
		this.instance = instance;
		this.ordinal = ordinal;
	}

	public T getInstance() {
		return instance;
	}

	public int getOrdinal() {
		return ordinal;
	}

	@Override
	public int compareTo(ExtensionWrapper<T> o) {
		return (ordinal - o.getOrdinal());
	}

}