package wissim.controller.filters.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;


public class Look {
	Color background;
    Color disabledBackground;
    Color disabledForeground;
    Color errorForeground;
    Color foreground;
    Color gridColor;
    Color selectionBackground;
    Color selectionForeground;
    Color textSelection;
    Color warningForeground;
    Font font;
    int maxVisiblePopupRows = FilterSettings.maxVisiblePopupRows;
    CustomChoiceDecorator customChoiceDecorator = FilterSettings
            .newCustomChoiceDecorator();

    /** Returns the background color used by the editors. */
    public Color getBackground() {
        return background;
    }

    /** Returns the registered {@link CustomChoiceDecorator}. */
    public CustomChoiceDecorator getCustomChoiceDecorator() {
        return customChoiceDecorator;
    }

    /** Returns the background color used for disabled editors. */
    public Color getDisabledBackground() {
        return disabledBackground;
    }

    /** Returns the foreground color used for disabled editors. */
    public Color getDisabledForeground() {
        return disabledForeground;
    }

    /**
     * Returns the color set by default as foreground on each text editor
     * when the user commits any error on the filter expression.
     */
    public Color getErrorForeground() {
        return errorForeground;
    }

    /** Returns the font used on editors. */
    public Font getFont() {
        return font;
    }

    /** Returns the foreground color used by the editors. */
    public Color getForeground() {
        return foreground;
    }

    /** Returns the color set by default for the header's grid. */
    public Color getGridColor() {
        return gridColor;
    }

    /** Returns the maximum number of visible rows in the popup menu. */
    public int getMaxVisiblePopupRows() {
        return maxVisiblePopupRows;
    }

    /** Returns the background color on focused editors. */
    public Color getSelectionBackground() {
        return selectionBackground;
    }

    /** Returns the foreground color on focused editors. */
    public Color getSelectionForeground() {
        return selectionForeground;
    }

    /** Returns the color set by default as text selection on filters. */
    public Color getTextSelection() {
        return textSelection;
    }

    /**
     * <p>Returns the color set by default as foreground on each text editor
     * when the filter would produce no visible rows</p>
     */
    public Color getWarningForeground() {
        return warningForeground;
    }

    /**
     * Prepares the provided component to have the expected appearance<br>
     * Only the background, foreground and font are updated.
     */
    public void setupComponent(Component c,
                               boolean   isSelected,
                               boolean   isEnabled) {
        Color bg;
        Color fg;
        if (isEnabled) {
            if (isSelected) {
                bg = getSelectionBackground();
                fg = getSelectionForeground();
            } else {
                bg = getBackground();
                fg = getForeground();
            }
        } else {
            bg = getDisabledBackground();
            fg = getDisabledForeground();
        }

        if (bg != c.getBackground()) {
            c.setBackground(bg);
        }

        if (fg != c.getForeground()) {
            c.setForeground(fg);
        }

        if (c.getFont() != getFont()) {
            c.setFont(getFont());
        }
    }
}
