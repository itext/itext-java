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
package com.itextpdf.kernel.pdf.action;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfFileAttachmentAnnotation;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfTargetTest extends ExtendedITextTest {

    @Test
    public void createInstanceTest() {
        PdfDictionary dictionary = new PdfDictionary();
        PdfTarget target = PdfTarget.create(dictionary);

        Assert.assertEquals(dictionary, target.getPdfObject());
    }

    @Test
    public void createParentInstanceTest() {
        PdfTarget target = PdfTarget.createParentTarget();
        PdfDictionary dictionary = target.getPdfObject();
        Assert.assertEquals(PdfName.P, dictionary.get(PdfName.R));
    }

    @Test
    public void createChildInstanceTest() {
        PdfTarget target = PdfTarget.createChildTarget();
        PdfDictionary dictionary = target.getPdfObject();
        Assert.assertEquals(PdfName.C, dictionary.get(PdfName.R));
    }

    @Test
    public void createChildInstanceWithEmbeddedFileTest() {
        final String embeddedFileName = "EmbeddedFileName.file";

        PdfTarget target = PdfTarget.createChildTarget(embeddedFileName);
        PdfDictionary dictionary = target.getPdfObject();
        Assert.assertEquals(PdfName.C, dictionary.get(PdfName.R));
        Assert.assertEquals(new PdfString(embeddedFileName), dictionary.get(PdfName.N));
    }

    @Test
    public void createChildInstanceWithNamedDestinationTest() {
        final String namedDestination = "namedDestination";
        final String annotationIdentifier = "annotationIdentifier";

        PdfTarget target = PdfTarget.createChildTarget(namedDestination, annotationIdentifier);

        PdfDictionary dictionary = target.getPdfObject();
        Assert.assertEquals(PdfName.C, dictionary.get(PdfName.R));
        Assert.assertEquals(new PdfString(namedDestination), dictionary.get(PdfName.P));
        Assert.assertEquals(new PdfString(annotationIdentifier), dictionary.get(PdfName.A));
    }

    @Test
    public void createChildInstanceWithPageNumberTest() {
        final int pageNumber = 23;
        final int annotationIndex = 7;

        PdfTarget target = PdfTarget.createChildTarget(pageNumber, annotationIndex);

        PdfDictionary dictionary = target.getPdfObject();
        Assert.assertEquals(PdfName.C, dictionary.get(PdfName.R));
        Assert.assertEquals(new PdfNumber(pageNumber - 1), dictionary.get(PdfName.P));
        Assert.assertEquals(new PdfNumber(annotationIndex), dictionary.get(PdfName.A));
    }

    @Test
    public void namePropertyTest() {
        final String name = "Name";

        PdfTarget target = PdfTarget.create(new PdfDictionary());
        target.setName(name);

        Assert.assertEquals(name, target.getName());
        Assert.assertEquals(new PdfString(name), target.getPdfObject().get(PdfName.N));

    }

    @Test
    public void targetPropertyTest() {

        final PdfDictionary oldDictionary = new PdfDictionary();
        oldDictionary.put(new PdfName("Id"), new PdfString("Old"));

        final PdfDictionary newDictionary = new PdfDictionary();
        newDictionary.put(new PdfName("Id"), new PdfString("New"));

        PdfTarget target = PdfTarget.create(oldDictionary);

        target.setTarget(PdfTarget.create(newDictionary));
        Assert.assertEquals(newDictionary, target.getTarget().getPdfObject());
        Assert.assertEquals(newDictionary, target.getPdfObject().get(PdfName.T));
    }

    @Test
    public void setAnnotationTest() {
        try (PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            PdfFileAttachmentAnnotation annotation0 = new PdfFileAttachmentAnnotation(
                    new Rectangle(0,0, 20, 20));
            PdfFileAttachmentAnnotation annotation1 = new PdfFileAttachmentAnnotation(
                    new Rectangle(1,1, 21, 21));
            PdfFileAttachmentAnnotation annotation2 = new PdfFileAttachmentAnnotation(
                    new Rectangle(2,2, 22, 22));

            document.addNewPage();
            document.getPage(1).addAnnotation(annotation0);
            document.getPage(1).addAnnotation(annotation1);
            document.getPage(1).addAnnotation(annotation2);

            PdfTarget target = PdfTarget.create(new PdfDictionary());
            target.setAnnotation(annotation2, document);

            PdfDictionary dictionary = target.getPdfObject();
            Assert.assertEquals(0, dictionary.getAsNumber(PdfName.P).intValue());
            Assert.assertEquals(2, dictionary.getAsNumber(PdfName.A).intValue());
        }
    }

    @Test
    public void setAnnotationWhichIsMissedOnThePageTest() {
        try (PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            PdfFileAttachmentAnnotation annotation0 = new PdfFileAttachmentAnnotation(
                    new Rectangle(0,0, 20, 20));
            PdfFileAttachmentAnnotation annotation1 = new PdfFileAttachmentAnnotation(
                    new Rectangle(1,1, 21, 21));
            PdfFileAttachmentAnnotation annotation2 = new PdfFileAttachmentAnnotation(
                    new Rectangle(2,2, 22, 22));

            document.addNewPage();
            document.getPage(1).addAnnotation(annotation0);
            document.getPage(1).addAnnotation(annotation1);
            // The page doesn't know about the annotation
            annotation2.setPage(document.getPage(1));

            PdfTarget target = PdfTarget.create(new PdfDictionary());
            target.setAnnotation(annotation2, document);

            PdfDictionary dictionary = target.getPdfObject();
            Assert.assertEquals(0, dictionary.getAsNumber(PdfName.P).intValue());
            Assert.assertEquals(-1, dictionary.getAsNumber(PdfName.A).intValue());
        }
    }

    @Test
    public void setAnnotationWithoutPageTest() {
        try (PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            document.addNewPage();

            PdfFileAttachmentAnnotation annotation = new PdfFileAttachmentAnnotation(
                    new Rectangle(0,0, 20, 20));

            PdfTarget target = PdfTarget.create(new PdfDictionary());

            Exception e = Assert.assertThrows(PdfException.class,
                    () -> target.setAnnotation(annotation, document)
            );
            Assert.assertEquals(KernelExceptionMessageConstant.ANNOTATION_SHALL_HAVE_REFERENCE_TO_PAGE, e.getMessage());
        }
    }

    @Test
    public void getAnnotationSetAsAnnotationTest() {
        try (PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {

            PdfFileAttachmentAnnotation annotation = new PdfFileAttachmentAnnotation(
                    new Rectangle(0,0, 20, 20));
            document.addNewPage();
            document.getPage(1).addAnnotation(annotation);

            PdfDictionary content = new PdfDictionary();
            content.put(new PdfName("Key"), new PdfString("Value"));

            PdfTarget target = PdfTarget.create(new PdfDictionary());
            target.setAnnotation(annotation, document);

            Assert.assertEquals(annotation.getPdfObject(), target.getAnnotation(document).getPdfObject());
        }
    }

    @Test
    public void getAnnotationSetAsIntsTest() {
        try (PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            final int pageNumber = 1;
            final int annotationIndex = 0;

            PdfTarget target = PdfTarget.createChildTarget(pageNumber, annotationIndex);

            PdfFileAttachmentAnnotation annotation = new PdfFileAttachmentAnnotation(
                    new Rectangle(0,0, 20, 20));
            document.addNewPage();
            document.getPage(1).addAnnotation(annotation);

            Assert.assertEquals(annotation.getPdfObject(), target.getAnnotation(document).getPdfObject());
        }
    }

    @Test
    public void getAnnotationSetAsStringTest() {
        try (PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            final String namedDestination = "namedDestination";
            final String annotationIdentifier = "annotationIdentifier";

            PdfTarget target = PdfTarget.createChildTarget(namedDestination, annotationIdentifier);

            PdfFileAttachmentAnnotation annotation = new PdfFileAttachmentAnnotation(
                    new Rectangle(0,0, 20, 20));
            annotation.setName(new PdfString(annotationIdentifier));

            document.addNewPage();
            document.getPage(1).addAnnotation(annotation);
            document.getCatalog().getNameTree(PdfName.Dests).addEntry(namedDestination,
                    new PdfArray(new PdfNumber(1)));

            PdfAnnotation retrievedAnnotation = target.getAnnotation(document);

            Assert.assertEquals(annotation.getPdfObject(), retrievedAnnotation.getPdfObject());
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOME_TARGET_FIELDS_ARE_NOT_SET_OR_INCORRECT)
    })
    public void getAnnotationSetAsStringNotAvailableTest() {
        try (PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            final String namedDestination = "namedDestination";
            final String annotationIdentifier = "annotationIdentifier";

            PdfTarget target = PdfTarget.createChildTarget(namedDestination, annotationIdentifier);

            document.addNewPage();
            document.getCatalog().getNameTree(PdfName.Dests).addEntry(namedDestination,
                    new PdfArray(new PdfNumber(1)));
            PdfAnnotation annotation = target.getAnnotation(document);

            Assert.assertNull(annotation);
        }
    }

    @Test
    public void putTest() {
        final PdfName key1 = new PdfName("Key1");
        final PdfName key2 = new PdfName("Key2");
        final PdfDictionary dictionary = new PdfDictionary();

        PdfTarget target = PdfTarget.create(dictionary);
        target.put(key1, new PdfNumber(23))
                .put(key2, new PdfString("Hello, world!"));
        Assert.assertEquals(23, dictionary.getAsNumber(key1).intValue());
        Assert.assertEquals("Hello, world!", dictionary.getAsString(key2).getValue());
    }


    @Test
    public void isWrappedObjectMustBeIndirectTest() {
        PdfDictionary pdfObject = new PdfDictionary();
        PdfTarget target = PdfTarget.create(pdfObject);

        Assert.assertFalse(target.isWrappedObjectMustBeIndirect());
    }

    @Test
    public void noAnnotationPageReferenceTest() {
        PdfFileAttachmentAnnotation pdfAnnotation = new PdfFileAttachmentAnnotation(new Rectangle(100, 100));
        PdfTarget pdfTarget = PdfTarget.create(new PdfDictionary());
        Exception exception = Assert.assertThrows(PdfException.class,
                () -> pdfTarget.setAnnotation(pdfAnnotation, null));
        Assert.assertEquals(KernelExceptionMessageConstant.ANNOTATION_SHALL_HAVE_REFERENCE_TO_PAGE,
                exception.getMessage());
    }
}
