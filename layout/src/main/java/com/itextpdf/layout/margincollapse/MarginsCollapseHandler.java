/*
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
package com.itextpdf.layout.margincollapse;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.renderer.BlockRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.RootRenderer;
import com.itextpdf.layout.renderer.TableRenderer;
import java.util.ArrayList;
import java.util.List;

/**
 * Rules of the margins collapsing are taken from Mozilla Developer Network:
 * https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Box_Model/Mastering_margin_collapsing
 */
public class MarginsCollapseHandler {
    private IRenderer renderer;
    private MarginsCollapseInfo collapseInfo;

    private MarginsCollapseInfo childMarginInfo;
    private MarginsCollapseInfo prevChildMarginInfo;
    private int firstNotEmptyKidIndex = 0;

    private int processedChildrenNum = 0;
    private List<IRenderer> rendererChildren;

    public MarginsCollapseHandler(IRenderer renderer, MarginsCollapseInfo marginsCollapseInfo) {
        this.renderer = renderer;
        this.collapseInfo = marginsCollapseInfo != null ? marginsCollapseInfo : new MarginsCollapseInfo();
    }

    public void processFixedHeightAdjustment(float heightDelta) {
        collapseInfo.setBufferSpace(collapseInfo.getBufferSpace() + heightDelta);
    }

    public MarginsCollapseInfo startChildMarginsHandling(IRenderer child, Rectangle layoutBox) {
        if (rendererChildren == null) {
            rendererChildren = new ArrayList<>();
        }
        rendererChildren.add(child);
        return startChildMarginsHandling(processedChildrenNum++, layoutBox);
    }

    public MarginsCollapseInfo startChildMarginsHandling(int childIndex, Rectangle layoutBox) {
        prevChildMarginInfo = childMarginInfo;
        childMarginInfo = null;

        IRenderer child = getRendererChild(childIndex);
        boolean childIsBlockElement = isBlockElement(child);

        prepareBoxForLayoutAttempt(layoutBox, childIndex, childIsBlockElement);

        if (childIsBlockElement) {
            childMarginInfo = createMarginsInfoForBlockChild(childIndex);
        }
        return this.childMarginInfo;
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
            childMarginsInfo.setBufferSpace(collapseInfo.getBufferSpace());
        }
        return childMarginsInfo;
    }

    public void endChildMarginsHandling() {
        endChildMarginsHandling(processedChildrenNum - 1, null);
    }

    public void endChildMarginsHandling(int childIndex, Rectangle layoutBox) {
        if (childMarginInfo != null) {
            if (firstNotEmptyKidIndex == childIndex && childMarginInfo.isSelfCollapsing()) {
                firstNotEmptyKidIndex = childIndex + 1;
            }
            collapseInfo.setSelfCollapsing(collapseInfo.isSelfCollapsing() && childMarginInfo.isSelfCollapsing());
        } else {
            collapseInfo.setSelfCollapsing(false);
        }

        if (firstNotEmptyKidIndex == childIndex && firstChildMarginAdjoinedToParent(renderer)) {
            if (!collapseInfo.isSelfCollapsing()) {
                getRidOfCollapseArtifactsAtopOccupiedArea();
                if (childMarginInfo != null) {
                    float buffSpaceDiff = collapseInfo.getBufferSpace() - childMarginInfo.getBufferSpace();
                    if (buffSpaceDiff > 0) {
                        layoutBox.moveDown(buffSpaceDiff);
                    }
                }
            }
        }

        if (prevChildMarginInfo != null) {
            fixPrevChildOccupiedArea(childIndex);

            if (prevChildMarginInfo.isSelfCollapsing() && prevChildMarginInfo.isIgnoreOwnMarginTop()) {
                collapseInfo.getCollapseBefore().joinMargin(prevChildMarginInfo.getOwnCollapseAfter());
            }
        }

        prevChildMarginInfo = null; // a sign that last kid processing finished successfully
    }

    public void startMarginsCollapse(Rectangle parentBBox) {
        collapseInfo.getCollapseBefore().joinMargin(getModelTopMargin(renderer));
        collapseInfo.getCollapseAfter().joinMargin(getModelBottomMargin(renderer));

        if (!firstChildMarginAdjoinedToParent(renderer)) {
            float topIndent = collapseInfo.getCollapseBefore().getCollapsedMarginsSize();
            adjustBoxPosAndHeight(parentBBox, topIndent);
        }
        if (!lastChildMarginAdjoinedToParent(renderer)) {
            float bottomIndent = collapseInfo.getCollapseAfter().getCollapsedMarginsSize();
            applyBottomMargin(parentBBox, bottomIndent);
        }

        // ignore current margins for now
        ignoreModelTopMargin(renderer);
        ignoreModelBottomMargin(renderer);
    }

    public void endMarginsCollapse() {
        if (prevChildMarginInfo != null) {
            // last kid processing finished with NOTHING
            childMarginInfo = prevChildMarginInfo;
        }

        if (childMarginInfo != null && childMarginInfo.isSelfCollapsing() && childMarginInfo.isIgnoreOwnMarginTop()) {
            collapseInfo.getCollapseBefore().joinMargin(childMarginInfo.getCollapseAfter());
        }

        boolean couldBeSelfCollapsing = MarginsCollapseHandler.marginsCouldBeSelfCollapsing(renderer);
        if (firstChildMarginAdjoinedToParent(renderer)) {
            if (collapseInfo.isSelfCollapsing() && !couldBeSelfCollapsing) {
                float indentTop = collapseInfo.getCollapseBefore().getCollapsedMarginsSize();
                renderer.getOccupiedArea().getBBox().moveDown(indentTop);
            }
        }
        collapseInfo.setSelfCollapsing(collapseInfo.isSelfCollapsing() && couldBeSelfCollapsing);

        MarginsCollapse ownCollapseAfter;
        boolean lastChildMarginJoinedToParent = childMarginInfo != null && childMarginInfo.isIgnoreOwnMarginBottom();
        if (lastChildMarginJoinedToParent) {
            ownCollapseAfter = childMarginInfo.getOwnCollapseAfter();
        } else {
            ownCollapseAfter = new MarginsCollapse();
        }
        ownCollapseAfter.joinMargin(getModelBottomMargin(renderer));
        collapseInfo.setOwnCollapseAfter(ownCollapseAfter);

        if (collapseInfo.isSelfCollapsing()) {
            if (childMarginInfo != null) {
                collapseInfo.setCollapseAfter(childMarginInfo.getCollapseAfter());
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
                collapseInfo.setCollapseAfter(childMarginInfo.getCollapseAfter());
            }
            if (!collapseInfo.isIgnoreOwnMarginBottom()) {
                float collapsedMargins = collapseInfo.getCollapseAfter().getCollapsedMarginsSize();
                overrideModelBottomMargin(renderer, collapsedMargins);
            }
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
                float ownCollapsedMargins = prevChildMarginInfo.getOwnCollapseAfter().getCollapsedMarginsSize();
                layoutBox.setHeight(layoutBox.getHeight() - ownCollapsedMargins);
            }
        } else if (childIndex > firstNotEmptyKidIndex) {
            if (lastChildMarginAdjoinedToParent(renderer)) {
                // restore layout box after inline element
                float bottomIndent = collapseInfo.getCollapseAfter().getCollapsedMarginsSize();
                layoutBox.setY(layoutBox.getY() - bottomIndent);
                layoutBox.setHeight(layoutBox.getHeight() + bottomIndent);
            }

        }

        if (!childIsBlockElement) {
            if (childIndex == firstNotEmptyKidIndex && firstChildMarginAdjoinedToParent(renderer)) {
                float topIndent = collapseInfo.getCollapseBefore().getCollapsedMarginsSize();
                adjustBoxPosAndHeight(layoutBox, topIndent);
            }
            if (lastChildMarginAdjoinedToParent(renderer)) {
                float bottomIndent = collapseInfo.getCollapseAfter().getCollapsedMarginsSize();
                applyBottomMargin(layoutBox, bottomIndent);
            }
        }
    }

    private void adjustBoxPosAndHeight(Rectangle box, float topIndent) {
        float bufferLeftovers = collapseInfo.getBufferSpace() - topIndent;
        if (bufferLeftovers >= 0) {
            collapseInfo.setBufferSpace(bufferLeftovers);
            box.moveDown(topIndent);
        } else {
            box.moveDown(collapseInfo.getBufferSpace());
            collapseInfo.setBufferSpace(0);
            box.setHeight(box.getHeight() + bufferLeftovers);

        }
    }

    private void applyBottomMargin(Rectangle box, float bottomIndent) {
        box.setY(box.getY() + bottomIndent);
        box.setHeight(box.getHeight() - bottomIndent);
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

    private IRenderer getRendererChild(int index) {
        if (rendererChildren != null) {
            return rendererChildren.get(index);
        }

        return this.renderer.getChildRenderers().get(index);
    }

    private void getRidOfCollapseArtifactsAtopOccupiedArea() {
        Rectangle bBox = renderer.getOccupiedArea().getBBox();
        bBox.setHeight(bBox.getHeight() - collapseInfo.getCollapseBefore().getCollapsedMarginsSize());
    }

    private static boolean marginsCouldBeSelfCollapsing(IRenderer renderer) {
        return !hasBottomBorders(renderer) && !hasTopBorders(renderer) && !hasBottomPadding(renderer) && !hasTopPadding(renderer) && !hasPositiveHeight(renderer);
    }

    private static boolean firstChildMarginAdjoinedToParent(IRenderer parent) {
        return !(parent instanceof RootRenderer) && !hasTopBorders(parent) && !hasTopPadding(parent);

    }

    private static boolean lastChildMarginAdjoinedToParent(IRenderer parent) {
        return !(parent instanceof RootRenderer) && !hasBottomBorders(parent) && !hasBottomPadding(parent) && !hasHeightProp(parent);

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
        return renderer.getOccupiedArea().getBBox().getHeight() > 0;
    }

    private static boolean hasTopPadding(IRenderer renderer) {
        Float padding = renderer.getModelElement().<Float>getProperty(Property.PADDING_TOP);
        return padding != null && padding > 0;
    }

    private static boolean hasBottomPadding(IRenderer renderer) {
        Float padding = renderer.getModelElement().<Float>getProperty(Property.PADDING_TOP);
        return padding != null && padding > 0;
    }

    private static boolean hasTopBorders(IRenderer renderer) {
        IPropertyContainer modelElement = renderer.getModelElement();
        return modelElement.hasProperty(Property.BORDER_TOP) || modelElement.hasProperty(Property.BORDER);
    }

    private static boolean hasBottomBorders(IRenderer renderer) {
        IPropertyContainer modelElement = renderer.getModelElement();
        return modelElement.hasProperty(Property.BORDER_BOTTOM) || modelElement.hasProperty(Property.BORDER);
    }

    private static float getModelTopMargin(IRenderer renderer) {
        Float margin = renderer.getModelElement().<Float>getProperty(Property.MARGIN_TOP);
        return margin != null ? (float) margin : 0;
    }

    private static void ignoreModelTopMargin(IRenderer renderer) {
        renderer.setProperty(Property.MARGIN_TOP, 0);
    }

    private static void overrideModelTopMargin(IRenderer renderer, float collapsedMargins) {
        renderer.setProperty(Property.MARGIN_TOP, collapsedMargins);
    }

    private static float getModelBottomMargin(IRenderer renderer) {
        Float margin = renderer.getModelElement().<Float>getProperty(Property.MARGIN_BOTTOM);
        return margin != null ? (float) margin : 0;
    }

    private static void ignoreModelBottomMargin(IRenderer renderer) {
        renderer.setProperty(Property.MARGIN_BOTTOM, 0);
    }

    private static void overrideModelBottomMargin(IRenderer renderer, float collapsedMargins) {
        renderer.setProperty(Property.MARGIN_BOTTOM, collapsedMargins);
    }
}
