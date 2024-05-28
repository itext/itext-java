package com.itextpdf.kernel.pdf;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfReaderUnitTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/PdfReaderUnitTest/";

    @Test
    public void readStreamBytesRawNullStreamTest() throws java.io.IOException {
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "testFile.pdf");
        Exception e = Assert.assertThrows(PdfException.class, () -> reader.readStreamBytesRaw(null));
        Assert.assertEquals(KernelExceptionMessageConstant.UNABLE_TO_READ_STREAM_BYTES, e.getMessage());
    }

    @Test
    public void readObjectStreamNullStreamTest() throws java.io.IOException {
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "testFile.pdf");
        Exception e = Assert.assertThrows(PdfException.class, () -> reader.readObjectStream(null));
        Assert.assertEquals(KernelExceptionMessageConstant.UNABLE_TO_READ_OBJECT_STREAM, e.getMessage());
    }

    @Test
    public void readObjectInvalidObjectStreamNumberTest() throws java.io.IOException {
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "testFile.pdf");
        PdfDocument doc = new PdfDocument(reader);

        PdfIndirectReference ref = new PdfIndirectReference(doc, 20);
        ref.setState(PdfObject.FREE);
        ref.setObjStreamNumber(5);
        ref.refersTo = null;

        PdfIndirectReference ref2 = new PdfIndirectReference(doc, 5);
        ref2.setState(PdfObject.FREE);
        ref2.refersTo = null;
        doc.getXref().add(ref2);

        doc.getCatalog().getPdfObject().put(PdfName.StructTreeRoot, ref);
        Exception e = Assert.assertThrows(PdfException.class, () -> reader.readObject(ref));
        Assert.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.INVALID_OBJECT_STREAM_NUMBER, 20, 5, 0), e.getMessage());
    }
}