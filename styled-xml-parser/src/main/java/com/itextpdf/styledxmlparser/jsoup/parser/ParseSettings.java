/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.parser;

import com.itextpdf.styledxmlparser.jsoup.internal.Normalizer;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attributes;

/**
 * Controls parser settings, to optionally preserve tag and/or attribute name case.
 */
public class ParseSettings {
    /**
     * HTML default settings: both tag and attribute names are lower-cased during parsing.
     */
    public static final ParseSettings htmlDefault;
    /**
     * Preserve both tag and attribute case.
     */
    public static final ParseSettings preserveCase;

    static {
        htmlDefault = new ParseSettings(false, false);
        preserveCase = new ParseSettings(true, true);
    }

    private final boolean preserveTagCase;
    private final boolean preserveAttributeCase;

    /**
     * Returns true if preserving tag name case.
     */
    public boolean preserveTagCase() {
        return preserveTagCase;
    }

    /**
     * Returns true if preserving attribute case.
     */
    public boolean preserveAttributeCase() {
        return preserveAttributeCase;
    }

    /**
     * Define parse settings.
     * @param tag preserve tag case?
     * @param attribute preserve attribute name case?
     */
    public ParseSettings(boolean tag, boolean attribute) {
        preserveTagCase = tag;
        preserveAttributeCase = attribute;
    }

    ParseSettings(ParseSettings copy) {
        this(copy.preserveTagCase, copy.preserveAttributeCase);
    }

    /**
     * Normalizes a tag name according to the case preservation setting.
     */
    public String normalizeTag(String name) {
        name = name.trim();
        if (!preserveTagCase)
            name = Normalizer.lowerCase(name);
        return name;
    }

    /**
     * Normalizes an attribute according to the case preservation setting.
     */
    public String normalizeAttribute(String name) {
        name = name.trim();
        if (!preserveAttributeCase)
            name = Normalizer.lowerCase(name);
        return name;
    }

    Attributes normalizeAttributes(Attributes attributes) {
        if (attributes != null && !preserveAttributeCase) {
            attributes.normalize();
        }
        return attributes;
    }
}
