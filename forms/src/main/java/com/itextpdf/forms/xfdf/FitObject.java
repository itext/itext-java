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
package com.itextpdf.forms.xfdf;

import com.itextpdf.forms.exceptions.XfdfException;
import com.itextpdf.kernel.pdf.PdfObject;

/**
 * Represent Fit, a child of the Dest element.
 * Content model: none.
 * Attributes: depends of type of Fit (FitH, FitB, FitV etc.).
 * For more details see paragraphs 6.5.13-6.5.19, 6.6.23 in Xfdf specification.
 */
public class FitObject {

    /**
     * Represents the page displayed by current Fit element.
     * Attribute of Fit, FitB, FitBH, FitBV, FitH, FitR, FitV, XYZ elements.
     */
    private final PdfObject page;

    /**
     * Vertical coordinate positioned at the top edge of the window.
     */
    private float top;

    /**
     * Vertical coordinate positioned at the bottom edge of the window.
     */
    private float bottom;

    /**
     * Horizontal coordinate positioned at the left edge of the window.
     */
    private float left;

    /**
     * Horizontal coordinate positioned at the right edge of the window.
     */
    private float right;

    /**
     * Corresponds to the zoom object in the destination syntax.
     * Attribute of XYZ object.
     */
    private float zoom;

    /**
     * Creates an instance of {@link FitObject}.
     *
     * @param page the page displayed by current Fit element
     */
    public FitObject(PdfObject page) {
        if (page == null) {
            throw new XfdfException(XfdfException.PAGE_IS_MISSING);
        }
        this.page = page;
    }

    /**
     * Gets the PdfObject representing the page displayed by current Fit element.
     * Attribute of Fit, FitB, FitBH, FitBV, FitH, FitR, FitV, XYZ elements.
     *
     * @return {@link PdfObject page} of the current Fit element.
     */
    public PdfObject getPage() {
        return page;
    }

    /**
     * Gets a float vertical coordinate positioned at the top edge of the window.
     *
     * @return top vertical coordinate.
     */
    public float getTop() {
        return top;
    }

    /**
     * Sets a float vertical coordinate positioned at the top edge of the window.
     *
     * @param top vertical coordinate value
     *
     * @return current {@link FitObject fit object}.
     */
    public FitObject setTop(float top) {
        this.top = top;
        return this;
    }

    /**
     * Gets a float horizontal coordinate positioned at the left edge of the window.
     *
     * @return left horizontal coordinate.
     */
    public float getLeft() {
        return left;
    }

    /**
     * Sets a float horizontal coordinate positioned at the left edge of the window.
     *
     * @param left horizontal coordinate value
     *
     * @return current {@link FitObject fit object}.
     */
    public FitObject setLeft(float left) {
        this.left = left;
        return this;
    }

    /**
     * Gets a float vertical coordinate positioned at the bottom edge of the window.
     *
     * @return bottom vertical coordinate.
     */
    public float getBottom() {
        return bottom;
    }

    /**
     * Sets a float vertical coordinate positioned at the bottom edge of the window.
     *
     * @param bottom vertical coordinate value
     *
     * @return current {@link FitObject fit object}.
     */
    public FitObject setBottom(float bottom) {
        this.bottom = bottom;
        return this;
    }

    /**
     * Gets a float horizontal coordinate positioned at the right edge of the window.
     *
     * @return right horizontal coordinate.
     */
    public float getRight() {
        return right;
    }

    /**
     * Sets a float horizontal coordinate positioned at the right edge of the window.
     *
     * @param right horizontal coordinate
     *
     * @return current {@link FitObject fit object}.
     */
    public FitObject setRight(float right) {
        this.right = right;
        return this;
    }

    /**
     * Gets a float representing the zoom ratio.
     * Attribute of XYZ object.
     *
     * @return zoom ratio value.
     */
    public float getZoom() {
        return zoom;
    }

    /**
     * Sets a float representing the zoom ratio.
     * Attribute of XYZ object.
     *
     * @param zoom ratio value
     *
     * @return current {@link FitObject fit object}.
     */
    public FitObject setZoom(float zoom) {
        this.zoom = zoom;
        return this;
    }
}
