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
package com.itextpdf.kernel.geom;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfFreeTextAnnotation;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class PageSizeTest extends ExtendedITextTest {

    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/geom/PageSizeTest/";

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/geom/PageSizeTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterClass
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @Test
    public void emptyA9PageTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "emptyA9Page.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_emptyA9Page.pdf";

        PdfDocument doc = new PdfDocument(CompareTool.createTestPdfWriter(outPdf));
        doc.addNewPage(PageSize.A9);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void notEmptyA9PageTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "notEmptyA9Page.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_notEmptyA9Page.pdf";

        PdfDocument doc = new PdfDocument(CompareTool.createTestPdfWriter(outPdf));
        PdfPage page = doc.addNewPage(PageSize.A9);
        PdfAnnotation annotation = new PdfFreeTextAnnotation(new Rectangle(50,10,50,50)
                , new PdfString("some content"));
        page.addAnnotation(annotation);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void allATypePageSizesTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "allATypePageSizes.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_allATypePageSizes.pdf";

        PageSize[] pageSizes = {PageSize.A0, PageSize.A1, PageSize.A2, PageSize.A3, PageSize.A4, PageSize.A5
                , PageSize.A6, PageSize.A7, PageSize.A8, PageSize.A9, PageSize.A10, };
        PdfDocument doc = new PdfDocument(CompareTool.createTestPdfWriter(outPdf));
        for (PageSize pageSize : pageSizes) {
            doc.addNewPage(pageSize);
        }
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff"));
    }
}
