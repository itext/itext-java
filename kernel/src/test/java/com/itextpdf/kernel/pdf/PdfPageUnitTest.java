package com.itextpdf.kernel.pdf;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import static com.itextpdf.kernel.pdf.PageLabelNumberingStyle.DECIMAL_ARABIC_NUMERALS;

@Category(UnitTest.class)
public class PdfPageUnitTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/PdfPageUnitTest/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void cannotGetMcidIfDocIsNotTagged() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.MUST_BE_A_TAGGED_DOCUMENT);

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage pdfPage = pdfDoc.addNewPage();
        pdfPage.getNextMcid();
    }

    @Test
    public void cannotSetPageLabelIfFirstPageLessThanOneTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(
                KernelExceptionMessageConstant.IN_A_PAGE_LABEL_THE_PAGE_NUMBERS_MUST_BE_GREATER_OR_EQUAL_TO_1);

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage pdfPage = pdfDoc.addNewPage();
        pdfPage.setPageLabel(DECIMAL_ARABIC_NUMERALS, "test_prefix", 0);
    }

    @Test
    public void cannotFlushTagsIfNoTagStructureIsPresentTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException
                .expectMessage(KernelExceptionMessageConstant.TAG_STRUCTURE_FLUSHING_FAILED_IT_MIGHT_BE_CORRUPTED);

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage pdfPage = pdfDoc.addNewPage();
        pdfPage.tryFlushPageTags();
    }

    @Test
    public void mediaBoxAttributeIsNotPresentTest() throws IOException {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.CANNOT_RETRIEVE_MEDIA_BOX_ATTRIBUTE);

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "mediaBoxAttributeIsNotPresentTest.pdf"));
        PdfObject mediaBox = pdfDoc.getPage(1).getPdfObject().get(PdfName.MediaBox);
        Assert.assertNull(mediaBox);
        PdfPage page = pdfDoc.getPage(1);
        page.getMediaBox();
    }
}
