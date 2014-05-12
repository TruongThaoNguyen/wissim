package models.networkcomponents.features;


import java.awt.Color;

public class GraphicLabel extends Label {
	Color color;

	public GraphicLabel(String name, Color color) {
		super(name);
		
		this.color = color;
	}

	public Color getColor() { return color; }
	
	public void setColor(Color color) {
		this.color = color;
		
		setChanged();
		notifyObservers("Color");
	}
}
