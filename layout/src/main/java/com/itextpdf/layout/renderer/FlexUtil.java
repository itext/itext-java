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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.properties.AlignmentPropertyValue;
import com.itextpdf.layout.properties.FlexDirectionPropertyValue;
import com.itextpdf.layout.properties.FlexWrapPropertyValue;
import com.itextpdf.layout.properties.InlineVerticalAlignmentType;
import com.itextpdf.layout.properties.JustifyContent;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

final class FlexUtil {

    private static final float EPSILON = 0.0001F;

    private static final float FLEX_GROW_INITIAL_VALUE = 0F;

    private static final float FLEX_SHRINK_INITIAL_VALUE = 1F;

    private static final Logger logger = LoggerFactory.getLogger(FlexUtil.class);

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
     * @param flexContainerBBox     bounding box in which flex container should be rendered
     * @param flexContainerRenderer flex container's renderer
     * @return list of lines
     */
    public static List<List<FlexItemInfo>> calculateChildrenRectangles(Rectangle flexContainerBBox,
                                                                       FlexContainerRenderer flexContainerRenderer) {
        Rectangle layoutBox = flexContainerBBox.clone();
        flexContainerRenderer.applyMarginsBordersPaddings(layoutBox, false);

        // 9.2. Line Length Determination

        // 2. Determine the available main and cross space for the flex items.

        // TODO DEVSIX-5001 min-content and max-content as width are not supported
        // if that dimension of the flex container is being sized under a min or max-content constraint,
        // the available space in that dimension is that constraint;

        final float mainSize = getMainSize(flexContainerRenderer, layoutBox);

        // We need to have crossSize only if its value is definite.
        Float[] crossSizes = getCrossSizes(flexContainerRenderer, layoutBox);
        Float crossSize = crossSizes[0];
        Float minCrossSize = crossSizes[1];
        Float maxCrossSize = crossSizes[2];

        float layoutBoxCrossSize = isColumnDirection(flexContainerRenderer) ?
                layoutBox.getWidth() : layoutBox.getHeight();
        layoutBoxCrossSize = crossSize == null ? layoutBoxCrossSize : Math.min((float) crossSize, layoutBoxCrossSize);
        List<FlexItemCalculationInfo> flexItemCalculationInfos =
                createFlexItemCalculationInfos(flexContainerRenderer, mainSize, layoutBoxCrossSize);

        determineFlexBasisAndHypotheticalMainSizeForFlexItems(flexItemCalculationInfos, layoutBoxCrossSize,
                isColumnDirection(flexContainerRenderer));

        // 9.3. Main Size Determination

        // 5. Collect flex items into flex lines:
        final boolean isSingleLine = !flexContainerRenderer.hasProperty(Property.FLEX_WRAP)
                || FlexWrapPropertyValue.NOWRAP == flexContainerRenderer.<FlexWrapPropertyValue>getProperty(
                Property.FLEX_WRAP);

        List<List<FlexItemCalculationInfo>> lines =
                collectFlexItemsIntoFlexLines(flexItemCalculationInfos, isColumnDirection(flexContainerRenderer) ?
                        Math.min(mainSize, layoutBox.getHeight()) : mainSize, isSingleLine);

        // 6. Resolve the flexible lengths of all the flex items to find their used main size.
        // See §9.7 Resolving Flexible Lengths.

        // 9.7. Resolving Flexible Lengths
        // First, calculate max line size. For column container it should be the default size if width is not set.
        // For row container it is not used currently.
        float maxHypotheticalMainSize = 0;
        for (List<FlexItemCalculationInfo> line : lines) {
            float hypotheticalMainSizesSum = 0;
            for (FlexItemCalculationInfo info : line) {
                hypotheticalMainSizesSum += info.getOuterMainSize(info.hypotheticalMainSize);
            }
            maxHypotheticalMainSize = Math.max(maxHypotheticalMainSize, hypotheticalMainSizesSum);
        }
        final float containerMainSize = getMainSize(flexContainerRenderer,
                new Rectangle(isColumnDirection(flexContainerRenderer) ? 0 : maxHypotheticalMainSize,
                        isColumnDirection(flexContainerRenderer) ? maxHypotheticalMainSize : 0));
        if (isColumnDirection(flexContainerRenderer)) {
            resolveFlexibleLengths(lines, layoutBox.getHeight(), containerMainSize);
        } else {
            resolveFlexibleLengths(lines, mainSize);
        }

        // 9.4. Cross Size Determination

        // 7. Determine the hypothetical cross size of each item by
        // performing layout with the used main size and the available space, treating auto as fit-content.
        determineHypotheticalCrossSizeForFlexItems(lines, isColumnDirection(flexContainerRenderer), layoutBoxCrossSize);

        // 8. Calculate the cross size of each flex line.
        List<Float> lineCrossSizes = calculateCrossSizeOfEachFlexLine(lines, minCrossSize, crossSize, maxCrossSize);

        // If the flex container is single-line, then clamp the line’s cross-size to be within
        // the container’s computed min and max cross sizes. Note that if CSS 2.1’s definition of min/max-width/height
        // applied more generally, this behavior would fall out automatically.

        // 9. Handle 'align-content: stretch'.
        Float currentCrossSize = isColumnDirection(flexContainerRenderer) ? new Float(layoutBoxCrossSize) : crossSize;
        handleAlignContentStretch(flexContainerRenderer, lines, currentCrossSize, lineCrossSizes, layoutBox);

        // TODO DEVSIX-2090 visibility-collapse items are not supported
        // 10. Collapse visibility:collapse items.

        // 11. Determine the used cross size of each flex item.
        determineUsedCrossSizeOfEachFlexItem(lines, lineCrossSizes, flexContainerRenderer);

        // 9.5. Main-Axis Alignment
        // 12. Align the items along the main-axis per justify-content.
        applyJustifyContent(lines, flexContainerRenderer, isColumnDirection(flexContainerRenderer) ?
                layoutBox.getHeight() : mainSize, containerMainSize);

        // 9.6. Cross-Axis Alignment

        // TODO DEVSIX-5002 margin: auto is not supported
        // 13. Resolve cross-axis auto margins

        // 14. Align all flex items along the cross-axis
        applyAlignItemsAndAlignSelf(lines, flexContainerRenderer, lineCrossSizes);

        // 15. Determine the flex container’s used cross size

        // TODO DEVSIX-5164 16. Align all flex lines per align-content.

        // Convert FlexItemCalculationInfo's into FlexItemInfo's
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

    static boolean isColumnDirection(FlexContainerRenderer renderer) {
        FlexDirectionPropertyValue flexDir = (FlexDirectionPropertyValue) renderer.
                <FlexDirectionPropertyValue>getProperty(Property.FLEX_DIRECTION, FlexDirectionPropertyValue.ROW);
        return FlexDirectionPropertyValue.COLUMN == flexDir || FlexDirectionPropertyValue.COLUMN_REVERSE == flexDir;
    }

    static float getMainSize(FlexContainerRenderer renderer, Rectangle layoutBox) {
        final boolean isColumnDirection = isColumnDirection(renderer);

        float layoutBoxMainSize;
        Float mainSize;

        Float maxDimension = null;
        Float minDimension = null;
        if (isColumnDirection) {
            layoutBoxMainSize = layoutBox.getHeight();
            mainSize = renderer.retrieveHeight();
            maxDimension = resolveUnitValue(renderer, Property.MAX_HEIGHT, layoutBoxMainSize);
            minDimension = resolveUnitValue(renderer, Property.MIN_HEIGHT, layoutBoxMainSize);
        } else {
            layoutBoxMainSize = layoutBox.getWidth();
            mainSize = renderer.retrieveWidth(layoutBoxMainSize);
            maxDimension = resolveUnitValue(renderer, Property.MAX_WIDTH, layoutBoxMainSize);
            minDimension = resolveUnitValue(renderer, Property.MIN_WIDTH, layoutBoxMainSize);
        }

        // TODO DEVSIX-5001 min-content and max-content as width are not supported
        // if that dimension of the flex container is being sized under a min or max-content constraint,
        // the available space in that dimension is that constraint;

        if (mainSize == null) {
            mainSize = layoutBoxMainSize;
        }
        if (minDimension != null && minDimension > mainSize) {
            mainSize = minDimension;
        }

        if (maxDimension != null && (minDimension == null || maxDimension > minDimension) && maxDimension < mainSize) {
            mainSize = maxDimension;
        }

        return (float) mainSize;
    }

    private static Float resolveUnitValue(FlexContainerRenderer renderer, int property, float baseValue) {
        UnitValue value = renderer.getPropertyAsUnitValue(property);
        if (value == null) {
            return null;
        }
        if (value.isPercentValue()) {
            return value.getValue() / 100 * baseValue;
        }
        return value.getValue();
    }

    private static Float[] getCrossSizes(FlexContainerRenderer renderer, Rectangle layoutBox) {
        final boolean isColumnDirection = isColumnDirection(renderer);

        return new Float[]{
                isColumnDirection ? renderer.retrieveWidth(layoutBox.getWidth()) : renderer.retrieveHeight(),
                isColumnDirection ? renderer.retrieveMinWidth(layoutBox.getWidth()) : renderer.retrieveMinHeight(),
                isColumnDirection ? renderer.retrieveMaxWidth(layoutBox.getWidth()) : renderer.retrieveMaxHeight()
        };
    }

    static void determineFlexBasisAndHypotheticalMainSizeForFlexItems(
            List<FlexItemCalculationInfo> flexItemCalculationInfos, float crossSize, boolean isColumnDirection) {
        for (FlexItemCalculationInfo info : flexItemCalculationInfos) {
            // 3. Determine the flex base size and hypothetical main size of each item:

            AbstractRenderer renderer = info.renderer;

            // TODO DEVSIX-5001 content as width are not supported
            // B. If the flex item has ...
            // an intrinsic aspect ratio,
            // a used flex basis of content, and
            // a definite cross size,
            // then the flex base size is calculated from its inner cross size
            // and the flex item’s intrinsic aspect ratio.
            Float definiteCrossSize = null;
            if (renderer.hasAspectRatio()) {
                definiteCrossSize = isColumnDirection ?
                        renderer.retrieveWidth(crossSize) : renderer.retrieveHeight();
            }
            if (info.flexBasisContent && definiteCrossSize != null) {
                float aspectRatio = (float) renderer.getAspectRatio();
                info.flexBaseSize = isColumnDirection ?
                        (float) definiteCrossSize / aspectRatio : (float) definiteCrossSize * aspectRatio;
            } else {
                // A. If the item has a definite used flex basis, that’s the flex base size.
                info.flexBaseSize = info.flexBasis;
            }

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
            // Each item in the flex line has a target main size, initially set to its flex base size
            info.mainSize = info.hypotheticalMainSize;

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
                occupiedLineSpace += info.getOuterMainSize(info.hypotheticalMainSize);
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
                        occupiedLineSpace = info.getOuterMainSize(info.hypotheticalMainSize);
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
                hypotheticalMainSizesSum += info.getOuterMainSize(info.hypotheticalMainSize);
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
                                .min(Math.max(info.mainSize, info.minContent), info.maxContent);
                        if (info.mainSize > clampedSize) {
                            info.isMaxViolated = true;
                        } else if (info.mainSize < clampedSize) {
                            info.isMinViolated = true;
                        }
                        sum += clampedSize - info.mainSize;
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
        }
    }

    private static void resolveFlexibleLengths(List<List<FlexItemCalculationInfo>> lines, float layoutBoxSize,
                                               float containerSize) {
        resolveFlexibleLengths(lines, containerSize);
        if (lines.size() == 1 && layoutBoxSize < containerSize - EPSILON) {
            List<FlexItemCalculationInfo> lineToRecalculate = new ArrayList<>();
            float mainSize = 0;
            for (FlexItemCalculationInfo itemInfo : lines.get(0)) {
                mainSize += itemInfo.getOuterMainSize(itemInfo.mainSize);
                if (mainSize < layoutBoxSize - EPSILON) {
                    itemInfo.isFrozen = false;
                    lineToRecalculate.add(itemInfo);
                } else {
                    break;
                }
            }
            if (lineToRecalculate.size() > 0) {
                List<List<FlexItemCalculationInfo>> updatedLines = new ArrayList<>();
                updatedLines.add(lineToRecalculate);
                resolveFlexibleLengths(updatedLines, layoutBoxSize);
            }
        }
    }

    static void determineHypotheticalCrossSizeForFlexItems(List<List<FlexItemCalculationInfo>> lines,
                                                           boolean isColumnDirection, float crossSize) {
        for (List<FlexItemCalculationInfo> line : lines) {
            for (FlexItemCalculationInfo info : line) {
                determineHypotheticalCrossSizeForFlexItem(info, isColumnDirection, crossSize);
            }
        }
    }

    private static void determineHypotheticalCrossSizeForFlexItem(FlexItemCalculationInfo info,
                                                                  boolean isColumnDirection, float crossSize) {
        if (info.renderer instanceof FlexContainerRenderer &&
                ((FlexContainerRenderer) info.renderer).getHypotheticalCrossSize(info.mainSize) != null) {
            // Take from cache
            info.hypotheticalCrossSize = ((FlexContainerRenderer) info.renderer)
                    .getHypotheticalCrossSize(info.mainSize).floatValue();
        } else if (isColumnDirection) {
            MinMaxWidth minMaxWidth = info.renderer.getMinMaxWidth();
            info.hypotheticalCrossSize = info.getInnerCrossSize(
                    Math.max(Math.min(minMaxWidth.getMaxWidth(), crossSize), minMaxWidth.getMinWidth()));
            // Cache hypotheticalCrossSize for FlexContainerRenderer
            if (info.renderer instanceof FlexContainerRenderer) {
                ((FlexContainerRenderer) info.renderer).setHypotheticalCrossSize(info.mainSize,
                        info.hypotheticalCrossSize);
            }
        } else {
            UnitValue prevMainSize = info.renderer.<UnitValue>replaceOwnProperty(Property.WIDTH,
                    UnitValue.createPointValue(info.mainSize));
            UnitValue prevMinMainSize = info.renderer.<UnitValue>replaceOwnProperty(Property.MIN_WIDTH, null);
            info.renderer.setProperty(Property.INLINE_VERTICAL_ALIGNMENT, InlineVerticalAlignmentType.BOTTOM);
            LayoutResult result = info.renderer.layout(new LayoutContext(
                    new LayoutArea(0, new Rectangle(AbstractRenderer.INF, AbstractRenderer.INF))));
            info.renderer.returnBackOwnProperty(Property.MIN_WIDTH, prevMinMainSize);
            info.renderer.returnBackOwnProperty(Property.WIDTH, prevMainSize);
            // Since main size is clamped with min-width, we do expect the result to be full
            if (result.getStatus() == LayoutResult.FULL) {
                info.hypotheticalCrossSize = info.getInnerCrossSize(result.getOccupiedArea().getBBox().getHeight());
                // Cache hypotheticalCrossSize for FlexContainerRenderer
                if (info.renderer instanceof FlexContainerRenderer) {
                    ((FlexContainerRenderer) info.renderer).setHypotheticalCrossSize(info.mainSize,
                            info.hypotheticalCrossSize);
                }
            } else {
                logger.error(IoLogMessageConstant.FLEX_ITEM_LAYOUT_RESULT_IS_NOT_FULL);
                info.hypotheticalCrossSize = 0;
            }
        }
    }

    static List<Float> calculateColumnDirectionCrossSizes(List<List<FlexItemInfo>> lines) {
        List<Float> lineCrossSizes = new ArrayList<>();
            for (List<FlexItemInfo> line : lines) {
                float flexLinesCrossSize = 0;
                float largestCrossSize = 0;
                for (FlexItemInfo info : line) {
                    // TODO DEVSIX-5002 Flex items whose cross-axis margins are both auto shouldn't be collected
                    // TODO DEVSIX-5038 Support BASELINE as align-self
                    largestCrossSize = Math.max(largestCrossSize, info.getRectangle().getWidth());
                    flexLinesCrossSize = Math.max(0, largestCrossSize);
                }
                lineCrossSizes.add(flexLinesCrossSize);
            }
        return lineCrossSizes;
    }

    static List<Float> calculateCrossSizeOfEachFlexLine(List<List<FlexItemCalculationInfo>> lines,
                                                        Float minCrossSize, Float crossSize, Float maxCrossSize) {
        boolean isSingleLine = lines.size() == 1;
        List<Float> lineCrossSizes = new ArrayList<>();
        if (isSingleLine && crossSize != null) {
            lineCrossSizes.add((float) crossSize);
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
                    // TODO DEVSIX-5002 margin: auto is not supported => "cross-axis margins are both non-auto" is true
                    // TODO DEVSIX-5038 Support BASELINE as align-self

                    // 2. Among all the items not collected by the previous step,
                    // find the largest outer hypothetical cross size.
                    if (largestHypotheticalCrossSize < info.getOuterCrossSize(info.hypotheticalCrossSize)) {
                        largestHypotheticalCrossSize = info.getOuterCrossSize(info.hypotheticalCrossSize);
                    }
                    flexLinesCrossSize = Math.max(0, largestHypotheticalCrossSize);
                }

                // 3. If the flex container is single-line, then clamp the line’s cross-size to be
                // within the container’s computed min and max cross sizes
                if (isSingleLine) {
                    if (null != minCrossSize) {
                        flexLinesCrossSize = Math.max((float) minCrossSize, flexLinesCrossSize);
                    }
                    if (null != maxCrossSize) {
                        flexLinesCrossSize = Math.min((float) maxCrossSize, flexLinesCrossSize);
                    }
                }
                lineCrossSizes.add(flexLinesCrossSize);
            }
        }
        return lineCrossSizes;
    }

    static void handleAlignContentStretch(FlexContainerRenderer flexContainerRenderer,
                                          List<List<FlexItemCalculationInfo>> lines,
                                          Float crossSize, List<Float> lineCrossSizes, Rectangle layoutBox) {
        AlignmentPropertyValue alignContent =
                (AlignmentPropertyValue) flexContainerRenderer.<AlignmentPropertyValue>getProperty(
                        Property.ALIGN_CONTENT, AlignmentPropertyValue.STRETCH);
        if (crossSize != null && alignContent == AlignmentPropertyValue.STRETCH) {
            // Line order becomes important for alignment
            if (flexContainerRenderer.isWrapReverse()) {
                Collections.reverse(lineCrossSizes);
                Collections.reverse(lines);
            }
            List<Float> currentPageLineCrossSizes =
                    retrieveCurrentPageLineCrossSizes(flexContainerRenderer, lines, lineCrossSizes, crossSize, layoutBox);
            if (currentPageLineCrossSizes.size() > 0) {
                float flexLinesCrossSizesSum = 0;
                for (float size : currentPageLineCrossSizes) {
                    flexLinesCrossSizesSum += size;
                }
                if (flexLinesCrossSizesSum < crossSize - EPSILON) {
                    float addition = ((float) crossSize - flexLinesCrossSizesSum) / currentPageLineCrossSizes.size();
                    for (int i = 0; i < currentPageLineCrossSizes.size(); i++) {
                        lineCrossSizes.set(i, lineCrossSizes.get(i) + addition);
                    }
                }
            }
            // Reverse back
            if (flexContainerRenderer.isWrapReverse()) {
                Collections.reverse(lineCrossSizes);
                Collections.reverse(lines);
            }
        }
    }

    static void determineUsedCrossSizeOfEachFlexItem(List<List<FlexItemCalculationInfo>> lines,
                                                     List<Float> lineCrossSizes, FlexContainerRenderer flexContainerRenderer) {
        final boolean isColumnDirection = isColumnDirection(flexContainerRenderer);
        AlignmentPropertyValue alignItems =
                (AlignmentPropertyValue) flexContainerRenderer.<AlignmentPropertyValue>getProperty(
                        Property.ALIGN_ITEMS, AlignmentPropertyValue.STRETCH);

        assert lines.size() == lineCrossSizes.size();

        for (int i = 0; i < lines.size(); i++) {
            for (FlexItemCalculationInfo info : lines.get(i)) {
                // TODO DEVSIX-5002 margin: auto is not supported
                // If a flex item has align-self: stretch, its computed cross size property is auto,
                // and neither of its cross-axis margins are auto,
                // the used outer cross size is the used cross size of its flex line,
                // clamped according to the item’s used min and max cross sizes.
                // Otherwise, the used cross size is the item’s hypothetical cross size.
                // Note that this step doesn't affect the main size of the flex item, even if it has aspect ratio.
                // Also note that for some reason browsers do not respect such a rule from the specification
                AbstractRenderer infoRenderer = info.renderer;
                AlignmentPropertyValue alignSelf =
                        (AlignmentPropertyValue) infoRenderer.<AlignmentPropertyValue>getProperty(
                                Property.ALIGN_SELF, alignItems);
                // TODO DEVSIX-5002 Stretch value shall be ignored if margin auto for cross axis is set
                boolean definiteCrossSize = isColumnDirection ?
                        info.renderer.hasProperty(Property.WIDTH) : info.renderer.hasProperty(Property.HEIGHT);
                if ((alignSelf == AlignmentPropertyValue.STRETCH || alignSelf == AlignmentPropertyValue.NORMAL)
                        && !definiteCrossSize) {
                    info.crossSize = info.getInnerCrossSize(lineCrossSizes.get(i));
                    Float maxCrossSize = isColumnDirection ?
                            infoRenderer.retrieveMaxWidth(lineCrossSizes.get(i)) : infoRenderer.retrieveMaxHeight();
                    if (maxCrossSize != null) {
                        info.crossSize = Math.min((float) maxCrossSize, info.crossSize);
                    }
                    Float minCrossSize = isColumnDirection ?
                            infoRenderer.retrieveMinWidth(lineCrossSizes.get(i)) : infoRenderer.retrieveMinHeight();
                    if (minCrossSize != null) {
                        info.crossSize = Math.max((float) minCrossSize, info.crossSize);
                    }
                } else {
                    info.crossSize = info.hypotheticalCrossSize;
                }
            }
        }
    }

    private static Float retrieveMaxHeightForMainDirection(AbstractRenderer renderer) {
        Float maxHeight = renderer.retrieveMaxHeight();
        return renderer.hasProperty(Property.MAX_HEIGHT) ? maxHeight : null;
    }

    private static Float retrieveMinHeightForMainDirection(AbstractRenderer renderer) {
        Float minHeight = renderer.retrieveMinHeight();
        return renderer.hasProperty(Property.MIN_HEIGHT) && renderer.<UnitValue>getProperty(Property.MIN_HEIGHT) != null ?
                minHeight : null;
    }

    private static void applyAlignItemsAndAlignSelf(List<List<FlexItemCalculationInfo>> lines,
                                                    FlexContainerRenderer renderer, List<Float> lineCrossSizes) {
        final boolean isColumnDirection = isColumnDirection(renderer);
        AlignmentPropertyValue itemsAlignment = (AlignmentPropertyValue) renderer.<AlignmentPropertyValue>getProperty(
                Property.ALIGN_ITEMS, AlignmentPropertyValue.STRETCH);

        assert lines.size() == lineCrossSizes.size();

        // Line order becomes important for counting nextLineShift
        if (renderer.isWrapReverse()) {
            Collections.reverse(lines);
            Collections.reverse(lineCrossSizes);
        }

        float lineShift;
        float nextLineShift = 0;
        for (int i = 0; i < lines.size(); ++i) {
            lineShift = nextLineShift;
            List<FlexItemCalculationInfo> line = lines.get(i);
            float lineCrossSize = lineCrossSizes.get(i);
            // Used to calculate an extra space between the right/bottom point of the current line and left/top point
            // of the next line
            nextLineShift = lineCrossSize - line.get(0).getOuterCrossSize(line.get(0).crossSize);
            for (FlexItemCalculationInfo itemInfo : line) {
                if (isColumnDirection) {
                    itemInfo.xShift = lineShift;
                } else {
                    itemInfo.yShift = lineShift;
                }

                AlignmentPropertyValue selfAlignment =
                        (AlignmentPropertyValue) itemInfo.renderer.<AlignmentPropertyValue>getProperty(
                                Property.ALIGN_SELF, itemsAlignment);

                final float freeSpace = lineCrossSize - itemInfo.getOuterCrossSize(itemInfo.crossSize);
                nextLineShift = Math.min(nextLineShift, freeSpace);

                switch (selfAlignment) {
                    case SELF_END:
                    case END:
                        if (isColumnDirection) {
                            itemInfo.xShift += freeSpace;
                        } else {
                            itemInfo.yShift += freeSpace;
                        }
                        nextLineShift = 0;
                        break;
                    case FLEX_END:
                        if (!renderer.isWrapReverse()) {
                            if (isColumnDirection) {
                                itemInfo.xShift += freeSpace;
                            } else {
                                itemInfo.yShift += freeSpace;
                            }
                            nextLineShift = 0;
                        }
                        break;
                    case CENTER:
                        if (isColumnDirection) {
                            itemInfo.xShift += freeSpace / 2;
                        } else {
                            itemInfo.yShift += freeSpace / 2;
                        }
                        nextLineShift = Math.min(nextLineShift, freeSpace / 2);
                        break;
                    case FLEX_START:
                        if (renderer.isWrapReverse()) {
                            if (isColumnDirection) {
                                itemInfo.xShift += freeSpace;
                            } else {
                                itemInfo.yShift += freeSpace;
                            }
                            nextLineShift = 0;
                        }
                        break;
                    case START:
                    case BASELINE:
                    case SELF_START:
                    case STRETCH:
                    case NORMAL:
                    default:
                        // We don't need to do anything in these cases
                }
            }
        }

        // Reverse back
        if (renderer.isWrapReverse()) {
            Collections.reverse(lines);
            Collections.reverse(lineCrossSizes);
        }
    }

    private static void applyJustifyContent(List<List<FlexItemCalculationInfo>> lines,
                                            FlexContainerRenderer renderer, float mainSize, float containerMainSize) {
        JustifyContent justifyContent = (JustifyContent) renderer.<JustifyContent>getProperty(
                Property.JUSTIFY_CONTENT, JustifyContent.FLEX_START);

        boolean containsFixedHeight = containerMainSize > 0;
        boolean isFixedHeightAppliedOnTheCurrentPage = containsFixedHeight && containerMainSize < mainSize;
        if (renderer.isWrapReverse()) {
            Collections.reverse(lines);
        }
        for (List<FlexItemCalculationInfo> line : lines) {
            float childrenMainSize = 0;
            // Items order becomes important for justification
            boolean isColumnReverse = FlexDirectionPropertyValue.COLUMN_REVERSE ==
                    renderer.<FlexDirectionPropertyValue>getProperty(Property.FLEX_DIRECTION, null);
            if (isColumnReverse) {
                Collections.reverse(line);
            }
            List<FlexItemCalculationInfo> lineToJustify = new ArrayList<>();
            for (int i = 0; i < line.size(); ++i) {
                FlexItemCalculationInfo itemInfo = line.get(i);
                if (i != 0 && isColumnDirection(renderer) && !isFixedHeightAppliedOnTheCurrentPage &&
                        lines.size() == 1 &&
                        childrenMainSize + itemInfo.getOuterMainSize(itemInfo.mainSize) > mainSize + EPSILON) {
                    break;
                }
                childrenMainSize += itemInfo.getOuterMainSize(itemInfo.mainSize);
                lineToJustify.add(itemInfo);
            }
            // Reverse back
            if (isColumnReverse) {
                Collections.reverse(line);
                Collections.reverse(lineToJustify);
            }
            float freeSpace = 0;
            if (!isColumnDirection(renderer)) {
                freeSpace = mainSize - childrenMainSize;
            } else if (containsFixedHeight) {
                // In case of column direction we should align only if container contains fixed height
                freeSpace = isFixedHeightAppliedOnTheCurrentPage ? containerMainSize - childrenMainSize :
                        Math.max(0, mainSize - childrenMainSize);
            }
            renderer.getFlexItemMainDirector().applyJustifyContent(lineToJustify, justifyContent, freeSpace);
        }
        if (renderer.isWrapReverse()) {
            Collections.reverse(lines);
        }
    }

    private static float calculateFreeSpace(final List<FlexItemCalculationInfo> line, final float initialFreeSpace) {
        float result = initialFreeSpace;
        for (FlexItemCalculationInfo info : line) {
            if (info.isFrozen) {
                result -= info.getOuterMainSize(info.mainSize);
            } else {
                result -= info.getOuterMainSize(info.flexBaseSize);
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

    static boolean isZero(final float value) {
        return Math.abs(value) < EPSILON;
    }

    private static List<FlexItemCalculationInfo> createFlexItemCalculationInfos(
            FlexContainerRenderer flexContainerRenderer, float flexContainerMainSize, float crossSize) {
        final List<IRenderer> childRenderers = flexContainerRenderer.getChildRenderers();
        final List<FlexItemCalculationInfo> flexItems = new ArrayList<>();
        for (final IRenderer renderer : childRenderers) {
            if (renderer instanceof AbstractRenderer) {
                AbstractRenderer abstractRenderer = (AbstractRenderer) renderer;

                // TODO DEVSIX-5091 improve determining of the flex base size when flex-basis: content
                float maxMainSize = calculateMaxMainSize(abstractRenderer, flexContainerMainSize,
                        isColumnDirection(flexContainerRenderer), crossSize);
                float flexBasis;
                boolean flexBasisContent = false;
                if (renderer.<UnitValue>getProperty(Property.FLEX_BASIS) == null) {
                    flexBasis = maxMainSize;
                    flexBasisContent = true;
                } else {
                    // For column layout layoutBox height should not be taken into account while calculating flexBasis
                    // in percents. If flex container doesn't have a definite size, flex basis percents should not be
                    // taken into account.
                    final float containerMainSize = isColumnDirection(flexContainerRenderer) ?
                            getMainSize(flexContainerRenderer, new Rectangle(0, 0)) : flexContainerMainSize;
                    flexBasis = (float) abstractRenderer.retrieveUnitValue(containerMainSize, Property.FLEX_BASIS);
                    if (AbstractRenderer.isBorderBoxSizing(abstractRenderer)) {
                        flexBasis -= AbstractRenderer.calculatePaddingBorderWidth(abstractRenderer);
                    }
                }
                flexBasis = Math.max(flexBasis, 0);

                float flexGrow = (float) renderer.<Float>getProperty(Property.FLEX_GROW, FLEX_GROW_INITIAL_VALUE);

                float flexShrink = (float) renderer.<Float>getProperty(Property.FLEX_SHRINK, FLEX_SHRINK_INITIAL_VALUE);

                final FlexItemCalculationInfo flexItemInfo = new FlexItemCalculationInfo((AbstractRenderer) renderer,
                        flexBasis, flexGrow, flexShrink, flexContainerMainSize, flexBasisContent,
                        isColumnDirection(flexContainerRenderer), crossSize);

                flexItems.add(flexItemInfo);
            }
        }
        return flexItems;
    }

    private static float calculateMaxMainSize(AbstractRenderer flexItemRenderer, float flexContainerMainSize,
                                              boolean isColumnDirection, float crossSize) {
        Float maxMainSize;
        if (flexItemRenderer instanceof TableRenderer) {
            // TODO DEVSIX-5214 we can't call TableRenderer#retrieveWidth method as far as it can throw NPE
            if (isColumnDirection) {
                Float itemRendererMaxHeight = flexItemRenderer.retrieveMaxHeight();
                maxMainSize = itemRendererMaxHeight;
                if (maxMainSize == null) {
                    maxMainSize = calculateHeight(flexItemRenderer, crossSize);
                }
            } else {
                maxMainSize = new Float(flexItemRenderer.getMinMaxWidth().getMaxWidth());
            }
            if (isColumnDirection) {
                maxMainSize = flexItemRenderer.applyMarginsBordersPaddings(
                        new Rectangle(0, (float) maxMainSize), false).getHeight();
            } else {
                maxMainSize = flexItemRenderer.applyMarginsBordersPaddings(
                        new Rectangle((float) maxMainSize, 0), false).getWidth();
            }
        } else {
            // We need to retrieve width and max-width manually because this methods take into account box-sizing
            maxMainSize = isColumnDirection ?
                    flexItemRenderer.retrieveHeight() : flexItemRenderer.retrieveWidth(flexContainerMainSize);
            if (maxMainSize == null) {
                maxMainSize = isColumnDirection ? retrieveMaxHeightForMainDirection(flexItemRenderer) :
                        flexItemRenderer.retrieveMaxWidth(flexContainerMainSize);
            }
            if (maxMainSize == null) {
                if (flexItemRenderer instanceof ImageRenderer) {
                    // TODO DEVSIX-5269 getMinMaxWidth doesn't always return the original image width
                    maxMainSize = isColumnDirection ? ((ImageRenderer) flexItemRenderer).getImageHeight()
                            : ((ImageRenderer) flexItemRenderer).getImageWidth();
                } else {
                    if (isColumnDirection) {
                        Float height = retrieveMaxHeightForMainDirection(flexItemRenderer);
                        if (height == null) {
                            height = calculateHeight(flexItemRenderer, crossSize);
                        }
                        maxMainSize = flexItemRenderer.applyMarginsBordersPaddings(
                                new Rectangle(0, (float) height), false).getHeight();
                    } else {
                        maxMainSize = flexItemRenderer.applyMarginsBordersPaddings(
                                new Rectangle(flexItemRenderer.getMinMaxWidth().getMaxWidth(), 0), false).getWidth();
                    }
                }
            }
        }
        return (float) maxMainSize;
    }

    private static List<Float> retrieveCurrentPageLineCrossSizes(FlexContainerRenderer flexContainerRenderer,
                                                                 List<List<FlexItemCalculationInfo>> lines,
                                                                 List<Float> lineCrossSizes, Float crossSize,
                                                                 Rectangle layoutBox) {
        float mainSize = getMainSize(flexContainerRenderer, new Rectangle(0, 0));
        boolean isColumnDirectionWithPagination = isColumnDirection(flexContainerRenderer) &&
                (mainSize < EPSILON || mainSize > layoutBox.getHeight() + EPSILON);
        if (!isColumnDirectionWithPagination || crossSize == null) {
            return lineCrossSizes;
        }

        List<Float> currentPageLineCrossSizes = new ArrayList<>();
        float flexLinesCrossSizesSum = 0;
        for (int i = 0; i < lineCrossSizes.size(); ++i) {
            float size = lineCrossSizes.get(i);
            if (flexLinesCrossSizesSum + size > crossSize + EPSILON ||
                    lines.get(i).get(0).mainSize > layoutBox.getHeight() + EPSILON) {
                if (i == 0) {
                    // We should add first line anyway
                    currentPageLineCrossSizes.add(size);
                }
                break;
            }
            flexLinesCrossSizesSum += size;
            currentPageLineCrossSizes.add(size);
        }
        return currentPageLineCrossSizes;
    }

    private static float calculateHeight(AbstractRenderer flexItemRenderer, float width) {
        LayoutResult result = flexItemRenderer.layout(new LayoutContext(
                new LayoutArea(1, new Rectangle(width, AbstractRenderer.INF))));
        return result.getStatus() == LayoutResult.NOTHING ? 0 : result.getOccupiedArea().getBBox().getHeight();
    }

    static class FlexItemCalculationInfo {
        AbstractRenderer renderer;
        float flexBasis;
        float flexShrink;
        float flexGrow;
        float minContent;
        float maxContent;

        float mainSize;
        float crossSize;

        float xShift;
        float yShift;

        // Calculation-related fields

        float scaledFlexShrinkFactor;
        boolean isFrozen = false;
        boolean isMinViolated = false;
        boolean isMaxViolated = false;
        float flexBaseSize;
        float hypotheticalMainSize;
        float hypotheticalCrossSize;
        boolean flexBasisContent;
        boolean isColumnDirection;

        public FlexItemCalculationInfo(AbstractRenderer renderer, float flexBasis,
                                       float flexGrow, float flexShrink, float areaMainSize, boolean flexBasisContent,
                                       boolean isColumnDirection, float crossSize) {
            this.isColumnDirection = isColumnDirection;
            this.flexBasisContent = flexBasisContent;
            this.renderer = renderer;
            this.flexBasis = flexBasis;
            if (flexShrink < 0) {
                throw new IllegalArgumentException(LayoutExceptionMessageConstant.FLEX_SHRINK_CANNOT_BE_NEGATIVE);
            }
            this.flexShrink = flexShrink;
            if (flexGrow < 0) {
                throw new IllegalArgumentException(LayoutExceptionMessageConstant.FLEX_GROW_CANNOT_BE_NEGATIVE);
            }
            this.flexGrow = flexGrow;
            Float definiteMinContent = isColumnDirection ?
                    retrieveMinHeightForMainDirection(renderer) : renderer.retrieveMinWidth(areaMainSize);
            // null means that min-width property is not set or has auto value. In both cases we should calculate it
            this.minContent = definiteMinContent == null ?
                    calculateMinContentAuto(areaMainSize, crossSize) : (float) definiteMinContent;
            Float maxMainSize = isColumnDirection ?
                    retrieveMaxHeightForMainDirection(this.renderer) : this.renderer.retrieveMaxWidth(areaMainSize);
            // As for now we assume that max width should be calculated so
            this.maxContent = maxMainSize == null ? AbstractRenderer.INF : (float) maxMainSize;
        }

        public Rectangle toRectangle() {
            return isColumnDirection ?
                    new Rectangle(xShift, yShift, getOuterCrossSize(crossSize), getOuterMainSize(mainSize)) :
                    new Rectangle(xShift, yShift, getOuterMainSize(mainSize), getOuterCrossSize(crossSize));
        }

        float getOuterMainSize(float size) {
            return isColumnDirection ?
                    renderer.applyMarginsBordersPaddings(new Rectangle(0, size), true).getHeight() :
                    renderer.applyMarginsBordersPaddings(new Rectangle(size, 0), true).getWidth();
        }

        float getInnerMainSize(float size) {
            return isColumnDirection ?
                    renderer.applyMarginsBordersPaddings(new Rectangle(0, size), false).getHeight() :
                    renderer.applyMarginsBordersPaddings(new Rectangle(size, 0), false).getWidth();
        }

        float getOuterCrossSize(float size) {
            return isColumnDirection ?
                    renderer.applyMarginsBordersPaddings(new Rectangle(size, 0), true).getWidth() :
                    renderer.applyMarginsBordersPaddings(new Rectangle(0, size), true).getHeight();
        }

        float getInnerCrossSize(float size) {
            return isColumnDirection ?
                    renderer.applyMarginsBordersPaddings(new Rectangle(size, 0), false).getWidth() :
                    renderer.applyMarginsBordersPaddings(new Rectangle(0, size), false).getHeight();
        }

        private float calculateMinContentAuto(float flexContainerMainSize, float crossSize) {
            // Automatic Minimum Size of Flex Items https://www.w3.org/TR/css-flexbox-1/#content-based-minimum-size
            Float specifiedSizeSuggestion = calculateSpecifiedSizeSuggestion(flexContainerMainSize);
            float contentSizeSuggestion = calculateContentSizeSuggestion(flexContainerMainSize, crossSize);
            if (renderer.hasAspectRatio() && specifiedSizeSuggestion == null) {
                // However, if the box has an aspect ratio and no specified size,
                // its content-based minimum size is the smaller of its content size suggestion
                // and its transferred size suggestion
                Float transferredSizeSuggestion = calculateTransferredSizeSuggestion(flexContainerMainSize);
                if (transferredSizeSuggestion == null) {
                    return contentSizeSuggestion;
                } else {
                    return Math.min(contentSizeSuggestion, (float) transferredSizeSuggestion);
                }
            } else if (specifiedSizeSuggestion == null) {
                // If the box has neither a specified size suggestion nor an aspect ratio,
                // its content-based minimum size is the content size suggestion.
                return contentSizeSuggestion;
            } else {
                // In general, the content-based minimum size of a flex item is the smaller
                // of its content size suggestion and its specified size suggestion
                return Math.min(contentSizeSuggestion, (float) specifiedSizeSuggestion);
            }
        }

        /**
         * If the item has an intrinsic aspect ratio and its computed cross size property is definite,
         * then the transferred size suggestion is that size (clamped by its min and max cross size properties
         * if they are definite), converted through the aspect ratio. It is otherwise undefined.
         *
         * @return transferred size suggestion if it can be calculated, null otherwise
         */
        private Float calculateTransferredSizeSuggestion(float flexContainerMainSize) {
            Float transferredSizeSuggestion = null;
            Float crossSize = isColumnDirection ?
                    renderer.retrieveWidth(flexContainerMainSize) : renderer.retrieveHeight();
            if (renderer.hasAspectRatio() && crossSize != null) {
                transferredSizeSuggestion = crossSize * renderer.getAspectRatio();

                transferredSizeSuggestion = clampValueByCrossSizesConvertedThroughAspectRatio(
                        (float) transferredSizeSuggestion, flexContainerMainSize);
            }
            return transferredSizeSuggestion;
        }

        /**
         * If the item’s computed main size property is definite,
         * then the specified size suggestion is that size (clamped by its max main size property if it’s definite).
         * It is otherwise undefined.
         *
         * @param flexContainerMainSize the width of the flex container
         * @return specified size suggestion if it's definite, null otherwise
         */
        private Float calculateSpecifiedSizeSuggestion(float flexContainerMainSize) {
            Float mainSizeSuggestion = null;
            if (isColumnDirection) {
                if (renderer.hasProperty(Property.HEIGHT)) {
                    mainSizeSuggestion = renderer.retrieveHeight();
                }
            } else {
                if (renderer.hasProperty(Property.WIDTH)) {
                    mainSizeSuggestion = renderer.retrieveWidth(flexContainerMainSize);
                }
            }

            return mainSizeSuggestion;
        }

        /**
         * The content size suggestion is the min-content size in the main axis, clamped, if it has an aspect ratio,
         * by any definite min and max cross size properties converted through the aspect ratio,
         * and then further clamped by the max main size property if that is definite.
         *
         * @param flexContainerMainSize the width of the flex container
         * @return content size suggestion
         */
        private float calculateContentSizeSuggestion(float flexContainerMainSize, float crossSize) {
            final UnitValue rendererWidth = renderer.<UnitValue>replaceOwnProperty(Property.WIDTH, null);
            final UnitValue rendererHeight = renderer.<UnitValue>replaceOwnProperty(Property.HEIGHT, null);
            float minContentSize;
            if (isColumnDirection) {
                Float height = retrieveMinHeightForMainDirection(renderer);
                if (height == null) {
                    height = calculateHeight(renderer, crossSize);
                }
                minContentSize = getInnerMainSize((float) height);
            } else {
                MinMaxWidth minMaxWidth = renderer.getMinMaxWidth();
                minContentSize = getInnerMainSize(minMaxWidth.getMinWidth());
            }
            renderer.returnBackOwnProperty(Property.HEIGHT, rendererHeight);
            renderer.returnBackOwnProperty(Property.WIDTH, rendererWidth);

            if (renderer.hasAspectRatio()) {
                minContentSize =
                        clampValueByCrossSizesConvertedThroughAspectRatio(minContentSize, flexContainerMainSize);
            }
            Float maxMainSize = isColumnDirection ? retrieveMaxHeightForMainDirection(renderer)
                    : renderer.retrieveMaxWidth(flexContainerMainSize);
            if (maxMainSize == null) {
                maxMainSize = AbstractRenderer.INF;
            }

            return Math.min(minContentSize, (float) maxMainSize);
        }

        private float clampValueByCrossSizesConvertedThroughAspectRatio(float value, float flexContainerMainSize) {
            Float maxCrossSize = isColumnDirection ?
                    renderer.retrieveMaxWidth(flexContainerMainSize) : renderer.retrieveMaxHeight();
            if (maxCrossSize == null ||
                    !renderer.hasProperty(isColumnDirection ? Property.MAX_WIDTH : Property.MAX_HEIGHT)) {
                maxCrossSize = AbstractRenderer.INF;
            }
            Float minCrossSize = isColumnDirection
                    ? renderer.retrieveMinWidth(flexContainerMainSize) : renderer.retrieveMinHeight();
            if (minCrossSize == null
                    || !renderer.hasProperty(isColumnDirection ? Property.MIN_WIDTH : Property.MIN_HEIGHT)) {
                minCrossSize = 0F;
            }

            return Math.min(
                    Math.max((float) (minCrossSize * renderer.getAspectRatio()), value),
                    (float) (maxCrossSize * renderer.getAspectRatio()));
        }
    }
}
