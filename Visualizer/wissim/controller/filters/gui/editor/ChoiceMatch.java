package wissim.controller.filters.gui.editor;

import java.util.Comparator;

class ChoiceMatch {
	// exact is true if the given index corresponds to an string that fully
    // matches the passed string
    boolean exact;
    // the matched content
    Object content;
    // index in the list. Will be -1 if the content is empty or a fullMatch
    // is required and cannot be found
    int index = -1;
    // length of the string that is matched, if exact is false.
    int len;

    /** Returns the number of matching characters between two strings. */
    public static int getMatchingLength(String     a,
                                        String     b,
                                        Comparator stringComparator) {
        int max = Math.min(a.length(), b.length());
        for (int i = 0; i < max; i++) {
            char f = a.charAt(i);
            char s = b.charAt(i);
            if ((f != s)
                    && (stringComparator.compare(String.valueOf(f),
                            String.valueOf(s)) != 0)) {
                return i;
            }
        }

        return max;
    }

}
