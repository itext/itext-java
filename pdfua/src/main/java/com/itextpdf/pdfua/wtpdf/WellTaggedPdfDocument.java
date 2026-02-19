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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.contrast.ColorContrastChecker;
import com.itextpdf.kernel.pdf.DocumentProperties;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.WellTaggedPdfConformance;
import com.itextpdf.kernel.validation.IValidationChecker;
import com.itextpdf.kernel.validation.Pdf20Checker;
import com.itextpdf.kernel.validation.ValidationContainer;
import com.itextpdf.layout.tagging.ProhibitedTagRelationsResolver;
import com.itextpdf.pdfua.PdfUAPageFactory;
import com.itextpdf.pdfua.logs.PdfUALogMessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates a Well Tagged PDF document.
 * This class is an extension of PdfDocument and adds the necessary configuration for Well Tagged conformance.
 * It will add necessary validation to guide the user to create a Well Tagged compliant document.
 */
public class WellTaggedPdfDocument extends PdfDocument {
    private static final Logger LOGGER = LoggerFactory.getLogger(WellTaggedPdfDocument.class);

    /**
     * Creates a WellTaggedPdfDocument instance.
     *
     * @param writer The writer to write the PDF document.
     * @param config The configuration for the Well Tagged document.
     */
    public WellTaggedPdfDocument(PdfWriter writer, WellTaggedPdfConfig config) {
        this(writer, new DocumentProperties(), config);
    }

    /**
     * Creates a WellTaggedPdfDocument instance.
     *
     * @param writer     The writer to write the PDF document.
     * @param properties The properties for the PDF document.
     * @param config     The configuration for the Well Tagged document.
     */
    public WellTaggedPdfDocument(PdfWriter writer, DocumentProperties properties, WellTaggedPdfConfig config) {
        super(configureWriterProperties(writer, config.getConformance()), properties);
        this.pdfConformance = new PdfConformance(config.getConformance());

        setupWtpdfConfiguration(config);
        final ValidationContainer validationContainer = new ValidationContainer();
        final List<IValidationChecker> checkers = createCheckers();
        for (IValidationChecker checker : checkers) {
            validationContainer.addChecker(checker);
        }
        this.getDiContainer().register(ValidationContainer.class, validationContainer);
        this.pdfPageFactory = new PdfUAPageFactory(getWtpdfChecker(checkers));
        getDiContainer().register(ProhibitedTagRelationsResolver.class, new ProhibitedTagRelationsResolver(this));
    }

    /**
     * Creates a WellTaggedPdfDocument instance.
     *
     * @param reader The reader to read the PDF document.
     * @param writer The writer to write the PDF document.
     * @param config The configuration for the Well Tagged document.
     */
    public WellTaggedPdfDocument(PdfReader reader, PdfWriter writer, WellTaggedPdfConfig config) {
        this(reader, writer, new StampingProperties(), config);
    }

    /**
     * Creates a WellTaggedPdfDocument instance.
     *
     * @param reader     The reader to read the PDF document.
     * @param writer     The writer to write the PDF document.
     * @param properties The properties for the PDF document.
     * @param config     The configuration for the Well Tagged document.
     */
    public WellTaggedPdfDocument(PdfReader reader, PdfWriter writer, StampingProperties properties,
                                 WellTaggedPdfConfig config) {
        super(reader, writer, properties);
        if (!getConformance().isWtpdf()) {
            LOGGER.warn(PdfUALogMessageConstants.PDF_TO_WTPDF_CONVERSION_IS_NOT_SUPPORTED);
        }

        setupWtpdfConfiguration(config);

        final ValidationContainer validationContainer = new ValidationContainer();
        final List<IValidationChecker> checkers = createCheckers();
        for (IValidationChecker checker : checkers) {
            validationContainer.addChecker(checker);
        }
        this.getDiContainer().register(ValidationContainer.class, validationContainer);
        this.pdfPageFactory = new PdfUAPageFactory(getWtpdfChecker(checkers));
    }

    /**
     * Creates a list of {@link IValidationChecker} for Well Tagged conformance.
     * If you want to enable/disable specific checks, you can override the implementation.
     *
     * @return list of Well Tagged related checkers
     */
    protected List<IValidationChecker> createCheckers() {
        List<IValidationChecker> checkers = new ArrayList<>();
        final ColorContrastChecker contrastChecker = new ColorContrastChecker(false, false);
        checkers.add(new WellTaggedPdfChecker(this));
        checkers.add(new Pdf20Checker(this));
        checkers.add(contrastChecker);
        return checkers;
    }

    private void setupWtpdfConfiguration(WellTaggedPdfConfig config) {
        // Basic configuration.
        this.setTagged();
        this.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
        this.getCatalog().setLang(new PdfString(config.getLanguage()));
        final PdfDocumentInfo info = this.getDocumentInfo();
        info.setTitle(config.getTitle());
    }

    private static PdfWriter configureWriterProperties(PdfWriter writer, WellTaggedPdfConformance wtpdfConformance) {
        writer.getProperties().addWtpdfXmpMetadata(wtpdfConformance);
        if (writer.getPdfVersion() != null && !PdfVersion.PDF_2_0.equals(writer.getPdfVersion())) {
            LOGGER.warn(MessageFormatUtil.format(
                    PdfUALogMessageConstants.WRITER_PROPERTIES_PDF_VERSION_WAS_OVERRIDDEN, PdfVersion.PDF_2_0));
            writer.getProperties().setPdfVersion(PdfVersion.PDF_2_0);
        }
        return writer;
    }

    private static WellTaggedPdfChecker getWtpdfChecker(List<IValidationChecker> checkers) {
        for (IValidationChecker checker : checkers) {
            if (checker instanceof WellTaggedPdfChecker) {
                return (WellTaggedPdfChecker) checker;
            }
        }
        return null;
    }
}
