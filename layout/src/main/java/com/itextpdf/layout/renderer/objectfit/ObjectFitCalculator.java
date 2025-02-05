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
package com.itextpdf.layout.renderer.objectfit;

import com.itextpdf.layout.properties.ObjectFit;

/**
 * Utility class which supports the {@link ObjectFit} property.
 */
public final class ObjectFitCalculator {

    private ObjectFitCalculator() {}

    /**
     * Calculates size of image to be render when certain {@link ObjectFit} mode is
     * applied. The width or the height of the image might be greater than the same
     * property of the image in a document. In this case parts of the image will not
     * be shown.
     *
     * @param objectFit            is an object-fit mode
     * @param absoluteImageWidth   is a width of the original image
     * @param absoluteImageHeight  is a height of the original image
     * @param imageContainerWidth  is a width of the image to draw
     * @param imageContainerHeight is a width of the image to draw
     * @return results of object-fit mode applying as an {@link ObjectFitApplyingResult} object
     */
    public static ObjectFitApplyingResult calculateRenderedImageSize(ObjectFit objectFit, 
            double absoluteImageWidth, double absoluteImageHeight, double imageContainerWidth,
            double imageContainerHeight) {

        switch (objectFit) {
            case FILL:
                return processFill(imageContainerWidth, imageContainerHeight);
            case CONTAIN:
                return processContain(absoluteImageWidth, absoluteImageHeight, imageContainerWidth,
                        imageContainerHeight);
            case COVER:
                return processCover(absoluteImageWidth, absoluteImageHeight, imageContainerWidth,
                        imageContainerHeight);
            case SCALE_DOWN:
                return processScaleDown(absoluteImageWidth, absoluteImageHeight, imageContainerWidth,
                        imageContainerHeight);
            case NONE:
                return processNone(absoluteImageWidth, absoluteImageHeight, imageContainerWidth,
                        imageContainerHeight);
            default:
                throw new IllegalArgumentException("Object fit parameter cannot be null!");
        }
    }

    private static ObjectFitApplyingResult processFill(double imageContainerWidth,
            double imageContainerHeight) {
        return new ObjectFitApplyingResult(imageContainerWidth, imageContainerHeight, false);
    }

    private static ObjectFitApplyingResult processContain(double absoluteImageWidth,
            double absoluteImageHeight, double imageContainerWidth, double imageContainerHeight) {
        return processToFitSide(absoluteImageWidth, absoluteImageHeight, imageContainerWidth,
                imageContainerHeight, false);
    }

    private static ObjectFitApplyingResult processCover(double absoluteImageWidth,
            double absoluteImageHeight, double imageContainerWidth, double imageContainerHeight) {
        return processToFitSide(absoluteImageWidth, absoluteImageHeight, imageContainerWidth,
                imageContainerHeight, true);
    }

    private static ObjectFitApplyingResult processScaleDown(double absoluteImageWidth,
            double absoluteImageHeight, double imageContainerWidth, double imageContainerHeight) {
        if (imageContainerWidth >= absoluteImageWidth &&
                imageContainerHeight >= absoluteImageHeight) {
            return new ObjectFitApplyingResult(absoluteImageWidth, absoluteImageHeight, false);
        } else {
            return processToFitSide(absoluteImageWidth, absoluteImageHeight, imageContainerWidth,
                    imageContainerHeight, false);
        }
    }

    private static ObjectFitApplyingResult processNone(double absoluteImageWidth,
            double absoluteImageHeight, double imageContainerWidth, double imageContainerHeight) {
        final boolean doesObjectFitRequireCutting = imageContainerWidth <= absoluteImageWidth ||
                imageContainerHeight <= absoluteImageHeight;
        return new ObjectFitApplyingResult(absoluteImageWidth, absoluteImageHeight,
                doesObjectFitRequireCutting);
    }

    private static ObjectFitApplyingResult processToFitSide(double absoluteImageWidth,
            double absoluteImageHeight, double imageContainerWidth, double imageContainerHeight,
            boolean clipToFit) {
        final double widthCoeff = imageContainerWidth / absoluteImageWidth;
        final double heightCoeff = imageContainerHeight / absoluteImageHeight;

        double renderedImageWidth;
        double renderedImageHeight;

        final boolean isWidthFitted = heightCoeff > widthCoeff ^ clipToFit;

        if (isWidthFitted) {
            renderedImageWidth = imageContainerWidth;
            renderedImageHeight = absoluteImageHeight * imageContainerWidth / absoluteImageWidth;
        } else {
            renderedImageHeight = imageContainerHeight;
            renderedImageWidth = absoluteImageWidth * imageContainerHeight / absoluteImageHeight;
        }

        return new ObjectFitApplyingResult(renderedImageWidth, renderedImageHeight, clipToFit);
    }
}
