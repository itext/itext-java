/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
