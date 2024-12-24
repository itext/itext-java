/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.styledxmlparser.util.WhiteSpaceUtil;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.impl.ISvgTextNodeRenderer;
import com.itextpdf.svg.renderers.impl.TextLeafSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.TextSvgBranchRenderer;

/**
 * Class containing utility methods for text operations in the context of SVG processing
 */
public final class SvgTextUtil {

    private SvgTextUtil() {
    }

    /**
     * Trim all the leading whitespace characters from the passed string
     *
     * @param toTrim string to trim
     * @return string with all leading whitespace characters removed
     */
    public static String trimLeadingWhitespace(String toTrim) {
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
        return "".equals(trimmedText);
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
}
