package com.itextpdf.commons.utils;

/**
 * Experimental features class which contains constants related to experimental form fields drawing.
 */
// TODO Shall be removed in the scope of DEVSIX-7385
public final class ExperimentalFeatures {
    /**
     * Determines, whether the old or the new checkbox form field drawing logic will be used.
     */
    public static boolean ENABLE_EXPERIMENTAL_CHECKBOX_RENDERING = false;

    /**
     * Determines, whether the old or the new text form field drawing logic will be used.
     */
    public static boolean ENABLE_EXPERIMENTAL_TEXT_FORM_RENDERING = false;

    private ExperimentalFeatures() {
        // utility class
    }

}
