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
package com.itextpdf.pdfa;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class extends {@link PdfADocument} and serves as {@link PdfADocument} for
 * PDF/A compliant documents and as {@link com.itextpdf.kernel.pdf.PdfDocument}
 * for non PDF/A documents.
 *
 * <p>
 * This class can throw various exceptions like {@link com.itextpdf.kernel.exceptions.PdfException}
 * as well as {@link com.itextpdf.pdfa.exceptions.PdfAConformanceException} for PDF/A documents.
 */
public class PdfAAgnosticPdfDocument extends PdfADocument {

    private PdfFont defaultFont;

    /**
     * Opens a PDF/A document in stamping mode.
     *
     * @param reader the {@link PdfReader}
     * @param writer the {@link PdfWriter} object to write to
     */
    public PdfAAgnosticPdfDocument (PdfReader reader, PdfWriter writer) {
        this(reader, writer, new StampingProperties());
    }

    /**
     * Opens a PDF/A document in stamping mode.
     *
     * @param reader the {@link PdfReader}
     * @param writer the {@link PdfWriter} object to write to
     * @param properties {@link StampingProperties} of the stamping process
     */
    public PdfAAgnosticPdfDocument (PdfReader reader, PdfWriter writer, StampingProperties properties) {
        super(reader, writer, properties, true);
    }

    /**
     * Get default font for the document: Helvetica, WinAnsi.
     * One instance per document.
     *
     * @return instance of {@link PdfFont} or {@code null} on error.
     */
    @Override
    public PdfFont getDefaultFont() {
        // TODO DEVSIX-7850 investigate embedding default font into PDF/A documents while signing
        if (defaultFont == null) {
            try {
                defaultFont = PdfFontFactory.createFont();
                if (writer != null) {
                    defaultFont.makeIndirect(this);
                }
            } catch (IOException e) {
                Logger logger = LoggerFactory.getLogger(PdfDocument.class);
                logger.error(IoLogMessageConstant.EXCEPTION_WHILE_CREATING_DEFAULT_FONT, e);
                defaultFont = null;
            }
        }
        return defaultFont;
    }
}
