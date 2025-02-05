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
package com.itextpdf.forms.fields;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;

/**
 * Builder for terminal form field.
 *
 * @param <T> specific terminal form field builder which extends this class.
 */
public abstract class TerminalFormFieldBuilder<T extends TerminalFormFieldBuilder<T>> extends FormFieldBuilder<T> {
    /**
     * Rectangle which defines widget placement.
     */
    private Rectangle widgetRectangle = null;
    /**
     * Page number to place widget at.
     */
    private int page = 0;
    private PdfFont font;

    /**
     * Creates builder for terminal form field creation.
     *
     * @param document document to be used for form field creation
     * @param formFieldName name of the form field
     */
    protected TerminalFormFieldBuilder(PdfDocument document, String formFieldName) {
        super(document, formFieldName);
    }

    /**
     * Gets rectangle which defines widget's placement.
     *
     * @return instance of {@link Rectangle} for widget placement
     */
    public Rectangle getWidgetRectangle() {
        return widgetRectangle;
    }

    /**
     * Gets page to be used for widget creation.
     *
     * @return number of page to place widget at
     */
    public int getPage() {
        return page;
    }

    /**
     * Sets page to be used for widget creation.
     *
     * @param page instance of {@link PdfPage}. Shall belong to already provided {@link PdfDocument}
     * @return this builder
     */
    public T setPage(PdfPage page) {
        this.page = getDocument().getPageNumber(page);
        return getThis();
    }

    /**
     * Sets page to be used for widget creation.
     *
     * @param page number of page to place widget at
     * @return this builder
     */
    public T setPage(int page) {
        this.page = page;
        return getThis();
    }

    /**
     * Sets rectangle which defines widget's placement.
     *
     * @param widgetRectangle instance of {@link Rectangle} for widget placement
     * @return this builder
     */
    public T setWidgetRectangle(Rectangle widgetRectangle) {
        this.widgetRectangle = widgetRectangle;
        return getThis();
    }

    void setPageToField(PdfFormField field) {
        if (page != 0) {
            field.getFirstFormAnnotation().setPage(page);
        }
    }
    void setPageToField(PdfFormAnnotation field) {
        if (page != 0) {
            field.setPage(page);
        }
    }

    /**
     * Set font to be used for form field creation.
     *
     * @param font instance of {@link PdfFont}.
     *
     * @return this builder
     */
    public T setFont(PdfFont font) {
        this.font = font;
        return getThis();
    }

    /**
     * Get font to be used for form field creation.
     *
     * @return instance of {@link PdfFont}.
     */
    public PdfFont getFont() {
        return font;
    }

}
