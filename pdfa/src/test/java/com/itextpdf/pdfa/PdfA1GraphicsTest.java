/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
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

import com.itextpdf.kernel.color.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.color.DeviceCmyk;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.kernel.xmp.XMPException;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class PdfA1GraphicsTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String cmpFolder = sourceFolder + "cmp/PdfA1GraphicsTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfA1GraphicsTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void colorCheckTest1() throws IOException, XMPException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.DevicergbAndDevicecmykColorspacesCannotBeUsedBothInOneFile);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, outputIntent);
        doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

        canvas.setFillColor(new DeviceCmyk(0.1f, 0.1f, 0.1f, 0.1f));
        canvas.moveTo(doc.getDefaultPageSize().getLeft(), doc.getDefaultPageSize().getBottom());
        canvas.lineTo(doc.getDefaultPageSize().getRight(), doc.getDefaultPageSize().getBottom());
        canvas.lineTo(doc.getDefaultPageSize().getRight(), doc.getDefaultPageSize().getTop());
        canvas.fill();
        canvas.setFillColor(ColorConstants.RED);
        canvas.moveTo(doc.getDefaultPageSize().getRight(), doc.getDefaultPageSize().getTop());
        canvas.lineTo(doc.getDefaultPageSize().getRight(), doc.getDefaultPageSize().getBottom());
        canvas.lineTo(doc.getDefaultPageSize().getLeft(), doc.getDefaultPageSize().getBottom());
        canvas.fill();

        doc.close();
    }

    @Test
    public void colorCheckTest2() throws IOException, XMPException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.DevicecmykMayBeUsedOnlyIfTheFileHasACmykPdfAOutputIntent);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, outputIntent);
        doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

        canvas.setFillColor(new DeviceCmyk(0.1f, 0.1f, 0.1f, 0.1f));
        canvas.moveTo(doc.getDefaultPageSize().getLeft(), doc.getDefaultPageSize().getBottom());
        canvas.lineTo(doc.getDefaultPageSize().getRight(), doc.getDefaultPageSize().getBottom());
        canvas.lineTo(doc.getDefaultPageSize().getRight(), doc.getDefaultPageSize().getTop());
        canvas.fill();

        doc.close();
    }

    @Test
    public void colorCheckTest3() throws IOException, XMPException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.IfDeviceRgbCmykGrayUsedInFileThatFileShallContainPdfaOutputIntent);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, null);
        doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

        canvas.setFillColor(ColorConstants.GREEN);
        canvas.moveTo(doc.getDefaultPageSize().getLeft(), doc.getDefaultPageSize().getBottom());
        canvas.lineTo(doc.getDefaultPageSize().getRight(), doc.getDefaultPageSize().getBottom());
        canvas.lineTo(doc.getDefaultPageSize().getRight(), doc.getDefaultPageSize().getTop());
        canvas.fill();

        doc.close();
    }

    @Test
    public void colorCheckTest4() throws IOException, XMPException, InterruptedException {
        String outPdf = destinationFolder + "pdfA1b_colorCheckTest4.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA1b_colorCheckTest4.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, outputIntent);
        doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

        canvas.setFillColor(ColorConstants.GREEN);
        canvas.moveTo(doc.getDefaultPageSize().getLeft(), doc.getDefaultPageSize().getBottom());
        canvas.lineTo(doc.getDefaultPageSize().getRight(), doc.getDefaultPageSize().getBottom());
        canvas.lineTo(doc.getDefaultPageSize().getRight(), doc.getDefaultPageSize().getTop());
        canvas.fill();

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void egsCheckTest1() throws IOException, XMPException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.AnExtgstateDictionaryShallNotContainTheTrKey);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, outputIntent);
        doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

        canvas.setExtGState(new PdfExtGState().setTransferFunction(new PdfName("Test")));
        canvas.rectangle(30, 30, 100, 100).fill();

        doc.close();
    }

    @Test
    public void egsCheckTest2() throws IOException, XMPException, InterruptedException {
        String outPdf = destinationFolder + "pdfA1b_egsCheckTest2.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA1b_egsCheckTest2.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, outputIntent);
        doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

        canvas.setExtGState(new PdfExtGState().setTransferFunction2(PdfName.Default));
        canvas.rectangle(30, 30, 100, 100).fill();

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void egsCheckTest3() throws IOException, XMPException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.AnExtgstateDictionaryShallNotContainTheTR2KeyWithAValueOtherThanDefault);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, outputIntent);
        doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

        canvas.setExtGState(new PdfExtGState().setTransferFunction2(new PdfName("Test")));
        canvas.rectangle(30, 30, 100, 100).fill();

        doc.close();
    }

    @Test
    public void egsCheckTest4() throws IOException, XMPException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.IfSpecifiedRenderingShallBeOneOfTheFollowingRelativecolorimetricAbsolutecolorimetricPerceptualOrSaturation);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, outputIntent);
        doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

        canvas.setExtGState(new PdfExtGState().setRenderingIntent(new PdfName("Test")));
        canvas.rectangle(30, 30, 100, 100).fill();

        doc.close();
    }

    @Test
    public void transparencyCheckTest1() throws IOException, XMPException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.AGroupObjectWithAnSKeyWithAValueOfTransparencyShallNotBeIncludedInAFormXobject);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, outputIntent);
        doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(100, 100));
        PdfCanvas xObjCanvas = new PdfCanvas(xObject, doc);
        xObjCanvas.rectangle(30, 30, 10, 10).fill();

        //imitating transparency group
        //todo replace with real transparency group logic when implemented
        PdfDictionary group = new PdfDictionary();
        group.put(PdfName.S, PdfName.Transparency);
        xObject.put(PdfName.Group, group);
        canvas.addXObject(xObject, new Rectangle(300, 300));

        doc.close();
    }

    @Test
    public void transparencyCheckTest2() throws IOException, XMPException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.TheSmaskKeyIsNotAllowedInExtgstate);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, outputIntent);
        doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

        canvas.setExtGState(new PdfExtGState().setSoftMask(new PdfName("Test")));
        canvas.rectangle(30, 30, 100, 100).fill();

        doc.close();
    }

    @Test
    public void transparencyCheckTest3() throws IOException, XMPException, InterruptedException {
        String outPdf = destinationFolder + "pdfA1b_transparencyCheckTest3.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA1b_transparencyCheckTest3.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, outputIntent);
        doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());

        canvas.setExtGState(new PdfExtGState().setSoftMask(PdfName.None));
        canvas.rectangle(30, 30, 100, 100).fill();

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    private void compareResult(String outPdf, String cmpPdf) throws IOException, InterruptedException {
        String result = new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (result != null) {
            fail(result);
        }
    }
}
