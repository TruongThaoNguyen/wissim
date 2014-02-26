package views.helpers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

public class PrintHelper {
	private int unit;				// unit distance
	
	public int marginLeft;			// left margin
	public int marginTop;			// top margin
	public int marginRight;		// right margin
	public int marginBottom;		// bottom margin
	
	public int width;				// width of the document
	public int height;				// height of the document
	
	private int footerMarginLeft, footerMarginTop;	
	
	public TextStyleTemplate titleStyle, h1Style, h2Style, paragraphStyle;
	public TextStyleTemplate emphStyle, footerStyle;
	
	public PrintHelper(int unit, int width, int height) {
		this.unit = unit;
		this.width = width;
		this.height = height;
		
		applyStandardStyle();
	}
	
	private void applyStandardStyle() {
		marginLeft = unit;
		marginTop = unit;
		marginRight = unit;
		marginBottom = unit;
		
		footerMarginLeft = unit;
		footerMarginTop = (int) (height - marginBottom + 0.5 * unit);
		
		titleStyle = new TextStyleTemplate("title", "Calibri Light", (int) (0.4 * unit), SWT.NORMAL, Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
		h1Style = new TextStyleTemplate("h1", "Calibri Light", (int) (0.2 * unit), SWT.NORMAL, Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
		h2Style = new TextStyleTemplate("h2", "Calibri Light", (int) (0.15 * unit), SWT.NORMAL, Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
		paragraphStyle = new TextStyleTemplate("paragraph", "Calibri", (int) (0.12 * unit), SWT.NORMAL, Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		emphStyle = new TextStyleTemplate("emphasize", "Calibri", (int)(0.12 * unit), SWT.ITALIC, Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		footerStyle = new TextStyleTemplate("footer", "Calibri", (int)(0.10 * unit), SWT.NORMAL, Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	}
	
	public void drawFooter(GC gc, String text) {
		int size = footerStyle.font.getFontData()[0].getHeight() * 2;
		gc.setBackground(footerStyle.color);
		gc.fillRectangle(footerMarginLeft, footerMarginTop, size / 2, size);
		
		footerStyle.draw(gc, text, footerMarginLeft + size, footerMarginTop);
	}
	
	public void drawTitle(GC gc, String text) {
		titleStyle.draw(gc, text, marginLeft, marginTop);
		
		int offset = titleStyle.font.getFontData()[0].getHeight() * 2;
		gc.drawLine(marginLeft, marginTop + offset, width - marginRight, marginTop + offset);		
	}
	
	public int getUnit() { return unit; }
}
