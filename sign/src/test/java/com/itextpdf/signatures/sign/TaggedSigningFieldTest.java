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
package com.itextpdf.signatures.sign;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.SignatureFormFieldBuilder;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.test.ExtendedITextTest;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;


@Tag("IntegrationTest")
// TODO DEVSIX-5438: Change assertions after implementing signature field tagging
public class TaggedSigningFieldTest extends ExtendedITextTest {

    @Test
    public void checkSigningFieldTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDoc.addNewPage();
            pdfDoc.setTagged();

            Rectangle rect = new Rectangle(36, 648, 200, 100);
            PdfFormField signField = new SignatureFormFieldBuilder(pdfDoc, "signature").setWidgetRectangle(rect)
                    .createSignature();
            PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
            form.addField(signField);

            TagTreePointer tagPointer = new TagTreePointer(pdfDoc);
            Assertions.assertNotNull(tagPointer.moveToKid(StandardRoles.FORM));
        }
    }

    @Test
    public void checkSigningFieldZeroSizeRectangleTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDoc.addNewPage();
            pdfDoc.setTagged();

            Rectangle rect = new Rectangle(36, 648, 0, 0);
            PdfFormField signField = new SignatureFormFieldBuilder(pdfDoc, "signature").setWidgetRectangle(rect)
                    .createSignature();
            PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
            form.addField(signField);

            TagTreePointer tagPointer = new TagTreePointer(pdfDoc);
            Assertions.assertNotNull(tagPointer.moveToKid(StandardRoles.FORM));
        }
    }

    @Test
    public void checkSigningFieldPrintFlagTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            PdfPage page = pdfDoc.addNewPage();
            pdfDoc.setTagged();

            Rectangle rect = new Rectangle(36, 648, 200, 100);
            PdfFormField signField = new SignatureFormFieldBuilder(pdfDoc, "signature").setWidgetRectangle(rect)
                    .createSignature();
            PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
            form.addField(signField);

            List<PdfAnnotation> annotations = page.getAnnotations();
            annotations.get(0).setFlag(PdfAnnotation.PRINT);

            TagTreePointer tagPointer = new TagTreePointer(pdfDoc);
            Assertions.assertNotNull(tagPointer.moveToKid(StandardRoles.FORM));
        }
    }

    @Test
    public void checkSigningFieldHiddenFlagTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            PdfPage page = pdfDoc.addNewPage();
            pdfDoc.setTagged();

            Rectangle rect = new Rectangle(36, 648, 200, 100);
            PdfFormField signField = new SignatureFormFieldBuilder(pdfDoc, "signature").setWidgetRectangle(rect)
                    .createSignature();
            PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
            form.addField(signField);

            List<PdfAnnotation> annotations = page.getAnnotations();
            annotations.get(0).setFlag(PdfAnnotation.HIDDEN);

            TagTreePointer tagPointer = new TagTreePointer(pdfDoc);
            Assertions.assertNotNull(tagPointer.moveToKid(StandardRoles.FORM));
        }
    }

    @Test
    public void checkSigningFieldNoViewFlagTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            PdfPage page = pdfDoc.addNewPage();
            pdfDoc.setTagged();

            Rectangle rect = new Rectangle(36, 648, 200, 100);
            PdfFormField signField = new SignatureFormFieldBuilder(pdfDoc, "signature").setWidgetRectangle(rect)
                    .createSignature();
            PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
            form.addField(signField);

            List<PdfAnnotation> annotations = page.getAnnotations();
            annotations.get(0).setFlag(PdfAnnotation.NO_VIEW);

            TagTreePointer tagPointer = new TagTreePointer(pdfDoc);
            Assertions.assertNotNull(tagPointer.moveToKid(StandardRoles.FORM));
        }
    }

    @Test
    public void checkSigningFieldInvisibleFlagTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            PdfPage page = pdfDoc.addNewPage();
            pdfDoc.setTagged();

            Rectangle rect = new Rectangle(36, 648, 200, 100);
            PdfFormField signField = new SignatureFormFieldBuilder(pdfDoc, "signature").setWidgetRectangle(rect)
                    .createSignature();
            PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
            form.addField(signField);

            List<PdfAnnotation> annotations = page.getAnnotations();
            annotations.get(0).setFlag(PdfAnnotation.INVISIBLE);

            TagTreePointer tagPointer = new TagTreePointer(pdfDoc);
            Assertions.assertNotNull(tagPointer.moveToKid(StandardRoles.FORM));
        }
    }

    @Test
    public void checkSigningFieldOutsidePageTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDoc.addNewPage();
            pdfDoc.setTagged();

            Rectangle rect = new Rectangle(-150, -150, 100, 100);
            PdfFormField signField = new SignatureFormFieldBuilder(pdfDoc, "signature").setWidgetRectangle(rect)
                    .createSignature();
            PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
            form.addField(signField);

            TagTreePointer tagPointer = new TagTreePointer(pdfDoc);
            Assertions.assertNotNull(tagPointer.moveToKid(StandardRoles.FORM));
        }
    }

    @Test
    public void checkSigningFieldOutsidePageAndHiddenTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            PdfPage page = pdfDoc.addNewPage();
            pdfDoc.setTagged();

            Rectangle rect = new Rectangle(-150, -150, 200, 100);
            PdfFormField signField = new SignatureFormFieldBuilder(pdfDoc, "signature").setWidgetRectangle(rect)
                    .createSignature();
            PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
            form.addField(signField);

            List<PdfAnnotation> annotations = page.getAnnotations();
            annotations.get(0).setFlag(PdfAnnotation.HIDDEN);

            TagTreePointer tagPointer = new TagTreePointer(pdfDoc);
            Assertions.assertNotNull(tagPointer.moveToKid(StandardRoles.FORM));
        }
    }
}
