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
import com.itextpdf.kernel.pdf.IPdfPageFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.utils.ValidationContainer;
import com.itextpdf.pdfua.checkers.PdfUA1Checker;
import com.itextpdf.pdfua.exceptions.PdfUALogMessageConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a Pdf/UA document.
 * This class is an extension of PdfDocument and adds the necessary configuration for PDF/UA conformance.
 * It will add necessary validation to guide the user to create a PDF/UA compliant document.
 */
public class PdfUADocument extends PdfDocument {

    private static final IPdfPageFactory pdfPageFactory = new PdfUAPageFactory();
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfUADocument.class);
    private PdfUAConfig config;
    private boolean warnedOnPageFlush = false;

    /**
     * Creates a PdfUADocument instance.
     *
     * @param writer The writer to write the PDF document.
     * @param config The configuration for the PDF/UA document.
     */
    public PdfUADocument(PdfWriter writer, PdfUAConfig config) {
        this(writer, new DocumentProperties(), config);
    }

    /**
     * Creates a PdfUADocument instance.
     *
     * @param writer     The writer to write the PDF document.
     * @param properties The properties for the PDF document.
     * @param config     The configuration for the PDF/UA document.
     */
    public PdfUADocument(PdfWriter writer, DocumentProperties properties, PdfUAConfig config) {
        super(configureWriterProperties(writer), properties);
        setupUAConfiguration(config);
    }

    /**
     * Creates a PdfUADocument instance.
     *
     * @param reader The reader to read the PDF document.
     * @param writer The writer to write the PDF document.
     * @param config The configuration for the PDF/UA document.
     */
    public PdfUADocument(PdfReader reader, PdfWriter writer, PdfUAConfig config) {
        super(reader, configureWriterProperties(writer));
        setupUAConfiguration(config);
    }

    /**
     * Creates a PdfUADocument instance.
     *
     * @param reader     The reader to read the PDF document.
     * @param writer     The writer to write the PDF document.
     * @param properties The properties for the PDF document.
     * @param config     The configuration for the PDF/UA document.
     */
    public PdfUADocument(PdfReader reader, PdfWriter writer, StampingProperties properties, PdfUAConfig config) {
        super(reader, configureWriterProperties(writer), properties);
        setupUAConfiguration(config);
    }

    /**
     * {inheritDoc}
     */
    @Override
    public IConformanceLevel getConformanceLevel() {
        return config.getConformanceLevel();
    }

    /**
     * @return The PageFactory for the PDF/UA document.
     */
    @Override
    protected IPdfPageFactory getPageFactory() {
        return pdfPageFactory;
    }

    /**
     * Returns if the document is in the closing state.
     *
     * @return true if the document is closing, false otherwise.
     */
    boolean isClosing(){
        return this.isClosing;
    }

    /**
     * Warns the user that the page is being flushed.
     * Will only warn once.
     */
    void warnOnPageFlush() {
        if (!warnedOnPageFlush) {
            LOGGER.warn(PdfUALogMessageConstants.PAGE_FLUSHING_DISABLED);
            warnedOnPageFlush = true;
        }
    }

    /**
     * Disables the warning for page flushing.
     */
    public void disablePageFlushingWarning() {
        warnedOnPageFlush = true;
    }

    private void setupUAConfiguration(PdfUAConfig config) {
        //basic configuration
        this.config = config;
        this.setTagged();
        this.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
        this.getCatalog().setLang(new PdfString(config.getLanguage()));
        final PdfDocumentInfo info = this.getDocumentInfo();
        info.setTitle(config.getTitle());
        //validation
        final ValidationContainer validationContainer = new ValidationContainer();
        validationContainer.addChecker(new PdfUA1Checker(this));
        this.getDiContainer().register(ValidationContainer.class, validationContainer);
    }

    private static PdfWriter configureWriterProperties(PdfWriter writer) {
        writer.getProperties().addUAXmpMetadata();
        return writer;
    }

}
