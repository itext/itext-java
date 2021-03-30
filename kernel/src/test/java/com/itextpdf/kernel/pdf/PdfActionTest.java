/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.action.PdfActionOcgState;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLineAnnotation;
import com.itextpdf.kernel.pdf.filespec.PdfStringFS;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import com.itextpdf.io.util.MessageFormatUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfActionTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfActionTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfActionTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void actionTest01() throws Exception {
        PdfDocument document = createDocument(new PdfWriter(destinationFolder + "actionTest01.pdf"), true);

        document.getCatalog().setOpenAction(PdfAction.createURI("http://itextpdf.com/"));

        document.close();

        System.out.println(MessageFormatUtil.format("Please open document {0} and make sure that you're automatically redirected to {1} site.", destinationFolder + "actionTest01.pdf", "http://itextpdf.com"));
    }

    @Test
    public void actionTest02() throws Exception {
        PdfDocument document = createDocument(new PdfWriter(destinationFolder + "actionTest02.pdf"), false);

        document.getPage(2).setAdditionalAction(PdfName.O, PdfAction.createURI("http://itextpdf.com/"));

        document.close();

        System.out.println(MessageFormatUtil.format("Please open document {0} at page 2 and make sure that you're automatically redirected to {1} site.", destinationFolder + "actionTest02.pdf", "http://itextpdf.com"));
    }

    @Test
    public void soundActionTest() throws Exception {
        String fileName = "soundActionTest.pdf";
        PdfDocument document = createDocument(new PdfWriter(destinationFolder + fileName), false);

        InputStream is = new FileInputStream(sourceFolder + "sample.aif");
        PdfStream sound1 = new PdfStream(document, is);
        sound1.put(PdfName.R, new PdfNumber(32117));
        sound1.put(PdfName.E, PdfName.Signed);
        sound1.put(PdfName.B, new PdfNumber(16));
        sound1.put(PdfName.C, new PdfNumber(1));

        document.getPage(2).setAdditionalAction(PdfName.O, PdfAction.createSound(sound1));
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void soundActionWithRepeatFlagTest() throws Exception {
        String fileName = "soundActionWithRepeatFlagTest.pdf";
        PdfDocument document = createDocument(new PdfWriter(destinationFolder + fileName), false);

        InputStream is = new FileInputStream(sourceFolder + "sample.aif");
        PdfStream sound1 = new PdfStream(document, is);
        sound1.put(PdfName.R, new PdfNumber(32117));
        sound1.put(PdfName.E, PdfName.Signed);
        sound1.put(PdfName.B, new PdfNumber(16));
        sound1.put(PdfName.C, new PdfNumber(1));

        document.getPage(2)
                .setAdditionalAction(PdfName.O, PdfAction.createSound(sound1,1f, false, true, false));
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void soundActionWithToBigVolumeTest() throws Exception {
        PdfDocument document = createDocument(new PdfWriter(new ByteArrayOutputStream()), false);

        InputStream is = new FileInputStream(sourceFolder + "sample.aif");
        PdfStream sound1 = new PdfStream(document, is);
        sound1.put(PdfName.R, new PdfNumber(32117));
        sound1.put(PdfName.E, PdfName.Signed);
        sound1.put(PdfName.B, new PdfNumber(16));
        sound1.put(PdfName.C, new PdfNumber(1));

        try {
            document.getPage(2)
                    .setAdditionalAction(PdfName.O, PdfAction.createSound(sound1, 1.1f, false, false, false));
            Assert.fail("Exception not thrown");
        } catch (Exception e) {
            Assert.assertEquals("volume", e.getMessage());
        }
        document.close();
    }

    @Test
    public void soundActionWithToLowVolumeTest() throws Exception {
        PdfDocument document = createDocument(new PdfWriter(new ByteArrayOutputStream()), false);

        InputStream is = new FileInputStream(sourceFolder + "sample.aif");
        PdfStream sound1 = new PdfStream(document, is);
        sound1.put(PdfName.R, new PdfNumber(32117));
        sound1.put(PdfName.E, PdfName.Signed);
        sound1.put(PdfName.B, new PdfNumber(16));
        sound1.put(PdfName.C, new PdfNumber(1));

        try {
            document.getPage(2)
                    .setAdditionalAction(PdfName.O, PdfAction.createSound(sound1, -1.1f, false, false, false));
            Assert.fail("Exception not thrown");
        } catch (Exception e) {
            Assert.assertEquals("volume", e.getMessage());
        }
        document.close();
    }

    @Test
    public void ocgStateTest() throws Exception {
        PdfName stateName = PdfName.ON;

        PdfDictionary ocgDict1 = new PdfDictionary();
        ocgDict1.put(PdfName.Type, PdfName.OCG);
        ocgDict1.put(PdfName.Name, new PdfName("ocg1"));

        PdfDictionary ocgDict2 = new PdfDictionary();
        ocgDict2.put(PdfName.Type, PdfName.OCG);
        ocgDict2.put(PdfName.Name, new PdfName("ocg2"));

        List<PdfDictionary> dicts = new ArrayList<>();
        dicts.add(ocgDict1);
        dicts.add(ocgDict2);

        List<PdfActionOcgState> ocgStates = new ArrayList<>();
        ocgStates.add(new PdfActionOcgState(stateName, dicts));

        String fileName = "ocgStateTest.pdf";
        PdfDocument document = createDocument(new PdfWriter(destinationFolder + fileName), false);
        document.getPage(1).setAdditionalAction(PdfName.O, PdfAction.createSetOcgState(ocgStates));
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void launchActionTest() throws Exception {
        String fileName = "launchActionTest.pdf";
        PdfDocument document = createDocument(new PdfWriter(destinationFolder + fileName), false);

        document.getPage(1).setAdditionalAction(PdfName.O, PdfAction.createLaunch(new PdfStringFS("launch.sh")));
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void launchActionOnNewWindowTest() throws Exception {
        String fileName = "launchActionOnNewWindowTest.pdf";
        PdfDocument document = createDocument(new PdfWriter(destinationFolder + fileName), false);

        document.getPage(1).setAdditionalAction(PdfName.O,
                PdfAction.createLaunch(new PdfStringFS("launch.sh"), true));
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void createHiddenAnnotationTest() throws Exception {
        String fileName = "createHiddenAnnotationTest.pdf";
        PdfDocument document = createDocument(new PdfWriter(destinationFolder + fileName), false);

        PdfAnnotation annotation = new PdfLineAnnotation(new Rectangle(10, 10, 200, 200),
                new float[] {50, 750, 50, 750});
        document.getPage(1).setAdditionalAction(PdfName.O, PdfAction.createHide(annotation, true));
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void createHiddenAnnotationsTest() throws Exception {
        String fileName = "createHiddenAnnotationsTest.pdf";
        PdfDocument document = createDocument(new PdfWriter(destinationFolder + fileName), false);

        PdfAnnotation[] annotations = new PdfAnnotation[] {
                new PdfLineAnnotation(new Rectangle(10, 10, 200, 200), new float[] {50, 750, 50, 750}),
                new PdfLineAnnotation(new Rectangle(200, 200, 200, 200), new float[] {50, 750, 50, 750})
        };
        document.getPage(1).setAdditionalAction(PdfName.O, PdfAction.createHide(annotations, true));
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void createHiddenByFieldNameTest() throws Exception {
        String fileName = "createHiddenByFieldNameTest.pdf";
        PdfDocument document = createDocument(new PdfWriter(destinationFolder + fileName), false);

        document.getPage(1).setAdditionalAction(PdfName.O, PdfAction.createHide("name", true));
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void createHiddenByFieldNamesTest() throws Exception {
        String fileName = "createHiddenByFieldNamesTest.pdf";
        PdfDocument document = createDocument(new PdfWriter(destinationFolder + fileName), false);

        document.getPage(1).setAdditionalAction(PdfName.O, PdfAction.createHide(new String[] {"name1", "name2"}, true));
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void createNamedTest() throws Exception {
        String fileName = "createNamedTest.pdf";
        PdfDocument document = createDocument(new PdfWriter(destinationFolder + fileName), false);

        document.getPage(1).setAdditionalAction(PdfName.O, PdfAction.createNamed(PdfName.LastPage));
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void createJavaScriptTest() throws Exception {
        String fileName = "createJavaScriptTest.pdf";
        PdfDocument document = createDocument(new PdfWriter(destinationFolder + fileName), false);

        String javaScriptRotatePages = "this.setPageRotations(0,2,90)";
        document.getPage(1).setAdditionalAction(PdfName.O, PdfAction.createJavaScript(javaScriptRotatePages));
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void soundAndNextJavaScriptActionTest() throws Exception {
        String fileName = "soundAndNextJavaScriptActionTest.pdf";
        PdfDocument document = createDocument(new PdfWriter(destinationFolder + fileName), false);

        InputStream is = new FileInputStream(sourceFolder + "sample.aif");
        PdfStream sound1 = new PdfStream(document, is);
        sound1.put(PdfName.R, new PdfNumber(32117));
        sound1.put(PdfName.E, PdfName.Signed);
        sound1.put(PdfName.B, new PdfNumber(16));
        sound1.put(PdfName.C, new PdfNumber(1));

        PdfAction action = PdfAction.createSound(sound1);
        action.next(PdfAction.createJavaScript("this.setPageRotations(0,2,90)"));
        document.getPage(2).setAdditionalAction(PdfName.O, action);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void soundAndTwoNextJavaScriptActionTest() throws Exception {
        String fileName = "soundAndTwoNextJavaScriptActionTest.pdf";
        PdfDocument document = createDocument(new PdfWriter(destinationFolder + fileName), false);

        InputStream is = new FileInputStream(sourceFolder + "sample.aif");
        PdfStream sound1 = new PdfStream(document, is);
        sound1.put(PdfName.R, new PdfNumber(32117));
        sound1.put(PdfName.E, PdfName.Signed);
        sound1.put(PdfName.B, new PdfNumber(16));
        sound1.put(PdfName.C, new PdfNumber(1));

        PdfAction action = PdfAction.createSound(sound1);
        action.next(PdfAction.createJavaScript("this.setPageRotations(0,2,90)"));
        action.next(PdfAction.createJavaScript("this.setPageRotations(0,2,180)"));
        document.getPage(2).setAdditionalAction(PdfName.O, action);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    private PdfDocument createDocument(PdfWriter writer, boolean flushPages) {
        PdfDocument document = new PdfDocument(writer);
        PdfPage p1 = document.addNewPage();
        PdfStream str1 = p1.getFirstContentStream();
        str1.getOutputStream().writeString("1 0 0 rg 100 600 100 100 re f\n");
        if (flushPages)
            p1.flush();
        PdfPage p2 = document.addNewPage();
        PdfStream str2 = p2.getFirstContentStream();
        str2.getOutputStream().writeString("0 1 0 rg 100 600 100 100 re f\n");
        if (flushPages)
            p2.flush();
        PdfPage p3 = document.addNewPage();
        PdfStream str3 = p3.getFirstContentStream();
        str3.getOutputStream().writeString("0 0 1 rg 100 600 100 100 re f\n");
        if (flushPages)
            p3.flush();
        return document;
    }

}
