package com.itextpdf.svg.css;

/**
 * Context necessary for evaluating certain Css statements whose final values depends on other statements
 * e.g. relative font-size statements.
 */
public class CssContext {

    private float rootFontSize;

    public float getRootFontSize() {
        return rootFontSize;
    }
}
