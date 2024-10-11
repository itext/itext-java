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
package com.itextpdf.pdfua;

import com.itextpdf.kernel.pdf.PdfUAConformance;

/**
 * Class that holds the configuration for the PDF/UA document.
 */
public class PdfUAConfig {

    private final PdfUAConformance conformance;
    private final String title;
    private final String language;

    /**
     * Creates a new PdfUAConfig instance.
     *
     * @param conformance the conformance of the PDF/UA document
     * @param title the title of the PDF/UA document
     * @param language the language of the PDF/UA document
     */
    public PdfUAConfig(PdfUAConformance conformance, String title, String language) {
        this.conformance = conformance;
        this.title = title;
        this.language = language;
    }

    /**
     * Gets the UA conformance.
     *
     * @return The {@link PdfUAConformance}.
     */
    public PdfUAConformance getConformance() {
        return conformance;
    }

    /**
     * Gets the title.
     *
     * @return The title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the language.
     *
     * @return The language.
     */
    public String getLanguage() {
        return language;
    }

}
