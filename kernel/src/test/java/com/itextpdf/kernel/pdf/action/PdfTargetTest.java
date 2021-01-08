/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.kernel.pdf.action;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.PdfException;
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
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfTargetTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

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
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(PdfException.AnnotationShallHaveReferenceToPage);

        try (PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            document.addNewPage();

            PdfFileAttachmentAnnotation annotation = new PdfFileAttachmentAnnotation(
                    new Rectangle(0,0, 20, 20));

            PdfTarget target = PdfTarget.create(new PdfDictionary());
            target.setAnnotation(annotation, document);
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
            @LogMessage(messageTemplate = LogMessageConstant.SOME_TARGET_FIELDS_ARE_NOT_SET_OR_INCORRECT)
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
}
