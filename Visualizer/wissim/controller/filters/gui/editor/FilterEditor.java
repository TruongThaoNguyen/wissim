package wissim.controller.filters.gui.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.text.Format;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.border.Border;


import wissim.controller.filters.Filter;
import wissim.controller.filters.IFilter;
import wissim.controller.filters.IParser;
import wissim.controller.filters.gui.AutoChoices;
import wissim.controller.filters.gui.ChoiceRenderer;
import wissim.controller.filters.gui.CustomChoice;
import wissim.controller.filters.gui.FiltersHandler;
import wissim.controller.filters.gui.IFilterEditor;
import wissim.controller.filters.gui.IParserModel;
import wissim.controller.filters.gui.Look;
import wissim.controller.filters.parser.DateComparator;

public class FilterEditor extends JComponent implements IFilterEditor{

	private static final long serialVersionUID = 6908400421021655278L;
    private EditorBorder border = new EditorBorder();
    private Set<CustomChoice> customChoices;
    private AutoChoices autoChoices;
    private Format format;
    private Comparator comparator;
    private Comparator choicesComparator;
    private Class modelClass;
    private boolean ignoreCase;
    private boolean enabledUI=true;

    FilterArrowButton downButton = new FilterArrowButton();
    EditorFilter filter = new EditorFilter();
    FiltersHandler filtersHandler;
    EditorComponent editor;
    PopupComponent popup;
    int modelIndex;

    public FilterEditor(FiltersHandler filtersHandler,
                        int            modelIndex,
                        Class<?>       modelClass,
                        Look           look) {
        this.filtersHandler = filtersHandler;
        this.modelIndex = modelIndex;
        this.modelClass = modelClass;
        this.format = getParserModel().getFormat(modelClass);
        this.ignoreCase = getParserModel().isIgnoreCase();
        this.comparator = getParserModel().getComparator(modelClass);
        this.choicesComparator = modelClass.equals(String.class) 
        		|| modelClass.isEnum() ? null : comparator;

        setLayout(new BorderLayout());
        setBorder(border);

        popup = new PopupComponent(this, format, getChoicesComparator(),
        		getStringComparator()) {

            @Override protected void choiceSelected(Object selection) {
                popupSelection(selection);
            }
        };
        downButton.addActionListener(new ActionListener() {

                @Override public void actionPerformed(ActionEvent e) {
                    triggerPopup(downButton);
                }
            });

        add(downButton, BorderLayout.EAST);

        editor = new EditorComponent(this, popup);
        setupComponent(editor);
        add(editor, BorderLayout.CENTER);

        setLook(look);
        editor.updateParser();
    }

    /** IFilterEditor method. */
    @Override public int getModelIndex() {
        return modelIndex;
    }

    /** IFilterEditor method. */
    @Override public Class getModelClass() {
        return modelClass;
    }

    /** IFilterEditor method. */
    @Override public IFilter getFilter() {
        return filter;
    }

    /** IFilterEditor method. */
    @Override public void resetFilter() {
        popup.clearHistory();
        setEditorContent(CustomChoice.MATCH_ALL);
        requestChoices();
    }

    /** IFilterEditor method. */
    @Override public void setContent(Object content) {
        setEditorContent((content == null || "".equals(content)) ? CustomChoice.MATCH_ALL : content);
    }

    /** IFilterEditor method. */
    @Override public Object getContent() {
        return editor.getContent();
    }

    /** IFilterEditor method. */
    @Override public void setAutoChoices(AutoChoices autoChoices) {
        if ((autoChoices != null) && (autoChoices != this.autoChoices)) {
            this.autoChoices = autoChoices;

            Object enums[] = modelClass.getEnumConstants();
            if ((Boolean.class == modelClass)
                    || ((enums != null) && (enums.length <= 8))) {
                setEditable(editor.isAutoCompletion()
                        || (autoChoices == AutoChoices.DISABLED));
                // the call to setEditable already invokes requestChoices
                setMaxHistory(0);
            } else {
                requestChoices();
            }
        }
    }

    /** IFilterEditor method. */
    @Override public AutoChoices getAutoChoices() {
        return autoChoices;
    }

    /** IFilterEditor method. */
    @Override public void setCustomChoices(Set<CustomChoice> choices) {
        if ((choices == null) || choices.isEmpty()) {
            this.customChoices = null;
        } else {
            this.customChoices = new HashSet<CustomChoice>(choices);
        }

        requestChoices();
    }

    /** IFilterEditor method. */
    @Override public Set<CustomChoice> getCustomChoices() {
        return (customChoices == null)
            ? new HashSet<CustomChoice>()
            : new HashSet<CustomChoice>(customChoices);
    }

    /** IFilterEditor method. */
    @Override public void setUserInteractionEnabled(boolean enable){
    	enabledUI = enable;
    	setFilterEnabled(filter.isEnabled());
    }
    
    /** IFilterEditor method. */
    @Override public boolean isUserInteractionEnabled() {
    	return enabledUI;
    }

    /** IFilterEditor method. */
    @Override public void setEditable(boolean editable) {
        if (getRenderer() == null) {
            editor.setTextMode(editable);
            requestChoices(); // escaping works now differently
        }
    }

    /** IFilterEditor method. */
    @Override public boolean isEditable() {
        return editor.isEditableContent();
    }

    /** IFilterEditor method. */
    @Override public void setIgnoreCase(boolean set) {
        if (ignoreCase != set) {
            ignoreCase = set;
            formatOrComparatorUpdated();
        }
    }

    /** IFilterEditor method. */
    @Override public boolean isIgnoreCase() {
        return ignoreCase;
    }

    /** IFilterEditor method. */
    @Override public void setFormat(Format format) {
        if (this.format != format) {
            this.format = format;
            // as bonus, override default Comparator for dates
            // if the instance is not a DateComparator, the user has set
            // its own comparator (perhaps has done it as well and it is
            // a DateComparator, but seems unlikely)
            if ((format != null) && (comparator instanceof DateComparator)
                    && Date.class.isAssignableFrom(modelClass)) {
                setComparator(DateComparator.getDateComparator(format));
            } else {
            	formatOrComparatorUpdated();
            }
        }
    }

    /** IFilterEditor method. */
    @Override public Format getFormat() {
        return format;
    }

    /** IFilterEditor method. */
    @Override public void setComparator(Comparator comparator) {
        // the comparator is used for sorting rendered choices, and for
    	// comparison operations on the text parser
        if ((comparator != this.comparator) && (comparator != null)) {
            this.comparator = comparator;
        	formatOrComparatorUpdated();
        }
    }

    /** IFilterEditor method. */
    @Override public Comparator getComparator() {
        return comparator;
    }

    /** IFilterEditor method. */
    @Override public void setChoicesComparator(Comparator comparator) {
		this.choicesComparator = comparator;
		formatOrComparatorUpdated();
    }
    
    /** IFilterEditor method. */
    @Override public Comparator getChoicesComparator() {
    	return choicesComparator;
    }
    
    /** IFilterEditor method. */
    @Override public void setAutoCompletion(boolean enable) {
        editor.setAutoCompletion(enable);
    }

    /** IFilterEditor method. */
    @Override public boolean isAutoCompletion() {
        return editor.isAutoCompletion();
    }

    /** IFilterEditor method. */
    @Override public void setInstantFiltering(boolean enable) {
        editor.setInstantFiltering(enable);
    }

    /** IFilterEditor method. */
    @Override public boolean isInstantFiltering() {
        return editor.isInstantFiltering();
    }

    /** IFilterEditor method. */
    @Override public void setMaxHistory(int size) {
        popup.setMaxHistory(size);
    }

    /** IFilterEditor method. */
    @Override public int getMaxHistory() {
        return popup.getMaxHistory();
    }

    /** IFilterEditor method. */
    @Override public void setHistory(List<Object> history){
        popup.setHistory(history);    	
    }

    /** IFilterEditor method. */
    @Override public List<Object> getHistory(){
    	return popup.getHistory();
    }


    /** IFilterEditor method. */
    @Override public void setRenderer(ChoiceRenderer renderer) {
    	if (renderer != getRenderer()){
	        if (renderer == null) {
	            popup.setStringContent(format, choicesComparator, 
	            	getStringComparator());
	            editor.setTextMode(true);
	        } else {
	            popup.setRenderedContent(renderer, 
	            	choicesComparator==null? comparator : choicesComparator, 
	            	getStringComparator());
	            editor.setRenderMode();
	        }
	        requestChoices();
    	}
    }

    /** IFilterEditor method. */
    @Override public ChoiceRenderer getRenderer() {
        return popup.getFilterRenderer().getUserRenderer();
    }

    /** IFilterEditor method. */
    @Override public Look getLook() {
        return downButton.getLook();
    }

    /** Method required by the TableFilterHeader to setup the editor. */
    public void setLook(Look look) {
        setBackground(look.getBackground());
        setForeground(look.getForeground());
        setFont(look.getFont());
        downButton.setLook(look);
        editor.updateLook();
        popup.setLook(look);
        border.color = look.getGridColor();
        repaint();
    }

    /** Method required by the FiltersHandler to propagate filter changes. */
    public boolean isEditing() {
        return editor.isFocused() && isEnabled();
    }

    /** Method invoked by the FiltersHandler to set the choices. */
    public void setChoices(Collection<?> choices) {
        popup.clearChoices();
        addChoices(choices);
    }

    /** Method invoked by the FiltersHandler to setup the choices. */
    public int getChoicesSize() {
        return popup.getChoicesSize();
    }

    /** Method invoked by the FiltersHandler to extend the choices. */
    public void addChoices(Collection<?> choices) {
        popup.addChoices(choices, editor.getChoicesParser());
    }

    /** Method called by the FiltersHandler to notify if rows are visible. */
    public void setWarning(boolean warning) {
        editor.setWarning(warning);
    }

    /** Enables / disables the editor, invoked from the filter itself. */
    void setFilterEnabled(boolean enabled) {
    	enabled = enabled && enabledUI;
    	if (enabled != isEnabled()){
	        super.setEnabled(enabled);
	        downButton.setEnabled(enabled);
	        popup.getFilterRenderer().setEnabled(enabled);
	        editor.setEnabled(enabled);
    	}
    }

    /** Method invoked by the EditorComponent to notify a filter update. */
    void filterUpdated(RowFilter editorFilter) {
        filter.editorFilterUpdated(editorFilter);
    }

    /** Method invoked by the EditorComponent to attempt a filter update. */
    boolean attemptFilterUpdate(RowFilter editorFilter) {
        // return true if the filter is some table rows remain visible
        return filter.attemptEditorFilterUpdate(editorFilter);
    }

    /** Method invoked by the EditorComponent on request. */
    IParser createParser() {
        return getParserModel().createParser(this);
    }

    /** triggers the popup for an operation starting on the source component. */
    void triggerPopup(Object source) {
        if (!popup.isMenuCanceledForMouseEvent(source)) {
            editor.requestFocus();
            if (showChoices()) {
                popup.setPopupFocused(true);
            }
        }
    }

    /** Sets the content, updating the filter -and propagating any changes. */
    private void setEditorContent(Object content) {
        editor.setContent(content);
    }

    /** Request choices, if enabled, to the filtersHandler. */
    private void requestChoices() {
        if (isEnabled()) {
            filtersHandler.updateEditorChoices(this);
        }
    }

    private void formatOrComparatorUpdated() {
        ChoiceRenderer lcr = getRenderer();
        boolean updated;
        if (lcr == null) {
            updated = popup.setStringContent(format, choicesComparator, 
            		getStringComparator());
            editor.updateParser();
        } else {
        	//for rendered content, it is required having a non null comparator
            updated = popup.setRenderedContent(lcr, 
            		choicesComparator==null? comparator : choicesComparator,
            		getStringComparator());
        }
        if (updated) {
        	requestChoices();
        }
    }

    private IParserModel getParserModel() {
        return filtersHandler.getParserModel();
    }

    private Comparator<String> getStringComparator() {
        return getParserModel().getStringComparator(ignoreCase);
    }

    private void setupComponent(JComponent component) {
        component.addFocusListener(new FocusListener() {

                @Override public void focusLost(FocusEvent e) {
                    popup.hide();
                    filter.consolidateFilter();
                    // important: call focusMoved AFTER checking changes, to
                    // ensure that any changes on decoration (custom choice)
                    // are not lost
                    editor.focusMoved(false);
                    downButton.setFocused(false);
                }

                @Override public void focusGained(FocusEvent e) {
                    downButton.setFocused(true);
                    if (isEnabled()) {
                        editor.focusMoved(true);
                    }
                }
            });

        component.setFocusable(true);

        setupEnterKey(component);
        setupEscKey(component);
        setupHomeKey(component);
        setupHomeCtrlKey(component);
        setupEndKey(component);
        setupEndCtrlKey(component);
        setupUpKey(component);
        setupUpCtrlKey(component);
        setupUpPageKey(component);
        setupDownPageKey(component);
        setupDownKey(component);
        setupDownCtrlKey(component);
    }

    /** Method called when an element in the choices popup is selected. */
    void popupSelection(Object selection) {
        if (selection != null) {
            setEditorContent(selection);
            filter.consolidateFilter(); // equivalent to press enter
        }
    }

    /** Shows the popup menu, preselecting the best match. */
    boolean showChoices() {
        if (!popup.isVisible() && editor.isValid()) {
            if (!popup.display(editor)) {
                return false;
            }

            ChoiceMatch cm = popup.selectBestMatch(editor.getContent());
            if (cm.content == null) {
                // select ANYTHING
                popup.selectBestMatch("");
            }
        }

        return true;
    }

    // LISTENERS for KEY EVENTS

    /**
     * Change action for pressing enter key: on a popup, select the current item
     * and close it.<br>
     * Without popup, unselect any possible selection
     */
    private void setupEnterKey(JComponent component) {

        String actionName = "FCB_ENTER";
        Action action = new AbstractAction(actionName) {

            private static final long serialVersionUID = 6926912268574067920L;

            @Override public void actionPerformed(ActionEvent e) {
                if (popup.isPopupFocused()) {
                    popupSelection(popup.getSelection());
                } else {
                    // use update instead of checkChanges, in case it
                    // is needed to reset the icon of a CustomChoice
                    filter.consolidateFilter();
                }

                popup.hide();
            }
        };
        component.getActionMap().put(actionName, action);
        component.getInputMap(JComponent.WHEN_FOCUSED)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), actionName);
    }

    /**
     * Change action for pressing enter key: on a popup, hide it<br>
     * And unselect any possible selection
     */
    private void setupEscKey(JComponent component) {

        String actionName = "FCB_ESC";
        Action action = new AbstractAction(actionName) {

            private static final long serialVersionUID = -4351240441578952476L;

            @Override public void actionPerformed(ActionEvent e) {
                popup.hide();
                if (e.getSource() instanceof JTextField) {
                    JTextField textField = (JTextField) e.getSource();
                    textField.setCaretPosition(textField.getCaretPosition());
                }
            }
        };
        component.getActionMap().put(actionName, action);
        component.getInputMap(JComponent.WHEN_FOCUSED)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), actionName);
    }

    private void setupEndKey(JComponent component) {
        String actionName = "FCB_END";
        Action action = new AbstractAction(actionName) {

            private static final long serialVersionUID = -2777729244353281164L;

            @Override public void actionPerformed(ActionEvent e) {
                if (!popup.isPopupFocused() || !popup.selectLast(false)) {
                    if (e.getSource() instanceof JTextField) {
                        JTextField textField = (JTextField) e.getSource();
                        textField.setCaretPosition(textField.getText()
                                .length());
                    }
                }
            }
        };
        component.getActionMap().put(actionName, action);
        component.getInputMap(JComponent.WHEN_FOCUSED)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), actionName);
    }

    private void setupEndCtrlKey(JComponent component) {
        String actionName = "FCB_END_CTRL";
        Action action = new AbstractAction(actionName) {

            private static final long serialVersionUID = 1945871436968682881L;

            @Override public void actionPerformed(ActionEvent e) {

                // if focus is on the popup: select the very last item on the
                // popup, changing probably from the history list to the choices
                // list;  if the item is already on the very last element, or
                // the focus is on the text field, move the caret to the end
                if (!popup.isPopupFocused() || !popup.selectLast(true)) {
                    if (e.getSource() instanceof JTextField) {
                        JTextField textField = (JTextField) e.getSource();
                        textField.setCaretPosition(textField.getText()
                                .length());
                    }
                }
            }
        };
        component.getActionMap().put(actionName, action);
        component.getInputMap(JComponent.WHEN_FOCUSED)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_END, Event.CTRL_MASK),
                actionName);
    }

    private void setupHomeCtrlKey(JComponent component) {
        String actionName = "FCB_HOME_CTRL";
        Action action = new AbstractAction(actionName) {

            private static final long serialVersionUID = 3916227645612863334L;

            @Override public void actionPerformed(ActionEvent e) {

                // if focus is on the popup: select the very first item on the
                // popup, changing probably from the choices list to the history
                // list;  if the item is already on the very first element, or
                // the focus is on the text field, move the caret home
                if (!popup.isPopupFocused() || !popup.selectFirst(true)) {
                    if (e.getSource() instanceof JTextField) {
                        JTextField textField = (JTextField) e.getSource();
                        textField.setCaretPosition(0);
                    }
                }
            }
        };
        component.getActionMap().put(actionName, action);
        component.getInputMap(JComponent.WHEN_FOCUSED)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, Event.CTRL_MASK),
                actionName);
    }

    /**
     * If the focus is on the popup, Home moves the selected item to the first
     * in the list -if it is the first on the choices list jumps to the first on
     * the history list. Otherwise, just moves the caret position to the origin.
     * Exceptionally, if the focus is on the popup and the selected item is
     * already the very first shown, it also moves the caret position to the
     * origin.
     */
    private void setupHomeKey(JComponent component) {
        String actionName = "FCB_HOME";
        Action action = new AbstractAction(actionName) {

            private static final long serialVersionUID = -1583258893221830664L;

            @Override public void actionPerformed(ActionEvent e) {
                if (!popup.isPopupFocused() || !popup.selectFirst(false)) {
                    if (e.getSource() instanceof JTextField) {
                        JTextField textField = (JTextField) e.getSource();
                        textField.setCaretPosition(0);
                    }
                }
            }
        };
        component.getActionMap().put(actionName, action);
        component.getInputMap(JComponent.WHEN_FOCUSED)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), actionName);
    }

    private void setupDownPageKey(JComponent component) {
        String actionName = "FCB_PAGE_DOWN";
        Action action = new AbstractAction(actionName) {

            private static final long serialVersionUID = -1187830005921916553L;

            @Override public void actionPerformed(ActionEvent e) {

                // without moving the focus, move down one page on the popup
                // menu, probably jumping to choices list
                if (popup.isVisible()) {
                    boolean focusPopup = popup.isPopupFocused();
                    popup.selectDownPage();
                    popup.setPopupFocused(focusPopup);
                } else {
                    showChoices();
                }
            }
        };
        component.getActionMap().put(actionName, action);
        component.getInputMap(JComponent.WHEN_FOCUSED)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), actionName);
    }

    private void setupUpPageKey(JComponent component) {
        String actionName = "FCB_PAGE_UP";
        Action action = new AbstractAction(actionName) {

            private static final long serialVersionUID = 6590487133211390977L;

            @Override public void actionPerformed(ActionEvent e) {

                // without moving the focus, move up one page on the popup menu,
                // probably jumping to history list
                if (popup.isVisible()) {
                    boolean focusPopup = popup.isPopupFocused();
                    popup.selectUpPage();
                    popup.setPopupFocused(focusPopup);
                }
            }
        };
        component.getActionMap().put(actionName, action);
        component.getInputMap(JComponent.WHEN_FOCUSED)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), actionName);
    }

    private void setupUpCtrlKey(JComponent component) {
        String actionName = "FCB_UP_CTRL";
        Action action = new AbstractAction(actionName) {

            private static final long serialVersionUID = 746565926592574009L;

            @Override public void actionPerformed(ActionEvent e) {
                // if focus is on the popup: move from choices to history, and,
                // being already on history, up to text field.
                if (popup.isPopupFocused()) {
                    if (!popup.selectUp(true)) {
                        popup.setPopupFocused(false);
                    }
                }
            }
        };
        component.getActionMap().put(actionName, action);
        component.getInputMap(JComponent.WHEN_FOCUSED)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, Event.CTRL_MASK),
                actionName);
    }

    private void setupUpKey(JComponent component) {
        String actionName = "FCB_UP";
        Action action = new AbstractAction(actionName) {

            private static final long serialVersionUID = 4555560696351340571L;

            @Override public void actionPerformed(ActionEvent e) {
                // if popup is not visible, just make it visible.
                // if popup has not the focus, pass it the focus
                // else: move up!
                if (popup.isVisible()) {
                    if (popup.isPopupFocused()) {
                        popup.selectUp(false);
                    } else {
                        popup.setPopupFocused(true);
                        // if still not focused, it has empty content
                        if (!popup.isPopupFocused()) {
                            popup.hide();
                        }
                    }
                } else {
                    showChoices();
                }
            }
        };
        component.getActionMap().put(actionName, action);
        component.getInputMap(JComponent.WHEN_FOCUSED)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), actionName);
    }

    private void setupDownCtrlKey(JComponent component) {
        String actionName = "FCB_DOWN_CTRL";
        Action action = new AbstractAction(actionName) {

            private static final long serialVersionUID = -8075976293862885060L;

            @Override public void actionPerformed(ActionEvent e) {
                // if popup is not visible, make it visible
                // if popup has not the focus, pass it the focus
                // else: move to the first visible element in the choices
                if (popup.isVisible()) {
                    if (popup.isPopupFocused()) {
                        popup.selectDown(true);
                    } else {
                        popup.setPopupFocused(true);
                    }
                } else {
                    showChoices();
                }
            }
        };
        component.getActionMap().put(actionName, action);
        component.getInputMap(JComponent.WHEN_FOCUSED)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, Event.CTRL_MASK),
                actionName);
    }

    private void setupDownKey(JComponent component) {
        String actionName = "FCB_DOWN";
        Action action = new AbstractAction(actionName) {

            private static final long serialVersionUID = -4133513199725709434L;

            @Override public void actionPerformed(ActionEvent e) {
                // if popup is not visible, just make it visible.
                // if popup has not the focus, pass it the focus
                // else: move down!
                if (popup.isVisible()) {
                    if (popup.isPopupFocused()) {
                        popup.selectDown(false);
                    } else {
                        popup.setPopupFocused(true);
                    }
                } else {
                    showChoices();
                }
            }
        };
        component.getActionMap().put(actionName, action);
        component.getInputMap(JComponent.WHEN_FOCUSED)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), actionName);
    }

    /**
     * Wrapper of the filter associated to the {@link EditorComponent}, ensuring
     * some added functionality (like auto-adding to the history list when the
     * filter changes).
     */
    final class EditorFilter extends Filter {
        RowFilter delegate;
        boolean toBeConsolidated;
        boolean reportOnConsolidation;

        @Override public boolean include(RowFilter.Entry entry) {
            return (delegate == null) ? true : delegate.include(entry);
        }

        @Override public void setEnabled(boolean enable) {
            if (enable != isEnabled()) {
                setFilterEnabled(enable);
                delegate = enable ? editor.getFilter() : null;
                super.setEnabled(enable);
            }
        }

        /** Reports an update on the associated filter. */
        public void editorFilterUpdated(RowFilter filter) {
            if (isEnabled()) {
                if (filter != delegate) {
                    delegate = filter;
                    reportFilterUpdatedToObservers();
                    reportOnConsolidation = false;
                    if (editor.isFocused()) {
                        toBeConsolidated = true;
                    } else {
                        filtersHandler.consolidateFilterChanges(modelIndex);
                    }
                }
            }
        }

        /**
         * Attempts an update on a editor filter. It is only performed if the
         * filter let pass rows (does not filter all out)
         */
        public boolean attemptEditorFilterUpdate(RowFilter filter) {
            delegate = filter;

            boolean ret = filtersHandler.applyEditorFilter(this);
            if (ret) {
                toBeConsolidated = true;
                reportOnConsolidation = false;
            } else {
                reportOnConsolidation = true;
            }

            return ret;
        }

        public void consolidateFilter() {
            if (isEnabled()) {
                // next call can imply a call to editorFilterUpdated if
                // there are changes on the filter
                editor.consolidateFilter();
                if (reportOnConsolidation) {
                    reportOnConsolidation = false;
                    reportFilterUpdatedToObservers();
                }

                if (toBeConsolidated) {
                    toBeConsolidated = false;

                    boolean warning = filtersHandler.consolidateFilterChanges(
                            modelIndex);
                    editor.setWarning(warning);

                    // only add valid content creating filters that do not
                    // filter all the content out
                    if (editor.isValidContent() && !warning) {
                        popup.addHistory(editor.getContent());
                    }
                }
            }
        }
    }

    /**
     * Implementation of the {@link Border} associated to each filter editor.
     */
    final static class EditorBorder implements Border {

        Color color;

        @Override public void paintBorder(Component c,
                                          Graphics  g,
                                          int       x,
                                          int       y,
                                          int       width,
                                          int       height) {
            g.setColor(color);
            g.drawLine(0, height - 1, width - 1, height - 1);
            g.drawLine(width - 1, 0, width - 1, height - 1);
        }

        @Override public boolean isBorderOpaque() {
            return true;
        }

        @Override public Insets getBorderInsets(Component c) {
            return new Insets(0, 1, 1, 1);
        }
    }
}
