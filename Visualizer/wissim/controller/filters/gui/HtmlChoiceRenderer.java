package wissim.controller.filters.gui;

import java.awt.Component;

import javax.swing.JLabel;


public class HtmlChoiceRenderer extends JLabel implements ChoiceRenderer{
private static final long serialVersionUID = -825539410560961416L;
	
	public HtmlChoiceRenderer() {
		setOpaque(true);
	}
	
	@Override
	public Component getRendererComponent(IFilterEditor editor, Object value,
			boolean isSelected) {
		setText(value==null? "" : value.toString());
		editor.getLook().setupComponent(this, isSelected, 
				editor.getFilter().isEnabled());
		return this;
	}
}
