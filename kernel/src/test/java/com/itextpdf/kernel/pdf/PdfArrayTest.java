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

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.test.ExtendedITextTest;

import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfArrayTest extends ExtendedITextTest {

    @Test
    public void testValuesIndirectContains() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfArray array = new PdfArray();
        array.add(new PdfNumber(0).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(2).makeIndirect(doc));
        array.add(new PdfNumber(3).makeIndirect(doc));
        array.add(new PdfNumber(4));
        array.add(new PdfNumber(5));

        Assertions.assertTrue(array.contains(array.get(0, false)));
        Assertions.assertTrue(array.contains(array.get(1, false)));
        Assertions.assertTrue(array.contains(array.get(2).getIndirectReference()));
        Assertions.assertTrue(array.contains(array.get(3).getIndirectReference()));
        Assertions.assertTrue(array.contains(array.get(4)));
        Assertions.assertTrue(array.contains(array.get(5)));
    }

    @Test
    public void testValuesIndirectRemove() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfArray array = new PdfArray();
        array.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(3).makeIndirect(doc));
        array.add(new PdfNumber(4).makeIndirect(doc));
        array.add(new PdfNumber(5));
        array.add(new PdfNumber(6));

        array.remove(array.get(0, false));
        array.remove(array.get(0, false));
        array.remove(array.get(0).getIndirectReference());
        array.remove(array.get(0).getIndirectReference());
        array.remove(array.get(0));
        array.remove(array.get(0));

        Assertions.assertEquals(0, array.size());
    }

    @Test
    public void testContains() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfArray array = new PdfArray();
        array.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(3).makeIndirect(doc));
        array.add(new PdfNumber(4).makeIndirect(doc));
        array.add(new PdfNumber(5));
        array.add(new PdfNumber(6));

        PdfArray array2 = new PdfArray();
        array2.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array2.add(new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        array2.add(new PdfNumber(3).makeIndirect(doc));
        array2.add(new PdfNumber(4).makeIndirect(doc));
        array2.add(new PdfNumber(5));
        array2.add(new PdfNumber(6));

        for (PdfObject obj : array2) {
            Assertions.assertTrue(array.contains(obj));
        }

        for (int i = 0; i < array2.size(); i++) {
            Assertions.assertTrue(array.contains(array2.get(i)));
        }
    }

    @Test
    public void testRemove() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfArray array = new PdfArray();
        array.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(3).makeIndirect(doc));
        array.add(new PdfNumber(4).makeIndirect(doc));
        array.add(new PdfNumber(5));
        array.add(new PdfNumber(6));

        PdfArray array2 = new PdfArray();
        array2.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array2.add(new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        array2.add(new PdfNumber(3).makeIndirect(doc));
        array2.add(new PdfNumber(4).makeIndirect(doc));
        array2.add(new PdfNumber(5));
        array2.add(new PdfNumber(6));

        for (PdfObject obj : array2) {
            array.remove(obj);
        }

        Assertions.assertEquals(0, array.size());
    }

    @Test
    public void testRemove2() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfArray array = new PdfArray();
        array.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(3).makeIndirect(doc));
        array.add(new PdfNumber(4).makeIndirect(doc));
        array.add(new PdfNumber(5));
        array.add(new PdfNumber(6));

        PdfArray array2 = new PdfArray();
        array2.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array2.add(new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        array2.add(new PdfNumber(3).makeIndirect(doc));
        array2.add(new PdfNumber(4).makeIndirect(doc));
        array2.add(new PdfNumber(5));
        array2.add(new PdfNumber(6));

        for (int i = 0; i < array2.size(); i++) {
            array.remove(array2.get(i));
        }

        Assertions.assertEquals(0, array.size());
    }
    @Test
    public void testIndexOf() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfArray array = new PdfArray();
        array.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(3).makeIndirect(doc));
        array.add(new PdfNumber(4).makeIndirect(doc));
        array.add(new PdfNumber(5));
        array.add(new PdfNumber(6));

        PdfArray array2 = new PdfArray();
        array2.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array2.add(new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        array2.add(new PdfNumber(3).makeIndirect(doc));
        array2.add(new PdfNumber(4).makeIndirect(doc));
        array2.add(new PdfNumber(5));
        array2.add(new PdfNumber(6));

        int i = 0;
        for (PdfObject obj : array2) {
            Assertions.assertEquals(i++, array.indexOf(obj));
        }
    }

    @Test
    public void testIndexOf2() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfArray array = new PdfArray();
        array.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        array.add(new PdfNumber(3).makeIndirect(doc));
        array.add(new PdfNumber(4).makeIndirect(doc));
        array.add(new PdfNumber(5));
        array.add(new PdfNumber(6));

        PdfArray array2 = new PdfArray();
        array2.add(new PdfNumber(1).makeIndirect(doc).getIndirectReference());
        array2.add(new PdfNumber(2).makeIndirect(doc).getIndirectReference());
        array2.add(new PdfNumber(3).makeIndirect(doc));
        array2.add(new PdfNumber(4).makeIndirect(doc));
        array2.add(new PdfNumber(5));
        array2.add(new PdfNumber(6));

        for (int i = 0; i < array2.size(); i++) {
            Assertions.assertEquals(i, array.indexOf(array2.get(i)));
        }
    }

    @Test
    public void pdfUncoloredPatternColorSize1Test() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        String contentColorSpace = "/Cs1 cs\n";
        PdfDictionary pageDictionary = (PdfDictionary) new PdfDictionary().makeIndirect(pdfDocument);
        PdfStream contentStream = new PdfStream(contentColorSpace.getBytes());
        pageDictionary.put(PdfName.Contents, contentStream);

        PdfPage page = pdfDocument.addNewPage();
        page.getPdfObject().put(PdfName.Contents, contentStream);

        PdfArray pdfArray = new PdfArray();
        pdfArray.add(PdfName.Pattern);
        PdfColorSpace space = PdfColorSpace.makeColorSpace(pdfArray);
        page.getResources().addColorSpace(space);

        Rectangle rectangle = new Rectangle(50, 50, 1000, 1000);
        page.setMediaBox(rectangle);

        PdfCanvasProcessor processor = new PdfCanvasProcessor(new NoOpListener());
        processor.processPageContent(page);

        // Check if we reach the end of the test without failings together with verifying expected color space instance
        Assertions.assertTrue(processor.getGraphicsState().getFillColor().getColorSpace() instanceof PdfSpecialCs.Pattern);
    }

    private static class NoOpListener implements IEventListener {
        @Override
        public void eventOccurred(IEventData data, EventType type) {
        }

        @Override
        public Set<EventType> getSupportedEvents() {
            return null;
        }
    }
}
