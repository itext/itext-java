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
package com.itextpdf.layout;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class NewLineTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/NewLineTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/NewLineTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void r() throws IOException, InterruptedException {
        test("\r", "r.pdf");
    }

    @Test
    public void n() throws IOException, InterruptedException {
        test("\n", "n.pdf");
    }

    @Test
    public void rn() throws IOException, InterruptedException {
        test("\r\n", "rn.pdf");
    }

    @Test
    public void rrn() throws IOException, InterruptedException {
        test("\r\r\n", "rrn.pdf");
    }

    @Test
    public void nn() throws IOException, InterruptedException {
        test("\n\n", "nn.pdf");
    }

    @Test
    public void rnn() throws IOException, InterruptedException {
        test("\r\n\n", "rnn.pdf");
    }

    @Test
    public void rnrn() throws IOException, InterruptedException {
        test("\r\n\r\n", "rnrn.pdf");
    }

    private void test(String newlineCharacters, String fileName) throws IOException, InterruptedException {
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + "cmp_" + fileName;
        String diffPrefix = "diff_" + fileName + "_";

        PdfDocument pdf = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName), new WriterProperties().setCompressionLevel(0)));
        Document document = new Document(pdf);

        Paragraph paragraph = new Paragraph().add(
                "This line is before." + newlineCharacters + "This line is after.");
                
        document.add(paragraph);
        document.close();

        Assert.assertNull(new CompareTool().compareVisually(outFileName, cmpFileName, destinationFolder, diffPrefix));
    }
}
