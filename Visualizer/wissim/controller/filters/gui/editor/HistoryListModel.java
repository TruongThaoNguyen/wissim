package wissim.controller.filters.gui.editor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractListModel;

class HistoryListModel extends AbstractListModel {

	private static final long serialVersionUID = -374115548677017807L;
    private List<Object> history = new ArrayList<Object>();
    private List<Object> shownHistory = history;
    private Object lastAdded;
    private Comparator stringComparator;
    private int maxHistory;

    /**  Initializes the model to a specific history content */
    public void initialize(List<Object> history){
        this.lastAdded = null;
        this.shownHistory = history;
        this.history.clear();
        if (!history.isEmpty()){
        	this.history.addAll(history.subList
        			(0, Math.min(maxHistory, history.size())));
        }
    }
    
    /** Returns the items currently visible */
    public List<Object> getShownHistory(){
    	return new ArrayList<Object>(shownHistory);
    }

    /**
     * Specifies how to handle the content. If there is a string comparator,
     * content is handled as strings and it is possible to look for best
     * matches; otherwise, it is treated as abstract objects, matching is done
     * by identity.
     */
    public void setStringContent(Comparator<String> stringComparator) {
        if (this.stringComparator != stringComparator) {
            this.stringComparator = stringComparator;
            clear();
        }
    }

    /** Clears any restrictions. {@link #restrict(Object)} */
    public int clearRestrictions() {
        shownHistory = history;

        return shownHistory.size();
    }

    /** Restricts the elements from the history -without removing it. */
    public boolean restrict(Object exclude) {
        int index = shownHistory.indexOf(exclude);
        if (index != -1) {
            if (shownHistory == history) {
                shownHistory = new ArrayList<Object>(history);
            }

            shownHistory.remove(index);
            fireIntervalAdded(this, index, index);
            return true;
        }

        return false;
    }

    @Override public Object getElementAt(int index) {
        return shownHistory.get(index);
    }

    /** Adds an element, Returning true if the number of elements changes. */
    public boolean add(Object st) {
        // never add the passed element (which is now selected on the
        // editor). We will add it when the next element is passed
        boolean ret = false;
        boolean removed = history.remove(st);
        if ((maxHistory > 0) && (lastAdded != null)
                && (lastAdded.toString().length() > 0)
                && !lastAdded.equals(st)) {
            history.add(0, lastAdded);

            int size = history.size();
            if (size > maxHistory) {
                history.remove(--size);
                removed = true;
            } else {
                ret = true;
                if (!removed) {
                    fireIntervalAdded(this, 0, 0);
                }
            }
        }

        if (removed) {
            fireContentsChanged(this, 0, history.size());
            ret = true;
        }

        lastAdded = st;
        shownHistory = history;

        return ret;
    }

    public boolean isEmpty() {
        return shownHistory.isEmpty();
    }

    public void clear() {
        int size = history.size();
        if (size > 0) {
            history.clear();
            shownHistory = history;
            fireIntervalRemoved(this, 0, size);
        }

        lastAdded = null;
    }

    @Override public int getSize() {
        return shownHistory.size();
    }

    /** Sets the max history size. */
    public void setMaxHistory(int size) {
        maxHistory = size;

        int current = history.size();
        if (current > size) {
            for (int i = current - 1; i >= size; i--) {
                history.remove(i);
            }

            shownHistory = history;
            fireContentsChanged(this, maxHistory, current);
        }
    }

    /** Returns the max history. */
    public int getMaxHistory() {
        return maxHistory;
    }

    /** Returns the history as a list. */
    public List getList() {
        return history;
    }

    /** @see  PopupComponent#selectBestMatch(Object) */
    public ChoiceMatch getClosestMatch(Object hint) {
        ChoiceMatch ret = new ChoiceMatch();
        if ((stringComparator != null) && (hint instanceof String)) {
        	String strStart = (String) hint;        	
            int strLen = strStart.length();
            int historyLen = shownHistory.size();
            while (historyLen-- > 0) {
                Object content = shownHistory.get(historyLen);
                String str = content.toString();
                int len = ChoiceMatch.getMatchingLength(strStart, str, 
                		stringComparator);
                if (((len > 0) && (len >= ret.len)) || (ret.len == 0)) {
                	ret.content = content;
                    ret.index = historyLen;
                    ret.len = len;
                    if ((len == strLen) && (str.length() == strLen)) {
                        ret.exact = true;
                        return ret;
                    }
                }
            }
        } else {
        	ret.index = shownHistory.indexOf(hint);
            if (ret.index != -1) {
                ret.exact = true;
                ret.content = hint;
            }
        }
        return ret;
    }
}
