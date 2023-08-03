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
package com.itextpdf.kernel.pdf.layer;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ITextTest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;

class PdfLayerTestUtils {

    static void addTextInsideLayer(IPdfOCG layer, PdfCanvas canvas, String text, float x, float y) {
        if (layer != null) {
            canvas.beginLayer(layer);
        }
        canvas
            .beginText()
            .moveText(x, y)
            .showText(text)
            .endText();

        if (layer != null) {
            canvas.endLayer();
        }
    }

    static PdfLayer prepareNewLayer() {
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        return new PdfLayer("layer1", dummyDoc);
    }

    static PdfLayer prepareLayerDesignIntent() {
        PdfLayer pdfLayer = prepareNewLayer();
        pdfLayer.setIntents(Collections.singletonList(PdfName.Design));
        return pdfLayer;
    }

    static PdfLayer prepareLayerDesignAndCustomIntent(PdfName custom) {
        PdfLayer pdfLayer = prepareNewLayer();
        pdfLayer.setIntents(Arrays.asList(PdfName.Design, custom));
        return pdfLayer;
    }

    static void compareLayers(String outPdf, String cmpPdf) throws IOException {
        ITextTest.printOutCmpPdfNameAndDir(outPdf, cmpPdf);
        System.out.println();
        try (PdfDocument outDoc = new PdfDocument(new PdfReader(outPdf))) {
            try (PdfDocument cmpDoc = new PdfDocument(new PdfReader(cmpPdf))) {
                PdfDictionary outOCP = outDoc.getCatalog().getPdfObject().getAsDictionary(PdfName.OCProperties);
                PdfDictionary cmpOCP = cmpDoc.getCatalog().getPdfObject().getAsDictionary(PdfName.OCProperties);
                Assert.assertNull(new CompareTool().compareDictionariesStructure(outOCP, cmpOCP));
            }
        }
    }
}
