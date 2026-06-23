/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.kernel.colors.gradients;

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.PatternColor;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.HintOffsetType;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.OffsetType;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.NoninvertibleTransformException;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;
import com.itextpdf.kernel.pdf.colorspace.shading.AbstractPdfShading;
import com.itextpdf.kernel.pdf.function.AbstractPdfFunction;
import com.itextpdf.kernel.pdf.function.IPdfFunction;
import com.itextpdf.kernel.pdf.function.PdfType2Function;
import com.itextpdf.kernel.pdf.function.PdfType3Function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.LoggerFactory;

/**
 * Base class for gradient builders implementations.
 */
public abstract class AbstractGradientBuilder<T> {

    /**
     * The epsilon value used for data creation
     */
    private static final double ZERO_EPSILON = 1E-10;

    private final List<GradientColorStop> stops = new ArrayList<>();
    private GradientSpreadMethod spreadMethod = GradientSpreadMethod.NONE;

    /**
     * Adds the new color stop to the end ({@link AbstractGradientBuilder more info}).
     *
     * <p>
     * Note: if the previously added color stop's offset would have grater offset than the added
     * one, then the new offset would be normalized to be equal to the previous one. (Comparison
     * made between relative on coordinates vector offsets. If any of them has
     * the absolute offset, then the absolute value would be converted to relative first.)
     *
     * @param gradientColorStop the gradient stop color to add
     *
     * @return the current builder instance
     */
    public AbstractGradientBuilder<T> addStopColor(GradientColorStop gradientColorStop) {
        if (gradientColorStop != null) {
            this.stops.add(gradientColorStop);
        }
        return this;
    }

    /**
     * Set the spread method to use for the gradient
     *
     * @param gradientSpreadMethod the gradient spread method to set
     *
     * @return the current builder instance
     */
    public AbstractGradientBuilder<T> setSpread(GradientSpreadMethod gradientSpreadMethod) {
        if (gradientSpreadMethod != null) {
            this.spreadMethod = gradientSpreadMethod;
        } else {
            this.spreadMethod = GradientSpreadMethod.NONE;
        }
        return this;
    }

    /**
     * Get the copy of current color stops list. Note that the stop colors are not copied here
     *
     * @return the copy of current stop colors list
     */
    public List<GradientColorStop> getStopColors() {
        return new ArrayList<>(this.stops);
    }

    /**
     * Get the current spread method
     *
     * @return the current spread method
     */
    public GradientSpreadMethod getSpread() {
        return this.spreadMethod;
    }

    /**
     * Builds the {@link Color} object representing the gradient with specified configuration
     * that fills the target bounding box.
     *
     * @param targetBoundingBox the bounding box to be filled in current space
     * @param contextTransform  the transformation from the base coordinates space into
     *                          the current space. The {@code null} value is valid and can be used
     *                          if there is no transformation from base coordinates to current space
     *                          specified, or it is equal to identity transformation.
     * @param document          the {@link PdfDocument} for which the linear gradient would be built.
     *
     * @return the constructed {@link Color} or {@code null} if no color to be applied
     * or base gradient vector has been specified
     */
    // TODO: DEVSIX-4136 the document argument would be required for opaque gradients
    //  (as we would need to create a mask form xObject)
    public Color buildColor(Rectangle targetBoundingBox, AffineTransform contextTransform, PdfDocument document) {
        Tuple2<T[], AffineTransform> gradientVectorWithTransform = getGradientVectorWithTransform(
                targetBoundingBox, contextTransform);
        T[] baseCoordinatesVector = gradientVectorWithTransform == null ? null : gradientVectorWithTransform.getFirst();
        if (baseCoordinatesVector == null || this.stops.isEmpty()) {
            // Can not create gradient color with 0 stops or null coordinates vector
            return null;
        }

        // evaluate actual coordinates and transformation
        AffineTransform shadingTransform = new AffineTransform();
        if (contextTransform != null) {
            shadingTransform.concatenate(contextTransform);
        }

        AffineTransform gradientTransformation = gradientVectorWithTransform.getSecond();
        if (gradientTransformation != null) {
            try {
                if (targetBoundingBox != null) {
                    targetBoundingBox = Rectangle.calculateBBox(Arrays.asList(
                            gradientTransformation.inverseTransform(
                                    new Point(targetBoundingBox.getLeft(), targetBoundingBox.getBottom()), null),
                            gradientTransformation.inverseTransform(
                                    new Point(targetBoundingBox.getLeft(), targetBoundingBox.getTop()), null),
                            gradientTransformation.inverseTransform(
                                    new Point(targetBoundingBox.getRight(), targetBoundingBox.getBottom()), null),
                            gradientTransformation.inverseTransform(
                                    new Point(targetBoundingBox.getRight(), targetBoundingBox.getTop()), null)
                    ));
                }
                shadingTransform.concatenate(gradientTransformation);
            } catch (NoninvertibleTransformException e) {
                LoggerFactory.getLogger(getClass())
                        .error(IoLogMessageConstant.UNABLE_TO_INVERT_GRADIENT_TRANSFORMATION);
            }
        }

        AbstractPdfShading pdfShading = constructPdfShading(baseCoordinatesVector, this.stops, this.spreadMethod,
                targetBoundingBox);
        if (pdfShading == null) {
            return null;
        }

        PdfPattern.Shading shading = new PdfPattern.Shading(pdfShading);
        if (!shadingTransform.isIdentity()) {
            double[] matrix = new double[6];
            shadingTransform.getMatrix(matrix);
            shading.setMatrix(new PdfArray(matrix));
        }
        return new PatternColor(shading);
    }

    /**
     * Returns the base gradient vector in gradient vector space and the current space
     * to gradient vector space transformations that should be applied to the shading color.
     *
     * <p>
     * The vector would be set as shading coordinates vector and its length would be used
     * to translate all color stops absolute offsets into the relatives.
     *
     * <p>
     * The transformation should be invertible as the current target
     * bounding box coordinates should be transformed into the resulted shading space coordinates.
     *
     * @param targetBoundingBox the rectangle to be covered by constructed color in current space
     * @param contextTransform  the current canvas transformation
     *
     * @return the pair of the vector, which is an array of exactly two elements specifying
     * the gradient coordinates vector, and the additional transformation to be concatenated to
     * the current for resulted shading or {@code null} if no additional transformation is specified
     */
    // TODO: DEVSIX-8808 make this method abstract
    //  (default implementation was added for avoid breaking changes in AbstractLinearGradientBuilder)
    protected Tuple2<T[], AffineTransform> getGradientVectorWithTransform(Rectangle targetBoundingBox,
            AffineTransform contextTransform) {
        return null;
    }

    /**
     * Computes the minimal domain that covers the box with vector normals.
     * The domain corresponding to the initial vector is [0, 1].
     *
     * @param coords  the array of exactly two elements that describe
     *                the base vector (corresponding to [0,1] domain, that need to be adjusted
     *                to cover the box
     * @param toCover the box that needs to be covered
     *
     * @return the array of two elements in ascending order specifying the calculated covering
     * domain
     */
    protected abstract double[] computeCoveringDomain(T[] coords, Rectangle toCover);

    /**
     * Expand the base vector to cover the new domain
     *
     * @param newDomain  the array of exactly two elements that specifies the domain
     *                   that should be covered by the created vector
     * @param baseVector the array of exactly two elements that specifies the base vector
     *                   which corresponds to [0, 1] domain
     *
     * @return the array of two
     */
    protected abstract T[] createCoordsForNewDomain(double[] newDomain, T[] baseVector);

    /**
     * Creates some gradient vector which would cover the target bounding box.
     *
     * @param targetBoundingBox the bounding box to cover
     *
     * @return constructed gradient vector
     */
    protected abstract T[] createCoveringCoordinates(Rectangle targetBoundingBox);

    /**
     * Evaluates base vector length for future color stops spreading.
     *
     * @param coordinates the base vector
     *
     * @return base vector length
     */
    protected abstract double getBaseVectorLength(T[] coordinates);

    /**
     * Converts gradient vector into pdf coordinates array for shading.
     *
     * @param coordinates vector to convert
     *
     * @return constructed pdf array
     */
    protected abstract PdfArray createCoordsDictEntry(T[] coordinates);

    /**
     * Creates the gradient shading object.
     *
     * @param colorSpace        colorspace to use
     * @param coordinates       gradient coordinates
     * @param coordinatesDomain gradient coordinates domain
     * @param stopsFunction     color stops function
     *
     * @return constructed gradient shading
     */
    protected abstract AbstractPdfShading createPdfShading(
            PdfColorSpace colorSpace, PdfArray coordinates,
            PdfArray coordinatesDomain, IPdfFunction stopsFunction);

    /**
     * Constructs {@link AffineTransform} which represents transformation from current coordinates
     * into the coordinates in which domainStart would be (0,0), domain end would be on X axes
     * (i.e. (X,0) coordinate) and the surface scaled both on X and Y by {@code scale} factor.
     *
     * @param domainStart the point to become (0,0)
     * @param domainEnd   the point to be placed on X axes
     * @param scale       the scale factor
     *
     * @return the constructed transformation
     */
    protected static AffineTransform getToIntervalTransform(Point domainStart, Point domainEnd, double scale) {
        AffineTransform transform = new AffineTransform();
        double sin = -(domainEnd.getY() - domainStart.getY()) * scale;
        double cos = (domainEnd.getX() - domainStart.getX()) * scale;
        if (isZero(cos)) {
            cos = 0d;
            sin = sin > 0d ? 1d : -1d;
        } else if (isZero(sin)) {
            sin = 0d;
            cos = cos > 0d ? 1d : -1d;
        }
        transform.concatenate(new AffineTransform(cos, sin, -sin, cos, 0, 0));

        transform.scale(scale, scale);
        transform.translate(-domainStart.getX(), -domainStart.getY());
        return transform;
    }

    /**
     * Checks whether the argument is close to 0
     *
     * @param d to check
     *
     * @return {@code true} if the argument is close to 0
     */
    protected static boolean isZero(double d) {
        return Math.abs(d) < ZERO_EPSILON;
    }

    private AbstractPdfShading constructPdfShading(T[] baseCoordinatesVector,
            List<GradientColorStop> stops, GradientSpreadMethod spreadMethod, Rectangle targetBoundingBox) {

        double baseVectorLength = getBaseVectorLength(baseCoordinatesVector);

        List<GradientColorStop> stopsToConstruct = normalizeStops(stops, baseVectorLength);
        double[] coordinatesDomain = new double[] {0, 1};
        T[] actualCoordinates;
        if (isZero(baseVectorLength)
                || stopsToConstruct.size() == 1) {
            // single color case
            if (spreadMethod == GradientSpreadMethod.NONE) {
                return null;
            }
            actualCoordinates = createCoveringCoordinates(targetBoundingBox);

            GradientColorStop lastColorStop = stopsToConstruct.get(stopsToConstruct.size() - 1);
            stopsToConstruct = Arrays.asList(new GradientColorStop(lastColorStop, 0d, OffsetType.RELATIVE),
                    new GradientColorStop(lastColorStop, 1d, OffsetType.RELATIVE));
        } else {
            coordinatesDomain = computeCoveringDomain(baseCoordinatesVector, targetBoundingBox);
            if (spreadMethod == GradientSpreadMethod.REPEAT || spreadMethod == GradientSpreadMethod.REFLECT) {
                stopsToConstruct = adjustNormalizedStopsToCoverDomain(stopsToConstruct, coordinatesDomain,
                        spreadMethod);
            } else if (spreadMethod == GradientSpreadMethod.PAD) {
                adjustStopsForPadIfNeeded(stopsToConstruct, coordinatesDomain);
            } else {
                // none case
                double firstStopOffset = stopsToConstruct.get(0).getOffset();
                double lastStopOffset = stopsToConstruct.get(stopsToConstruct.size() - 1).getOffset();
                if (isZero(lastStopOffset - firstStopOffset)
                        || coordinatesDomain[1] <= firstStopOffset
                        || coordinatesDomain[0] >= lastStopOffset) {
                    return null;
                }
                coordinatesDomain[0] = Math.max(coordinatesDomain[0], firstStopOffset);
                coordinatesDomain[1] = Math.min(coordinatesDomain[1], lastStopOffset);
            }
            assert coordinatesDomain[0] <= coordinatesDomain[1];

            actualCoordinates = createCoordsForNewDomain(coordinatesDomain, baseCoordinatesVector);
        }

        return createPdfShading(new PdfDeviceCs.Rgb(), createCoordsDictEntry(actualCoordinates),
                new PdfArray(coordinatesDomain), constructFunction(stopsToConstruct));
    }

    // the result list would have the same list of stop colors as the original one
    // with all offsets on coordinates domain dimension and adjusted for ascending values
    private static List<GradientColorStop> normalizeStops(List<GradientColorStop> toNormalize,
            double baseVectorLength) {
        if (isZero(baseVectorLength)) {
            return Collections.singletonList(new GradientColorStop(toNormalize.get(toNormalize.size() - 1),
                    0d, OffsetType.RELATIVE));
        }
        // get rid of all absolute on vector offsets and hint offsets
        List<GradientColorStop> result = copyStopsAndNormalizeAbsoluteOffsets(toNormalize, baseVectorLength);
        // normalize 1st stop as it may be a special case
        normalizeFirstStopOffset(result);
        // now we have 1st stop with relative offset, all other stops are either auto or relative
        normalizeAutoStops(result);
        // normalize hints to left only none or relative to colors hint offset types
        normalizeHintsOffsets(result);

        return result;
    }

    private static void normalizeHintsOffsets(List<GradientColorStop> result) {
        // normalize all except last
        for (int i = 0; i < result.size() - 1; ++i) {
            GradientColorStop stopColor = result.get(i);
            if (stopColor.getHintOffsetType() == HintOffsetType.RELATIVE_ON_GRADIENT) {
                double currentStopOffset = stopColor.getOffset();
                double nextStopOffset = result.get(i + 1).getOffset();
                if (currentStopOffset == nextStopOffset) {
                    // if stops has the same offset, then no hint needed
                    stopColor.setHint(0, HintOffsetType.NONE);
                } else {
                    double hintOffset = (stopColor.getHintOffset() - currentStopOffset)
                            / (nextStopOffset - currentStopOffset);
                    stopColor.setHint(hintOffset, HintOffsetType.RELATIVE_BETWEEN_COLORS);
                }
            }
        }
        // the last color hint is not needed as even with pad and reflect it won't be used
        result.get(result.size() - 1).setHint(0, HintOffsetType.NONE);
    }

    private static void normalizeAutoStops(List<GradientColorStop> toNormalize) {
        assert toNormalize.get(0).getOffsetType() == OffsetType.RELATIVE;

        int firstAutoStopIndex = 1;
        GradientColorStop firstStopColor = toNormalize.get(0);
        double prevOffset = firstStopColor.getHintOffsetType() == HintOffsetType.RELATIVE_ON_GRADIENT
                ? firstStopColor.getHintOffset() : firstStopColor.getOffset();
        for (int i = 1; i < toNormalize.size(); ++i) {
            GradientColorStop currentStop = toNormalize.get(i);
            if (currentStop.getOffsetType() == OffsetType.AUTO) {
                if (currentStop.getHintOffsetType() == HintOffsetType.RELATIVE_ON_GRADIENT) {
                    double hintOffset = currentStop.getHintOffset();
                    normalizeAutoStops(toNormalize, firstAutoStopIndex, i + 1, prevOffset, hintOffset);
                    prevOffset = hintOffset;
                    firstAutoStopIndex = i + 1;
                }
            } else {
                if (firstAutoStopIndex < i) {
                    // current stop offset is relative
                    double offset = currentStop.getOffset();
                    normalizeAutoStops(toNormalize, firstAutoStopIndex, i, prevOffset, offset);
                }
                firstAutoStopIndex = i + 1;
                prevOffset = currentStop.getHintOffsetType() == HintOffsetType.RELATIVE_ON_GRADIENT
                        ? currentStop.getHintOffset() : currentStop.getOffset();
            }
        }

        // check whether the last interval has auto
        if (firstAutoStopIndex < toNormalize.size()) {
            double lastStopOffset = Math.max(1, prevOffset);
            normalizeAutoStops(toNormalize, firstAutoStopIndex, toNormalize.size(), prevOffset, lastStopOffset);
        }
    }

    private static void normalizeAutoStops(List<GradientColorStop> toNormalizeList,
            int fromIndex, int toIndex, double prevOffset, double nextOffset) {
        assert toIndex >= fromIndex;

        int intervalsCount = Math.min(toIndex, toNormalizeList.size() - 1) - fromIndex + 1;
        double offsetShift = (nextOffset - prevOffset) / intervalsCount;
        double currentOffset = prevOffset;
        for (int i = fromIndex; i < toIndex; ++i) {
            currentOffset += offsetShift;
            GradientColorStop currentAutoStop = toNormalizeList.get(i);

            assert currentAutoStop.getOffsetType() == OffsetType.AUTO;

            currentAutoStop.setOffset(currentOffset, OffsetType.RELATIVE);
        }
    }

    private static void normalizeFirstStopOffset(List<GradientColorStop> result) {
        // assert that all stops has no absolute on vector offsets and hints
        GradientColorStop firstStop = result.get(0);
        if (firstStop.getOffsetType() != OffsetType.AUTO) {
            return;
        }
        double firstStopOffset = 0;
        for (GradientColorStop stopColor : result) {
            if (stopColor.getOffsetType() == OffsetType.RELATIVE) {
                firstStopOffset = stopColor.getOffset();
                break;
            } else if (stopColor.getHintOffsetType() == HintOffsetType.RELATIVE_ON_GRADIENT) {
                firstStopOffset = stopColor.getHintOffset();
                break;
            }
        }
        firstStopOffset = Math.min(0, firstStopOffset);
        firstStop.setOffset(firstStopOffset, OffsetType.RELATIVE);
    }

    private static List<GradientColorStop> copyStopsAndNormalizeAbsoluteOffsets(List<GradientColorStop> toNormalize,
            double baseVectorLength) {
        double lastUsedOffset = Double.NEGATIVE_INFINITY;
        List<GradientColorStop> copy = new ArrayList<>(toNormalize.size());
        for (GradientColorStop stop : toNormalize) {
            double offset = stop.getOffset();
            OffsetType offsetType = stop.getOffsetType();
            if (offsetType == OffsetType.ABSOLUTE) {
                offsetType = OffsetType.RELATIVE;
                offset /= baseVectorLength;
            }

            if (offsetType == OffsetType.RELATIVE) {
                if (offset < lastUsedOffset) {
                    offset = lastUsedOffset;
                }
                lastUsedOffset = offset;
            }

            GradientColorStop result = new GradientColorStop(stop, offset, offsetType);

            double hintOffset = stop.getHintOffset();
            HintOffsetType hintOffsetType = stop.getHintOffsetType();
            if (hintOffsetType == HintOffsetType.ABSOLUTE_ON_GRADIENT) {
                hintOffsetType = HintOffsetType.RELATIVE_ON_GRADIENT;
                hintOffset /= baseVectorLength;
            }

            if (hintOffsetType == HintOffsetType.RELATIVE_ON_GRADIENT) {
                if (hintOffset < lastUsedOffset) {
                    hintOffset = lastUsedOffset;
                }
                lastUsedOffset = hintOffset;
            }

            result.setHint(hintOffset, hintOffsetType);
            copy.add(result);
        }
        return copy;
    }

    private static void adjustStopsForPadIfNeeded(List<GradientColorStop> stopsToConstruct,
            double[] coordinatesDomain) {
        GradientColorStop firstStop = stopsToConstruct.get(0);
        if (coordinatesDomain[0] < firstStop.getOffset()) {
            stopsToConstruct.add(0, new GradientColorStop(firstStop, coordinatesDomain[0], OffsetType.RELATIVE));
        }
        GradientColorStop lastStop = stopsToConstruct.get(stopsToConstruct.size() - 1);
        if (coordinatesDomain[1] > lastStop.getOffset()) {
            stopsToConstruct.add(new GradientColorStop(lastStop, coordinatesDomain[1], OffsetType.RELATIVE));
        }
    }

    private static List<GradientColorStop> adjustNormalizedStopsToCoverDomain(List<GradientColorStop> normalizedStops,
            double[] targetDomain, GradientSpreadMethod spreadMethod) {
        List<GradientColorStop> adjustedStops = new ArrayList<>();

        GradientColorStop lastColorStop = normalizedStops.get(normalizedStops.size() - 1);
        double originalIntervalEnd = lastColorStop.getOffset();
        double originalIntervalStart = normalizedStops.get(0).getOffset();
        double originalIntervalLength = originalIntervalEnd - originalIntervalStart;

        if (originalIntervalLength <= ZERO_EPSILON) {
            return Arrays.asList(new GradientColorStop(lastColorStop, targetDomain[0], OffsetType.RELATIVE),
                    new GradientColorStop(lastColorStop, targetDomain[1], OffsetType.RELATIVE));
        }

        double startIntervalsShift = Math.floor((targetDomain[0] - originalIntervalStart) / originalIntervalLength);
        double iterationOffset = originalIntervalStart + (originalIntervalLength * startIntervalsShift);
        boolean isIterationInverse =
                spreadMethod == GradientSpreadMethod.REFLECT && Math.abs(startIntervalsShift) % 2 != 0;

        int currentIterationIndex = isIterationInverse ? normalizedStops.size() - 1 : 0;

        double lastComputedOffset = iterationOffset;
        while (lastComputedOffset <= targetDomain[1]) {
            GradientColorStop currentStop = normalizedStops.get(currentIterationIndex);
            lastComputedOffset = isIterationInverse ?
                    iterationOffset + originalIntervalEnd - currentStop.getOffset()
                    : iterationOffset + currentStop.getOffset() - originalIntervalStart;
            GradientColorStop computedStop = new GradientColorStop(currentStop, lastComputedOffset,
                    OffsetType.RELATIVE);

            if (lastComputedOffset < targetDomain[0] && !adjustedStops.isEmpty()) {
                adjustedStops.set(0, computedStop);
            } else {
                adjustedStops.add(computedStop);
            }

            if (isIterationInverse) {
                --currentIterationIndex;
                if (currentIterationIndex < 0) {
                    iterationOffset += originalIntervalLength;
                    isIterationInverse = false;
                    currentIterationIndex = 1;
                }
            } else {
                ++currentIterationIndex;
                if (currentIterationIndex == normalizedStops.size()) {
                    iterationOffset += originalIntervalLength;
                    isIterationInverse = spreadMethod == GradientSpreadMethod.REFLECT;
                    currentIterationIndex = isIterationInverse ? normalizedStops.size() - 2 : 0;
                }
            }
            // check the next iteration type to set the correct stop color hint for just added stop
            if (isIterationInverse) {
                GradientColorStop nextColor = normalizedStops.get(currentIterationIndex);
                // this method should be invoked only after the normalization. it means that
                // the hint offset type for each stop is either relative to colors interval
                // (i.e. for inverse iteration we need to inverse the hint offset), or is none
                // (i.e. the hint offset value should be ignored)
                computedStop.setHint(1 - nextColor.getHintOffset(), nextColor.getHintOffsetType());
            } else {
                computedStop.setHint(currentStop.getHintOffset(), currentStop.getHintOffsetType());
            }
        }

        return adjustedStops;
    }

    private static IPdfFunction constructFunction(List<GradientColorStop> toConstruct) {
        int functionsAmount = toConstruct.size() - 1;

        double[] bounds = new double[functionsAmount - 1];
        List<AbstractPdfFunction<? extends PdfDictionary>> type2Functions = new ArrayList<>(functionsAmount);

        GradientColorStop currentStop;
        GradientColorStop nextStop = toConstruct.get(0);
        double domainStart = nextStop.getOffset();
        for (int i = 1; i < functionsAmount; ++i) {
            currentStop = nextStop;
            nextStop = toConstruct.get(i);
            bounds[i - 1] = nextStop.getOffset();
            type2Functions.add(constructSingleGradientSegmentFunction(currentStop, nextStop));
        }

        currentStop = nextStop;
        nextStop = toConstruct.get(toConstruct.size() - 1);
        type2Functions.add(constructSingleGradientSegmentFunction(currentStop, nextStop));
        double domainEnd = nextStop.getOffset();

        double[] encode = new double[functionsAmount * 2];
        for (int i = 0; i < encode.length; i += 2) {
            encode[i] = 0d;
            encode[i + 1] = 1d;
        }

        return new PdfType3Function(new double[] {domainStart, domainEnd}, null, type2Functions, bounds, encode);
    }

    private static AbstractPdfFunction<? extends PdfDictionary> constructSingleGradientSegmentFunction(
            GradientColorStop from, GradientColorStop to) {

        double exponent = 1d;
        float[] fromColor = from.getRgbArray();
        float[] toColor = to.getRgbArray();
        if (from.getHintOffsetType() == HintOffsetType.RELATIVE_BETWEEN_COLORS) {
            double hintOffset = from.getHintOffset();
            if (hintOffset <= 0d + ZERO_EPSILON) {
                fromColor = toColor;
            } else if (hintOffset >= 1d - ZERO_EPSILON) {
                toColor = fromColor;
            } else {
                // similar to css color hint logic
                exponent = Math.log(0.5) / Math.log(hintOffset);
            }
        }
        return new PdfType2Function(new float[] {0f, 1f}, null, fromColor, toColor, exponent);
    }
}
