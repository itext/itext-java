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

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfCircleAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class SmartModeTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/SmartModeTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/SmartModeTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void smartModeSameResourcesCopyingAndFlushing() throws IOException, InterruptedException {
        String outFile = destinationFolder + "smartModeSameResourcesCopyingAndFlushing.pdf";
        String cmpFile = sourceFolder + "cmp_smartModeSameResourcesCopyingAndFlushing.pdf";
        String[] srcFiles = new String[]{
                sourceFolder + "indirectResourcesStructure.pdf",
                sourceFolder + "indirectResourcesStructure2.pdf"
        };

        PdfDocument outputDoc = new PdfDocument(new PdfWriter(outFile, new WriterProperties().useSmartMode()));

        for (String srcFile : srcFiles) {
            PdfDocument sourceDoc = new PdfDocument(new PdfReader(srcFile));
            sourceDoc.copyPagesTo(1, sourceDoc.getNumberOfPages(), outputDoc);
            sourceDoc.close();

            outputDoc.flushCopiedObjects(sourceDoc);
        }

        outputDoc.close();

        PdfDocument assertDoc = new PdfDocument(new PdfReader(outFile));
        PdfIndirectReference page1ResFontObj = assertDoc.getPage(1).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();
        PdfIndirectReference page2ResFontObj = assertDoc.getPage(2).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();
        PdfIndirectReference page3ResFontObj = assertDoc.getPage(3).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();

        Assert.assertTrue(page1ResFontObj.equals(page2ResFontObj));
        Assert.assertTrue(page1ResFontObj.equals(page3ResFontObj));
        assertDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder));
    }

    @Test
    public void smartModeSameResourcesCopyingModifyingAndFlushing() throws IOException {
        String outFile = destinationFolder + "smartModeSameResourcesCopyingModifyingAndFlushing.pdf";
        String[] srcFiles = new String[]{
                sourceFolder + "indirectResourcesStructure.pdf",
                sourceFolder + "indirectResourcesStructure2.pdf"
        };
        boolean exceptionCaught = false;

        PdfDocument outputDoc = new PdfDocument(new PdfWriter(outFile, new WriterProperties().useSmartMode()));

        int lastPageNum = 1;
        PdfFont font = PdfFontFactory.createFont();
        for (String srcFile : srcFiles) {
            PdfDocument sourceDoc = new PdfDocument(new PdfReader(srcFile));
            sourceDoc.copyPagesTo(1, sourceDoc.getNumberOfPages(), outputDoc);
            sourceDoc.close();

            int i;
            for (i = lastPageNum; i <= outputDoc.getNumberOfPages(); ++i) {
                PdfCanvas canvas;
                try {
                    canvas = new PdfCanvas(outputDoc.getPage(i));
                } catch (NullPointerException expected) {
                    // Smart mode makes it possible to share objects coming from different source documents.
                    // Flushing one object documents might make it impossible to modify further copied objects.
                    Assert.assertEquals(2, i);
                    exceptionCaught = true;
                    break;
                }
                canvas.beginText().moveText(36, 36).setFontAndSize(font, 12).showText("Page " + i).endText();
            }
            lastPageNum = i;

            if (exceptionCaught) {
                break;
            }

            outputDoc.flushCopiedObjects(sourceDoc);
        }

        if (!exceptionCaught) {
            Assert.fail();
        }
    }

    @Test
    public void smartModeSameResourcesCopyingModifyingAndFlushing_ensureObjectFresh() throws IOException, InterruptedException {
        String outFile = destinationFolder + "smartModeSameResourcesCopyingModifyingAndFlushing_ensureObjectFresh.pdf";
        String cmpFile = sourceFolder + "cmp_smartModeSameResourcesCopyingModifyingAndFlushing_ensureObjectFresh.pdf";
        String[] srcFiles = new String[]{
                sourceFolder + "indirectResourcesStructure.pdf",
                sourceFolder + "indirectResourcesStructure2.pdf"
        };

        PdfDocument outputDoc = new PdfDocument(new PdfWriter(outFile, new WriterProperties().useSmartMode()));

        int lastPageNum = 1;
        PdfFont font = PdfFontFactory.createFont();
        for (String srcFile : srcFiles) {
            PdfDocument sourceDoc = new PdfDocument(new PdfReader(srcFile));
            for (int i = 1; i <= sourceDoc.getNumberOfPages(); ++i) {
                PdfDictionary srcRes = sourceDoc.getPage(i).getPdfObject().getAsDictionary(PdfName.Resources);

                // Ensures that objects copied to the output document are fresh,
                // i.e. are not reused from already copied objects cache.
                boolean ensureObjectIsFresh = true;
                // it's crucial to copy first inner objects and then the container object!
                for (PdfObject v : srcRes.values()) {
                    if (v.getIndirectReference() != null) {
                        // We are not interested in returned copied objects instances, they will be picked up by
                        // general copying mechanism from copied objects cache by default.
                        v.copyTo(outputDoc, ensureObjectIsFresh);
                    }
                }
                if (srcRes.getIndirectReference() != null) {
                    srcRes.copyTo(outputDoc, ensureObjectIsFresh);
                }
            }
            sourceDoc.copyPagesTo(1, sourceDoc.getNumberOfPages(), outputDoc);
            sourceDoc.close();

            int i;
            for (i = lastPageNum; i <= outputDoc.getNumberOfPages(); ++i) {
                PdfPage page = outputDoc.getPage(i);
                PdfCanvas canvas = new PdfCanvas(page);
                canvas.beginText().moveText(36, 36).setFontAndSize(font, 12).showText("Page " + i).endText();
            }
            lastPageNum = i;

            outputDoc.flushCopiedObjects(sourceDoc);
        }

        outputDoc.close();

        PdfDocument assertDoc = new PdfDocument(new PdfReader(outFile));
        PdfIndirectReference page1ResFontObj = assertDoc.getPage(1).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();
        PdfIndirectReference page2ResFontObj = assertDoc.getPage(2).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();
        PdfIndirectReference page3ResFontObj = assertDoc.getPage(3).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();

        Assert.assertFalse(page1ResFontObj.equals(page2ResFontObj));
        Assert.assertFalse(page1ResFontObj.equals(page3ResFontObj));
        Assert.assertFalse(page2ResFontObj.equals(page3ResFontObj));
        assertDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder));
    }
}
