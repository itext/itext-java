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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.test.ExtendedITextTest;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class TagTreeIteratorTest extends ExtendedITextTest {


    @Test
    public void tagTreeIteratorTagPointerNull() {
        String errorMessage =
                MessageFormatUtil.format(KernelExceptionMessageConstant.ARG_SHOULD_NOT_BE_NULL, "tagTreepointer");
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> new TagTreeIterator(null));
        Assertions.assertEquals(e.getMessage(), errorMessage);
    }

    @Test
    public void tagTreeIteratorApproverNull() {
        String errorMessage =
                MessageFormatUtil.format(KernelExceptionMessageConstant.ARG_SHOULD_NOT_BE_NULL, "approver");
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(), new WriterProperties()));
        doc.setTagged();
        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> new TagTreeIterator(doc.getStructTreeRoot(), null, TagTreeIterator.TreeTraversalOrder.PRE_ORDER));
        Assertions.assertEquals(e.getMessage(), errorMessage);
    }

    @Test
    public void tagTreeIteratorHandlerNull() {
        String errorMessage =
                MessageFormatUtil.format(KernelExceptionMessageConstant.ARG_SHOULD_NOT_BE_NULL, "handler");
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(), new WriterProperties()));
        doc.setTagged();
        TagTreeIterator it = new TagTreeIterator(doc.getStructTreeRoot());
        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> it.addHandler(null));
        Assertions.assertEquals(e.getMessage(), errorMessage);
    }

    @Test
    public void traversalWithoutElements() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(), new WriterProperties()));
        doc.setTagged();
        TagTreeIterator iterator = new TagTreeIterator(doc.getStructTreeRoot());
        TestHandler handler = new TestHandler();
        iterator.addHandler(handler);
        iterator.traverse();
        Assertions.assertEquals(1, handler.nodes.size());
    }

    @Test
    public void traversalWithSomeElements() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(), new WriterProperties()));
        doc.setTagged();
        TagTreePointer tp = new TagTreePointer(doc);

        tp.addTag(StandardRoles.DIV);
        tp.addTag(StandardRoles.P);
        tp.addTag(StandardRoles.FIGURE);
        tp.moveToParent();
        tp.addTag(StandardRoles.DIV);
        tp.addTag(StandardRoles.CODE);

        TagTreeIterator iterator = new TagTreeIterator(doc.getStructTreeRoot());
        TestHandler handler = new TestHandler();

        iterator.addHandler(handler);
        iterator.traverse();
        Assertions.assertEquals(7, handler.nodes.size());
        Assertions.assertNull(handler.nodes.get(0).getRole());
        Assertions.assertEquals(PdfName.Document, handler.nodes.get(1).getRole());
        Assertions.assertEquals(PdfName.Div, handler.nodes.get(2).getRole());
        Assertions.assertEquals(PdfName.P, handler.nodes.get(3).getRole());
        Assertions.assertEquals(PdfName.Figure, handler.nodes.get(4).getRole());
        Assertions.assertEquals(PdfName.Div, handler.nodes.get(5).getRole());
        Assertions.assertEquals(PdfName.Code, handler.nodes.get(6).getRole());
    }

    @Test
    public void postOrderTraversal() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(), new WriterProperties()));
        doc.setTagged();
        TagTreePointer tp = new TagTreePointer(doc);

        tp.addTag(StandardRoles.DIV);
        tp.addTag(StandardRoles.P);
        tp.addTag(StandardRoles.FIGURE);
        tp.moveToParent();
        tp.addTag(StandardRoles.DIV);
        tp.addTag(StandardRoles.CODE);

        TagTreeIterator iterator = new TagTreeIterator(doc.getStructTreeRoot(), new TagTreeIteratorElementApprover(),
                TagTreeIterator.TreeTraversalOrder.POST_ORDER);
        TestHandler handler = new TestHandler();

        iterator.addHandler(handler);
        iterator.traverse();
        Assertions.assertEquals(7, handler.nodes.size());
        Assertions.assertEquals(PdfName.Figure, handler.nodes.get(0).getRole());
        Assertions.assertEquals(PdfName.Code, handler.nodes.get(1).getRole());
        Assertions.assertEquals(PdfName.Div, handler.nodes.get(2).getRole());
        Assertions.assertEquals(PdfName.P, handler.nodes.get(3).getRole());
        Assertions.assertEquals(PdfName.Div, handler.nodes.get(4).getRole());
        Assertions.assertEquals(PdfName.Document, handler.nodes.get(5).getRole());
        Assertions.assertNull(handler.nodes.get(6).getRole());
    }

    @Test
    public void cyclicReferencesTraversal() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(), new WriterProperties()));
        doc.setTagged();

        PdfStructElem kid1 = new PdfStructElem(doc, PdfStructTreeRoot.convertRoleToPdfName(StandardRoles.P));
        PdfStructElem kid2 = new PdfStructElem(doc, PdfStructTreeRoot.convertRoleToPdfName(StandardRoles.DIV));
        doc.getStructTreeRoot().addKid(kid1);
        doc.getStructTreeRoot().addKid(kid2);
        kid1.addKid(kid2);
        kid2.addKid(kid1);

        TagTreeIterator iterator = new TagTreeIterator(doc.getStructTreeRoot(),
                new TagTreeIteratorAvoidDuplicatesApprover(),
                TagTreeIterator.TreeTraversalOrder.POST_ORDER);
        TestHandler handler = new TestHandler();

        iterator.addHandler(handler);
        iterator.traverse();

        Assertions.assertEquals(3, handler.nodes.size());

        Assertions.assertEquals(PdfName.Div, handler.nodes.get(0).getRole());
        Assertions.assertEquals(PdfName.P, handler.nodes.get(1).getRole());
        Assertions.assertNull(handler.nodes.get(2).getRole());
    }

    static class TestHandler implements ITagTreeIteratorHandler {

        final List<IStructureNode> nodes = new ArrayList<>();

        @Override
        public boolean nextElement(IStructureNode elem) {
            nodes.add(elem);
            return true;
        }
    }
}