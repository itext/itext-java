/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.nodes;

/**
 * A boolean attribute that is written out without any value.
 */
public class BooleanAttribute extends Attribute {
    /**
     * Create a new boolean attribute from unencoded (raw) key.
     * @param key attribute key
     */
    public BooleanAttribute(String key) {
        super(key, "");
    }

    @Override
    protected boolean isBooleanAttribute() {
        return true;
    }
}
