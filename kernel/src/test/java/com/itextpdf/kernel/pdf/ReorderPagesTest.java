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
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
public class ReorderPagesTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/ReorderPagesTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/ReorderPagesTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void reorderTaggedHasCommonStructElem01() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String inPath = sourceFolder + "taggedHasCommonStructElem.pdf";
        String outPath = destinationFolder + "reorderTaggedHasCommonStructElem01.pdf";
        String cmpPath = sourceFolder + "cmp_reorderTaggedHasCommonStructElem.pdf";

        PdfDocument pdf = new PdfDocument(new PdfReader(inPath), new PdfWriter(outPath));
        pdf.setTagged();

        pdf.movePage(2,1);
        pdf.close();

        compare(outPath, cmpPath, destinationFolder, "diff_01");
    }

    @Test
    public void reorderTaggedHasCommonStructElem02() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String inPath = sourceFolder + "taggedHasCommonStructElem.pdf";
        String outPath = destinationFolder + "reorderTaggedHasCommonStructElem02.pdf";
        String cmpPath = sourceFolder + "cmp_reorderTaggedHasCommonStructElem.pdf";

        PdfDocument pdf = new PdfDocument(new PdfReader(inPath), new PdfWriter(outPath));
        pdf.movePage(1,3);
        pdf.close();

        compare(outPath, cmpPath, destinationFolder, "diff_02");
    }

    @Test
    public void reorderTaggedHasCommonStructElemBigger() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String inPath = sourceFolder + "taggedHasCommonStructElemBigger.pdf";
        String outPath = destinationFolder + "reorderTaggedHasCommonStructElemBigger.pdf";
        String cmpPath = sourceFolder + "cmp_reorderTaggedHasCommonStructElemBigger.pdf";

        PdfDocument pdf = new PdfDocument(new PdfReader(inPath), new PdfWriter(outPath));
        pdf.movePage(2,5);
        pdf.close();

        compare(outPath, cmpPath, destinationFolder, "diff_03");
    }

    @Test
    public void copyReorderTaggedHasCommonStructElem() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String inPath = sourceFolder + "taggedHasCommonStructElem.pdf";
        String outPath = destinationFolder + "copyReorderTaggedHasCommonStructElem.pdf";
        String cmpPath = sourceFolder + "cmp_copyReorderTaggedHasCommonStructElem.pdf";

        PdfDocument sourceDoc = new PdfDocument(new PdfReader(inPath));
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPath));
        pdfDoc.setTagged();

        sourceDoc.copyPagesTo(Arrays.asList(2, 1, 3), pdfDoc);

        sourceDoc.close();
        pdfDoc.close();

        compare(outPath, cmpPath, destinationFolder, "diff_04");
    }

    private void compare(String outPath, String cmpPath, String destinationFolder, String diffPrefix) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        CompareTool compareTool = new CompareTool();
        String tagStructureErrors = compareTool.compareTagStructures(outPath, cmpPath);
        String contentErrors = compareTool.compareByContent(outPath, cmpPath, destinationFolder, diffPrefix);
        String resultMessage = "";
        if (tagStructureErrors != null) {
            resultMessage += tagStructureErrors + "\n";
        }
        if (contentErrors != null) {
            resultMessage += contentErrors + "\n";
        }
        assertTrue(resultMessage, tagStructureErrors == null && contentErrors == null);
    }

}
