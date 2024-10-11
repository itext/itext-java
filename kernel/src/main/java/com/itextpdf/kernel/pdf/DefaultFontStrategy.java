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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class defines a default font strategy for {@link PdfDocument}
 * which is used in the scope of {@link PdfDocument#getDefaultFont()}.
 */
public class DefaultFontStrategy {
    private final PdfDocument pdfDocument;
    private PdfFont defaultFont = null;

    /**
     * Instantiates a new instance of {@link DefaultFontStrategy} which
     * will be used for passed {@link PdfDocument} instance.
     *
     * @param pdfDocument the pdf document for which the strategy will be used to
     */
    public DefaultFontStrategy(PdfDocument pdfDocument) {
        this.pdfDocument = pdfDocument;
    }

    /**
     * Gets default font.
     *
     * @return the {@link PdfFont} instance
     */
    public PdfFont getFont() {
        if (defaultFont == null) {
            try {
                defaultFont = PdfFontFactory.createFont();
                if (pdfDocument.getWriter() != null) {
                    defaultFont.makeIndirect(pdfDocument);
                }
            } catch (IOException e) {
                Logger logger = LoggerFactory.getLogger(DefaultFontStrategy.class);
                logger.error(IoLogMessageConstant.EXCEPTION_WHILE_CREATING_DEFAULT_FONT, e);
                defaultFont = null;
            }
        }
        return defaultFont;
    }
}
