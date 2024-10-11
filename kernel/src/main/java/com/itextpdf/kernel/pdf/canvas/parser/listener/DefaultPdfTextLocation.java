/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.kernel.pdf.canvas.parser.listener;

import com.itextpdf.kernel.geom.Rectangle;

/**
 * This class acts as a default implementation of IPdfTextLocation
 */
public class DefaultPdfTextLocation implements IPdfTextLocation {
    private Rectangle rectangle;
    private String text;

    /**
     * Creates new pdf text location.
     *
     * @param rect text rectangle on pdf canvas
     * @param text actual text on designated area of canvas
     */
    public DefaultPdfTextLocation(Rectangle rect, String text) {
        this.rectangle = rect;
        this.text = text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rectangle getRectangle() {
        return rectangle;
    }

    /**
     * Sets text rectangle (occupied area) for this pdf text location.
     *
     * @param rectangle new text rectangle
     * @return this {@code DefaultPdfTextLocation} instance
     */
    public DefaultPdfTextLocation setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText() {
        return text;
    }

    /**
     * Sets text for this pdf text location.
     *
     * @param text new text
     * @return this {@code DefaultPdfTextLocation} instance
     */
    public DefaultPdfTextLocation setText(String text) {
        this.text = text;
        return this;
    }
}
