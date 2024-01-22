package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class TagTreeIteratorTest extends ExtendedITextTest {


    @Test
    public void tagTreeIteratorTagPointerNull() {
        String errorMessage =
                MessageFormatUtil.format(KernelExceptionMessageConstant.ARG_SHOULD_NOT_BE_NULL, "tagTreepointer");
        Exception e = Assert.assertThrows(IllegalArgumentException.class, () -> new TagTreeIterator(null));
        Assert.assertEquals(e.getMessage(), errorMessage);
    }

    @Test
    public void traversalWithoutElements() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(), new WriterProperties()));
        doc.setTagged();
        TagTreeIterator iterator = new TagTreeIterator(doc.getStructTreeRoot());
        TestHandler handler = new TestHandler();
        iterator.addHandler(handler);
        iterator.traverse();
        Assert.assertEquals(1, handler.nodes.size());
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
        Assert.assertEquals(7, handler.nodes.size());
        Assert.assertNull(handler.nodes.get(0).getRole());
        Assert.assertEquals(PdfName.Document, handler.nodes.get(1).getRole());
        Assert.assertEquals(PdfName.Div, handler.nodes.get(2).getRole());
        Assert.assertEquals(PdfName.P, handler.nodes.get(3).getRole());
        Assert.assertEquals(PdfName.Figure, handler.nodes.get(4).getRole());
        Assert.assertEquals(PdfName.Div, handler.nodes.get(5).getRole());
        Assert.assertEquals(PdfName.Code, handler.nodes.get(6).getRole());
    }

    static class TestHandler implements ITagTreeIteratorHandler {

        final List<IStructureNode> nodes = new ArrayList<>();

        @Override
        public void nextElement(IStructureNode elem) {
            nodes.add(elem);
        }
    }


}