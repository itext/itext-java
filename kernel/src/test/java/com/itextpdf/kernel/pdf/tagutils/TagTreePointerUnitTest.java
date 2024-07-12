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
package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.tagging.PdfMcrNumber;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class TagTreePointerUnitTest extends ExtendedITextTest {

    @Test
    public void rootTagCannotBeRemovedTest () {
        PdfDocument pdfDoc = createTestDocument();
        TagTreePointer tagTreePointer = new TagTreePointer(pdfDoc);
        Exception exception = Assert.assertThrows(PdfException.class,
                () -> tagTreePointer.removeTag());
        Assert.assertEquals(KernelExceptionMessageConstant.CANNOT_REMOVE_DOCUMENT_ROOT_TAG,
                exception.getMessage());
    }

    @Test
    public void cannotMoveToKidWithNonExistingRoleTest() {
        PdfDocument pdfDoc = createTestDocument();
        TagTreePointer tagTreePointer = new TagTreePointer(pdfDoc);
        Exception exception = Assert.assertThrows(PdfException.class,
                () -> tagTreePointer.moveToKid(1, "role"));
        Assert.assertEquals(KernelExceptionMessageConstant.NO_KID_WITH_SUCH_ROLE,
                exception.getMessage());
    }

    @Test
    public void cannotMoveToKidMcrTest01() {
        PdfDocument pdfDoc = createTestDocument();
        TagTreePointer tagTreePointer = new TagTreePointer(pdfDoc);
        Exception exception = Assert.assertThrows(PdfException.class,
                () -> tagTreePointer.moveToKid(1, "MCR"));
        Assert.assertEquals(KernelExceptionMessageConstant.CANNOT_MOVE_TO_MARKED_CONTENT_REFERENCE,
                exception.getMessage());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.ENCOUNTERED_INVALID_MCR)
    })
    public void cannotMoveToKidMcrTest02() {
        PdfDocument pdfDoc = createTestDocument();
        TagTreePointer tagTreePointer = new TagTreePointer(pdfDoc);
        PdfStructElem parent = new PdfStructElem(pdfDoc, PdfName.MCR);
        parent.put(PdfName.P, new PdfStructElem(pdfDoc, PdfName.MCR).getPdfObject());
        PdfStructElem kid1 = new PdfStructElem(pdfDoc, PdfName.MCR);
        PdfMcrNumber kid2 = new PdfMcrNumber(new PdfNumber(1), parent);
        parent.addKid(kid1);
        parent.addKid(kid2);
        tagTreePointer.setCurrentStructElem(parent);
        Exception exception = Assert.assertThrows(PdfException.class,
                () -> tagTreePointer.moveToKid(1));
        Assert.assertEquals(KernelExceptionMessageConstant.CANNOT_MOVE_TO_MARKED_CONTENT_REFERENCE,
                exception.getMessage());
    }

    @Test
    public void noParentObjectInStructElemTest() {
        PdfDocument pdfDoc = createTestDocument();
        TagTreePointer tagTreePointer = new TagTreePointer(pdfDoc);
        PdfStructElem pdfStructElem = new PdfStructElem(pdfDoc, PdfName.MCR);
        Exception exception = Assert.assertThrows(PdfException.class,
                () -> tagTreePointer.setCurrentStructElem(pdfStructElem));
        Assert.assertEquals(KernelExceptionMessageConstant.STRUCTURE_ELEMENT_SHALL_CONTAIN_PARENT_OBJECT,
                exception.getMessage());
    }

    @Test
    public void pageMustBeInitializedBeforeNextMcidCreationTest() {
        PdfDocument pdfDoc = createTestDocument();
        TagTreePointer tagTreePointer = new TagTreePointer(pdfDoc);
        PdfStructElem pdfStructElem = new PdfStructElem(pdfDoc, PdfName.MCR);
        Exception exception = Assert.assertThrows(PdfException.class,
                () -> tagTreePointer.createNextMcidForStructElem(pdfStructElem, 1));
        Assert.assertEquals(KernelExceptionMessageConstant.PAGE_IS_NOT_SET_FOR_THE_PDF_TAG_STRUCTURE,
                exception.getMessage());
    }

    @Test
    public void cannotMoveRootToParentTest() {
        PdfDocument pdfDoc = createTestDocument();
        TagTreePointer tagTreePointer = new TagTreePointer(pdfDoc);
        Exception exception = Assert.assertThrows(PdfException.class,
                () -> tagTreePointer.moveToParent());
        Assert.assertEquals(KernelExceptionMessageConstant.CANNOT_MOVE_TO_PARENT_CURRENT_ELEMENT_IS_ROOT,
                exception.getMessage());
    }

    @Test
    public void cannotRelocateRootTagTest() {
        PdfDocument pdfDoc = createTestDocument();
        TagTreePointer tagTreePointer = new TagTreePointer(pdfDoc);
        Exception exception = Assert.assertThrows(PdfException.class,
                () -> tagTreePointer.relocate(tagTreePointer));
        Assert.assertEquals(KernelExceptionMessageConstant.CANNOT_RELOCATE_ROOT_TAG, exception.getMessage());
    }

    @Test
    public void cannotFlushAlreadyFlushedPageTest() {
        PdfDocument pdfDoc = createTestDocument();
        TagTreePointer tagTreePointer = new TagTreePointer(pdfDoc);
        PdfPage pdfPage = pdfDoc.addNewPage(1);
        pdfPage.flush();
        Exception exception = Assert.assertThrows(PdfException.class,
                () -> tagTreePointer.setPageForTagging(pdfPage));
        Assert.assertEquals(KernelExceptionMessageConstant.PAGE_ALREADY_FLUSHED, exception.getMessage());
    }

    @Test
    public void cyclicReferencesWhileLookingForRoleTest() {
        PdfDocument doc = createTestDocument();

        PdfStructElem kid1 = new PdfStructElem(doc, PdfStructTreeRoot.convertRoleToPdfName(StandardRoles.P));
        PdfStructElem kid2 = new PdfStructElem(doc, PdfStructTreeRoot.convertRoleToPdfName(StandardRoles.DIV));
        doc.getStructTreeRoot().addKid(kid1);
        doc.getStructTreeRoot().addKid(kid2);
        kid1.addKid(kid2);
        kid2.addKid(kid1);

        TagTreePointer pointer = new TagTreePointer(doc);
        Exception exception = Assert.assertThrows(PdfException.class,
                () -> pointer.moveToKid(StandardRoles.FIGURE));
        Assert.assertEquals(KernelExceptionMessageConstant.NO_KID_WITH_SUCH_ROLE,
                exception.getMessage());
    }

    @Test
    public void cyclicReferencesWhileFlushingTest() {
        PdfDocument doc = createTestDocument();

        PdfStructElem kid1 = new PdfStructElem(doc, PdfStructTreeRoot.convertRoleToPdfName(StandardRoles.P));
        PdfStructElem kid2 = new PdfStructElem(doc, PdfStructTreeRoot.convertRoleToPdfName(StandardRoles.DIV));
        doc.getStructTreeRoot().addKid(kid1);
        doc.getStructTreeRoot().addKid(kid2);
        kid1.addKid(kid2);
        kid2.addKid(kid1);

        TagTreePointer pointer = new TagTreePointer(doc);
        pointer.moveToKid(StandardRoles.P);

        AssertUtil.doesNotThrow(() -> pointer.flushTag());
        Assert.assertTrue(kid1.isFlushed());
        Assert.assertTrue(kid2.isFlushed());
    }

    @Test
    public void cyclicReferencesWithWaitingObjectsWhileFlushingTest() {
        PdfDocument doc = createTestDocument();

        PdfStructElem kid1 = new PdfStructElem(doc, PdfStructTreeRoot.convertRoleToPdfName(StandardRoles.P));
        PdfStructElem kid2 = new PdfStructElem(doc, PdfStructTreeRoot.convertRoleToPdfName(StandardRoles.DIV));
        doc.getStructTreeRoot().addKid(kid1);
        doc.getStructTreeRoot().addKid(kid2);
        kid1.addKid(kid2);
        kid2.addKid(kid1);

        TagTreePointer pointer = new TagTreePointer(doc);
        pointer.moveToKid(StandardRoles.P);
        WaitingTagsManager waitingTagsManager = pointer.getContext().getWaitingTagsManager();
        Object pWaitingTagObj = new Object();
        waitingTagsManager.assignWaitingState(pointer, pWaitingTagObj);
        pointer.moveToParent().moveToKid(StandardRoles.DIV);

        AssertUtil.doesNotThrow(() -> pointer.flushTag());
        Assert.assertFalse(kid1.isFlushed());
        Assert.assertTrue(kid2.isFlushed());
    }

    private static PdfDocument createTestDocument() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc.setTagged();
        return pdfDoc;
    }
}
