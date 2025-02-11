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
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.PdfDocument;

/**
 * This class holds instances which required for drawing on pdf document.
 */
public class DrawContext {

    private PdfDocument document;
    private PdfCanvas canvas;
    private boolean taggingEnabled;

    /**
     * Create drawing context by setting document and pdf canvas on which drawing will be performed.
     *
     * @param document pdf document
     * @param canvas canvas to draw on
     */
    public DrawContext(PdfDocument document, PdfCanvas canvas) {
        this(document, canvas, false);
    }

    /**
     * Create drawing context by setting document and pdf canvas on which drawing will be performed.
     *
     * @param document pdf document
     * @param canvas canvas to draw on
     * @param enableTagging if true document drawing operations will be appropriately tagged
     */
    public DrawContext(PdfDocument document, PdfCanvas canvas, boolean enableTagging) {
        this.document = document;
        this.canvas = canvas;
        this.taggingEnabled = enableTagging;
    }

    /**
     * Get pdf document.
     *
     * @return {@code PdfDocument} instance
     */
    public PdfDocument getDocument() {
        return document;
    }

    /**
     * Get pdf canvas.
     *
     * @return {@code PdfCanvas} instance
     */
    public PdfCanvas getCanvas() {
        return canvas;
    }

    /**
     * Get document tagging property.
     *
     * @return true if tagging is enabled, false otherwise
     */
    public boolean isTaggingEnabled() {
        return taggingEnabled;
    }

    /**
     * Set document tagging property.
     *
     * @param taggingEnabled true if to enable tagging, false to disable it
     */
    public void setTaggingEnabled(boolean taggingEnabled) {
        this.taggingEnabled = taggingEnabled;
    }
}
