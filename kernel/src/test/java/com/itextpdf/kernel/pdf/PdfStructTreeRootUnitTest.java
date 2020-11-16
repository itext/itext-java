package com.itextpdf.kernel.pdf;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfStructTreeRootUnitTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void cannotMovePageInPartlyFlushedDocTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(
                MessageFormatUtil
                        .format(KernelExceptionMessageConstant.CANNOT_MOVE_PAGES_IN_PARTLY_FLUSHED_DOCUMENT, 1));

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage pdfPage = pdfDocument.addNewPage(1);
        pdfPage.flush();
        PdfStructTreeRoot pdfStructTreeRoot = new PdfStructTreeRoot(pdfDocument);
        pdfStructTreeRoot.move(pdfPage, -1);
    }
}
