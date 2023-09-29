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
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.pdfa.VeraPdfValidator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Category(IntegrationTest.class)
public class PdfA4GraphicsCheckTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String CMP_FOLDER = SOURCE_FOLDER + "cmp/PdfA4GraphicsCheckTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfa/PdfA4GraphicsCheckTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void validHalftoneTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_halftone.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_halftone.pdf";
        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = new FileInputStream(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);

        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent)) {
            doc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(doc.getLastPage());
            PdfDictionary colourantHalftone = new PdfDictionary();
            colourantHalftone.put(PdfName.HalftoneType, new PdfNumber(1));
            colourantHalftone.put(PdfName.TransferFunction, PdfName.Identity);

            PdfDictionary halftone = new PdfDictionary();
            halftone.put(PdfName.HalftoneType, new PdfNumber(5));
            halftone.put(new PdfName("Green"), colourantHalftone);
            canvas.setExtGState(new PdfExtGState().setHalftone(halftone));
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));

        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void validHalftoneType1Test() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_halftone1.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_halftone1.pdf";
        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = new FileInputStream(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);

        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent)) {
            doc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

            PdfDictionary halftone = new PdfDictionary();
            halftone.put(PdfName.HalftoneType, new PdfNumber(1));
            canvas.setExtGState(new PdfExtGState().setHalftone(halftone));
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));

        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void validHalftoneTest2() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_halftone2.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_halftone2.pdf";
        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = new FileInputStream(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);

        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent)) {
            doc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(doc.getLastPage());
            PdfDictionary colourantHalftone = new PdfDictionary();
            colourantHalftone.put(PdfName.HalftoneType, new PdfNumber(1));

            PdfDictionary halftone = new PdfDictionary();
            halftone.put(PdfName.HalftoneType, new PdfNumber(5));
            halftone.put(new PdfName("Green"), colourantHalftone);
            canvas.setExtGState(new PdfExtGState().setHalftone(halftone));
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));

        //this pdf is invalid according to pdf 2.0 so we don't use verapdf check here
        //Table 128 — Entries in a Type 1 halftone dictionary
        //TransferFunction - This entry shall be present if the dictionary is a component of a Type 5 halftone
        // (see 10.6.5.6, "Type 5 halftones") and represents either a nonprimary or nonstandard primary colour component
        // (see 10.5, "Transfer functions").
    }

    @Test
    public void validHalftoneTest3() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4_halftone3.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4_halftone3.pdf";
        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = new FileInputStream(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);

        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent)) {
            doc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(doc.getLastPage());
            PdfDictionary colourantHalftone = new PdfDictionary();
            colourantHalftone.put(PdfName.HalftoneType, new PdfNumber(1));

            PdfDictionary halftone = new PdfDictionary();
            halftone.put(PdfName.HalftoneType, new PdfNumber(5));
            halftone.put(PdfName.Cyan, colourantHalftone);
            canvas.setExtGState(new PdfExtGState().setHalftone(halftone));
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));

        //this pdf is invalid according to pdf 2.0 so we don't use verapdf check here
        //Table 128 — Entries in a Type 1 halftone dictionary
        //TransferFunction - This entry shall be present if the dictionary is a component of a Type 5 halftone
        // (see 10.6.5.6, "Type 5 halftones") and represents either a nonprimary or nonstandard primary colour component
        // (see 10.5, "Transfer functions").
    }

    @Test
    public void invalidHalftoneTest1() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = new FileInputStream(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);

        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent)) {
            doc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(doc.getLastPage());
            PdfDictionary halftone = new PdfDictionary();
            halftone.put(PdfName.HalftoneType, new PdfNumber(1));
            halftone.put(PdfName.TransferFunction, new PdfDictionary());

            Exception e = Assert.assertThrows(PdfAConformanceException.class,
                    () -> canvas.setExtGState(new PdfExtGState().setHalftone(halftone))
            );
            Assert.assertEquals(PdfaExceptionMessageConstant.ALL_HALFTONES_CONTAINING_TRANSFER_FUNCTION_SHALL_HAVE_HALFTONETYPE_5,
                    e.getMessage());
        }
    }

    @Test
    public void invalidHalftoneTest2() throws IOException, InterruptedException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = new FileInputStream(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);

        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent)) {
            doc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(doc.getLastPage());
            PdfDictionary halftone = new PdfDictionary();
            halftone.put(PdfName.HalftoneType, new PdfNumber(5));
            halftone.put(PdfName.TransferFunction, new PdfDictionary());
            halftone.put(PdfName.Magenta, new PdfDictionary());
            Exception e = Assert.assertThrows(PdfAConformanceException.class,
                    () -> canvas.setExtGState(new PdfExtGState().setHalftone(halftone))
            );
            Assert.assertEquals(PdfaExceptionMessageConstant.ALL_HALFTONES_CONTAINING_TRANSFER_FUNCTION_SHALL_HAVE_HALFTONETYPE_5,
                    e.getMessage());
        }
    }

    @Test
    public void invalidHalftoneTest3() throws IOException, InterruptedException {
        testWithColourant(PdfName.Cyan);
    }

    @Test
    public void invalidHalftoneTest4() throws IOException, InterruptedException {
        testWithColourant(PdfName.Magenta);
    }

    @Test
    public void invalidHalftoneTest5() throws IOException, InterruptedException {
        testWithColourant(PdfName.Yellow);
    }

    @Test
    public void invalidHalftoneTest6() throws IOException, InterruptedException {
        testWithColourant(PdfName.Black);
    }

    private void testWithColourant(PdfName color) throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = new FileInputStream(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);

        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, outputIntent)) {
            doc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(doc.getLastPage());
            PdfDictionary colourantHalftone = new PdfDictionary();
            colourantHalftone.put(PdfName.HalftoneType, new PdfNumber(1));
            colourantHalftone.put(PdfName.TransferFunction, PdfName.Identity);

            PdfDictionary halftone = new PdfDictionary();
            halftone.put(PdfName.HalftoneType, new PdfNumber(5));
            halftone.put(color, colourantHalftone);
            Exception e = Assert.assertThrows(PdfAConformanceException.class,
                    () -> canvas.setExtGState(new PdfExtGState().setHalftone(halftone))
            );
            Assert.assertEquals(PdfaExceptionMessageConstant.ALL_HALFTONES_CONTAINING_TRANSFER_FUNCTION_SHALL_HAVE_HALFTONETYPE_5,
                    e.getMessage());
        }
    }
}
