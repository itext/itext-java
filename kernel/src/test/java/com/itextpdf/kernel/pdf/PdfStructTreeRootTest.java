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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
public class PdfStructTreeRootTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfStructTreeRootTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfStructTreeRootTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void directStructTreeRootReadingModeTest() throws IOException {
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "directStructTreeRoot.pdf"));
        assertTrue(document.isTagged());
        document.close();
    }

    @Test
    public void directStructTreeRootStampingModeTest() throws IOException {
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "directStructTreeRoot.pdf"),
                new PdfWriter(new ByteArrayOutputStream()));
        assertTrue(document.isTagged());
        document.close();
    }

    @Test
    public void severalSameElementsInStructTreeRootTest() throws IOException {
        String inFile = sourceFolder + "severalSameElementsInStructTreeRoot.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(inFile), new PdfWriter(new ByteArrayOutputStream()));

        PdfStructTreeRoot structTreeRoot = doc.getStructTreeRoot();

        List<PdfStructElem> kidsOfStructTreeRootKids = new ArrayList<>();
        for (IStructureNode kid : structTreeRoot.getKids()) {
            for (IStructureNode kidOfKid : kid.getKids()) {
                if (kidOfKid instanceof PdfStructElem) {
                    kidsOfStructTreeRootKids.add((PdfStructElem) kidOfKid);
                }
            }
        }

        structTreeRoot.flush();

        for (PdfStructElem kidsOfStructTreeRootKid : kidsOfStructTreeRootKids) {
            Assert.assertTrue(kidsOfStructTreeRootKid.isFlushed());
        }
    }

    @Test
    public void idTreeIsLazyTest() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(os).setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setTagged();

        pdfDoc.addNewPage().getFirstContentStream().setData("q Q".getBytes(StandardCharsets.UTF_8));

        pdfDoc.getStructTreeRoot().getIdTree();
        pdfDoc.close();

        // we've retrieved the ID tree but not used it -> it should be left out in the resulting file
        PdfReader r = new PdfReader(new ByteArrayInputStream(os.toByteArray()));
        PdfDocument readPdfDoc = new PdfDocument(r);
        Assert.assertFalse(readPdfDoc.getStructTreeRoot().getPdfObject().containsKey(PdfName.IDTree));

    }
}
