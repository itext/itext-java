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
 * The class represents results of calculating of rendered image size
 * after applying of the {@link ObjectFit} property.
 */
public class ObjectFitApplyingResult {
    private double renderedImageWidth;
    private double renderedImageHeight;
    private boolean imageCuttingRequired;

    /**
     * Creates a new instance of the class with default values.
     */
    public ObjectFitApplyingResult() {
    }

    /**
     * Creates a new instance of the class.
     *
     * @param renderedImageWidth   is a width of the image to render
     * @param renderedImageHeight  is a height of the image to render
     * @param imageCuttingRequired is a flag showing if rendered image should be clipped
     *                             as its size is greater than size of the image container
     */
    public ObjectFitApplyingResult(double renderedImageWidth, double renderedImageHeight,
            boolean imageCuttingRequired) {
        this.renderedImageWidth = renderedImageWidth;
        this.renderedImageHeight = renderedImageHeight;
        this.imageCuttingRequired = imageCuttingRequired;
    }


    /**
     * Getter for width of rendered image.
     *
     * @return width of rendered image
     */
    public double getRenderedImageWidth() {
        return renderedImageWidth;
    }

    /**
     * Setter for width of rendered image.
     *
     * @param renderedImageWidth is a new width of rendered image
     */
    public void setRenderedImageWidth(double renderedImageWidth) {
        this.renderedImageWidth = renderedImageWidth;
    }

    /**
     * Getter for height of rendered image.
     *
     * @return height of rendered image
     */
    public double getRenderedImageHeight() {
        return renderedImageHeight;
    }

    /**
     * Setter for height of rendered image.
     *
     * @param renderedImageHeight is a new height of rendered image
     */
    public void setRenderedImageHeight(double renderedImageHeight) {
        this.renderedImageHeight = renderedImageHeight;
    }

    /**
     * Getter for a boolean value showing if at least one dimension of rendered image
     * is greater than expected image size. If true then image will be shown partially
     *
     * @return true if the image need to be cutting during rendering and false otherwise
     */
    public boolean isImageCuttingRequired() {
        return imageCuttingRequired;
    }

    /**
     * Setter for a boolean value showing if at least one dimension of rendered image
     * is greater than expected image size. If true then image will be shown partially
     *
     * @param imageCuttingRequired is a new value of the cutting-required flag
     */
    public void setImageCuttingRequired(boolean imageCuttingRequired) {
        this.imageCuttingRequired = imageCuttingRequired;
    }
}
