/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
