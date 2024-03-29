package wissim.controller.filters.gui;

import java.beans.PropertyChangeListener;
import java.text.Format;
import java.util.Comparator;

import wissim.controller.filters.IParser;


public interface IParserModel {
	/** Property fired when the ignore case value changes. */
    String IGNORE_CASE_PROPERTY = "ignoreCase";

    /** Property fired when any class' comparator changes. */
    String COMPARATOR_PROPERTY = "comparator";

    /** Property fired when any class' format changes. */
    String FORMAT_PROPERTY = "format";

    /** Creates a text parser for the given editor. */
    IParser createParser(IFilterEditor editor);

    /** Returns the {@link Format} for the given class. */
    Format getFormat(Class c);

    /** Defines the {@link Format} for the given class. */
    void setFormat(Class c, Format format);

    /**
     * Returns the {@link Comparator} for the given class.<br>
     * It never returns null.
     */
    Comparator getComparator(Class c);

    /** Defines the {@link Comparator} for the given class. */
    void setComparator(Class c, Comparator format);

    /** Returns the {@link Comparator} used for String comparisons. */
    Comparator<String> getStringComparator(boolean ignoreCase);

    /** Sets a String comparator that is case sensitive/insensitive. */
    void setIgnoreCase(boolean set);

    /**
     * Returns true if the String comparator ignores case<br>
     * Note that this is redundant information, which can be retrieved from the
     * {@link #getComparator(Class)} method with a String.class parameter.
     */
    boolean isIgnoreCase();

    /**
     * Adds a {@link PropertyChangeListener}.<br>
     * Any property change will be transmitted as an event
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /** Removes an existing {@link PropertyChangeListener}. */
    void removePropertyChangeListener(PropertyChangeListener listener);

}
