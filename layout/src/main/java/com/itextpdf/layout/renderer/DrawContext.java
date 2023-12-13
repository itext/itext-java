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
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.PdfDocument;

public class DrawContext {

    private PdfDocument document;
    private PdfCanvas canvas;
    private boolean taggingEnabled;

    public DrawContext(PdfDocument document, PdfCanvas canvas) {
        this(document, canvas, false);
    }

    public DrawContext(PdfDocument document, PdfCanvas canvas, boolean enableTagging) {
        this.document = document;
        this.canvas = canvas;
        this.taggingEnabled = enableTagging;
    }

    public PdfDocument getDocument() {
        return document;
    }

    public PdfCanvas getCanvas() {
        return canvas;
    }

    public boolean isTaggingEnabled() {
        return taggingEnabled;
    }

    public void setTaggingEnabled(boolean taggingEnabled) {
        this.taggingEnabled = taggingEnabled;
    }
}
