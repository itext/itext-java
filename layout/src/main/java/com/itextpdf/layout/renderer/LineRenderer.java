/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.property.BaseDirection;
import com.itextpdf.layout.property.Leading;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.LineLayoutResult;
import com.itextpdf.layout.layout.TextLayoutResult;
import com.itextpdf.layout.property.TabAlignment;
import com.itextpdf.layout.property.UnitValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

public class LineRenderer extends AbstractRenderer {

    protected float maxAscent;
    protected float maxDescent;

    protected byte[] levels;

    @Override
    public LineLayoutResult layout(LayoutContext layoutContext) {
        Rectangle layoutBox = layoutContext.getArea().getBBox().clone();
        occupiedArea = new LayoutArea(layoutContext.getArea().getPageNumber(), layoutBox.clone().moveDown(-layoutBox.getHeight()).setHeight(0));

        float curWidth = 0;
        maxAscent = 0;
        maxDescent = 0;
        int childPos = 0;

        BaseDirection baseDirection = this.<BaseDirection>getProperty(Property.BASE_DIRECTION);
        for (IRenderer renderer : childRenderers) {
            if (renderer instanceof TextRenderer) {
                renderer.setParent(this);
                ((TextRenderer) renderer).applyOtf();
                renderer.setParent(null);
                if (baseDirection == null || baseDirection == BaseDirection.NO_BIDI) {
                    baseDirection = renderer.<BaseDirection>getOwnProperty(Property.BASE_DIRECTION);
                }
            }
        }

        if (levels == null && baseDirection != null && baseDirection != BaseDirection.NO_BIDI) {
            List<Integer> unicodeIdsLst = new ArrayList<>();
            for (IRenderer child : childRenderers) {
                if (child instanceof TextRenderer) {
                    GlyphLine text = ((TextRenderer) child).getText();
                    for (int i = text.start; i < text.end; i++) {
                        assert text.get(i).getChars().length > 0;
                        // we assume all the chars will have the same bidi group
                        // we also assume pairing symbols won't get merged with other ones
                        int unicode = text.get(i).getChars()[0];
                        unicodeIdsLst.add(unicode);
                    }
                }
            }
            levels = TypographyUtils.getBidiLevels(baseDirection, ArrayUtil.toArray(unicodeIdsLst));
        }

        boolean anythingPlaced = false;
        TabStop nextTabStop = null;

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
                if (nextTabStop != null) {
                    IRenderer tabRenderer = childRenderers.get(childPos - 1);
                    tabRenderer.layout(new LayoutContext(new LayoutArea(layoutContext.getArea().getPageNumber(), bbox)));
                    curWidth += tabRenderer.getOccupiedArea().getBBox().getWidth();
                }
                nextTabStop = calculateTab(childRenderer, curWidth, layoutBox.getWidth());
                if (childPos == childRenderers.size() - 1)
                    nextTabStop = null;
                if (nextTabStop != null) {
                    ++childPos;
                    continue;
                }
            }

            if (!anythingPlaced && childRenderer instanceof TextRenderer) {
                ((TextRenderer) childRenderer).trimFirst();
            }

            if (nextTabStop != null && nextTabStop.getTabAlignment() == TabAlignment.ANCHOR
                                               && childRenderer instanceof TextRenderer) {
                childRenderer.setProperty(Property.TAB_ANCHOR, nextTabStop.getTabAnchor());
            }

            childResult = childRenderer.setParent(this).layout(new LayoutContext(new LayoutArea(layoutContext.getArea().getPageNumber(), bbox)));

            float childAscent = 0;
            float childDescent = 0;
            if (childRenderer instanceof TextRenderer) {
                childAscent = ((TextRenderer) childRenderer).getAscent();
                childDescent = ((TextRenderer) childRenderer).getDescent();
            } else if (childRenderer instanceof ImageRenderer) {
                childAscent = childRenderer.getOccupiedArea().getBBox().getHeight();
            }

            maxAscent = Math.max(maxAscent, childAscent);
            maxDescent = Math.min(maxDescent, childDescent);
            float maxHeight = maxAscent - maxDescent;

            if (nextTabStop != null) {
                IRenderer tabRenderer = childRenderers.get(childPos - 1);
                float tabWidth = calculateTab(layoutBox, curWidth, nextTabStop, childRenderer, childResult, tabRenderer);

                tabRenderer.layout(new LayoutContext(new LayoutArea(layoutContext.getArea().getPageNumber(), bbox)));
                childResult.getOccupiedArea().getBBox().moveRight(tabWidth);
                if (childResult.getSplitRenderer() != null)
                    childResult.getSplitRenderer().getOccupiedArea().getBBox().moveRight(tabWidth);
                nextTabStop = null;

                curWidth += tabWidth;
            }

            curWidth += childResult.getOccupiedArea().getBBox().getWidth();
            occupiedArea.setBBox(new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight() - maxHeight, curWidth, maxHeight));

            if (childResult.getStatus() != LayoutResult.FULL) {
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
                    if (childResult.getStatus() == LayoutResult.PARTIAL) {
                        split[0].addChild(childResult.getSplitRenderer());
                        anythingPlaced = true;
                    }

                    if (childResult.getStatus() == LayoutResult.PARTIAL && childResult.getOverflowRenderer() instanceof ImageRenderer){
                        ((ImageRenderer)childResult.getOverflowRenderer()).autoScale(layoutContext.getArea());
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

                result = new LineLayoutResult(anythingPlaced ? LayoutResult.PARTIAL : LayoutResult.NOTHING, occupiedArea, split[0], split[1]);
                if (childResult.getStatus() == LayoutResult.PARTIAL && childResult instanceof TextLayoutResult && ((TextLayoutResult) childResult).isSplitForcedByNewline())
                    result.setSplitForcedByNewline(true);
                break;
            } else {
                anythingPlaced = true;
                childPos++;
            }
        }

        if (result == null) {
            if (anythingPlaced) {
                result = new LineLayoutResult(LayoutResult.FULL, occupiedArea, null, null);
            } else {
                result = new LineLayoutResult(LayoutResult.NOTHING, occupiedArea, null, this);
            }
        }

        // Consider for now that all the children have the same font, and that after reordering text pieces
        // can be reordered, but cannot be split.
        if (baseDirection != null && baseDirection != BaseDirection.NO_BIDI) {
            List<IRenderer> children = null;
            if (result.getStatus() == LayoutResult.PARTIAL) {
                children = result.getSplitRenderer().getChildRenderers();
            } else if (result.getStatus() == LayoutResult.FULL) {
                children = getChildRenderers();
            }

            if (children != null) {
                List<RendererGlyph> lineGlyphs = new ArrayList<>();
                for (IRenderer child : children) {
                    if (child instanceof TextRenderer) {
                        GlyphLine childLine = ((TextRenderer) child).line;
                        for (int i = childLine.start; i < childLine.end; i++) {
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
                    List<int[]> reversedRanges = new ArrayList<>();
                    int initialPos = 0;
                    boolean reversed = false;
                    int offset = 0;
                    while (pos < lineGlyphs.size()) {
                        IRenderer renderer = lineGlyphs.get(pos).renderer;
                        TextRenderer newRenderer = new TextRenderer((TextRenderer) renderer);
                        newRenderer.deleteOwnProperty(Property.REVERSED);
                        children.add(newRenderer);
                        ((TextRenderer) children.get(children.size() - 1)).line = new GlyphLine(((TextRenderer) children.get(children.size() - 1)).line);
                        GlyphLine gl = ((TextRenderer) children.get(children.size() - 1)).line;
                        List<Glyph> replacementGlyphs = new ArrayList<>();
                        while (pos < lineGlyphs.size() && lineGlyphs.get(pos).renderer == renderer) {
                            if (pos < lineGlyphs.size() - 1) {
                                if (reorder[pos] == reorder[pos + 1] + 1) {
                                    reversed = true;
                                } else {
                                    if (reversed) {
                                        List<int[]> reversedRange = new ArrayList<>();
                                        reversedRange.add(new int[]{initialPos - offset, pos - offset});
                                        newRenderer.setProperty(Property.REVERSED, reversedRange);
                                        reversedRanges.add(new int[]{initialPos - offset, pos - offset});
                                        reversed = false;
                                    }
                                    initialPos = pos + 1;
                                }
                            }

                            replacementGlyphs.add(lineGlyphs.get(pos).glyph);
                            pos++;
                        }

                        if (reversed) {
                            List<int[]> reversedRange = new ArrayList<>();
                            reversedRange.add(new int[]{initialPos - offset, pos - 1 - offset});
                            newRenderer.setProperty(Property.REVERSED, reversedRange);
                            reversedRanges.add(new int[]{initialPos - offset, pos - 1 - offset});
                            reversed = false;
                            initialPos = pos;
                        }
                        offset = initialPos;
                        gl.setGlyphs(replacementGlyphs);
                    }
                    if (reversed) {
                        if (children.size() == 1) {
                            offset = 0;
                        }
                        List<int[]> reversedRange = new ArrayList<>();
                        reversedRange.add(new int[]{initialPos - offset, pos - offset - 1});
                        lineGlyphs.get(pos - 1).renderer.setProperty(Property.REVERSED, reversedRange);
                        reversedRanges.add(new int[]{initialPos - offset, pos - 1 - offset});
                    }
                    if (!reversedRanges.isEmpty()) {
                        if (children.size() == 1) {
                            lineGlyphs.get(0).renderer.setProperty(Property.REVERSED, reversedRanges);
                        }
                    }

                    float currentXPos = layoutContext.getArea().getBBox().getLeft();
                    for (IRenderer child : children) {
                        float currentWidth = ((TextRenderer) child).calculateLineWidth();
                        ((TextRenderer) child).occupiedArea.getBBox().setX(currentXPos).setWidth(currentWidth);
                        currentXPos += currentWidth;
                    }
                }

                if (result.getStatus() == LayoutResult.PARTIAL) {
                    LineRenderer overflow = (LineRenderer) result.getOverflowRenderer();
                    if (levels != null) {
                        overflow.levels = new byte[levels.length - lineLevels.length];
                        System.arraycopy(levels, lineLevels.length, overflow.levels, 0, overflow.levels.length);
                    }
                }
            }
        }

        if (anythingPlaced) {
            LineRenderer processed = result.getStatus() == LayoutResult.FULL ? this : (LineRenderer) result.getSplitRenderer();
            processed.adjustChildrenYLine().trimLast();
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
        float ratio = (float) getPropertyAsFloat(Property.SPACING_RATIO);
        float freeWidth = occupiedArea.getBBox().getX() + width -
                getLastChildRenderer().getOccupiedArea().getBBox().getX() - getLastChildRenderer().getOccupiedArea().getBBox().getWidth();
        int numberOfSpaces = getNumberOfSpaces();
        int baseCharsCount = baseCharactersCount();
        float baseFactor = freeWidth / (ratio * numberOfSpaces + (1 - ratio) * (baseCharsCount - 1));
        float wordSpacing = ratio * baseFactor;
        float characterSpacing = (1 - ratio) * baseFactor;

        float lastRightPos = occupiedArea.getBBox().getX();
        for (Iterator<IRenderer> iterator = childRenderers.iterator(); iterator.hasNext(); ) {
            IRenderer child = iterator.next();
            float childX = child.getOccupiedArea().getBBox().getX();
            child.move(lastRightPos - childX, 0);
            childX = lastRightPos;
            if (child instanceof TextRenderer) {
                float childHSCale = (float) ((TextRenderer) child).getPropertyAsFloat(Property.HORIZONTAL_SCALING, 1f);
                child.setProperty(Property.CHARACTER_SPACING, characterSpacing / childHSCale);
                child.setProperty(Property.WORD_SPACING, wordSpacing / childHSCale);
                boolean isLastTextRenderer = !iterator.hasNext();
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
        for (IRenderer renderer: childRenderers) {
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
        overflowRenderer.levels = levels;
        overflowRenderer.addAllProperties(getOwnProperties());

        return new LineRenderer[] {splitRenderer, overflowRenderer};
    }

    protected LineRenderer adjustChildrenYLine() {
        float actualYLine = occupiedArea.getBBox().getY() + occupiedArea.getBBox().getHeight() - maxAscent;
        for (IRenderer renderer : childRenderers) {
            if (renderer instanceof TextRenderer) {
                ((TextRenderer) renderer).moveYLineTo(actualYLine);
            } else if (renderer instanceof ImageRenderer){
                renderer.getOccupiedArea().getBBox().setY(occupiedArea.getBBox().getY()- maxDescent);
            } else {
                renderer.getOccupiedArea().getBBox().setY(occupiedArea.getBBox().getY());
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

    protected boolean containsImage(){
        for (IRenderer renderer : childRenderers){
            if (renderer instanceof ImageRenderer){
                return true;
            }
        }
        return false;
    }

    private IRenderer getLastChildRenderer() {
        return childRenderers.get(childRenderers.size() - 1);
    }

    private TabStop getNextTabStop(float curWidth) {
        NavigableMap<Float, TabStop> tabStops = this.<NavigableMap<Float,TabStop>>getProperty(Property.TAB_STOPS);

        Map.Entry<Float, TabStop> nextTabStopEntry = null;
        TabStop nextTabStop = null;

        if (tabStops != null)
            nextTabStopEntry = tabStops.higherEntry(curWidth);
        if (nextTabStopEntry != null) {
            nextTabStop = nextTabStopEntry.getValue();
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
        childRenderer.setProperty(Property.HEIGHT, maxAscent - maxDescent);
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
                tabWidth = tabStop.getTabPosition() - curWidth - childWidth/2;
                break;
            case ANCHOR:
                float anchorPosition = -1;
                if (nextElementRenderer instanceof TextRenderer)
                    anchorPosition = ((TextRenderer)nextElementRenderer).getTabAnchorCharacterPosition();
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
        tabRenderer.setProperty(Property.HEIGHT, maxAscent - maxDescent);
        return tabWidth;
    }

    private void processDefaultTab(IRenderer tabRenderer, float curWidth, float lineWidth) {
        Float tabDefault = getPropertyAsFloat(Property.TAB_DEFAULT);
        Float tabWidth = tabDefault - curWidth % tabDefault;
        if (curWidth + tabWidth > lineWidth)
            tabWidth = lineWidth - curWidth;
        tabRenderer.setProperty(Property.WIDTH, UnitValue.createPointValue((float)tabWidth));
        tabRenderer.setProperty(Property.HEIGHT, maxAscent - maxDescent);
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
