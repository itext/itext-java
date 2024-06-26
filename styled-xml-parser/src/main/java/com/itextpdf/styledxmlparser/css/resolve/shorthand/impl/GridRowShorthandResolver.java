package com.itextpdf.styledxmlparser.css.resolve.shorthand.impl;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;

/**
 * {@link IShorthandResolver} implementation for grid-row shorthand.
 */
public class GridRowShorthandResolver extends GridItemShorthandResolver {
    /**
     * Creates a shorthand resolver for grid-row property
     */
    public GridRowShorthandResolver() {
        super(CommonCssConstants.GRID_ROW);
    }
}
