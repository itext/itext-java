/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.pdfa;

import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfAXmpTest extends ExtendedITextTest {

    public static final String cmpFolder = "./src/test/resources/com/itextpdf/pdfa/cmp/PdfAXmpTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfAXmpTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void keywordsInfoTestPdfA1b() throws IOException, InterruptedException {
        String outFile = destinationFolder + "keywordsInfoTestPdfA1b.pdf";
        String cmpFile = cmpFolder + "cmp_keywordsInfoTestPdfA1b.pdf";

        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(new PdfWriter(outFile), PdfAConformanceLevel.PDF_A_1B, outputIntent);
        doc.addNewPage();

        doc.getDocumentInfo().setKeywords("key1, key2 , key3;key4,key5");

        doc.close();

        CompareTool ct = new CompareTool();
        Assert.assertNull(ct.compareByContent(outFile, cmpFile, destinationFolder));
        Assert.assertNull(ct.compareDocumentInfo(outFile, cmpFile));
        Assert.assertNull(ct.compareXmp(outFile, cmpFile, true));
    }

    @Test
    public void keywordsInfoTestPdfA2b() throws IOException, InterruptedException {
        String outFile = destinationFolder + "keywordsInfoTestPdfA2b.pdf";
        String cmpFile = cmpFolder + "cmp_keywordsInfoTestPdfA2b.pdf";

        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(new PdfWriter(outFile), PdfAConformanceLevel.PDF_A_2B, outputIntent);
        doc.addNewPage();

        doc.getDocumentInfo().setKeywords("key1, key2 , key3;key4,key5");

        doc.close();

        CompareTool ct = new CompareTool();
        Assert.assertNull(ct.compareByContent(outFile, cmpFile, destinationFolder));
        Assert.assertNull(ct.compareDocumentInfo(outFile, cmpFile));
        Assert.assertNull(ct.compareXmp(outFile, cmpFile, true));
    }

}
