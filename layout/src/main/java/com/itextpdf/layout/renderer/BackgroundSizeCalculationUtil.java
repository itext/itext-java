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

import com.itextpdf.layout.property.BackgroundImage;
import com.itextpdf.layout.property.BackgroundSize;
import com.itextpdf.layout.property.UnitValue;

/**
 * Utility class for calculate background image width and height.
 */
final class BackgroundSizeCalculationUtil {

    private static final int PERCENT_100 = 100;
    private static final UnitValue PERCENT_VALUE_100 = UnitValue.createPercentValue(100);

    private BackgroundSizeCalculationUtil() {
        //no instance required
    }

    /**
     * Calculates width and height values for image with a given area params.
     *
     * @param image      the {@link BackgroundImage} width and height of which you want to calculate
     * @param areaWidth  width of the area of this images
     * @param areaHeight height of the area of this images
     * @return array of two Float values. NOTE that first value defines width, second defines height.

     * @see BackgroundSize
     */
    public static float[] calculateBackgroundImageSize(BackgroundImage image, float areaWidth, float areaHeight) {
        boolean isGradient = image.getLinearGradientBuilder() != null;

        BackgroundSize size;
        if (!isGradient && image.getBackgroundSize().isSpecificSize()) {
            size = calculateBackgroundSizeForArea(image, areaWidth, areaHeight);
        } else {
            size = image.getBackgroundSize();
        }
        UnitValue width = size.getBackgroundWidthSize();
        UnitValue height = size.getBackgroundHeightSize();;

        Float[] widthAndHeight = new Float[2];

        if (width != null && width.getValue() >= 0) {
            boolean needScale = !isGradient && height == null;
            calculateBackgroundWidth(width, areaWidth, needScale, image, widthAndHeight);
        }
        if (height != null && height.getValue() >= 0) {
            boolean needScale = !isGradient && width == null;
            calculateBackgroundHeight(height, areaHeight, needScale, image, widthAndHeight);
        }
        setDefaultSizeIfNull(widthAndHeight, areaWidth, areaHeight, image, isGradient);
        return new float[] {(float) widthAndHeight[0], (float) widthAndHeight[1]};
    }

    private static BackgroundSize calculateBackgroundSizeForArea(BackgroundImage image,
            float areaWidth, float areaHeight) {
        double widthDifference = areaWidth / image.getImageWidth();
        double heightDifference = areaHeight / image.getImageHeight();
        if (image.getBackgroundSize().isCover()) {
            return createSizeWithMaxValueSide(widthDifference > heightDifference);
        } else if (image.getBackgroundSize().isContain()) {
            return createSizeWithMaxValueSide(widthDifference < heightDifference);
        } else {
            return new BackgroundSize();
        }
    }

    private static BackgroundSize createSizeWithMaxValueSide(boolean maxWidth) {
        BackgroundSize size = new BackgroundSize();
        if (maxWidth) {
            size.setBackgroundSizeToValues(PERCENT_VALUE_100, null);
        } else {
            size.setBackgroundSizeToValues(null, PERCENT_VALUE_100);
        }
        return size;
    }

    private static void calculateBackgroundWidth(UnitValue width, float areaWidth, boolean scale,
            BackgroundImage image, Float[] widthAndHeight) {
        if (scale) {
            if (width.isPercentValue()) {
                scaleWidth(areaWidth * width.getValue() / PERCENT_100, image, widthAndHeight);
            } else {
                scaleWidth(width.getValue(), image, widthAndHeight);
            }
        } else {
            if (width.isPercentValue()) {
                widthAndHeight[0] = areaWidth * width.getValue() / PERCENT_100;
            } else {
                widthAndHeight[0] = width.getValue();
            }
        }
    }

    private static void calculateBackgroundHeight(UnitValue height, float areaHeight, boolean scale,
            BackgroundImage image, Float[] widthAndHeight) {
        if (scale) {
            if (height.isPercentValue()) {
                scaleHeight(areaHeight * height.getValue() / PERCENT_100, image, widthAndHeight);
            } else {
                scaleHeight(height.getValue(), image, widthAndHeight);
            }
        } else {
            if (height.isPercentValue()) {
                widthAndHeight[1] = areaHeight * height.getValue() / PERCENT_100;
            } else {
                widthAndHeight[1] = height.getValue();
            }
        }
    }

    private static void scaleWidth(float newWidth, BackgroundImage image, Float[] imageWidthAndHeight) {
        float difference = image.getImageWidth() == 0f ? 1f : newWidth / image.getImageWidth();
        imageWidthAndHeight[0] = newWidth;
        imageWidthAndHeight[1] = image.getImageHeight() * difference;
    }

    private static void scaleHeight(float newHeight, BackgroundImage image, Float[] imageWidthAndHeight) {
        float difference = image.getImageHeight() == 0f ? 1f : newHeight / image.getImageHeight();
        imageWidthAndHeight[0] = image.getImageWidth() * difference;
        imageWidthAndHeight[1] = newHeight;
    }

    private static void setDefaultSizeIfNull(Float[] widthAndHeight, float areaWidth, float areaHeight,
            BackgroundImage image, boolean isGradient) {
        if (isGradient) {
            widthAndHeight[0] = widthAndHeight[0] == null ? areaWidth : widthAndHeight[0];
            widthAndHeight[1] = widthAndHeight[1] == null ? areaHeight : widthAndHeight[1];
        } else {
            widthAndHeight[0] = widthAndHeight[0] == null ? image.getImageWidth() : widthAndHeight[0];
            widthAndHeight[1] = widthAndHeight[1] == null ? image.getImageHeight() : widthAndHeight[1];
        }
    }
}
