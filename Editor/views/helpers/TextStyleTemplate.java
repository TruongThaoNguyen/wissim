package views.helpers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

public class TextStyleTemplate {
	String styleName;	// style name
	
	Font font;			// font will be used
	Color color;		// text color
	
	public TextStyleTemplate(String styleName, String fontName, int size, int style, Color color) {
		this.styleName = styleName;
		this.font = new Font(Display.getCurrent(), fontName, size, style);
		this.color = color;
	}
	
	/**
	 * Draw text by using this template
	 * @param gc
	 * @param text
	 * @param x
	 * @param y
	 */
	public void draw(GC gc, String text, int x, int y) {
		gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		gc.setForeground(color);
		gc.setFont(font);
		gc.drawString(text, x, y);
	}
	
	public Font getFont() { return font; }
	public Color getColor() { return color; }
	public String getStyleName() { return styleName; }
}
