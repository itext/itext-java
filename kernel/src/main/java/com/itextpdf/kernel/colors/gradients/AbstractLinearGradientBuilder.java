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
package com.itextpdf.kernel.colors.gradients;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.colors.PatternColor;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.NoninvertibleTransformException;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;
import com.itextpdf.kernel.pdf.colorspace.PdfShading;
import com.itextpdf.kernel.pdf.function.PdfFunction;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.HintOffsetType;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.OffsetType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.LoggerFactory;

/**
 * Base class for linear gradient builders implementations.
 * <p>
 * Color transitions for linear gradients are defined by a series of color stops along a gradient
 * vector. A gradient normal defines how the colors in a vector are painted to the surface. For
 * a linear gradient, a normal is a line perpendicular to the vector.
 * <p>
 * Contains the main logic that works with stop colors and creation of the resulted pdf color object.
 */
public abstract class AbstractLinearGradientBuilder {

    /**
     * The epsilon value used for data creation
     */
    protected static final double ZERO_EPSILON = 1E-10;

    private final List<GradientColorStop> stops = new ArrayList<>();
    private GradientSpreadMethod spreadMethod = GradientSpreadMethod.NONE;

    /**
     * Adds the new color stop to the end ({@link AbstractLinearGradientBuilder more info}).
     *
     * Note: if the previously added color stop's offset would have grater offset than the added
     * one, then the new offset would be normalized to be equal to the previous one. (Comparison
     * made between relative on coordinates vector offsets. If any of them has
     * the absolute offset, then the absolute value would converted to relative first.)
     *
     * @param gradientColorStop the gradient stop color to add
     * @return the current builder instance
     */
    public AbstractLinearGradientBuilder addColorStop(GradientColorStop gradientColorStop) {
        if (gradientColorStop != null) {
            this.stops.add(gradientColorStop);
        }
        return this;
    }

    /**
     * Set the spread method to use for the gradient
     *
     * @param gradientSpreadMethod the gradient spread method to set
     * @return the current builder instance
     */
    public AbstractLinearGradientBuilder setSpreadMethod(GradientSpreadMethod gradientSpreadMethod) {
        if (spreadMethod != null) {
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
    public List<GradientColorStop> getColorStops() {
        return new ArrayList<>(this.stops);
    }

    /**
     * Get the current spread method
     *
     * @return the current spread method
     */
    public GradientSpreadMethod getSpreadMethod() {
        return this.spreadMethod;
    }

    /**
     * Builds the {@link Color} object representing the linear gradient with specified configuration
     * that fills the target bounding box.
     *
     * @param targetBoundingBox the bounding box to be filled in current space
     * @param contextTransform  the transformation from the base coordinates space into
     *                          the current space. The {@code null} value is valid and can be used
     *                          if there is no transformation from base coordinates to current space
     *                          specified, or it is equal to identity transformation.
     * @return the constructed {@link Color}
     */
    public Color buildColor(Rectangle targetBoundingBox, AffineTransform contextTransform) {
        Point[] coordinates = getGradientVector(targetBoundingBox, contextTransform);
        if (coordinates == null || this.stops.isEmpty()) {
            // Can not create gradient color with 0 stops
            return null;
        } else if (this.stops.size() == 1 || coordinates[0].equals(coordinates[1])) {
            // single stop and zero vector case
            float[] lastStopRgb = this.stops.get(this.stops.size() - 1).getRgbArray();
            return new DeviceRgb(lastStopRgb[0], lastStopRgb[1], lastStopRgb[2]);
        }

        // evaluate actual coordinates and transformation
        Point[] baseCoordinatesVector = new Point[] {coordinates[0].getLocation(), coordinates[1].getLocation()};
        AffineTransform shadingTransform = new AffineTransform();
        if (contextTransform != null) {
            shadingTransform.concatenate(contextTransform);
        }

        AffineTransform gradientTransformation = getCurrentSpaceToGradientVectorSpaceTransformation(targetBoundingBox,
                contextTransform);
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
                LoggerFactory.getLogger(getClass()).error(LogMessageConstant.UNABLE_TO_INVERT_GRADIENT_TRANSFORMATION);
            }
        }

        PdfShading.Axial axial = createAxialShading(baseCoordinatesVector, this.stops, this.spreadMethod,
                targetBoundingBox);

        PdfPattern.Shading shading = new PdfPattern.Shading(axial);
        if (!shadingTransform.isIdentity()) {
            double[] matrix = new double[6];
            shadingTransform.getMatrix(matrix);
            shading.setMatrix(new PdfArray(matrix));
        }
        return new PatternColor(shading);
    }

    /**
     * Returns the base gradient vector in gradient vector space. This vector would be set
     * as shading coordinates vector and its length would be used to translate all color stops
     * absolute offsets into the relatives.
     *
     * @param targetBoundingBox the rectangle to be covered by constructed color in current space
     * @param contextTransform  the current canvas transformation
     * @return the array of exactly two elements specifying the gradient coordinates vector
     */
    protected abstract Point[] getGradientVector(Rectangle targetBoundingBox,
            AffineTransform contextTransform);

    /**
     * Returns the current space to gradient vector space transformations that should be applied
     * to the shading color. The transformation should be invertible as the current target
     * bounding box coordinates should be transformed into the resulted shading space coordinates.
     *
     * @param targetBoundingBox the rectangle to be covered by constructed color in current space
     * @param contextTransform  the current canvas transformation
     * @return the additional transformation to be concatenated to the current for resulted shading
     * or {@code null} if no additional transformation is specified
     */
    protected AffineTransform getCurrentSpaceToGradientVectorSpaceTransformation(
            Rectangle targetBoundingBox, AffineTransform contextTransform) {
        return null;
    }

    /**
     * Evaluates the minimal domain that covers the box with vector normals.
     * The domain corresponding to the initial vector is [0, 1].
     *
     * @param coords  the array of exactly two elements that describe
     *                the base vector (corresponding to [0,1] domain, that need to be adjusted
     *                to cover the box
     * @param toCover the box that needs to be covered
     * @return the array of two elements in ascending order specifying the calculated covering
     * domain
     */
    protected static double[] evaluateCoveringDomain(Point[] coords, Rectangle toCover) {
        if (toCover == null) {
            return new double[] {0d, 1d};
        }
        AffineTransform transform = new AffineTransform();
        double scale = 1d / (coords[0].distance(coords[1]));
        double sin = -(coords[1].getY() - coords[0].getY()) * scale;
        double cos = (coords[1].getX() - coords[0].getX()) * scale;
        if (Math.abs(cos) < ZERO_EPSILON) {
            cos = 0d;
            sin = sin > 0d ? 1d : -1d;
        } else if (Math.abs(sin) < ZERO_EPSILON) {
            sin = 0d;
            cos = cos > 0d ? 1d : -1d;
        }
        transform.concatenate(new AffineTransform(cos, sin, -sin, cos, 0, 0));

        transform.scale(scale, scale);
        transform.translate(-coords[0].getX(), -coords[0].getY());

        Point[] rectanglePoints = toCover.toPointsArray();
        double minX = transform.transform(rectanglePoints[0], null).getX();
        double maxX = minX;
        for (int i = 1; i < rectanglePoints.length; ++i) {
            double currentX = transform.transform(rectanglePoints[i], null).getX();
            minX = Math.min(minX, currentX);
            maxX = Math.max(maxX, currentX);
        }

        return new double[] {minX, maxX};
    }

    /**
     * Expand the base vector to cover the new domain
     *
     * @param newDomain  the array of exactly two elements that specifies the domain
     *                   that should be covered by the created vector
     * @param baseVector the array of exactly two elements that specifies the base vector
     *                   which corresponds to [0, 1] domain
     * @return the array of two
     */
    protected static Point[] createCoordinatesForNewDomain(double[] newDomain, Point[] baseVector) {
        double xDiff = baseVector[1].getX() - baseVector[0].getX();
        double yDiff = baseVector[1].getY() - baseVector[0].getY();

        Point[] targetCoords = new Point[] {
                baseVector[0].getLocation(),
                baseVector[1].getLocation()
        };
        targetCoords[0].translate(xDiff * newDomain[0], yDiff * newDomain[0]);
        targetCoords[1].translate(xDiff * (newDomain[1] - 1), yDiff * (newDomain[1] - 1));
        return targetCoords;
    }

    private static PdfShading.Axial createAxialShading(Point[] baseCoordinatesVector,
            List<GradientColorStop> stops, GradientSpreadMethod spreadMethod, Rectangle targetBoundingBox) {
        List<GradientColorStop> stopsToConstruct = normalizeStops(stops, baseCoordinatesVector);

        double[] coordinatesDomain = new double[] {0, 1};
        if (spreadMethod == GradientSpreadMethod.REPEAT || spreadMethod == GradientSpreadMethod.REFLECT) {
            coordinatesDomain = evaluateCoveringDomain(baseCoordinatesVector, targetBoundingBox);
            stopsToConstruct = adjustNormalizedStopsToCoverDomain(stopsToConstruct, coordinatesDomain, spreadMethod);
        } else {
            // to ensure that stops list covers the full domain. For repeat and reflect cases
            // this is done by adjusting the initial stops to cover the evaluated domain
            coordinatesDomain[0] = Math.max(coordinatesDomain[0], stopsToConstruct.get(0).getOffset());
            coordinatesDomain[1] = Math.min(coordinatesDomain[1],
                    stopsToConstruct.get(stopsToConstruct.size() - 1).getOffset());
            coordinatesDomain[1] = Math.max(coordinatesDomain[0], coordinatesDomain[1]);
        }

        // workaround for PAD case
        if (spreadMethod == GradientSpreadMethod.PAD) {
            coordinatesDomain = modifyNormalizedStopsForPad(stopsToConstruct, coordinatesDomain);
        }

        assert coordinatesDomain[0] <= coordinatesDomain[1];

        Point[] actualCoordinates = createCoordinatesForNewDomain(coordinatesDomain, baseCoordinatesVector);

        PdfShading.Axial axial = new PdfShading.Axial(
                new PdfDeviceCs.Rgb(),
                createCoordsPdfArray(actualCoordinates),
                new PdfArray(coordinatesDomain),
                constructFunction(stopsToConstruct)
        );
        // apply extended flag for PAD case
        if (spreadMethod == GradientSpreadMethod.PAD) {
            axial.setExtend(true, true);
        }
        return axial;
    }

    private static double[] modifyNormalizedStopsForPad(List<GradientColorStop> stopsToConstruct,
            double[] coordinatesDomain) {
        double[] newDomain = new double[] {coordinatesDomain[0], coordinatesDomain[1]};
        double eps = Math.max(1, (coordinatesDomain[1] - coordinatesDomain[0])) * 0.05;

        for (int i = stopsToConstruct.size() - 1; i > 0; --i) {
            GradientColorStop currentStop = stopsToConstruct.get(i);
            double currentStopOffset = currentStop.getOffset();
            if (Math.abs(currentStopOffset - coordinatesDomain[1]) < eps) {
                GradientColorStop prevStop = stopsToConstruct.get(i - 1);
                if ((prevStop.getHintOffsetType() == HintOffsetType.RELATIVE_BETWEEN_COLORS
                        && prevStop.getHintOffset() >= 1d - ZERO_EPSILON)
                        || (prevStop.getOffset() > currentStopOffset - eps)) {
                    double lastOffset = currentStopOffset + eps;
                    stopsToConstruct.add(i + 1, new GradientColorStop(currentStop, lastOffset, OffsetType.RELATIVE));
                    newDomain[1] = lastOffset;
                }
                break;
            }
            if (currentStopOffset < coordinatesDomain[1]) {
                break;
            }
        }

        for (int i = 0; i < stopsToConstruct.size() - 1; ++i) {
            GradientColorStop currentStop = stopsToConstruct.get(i);
            double currentStopOffset = currentStop.getOffset();
            if (Math.abs(currentStopOffset - coordinatesDomain[0]) < eps) {
                if ((currentStop.getHintOffsetType() == HintOffsetType.RELATIVE_BETWEEN_COLORS
                        && currentStop.getHintOffset() <= 0d + ZERO_EPSILON)
                        || (stopsToConstruct.get(i + 1).getOffset() < currentStopOffset + eps)) {
                    double firstOffset = currentStopOffset - eps;
                    stopsToConstruct.add(i, new GradientColorStop(currentStop, firstOffset, OffsetType.RELATIVE));
                    newDomain[0] = firstOffset;
                }
                break;
            }
            if (currentStopOffset > coordinatesDomain[0]) {
                break;
            }
        }
        return newDomain;
    }

    // the result list would have the same list of stop colors as the original one
    // with all offsets on coordinates domain dimension and adjusted for ascending values
    private static List<GradientColorStop> normalizeStops(List<GradientColorStop> toNormalize, Point[] coordinates) {
        double baseVectorLength = coordinates[1].distance(coordinates[0]);
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
                if (currentStopOffset != nextStopOffset) {
                    double hintOffset = (stopColor.getHintOffset() - currentStopOffset)
                            / (nextStopOffset - currentStopOffset);
                    stopColor.setHint(hintOffset, HintOffsetType.RELATIVE_BETWEEN_COLORS);
                } else {
                    // if stops has the same offset, then no hint needed
                    stopColor.setHint(0, HintOffsetType.NONE);
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

    private static PdfFunction constructFunction(List<GradientColorStop> toConstruct) {
        int functionsAmount = toConstruct.size() - 1;

        double[] bounds = new double[functionsAmount - 1];
        List<PdfFunction> type2Functions = new ArrayList<>(functionsAmount);

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

        return new PdfFunction.Type3(new PdfArray(new double[] {domainStart, domainEnd}), null,
                type2Functions, new PdfArray(bounds), new PdfArray(encode));
    }

    private static PdfFunction constructSingleGradientSegmentFunction(GradientColorStop from, GradientColorStop to) {
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
        return new PdfFunction.Type2(new PdfArray(new float[] {0f, 1f}), null,
                new PdfArray(fromColor), new PdfArray(toColor), new PdfNumber(exponent));
    }

    private static PdfArray createCoordsPdfArray(Point[] coordsPoints) {
        assert coordsPoints != null && coordsPoints.length == 2;

        return new PdfArray(new double[] {coordsPoints[0].getX(), coordsPoints[0].getY(),
                coordsPoints[1].getX(), coordsPoints[1].getY()});
    }

}
