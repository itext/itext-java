package com.itextpdf.styledxmlparser.css.resolve.shorthand.impl;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TextDecorationShorthandResolver implements IShorthandResolver {

    private static final Set<String> TEXT_DECORATION_LINE_VALUES = new HashSet<>(Arrays.asList(
            CommonCssConstants.UNDERLINE, CommonCssConstants.OVERLINE, CommonCssConstants.LINE_THROUGH,
            CommonCssConstants.BLINK
    ));

    private static final Set<String> TEXT_DECORATION_STYLE_VALUES = new HashSet<>(Arrays.asList(
            CommonCssConstants.SOLID, CommonCssConstants.DOUBLE, CommonCssConstants.DOTTED,
            CommonCssConstants.DASHED, CommonCssConstants.WAVY
    ));

    @Override
    public List<CssDeclaration> resolveShorthand(String shorthandExpression) {
        if (CommonCssConstants.INITIAL.equals(shorthandExpression) || CommonCssConstants.INHERIT
                .equals(shorthandExpression)) {
            return Arrays.asList(
                    new CssDeclaration(CommonCssConstants.TEXT_DECORATION_LINE, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.TEXT_DECORATION_STYLE, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.TEXT_DECORATION_COLOR, shorthandExpression));
        }

        //regexp for separating line by spaces that are not inside the parentheses, so rgb()
        // and hsl() color declarations are parsed correctly
        String[] props = shorthandExpression.split("\\s+(?![^\\(]*\\))");

        List<String> textDecorationLineValues = new ArrayList<>();
        String textDecorationStyleValue = null;
        String textDecorationColorValue = null;

        for (String value : props) {
            //For text-decoration-line attributes several attributes may be present at once.
            //However when "none" attribute is present, all the other attributes become invalid
            if (TEXT_DECORATION_LINE_VALUES.contains(value)
                    || CommonCssConstants.NONE.equals(value)) {
                textDecorationLineValues.add(value);
            } else if (TEXT_DECORATION_STYLE_VALUES.contains(value)) {
                textDecorationStyleValue = value;
            } else if (!value.isEmpty()) {
                textDecorationColorValue = value;
            }
        }

        List<CssDeclaration> resolvedDecl = new ArrayList<>();
        if (textDecorationLineValues.isEmpty()) {
            resolvedDecl.add(new CssDeclaration(CommonCssConstants.TEXT_DECORATION_LINE, CommonCssConstants.INITIAL));
        } else {
            StringBuilder resultLine = new StringBuilder();
            for (String line : textDecorationLineValues) {
                resultLine.append(line).append(" ");
            }
            resolvedDecl.add(new CssDeclaration(CommonCssConstants.TEXT_DECORATION_LINE, resultLine.toString().trim()));
        }

        resolvedDecl.add(new CssDeclaration(CommonCssConstants.TEXT_DECORATION_STYLE,
                textDecorationStyleValue == null ? CommonCssConstants.INITIAL : textDecorationStyleValue));
        resolvedDecl.add(new CssDeclaration(CommonCssConstants.TEXT_DECORATION_COLOR,
                textDecorationColorValue == null ? CommonCssConstants.INITIAL : textDecorationColorValue));
        return resolvedDecl;
    }
}
