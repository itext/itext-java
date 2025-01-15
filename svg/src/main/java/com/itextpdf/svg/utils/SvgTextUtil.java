/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.svg.utils;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.layout.properties.Underline;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.TextDecorationShorthandResolver;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.styledxmlparser.util.WhiteSpaceUtil;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.renderers.impl.ISvgTextNodeRenderer;
import com.itextpdf.svg.renderers.impl.TextLeafSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.TextSvgBranchRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Class containing utility methods for text operations in the context of SVG processing
 */
public final class SvgTextUtil {
    private static final float TEXT_DECORATION_LINE_THROUGH_Y_POS = -3 / 10F;
    private static final float TEXT_DECORATION_LINE_OVER_Y_POS = -9 / 10F;
    private static final float TEXT_DECORATION_LINE_UNDER_Y_POS = 1 / 10F;

    private SvgTextUtil() {
    }

    /**
     * Trim all the leading whitespace characters from the passed string
     *
     * @param toTrim string to trim
     * @return string with all leading whitespace characters removed
     */
    public static String trimLeadingWhitespace(String toTrim) {
        // TODO DEVSIX-8808 make this method private static, because it is used only in the scope of that class
        if (toTrim == null) {
            return "";
        }
        int current = 0;
        int end = toTrim.length();
        while (current < end) {
            char currentChar = toTrim.charAt(current);
            if (Character.isWhitespace(currentChar) && !(currentChar == '\n' || currentChar == '\r')) {
                // If the character is whitespace and not a newline, increase current.
                current++;
            } else {
                break;
            }
        }
        return toTrim.substring(current);
    }

    /**
     * Trim all the trailing whitespace characters from the passed string
     *
     * @param toTrim string to trim
     * @return string with al trailing whitespace characters removed
     */
    public static String trimTrailingWhitespace(String toTrim) {
        // TODO DEVSIX-8808 make this method private static, because it is used only in the scope of that class
        if (toTrim == null) {
            return "";
        }
        int end = toTrim.length();
        if (end > 0) {
            int current = end - 1;
            while (current >= 0) {
                char currentChar = toTrim.charAt(current);
                if (Character.isWhitespace(currentChar) && !(currentChar == '\n' || currentChar == '\r')) {
                    // If the character is whitespace and not a newline, increase current.
                    current--;
                } else {
                    break;
                }
            }
            if (current < 0) {
                return "";
            } else {
                return toTrim.substring(0, current + 1);
            }
        } else {
            return toTrim;
        }
    }


    /**
     * Process the whitespace inside the Text Tree.
     * Whitespace is collapsed and new lines are handled
     * A leading element in each subtree is handled different: the preceding whitespace is trimmed instead of kept
     *
     * @param root             root of the text-renderer subtree
     * @param isLeadingElement true if this element is a leading element(either the first child or the first element after an absolute position change)
     */
    public static void processWhiteSpace(TextSvgBranchRenderer root, boolean isLeadingElement) {
        // When svg is parsed by jsoup it leaves all whitespace in text element as is. Meaning that
        // tab/space indented xml files will retain their tabs and spaces.
        // The following regex replaces all whitespace with a single space.
        String whiteSpace = root.getAttributeMapCopy().isEmpty() ? CommonCssConstants.NORMAL :
                root.getAttribute(CommonCssConstants.WHITE_SPACE);
        if (whiteSpace == null) {
            // XML_SPACE is 'default' or 'preserve'.
            whiteSpace = root.getAttribute(SvgConstants.Attributes.XML_SPACE);
            if ("preserve".equals(whiteSpace)) {
                whiteSpace = CommonCssConstants.PRE;
            } else {
                whiteSpace = CommonCssConstants.NORMAL;
            }
        }
        boolean keepLineBreaks = CommonCssConstants.PRE.equals(whiteSpace) ||
                CommonCssConstants.PRE_WRAP.equals(whiteSpace) || CommonCssConstants.PRE_LINE.equals(whiteSpace)
                || CommonCssConstants.BREAK_SPACES.equals(whiteSpace);
        boolean collapseSpaces = !(CommonCssConstants.PRE.equals(whiteSpace) ||
                CommonCssConstants.PRE_WRAP.equals(whiteSpace) || CommonCssConstants.BREAK_SPACES.equals(whiteSpace));
        for (ISvgTextNodeRenderer child : root.getChildren()) {
            // If child is leaf, process contents, if it is branch, call function again.
            if (child instanceof TextSvgBranchRenderer) {
                // Branch processing.
                processWhiteSpace((TextSvgBranchRenderer) child,
                        child.containsAbsolutePositionChange() || isLeadingElement);
                ((TextSvgBranchRenderer) child).markWhiteSpaceProcessed();
                isLeadingElement = false;
            }
            if (child instanceof TextLeafSvgNodeRenderer) {
                // Leaf processing.
                TextLeafSvgNodeRenderer leafRend = (TextLeafSvgNodeRenderer) child;
                // Process text.
                String toProcess = leafRend.getAttribute(SvgConstants.Attributes.TEXT_CONTENT);
                // For now, text element contains single line and no-wrapping by default.
                toProcess = toProcess.replace("\n", "");
                toProcess = WhiteSpaceUtil.processWhitespaces(toProcess, keepLineBreaks, collapseSpaces);
                if (!keepLineBreaks) {
                    if (isLeadingElement) {
                        // Trim leading and trailing whitespaces.
                        toProcess = trimLeadingWhitespace(toProcess);
                        toProcess = trimTrailingWhitespace(toProcess);
                        isLeadingElement = false;
                    } else {
                        // Only trim trailing whitespaces.
                        toProcess = trimTrailingWhitespace(toProcess);
                    }
                }
                leafRend.setAttribute(SvgConstants.Attributes.TEXT_CONTENT, toProcess);
            }
        }
    }

    /**
     * Check if the String is only composed of whitespace characters
     *
     * @param s string to check
     * @return true if the string only contains whitespace characters, false otherwise
     */
    public static boolean isOnlyWhiteSpace(String s) {
        String trimmedText = s.replaceAll("\\s+", " ");
        // Trim leading whitespace.
        trimmedText = SvgTextUtil.trimLeadingWhitespace(trimmedText);
        // Trim trailing whitespace.
        trimmedText = SvgTextUtil.trimTrailingWhitespace(trimmedText);
        return trimmedText.isEmpty();
    }

    /**
     * Resolve the font size stored inside the passed renderer
     *
     * @param renderer       renderer containing the font size declaration
     * @param parentFontSize parent font size to fall back on if the renderer does not contain a font size declarations or if the stored declaration is invalid
     *
     * @return float containing the font-size, or the parent font size if the renderer's declaration cannot be resolved
     *
     * @deprecated will be removed together with {@link TextLeafSvgNodeRenderer#getTextContentLength(float, PdfFont)}
     */
    @Deprecated
    public static float resolveFontSize(ISvgTextNodeRenderer renderer, float parentFontSize) {
        //Use own font-size declaration if it is present, parent's otherwise
        float fontSize = Float.NaN;
        final String elementFontSize = renderer.getAttribute(SvgConstants.Attributes.FONT_SIZE);
        if (null != elementFontSize && !elementFontSize.isEmpty()) {
            if (CssTypesValidationUtils.isRelativeValue(elementFontSize)
                    || CommonCssConstants.LARGER.equals(elementFontSize)
                    || CommonCssConstants.SMALLER.equals(elementFontSize)) {
                fontSize = CssDimensionParsingUtils.parseRelativeFontSize(elementFontSize, parentFontSize);
            } else {
                fontSize = CssDimensionParsingUtils.parseAbsoluteFontSize(elementFontSize, CommonCssConstants.PX);
            }
        }
        if ((Float.isNaN(fontSize)) || fontSize < 0f) {
            fontSize = parentFontSize;
        }
        return fontSize;
    }

    /**
     * The reference value may contain a hashtag character or 'url' designation and this method will filter them.
     *
     * @param name value to be filtered
     * @return filtered value
     */
    public static String filterReferenceValue(String name) {
        return name.replace("#", "").replace("url(", "").replace(")", "").trim();
    }

    /**
     * Processes text-decoration attribute which is shorthand for text-decoration-line and text-decoration-style.
     *
     * <p>
     * Note, that text-decoration from styles (resolved in SvgStyleResolver) takes precedence when both are specified.
     *
     * @param renderer to apply text-decoration for
     * @param doFill boolean specifying whether text should be filled
     * @param doStroke boolean specifying whether text should be stroked
     * @param context current SVG draw context
     */
    public static void applyTextDecoration(ISvgTextNodeRenderer renderer, boolean doFill, boolean doStroke,
                                           SvgDrawContext context) {
        if (renderer.getAttributeMapCopy().isEmpty()) {
            return;
        }
        String textDecoration = renderer.getAttribute(CommonCssConstants.TEXT_DECORATION);
        if (textDecoration != null) {
            List<CssDeclaration> resolvedShorthandProps =
                    new TextDecorationShorthandResolver().resolveShorthand(textDecoration);
            for (CssDeclaration cssDeclaration : resolvedShorthandProps) {
                if (renderer.getAttribute(cssDeclaration.getProperty()) == null) {
                    renderer.setAttribute(cssDeclaration.getProperty(), cssDeclaration.getExpression());
                }
            }
        }

        final String textDecorationLineProp = renderer.getAttribute(CommonCssConstants.TEXT_DECORATION_LINE);
        if (textDecorationLineProp == null) {
            return;
        }
        // TODO DEVSIX-4063 Support text-decoration-style property
        final String[] textDecorationArray = textDecorationLineProp.split("\\s+");
        List<Float> baselineList = new ArrayList<>();
        for (Underline underline : context.getSvgTextProperties().getTextDecoration()) {
            baselineList.add(underline.getYPositionMul());
        }
        for (final String line : textDecorationArray) {
            float textDecorationLine;
            switch (line) {
                case CommonCssConstants.LINE_THROUGH:
                    textDecorationLine = TEXT_DECORATION_LINE_THROUGH_Y_POS;
                    break;
                case CommonCssConstants.OVERLINE:
                    textDecorationLine = TEXT_DECORATION_LINE_OVER_Y_POS;
                    break;
                case CommonCssConstants.UNDERLINE:
                    textDecorationLine = TEXT_DECORATION_LINE_UNDER_Y_POS;
                    break;
                case CommonCssConstants.NONE:
                default:
                    return;
            }
            baselineList.add(textDecorationLine);
        }

        // SVG2 spec, 11.11. Text decoration: the fill and stroke of the text decoration are given by the fill and
        // stroke of the text at the point where the text decoration is declared.
        Color fillColor = doFill ? context.getSvgTextProperties().getFillColor() : null;
        float fillOpacity = context.getSvgTextProperties().getFillOpacity();
        Color strokeColor = doStroke ? context.getSvgTextProperties().getStrokeColor() : null;
        float strokeOpacity = context.getSvgTextProperties().getStrokeOpacity();

        List<Underline> underlineList = new ArrayList<>();
        for (float textDecorationLine : baselineList) {
            addUnderline(context, underlineList, strokeColor, strokeOpacity, fillColor, fillOpacity,
                    textDecorationLine, doFill, doStroke);
        }
        context.getSvgTextProperties().setTextDecoration(underlineList);
    }

    private static void addUnderline(SvgDrawContext context, List<Underline> underlineList, Color strokeColor,
                                     float strokeOpacity, Color fillColor, float fillOpacity, float textDecorationLine,
                                     boolean doFill, boolean doStroke) {
        Underline underline;
        if (doStroke && doFill) {
            underline = new Underline(fillColor, fillOpacity, context.getSvgTextProperties().getLineWidth(), 0.07f,
                    0, textDecorationLine, PdfCanvasConstants.LineCapStyle.BUTT)
                    .setStrokeColor(new TransparentColor(strokeColor, strokeOpacity))
                    .setStrokeWidth(context.getSvgTextProperties().getLineWidth())
                    .setDashPattern(context.getSvgTextProperties().getDashArray(),
                            context.getSvgTextProperties().getDashPhase());
        } else if (doStroke) {
            underline = new Underline(null, 0, context.getSvgTextProperties().getLineWidth(), 0.07f,
                    0, textDecorationLine, PdfCanvasConstants.LineCapStyle.BUTT)
                    .setStrokeColor(new TransparentColor(strokeColor, strokeOpacity))
                    .setStrokeWidth(context.getSvgTextProperties().getLineWidth())
                    .setDashPattern(context.getSvgTextProperties().getDashArray(),
                            context.getSvgTextProperties().getDashPhase());
        } else {
            underline = new Underline(fillColor, fillOpacity, 0, 0.07f, 0, textDecorationLine,
                    PdfCanvasConstants.LineCapStyle.BUTT);
        }
        underlineList.add(underline);
    }
}
