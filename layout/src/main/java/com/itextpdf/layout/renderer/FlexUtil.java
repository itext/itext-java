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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.property.AlignmentPropertyValue;
import com.itextpdf.layout.property.FlexWrapPropertyValue;
import com.itextpdf.layout.property.JustifyContent;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

final class FlexUtil {

    private static final float EPSILON = 0.0001F;

    private static final float FLEX_GROW_INITIAL_VALUE = 0F;

    private static final float FLEX_SHRINK_INITIAL_VALUE = 1F;

    private static Logger logger = LoggerFactory.getLogger(FlexUtil.class);

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

        Float mainSize = flexContainerRenderer.retrieveWidth(layoutBox.getWidth());
        if (mainSize == null) {
            mainSize = layoutBox.getWidth();
        }
        // We need to have crossSize only if its value is definite.
        Float crossSize = flexContainerRenderer.retrieveHeight();
        Float minCrossSize = flexContainerRenderer.retrieveMinHeight();
        Float maxCrossSize = flexContainerRenderer.retrieveMaxHeight();

        List<FlexItemCalculationInfo> flexItemCalculationInfos =
                createFlexItemCalculationInfos(flexContainerRenderer, (float) mainSize);

        determineFlexBasisAndHypotheticalMainSizeForFlexItems(flexItemCalculationInfos);

        // 9.3. Main Size Determination

        // 5. Collect flex items into flex lines:
        boolean isSingleLine = !flexContainerRenderer.hasProperty(Property.FLEX_WRAP)
                || FlexWrapPropertyValue.NOWRAP == flexContainerRenderer.<FlexWrapPropertyValue>getProperty(
                Property.FLEX_WRAP);

        List<List<FlexItemCalculationInfo>> lines =
                collectFlexItemsIntoFlexLines(flexItemCalculationInfos, (float) mainSize, isSingleLine);

        // 6. Resolve the flexible lengths of all the flex items to find their used main size.
        // See §9.7 Resolving Flexible Lengths.

        // 9.7. Resolving Flexible Lengths
        resolveFlexibleLengths(lines, (float) mainSize);

        // 9.4. Cross Size Determination

        // 7. Determine the hypothetical cross size of each item by
        // performing layout with the used main size and the available space, treating auto as fit-content.
        determineHypotheticalCrossSizeForFlexItems(lines);

        // 8. Calculate the cross size of each flex line.
        List<Float> lineCrossSizes =
                calculateCrossSizeOfEachFlexLine(lines, isSingleLine, minCrossSize, crossSize, maxCrossSize);

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

        // 11. Determine the used cross size of each flex item.
        determineUsedCrossSizeOfEachFlexItem(lines, lineCrossSizes, flexContainerRenderer);

        // 9.5. Main-Axis Alignment
        // 12. Align the items along the main-axis per justify-content.
        applyJustifyContent(lines, flexContainerRenderer, (float) mainSize);

        // 9.6. Cross-Axis Alignment

        // TODO DEVSIX-5002 margin: auto is not supported
        // 13. Resolve cross-axis auto margins

        // 14. Align all flex items along the cross-axis
        applyAlignItemsAndAlignSelf(lines, flexContainerRenderer, lineCrossSizes);

        // 15. Determine the flex container’s used cross size

        // TODO DEVSIX-5164 16. Align all flex lines per align-content.

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
            List<FlexItemCalculationInfo> flexItemCalculationInfos) {
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
            Float rendererHeight = renderer.retrieveHeight();
            if (renderer.hasAspectRatio() &&
                    info.flexBasisContent && rendererHeight != null) {
                float aspectRatio = (float) renderer.getAspectRatio();
                info.flexBaseSize = (float) rendererHeight * aspectRatio;
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

    static void determineHypotheticalCrossSizeForFlexItems(List<List<FlexItemCalculationInfo>> lines) {
        for (List<FlexItemCalculationInfo> line : lines) {
            for (FlexItemCalculationInfo info : line) {
                UnitValue prevWidth = info.renderer.<UnitValue>replaceOwnProperty(Property.WIDTH,
                        UnitValue.createPointValue(info.mainSize));
                UnitValue prevMinWidth = info.renderer.<UnitValue>replaceOwnProperty(Property.MIN_WIDTH, null);
                LayoutResult result = info.renderer.layout(new LayoutContext(
                        new LayoutArea(0, new Rectangle(AbstractRenderer.INF, AbstractRenderer.INF))));
                info.renderer.returnBackOwnProperty(Property.MIN_WIDTH, prevMinWidth);
                info.renderer.returnBackOwnProperty(Property.WIDTH, prevWidth);
                // Since main size is clamped with min-width, we do expect the result to be full
                if (result.getStatus() == LayoutResult.FULL) {
                    info.hypotheticalCrossSize = info.getInnerCrossSize(result.getOccupiedArea().getBBox().getHeight());
                } else {
                    logger.error(LogMessageConstant.FLEX_ITEM_LAYOUT_RESULT_IS_NOT_FULL);
                    info.hypotheticalCrossSize = 0;
                }
            }
        }
    }

    static List<Float> calculateCrossSizeOfEachFlexLine(List<List<FlexItemCalculationInfo>> lines,
            boolean isSingleLine, Float minCrossSize, Float crossSize, Float maxCrossSize) {
        List<Float> lineCrossSizes = new ArrayList<>();
        if (isSingleLine && crossSize != null && !lines.isEmpty()) {
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
                    // TODO DEVSIX-5003 Condition "inline-axis is parallel to the main-axis" is not supported
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
                if (isSingleLine && !lines.isEmpty()) {
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

    static void handleAlignContentStretch(FlexContainerRenderer flexContainerRenderer, Float crossSize,
                                          float flexLinesCrossSizesSum, List<Float> lineCrossSizes) {
        AlignmentPropertyValue alignContent =
                (AlignmentPropertyValue) flexContainerRenderer.<AlignmentPropertyValue>getProperty(
                        Property.ALIGN_CONTENT, AlignmentPropertyValue.STRETCH);
        if (crossSize != null &&
                alignContent == AlignmentPropertyValue.STRETCH && flexLinesCrossSizesSum < crossSize - EPSILON) {
            float addition = ((float) crossSize - flexLinesCrossSizesSum) / lineCrossSizes.size();
            for (int i = 0; i < lineCrossSizes.size(); i++) {
                lineCrossSizes.set(i, lineCrossSizes.get(i) + addition);
            }
        }
    }

    static void determineUsedCrossSizeOfEachFlexItem(List<List<FlexItemCalculationInfo>> lines,
                                                     List<Float> lineCrossSizes,
                                                     FlexContainerRenderer flexContainerRenderer) {
        AlignmentPropertyValue alignItems =
                (AlignmentPropertyValue) flexContainerRenderer.<AlignmentPropertyValue>getProperty(
                        Property.ALIGN_ITEMS, AlignmentPropertyValue.STRETCH);

        assert lines.size() == lineCrossSizes.size();

        for (int i = 0; i < lines.size(); i++) {
            for (FlexItemCalculationInfo info : lines.get(i)) {
                // TODO DEVSIX-5002 margin: auto is not supported
                // TODO DEVSIX-5003 min/max height calculations are not implemented
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
                if ((alignSelf == AlignmentPropertyValue.STRETCH || alignSelf == AlignmentPropertyValue.NORMAL) &&
                        info.renderer.<UnitValue>getProperty(Property.HEIGHT) == null) {
                    info.crossSize = info.getInnerCrossSize(lineCrossSizes.get(i));
                    Float maxHeight = infoRenderer.retrieveMaxHeight();
                    if (maxHeight != null) {
                        info.crossSize = Math.min((float) maxHeight, info.crossSize);
                    }
                    Float minHeight = infoRenderer.retrieveMinHeight();
                    if (minHeight != null) {
                        info.crossSize = Math.max((float) minHeight, info.crossSize);
                    }
                } else {
                    info.crossSize = info.hypotheticalCrossSize;
                }
            }
        }
    }

    private static void applyAlignItemsAndAlignSelf(List<List<FlexItemCalculationInfo>> lines,
                                                    FlexContainerRenderer renderer, List<Float> lineCrossSizes) {
        AlignmentPropertyValue itemsAlignment = (AlignmentPropertyValue) renderer.<AlignmentPropertyValue>getProperty(
                Property.ALIGN_ITEMS, AlignmentPropertyValue.STRETCH);

        assert lines.size() == lineCrossSizes.size();

        for (int i = 0; i < lines.size(); ++i) {
            float lineCrossSize = lineCrossSizes.get(i);
            for (FlexItemCalculationInfo itemInfo : lines.get(i)) {
                AlignmentPropertyValue selfAlignment =
                        (AlignmentPropertyValue) itemInfo.renderer.<AlignmentPropertyValue>getProperty(
                                Property.ALIGN_SELF, itemsAlignment);

                float freeSpace = lineCrossSize - itemInfo.getOuterCrossSize(itemInfo.crossSize);

                switch (selfAlignment) {
                    case SELF_END:
                    case END:
                    case FLEX_END:
                        itemInfo.yShift = freeSpace;
                        break;
                    case CENTER:
                        itemInfo.yShift = freeSpace / 2;
                        break;
                    case START:
                    case BASELINE:
                    case SELF_START:
                    case STRETCH:
                    case NORMAL:
                    case FLEX_START:
                    default:
                        // We don't need to do anything in these cases
                }
            }
        }
    }

    private static void applyJustifyContent(List<List<FlexItemCalculationInfo>> lines,
                                            FlexContainerRenderer renderer, float mainSize) {
        JustifyContent justifyContent = (JustifyContent) renderer.<JustifyContent>getProperty(
                Property.JUSTIFY_CONTENT, JustifyContent.FLEX_START);

        for (List<FlexItemCalculationInfo> line : lines) {
            float childrenWidth = 0;
            for (FlexItemCalculationInfo itemInfo : line) {
                childrenWidth += itemInfo.getOuterMainSize(itemInfo.mainSize);
            }
            float freeSpace = mainSize - childrenWidth;

            switch (justifyContent) {
                case RIGHT:
                case END:
                case SELF_END:
                case FLEX_END:
                    line.get(0).xShift = freeSpace;
                    break;
                case CENTER:
                    line.get(0).xShift = freeSpace / 2;
                    break;
                case NORMAL:
                case STRETCH:
                case START:
                case LEFT:
                case SELF_START:
                case FLEX_START:
                default:
                    // We don't need to do anything in these cases
            }
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
            FlexContainerRenderer flexContainerRenderer, float flexContainerWidth) {
        final List<IRenderer> childRenderers = flexContainerRenderer.getChildRenderers();
        final List<FlexItemCalculationInfo> flexItems = new ArrayList<>();
        for (final IRenderer renderer : childRenderers) {
            if (renderer instanceof AbstractRenderer) {
                AbstractRenderer abstractRenderer = (AbstractRenderer) renderer;

                // TODO DEVSIX-5091 improve determining of the flex base size when flex-basis: content
                float maxWidth = calculateMaxWidth(abstractRenderer, flexContainerWidth);
                float flexBasis;
                boolean flexBasisContent = false;
                if (renderer.<UnitValue>getProperty(Property.FLEX_BASIS) == null) {
                    flexBasis = maxWidth;
                    flexBasisContent = true;
                } else {
                    flexBasis = (float) abstractRenderer.retrieveUnitValue(flexContainerWidth, Property.FLEX_BASIS);
                    if (AbstractRenderer.isBorderBoxSizing(abstractRenderer)) {
                        flexBasis -= AbstractRenderer.calculatePaddingBorderWidth(abstractRenderer);
                    }
                }
                flexBasis = Math.max(flexBasis, 0);

                float flexGrow = (float) renderer.<Float>getProperty(Property.FLEX_GROW, FLEX_GROW_INITIAL_VALUE);

                float flexShrink = (float) renderer.<Float>getProperty(Property.FLEX_SHRINK, FLEX_SHRINK_INITIAL_VALUE);

                final FlexItemCalculationInfo flexItemInfo = new FlexItemCalculationInfo((AbstractRenderer) renderer,
                        flexBasis, flexGrow, flexShrink, flexContainerWidth, flexBasisContent);

                flexItems.add(flexItemInfo);
            }
        }
        return flexItems;
    }

    private static float calculateMaxWidth(AbstractRenderer flexItemRenderer, float flexContainerWidth) {
        Float maxWidth;
        if (flexItemRenderer instanceof TableRenderer) {
            // TODO DEVSIX-5214 we can't call TableRenderer#retrieveWidth method as far as it can throw NPE
            maxWidth = flexItemRenderer.getMinMaxWidth().getMaxWidth();
            maxWidth = flexItemRenderer.applyMarginsBordersPaddings(
                    new Rectangle((float) maxWidth, 0), false).getWidth();
        } else {
            // We need to retrieve width and max-width manually because this methods take into account box-sizing
            maxWidth = flexItemRenderer.retrieveWidth(flexContainerWidth);
            if (maxWidth == null) {
                maxWidth = flexItemRenderer.retrieveMaxWidth(flexContainerWidth);
            }
            if (maxWidth == null) {
                if (flexItemRenderer instanceof ImageRenderer) {
                    // TODO DEVSIX-5269 getMinMaxWidth doesn't always return the original image width
                    maxWidth = ((ImageRenderer) flexItemRenderer).getImageWidth();
                } else {
                    maxWidth = flexItemRenderer.applyMarginsBordersPaddings(
                            new Rectangle(flexItemRenderer.getMinMaxWidth().getMaxWidth(), 0), false).getWidth();
                }
            }
        }
        return (float) maxWidth;
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

        public FlexItemCalculationInfo(AbstractRenderer renderer, float flexBasis,
                                       float flexGrow, float flexShrink, float areaWidth, boolean flexBasisContent) {
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
            Float definiteMinContent = renderer.retrieveMinWidth(areaWidth);
            // null means that min-width property is not set or has auto value. In both cases we should calculate it
            this.minContent =
                    definiteMinContent == null ? calculateMinContentAuto(areaWidth) : (float) definiteMinContent;
            Float maxWidth = this.renderer.retrieveMaxWidth(areaWidth);
            // As for now we assume that max width should be calculated so
            this.maxContent = maxWidth == null ? AbstractRenderer.INF : (float) maxWidth;
        }

        public Rectangle toRectangle() {
            return new Rectangle(xShift, yShift, getOuterMainSize(mainSize), getOuterCrossSize(crossSize));
        }

        float getOuterMainSize(float size) {
            return renderer.applyMarginsBordersPaddings(new Rectangle(size, 0), true).getWidth();
        }

        float getInnerMainSize(float size) {
            return renderer.applyMarginsBordersPaddings(new Rectangle(size, 0), false).getWidth();
        }

        float getOuterCrossSize(float size) {
            return renderer.applyMarginsBordersPaddings(new Rectangle(0, size), true).getHeight();
        }

        float getInnerCrossSize(float size) {
            return renderer.applyMarginsBordersPaddings(new Rectangle(0, size), false).getHeight();
        }

        private float calculateMinContentAuto(float flexContainerWidth) {
            // Automatic Minimum Size of Flex Items https://www.w3.org/TR/css-flexbox-1/#content-based-minimum-size
            Float specifiedSizeSuggestion = calculateSpecifiedSizeSuggestion(flexContainerWidth);
            float contentSizeSuggestion = calculateContentSizeSuggestion(flexContainerWidth);
            if (renderer.hasAspectRatio() && specifiedSizeSuggestion == null) {
                // However, if the box has an aspect ratio and no specified size,
                // its content-based minimum size is the smaller of its content size suggestion
                // and its transferred size suggestion
                Float transferredSizeSuggestion = calculateTransferredSizeSuggestion();
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
        private Float calculateTransferredSizeSuggestion() {
            Float transferredSizeSuggestion = null;
            Float height = renderer.retrieveHeight();
            if (renderer.hasAspectRatio() && height != null) {
                transferredSizeSuggestion = height * renderer.getAspectRatio();

                transferredSizeSuggestion =
                        clampValueByCrossSizesConvertedThroughAspectRatio((float) transferredSizeSuggestion);
            }
            return transferredSizeSuggestion;
        }

        /**
         * If the item’s computed main size property is definite,
         * then the specified size suggestion is that size (clamped by its max main size property if it’s definite).
         * It is otherwise undefined.
         *
         * @param flexContainerWidth the width of the flex container
         * @return specified size suggestion if it's definite, null otherwise
         */
        private Float calculateSpecifiedSizeSuggestion(float flexContainerWidth) {
            if (renderer.hasProperty(Property.WIDTH)) {
                return renderer.retrieveWidth(flexContainerWidth);
            } else {
                return null;
            }
        }

        /**
         * The content size suggestion is the min-content size in the main axis, clamped, if it has an aspect ratio,
         * by any definite min and max cross size properties converted through the aspect ratio,
         * and then further clamped by the max main size property if that is definite.
         *
         * @param flexContainerWidth the width of the flex container
         * @return content size suggestion
         */
        private float calculateContentSizeSuggestion(float flexContainerWidth) {
            final UnitValue rendererWidth = renderer.<UnitValue>replaceOwnProperty(Property.WIDTH, null);
            final UnitValue rendererHeight = renderer.<UnitValue>replaceOwnProperty(Property.HEIGHT, null);
            MinMaxWidth minMaxWidth = renderer.getMinMaxWidth();
            float minContentSize = getInnerMainSize(minMaxWidth.getMinWidth());
            renderer.returnBackOwnProperty(Property.HEIGHT, rendererHeight);
            renderer.returnBackOwnProperty(Property.WIDTH, rendererWidth);

            if (renderer.hasAspectRatio()) {
                minContentSize = clampValueByCrossSizesConvertedThroughAspectRatio(minContentSize);
            }
            Float maxWidth = renderer.retrieveMaxWidth(flexContainerWidth);
            if (maxWidth == null) {
                maxWidth = AbstractRenderer.INF;
            }

            return Math.min(minContentSize, (float) maxWidth);
        }

        private float clampValueByCrossSizesConvertedThroughAspectRatio(float value) {
            Float maxHeight = renderer.retrieveMaxHeight();
            if (maxHeight == null || !renderer.hasProperty(Property.MAX_HEIGHT)) {
                maxHeight = AbstractRenderer.INF;
            }
            Float minHeight = renderer.retrieveMinHeight();
            if (minHeight == null || !renderer.hasProperty(Property.MIN_HEIGHT)) {
                minHeight = 0F;
            }

            return Math.min(
                    Math.max((float) (minHeight * renderer.getAspectRatio()), value),
                    (float) (maxHeight * renderer.getAspectRatio()));
        }
    }
}
