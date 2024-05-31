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
package com.itextpdf.layout;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

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

        PdfDocument pdf = new PdfDocument(new PdfWriter(FileUtil.getFileOutputStream(outFileName),
                new WriterProperties().setCompressionLevel(0)));
        Document document = new Document(pdf);

        Paragraph paragraph = new Paragraph().add(
                "This line is before." + newlineCharacters + "This line is after.");
                
        document.add(paragraph);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, diffPrefix));
    }
}
