/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

    private int pageNr;
    private Rectangle rectangle;
    private String text;

    public DefaultPdfTextLocation(int pageNr, Rectangle rect, String text) {
        this.pageNr = pageNr;
        this.rectangle = rect;
        this.text = text;
    }

    @Override
    public Rectangle getRectangle() {
        return rectangle;
    }

    public DefaultPdfTextLocation setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
        return this;
    }

    @Override
    public String getText() {
        return text;
    }

    public DefaultPdfTextLocation setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public int getPageNumber() {
        return pageNr;
    }

    public DefaultPdfTextLocation setPageNr(int pageNr) {
        this.pageNr = pageNr;
        return this;
    }
}
