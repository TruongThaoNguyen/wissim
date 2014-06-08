package wissim.controller.filters.gui;

import java.awt.Component;

public interface ChoiceRenderer {
	Component getRendererComponent(IFilterEditor editor,
            Object        value,
            boolean       isSelected);

}
