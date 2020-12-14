/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.tagging.PdfMcrNumber;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class TagTreePointerUnitTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void rootTagCannotBeRemovedTest () {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.CANNOT_REMOVE_DOCUMENT_ROOT_TAG);

        PdfDocument pdfDoc = createTestDocument();
        TagTreePointer tagTreePointer = new TagTreePointer(pdfDoc);
        tagTreePointer.removeTag();
    }

    @Test
    public void cannotMoveToKidWithNonExistingRoleTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.NO_KID_WITH_SUCH_ROLE);

        PdfDocument pdfDoc = createTestDocument();
        TagTreePointer tagTreePointer = new TagTreePointer(pdfDoc);
        tagTreePointer.moveToKid(1, "role");
    }

    @Test
    public void cannotMoveToKidMcrTest01() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.CANNOT_MOVE_TO_MARKED_CONTENT_REFERENCE);

        PdfDocument pdfDoc = createTestDocument();
        TagTreePointer tagTreePointer = new TagTreePointer(pdfDoc);
        tagTreePointer.moveToKid(1, "MCR");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ENCOUNTERED_INVALID_MCR)
    })
    public void cannotMoveToKidMcrTest02() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.CANNOT_MOVE_TO_MARKED_CONTENT_REFERENCE);

        PdfDocument pdfDoc = createTestDocument();
        TagTreePointer tagTreePointer = new TagTreePointer(pdfDoc);
        PdfStructElem parent = new PdfStructElem(pdfDoc, PdfName.MCR);
        parent.put(PdfName.P, new PdfStructElem(pdfDoc, PdfName.MCR).getPdfObject());
        PdfStructElem kid1 = new PdfStructElem(pdfDoc, PdfName.MCR);
        PdfMcrNumber kid2 = new PdfMcrNumber(new PdfNumber(1), parent);
        parent.addKid(kid1);
        parent.addKid(kid2);
        tagTreePointer.setCurrentStructElem(parent);
        tagTreePointer.moveToKid(1);
    }

    @Test
    public void noParentObjectInStructElemTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException
                .expectMessage(KernelExceptionMessageConstant.STRUCTURE_ELEMENT_SHALL_CONTAIN_PARENT_OBJECT);

        PdfDocument pdfDoc = createTestDocument();
        TagTreePointer tagTreePointer = new TagTreePointer(pdfDoc);
        PdfStructElem pdfStructElem = new PdfStructElem(pdfDoc, PdfName.MCR);
        tagTreePointer.setCurrentStructElem(pdfStructElem);
    }

    @Test
    public void pageMustBeInitializedBeforeNextMcidCreationTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException
                .expectMessage(KernelExceptionMessageConstant.PAGE_IS_NOT_SET_FOR_THE_PDF_TAG_STRUCTURE);

        PdfDocument pdfDoc = createTestDocument();
        TagTreePointer tagTreePointer = new TagTreePointer(pdfDoc);
        PdfStructElem pdfStructElem = new PdfStructElem(pdfDoc, PdfName.MCR);
        tagTreePointer.createNextMcidForStructElem(pdfStructElem, 1);
    }

    @Test
    public void cannotMoveRootToParentTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException
                .expectMessage(KernelExceptionMessageConstant.CANNOT_MOVE_TO_PARENT_CURRENT_ELEMENT_IS_ROOT);

        PdfDocument pdfDoc = createTestDocument();
        TagTreePointer tagTreePointer = new TagTreePointer(pdfDoc);
        tagTreePointer.moveToParent();
    }

    @Test
    public void cannotRelocateRootTagTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.CANNOT_RELOCATE_ROOT_TAG);

        PdfDocument pdfDoc = createTestDocument();
        TagTreePointer tagTreePointer = new TagTreePointer(pdfDoc);
        tagTreePointer.relocate(tagTreePointer);
    }

    @Test
    public void cannotFlushAlreadyFlushedPageTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.PAGE_ALREADY_FLUSHED);

        PdfDocument pdfDoc = createTestDocument();
        TagTreePointer tagTreePointer = new TagTreePointer(pdfDoc);
        PdfPage pdfPage = pdfDoc.addNewPage(1);
        pdfPage.flush();
        tagTreePointer.setPageForTagging(pdfPage);
    }

    private static PdfDocument createTestDocument() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc.setTagged();
        return pdfDoc;
    }
}
