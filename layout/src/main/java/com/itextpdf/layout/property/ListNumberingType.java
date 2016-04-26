package com.itextpdf.layout.property;

import com.itextpdf.layout.IPropertyContainer;

/**
 * A specialized enum holding the possible values for a list {@link
 * com.itextpdf.layout.element.List}'s entry prefix. This class is meant to
 * be used as the value for the {@link Property#LIST_SYMBOL} key in an
 * {@link IPropertyContainer}.
 */
public enum ListNumberingType {
    DECIMAL,
    ROMAN_LOWER,
    ROMAN_UPPER,
    ENGLISH_LOWER,
    ENGLISH_UPPER,
    GREEK_LOWER,
    GREEK_UPPER,
    /** Zapfdingbats font characters in range [172; 181] */
    ZAPF_DINGBATS_1,
    /** Zapfdingbats font characters in range [182; 191] */
    ZAPF_DINGBATS_2,
    /** Zapfdingbats font characters in range [192; 201] */
    ZAPF_DINGBATS_3,
    /** Zapfdingbats font characters in range [202; 221] */
    ZAPF_DINGBATS_4
}
