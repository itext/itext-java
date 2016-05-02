package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfStringTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfStringTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfStringTest/";

    @Before
    public void before() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void testPdfDocumentInfoStringEncoding01() throws IOException, InterruptedException {
        String fileName = "testPdfDocumentInfoStringEncoding01.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + fileName, new WriterProperties().setCompressionLevel(CompressionConstants.NO_COMPRESSION)));
        pdfDocument.addNewPage();

        String author = "Алексей";
        String title = "Заголовок";
        String subject = "Тема";
        String keywords = "Ключевые слова";
        String creator = "English text";

        pdfDocument.getDocumentInfo().setAuthor(author);
        pdfDocument.getDocumentInfo().setTitle(title);
        pdfDocument.getDocumentInfo().setSubject(subject);
        pdfDocument.getDocumentInfo().setKeywords(keywords);
        pdfDocument.getDocumentInfo().setCreator(creator);

        pdfDocument.close();

        PdfDocument readDoc = new PdfDocument(new PdfReader(destinationFolder + fileName));
        Assert.assertEquals(author, readDoc.getDocumentInfo().getAuthor());
        Assert.assertEquals(title, readDoc.getDocumentInfo().getTitle());
        Assert.assertEquals(subject, readDoc.getDocumentInfo().getSubject());
        Assert.assertEquals(keywords, readDoc.getDocumentInfo().getKeywords());
        Assert.assertEquals(creator, readDoc.getDocumentInfo().getCreator());

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + fileName, sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

}
