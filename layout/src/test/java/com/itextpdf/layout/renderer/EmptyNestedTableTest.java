package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;

/**
 */
@Category(IntegrationTest.class)
public class EmptyNestedTableTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/EmptyNestedTableTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/EmptyNestedTableTest/";

    @Test
    public void buildEmptyTable() throws IOException, InterruptedException {

        createDestinationFolder(destinationFolder);

        String outFileName = destinationFolder + "emptyNestedTableTest.pdf";
        String cmpFileName = sourceFolder + "cmp_emptyNestedTableTest.pdf";

        // setup document
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        pdfDocument.setTagged();
        Document layoutDocument = new Document(pdfDocument);

        // add table to document
        Table x = new Table(new float[]{1f}).addCell(new Cell().add(new Table(new float[]{1f})));
        layoutDocument.add(x);

        // close document
        layoutDocument.close();

        // compare
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

}
