package com.itextpdf.model.renderer;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.model.Property;
import com.itextpdf.model.element.TabStop;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;
import com.itextpdf.model.layout.LineLayoutResult;
import com.itextpdf.model.layout.TextLayoutResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;

public class LineRenderer extends AbstractRenderer {

    protected float maxAscent;
    protected float maxDescent;

    @Override
    public LineLayoutResult layout(LayoutContext layoutContext) {
        Rectangle layoutBox = layoutContext.getArea().getBBox().clone();
        occupiedArea = new LayoutArea(layoutContext.getArea().getPageNumber(), layoutBox.clone().moveDown(-layoutBox.getHeight()).setHeight(0));

        float curWidth = 0;
        maxAscent = 0;
        maxDescent = 0;
        int childPos = 0;

        boolean anythingPlaced = false;
        TabStop nextTabStop = null;

        while (childPos < childRenderers.size()) {
            IRenderer childRenderer = childRenderers.get(childPos);
            LayoutResult childResult;
            Rectangle bbox = new Rectangle(layoutBox.getX() + curWidth, layoutBox.getY(), layoutBox.getWidth() - curWidth, layoutBox.getHeight());

            if (childRenderer instanceof TextRenderer) {
                // Delete these properties in case of relayout. We might have applied them during justify().
                childRenderer.deleteProperty(Property.CHARACTER_SPACING);
                childRenderer.deleteProperty(Property.WORD_SPACING);
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

            if (nextTabStop != null && nextTabStop.getTabAlignment() == Property.TabAlignment.ANCHOR
                                               && childRenderer instanceof TextRenderer) {
                childRenderer.setProperty(Property.TAB_ANCHOR, nextTabStop.getTabAnchor());
            }

            childResult = childRenderer.layout(new LayoutContext(new LayoutArea(layoutContext.getArea().getPageNumber(), bbox)));

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

                    split[1].childRenderers.add(childResult.getOverflowRenderer());
                    split[1].childRenderers.addAll(childRenderers.subList(childPos + 1, childRenderers.size()));
                }

                split[0].adjustChildrenYLine().trimLast();
                LineLayoutResult result = new LineLayoutResult(anythingPlaced ? LayoutResult.PARTIAL : LayoutResult.NOTHING, occupiedArea, split[0], split[1]);
                if (childResult.getStatus() == LayoutResult.PARTIAL && childResult instanceof TextLayoutResult && ((TextLayoutResult) childResult).isSplitForcedByNewline())
                    result.setSplitForcedByNewline(true);
                return result;
            } else {
                anythingPlaced = true;
                childPos++;
            }
        }

        if (anythingPlaced) {
            adjustChildrenYLine().trimLast();
            return new LineLayoutResult(LayoutResult.FULL, occupiedArea, null, null);
        } else {
            return new LineLayoutResult(LayoutResult.NOTHING, occupiedArea, null, this);
        }
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

    public float getLeadingValue(Property.Leading leading) {
        switch (leading.getType()) {
            case Property.Leading.FIXED:
                return leading.getValue();
            case Property.Leading.MULTIPLIED:
                return occupiedArea.getBBox().getHeight() * leading.getValue();
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    protected Float getFirstYLineRecursively() {
        return getYLine();
    }

    protected void justify(float width) {
        float ratio = getPropertyAsFloat(Property.SPACING_RATIO);
        float freeWidth = occupiedArea.getBBox().getX() + width -
                getLastChildRenderer().getOccupiedArea().getBBox().getX() - getLastChildRenderer().getOccupiedArea().getBBox().getWidth();
        int numberOfSpaces = getNumberOfSpaces();
        int lineLength = length();
        float baseFactor = freeWidth / (ratio * numberOfSpaces + (1 - ratio) * (lineLength - 1));
        float wordSpacing = ratio * baseFactor;
        float characterSpacing = (1 - ratio) * baseFactor;

        float lastRightPos = occupiedArea.getBBox().getX();
        for (Iterator<IRenderer> iterator = childRenderers.iterator(); iterator.hasNext(); ) {
            IRenderer child = iterator.next();
            float childX = child.getOccupiedArea().getBBox().getX();
            child.move(lastRightPos - childX, 0);
            childX = lastRightPos;
            if (child instanceof TextRenderer) {
                float childHSCale = child.getProperty(Property.HORIZONTAL_SCALING);
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

    protected LineRenderer createSplitRenderer() {
        return new LineRenderer();
    }

    protected LineRenderer createOverflowRenderer() {
        return new LineRenderer();
    }

    protected LineRenderer[] split() {
        LineRenderer splitRenderer = createSplitRenderer();
        splitRenderer.occupiedArea = occupiedArea.clone();
        splitRenderer.parent = parent;
        splitRenderer.maxAscent = maxAscent;
        splitRenderer.maxDescent = maxDescent;

        LineRenderer overflowRenderer = createOverflowRenderer();
        overflowRenderer.parent = parent;

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
        NavigableMap<Float, TabStop> tabStops = getProperty(Property.TAB_STOPS);

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
        childRenderer.setProperty(Property.WIDTH, Property.UnitValue.createPointValue(nextTabStop.getTabPosition() - curWidth));
        childRenderer.setProperty(Property.HEIGHT, maxAscent - maxDescent);
        if (nextTabStop.getTabAlignment() == Property.TabAlignment.LEFT) {
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

        tabRenderer.setProperty(Property.WIDTH, Property.UnitValue.createPointValue(tabWidth));
        tabRenderer.setProperty(Property.HEIGHT, maxAscent - maxDescent);
        return tabWidth;
    }

    private void processDefaultTab(IRenderer tabRenderer, float curWidth, float lineWidth) {
        Float tabDefault = getPropertyAsFloat(Property.TAB_DEFAULT);
        Float tabWidth = tabDefault - curWidth % tabDefault;
        if (curWidth + tabWidth > lineWidth)
            tabWidth = lineWidth - curWidth;
        tabRenderer.setProperty(Property.WIDTH, Property.UnitValue.createPointValue(tabWidth));
        tabRenderer.setProperty(Property.HEIGHT, maxAscent - maxDescent);
    }
}
