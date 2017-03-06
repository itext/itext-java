/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.util.ArrayUtil;
import com.itextpdf.io.util.TextUtil;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.LineLayoutResult;
import com.itextpdf.layout.layout.MinMaxWidthLayoutResult;
import com.itextpdf.layout.layout.TextLayoutResult;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.property.BaseDirection;
import com.itextpdf.layout.property.Leading;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.TabAlignment;
import com.itextpdf.layout.property.UnitValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

public class LineRenderer extends AbstractRenderer {

    protected float maxAscent;
    protected float maxDescent;

    // bidi levels
    protected byte[] levels;

    @Override
    public LineLayoutResult layout(LayoutContext layoutContext) {
        Rectangle layoutBox = layoutContext.getArea().getBBox().clone();
        occupiedArea = new LayoutArea(layoutContext.getArea().getPageNumber(), layoutBox.clone().moveDown(-layoutBox.getHeight()).setHeight(0));

        float curWidth = 0;
        maxAscent = 0;
        maxDescent = 0;
        int childPos = 0;

        MinMaxWidth minMaxWidth = new MinMaxWidth(0, layoutBox.getWidth());
        AbstractWidthHandler widthHandler = new MaxSumWidthHandler(minMaxWidth);
        
        updateChildrenParent();

        resolveChildrenFonts();

        int totalNumberOfTrimmedGlyphs = trimFirst();

        BaseDirection baseDirection = applyOtf();

        updateBidiLevels(totalNumberOfTrimmedGlyphs, baseDirection);

        boolean anythingPlaced = false;
        TabStop hangingTabStop = null;
        LineLayoutResult result = null;

        while (childPos < childRenderers.size()) {
            IRenderer childRenderer = childRenderers.get(childPos);
            LayoutResult childResult;
            Rectangle bbox = new Rectangle(layoutBox.getX() + curWidth, layoutBox.getY(), layoutBox.getWidth() - curWidth, layoutBox.getHeight());

            if (childRenderer instanceof TextRenderer) {
                // Delete these properties in case of relayout. We might have applied them during justify().
                childRenderer.deleteOwnProperty(Property.CHARACTER_SPACING);
                childRenderer.deleteOwnProperty(Property.WORD_SPACING);
            } else if (childRenderer instanceof TabRenderer) {
                if (hangingTabStop != null) {
                    IRenderer tabRenderer = childRenderers.get(childPos - 1);
                    tabRenderer.layout(new LayoutContext(new LayoutArea(layoutContext.getArea().getPageNumber(), bbox)));
                    curWidth += tabRenderer.getOccupiedArea().getBBox().getWidth();
                    widthHandler.updateMaxChildWidth(tabRenderer.getOccupiedArea().getBBox().getWidth());
                }
                hangingTabStop = calculateTab(childRenderer, curWidth, layoutBox.getWidth());
                if (childPos == childRenderers.size() - 1)
                    hangingTabStop = null;
                if (hangingTabStop != null) {
                    ++childPos;
                    continue;
                }
            }

            if (hangingTabStop != null && hangingTabStop.getTabAlignment() == TabAlignment.ANCHOR
                    && childRenderer instanceof TextRenderer) {
                childRenderer.setProperty(Property.TAB_ANCHOR, hangingTabStop.getTabAnchor());
            }

            childResult = childRenderer.layout(new LayoutContext(new LayoutArea(layoutContext.getArea().getPageNumber(), bbox)));

            float minChildWidth = 0;
            float maxChildWidth = 0;
            if (childResult instanceof MinMaxWidthLayoutResult) {
                minChildWidth = ((MinMaxWidthLayoutResult)childResult).getNotNullMinMaxWidth(bbox.getWidth()).getMinWidth();
                maxChildWidth = ((MinMaxWidthLayoutResult)childResult).getNotNullMinMaxWidth(bbox.getWidth()).getMaxWidth();
            }

            float childAscent = 0;
            float childDescent = 0;
            if (childRenderer instanceof ILeafElementRenderer) {
                childAscent = ((ILeafElementRenderer) childRenderer).getAscent();
                childDescent = ((ILeafElementRenderer) childRenderer).getDescent();
            }

            maxAscent = Math.max(maxAscent, childAscent);
            maxDescent = Math.min(maxDescent, childDescent);
            float maxHeight = maxAscent - maxDescent;

            if (hangingTabStop != null) {
                IRenderer tabRenderer = childRenderers.get(childPos - 1);
                float tabWidth = calculateTab(layoutBox, curWidth, hangingTabStop, childRenderer, childResult, tabRenderer);

                tabRenderer.layout(new LayoutContext(new LayoutArea(layoutContext.getArea().getPageNumber(), bbox)));
                childResult.getOccupiedArea().getBBox().moveRight(tabWidth);
                if (childResult.getSplitRenderer() != null)
                    childResult.getSplitRenderer().getOccupiedArea().getBBox().moveRight(tabWidth);

                float tabAndNextElemWidth = tabWidth + childResult.getOccupiedArea().getBBox().getWidth();
                if (hangingTabStop.getTabAlignment() == TabAlignment.RIGHT && curWidth + tabAndNextElemWidth < hangingTabStop.getTabPosition()) {
                    curWidth = hangingTabStop.getTabPosition();
                } else {
                    curWidth += tabAndNextElemWidth;
                }
                widthHandler.updateMinChildWidth(minChildWidth);
                widthHandler.updateMaxChildWidth(tabWidth + maxChildWidth);
                hangingTabStop = null;
            } else {
                curWidth += childResult.getOccupiedArea().getBBox().getWidth();
                widthHandler.updateMinChildWidth(minChildWidth);
                widthHandler.updateMaxChildWidth(maxChildWidth);
            }
            occupiedArea.setBBox(new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight() - maxHeight, curWidth, maxHeight));

            boolean newLineOccurred = (childResult instanceof TextLayoutResult && ((TextLayoutResult)childResult).isSplitForcedByNewline());
            boolean shouldBreakLayouting = childResult.getStatus() != LayoutResult.FULL || newLineOccurred;
            if (shouldBreakLayouting) {
                LineRenderer[] split = split();
                split[0].childRenderers = new ArrayList<>(childRenderers.subList(0, childPos));

                boolean wordWasSplitAndItWillFitOntoNextLine = false;
                if (childResult instanceof TextLayoutResult && ((TextLayoutResult) childResult).isWordHasBeenSplit()) {
                    LayoutResult newLayoutResult = childRenderer.layout(layoutContext);
                    if (newLayoutResult instanceof TextLayoutResult && !((TextLayoutResult) newLayoutResult).isWordHasBeenSplit()) {
                        wordWasSplitAndItWillFitOntoNextLine = true;
                    }
                }

                if (wordWasSplitAndItWillFitOntoNextLine) {
                    split[1].childRenderers.add(childRenderer);
                    split[1].childRenderers.addAll(childRenderers.subList(childPos + 1, childRenderers.size()));
                } else {
                    if (childResult.getStatus() == LayoutResult.PARTIAL || childResult.getStatus() == LayoutResult.FULL) {
                        split[0].addChild(childResult.getSplitRenderer());
                        anythingPlaced = true;
                    }

                    if (childResult.getStatus() == LayoutResult.PARTIAL && childResult.getOverflowRenderer() instanceof ImageRenderer) {
                        ((ImageRenderer) childResult.getOverflowRenderer()).autoScale(layoutContext.getArea());
                    }

                    if (null != childResult.getOverflowRenderer()) {
                        split[1].childRenderers.add(childResult.getOverflowRenderer());
                    }
                    split[1].childRenderers.addAll(childRenderers.subList(childPos + 1, childRenderers.size()));
                    // no sense to process empty renderer
                    if (split[1].childRenderers.size() == 0) {
                        split[1] = null;
                    }
                }

                IRenderer causeOfNothing = childResult.getStatus() == LayoutResult.NOTHING ? childResult.getCauseOfNothing() : childRenderer;
                if (split[1] == null) {
                    result = new LineLayoutResult(LayoutResult.FULL, occupiedArea, split[0], split[1], causeOfNothing);
                } else {
                    if (anythingPlaced) {
                        result = new LineLayoutResult(LayoutResult.PARTIAL, occupiedArea, split[0], split[1], causeOfNothing);
                    } else {
                        result = new LineLayoutResult(LayoutResult.NOTHING, null, split[0], split[1], causeOfNothing);
                    }
                }
                if (newLineOccurred) {
                    result.setSplitForcedByNewline(true);
                }
                break;
            } else {
                anythingPlaced = true;
                childPos++;
            }
        }

        if (result == null) {
            if (anythingPlaced || 0 == childRenderers.size()) {
                result = new LineLayoutResult(LayoutResult.FULL, occupiedArea, null, null);
            } else {
                result = new LineLayoutResult(LayoutResult.NOTHING, null, null, this, this);
            }
        }

        if (baseDirection != null && baseDirection != BaseDirection.NO_BIDI) {
            List<IRenderer> children = null;
            if (result.getStatus() == LayoutResult.PARTIAL) {
                children = result.getSplitRenderer().getChildRenderers();
            } else if (result.getStatus() == LayoutResult.FULL) {
                children = getChildRenderers();
            }

            if (children != null) {
                boolean newLineFound = false;
                List<RendererGlyph> lineGlyphs = new ArrayList<>();
                for (IRenderer child : children) {
                    if (newLineFound) {
                        break;
                    }
                    if (child instanceof TextRenderer) {
                        GlyphLine childLine = ((TextRenderer) child).line;
                        for (int i = childLine.start; i < childLine.end; i++) {
                            if (TextUtil.isNewLine(childLine.get(i))) {
                                newLineFound = true;
                                break;
                            }
                            lineGlyphs.add(new RendererGlyph(childLine.get(i), (TextRenderer) child));
                        }
                    }
                }
                byte[] lineLevels = new byte[lineGlyphs.size()];
                if (levels != null) {
                    System.arraycopy(levels, 0, lineLevels, 0, lineGlyphs.size());
                }

                int[] reorder = TypographyUtils.reorderLine(lineGlyphs, lineLevels, levels);

                if (reorder != null) {
                    children.clear();
                    int pos = 0;
                    int initialPos = 0;
                    boolean reversed = false;
                    int offset = 0;
                    while (pos < lineGlyphs.size()) {
                        IRenderer renderer = lineGlyphs.get(pos).renderer;
                        TextRenderer newRenderer = new TextRenderer((TextRenderer) renderer).removeReversedRanges();
                        children.add(newRenderer);
                        newRenderer.line = new GlyphLine(newRenderer.line);
                        List<Glyph> replacementGlyphs = new ArrayList<>();
                        while (pos < lineGlyphs.size() && lineGlyphs.get(pos).renderer == renderer) {
                            if (pos + 1 < lineGlyphs.size()) {
                                if (reorder[pos] == reorder[pos + 1] + 1 &&
                                        !TextUtil.isSpaceOrWhitespace(lineGlyphs.get(pos + 1).glyph) && !TextUtil.isSpaceOrWhitespace(lineGlyphs.get(pos).glyph)) {
                                    reversed = true;
                                } else {
                                    if (reversed) {
                                        List<int[]> reversedRange = newRenderer.initReversedRanges();
                                        reversedRange.add(new int[]{initialPos - offset, pos - offset});
                                        reversed = false;
                                    }
                                    initialPos = pos + 1;
                                }
                            }

                            replacementGlyphs.add(lineGlyphs.get(pos).glyph);
                            pos++;
                        }

                        if (reversed) {
                            List<int[]> reversedRange = newRenderer.initReversedRanges();
                            reversedRange.add(new int[]{initialPos - offset, pos - 1 - offset});
                            reversed = false;
                            initialPos = pos;
                        }
                        offset = initialPos;
                        newRenderer.line.setGlyphs(replacementGlyphs);
                    }

                    float currentXPos = layoutContext.getArea().getBBox().getLeft();
                    for (IRenderer child : children) {
                        float currentWidth = ((TextRenderer) child).calculateLineWidth();
                        float[] margins = ((TextRenderer) child).getMargins();
                        currentWidth += margins[1] + margins[3];
                        ((TextRenderer) child).occupiedArea.getBBox().setX(currentXPos).setWidth(currentWidth);
                        currentXPos += currentWidth;
                    }
                }

                if (result.getStatus() == LayoutResult.PARTIAL) {
                    LineRenderer overflow = (LineRenderer) result.getOverflowRenderer();
                    if (levels != null) {
                        overflow.levels = new byte[levels.length - lineLevels.length];
                        System.arraycopy(levels, lineLevels.length, overflow.levels, 0, overflow.levels.length);
                        if (overflow.levels.length == 0) {
                            overflow.levels = null;
                        }
                    }
                }
            }
        }

        if (anythingPlaced) {
            LineRenderer processed = result.getStatus() == LayoutResult.FULL ? this : (LineRenderer) result.getSplitRenderer();
            processed.adjustChildrenYLine().trimLast();
            result.setMinMaxWidth(minMaxWidth);
        }

        return result;
    }

    public float getMaxAscent() {
        return maxAscent;
    }

    public float getMaxDescent() {
        return maxDescent;
    }

    public float getYLine() {
        return occupiedArea.getBBox().getY() - maxDescent;
    }

    public float getLeadingValue(Leading leading) {
        switch (leading.getType()) {
            case Leading.FIXED:
                return leading.getValue();
            case Leading.MULTIPLIED:
                return occupiedArea.getBBox().getHeight() * leading.getValue();
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public IRenderer getNextRenderer() {
        return new LineRenderer();
    }

    @Override
    protected Float getFirstYLineRecursively() {
        return getYLine();
    }

    protected void justify(float width) {
        float ratio = (float) this.getPropertyAsFloat(Property.SPACING_RATIO);
        float freeWidth = occupiedArea.getBBox().getX() + width -
                getLastChildRenderer().getOccupiedArea().getBBox().getX() - getLastChildRenderer().getOccupiedArea().getBBox().getWidth();
        int numberOfSpaces = getNumberOfSpaces();
        int baseCharsCount = baseCharactersCount();
        float baseFactor = freeWidth / (ratio * numberOfSpaces + (1 - ratio) * (baseCharsCount - 1));
        float wordSpacing = ratio * baseFactor;
        float characterSpacing = (1 - ratio) * baseFactor;

        float lastRightPos = occupiedArea.getBBox().getX();
        for (int i = 0; i < childRenderers.size(); ++i) {
            IRenderer child = childRenderers.get(i);
            float childX = child.getOccupiedArea().getBBox().getX();
            child.move(lastRightPos - childX, 0);
            childX = lastRightPos;
            if (child instanceof TextRenderer) {
                float childHSCale = (float) ((TextRenderer) child).getPropertyAsFloat(Property.HORIZONTAL_SCALING, 1f);
                child.setProperty(Property.CHARACTER_SPACING, characterSpacing / childHSCale);
                child.setProperty(Property.WORD_SPACING, wordSpacing / childHSCale);
                boolean isLastTextRenderer = i + 1 == childRenderers.size();
                float widthAddition = (isLastTextRenderer ? (((TextRenderer) child).lineLength() - 1) : ((TextRenderer) child).lineLength()) * characterSpacing +
                        wordSpacing * ((TextRenderer) child).getNumberOfSpaces();
                child.getOccupiedArea().getBBox().setWidth(child.getOccupiedArea().getBBox().getWidth() + widthAddition);
            }
            lastRightPos = childX + child.getOccupiedArea().getBBox().getWidth();
        }

        getOccupiedArea().getBBox().setWidth(width);
    }

    protected int getNumberOfSpaces() {
        int spaces = 0;
        for (IRenderer child : childRenderers) {
            if (child instanceof TextRenderer) {
                spaces += ((TextRenderer) child).getNumberOfSpaces();
            }
        }
        return spaces;
    }

    /**
     * Gets the total lengths of characters in this line. Other elements (images, tables) are not taken
     * into account.
     */
    protected int length() {
        int length = 0;
        for (IRenderer child : childRenderers) {
            if (child instanceof TextRenderer) {
                length += ((TextRenderer) child).lineLength();
            }
        }
        return length;
    }

    /**
     * Returns the number of base characters, i.e. non-mark characters
     */
    protected int baseCharactersCount() {
        int count = 0;
        for (IRenderer child : childRenderers) {
            if (child instanceof TextRenderer) {
                count += ((TextRenderer) child).baseCharactersCount();
            }
        }
        return count;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (IRenderer renderer : childRenderers) {
            sb.append(renderer.toString());
        }
        return sb.toString();
    }

    protected LineRenderer createSplitRenderer() {
        return (LineRenderer) getNextRenderer();
    }

    protected LineRenderer createOverflowRenderer() {
        return (LineRenderer) getNextRenderer();
    }

    protected LineRenderer[] split() {
        LineRenderer splitRenderer = createSplitRenderer();
        splitRenderer.occupiedArea = occupiedArea.clone();
        splitRenderer.parent = parent;
        splitRenderer.maxAscent = maxAscent;
        splitRenderer.maxDescent = maxDescent;
        splitRenderer.levels = levels;
        splitRenderer.addAllProperties(getOwnProperties());

        LineRenderer overflowRenderer = createOverflowRenderer();
        overflowRenderer.parent = parent;
        overflowRenderer.addAllProperties(getOwnProperties());

        return new LineRenderer[]{splitRenderer, overflowRenderer};
    }

    protected LineRenderer adjustChildrenYLine() {
        float actualYLine = occupiedArea.getBBox().getY() + occupiedArea.getBBox().getHeight() - maxAscent;
        for (IRenderer renderer : childRenderers) {
            if (renderer instanceof ILeafElementRenderer) {
                float descent = ((ILeafElementRenderer) renderer).getDescent();
                renderer.move(0, actualYLine - renderer.getOccupiedArea().getBBox().getBottom() + descent);
            } else {
                renderer.move(0, occupiedArea.getBBox().getY() - renderer.getOccupiedArea().getBBox().getBottom());
            }
        }
        return this;
    }

    protected LineRenderer trimLast() {
        IRenderer lastRenderer = childRenderers.size() > 0 ? childRenderers.get(childRenderers.size() - 1) : null;
        if (lastRenderer instanceof TextRenderer) {
            float trimmedSpace = ((TextRenderer) lastRenderer).trimLast();
            occupiedArea.getBBox().setWidth(occupiedArea.getBBox().getWidth() - trimmedSpace);
        }
        return this;
    }

    protected boolean containsImage() {
        for (IRenderer renderer : childRenderers) {
            if (renderer instanceof ImageRenderer) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected MinMaxWidth getMinMaxWidth(float availableWidth) {
        LineLayoutResult result = (LineLayoutResult) layout(new LayoutContext(new LayoutArea(1, new Rectangle(availableWidth, AbstractRenderer.INF))));
        return result.getNotNullMinMaxWidth(availableWidth);
    }

    private IRenderer getLastChildRenderer() {
        return childRenderers.get(childRenderers.size() - 1);
    }

    private TabStop getNextTabStop(float curWidth) {
        NavigableMap<Float, TabStop> tabStops = this.<NavigableMap<Float, TabStop>>getProperty(Property.TAB_STOPS);

        Map.Entry<Float, TabStop> nextTabStopEntry = null;
        TabStop nextTabStop = null;

        if (tabStops != null)
            nextTabStopEntry = tabStops.higherEntry(curWidth);
        if (nextTabStopEntry != null) {
            nextTabStop = ((Map.Entry<Float, TabStop>)nextTabStopEntry).getValue();
        }

        return nextTabStop;
    }


    /**
     * Calculates and sets encountered tab size.
     * Returns null, if processing is finished and layout can be performed for the tab renderer;
     * otherwise, in case when the tab should be processed after the next element in the line, this method returns corresponding tab stop.
     */
    private TabStop calculateTab(IRenderer childRenderer, float curWidth, float lineWidth) {
        TabStop nextTabStop = getNextTabStop(curWidth);

        if (nextTabStop == null) {
            processDefaultTab(childRenderer, curWidth, lineWidth);
            return null;
        }

        childRenderer.setProperty(Property.TAB_LEADER, nextTabStop.getTabLeader());
        childRenderer.setProperty(Property.WIDTH, UnitValue.createPointValue(nextTabStop.getTabPosition() - curWidth));
        childRenderer.setProperty(Property.MIN_HEIGHT, maxAscent - maxDescent);

        if (nextTabStop.getTabAlignment() == TabAlignment.LEFT) {
            return null;
        }

        return nextTabStop;
    }

    /**
     * Calculates and sets tab size with the account of the element that is next in the line after the tab.
     * Returns resulting width of the tab.
     */
    private float calculateTab(Rectangle layoutBox, float curWidth, TabStop tabStop, IRenderer nextElementRenderer, LayoutResult nextElementResult, IRenderer tabRenderer) {
        float childWidth = 0;
        if (nextElementRenderer != null)
            childWidth = nextElementRenderer.getOccupiedArea().getBBox().getWidth();
        float tabWidth = 0;
        switch (tabStop.getTabAlignment()) {
            case RIGHT:
                tabWidth = tabStop.getTabPosition() - curWidth - childWidth;
                break;
            case CENTER:
                tabWidth = tabStop.getTabPosition() - curWidth - childWidth / 2;
                break;
            case ANCHOR:
                float anchorPosition = -1;
                if (nextElementRenderer instanceof TextRenderer)
                    anchorPosition = ((TextRenderer) nextElementRenderer).getTabAnchorCharacterPosition();
                if (anchorPosition == -1)
                    anchorPosition = childWidth;
                tabWidth = tabStop.getTabPosition() - curWidth - anchorPosition;
                break;
        }
        if (tabWidth < 0)
            tabWidth = 0;
        if (curWidth + tabWidth + childWidth > layoutBox.getWidth())
            tabWidth -= (curWidth + childWidth + tabWidth) - layoutBox.getWidth();

        tabRenderer.setProperty(Property.WIDTH, UnitValue.createPointValue(tabWidth));
        tabRenderer.setProperty(Property.MIN_HEIGHT, maxAscent - maxDescent);

        return tabWidth;
    }

    private void processDefaultTab(IRenderer tabRenderer, float curWidth, float lineWidth) {
        Float tabDefault = this.getPropertyAsFloat(Property.TAB_DEFAULT);
        Float tabWidth = tabDefault - curWidth % tabDefault;
        if (curWidth + tabWidth > lineWidth)
            tabWidth = lineWidth - curWidth;
        tabRenderer.setProperty(Property.WIDTH, UnitValue.createPointValue((float) tabWidth));
        tabRenderer.setProperty(Property.MIN_HEIGHT, maxAscent - maxDescent);
    }

    private void updateChildrenParent() {
        for (IRenderer renderer : childRenderers) {
            renderer.setParent(this);
        }
    }

    /**
     * Trim first child text renderers.
     *
     * @return total number of trimmed glyphs.
     */
    private int trimFirst() {
        int totalNumberOfTrimmedGlyphs = 0;
        for (IRenderer renderer : childRenderers) {
            if (renderer instanceof TextRenderer) {
                TextRenderer textRenderer = (TextRenderer) renderer;
                GlyphLine currentText = textRenderer.getText();
                if (currentText != null) {
                    int prevTextStart = currentText.start;
                    textRenderer.trimFirst();
                    int numOfTrimmedGlyphs = textRenderer.getText().start - prevTextStart;
                    totalNumberOfTrimmedGlyphs += numOfTrimmedGlyphs;
                }
                if (textRenderer.length() > 0) {
                    break;
                }
            } else {
                break;
            }
        }
        return totalNumberOfTrimmedGlyphs;
    }

    /**
     * Apply OTF features and return the last(!) base direction of child renderer
     *
     * @return the last(!) base direction of child renderer.
     */
    private BaseDirection applyOtf() {
        BaseDirection baseDirection = this.<BaseDirection>getProperty(Property.BASE_DIRECTION);
        for (IRenderer renderer : childRenderers) {
            if (renderer instanceof TextRenderer) {
                ((TextRenderer) renderer).applyOtf();
                if (baseDirection == null || baseDirection == BaseDirection.NO_BIDI) {
                    baseDirection = renderer.<BaseDirection>getOwnProperty(Property.BASE_DIRECTION);
                }
            }
        }
        return baseDirection;
    }

    private void updateBidiLevels(int totalNumberOfTrimmedGlyphs, BaseDirection baseDirection) {
        if (totalNumberOfTrimmedGlyphs != 0 && levels != null) {
            levels = Arrays.copyOfRange(levels, totalNumberOfTrimmedGlyphs, levels.length);
        }

        List<Integer> unicodeIdsReorderingList = null;
        if (levels == null && baseDirection != null && baseDirection != BaseDirection.NO_BIDI) {
            unicodeIdsReorderingList = new ArrayList<>();
            boolean newLineFound = false;
            for (IRenderer child : childRenderers) {
                if (newLineFound) {
                    break;
                }
                if (child instanceof TextRenderer) {
                    GlyphLine text = ((TextRenderer) child).getText();
                    for (int i = text.start; i < text.end; i++) {
                        Glyph glyph = text.get(i);
                        if (TextUtil.isNewLine(glyph)) {
                            newLineFound = true;
                            break;
                        }
                        // we assume all the chars will have the same bidi group
                        // we also assume pairing symbols won't get merged with other ones
                        int unicode = glyph.hasValidUnicode() ? glyph.getUnicode() : glyph.getUnicodeChars()[0];
                        unicodeIdsReorderingList.add(unicode);
                    }
                }
            }
            levels = unicodeIdsReorderingList.size() > 0 ? TypographyUtils.getBidiLevels(baseDirection, ArrayUtil.toArray(unicodeIdsReorderingList)) : null;
        }
    }

    /**
     * While resolving TextRenderer may split into several ones with different fonts.
     */
    private void resolveChildrenFonts() {
        List<IRenderer> newChildRenderers = new ArrayList<>(childRenderers.size());
        boolean updateChildRendrers = false;
        for (IRenderer child : childRenderers) {
            if (child instanceof TextRenderer) {
                if (((TextRenderer)child).resolveFonts(newChildRenderers)) {
                    updateChildRendrers = true;
                }
            } else {
                newChildRenderers.add(child);
            }
        }

        // this mean, that some TextRenderer has been replaced.
        if (updateChildRendrers) {
            childRenderers = newChildRenderers;
        }
    }


    static class RendererGlyph {
        public RendererGlyph(Glyph glyph, TextRenderer textRenderer) {
            this.glyph = glyph;
            this.renderer = textRenderer;
        }

        public Glyph glyph;
        public TextRenderer renderer;
    }

}
