/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.parser;

import java.util.ArrayList;

/**
 * A container for ParseErrors.
 * 
 * @author Jonathan Hedley
 */
class ParseErrorList extends ArrayList<ParseError>{
    private static final int INITIAL_CAPACITY = 16;
    private final int maxSize;
    
    ParseErrorList(int initialCapacity, int maxSize) {
        super(initialCapacity);
        this.maxSize = maxSize;
    }
    
    boolean canAddError() {
        return size() < maxSize;
    }

    int getMaxSize() {
        return maxSize;
    }

    static ParseErrorList noTracking() {
        return new ParseErrorList(0, 0);
    }
    
    static ParseErrorList tracking(int maxSize) {
        return new ParseErrorList(INITIAL_CAPACITY, maxSize);
    }
}
