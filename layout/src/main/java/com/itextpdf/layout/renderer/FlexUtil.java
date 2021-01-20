/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.property.AlignmentPropertyValue;
import com.itextpdf.layout.property.FlexWrapPropertyValue;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;

import java.util.ArrayList;
import java.util.List;

final class FlexUtil {
    private static final float EPSILON = 0.00001f;

    private FlexUtil() {
        // Do nothing
    }

    /**
     * Performs flex layout algorithm.
     *
     * <p>
     * The algorithm could be found here:
     * {@see https://www.w3.org/TR/css-flexbox-1/#layout-algorithm}
     *
     * @param flexContainerBBox        bounding box in which flex container should be rendered
     * @param flexContainerRenderer    flex container's renderer
     * @param flexItemCalculationInfos list of flex item descriptions
     * @return list of lines
     */
    public static List<List<FlexItemInfo>> calculateChildrenRectangles(Rectangle flexContainerBBox,
            FlexContainerRenderer flexContainerRenderer, List<FlexItemCalculationInfo> flexItemCalculationInfos) {
        Rectangle layoutBox = flexContainerBBox.clone();
        flexContainerRenderer.applyBordersPaddingsMargins(
                layoutBox,
                flexContainerRenderer.getBorders(),
                flexContainerRenderer.getPaddings());

        // 9.2. Line Length Determination

        // 2. Determine the available main and cross space for the flex items.

        // TODO DEVSIX-5001 min-content and max-content as width are not supported
        // if that dimension of the flex container is being sized under a min or max-content constraint,
        // the available space in that dimension is that constraint;

        float mainSize = retrieveSize(flexContainerRenderer, Property.WIDTH, layoutBox.getWidth());
        float crossSize = retrieveSize(flexContainerRenderer, Property.HEIGHT, layoutBox.getHeight());

        determineFlexBasisAndHypotheticalMainSizeForFlexItems(flexItemCalculationInfos, mainSize);

        // 9.3. Main Size Determination

        // 5. Collect flex items into flex lines:
        boolean isSingleLine = !flexContainerRenderer.hasProperty(Property.FLEX_WRAP)
                || FlexWrapPropertyValue.NOWRAP == flexContainerRenderer.<FlexWrapPropertyValue>getProperty(
                Property.FLEX_WRAP);

        List<List<FlexItemCalculationInfo>> lines =
                collectFlexItemsIntoFlexLines(flexItemCalculationInfos, mainSize, isSingleLine);

        // 6. Resolve the flexible lengths of all the flex items to find their used main size.
        // See §9.7 Resolving Flexible Lengths.

        // 9.7. Resolving Flexible Lengths
        resolveFlexibleLengths(lines, mainSize);

        // 9.4. Cross Size Determination

        // 7. Determine the hypothetical cross size of each item by
        // performing layout with the used main size and the available space, treating auto as fit-content.
        determineHypotheticalCrossSizeForFlexItems(lines);

        // 8. Calculate the cross size of each flex line.
        List<Float> lineCrossSizes = calculateCrossSizeOfEachFlexLine(lines, flexContainerRenderer, isSingleLine);

        // TODO DEVSIX-5003 min/max height calculations are not supported
        // If the flex container is single-line, then clamp the line’s cross-size to be within
        // the container’s computed min and max cross sizes. Note that if CSS 2.1’s definition of min/max-width/height
        // applied more generally, this behavior would fall out automatically.

        float flexLinesCrossSizesSum = 0;
        for (float size : lineCrossSizes) {
            flexLinesCrossSizesSum += size;
        }

        // 9. Handle 'align-content: stretch'.
        handleAlignContentStretch(flexContainerRenderer, crossSize, flexLinesCrossSizesSum, lineCrossSizes);

        // TODO DEVSIX-2090 visibility-collapse items are not supported
        // 10. Collapse visibility:collapse items.

        // Determine the used cross size of each flex item.
        determineUsedCrossSizeOfEachFlexItem(lines, lineCrossSizes);

        // 9.6. Cross-Axis Alignment

        // TODO DEVSIX-5002 margin: auto is not supported
        // 13. Resolve cross-axis auto margins

        // TODO DEVSIX-4997 14. Align all flex items along the cross-axis

        // TODO DEVSIX-4997 15. Determine the flex container’s used cross size:

        // TODO DEVSIX-4997 16. Align all flex lines per align-content.

        List<List<FlexItemInfo>> layoutTable = new ArrayList<>();
        for (List<FlexItemCalculationInfo> line : lines) {
            List<FlexItemInfo> layoutLine = new ArrayList<>();
            for (FlexItemCalculationInfo info : line) {
                layoutLine.add(new FlexItemInfo(info.renderer, info.toRectangle()));
            }
            layoutTable.add(layoutLine);
        }

        return layoutTable;
    }

    static void determineFlexBasisAndHypotheticalMainSizeForFlexItems(
            List<FlexItemCalculationInfo> flexItemCalculationInfos, float mainSize) {
        for (FlexItemCalculationInfo info : flexItemCalculationInfos) {
            // 3. Determine the flex base size and hypothetical main size of each item:

            // Note: We assume that flex-basis: auto was resolved (set to either width or height) on some upper level
            assert null != info.flexBasis;

            // A. If the item has a definite used flex basis, that’s the flex base size.
            info.flexBaseSize = info.flexBasis.isPercentValue()
                    ? (info.flexBasis.getValue() * mainSize / 100)
                    : info.flexBasis.getValue();

            // TODO DEVSIX-5001 content as width are not supported
            // TODO DEVSIX-5004 Implement method to check whether an element has an intrinsic aspect ratio
            // B. If the flex item has ...
            // an intrinsic aspect ratio,
            // a used flex basis of content, and
            // a definite cross size,
            // then the flex base size is calculated from its inner cross size
            // and the flex item’s intrinsic aspect ratio.

            // TODO DEVSIX-5001 content as width is not supported
            // C. If the used flex basis is content or depends on its available space,
            // and the flex container is being sized under a min-content or max-content constraint
            // (e.g. when performing automatic table layout [CSS21]), size the item under that constraint.
            // The flex base size is the item’s resulting main size.

            // TODO DEVSIX-5001 content as width is not supported
            // Otherwise, if the used flex basis is content or depends on its available space,
            // the available main size is infinite, and the flex item’s inline axis is parallel to the main axis,
            // lay the item out using the rules for a box in an orthogonal flow [CSS3-WRITING-MODES].
            // The flex base size is the item’s max-content main size.

            // TODO DEVSIX-5001 max-content as width is not supported
            // Otherwise, size the item into the available space using its used flex basis in place of its main size,
            // treating a value of content as max-content. If a cross size is needed to determine the main size
            // (e.g. when the flex item’s main size is in its block axis)
            // and the flex item’s cross size is auto and not definite,
            // in this calculation use fit-content as the flex item’s cross size.
            // The flex base size is the item’s resulting main size.

            // The hypothetical main size is the item’s flex base size clamped
            // according to its used min and max main sizes (and flooring the content box size at zero).
            info.hypotheticalMainSize = Math.max(
                    0,
                    Math.min(
                            Math.max(info.minContent, info.flexBaseSize),
                            info.maxContent));

            // Note: We assume that it was resolved on some upper level
            // 4. Determine the main size of the flex container
        }
    }

    static List<List<FlexItemCalculationInfo>> collectFlexItemsIntoFlexLines(
            List<FlexItemCalculationInfo> flexItemCalculationInfos, float mainSize, boolean isSingleLine) {
        List<List<FlexItemCalculationInfo>> lines = new ArrayList<>();
        List<FlexItemCalculationInfo> currentLineInfos = new ArrayList<>();

        if (isSingleLine) {
            currentLineInfos.addAll(flexItemCalculationInfos);
        } else {
            float occupiedLineSpace = 0;
            for (FlexItemCalculationInfo info : flexItemCalculationInfos) {
                occupiedLineSpace += info.getOuterWidth(info.hypotheticalMainSize);
                if (occupiedLineSpace > mainSize + EPSILON) {
                    // If the very first uncollected item wouldn’t fit, collect just it into the line.
                    if (currentLineInfos.isEmpty()) {
                        currentLineInfos.add(info);
                        lines.add(currentLineInfos);
                        currentLineInfos = new ArrayList<>();
                        occupiedLineSpace = 0;
                    } else {
                        lines.add(currentLineInfos);
                        currentLineInfos = new ArrayList<>();
                        currentLineInfos.add(info);
                        occupiedLineSpace = info.hypotheticalMainSize;
                    }
                } else {
                    currentLineInfos.add(info);
                }
            }
        }

        // the last line should be added
        if (!currentLineInfos.isEmpty()) {
            lines.add(currentLineInfos);
        }

        return lines;
    }

    static void resolveFlexibleLengths(List<List<FlexItemCalculationInfo>> lines, float mainSize) {
        for (List<FlexItemCalculationInfo> line : lines) {

            // 1. Determine the used flex factor.
            float hypotheticalMainSizesSum = 0;
            for (FlexItemCalculationInfo info : line) {
                hypotheticalMainSizesSum += info.getOuterWidth(info.hypotheticalMainSize);
            }

            // if the sum is less than the flex container’s inner main size,
            // use the flex grow factor for the rest of this algorithm; otherwise, use the flex shrink factor.
            boolean isFlexGrow = hypotheticalMainSizesSum < mainSize;
            // 2. Size inflexible items.
            for (FlexItemCalculationInfo info : line) {
                if (isFlexGrow) {
                    if (isZero(info.flexGrow) || info.flexBaseSize > info.hypotheticalMainSize) {
                        info.mainSize = info.hypotheticalMainSize;
                        info.isFrozen = true;
                    }
                } else {
                    if (isZero(info.flexShrink) || info.flexBaseSize < info.hypotheticalMainSize) {
                        info.mainSize = info.hypotheticalMainSize;
                        info.isFrozen = true;
                    }
                }
            }

            // 3. Calculate initial free space.
            float initialFreeSpace = calculateFreeSpace(line, mainSize);

            // 4. Loop:
            // a. Check for flexible items
            while (hasFlexibleItems(line)) {

                // b. Calculate the remaining free space as for initial free space, above.
                float remainingFreeSpace = calculateFreeSpace(line, mainSize);
                float flexFactorSum = 0;
                for (FlexItemCalculationInfo info : line) {
                    if (!info.isFrozen) {
                        flexFactorSum += isFlexGrow ? info.flexGrow : info.flexShrink;
                    }
                }
                // If the sum of the unfrozen flex items’ flex factors is less than one,
                // multiply the initial free space by this sum.
                // If the magnitude of this value is less than the magnitude of the remaining free space,
                // use this as the remaining free space.
                if (flexFactorSum < 1 && Math.abs(remainingFreeSpace) > Math.abs(initialFreeSpace * flexFactorSum)) {
                    remainingFreeSpace = initialFreeSpace * flexFactorSum;
                }

                // c. Distribute free space proportional to the flex factors
                if (!isZero(remainingFreeSpace)) {
                    float scaledFlexShrinkFactorsSum = 0;
                    for (FlexItemCalculationInfo info : line) {
                        if (!info.isFrozen) {
                            if (isFlexGrow) {
                                float ratio = info.flexGrow / flexFactorSum;
                                info.mainSize = info.flexBaseSize + remainingFreeSpace * ratio;
                            } else {
                                info.scaledFlexShrinkFactor = info.flexShrink * info.flexBaseSize;
                                scaledFlexShrinkFactorsSum += info.scaledFlexShrinkFactor;
                            }
                        }
                    }

                    if (!isZero(scaledFlexShrinkFactorsSum)) {
                        for (FlexItemCalculationInfo info : line) {
                            if (!info.isFrozen && !isFlexGrow) {
                                float ratio = info.scaledFlexShrinkFactor / scaledFlexShrinkFactorsSum;
                                info.mainSize =
                                        info.flexBaseSize - Math.abs(remainingFreeSpace) * ratio;
                            }
                        }
                    }
                } else {
                    // This is not mentioned in the algo, however we must initialize main size (target main size)
                    for (FlexItemCalculationInfo info : line) {
                        if (!info.isFrozen) {
                            info.mainSize = info.flexBaseSize;
                        }
                    }
                }
                // d. Fix min/max violations.
                float sum = 0;
                for (FlexItemCalculationInfo info : line) {
                    if (!info.isFrozen) {
                        // Clamp each non-frozen item’s target main size by its used min and max main sizes
                        // and floor its content-box size at zero.
                        float clampedSize = Math
                                .min(Math.max((float) info.mainSize, info.minContent), info.maxContent);
                        if (info.mainSize > clampedSize) {
                            info.isMaxViolated = true;
                        } else if (info.mainSize < clampedSize) {
                            info.isMinViolated = true;
                        }
                        sum += (clampedSize - (float) info.mainSize);
                        info.mainSize = clampedSize;
                    }
                }
                for (FlexItemCalculationInfo info : line) {
                    if (!info.isFrozen) {
                        if (isZero(sum)
                                || (0 < sum && info.isMinViolated)
                                || (0 > sum && info.isMaxViolated)) {
                            info.isFrozen = true;
                        }
                    }
                }
            }

            // 9.5. Main-Axis Alignment

            // 12. Distribute any remaining free space.

            // Once any of the to-do remarks below is resolved, one should add a corresponding block,
            // which will be triggered if 0 < remaining free space
            // TODO DEVSIX-5002 margin: auto is not supported
            // If the remaining free space is positive and at least one main-axis margin on this line is auto,
            // distribute the free space equally among these margins. Otherwise, set all auto margins to zero.

            // TODO DEVSIX-4997 Align the items along the main-axis per justify-content.
        }
    }

    static void determineHypotheticalCrossSizeForFlexItems(List<List<FlexItemCalculationInfo>> lines) {
        for (List<FlexItemCalculationInfo> line : lines) {
            for (FlexItemCalculationInfo info : line) {
                LayoutResult result = info.renderer
                        .layout(new LayoutContext(new LayoutArea(0, new Rectangle((float) info.mainSize, 100000))));
                // Since main size is clamped with min-width, we do expect the result to be full
                assert result.getStatus() == LayoutResult.FULL;
                info.hypotheticalCrossSize = result.getOccupiedArea().getBBox().getHeight();
            }
        }
    }

    static List<Float> calculateCrossSizeOfEachFlexLine(List<List<FlexItemCalculationInfo>> lines,
            FlexContainerRenderer flexContainerRenderer, boolean isSingleLine) {
        List<Float> lineCrossSizes = new ArrayList<>();
        if (isSingleLine && flexContainerRenderer.hasProperty(Property.HEIGHT)
                && ((UnitValue) flexContainerRenderer.<UnitValue>getProperty(Property.HEIGHT)).isPointValue()) {
            UnitValue heightUV = (UnitValue) flexContainerRenderer.<UnitValue>getProperty(Property.HEIGHT);
            lineCrossSizes.add((float) heightUV.getValue());
        } else {
            for (List<FlexItemCalculationInfo> line : lines) {
                float flexLinesCrossSize = 0;

                float largestHypotheticalCrossSize = 0;
                for (FlexItemCalculationInfo info : line) {
                    // 1. Collect all the flex items whose inline-axis is parallel to the main-axis,
                    // whose align-self is baseline, and whose cross-axis margins are both non-auto.
                    // Find the largest of the distances between each item’s baseline and
                    // its hypothetical outer cross-start edge, and the largest of the distances
                    // between each item’s baseline and its hypothetical outer cross-end edge, and sum these two values.
                    // TODO DEVSIX-5003 Condition "inline-axis is parallel to the main-axis" is not supported
                    // TODO DEVSIX-5002 margin: auto is not supported => "cross-axis margins are both non-auto" is true
                    // TODO DEVSIX-5038 Support BASELINE as align-self

                    // 2. Among all the items not collected by the previous step,
                    // find the largest outer hypothetical cross size.
                    if (largestHypotheticalCrossSize < info.getOuterWidth(info.hypotheticalCrossSize)) {
                        largestHypotheticalCrossSize = info.getOuterWidth(info.hypotheticalCrossSize);
                    }
                    flexLinesCrossSize = Math.max(0, largestHypotheticalCrossSize);
                }
                lineCrossSizes.add(flexLinesCrossSize);
            }
        }
        return lineCrossSizes;
    }

    static void handleAlignContentStretch(FlexContainerRenderer flexContainerRenderer, float crossSize,
            float flexLinesCrossSizesSum, List<Float> lineCrossSizes) {
        if (flexContainerRenderer.hasProperty(Property.HEIGHT) && (
                flexContainerRenderer.hasProperty(Property.ALIGN_CONTENT)
                        && AlignmentPropertyValue.STRETCH
                        == (AlignmentPropertyValue) flexContainerRenderer.<AlignmentPropertyValue>getProperty(
                        Property.ALIGN_CONTENT))
                && flexLinesCrossSizesSum < crossSize) {
            float addition = (crossSize - flexLinesCrossSizesSum) / lineCrossSizes.size();
            for (int i = 0; i < lineCrossSizes.size(); i++) {
                lineCrossSizes.set(i, lineCrossSizes.get(i) + addition);
            }
        }
    }

    static void determineUsedCrossSizeOfEachFlexItem(List<List<FlexItemCalculationInfo>> lines,
            List<Float> lineCrossSizes) {
        for (int i = 0; i < lines.size(); i++) {
            for (FlexItemCalculationInfo info : lines.get(i)) {
                // TODO DEVSIX-5002 margin: auto is not supported
                // TODO DEVSIX-5003 min/max height calculations are not implemented
                // If a flex item has align-self: stretch, its computed cross size property is auto,
                // and neither of its cross-axis margins are auto,
                // the used outer cross size is the used cross size of its flex line,
                // clamped according to the item’s used min and max cross sizes.
                // Otherwise, the used cross size is the item’s hypothetical cross size.
                if (info.renderer.hasProperty(Property.ALIGN_SELF)
                        && AlignmentPropertyValue.STRETCH == (AlignmentPropertyValue)
                        info.renderer.<AlignmentPropertyValue>getProperty(Property.ALIGN_SELF)) {
                    info.crossSize = lineCrossSizes.get(i);
                } else {
                    info.crossSize = info.hypotheticalCrossSize;
                }
            }
        }
    }

    private static float calculateFreeSpace(final List<FlexItemCalculationInfo> line, final float initialFreeSpace) {
        float result = initialFreeSpace;
        for (FlexItemCalculationInfo info : line) {
            if (info.isFrozen) {
                assert null != info.mainSize;
                result -= info.getOuterWidth((float) info.mainSize);
            } else {
                result -= info.getOuterWidth(info.flexBaseSize);
            }
        }
        return result;
    }

    private static boolean hasFlexibleItems(final List<FlexItemCalculationInfo> line) {
        for (FlexItemCalculationInfo info : line) {
            if (!info.isFrozen) {
                return true;
            }
        }
        return false;
    }

    static float retrieveSize(IRenderer renderer, int sizeType, float areaSize) {
        float size = 0;
        if (renderer.hasProperty(sizeType)) {
            UnitValue sizeUV = renderer.<UnitValue>getProperty(sizeType);
            if (sizeUV.isPercentValue()) {
                size = sizeUV.getValue() * areaSize / 100;
            } else {
                size = sizeUV.getValue();
            }
        } else {
            size = areaSize;
        }
        return size;
    }

    static boolean isZero(final float value) {
        return Math.abs(value) < EPSILON;
    }

    static class FlexItemCalculationInfo {
        AbstractRenderer renderer;
        UnitValue flexBasis;
        float flexShrink;
        float flexGrow;
        float minContent;
        float maxContent;

        Float mainSize;
        Float crossSize;

        // Calculation-related fields

        float scaledFlexShrinkFactor;
        boolean isFrozen = false;
        boolean isMinViolated = false;
        boolean isMaxViolated = false;
        float flexBaseSize;
        float hypotheticalMainSize;
        float hypotheticalCrossSize;

        public FlexItemCalculationInfo(AbstractRenderer renderer, UnitValue flexBasis, float flexGrow, float flexShrink,
                float areaWidth) {
            this.renderer = renderer;
            if (null == flexBasis) {
                throw new IllegalArgumentException(LayoutExceptionMessageConstant.FLEX_BASIS_CANNOT_BE_NULL);
            }
            this.flexBasis = flexBasis;
            if (flexShrink < 0) {
                throw new IllegalArgumentException(LayoutExceptionMessageConstant.FLEX_SHRINK_CANNOT_BE_NEGATIVE);
            }
            this.flexShrink = flexShrink;
            if (flexGrow < 0) {
                throw new IllegalArgumentException(LayoutExceptionMessageConstant.FLEX_GROW_CANNOT_BE_NEGATIVE);
            }
            this.flexGrow = flexGrow;
            // We always need to clamp flex item's sizes with min-width, so this calculation is necessary
            MinMaxWidth minMaxWidth = renderer.getMinMaxWidth();
            this.minContent = minMaxWidth.getMinWidth();
            boolean isMaxWidthApplied = null != this.renderer.retrieveMaxWidth(areaWidth);
            // As for now we assume that max width should be calculated so
            this.maxContent = isMaxWidthApplied
                    ? minMaxWidth.getMaxWidth()
                    : Math.max(minMaxWidth.getMaxWidth(), areaWidth);
        }


        public Rectangle toRectangle() {
            return new Rectangle((float) mainSize, (float) crossSize);
        }

        float getOuterWidth(float size) {
            Rectangle tempRect = new Rectangle(size, 0);
            renderer.applyMargins(tempRect, true);
            renderer.applyBorderBox(tempRect, true);
            renderer.applyPaddings(tempRect, true);

            return tempRect.getWidth();
        }
    }
}
