/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.layout.renderer.objectfit;

import com.itextpdf.layout.property.ObjectFit;

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
