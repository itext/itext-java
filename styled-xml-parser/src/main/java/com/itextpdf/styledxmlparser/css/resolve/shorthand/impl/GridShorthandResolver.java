package com.itextpdf.styledxmlparser.css.resolve.shorthand.impl;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link IShorthandResolver} implementation for grid shorthand.
 */
public class GridShorthandResolver implements IShorthandResolver {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssDeclaration> resolveShorthand(String shorthandExpression) {
        if (!shorthandExpression.contains(CommonCssConstants.AUTO_FLOW)) {
            return new GridTemplateShorthandResolver().resolveShorthand(shorthandExpression);
        }
        String[] values = shorthandExpression.trim().split("/");
        List<CssDeclaration> result = new ArrayList<>();
        if (values[0].contains(CommonCssConstants.AUTO_FLOW)) {
            if (values[0].contains(CommonCssConstants.DENSE)) {
                result.add(new CssDeclaration(CommonCssConstants.GRID_AUTO_FLOW, CommonCssConstants.DENSE));
            }
            String rowsTemplate =
                    values[0].substring(
                            Math.max(values[0].indexOf(CommonCssConstants.AUTO_FLOW) + CommonCssConstants.AUTO_FLOW.length(),
                                    values[0].indexOf(CommonCssConstants.DENSE) + CommonCssConstants.DENSE.length())
                    );
            if (!rowsTemplate.trim().isEmpty()) {
                result.add(new CssDeclaration(CommonCssConstants.GRID_AUTO_ROWS, rowsTemplate));
            }
            if (values.length == 2) {
                result.add(new CssDeclaration(CommonCssConstants.GRID_TEMPLATE_COLUMNS, values[1]));
            }
        } else if (values.length == 2) {
            result.add(new CssDeclaration(CommonCssConstants.GRID_TEMPLATE_ROWS, values[0]));
            if (values[1].contains(CommonCssConstants.DENSE)) {
                result.add(new CssDeclaration(CommonCssConstants.GRID_AUTO_FLOW, CommonCssConstants.COLUMN + " " + CommonCssConstants.DENSE));
            }
            String columnsTemplate =
                    values[1].substring(
                            Math.max(values[1].indexOf(CommonCssConstants.AUTO_FLOW) + CommonCssConstants.AUTO_FLOW.length(),
                                    values[1].indexOf(CommonCssConstants.DENSE) + CommonCssConstants.DENSE.length())
                    );
            if (!columnsTemplate.trim().isEmpty()) {
                result.add(new CssDeclaration(CommonCssConstants.GRID_AUTO_COLUMNS, columnsTemplate));
            }
        }
        return result;
    }
}
