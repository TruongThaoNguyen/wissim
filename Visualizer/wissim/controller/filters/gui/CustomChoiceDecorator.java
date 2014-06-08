package wissim.controller.filters.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JComponent;

public interface CustomChoiceDecorator {
	/** Returns the background color. */
    Color getBackground(CustomChoice  choice,
                        IFilterEditor editor,
                        boolean       isSelected);

    /** Returns the foreground color. */
    Color getForeground(CustomChoice  choice,
                        IFilterEditor editor,
                        boolean       isSelected);

    /** Returns the font. */
    Font getFont(CustomChoice choice, IFilterEditor editor, boolean isSelected);

    /** Decorates the choice on the given editor. */
    void decorateComponent(CustomChoice  choice,
                           IFilterEditor editor,
                           boolean       isSelected,
                           JComponent    c,
                           Graphics      g);

    /**
     * Default decorator, delegating always to the associated methods on the
     * {@link CustomChoice} instances. The font, by default, will be cursive
     */
    public class DefaultDecorator implements CustomChoiceDecorator {

        private Font baseFont;
        private Font italicFont;

        @Override public void decorateComponent(CustomChoice  choice,
                                                IFilterEditor editor,
                                                boolean       isSelected,
                                                JComponent    c,
                                                Graphics      g) {
            choice.decorateComponent(editor, isSelected, c, g);
        }

        @Override public Font getFont(CustomChoice  choice,
                                      IFilterEditor editor,
                                      boolean       isSelected) {
            Font ret = choice.getFont(editor, isSelected);
            if (ret == null) {
                ret = editor.getLook().getFont();
                if (ret != baseFont) {
                    baseFont = ret;
                    italicFont = baseFont.deriveFont(Font.ITALIC);
                }

                ret = italicFont;
            }

            return ret;
        }

        @Override public Color getBackground(CustomChoice  choice,
                                             IFilterEditor editor,
                                             boolean       isSelected) {
            Color color = choice.getBackground(editor, isSelected);
            if (color == null) {
                Look look = editor.getLook();
                color = isSelected ? look.getSelectionBackground() 
                		           : look.getBackground();
            }
            return color;
        }

        @Override public Color getForeground(CustomChoice  choice,
                                             IFilterEditor editor,
                                             boolean       isSelected) {
            Color color = choice.getForeground(editor, isSelected);
            if (color == null) {
                Look look = editor.getLook();
                color = isSelected ? look.getSelectionForeground() 
                		           : look.getForeground();
            }
            return color;
        }
    }
}
