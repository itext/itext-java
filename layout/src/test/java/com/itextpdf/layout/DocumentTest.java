/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.layout;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.events.AbstractProductProcessITextEvent;
import com.itextpdf.events.EventManager;
import com.itextpdf.events.sequence.AbstractIdentifiableElement;
import com.itextpdf.events.sequence.SequenceId;
import com.itextpdf.events.sequence.SequenceIdManager;
import com.itextpdf.kernel.counter.event.ITextCoreEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.testutil.TestConfigurationEvent;
import com.itextpdf.layout.testutil.TestProductEvent;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class DocumentTest extends ExtendedITextTest {
    private static final TestConfigurationEvent CONFIGURATION_ACCESS = new TestConfigurationEvent();

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void executeActionInClosedDocTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException
                .expectMessage(LayoutExceptionMessageConstant.DOCUMENT_CLOSED_IT_IS_IMPOSSIBLE_TO_EXECUTE_ACTION);

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDoc);
        Paragraph paragraph = new Paragraph("test");
        document.add(paragraph);
        document.close();
        document.checkClosingStatus();
    }

    @Test
    public void addBlockElemMethodLinkingTest() {
        try (Document doc = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())))) {
            SequenceId sequenceId = new SequenceId();
            EventManager.getInstance().onEvent(new TestProductEvent(sequenceId));

            IBlockElement blockElement = new Paragraph("some text");
            SequenceIdManager.setSequenceId((AbstractIdentifiableElement) blockElement, sequenceId);
            doc.add(blockElement);

            List<AbstractProductProcessITextEvent> events = CONFIGURATION_ACCESS.getPublicEvents(
                    doc.getPdfDocument().getDocumentIdWrapper());
            // Second event was linked by adding block element method
            Assert.assertEquals(2, events.size());

            Assert.assertTrue(events.get(0) instanceof ITextCoreEvent);
            Assert.assertTrue(events.get(1) instanceof TestProductEvent);
        }
    }

    @Test
    public void addAreaBreakElemMethodLinkingTest() {
        try (Document doc = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())))) {
            SequenceId sequenceId = new SequenceId();
            EventManager.getInstance().onEvent(new TestProductEvent(sequenceId));

            AreaBreak areaBreak = new AreaBreak();
            SequenceIdManager.setSequenceId(areaBreak, sequenceId);
            doc.add(areaBreak);

            List<AbstractProductProcessITextEvent> events = CONFIGURATION_ACCESS.getPublicEvents(
                    doc.getPdfDocument().getDocumentIdWrapper());
            Assert.assertEquals(1, events.size());

            Assert.assertTrue(events.get(0) instanceof ITextCoreEvent);
        }
    }

    @Test
    public void addImageElemMethodLinkingTest() {
        try (Document doc = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())))) {
            SequenceId sequenceId = new SequenceId();
            EventManager.getInstance().onEvent(new TestProductEvent(sequenceId));

            Image image = new Image(new PdfFormXObject(new Rectangle(10, 10)));
            SequenceIdManager.setSequenceId(image, sequenceId);
            doc.add(image);

            List<AbstractProductProcessITextEvent> events = CONFIGURATION_ACCESS.getPublicEvents(
                    doc.getPdfDocument().getDocumentIdWrapper());
            // Second event was linked by adding block element
            Assert.assertEquals(2, events.size());

            Assert.assertTrue(events.get(0) instanceof ITextCoreEvent);
            Assert.assertTrue(events.get(1) instanceof TestProductEvent);
        }
    }
}
