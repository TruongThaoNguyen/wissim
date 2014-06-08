package wissim.ui;

public interface IObservable {
	public void register(IObserver obs);
	public void unRegister(IObserver obs);
	public void notifyObservers();

}
