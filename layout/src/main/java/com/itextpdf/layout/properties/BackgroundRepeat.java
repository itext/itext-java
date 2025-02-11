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
package com.itextpdf.layout.properties;

import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;

/**
 * Class to hold background-repeat property.
 */
public class BackgroundRepeat {
    private final BackgroundRepeatValue xAxisRepeat;
    private final BackgroundRepeatValue yAxisRepeat;

    /**
     * Creates a new {@link BackgroundRepeat} instance.
     * The axis will have the value {@link BackgroundRepeatValue#REPEAT}.
     */
    public BackgroundRepeat() {
        this(BackgroundRepeatValue.REPEAT);
    }

    /**
     * Creates a new {@link BackgroundRepeat} instance based on one {@link BackgroundRepeat} instance.
     *
     * @param repeat the repeat value that will be set for for both axes
     */
    public BackgroundRepeat(final BackgroundRepeatValue repeat) {
        this(repeat, repeat);
    }

    /**
     * Creates a new {@link BackgroundRepeat} instance based on two {@link BackgroundRepeat} instance.
     *
     * @param xAxisRepeat the repeat value that will be set for for X axis
     * @param yAxisRepeat the repeat value that will be set for for Y axis
     */
    public BackgroundRepeat(final BackgroundRepeatValue xAxisRepeat, final BackgroundRepeatValue yAxisRepeat) {
        this.xAxisRepeat = xAxisRepeat;
        this.yAxisRepeat = yAxisRepeat;
    }

    /**
     * Gets the {@link BackgroundRepeatValue} value for X axis.
     *
     * @return the repeat value for X axis.
     */
    public BackgroundRepeatValue getXAxisRepeat() {
        return xAxisRepeat;
    }

    /**
     * Gets the {@link BackgroundRepeatValue} value for Y axis.
     *
     * @return the repeat value for Y axis.
     */
    public BackgroundRepeatValue getYAxisRepeat() {
        return yAxisRepeat;
    }

    /**
     * Checks whether the {@link BackgroundRepeatValue#NO_REPEAT} value is set on X axis or not.
     *
     * @return is the X axis have {@link BackgroundRepeatValue#NO_REPEAT} value
     */
    public boolean isNoRepeatOnXAxis() {
        return xAxisRepeat == BackgroundRepeatValue.NO_REPEAT;
    }

    /**
     * Checks whether the {@link BackgroundRepeatValue#NO_REPEAT} value is set on Y axis or not.
     *
     * @return is the Y axis have {@link BackgroundRepeatValue#NO_REPEAT} value
     */
    public boolean isNoRepeatOnYAxis() {
        return yAxisRepeat == BackgroundRepeatValue.NO_REPEAT;
    }

    /**
     * Prepares the image rectangle for drawing. This means that the size and position of the image
     * rectangle will be changed to match the {@link BackgroundRepeatValue} values for the axes.
     *
     * @param imageRectangle the image rectangle which will be changed
     * @param backgroundArea the background available area
     * @param backgroundSize the image background size property
     * @return the necessary whitespace between backgrounds
     */
    public Point prepareRectangleToDrawingAndGetWhitespace(final Rectangle imageRectangle,
            final Rectangle backgroundArea, final BackgroundSize backgroundSize) {
        if (BackgroundRepeatValue.ROUND == xAxisRepeat) {
            final int ratio = BackgroundRepeat.calculateRatio(backgroundArea.getWidth(), imageRectangle.getWidth());
            final float initialImageRatio = imageRectangle.getHeight() / imageRectangle.getWidth();
            imageRectangle.setWidth(backgroundArea.getWidth() / ratio);
            if (BackgroundRepeatValue.ROUND != yAxisRepeat && backgroundSize.getBackgroundHeightSize() == null) {
                imageRectangle.moveUp(imageRectangle.getHeight() - imageRectangle.getWidth() * initialImageRatio);
                imageRectangle.setHeight(imageRectangle.getWidth() * initialImageRatio);
            }
        }
        if (BackgroundRepeatValue.ROUND == yAxisRepeat) {
            final int ratio = BackgroundRepeat.calculateRatio(backgroundArea.getHeight(), imageRectangle.getHeight());
            final float initialImageRatio = imageRectangle.getWidth() / imageRectangle.getHeight();
            imageRectangle.moveUp(imageRectangle.getHeight() - backgroundArea.getHeight() / ratio);
            imageRectangle.setHeight(backgroundArea.getHeight() / ratio);
            if (BackgroundRepeatValue.ROUND != xAxisRepeat && backgroundSize.getBackgroundWidthSize() == null) {
                imageRectangle.setWidth(imageRectangle.getHeight() * initialImageRatio);
            }
        }

        return processSpaceValueAndCalculateWhitespace(imageRectangle, backgroundArea);
    }

    private Point processSpaceValueAndCalculateWhitespace(final Rectangle imageRectangle, final Rectangle backgroundArea) {
        final Point whitespace = new Point();
        if (BackgroundRepeatValue.SPACE == xAxisRepeat) {
            if (imageRectangle.getWidth() * 2 <= backgroundArea.getWidth()) {
                imageRectangle.setX(backgroundArea.getX());
                whitespace.setLocation(BackgroundRepeat.calculateWhitespace(backgroundArea.getWidth(), imageRectangle.getWidth()), 0);
            } else {
                final float rightSpace = backgroundArea.getRight() - imageRectangle.getRight();
                final float leftSpace = imageRectangle.getLeft() - backgroundArea.getLeft();
                float xWhitespace = Math.max(rightSpace, leftSpace);
                xWhitespace = xWhitespace > 0 ? xWhitespace : 0;
                whitespace.setLocation(xWhitespace, 0);
            }
        }
        if (BackgroundRepeatValue.SPACE == yAxisRepeat) {
            if (imageRectangle.getHeight() * 2 <= backgroundArea.getHeight()) {
                imageRectangle.setY(backgroundArea.getY() + backgroundArea.getHeight() - imageRectangle.getHeight());
                whitespace.setLocation(whitespace.getX(), BackgroundRepeat.calculateWhitespace(backgroundArea.getHeight(), imageRectangle.getHeight()));
            } else {
                final float topSpace = backgroundArea.getTop() - imageRectangle.getTop();
                final float bottomSpace = imageRectangle.getBottom() - backgroundArea.getBottom();
                float yWhitespace = Math.max(topSpace, bottomSpace);
                yWhitespace = yWhitespace > 0 ? yWhitespace : 0;
                whitespace.setLocation(whitespace.getX(), yWhitespace);
            }
        }
        return whitespace;
    }

    private static int calculateRatio(float areaSize, float backgroundSize) {
        int ratio = (int) Math.floor(areaSize / backgroundSize);
        final float remainSpace = areaSize - (ratio * backgroundSize);
        if (remainSpace >= (backgroundSize / 2)) {
            ratio++;
        }
        return ratio == 0 ? 1 : ratio;
    }

    private static float calculateWhitespace(float areaSize, float backgroundSize) {
        float whitespace = 0;
        final int ratio = (int) Math.floor(areaSize / backgroundSize);
        if (ratio > 0) {
            whitespace = areaSize - (ratio * backgroundSize);
            if (ratio > 1) {
                whitespace /= (ratio - 1);
            }
        }
        return whitespace;
    }

    /**
     * Defines all possible background repeat values for one axis.
     */
    public enum BackgroundRepeatValue {
        /**
         * The no repeat value which mean that the background will not be repeated, but displayed once with its
         * original size.
         */
        NO_REPEAT,
        /**
         * The repeat value which means that the background with its original size will be repeated over the entire
         * available space.
         */
        REPEAT,
        /**
         * The round value which mean that the background will stretch or compress. Initially, the available space is
         * divided by module by the size of the background, if the result is less than half the size of the background,
         * then the background is stretched in such a way that when it is repeated it will take up all the space,
         * otherwise the background is compressed to fit one more background in the available space.
         */
        ROUND,
        /**
         * The space value which means that the background will be repeated as much as possible with its original size
         * and without cropping. the first and last backgrounds are attached to opposite edges of the available space,
         * and the whitespaces are evenly distributed between the backgrounds.
         */
        SPACE
    }
}
