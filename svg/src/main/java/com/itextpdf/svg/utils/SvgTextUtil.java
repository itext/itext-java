/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.svg.utils;


import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
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
                //if the character is whitespace and not a newline, increase current
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
                    //if the character is whitespace and not a newline, increase current
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
        // when svg is parsed by jsoup it leaves all whitespace in text element as is. Meaning that
        // tab/space indented xml files will retain their tabs and spaces.
        // The following regex replaces all whitespace with a single space.
        boolean performLeadingTrim = isLeadingElement;
        for (ISvgTextNodeRenderer child : root.getChildren()) {
            //If leaf, process contents, if branch, call function again
            if (child instanceof TextSvgBranchRenderer) {
                //Branch processing
                processWhiteSpace((TextSvgBranchRenderer) child, child.containsAbsolutePositionChange());
                ((TextSvgBranchRenderer) child).markWhiteSpaceProcessed();
            }
            if (child instanceof TextLeafSvgNodeRenderer) {
                //Leaf processing
                TextLeafSvgNodeRenderer leafRend = (TextLeafSvgNodeRenderer) child;
                //Process text
                String toProcess = leafRend.getAttribute(SvgConstants.Attributes.TEXT_CONTENT);
                toProcess = toProcess.replaceAll("\\s+", " ");
                toProcess = WhiteSpaceUtil.collapseConsecutiveSpaces(toProcess);
                if (performLeadingTrim) {
                    //Trim leading white spaces
                    toProcess = trimLeadingWhitespace(toProcess);
                    toProcess = trimTrailingWhitespace(toProcess);
                    performLeadingTrim = false;
                } else {
                    //only collapse whitespace
                    toProcess = trimTrailingWhitespace(toProcess);
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
        //Trim leading whitespace
        trimmedText = SvgTextUtil.trimLeadingWhitespace(trimmedText);
        //Trim trailing whitespace
        trimmedText = SvgTextUtil.trimTrailingWhitespace(trimmedText);
        return trimmedText.equals("");
    }

    /**
     * Resolve the font size stored inside the passed renderer
     *
     * @param renderer       renderer containing the font size declaration
     * @param parentFontSize parent font size to fall back on if the renderer does not contain a font size declarations or if the stored declaration is invalid
     * @return float containing the font-size, or the parent font size if the renderer's declaration cannot be resolved
     */
    public static float resolveFontSize(ISvgTextNodeRenderer renderer, float parentFontSize) {
        //Use own font-size declaration if it is present, parent's otherwise
        float fontSize = Float.NaN;
        String elementFontSize = renderer.getAttribute(SvgConstants.Attributes.FONT_SIZE);
        if (null != elementFontSize && !elementFontSize.isEmpty()) {
            if (CssUtils.isRelativeValue(elementFontSize) || CommonCssConstants.LARGER.equals(elementFontSize) || CommonCssConstants.SMALLER.equals(elementFontSize)) {
                // TODO DEVSIX-2866 Support rem value for svgs
                fontSize = CssUtils.parseRelativeFontSize(elementFontSize, parentFontSize);
            } else {
                fontSize = CssUtils.parseAbsoluteFontSize(elementFontSize, CommonCssConstants.PX);
            }
        }
        if ((Float.isNaN(fontSize)) || fontSize < 0f) {
            fontSize = parentFontSize;
        }
        return fontSize;
    }
}
