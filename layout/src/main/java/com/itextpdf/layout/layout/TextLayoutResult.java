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
package com.itextpdf.layout.layout;

import com.itextpdf.layout.renderer.IRenderer;

/**
 * Represents the result of a text {@link com.itextpdf.layout.renderer.TextRenderer#layout(LayoutContext) layout}.
 */
public class TextLayoutResult extends MinMaxWidthLayoutResult {

    /**
     * Indicates whether some word was split during {@link com.itextpdf.layout.renderer.TextRenderer#layout(LayoutContext) layout}.
     */
    protected boolean wordHasBeenSplit;
    /**
     * Indicates whether split was forced by new line symbol in text or not.
     */
    protected boolean splitForcedByNewline;

    protected boolean containsPossibleBreak = false;

    protected boolean startsWithSplitCharacterWhiteSpace = false;

    protected boolean endsWithSplitCharacter = false;

    protected float leftMinWidth;

    protected float rightMinWidth;

    /**
     * Creates the {@link LayoutResult result of {@link com.itextpdf.layout.renderer.TextRenderer#layout(LayoutContext) layouting}}.
     * The {@link LayoutResult#causeOfNothing} will be set as null.
     *
     * @param status the status of {@link com.itextpdf.layout.renderer.TextRenderer#layout(LayoutContext)}
     * @param occupiedArea the area occupied by the content
     * @param splitRenderer the renderer to draw the split part of the content
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
     * @param splitRenderer the renderer to draw the split part of the content
     * @param overflowRenderer the renderer to draw the overflowed part of the content
     * @param cause the first renderer to produce {@link LayoutResult#NOTHING}
     */
    public TextLayoutResult(int status, LayoutArea occupiedArea, IRenderer splitRenderer, IRenderer overflowRenderer, IRenderer cause) {
        super(status, occupiedArea, splitRenderer, overflowRenderer, cause);
    }

    /**
     * Indicates whether some word in a rendered text was split during {@link com.itextpdf.layout.renderer.IRenderer#layout layout}.
     * The value will be set as true if, for example, the rendered words width is bigger than the width of layout area.
     *
     * @return whether some word was split or not.
     */
    public boolean isWordHasBeenSplit() {
        return wordHasBeenSplit;
    }

    /**
     * Sets {@link #wordHasBeenSplit}
     * @param wordHasBeenSplit indicates that some word was split during {@link com.itextpdf.layout.renderer.IRenderer#layout layout}.
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

    /**
     * Indicates whether split renderer contains possible break.
     * Possible breaks are either whitespaces or split characters.
     *
     * @return true if there's a possible break within the split renderer.
     * @see com.itextpdf.layout.splitting.ISplitCharacters
     */
    public boolean isContainsPossibleBreak() {
        return containsPossibleBreak;
    }

    /**
     * Sets {@link #isContainsPossibleBreak()}.
     *
     * @param containsPossibleBreak indicates that split renderer contains possible break.
     * @return {@link com.itextpdf.layout.layout.TextLayoutResult this layout result} the setting was applied on.
     * @see #isContainsPossibleBreak
     */
    public TextLayoutResult setContainsPossibleBreak(boolean containsPossibleBreak) {
        this.containsPossibleBreak = containsPossibleBreak;
        return this;
    }

    /**
     * Sets {@link #isStartsWithSplitCharacterWhiteSpace()}.
     *
     * @param startsWithSplitCharacterWhiteSpace indicates if TextRenderer#line starts with a split character that is
     *                                           also a whitespace.
     * @return {@link com.itextpdf.layout.layout.TextLayoutResult this layout result} the setting was applied on.
     * @see #isStartsWithSplitCharacterWhiteSpace
     */
    public TextLayoutResult setStartsWithSplitCharacterWhiteSpace(boolean startsWithSplitCharacterWhiteSpace) {
        this.startsWithSplitCharacterWhiteSpace = startsWithSplitCharacterWhiteSpace;
        return this;
    }

    /**
     * Indicates whether TextRenderer#line starts with a whitespace.
     *
     * @return true if TextRenderer#line starts with a whitespace.
     */
    public boolean isStartsWithSplitCharacterWhiteSpace() {
        return startsWithSplitCharacterWhiteSpace;
    }

    /**
     * Sets {@link #isEndsWithSplitCharacter()}.
     *
     * @param endsWithSplitCharacter indicates if TextRenderer#line ends with a splitCharacter.
     * @return {@link com.itextpdf.layout.layout.TextLayoutResult this layout result} the setting was applied on.
     * @see #isEndsWithSplitCharacter
     */
    public TextLayoutResult setEndsWithSplitCharacter(boolean endsWithSplitCharacter) {
        this.endsWithSplitCharacter = endsWithSplitCharacter;
        return this;
    }

    /**
     * Indicates whether TextRenderer#line ends with a splitCharacter.
     *
     * @return true if TextRenderer#line ends with a splitCharacter.
     * @see com.itextpdf.layout.splitting.ISplitCharacters
     */
    public boolean isEndsWithSplitCharacter() {
        return endsWithSplitCharacter;
    }

    /**
     * Sets min width of the leftmost unbreakable part of the TextRenderer#line after layout.
     * This value includes left-side additional width, i.e. left margin, border and padding widths.
     * In case when entire TextRenderer#line is unbreakable, leftMinWidth also includes right-side additional width.
     *
     * @param leftMinWidth min width of the leftmost unbreakable part of the TextRenderer#line after layout.
     * @return {@link com.itextpdf.layout.layout.TextLayoutResult this layout result} the setting was applied on.
     */
    public TextLayoutResult setLeftMinWidth(float leftMinWidth) {
        this.leftMinWidth = leftMinWidth;
        return this;
    }

    /**
     * Gets min width of the leftmost unbreakable part of the TextRenderer#line after layout.
     * This value leftMinWidth includes left-side additional width, i.e. left margin, border and padding widths.
     * In case when entire TextRenderer#line is unbreakable, leftMinWidth also includes right-side additional width.
     *
     * @return min width of the leftmost unbreakable part of the TextRenderer#line after layout.
     */
    public float getLeftMinWidth() {
        return leftMinWidth;
    }

    /**
     * Sets min width of the rightmost unbreakable part of the TextRenderer#line after layout.
     * This value includes right-side additional width, i.e. right margin, border and padding widths.
     * In case when entire TextRenderer#line is unbreakable, this value must be -1
     * and right-side additional width must be included in leftMinWidth.
     *
     * @param rightMinWidth min width of the rightmost unbreakable part of the TextRenderer#line after layout.
     * @return {@link com.itextpdf.layout.layout.TextLayoutResult this layout result} the setting was applied on.
     */
    public TextLayoutResult setRightMinWidth(float rightMinWidth) {
        this.rightMinWidth = rightMinWidth;
        return this;
    }

    /**
     * Gets min width of the rightmost unbreakable part of the TextRenderer#line after layout.
     * This value includes right-side additional width, i.e. right margin, border and padding widths.
     * In case when entire TextRenderer#line is unbreakable, this value must be -1
     * and right-side additional width must be included in leftMinWidth.
     *
     * @return min width of the leftmost unbreakable part of the TextRenderer#line after layout.
     */
    public float getRightMinWidth() {
        return rightMinWidth;
    }
}
