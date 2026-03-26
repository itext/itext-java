/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.pdfua.wtpdf;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.WellTaggedPdfConformance;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that holds the configuration for the Well Tagged PDF document.
 */
public class WellTaggedPdfConfig {
    private final List<WellTaggedPdfConformance> conformance = new ArrayList<>();
    private final String title;
    private final String language;

    /**
     * Creates a new WellTaggedPdfConfig instance.
     *
     * @param conformance the conformance of the Well Tagged PDF document
     * @param title       the title of the Well Tagged PDF document
     * @param language    the language of the Well Tagged PDF document
     */
    public WellTaggedPdfConfig(WellTaggedPdfConformance conformance, String title, String language) {
        if (conformance == null) {
            throw new PdfException("Conformance cannot be null");
        }
        this.conformance.add(conformance);
        this.title = title;
        this.language = language;
    }


    /**
     * Creates a new WellTaggedPdfConfig instance.
     *
     * @param conformanceList the conformance of the Well Tagged PDF document
     * @param title       the title of the Well Tagged PDF document
     * @param language    the language of the Well Tagged PDF document
     */
    public WellTaggedPdfConfig(List<WellTaggedPdfConformance> conformanceList, String title, String language) {
        if (conformanceList == null || conformanceList.isEmpty()) {
            throw new PdfException("Conformance list cannot be null or empty");
        }
        this.conformance.addAll(conformanceList);
        this.title = title;
        this.language = language;
    }

    /**
     * Gets the Well Tagged PDF conformance.
     *
     * @return The {@link PdfConformance}
     */
    public List<WellTaggedPdfConformance> getConformance() {
        return new ArrayList<>(conformance);
    }

    /**
     * Gets the title.
     *
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the language.
     *
     * @return The language
     */
    public String getLanguage() {
        return language;
    }
}
