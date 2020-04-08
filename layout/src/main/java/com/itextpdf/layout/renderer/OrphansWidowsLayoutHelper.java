/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.margincollapse.MarginsCollapseInfo;
import com.itextpdf.layout.property.ParagraphOrphansControl;
import com.itextpdf.layout.property.ParagraphWidowsControl;
import com.itextpdf.layout.property.Property;
import java.util.ArrayList;

class OrphansWidowsLayoutHelper {

    private OrphansWidowsLayoutHelper() { }

    static LayoutResult orphansWidowsAwareLayout(ParagraphRenderer renderer, LayoutContext context,
            ParagraphOrphansControl orphansControl, ParagraphWidowsControl widowsControl) {
        OrphansWidowsLayoutAttempt layoutAttempt = attemptLayout(renderer, context, context.getArea().clone());

        if (context.isClippedHeight() || renderer.isPositioned()
                || layoutAttempt.attemptResult.getStatus() != LayoutResult.PARTIAL || layoutAttempt.attemptResult.getSplitRenderer() == null) {
            return handleAttemptAsSuccessful(layoutAttempt, context);
        }

        ParagraphRenderer splitRenderer = (ParagraphRenderer) layoutAttempt.attemptResult.getSplitRenderer();
        boolean orphansViolation = orphansControl != null && splitRenderer != null
                && splitRenderer.getLines().size() < orphansControl.getMinOrphans() && !renderer.isFirstOnRootArea();
        boolean forcedPlacement = Boolean.TRUE.equals(renderer.getPropertyAsBoolean(Property.FORCED_PLACEMENT));
        if (orphansViolation && forcedPlacement) {
            orphansControl.handleViolatedOrphans(splitRenderer, "Ignored orphans constraint due to forced placement.");
        }

        if (orphansViolation && !forcedPlacement) {
            layoutAttempt = null;
        } else if (widowsControl != null && splitRenderer != null && layoutAttempt.attemptResult.getOverflowRenderer() != null) {
            ParagraphRenderer overflowRenderer = (ParagraphRenderer) layoutAttempt.attemptResult.getOverflowRenderer();

            // Excessively big value to check if widows constraint is violated;
            // Make this value less in order to improve performance if you are sure
            // that min number of widows will fit in this height. E.g. A4 page height is 842.
            int simulationHeight = 3500;

            LayoutArea simulationArea = new LayoutArea(context.getArea().getPageNumber(), context.getArea().getBBox().clone().setHeight(simulationHeight));
            // collapsingMarginsInfo might affect available space, which is redundant in case we pass arbitrary space.
            // floatedRendererAreas list on new area is considered empty. We don't know if there will be any, however their presence in any case will result in more widows, not less.
            // clippedHeight is undefined for the next area, because it is defined by overflow part of the paragraph parent.
            //               Even if it will be set to true in actual overflow-part layouting, stealing lines approach will result in
            //               giving bigger part of MAX-HEIGHT to the overflow part and resulting in bigger number of widows, which is better.
            //               However for possible other approaches which change content "length" (like word/char spacing adjusts),
            //               if in actual overflow-part layouting clippedHeight will be true, those widows fixing attempts will result in worse results.
            LayoutContext simulationContext = new LayoutContext(simulationArea);

            LayoutResult simulationResult = overflowRenderer.directLayout(simulationContext);

            if (simulationResult.getStatus() == LayoutResult.FULL) {
                // simulationHeight is excessively big in order to allow to layout all of the content remaining in overflowRenderer:
                // this way after all of the remaining content is layouted we can check if it has led to widows violation.
                // To make this analysis possible, we expect to get result FULL.
                // if result is PARTIAL: means that simulationHeight value isn't big enough to layout all of the content remaining in overflowRenderer.
                // In this case we assume that widows aren't violated since the amount of the lines to fit the simulatedHeight is expected to be very large.
                // if result is NOTHING: unexpected result, limitation of simulation approach. Retry again with forced placement set.

                int extraWidows = widowsControl.getMinWidows() - overflowRenderer.getLines().size();
                if (extraWidows > 0) {
                    int extraLinesToMove = orphansControl != null ? Math.max(orphansControl.getMinOrphans(), 1) : 1;
                    if (extraWidows <= widowsControl.getMaxLinesToMove() && splitRenderer.getLines().size() - extraWidows >= extraLinesToMove) {
                        LineRenderer lastLine = splitRenderer.getLines().get(splitRenderer.getLines().size() - 1);
                        LineRenderer lastLineToLeave = splitRenderer.getLines().get(splitRenderer.getLines().size() - extraWidows - 1);
                        float d = lastLineToLeave.getOccupiedArea().getBBox().getY() - lastLine.getOccupiedArea().getBBox().getY() - AbstractRenderer.EPS;

                        Rectangle smallerBBox = new Rectangle(context.getArea().getBBox());
                        smallerBBox.decreaseHeight(d);
                        smallerBBox.moveUp(d);

                        LayoutArea smallerAvailableArea = new LayoutArea(context.getArea().getPageNumber(), smallerBBox);

                        layoutAttempt = attemptLayout(renderer, context, smallerAvailableArea);
                    } else {
                        if (forcedPlacement || renderer.isFirstOnRootArea() || !widowsControl.isOverflowOnWidowsViolation()) {
                            if (forcedPlacement) {
                                widowsControl.handleViolatedWidows(overflowRenderer, "forced placement");
                            } else {
                                widowsControl.handleViolatedWidows(overflowRenderer, "inability to fix it");
                            }
                        } else {
                            layoutAttempt = null;
                        }
                    }
                }
            }
        }

        if (layoutAttempt != null) {
            return handleAttemptAsSuccessful(layoutAttempt, context);
        } else {
            return new LayoutResult(LayoutResult.NOTHING, null, null, renderer);
        }
    }

    private static OrphansWidowsLayoutAttempt attemptLayout(ParagraphRenderer renderer, LayoutContext originalContext, LayoutArea attemptArea) {
        OrphansWidowsLayoutAttempt attemptResult = new OrphansWidowsLayoutAttempt();

        MarginsCollapseInfo copiedMarginsCollapseInfo = null;
        if (originalContext.getMarginsCollapseInfo() != null) {
            copiedMarginsCollapseInfo = MarginsCollapseInfo.createDeepCopy(originalContext.getMarginsCollapseInfo());
        }
        ArrayList<Rectangle> attemptFloatRectsList = new ArrayList<>(originalContext.getFloatRendererAreas());
        LayoutContext attemptContext = new LayoutContext(attemptArea, copiedMarginsCollapseInfo, attemptFloatRectsList, originalContext.isClippedHeight());

        attemptResult.attemptContext = attemptContext;
        attemptResult.attemptResult = renderer.directLayout(attemptContext);
        return attemptResult;
    }

    private static LayoutResult handleAttemptAsSuccessful(OrphansWidowsLayoutAttempt attemptResult, LayoutContext originalContext) {
        originalContext.getFloatRendererAreas().clear();
        originalContext.getFloatRendererAreas().addAll(attemptResult.attemptContext.getFloatRendererAreas());
        if (originalContext.getMarginsCollapseInfo() != null) {
            MarginsCollapseInfo.updateFromCopy(originalContext.getMarginsCollapseInfo(), attemptResult.attemptContext.getMarginsCollapseInfo());
        }
        return attemptResult.attemptResult;
    }

    private static class OrphansWidowsLayoutAttempt {
        LayoutContext attemptContext;
        LayoutResult attemptResult;
    }
}
