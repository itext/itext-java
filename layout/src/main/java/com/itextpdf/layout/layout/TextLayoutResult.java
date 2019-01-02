/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.layout.layout;

import com.itextpdf.layout.renderer.IRenderer;

/**
 * Represents the result of a text {@link com.itextpdf.layout.renderer.TextRenderer#layout(LayoutContext) layout}.
 */
public class TextLayoutResult extends MinMaxWidthLayoutResult {

    /**
     * Indicates whether some word was splitted during {@link com.itextpdf.layout.renderer.TextRenderer#layout(LayoutContext) layout}.
     */
    protected boolean wordHasBeenSplit;
    /**
     * Indicates whether split was forced by new line symbol in text or not.
     */
    protected boolean splitForcedByNewline;

    /**
     * Creates the {@link LayoutResult result of {@link com.itextpdf.layout.renderer.TextRenderer#layout(LayoutContext) layouting}}.
     * The {@link LayoutResult#causeOfNothing} will be set as null.
     *
     * @param status the status of {@link com.itextpdf.layout.renderer.TextRenderer#layout(LayoutContext)}
     * @param occupiedArea the area occupied by the content
     * @param splitRenderer the renderer to draw the splitted part of the content
     * @param overflowRenderer the renderer to draw the overflowed part of the content
     */
    public TextLayoutResult(int status, LayoutArea occupiedArea, IRenderer splitRenderer, IRenderer overflowRenderer) {
        super(status, occupiedArea, splitRenderer, overflowRenderer);
    }

    /**
     * Creates the {@link LayoutResult result of {@link com.itextpdf.layout.renderer.TextRenderer#layout(LayoutContext) layouting}}.
     *
     * @param status the status of {@link com.itextpdf.layout.renderer.TextRenderer#layout(LayoutContext)}
     * @param occupiedArea the area occupied by the content
     * @param splitRenderer the renderer to draw the splitted part of the content
     * @param overflowRenderer the renderer to draw the overflowed part of the content
     * @param cause the first renderer to produce {@link LayoutResult#NOTHING}
     */
    public TextLayoutResult(int status, LayoutArea occupiedArea, IRenderer splitRenderer, IRenderer overflowRenderer, IRenderer cause) {
        super(status, occupiedArea, splitRenderer, overflowRenderer, cause);
    }

    /**
     * Indicates whether some word in a rendered text was splitted during {@link com.itextpdf.layout.renderer.IRenderer#layout layout}.
     * The value will be set as true if, for example, the rendered words width is bigger than the width of layout area.
     *
     * @return whether some word was splitted or not.
     */
    public boolean isWordHasBeenSplit() {
        return wordHasBeenSplit;
    }

    /**
     * Sets {@link #wordHasBeenSplit}
     * @param wordHasBeenSplit indicates that some word was splitted during {@link com.itextpdf.layout.renderer.IRenderer#layout layout}.
     * @return {@link com.itextpdf.layout.layout.TextLayoutResult this layout result} the setting was applied on
     * @see #wordHasBeenSplit
     */
    public TextLayoutResult setWordHasBeenSplit(boolean wordHasBeenSplit) {
        this.wordHasBeenSplit = wordHasBeenSplit;
        return this;
    }

    /**
     * Indicates whether split was forced by new line symbol in rendered text.
     * The value will be set as true if, for example, the rendered text contains '\n' symbol.
     * This value can also be true even if the text was fully placed, but had line break at the end.
     *
     * @return whether split was forced by new line or not.
     */
    public boolean isSplitForcedByNewline() {
        return splitForcedByNewline;
    }

    /**
     * Sets {@link #isSplitForcedByNewline}
     *
     * @param isSplitForcedByNewline indicates that split was forced by new line symbol in rendered text.
     * @return {@link com.itextpdf.layout.layout.TextLayoutResult this layout result} the setting was applied on.
     * @see #setSplitForcedByNewline
     */
    public TextLayoutResult setSplitForcedByNewline(boolean isSplitForcedByNewline) {
        this.splitForcedByNewline = isSplitForcedByNewline;
        return this;
    }
}
