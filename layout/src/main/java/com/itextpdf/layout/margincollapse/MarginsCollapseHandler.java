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
package com.itextpdf.layout.margincollapse;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.property.FloatPropertyValue;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.renderer.AbstractRenderer;
import com.itextpdf.layout.renderer.BlockFormattingContextUtil;
import com.itextpdf.layout.renderer.BlockRenderer;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.LineRenderer;
import com.itextpdf.layout.renderer.RootRenderer;
import com.itextpdf.layout.renderer.TableRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Rules of the margins collapsing are taken from Mozilla Developer Network:
 * https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Box_Model/Mastering_margin_collapsing
 * See also:
 * https://www.w3.org/TR/CSS2/box.html#collapsing-margins
 */
public class MarginsCollapseHandler {
    private IRenderer renderer;
    private MarginsCollapseInfo collapseInfo;

    private MarginsCollapseInfo childMarginInfo;
    private MarginsCollapseInfo prevChildMarginInfo;
    private int firstNotEmptyKidIndex = 0;

    private int processedChildrenNum = 0;
    private List<IRenderer> rendererChildren = new ArrayList<>();

    // Layout box and collapse info are saved before processing the next kid, in order to be able to restore it in case
    // the next kid is not placed. These values are not null only between startChildMarginsHandling and endChildMarginsHandling calls.
    private Rectangle backupLayoutBox;
    private MarginsCollapseInfo backupCollapseInfo;

    private boolean lastKidCollapsedAfterHasClearanceApplied;

    public MarginsCollapseHandler(IRenderer renderer, MarginsCollapseInfo marginsCollapseInfo) {
        this.renderer = renderer;
        this.collapseInfo = marginsCollapseInfo != null ? marginsCollapseInfo : new MarginsCollapseInfo();
    }

    public void processFixedHeightAdjustment(float heightDelta) {
        collapseInfo.setBufferSpaceOnTop(collapseInfo.getBufferSpaceOnTop() + heightDelta);
        collapseInfo.setBufferSpaceOnBottom(collapseInfo.getBufferSpaceOnBottom() + heightDelta);
    }

    public MarginsCollapseInfo startChildMarginsHandling(IRenderer child, Rectangle layoutBox) {
        if (backupLayoutBox != null) {
            // this should happen only if previous kid was floated
            restoreLayoutBoxAfterFailedLayoutAttempt(layoutBox);
            removeRendererChild(--processedChildrenNum);
            childMarginInfo = null;
        }

        rendererChildren.add(child);

        int childIndex = processedChildrenNum++;

        // If renderer is floated, prepare layout box as if it was inline,
        // however it will be restored from backup when next kid processing will start.
        boolean childIsBlockElement = !rendererIsFloated(child) && isBlockElement(child);

        backupLayoutBox = layoutBox.clone();
        backupCollapseInfo = new MarginsCollapseInfo();
        collapseInfo.copyTo(backupCollapseInfo);

        prepareBoxForLayoutAttempt(layoutBox, childIndex, childIsBlockElement);

        if (childIsBlockElement) {
            childMarginInfo = createMarginsInfoForBlockChild(childIndex);
        }
        return this.childMarginInfo;
    }

    public void applyClearance(float clearHeightCorrection) {
        // Actually, clearance is applied only in case margins were not enough,
        // however I wasn't able to notice difference in browsers behaviour.
        // Also, iText behaviour concerning margins self collapsing and clearance differs from browsers in some cases.
        collapseInfo.setClearanceApplied(true);
        collapseInfo.getCollapseBefore().joinMargin(clearHeightCorrection);
    }

    private MarginsCollapseInfo createMarginsInfoForBlockChild(int childIndex) {
        boolean ignoreChildTopMargin = false;
        // always assume that current child might be the last on this area
        boolean ignoreChildBottomMargin = lastChildMarginAdjoinedToParent(renderer);
        if (childIndex == firstNotEmptyKidIndex) {
            ignoreChildTopMargin = firstChildMarginAdjoinedToParent(renderer);
        }

        MarginsCollapse childCollapseBefore;
        if (childIndex == 0) {
            MarginsCollapse parentCollapseBefore = collapseInfo.getCollapseBefore();
            childCollapseBefore = ignoreChildTopMargin ? parentCollapseBefore : new MarginsCollapse();
        } else {
            MarginsCollapse prevChildCollapseAfter = prevChildMarginInfo != null ? prevChildMarginInfo.getOwnCollapseAfter() : null;
            childCollapseBefore = prevChildCollapseAfter != null ? prevChildCollapseAfter : new MarginsCollapse();
        }

        MarginsCollapse parentCollapseAfter = collapseInfo.getCollapseAfter().clone();
        MarginsCollapse childCollapseAfter = ignoreChildBottomMargin ? parentCollapseAfter : new MarginsCollapse();
        MarginsCollapseInfo childMarginsInfo = new MarginsCollapseInfo(ignoreChildTopMargin, ignoreChildBottomMargin, childCollapseBefore, childCollapseAfter);
        if (ignoreChildTopMargin && childIndex == firstNotEmptyKidIndex) {
            childMarginsInfo.setBufferSpaceOnTop(collapseInfo.getBufferSpaceOnTop());
        }
        if (ignoreChildBottomMargin) {
            childMarginsInfo.setBufferSpaceOnBottom(collapseInfo.getBufferSpaceOnBottom());
        }
        return childMarginsInfo;
    }

    /**
     * This method shall be called after child occupied area is included into parent occupied area.
     */
    public void endChildMarginsHandling(Rectangle layoutBox) {
        int childIndex = processedChildrenNum - 1;
        if (rendererIsFloated(getRendererChild(childIndex))) {
            return;
        }

        if (childMarginInfo != null) {
            if (firstNotEmptyKidIndex == childIndex && childMarginInfo.isSelfCollapsing()) {
                firstNotEmptyKidIndex = childIndex + 1;
            }
            collapseInfo.setSelfCollapsing(collapseInfo.isSelfCollapsing() && childMarginInfo.isSelfCollapsing());

            lastKidCollapsedAfterHasClearanceApplied = childMarginInfo.isSelfCollapsing() && childMarginInfo.isClearanceApplied();
        } else {
            lastKidCollapsedAfterHasClearanceApplied = false;
            collapseInfo.setSelfCollapsing(false);
        }

        if (prevChildMarginInfo != null) {
            fixPrevChildOccupiedArea(childIndex);

            updateCollapseBeforeIfPrevKidIsFirstAndSelfCollapsed(prevChildMarginInfo.getOwnCollapseAfter());
        }

        if (firstNotEmptyKidIndex == childIndex && firstChildMarginAdjoinedToParent(renderer)) {
            if (!collapseInfo.isSelfCollapsing()) {
                getRidOfCollapseArtifactsAtopOccupiedArea();
                if (childMarginInfo != null) {
                    processUsedChildBufferSpaceOnTop(layoutBox);
                }
            }
        }

        prevChildMarginInfo = childMarginInfo;
        childMarginInfo = null;

        backupLayoutBox = null;
        backupCollapseInfo = null;
    }

    public void startMarginsCollapse(Rectangle parentBBox) {
        collapseInfo.getCollapseBefore().joinMargin(getModelTopMargin(renderer));
        collapseInfo.getCollapseAfter().joinMargin(getModelBottomMargin(renderer));

        if (!firstChildMarginAdjoinedToParent(renderer)) {
            float topIndent = collapseInfo.getCollapseBefore().getCollapsedMarginsSize();
            applyTopMargin(parentBBox, topIndent);
        }
        if (!lastChildMarginAdjoinedToParent(renderer)) {
            float bottomIndent = collapseInfo.getCollapseAfter().getCollapsedMarginsSize();
            applyBottomMargin(parentBBox, bottomIndent);
        }

        // ignore current margins for now
        ignoreModelTopMargin(renderer);
        ignoreModelBottomMargin(renderer);
    }

    public void endMarginsCollapse(Rectangle layoutBox) {
        if (backupLayoutBox != null) {
            restoreLayoutBoxAfterFailedLayoutAttempt(layoutBox);
        }

        if (prevChildMarginInfo != null) {
            updateCollapseBeforeIfPrevKidIsFirstAndSelfCollapsed(prevChildMarginInfo.getCollapseAfter());
        }

        boolean couldBeSelfCollapsing = MarginsCollapseHandler.marginsCouldBeSelfCollapsing(renderer) && !lastKidCollapsedAfterHasClearanceApplied;
        boolean blockHasNoKidsWithContent = collapseInfo.isSelfCollapsing();
        if (firstChildMarginAdjoinedToParent(renderer)) {
            if (blockHasNoKidsWithContent && !couldBeSelfCollapsing) {
                addNotYetAppliedTopMargin(layoutBox);
            }
        }
        collapseInfo.setSelfCollapsing(collapseInfo.isSelfCollapsing() && couldBeSelfCollapsing);

        if (!blockHasNoKidsWithContent && lastKidCollapsedAfterHasClearanceApplied) {
            applySelfCollapsedKidMarginWithClearance(layoutBox);
        }

        MarginsCollapse ownCollapseAfter;
        boolean lastChildMarginJoinedToParent = prevChildMarginInfo != null && prevChildMarginInfo.isIgnoreOwnMarginBottom() && !lastKidCollapsedAfterHasClearanceApplied;
        if (lastChildMarginJoinedToParent) {
            ownCollapseAfter = prevChildMarginInfo.getOwnCollapseAfter();
        } else {
            ownCollapseAfter = new MarginsCollapse();
        }
        ownCollapseAfter.joinMargin(getModelBottomMargin(renderer));
        collapseInfo.setOwnCollapseAfter(ownCollapseAfter);

        if (collapseInfo.isSelfCollapsing()) {
            if (prevChildMarginInfo != null) {
                collapseInfo.setCollapseAfter(prevChildMarginInfo.getCollapseAfter());
            } else {
                collapseInfo.getCollapseAfter().joinMargin(collapseInfo.getCollapseBefore());
                collapseInfo.getOwnCollapseAfter().joinMargin(collapseInfo.getCollapseBefore());
            }
            if (!collapseInfo.isIgnoreOwnMarginBottom() && !collapseInfo.isIgnoreOwnMarginTop()) {
                float collapsedMargins = collapseInfo.getCollapseAfter().getCollapsedMarginsSize();
                overrideModelBottomMargin(renderer, collapsedMargins);
            }
        } else {
            MarginsCollapse marginsCollapseBefore = collapseInfo.getCollapseBefore();
            if (!collapseInfo.isIgnoreOwnMarginTop()) {
                float collapsedMargins = marginsCollapseBefore.getCollapsedMarginsSize();
                overrideModelTopMargin(renderer, collapsedMargins);
            }

            if (lastChildMarginJoinedToParent) {
                collapseInfo.setCollapseAfter(prevChildMarginInfo.getCollapseAfter());
            }
            if (!collapseInfo.isIgnoreOwnMarginBottom()) {
                float collapsedMargins = collapseInfo.getCollapseAfter().getCollapsedMarginsSize();
                overrideModelBottomMargin(renderer, collapsedMargins);
            }
        }

        if (lastChildMarginAdjoinedToParent(renderer) && (prevChildMarginInfo != null || blockHasNoKidsWithContent)) {
            // Adjust layout box here in order to make it represent the available area left.
            float collapsedMargins = collapseInfo.getCollapseAfter().getCollapsedMarginsSize();

            // May be in case of self-collapsed margins it would make more sense to apply this value to topMargin,
            // because that way the layout box would represent the area left after the empty self-collapsed block, not
            // before it. However at the same time any considerations about the layout (i.e. content) area in case
            // of the self-collapsed block seem to be invalid, because self-collapsed block shall have content area
            // of zero height.
            applyBottomMargin(layoutBox, collapsedMargins);
        }

    }

    private void updateCollapseBeforeIfPrevKidIsFirstAndSelfCollapsed(MarginsCollapse collapseAfter) {
        if (prevChildMarginInfo.isSelfCollapsing() && prevChildMarginInfo.isIgnoreOwnMarginTop()) {
            // prevChildMarginInfo.isIgnoreOwnMarginTop() is true only if it's the first kid and is adjoined to parent margin
            collapseInfo.getCollapseBefore().joinMargin(collapseAfter);
        }
    }

    private void prepareBoxForLayoutAttempt(Rectangle layoutBox, int childIndex, boolean childIsBlockElement) {
        if (prevChildMarginInfo != null) {
            boolean prevChildHasAppliedCollapseAfter = !prevChildMarginInfo.isIgnoreOwnMarginBottom()
                    && (!prevChildMarginInfo.isSelfCollapsing() || !prevChildMarginInfo.isIgnoreOwnMarginTop());
            if (prevChildHasAppliedCollapseAfter) {
                layoutBox.setHeight(layoutBox.getHeight() + prevChildMarginInfo.getCollapseAfter().getCollapsedMarginsSize());
            }

            boolean prevChildCanApplyCollapseAfter = !prevChildMarginInfo.isSelfCollapsing() || !prevChildMarginInfo.isIgnoreOwnMarginTop();
            if (!childIsBlockElement && prevChildCanApplyCollapseAfter) {
                MarginsCollapse ownCollapseAfter = prevChildMarginInfo.getOwnCollapseAfter();
                float ownCollapsedMargins = ownCollapseAfter == null ? 0 : ownCollapseAfter.getCollapsedMarginsSize();
                layoutBox.setHeight(layoutBox.getHeight() - ownCollapsedMargins);
            }
        } else if (childIndex > firstNotEmptyKidIndex) {
            if (lastChildMarginAdjoinedToParent(renderer)) {
                // restore layout box after inline element
                float bottomIndent = collapseInfo.getCollapseAfter().getCollapsedMarginsSize() - collapseInfo.getUsedBufferSpaceOnBottom(); // used space shall be always less or equal to collapsedMarginAfter size
                collapseInfo.setBufferSpaceOnBottom(collapseInfo.getBufferSpaceOnBottom() + collapseInfo.getUsedBufferSpaceOnBottom());
                collapseInfo.setUsedBufferSpaceOnBottom(0);
                layoutBox.setY(layoutBox.getY() - bottomIndent);
                layoutBox.setHeight(layoutBox.getHeight() + bottomIndent);
            }

        }

        if (!childIsBlockElement) {
            if (childIndex == firstNotEmptyKidIndex && firstChildMarginAdjoinedToParent(renderer)) {
                float topIndent = collapseInfo.getCollapseBefore().getCollapsedMarginsSize();
                applyTopMargin(layoutBox, topIndent);
            }
            if (lastChildMarginAdjoinedToParent(renderer)) { // if not adjoined - bottom margin have been already applied on startMarginsCollapse
                float bottomIndent = collapseInfo.getCollapseAfter().getCollapsedMarginsSize();
                applyBottomMargin(layoutBox, bottomIndent);
            }
        }
    }

    private void restoreLayoutBoxAfterFailedLayoutAttempt(Rectangle layoutBox) {
        layoutBox.setX(backupLayoutBox.getX()).setY(backupLayoutBox.getY())
                .setWidth(backupLayoutBox.getWidth()).setHeight(backupLayoutBox.getHeight());
        backupCollapseInfo.copyTo(collapseInfo);

        backupLayoutBox = null;
        backupCollapseInfo = null;
    }

    private void applyTopMargin(Rectangle box, float topIndent) {
        float bufferLeftoversOnTop = collapseInfo.getBufferSpaceOnTop() - topIndent;
        float usedTopBuffer = bufferLeftoversOnTop > 0 ? topIndent : collapseInfo.getBufferSpaceOnTop();
        collapseInfo.setUsedBufferSpaceOnTop(usedTopBuffer);
        subtractUsedTopBufferFromBottomBuffer(usedTopBuffer);

        if (bufferLeftoversOnTop >= 0) {
            collapseInfo.setBufferSpaceOnTop(bufferLeftoversOnTop);
            box.moveDown(topIndent);
        } else {
            box.moveDown(collapseInfo.getBufferSpaceOnTop());
            collapseInfo.setBufferSpaceOnTop(0);
            box.setHeight(box.getHeight() + bufferLeftoversOnTop);
        }
    }

    private void applyBottomMargin(Rectangle box, float bottomIndent) {
        // Here we don't subtract used buffer space from topBuffer, because every kid is assumed to be
        // the last one on the page, and so every kid always has parent's bottom buffer, however only the true last kid
        // uses it for real. Also, bottom margin are always applied after top margins, so it doesn't matter anyway.

        float bottomIndentLeftovers = bottomIndent - collapseInfo.getBufferSpaceOnBottom();
        if (bottomIndentLeftovers < 0) {
            collapseInfo.setUsedBufferSpaceOnBottom(bottomIndent);
            collapseInfo.setBufferSpaceOnBottom(-bottomIndentLeftovers);
        } else {
            collapseInfo.setUsedBufferSpaceOnBottom(collapseInfo.getBufferSpaceOnBottom());
            collapseInfo.setBufferSpaceOnBottom(0);
            box.setY(box.getY() + bottomIndentLeftovers);
            box.setHeight(box.getHeight() - bottomIndentLeftovers);
        }
    }

    private void processUsedChildBufferSpaceOnTop(Rectangle layoutBox) {
        float childUsedBufferSpaceOnTop = childMarginInfo.getUsedBufferSpaceOnTop();
        if (childUsedBufferSpaceOnTop > 0) {
            if (childUsedBufferSpaceOnTop > collapseInfo.getBufferSpaceOnTop()) {
                childUsedBufferSpaceOnTop = collapseInfo.getBufferSpaceOnTop();
            }

            collapseInfo.setBufferSpaceOnTop(collapseInfo.getBufferSpaceOnTop() - childUsedBufferSpaceOnTop);
            collapseInfo.setUsedBufferSpaceOnTop(childUsedBufferSpaceOnTop);
            // usage of top buffer space on child is expressed by moving layout box down instead of making it smaller,
            // so in order to process next kids correctly, we need to move parent layout box also
            layoutBox.moveDown(childUsedBufferSpaceOnTop);

            subtractUsedTopBufferFromBottomBuffer(childUsedBufferSpaceOnTop);
        }
    }

    private void subtractUsedTopBufferFromBottomBuffer(float usedTopBuffer) {
        if (collapseInfo.getBufferSpaceOnTop() > collapseInfo.getBufferSpaceOnBottom()) {
            float bufferLeftoversOnTop = collapseInfo.getBufferSpaceOnTop() - usedTopBuffer;
            if (bufferLeftoversOnTop < collapseInfo.getBufferSpaceOnBottom()) {
                collapseInfo.setBufferSpaceOnBottom(bufferLeftoversOnTop);
            }
        } else {
            collapseInfo.setBufferSpaceOnBottom(collapseInfo.getBufferSpaceOnBottom() - usedTopBuffer);
        }
    }

    private void fixPrevChildOccupiedArea(int childIndex) {
        IRenderer prevRenderer = getRendererChild(childIndex - 1);

        Rectangle bBox = prevRenderer.getOccupiedArea().getBBox();

        boolean prevChildHasAppliedCollapseAfter = !prevChildMarginInfo.isIgnoreOwnMarginBottom()
                && (!prevChildMarginInfo.isSelfCollapsing() || !prevChildMarginInfo.isIgnoreOwnMarginTop());

        if (prevChildHasAppliedCollapseAfter) {
            float bottomMargin = prevChildMarginInfo.getCollapseAfter().getCollapsedMarginsSize();
            bBox.setHeight(bBox.getHeight() - bottomMargin);
            bBox.moveUp(bottomMargin);
            ignoreModelBottomMargin(prevRenderer);
        }

        boolean isNotBlockChild = !isBlockElement(getRendererChild(childIndex));
        boolean prevChildCanApplyCollapseAfter = !prevChildMarginInfo.isSelfCollapsing() || !prevChildMarginInfo.isIgnoreOwnMarginTop();
        if (isNotBlockChild && prevChildCanApplyCollapseAfter) {
            float ownCollapsedMargins = prevChildMarginInfo.getOwnCollapseAfter().getCollapsedMarginsSize();
            bBox.setHeight(bBox.getHeight() + ownCollapsedMargins);
            bBox.moveDown(ownCollapsedMargins);
            overrideModelBottomMargin(prevRenderer, ownCollapsedMargins);
        }
    }

    private void addNotYetAppliedTopMargin(Rectangle layoutBox) {
        // normally, space for margins is added when content is met, however if all kids were self-collapsing (i.e.
        // had no content) or if there were no kids, we need to add it when no more adjoining margins will be met
        float indentTop = collapseInfo.getCollapseBefore().getCollapsedMarginsSize();
        renderer.getOccupiedArea().getBBox().moveDown(indentTop);

        // Even though all kids have been already drawn, we still need to adjust layout box here
        // in order to make it represent the available area for element content (e.g. needed for fixed height elements).
        applyTopMargin(layoutBox, indentTop);
    }

    // Actually, this should be taken into account when layouting a kid and assuming it's the last one on page.
    // However it's not feasible, because
    // - before kid layout, we don't know if it's self-collapsing or if we have applied clearance to it;
    // - this might be very difficult to correctly change kid and parent occupy area, based on if it's
    // the last kid on page or not;
    // - in the worst case scenario (which is kinda rare) page last kid (self-collapsed and with clearance)
    // margin applying would result in margins page overflow, which will not be visible except
    // margins would be visually less than expected.
    private void applySelfCollapsedKidMarginWithClearance(Rectangle layoutBox) {
        // Self-collapsed kid margin with clearance will not be applied to parent top margin
        // if parent is not self-collapsing. It's self-collapsing kid, thus we just can
        // add this area to occupied area of parent.
        float clearedKidMarginWithClearance = prevChildMarginInfo.getOwnCollapseAfter().getCollapsedMarginsSize();
        renderer.getOccupiedArea().getBBox().
                increaseHeight(clearedKidMarginWithClearance)
                .moveDown(clearedKidMarginWithClearance);

        layoutBox.decreaseHeight(clearedKidMarginWithClearance);
    }

    private IRenderer getRendererChild(int index) {
        return rendererChildren.get(index);
    }

    private IRenderer removeRendererChild(int index) {
        return rendererChildren.remove(index);
    }

    private void getRidOfCollapseArtifactsAtopOccupiedArea() {
        Rectangle bBox = renderer.getOccupiedArea().getBBox();
        bBox.decreaseHeight(collapseInfo.getCollapseBefore().getCollapsedMarginsSize());
    }

    private static boolean marginsCouldBeSelfCollapsing(IRenderer renderer) {
        return !(renderer instanceof TableRenderer)
                && !rendererIsFloated(renderer)
                && !hasBottomBorders(renderer) && !hasTopBorders(renderer)
                && !hasBottomPadding(renderer) && !hasTopPadding(renderer) && !hasPositiveHeight(renderer)
                // inline block
                && !(isBlockElement(renderer) && renderer instanceof AbstractRenderer && ((AbstractRenderer) renderer).getParent() instanceof LineRenderer);
    }

    private static boolean firstChildMarginAdjoinedToParent(IRenderer parent) {
        return !BlockFormattingContextUtil.isRendererCreateBfc(parent)
                && !(parent instanceof TableRenderer)
                && !hasTopBorders(parent) && !hasTopPadding(parent);
    }

    private static boolean lastChildMarginAdjoinedToParent(IRenderer parent) {
        return !BlockFormattingContextUtil.isRendererCreateBfc(parent)
                && !(parent instanceof TableRenderer)
                && !hasBottomBorders(parent) && !hasBottomPadding(parent) && !hasHeightProp(parent);
    }

    private static boolean isBlockElement(IRenderer renderer) {
        return renderer instanceof BlockRenderer || renderer instanceof TableRenderer;
    }

    private static boolean hasHeightProp(IRenderer renderer) {
        // in mozilla and chrome height always prevents margins collapse in all cases.
        return renderer.getModelElement().hasProperty(Property.HEIGHT);

        // "min-height" property affects margins collapse differently in chrome and mozilla. While in chrome, this property
        // seems to not have any effect on collapsing margins at all (child margins collapse with parent margins even if
        // there is a considerable space between them due to the min-height property on parent), mozilla behaves better
        // and collapse happens only in case min-height of parent is less than actual height of the content and therefore
        // collapse really should happen. However even in mozilla, if parent has min-height which is a little bigger then
        // it's content actual height and margin collapse doesn't happen, in this case the child's margin is not shown fully however.
        //
        // || styles.containsKey(CssConstants.MIN_HEIGHT)

        // "max-height" doesn't seem to affect margins collapse in any way at least in chrome.
        // In mozilla it affects collapsing when parent's max-height is less than children actual height,
        // in this case collapse doesn't happen. However, at the moment in iText we won't show anything at all if
        // kid's height is bigger than parent's max-height, therefore this logic is irrelevant now anyway.
        //
        // || (includingMaxHeight && styles.containsKey(CssConstants.MAX_HEIGHT));
    }

    private static boolean hasPositiveHeight(IRenderer renderer) {
        float height = renderer.getOccupiedArea().getBBox().getHeight();

        if (height == 0) {
            UnitValue heightPropVal = renderer.<UnitValue>getProperty(Property.HEIGHT);
            UnitValue minHeightPropVal = renderer.<UnitValue>getProperty(Property.MIN_HEIGHT);
            height = minHeightPropVal != null
                    ? (float) minHeightPropVal.getValue()
                    : heightPropVal != null ? (float) heightPropVal.getValue() : 0;
        }
        return height > 0;
    }

    private static boolean hasTopPadding(IRenderer renderer) {
        UnitValue padding = renderer.getModelElement().<UnitValue>getProperty(Property.PADDING_TOP);
        if (null != padding && !padding.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(MarginsCollapseHandler.class);
            logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.PADDING_TOP));
        }
        return padding != null && padding.getValue() > 0;
    }

    private static boolean hasBottomPadding(IRenderer renderer) {
        UnitValue padding = renderer.getModelElement().<UnitValue>getProperty(Property.PADDING_BOTTOM);
        if (null != padding && !padding.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(MarginsCollapseHandler.class);
            logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.PADDING_BOTTOM));
        }
        return padding != null && padding.getValue() > 0;
    }

    private static boolean hasTopBorders(IRenderer renderer) {
        IPropertyContainer modelElement = renderer.getModelElement();
        return modelElement.hasProperty(Property.BORDER_TOP) || modelElement.hasProperty(Property.BORDER);
    }

    private static boolean hasBottomBorders(IRenderer renderer) {
        IPropertyContainer modelElement = renderer.getModelElement();
        return modelElement.hasProperty(Property.BORDER_BOTTOM) || modelElement.hasProperty(Property.BORDER);
    }

    private static boolean rendererIsFloated(IRenderer renderer) {
        if (renderer == null) {
            return false;
        }
        FloatPropertyValue floatPropertyValue = renderer.<FloatPropertyValue>getProperty(Property.FLOAT);
        return floatPropertyValue != null && !floatPropertyValue.equals(FloatPropertyValue.NONE);
    }

    private static float getModelTopMargin(IRenderer renderer) {
        UnitValue marginUV = renderer.getModelElement().<UnitValue>getProperty(Property.MARGIN_TOP);
        if (null != marginUV && !marginUV.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(MarginsCollapseHandler.class);
            logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.MARGIN_TOP));
        }
        // TODO Concerning "renderer instanceof CellRenderer" check: may be try to apply more general solution in future
        return marginUV != null && !(renderer instanceof CellRenderer) ? marginUV.getValue() : 0;
    }

    private static void ignoreModelTopMargin(IRenderer renderer) {
        renderer.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(0f));
    }

    private static void overrideModelTopMargin(IRenderer renderer, float collapsedMargins) {
        renderer.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(collapsedMargins));
    }

    private static float getModelBottomMargin(IRenderer renderer) {
        UnitValue marginUV = renderer.getModelElement().<UnitValue>getProperty(Property.MARGIN_BOTTOM);
        if (null != marginUV && !marginUV.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(MarginsCollapseHandler.class);
            logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.MARGIN_TOP));
        }
        // TODO Concerning "renderer instanceof CellRenderer" check: may be try to apply more general solution in future
        return marginUV != null && !(renderer instanceof CellRenderer) ? marginUV.getValue() : 0;
    }

    private static void ignoreModelBottomMargin(IRenderer renderer) {
        renderer.setProperty(Property.MARGIN_BOTTOM, UnitValue.createPointValue(0f));
    }

    private static void overrideModelBottomMargin(IRenderer renderer, float collapsedMargins) {
        renderer.setProperty(Property.MARGIN_BOTTOM, UnitValue.createPointValue(collapsedMargins));
    }
}
