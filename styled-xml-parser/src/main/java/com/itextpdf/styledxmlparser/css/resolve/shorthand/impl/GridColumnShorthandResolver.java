package com.itextpdf.styledxmlparser.css.resolve.shorthand.impl;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;

/**
 * {@link IShorthandResolver} implementation for grid-column shorthand.
 */
public class GridColumnShorthandResolver extends GridItemShorthandResolver {
    /**
     * Creates a shorthand resolver for grid-column property
     */
    public GridColumnShorthandResolver() {
        super(CommonCssConstants.GRID_COLUMN);
    }
}
