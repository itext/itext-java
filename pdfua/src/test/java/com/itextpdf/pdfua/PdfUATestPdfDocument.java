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

import com.itextpdf.kernel.pdf.DocumentProperties;
import com.itextpdf.kernel.pdf.IConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfUAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.ValidationContainer;
import com.itextpdf.pdfua.checkers.PdfUA1Checker;

/**
 * PdfDocument extension for testing purposes.
 */
public class PdfUATestPdfDocument extends PdfDocument {


    private final IConformanceLevel conformanceLevel = PdfUAConformanceLevel.PDFUA_1;

    public PdfUATestPdfDocument(PdfReader reader) {
        this(reader, new DocumentProperties());
    }

    public PdfUATestPdfDocument(PdfReader reader, DocumentProperties properties) {
        super(reader, properties);
        setupUAConfiguration();
    }

    public PdfUATestPdfDocument(PdfWriter writer) {
        this(writer, new DocumentProperties());
    }

    public PdfUATestPdfDocument(PdfWriter writer, DocumentProperties properties) {
        super(writer, properties);
        setupUAConfiguration();
    }

    public PdfUATestPdfDocument(PdfReader reader, PdfWriter writer) {
        super(reader, writer);
        setupUAConfiguration();
    }

    public static WriterProperties createWriterProperties() {
        return new WriterProperties().addUAXmpMetadata();
    }

    public PdfUATestPdfDocument(PdfReader reader, PdfWriter writer, StampingProperties properties) {
        super(reader, writer, properties);
    }

    /**
     * {inheritDoc}
     */
    @Override
    public IConformanceLevel getConformanceLevel() {
        return conformanceLevel;
    }

    private void setupUAConfiguration() {
        //basic configuration
        this.setTagged();

        this.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
        this.getCatalog().setLang(new PdfString("en-US"));
        PdfDocumentInfo info = this.getDocumentInfo();
        info.setTitle("English pangram");
        //validation
        ValidationContainer validationContainer = new ValidationContainer();
        validationContainer.addChecker(new PdfUA1Checker(this));
        this.getDiContainer().register(ValidationContainer.class, validationContainer);
    }
}
