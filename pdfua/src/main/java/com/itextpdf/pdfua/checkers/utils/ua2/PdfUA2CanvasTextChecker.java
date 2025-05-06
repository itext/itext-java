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
package com.itextpdf.pdfua.checkers.utils.ua2;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfMcr;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.validation.context.CanvasTextAdditionContext;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class which performs UA-2 checks related to Replacements and Alternatives.
 */
public class PdfUA2CanvasTextChecker {

    private final List<CanvasTextAdditionContext> textWithPua = new ArrayList<>();

    /**
     * Creates {@link PdfUA2CanvasTextChecker} instance.
     */
    public PdfUA2CanvasTextChecker() {
        // Empty constructor.
    }

    /**
     * Collects all text strings, which contain PUA Unicode values.
     *
     * @param context {@link CanvasTextAdditionContext} which contains all the data needed for validation
     */
    public void collectTextAdditionContext(CanvasTextAdditionContext context) {
        String text = context.getText();
        PdfDictionary attributes = context.getAttributes();
        PdfString alt = null;
        PdfString actualText = null;
        if (attributes != null) {
            alt = attributes.getAsString(PdfName.Alt);
            actualText = attributes.getAsString(PdfName.ActualText);
        }
        if (PdfUA2StringChecker.stringContainsPua(text)) {
            if (alt == null && actualText == null) {
                textWithPua.add(context);
            }
        }
    }

    /**
     * Checks previously collected data according to Replacements and Alternatives UA-2 rules.
     *
     * @param document {@link PdfDocument} to be checked
     */
    public void checkCollectedContexts(PdfDocument document) {
        for (CanvasTextAdditionContext context : textWithPua) {
            if (context.getMcId() == null) {
                throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.PUA_CONTENT_WITHOUT_ALT);
            }
            PdfMcr mcr = findMcrByMcId(document, context.getMcId(), context.getContentStream());
            if (mcr == null) {
                throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.PUA_CONTENT_WITHOUT_ALT);
            }
            IStructureNode structureNode = mcr.getParent();
            if (!(structureNode instanceof PdfStructElem)) {
                throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.PUA_CONTENT_WITHOUT_ALT);
            }
            PdfStructElem structElem = (PdfStructElem) structureNode;
            PdfString alt = structElem.getAlt();
            PdfString actualText = structElem.getActualText();
            if (alt == null && actualText == null) {
                throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.PUA_CONTENT_WITHOUT_ALT);
            }
        }
    }

    private static PdfMcr findMcrByMcId(PdfDocument document, PdfNumber mcId, PdfStream contentStream) {
        for (int i = 1; i <= document.getNumberOfPages(); ++i) {
            PdfPage page = document.getPage(i);
            for (int j = 0; j < page.getContentStreamCount(); ++j) {
                PdfStream pageStream = page.getContentStream(j);
                if (pageStream.getIndirectReference().equals(contentStream.getIndirectReference())) {
                    PdfMcr mcr = document.getStructTreeRoot().findMcrByMcid(page.getPdfObject(), mcId.intValue());
                    if (mcr != null) {
                        return mcr;
                    }
                }
            }
        }
        return null;
    }
}
