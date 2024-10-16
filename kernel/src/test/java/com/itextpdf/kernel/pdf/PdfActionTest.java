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
package com.itextpdf.kernel.pdf;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.action.PdfActionOcgState;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLineAnnotation;
import com.itextpdf.kernel.pdf.filespec.PdfStringFS;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfActionTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfActionTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfActionTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }
    
    @Test
    public void actionTest01() throws Exception {
        PdfDocument document = createDocument(CompareTool.createTestPdfWriter(destinationFolder + "actionTest01.pdf"), true);

        document.getCatalog().setOpenAction(PdfAction.createURI("http://itextpdf.com/"));

        document.close();

        System.out.println(MessageFormatUtil.format("Please open document {0} and make sure that you're automatically redirected to {1} site.", destinationFolder + "actionTest01.pdf", "http://itextpdf.com"));
    }

    @Test
    public void actionTest02() throws Exception {
        PdfDocument document = createDocument(CompareTool.createTestPdfWriter(destinationFolder + "actionTest02.pdf"), false);

        document.getPage(2).setAdditionalAction(PdfName.O, PdfAction.createURI("http://itextpdf.com/"));

        document.close();

        System.out.println(MessageFormatUtil.format("Please open document {0} at page 2 and make sure that you're automatically redirected to {1} site.", destinationFolder + "actionTest02.pdf", "http://itextpdf.com"));
    }

    @Test
    public void actionTest03() throws Exception {
        PdfDocument document = createDocument(CompareTool.createTestPdfWriter(destinationFolder + "actionTest03.pdf"), true);
        String uri = "http://itextpdf.com/";

        document.getCatalog().setOpenAction(PdfAction.createURI(new URI(uri)));
        Assertions.assertEquals(new PdfString(uri),
                document.getCatalog().getPdfObject().getAsDictionary(PdfName.OpenAction).get(PdfName.URI));
        document.close();

        System.out.println(MessageFormatUtil.format("Please open document {0} and make sure that you're automatically redirected to {1} site.", destinationFolder + "actionTest01.pdf", "http://itextpdf.com"));
    }

    @Test
    public void soundActionTest() throws Exception {
        String fileName = "soundActionTest.pdf";
        PdfDocument document = createDocument(CompareTool.createTestPdfWriter(destinationFolder + fileName), false);

        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sample.aif");
        PdfStream sound1 = new PdfStream(document, is);
        sound1.put(PdfName.R, new PdfNumber(32117));
        sound1.put(PdfName.E, PdfName.Signed);
        sound1.put(PdfName.B, new PdfNumber(16));
        sound1.put(PdfName.C, new PdfNumber(1));

        document.getPage(2).setAdditionalAction(PdfName.O, PdfAction.createSound(sound1));
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void soundActionWithRepeatFlagTest() throws Exception {
        String fileName = "soundActionWithRepeatFlagTest.pdf";
        PdfDocument document = createDocument(CompareTool.createTestPdfWriter(destinationFolder + fileName), false);

        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sample.aif");
        PdfStream sound1 = new PdfStream(document, is);
        sound1.put(PdfName.R, new PdfNumber(32117));
        sound1.put(PdfName.E, PdfName.Signed);
        sound1.put(PdfName.B, new PdfNumber(16));
        sound1.put(PdfName.C, new PdfNumber(1));

        document.getPage(2)
                .setAdditionalAction(PdfName.O, PdfAction.createSound(sound1,1f, false, true, false));
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void soundActionWithToBigVolumeTest() throws Exception {
        PdfDocument document = createDocument(new PdfWriter(new ByteArrayOutputStream()), false);

        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sample.aif");
        PdfStream sound1 = new PdfStream(document, is);
        sound1.put(PdfName.R, new PdfNumber(32117));
        sound1.put(PdfName.E, PdfName.Signed);
        sound1.put(PdfName.B, new PdfNumber(16));
        sound1.put(PdfName.C, new PdfNumber(1));

        try {
            document.getPage(2)
                    .setAdditionalAction(PdfName.O, PdfAction.createSound(sound1, 1.1f, false, false, false));
            Assertions.fail("Exception not thrown");
        } catch (Exception e) {
            Assertions.assertEquals("volume", e.getMessage());
        }
        document.close();
    }

    @Test
    public void soundActionWithToLowVolumeTest() throws Exception {
        PdfDocument document = createDocument(new PdfWriter(new ByteArrayOutputStream()), false);

        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sample.aif");
        PdfStream sound1 = new PdfStream(document, is);
        sound1.put(PdfName.R, new PdfNumber(32117));
        sound1.put(PdfName.E, PdfName.Signed);
        sound1.put(PdfName.B, new PdfNumber(16));
        sound1.put(PdfName.C, new PdfNumber(1));

        try {
            document.getPage(2)
                    .setAdditionalAction(PdfName.O, PdfAction.createSound(sound1, -1.1f, false, false, false));
            Assertions.fail("Exception not thrown");
        } catch (Exception e) {
            Assertions.assertEquals("volume", e.getMessage());
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
        PdfDocument document = createDocument(CompareTool.createTestPdfWriter(destinationFolder + fileName), false);
        document.getPage(1).setAdditionalAction(PdfName.O, PdfAction.createSetOcgState(ocgStates));
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void launchActionTest() throws Exception {
        String fileName = "launchActionTest.pdf";
        PdfDocument document = createDocument(CompareTool.createTestPdfWriter(destinationFolder + fileName), false);

        document.getPage(1).setAdditionalAction(PdfName.O, PdfAction.createLaunch(new PdfStringFS("launch.sh")));
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void launchActionOnNewWindowTest() throws Exception {
        String fileName = "launchActionOnNewWindowTest.pdf";
        PdfDocument document = createDocument(CompareTool.createTestPdfWriter(destinationFolder + fileName), false);

        document.getPage(1).setAdditionalAction(PdfName.O,
                PdfAction.createLaunch(new PdfStringFS("launch.sh"), true));
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void createHiddenAnnotationTest() throws Exception {
        String fileName = "createHiddenAnnotationTest.pdf";
        PdfDocument document = createDocument(CompareTool.createTestPdfWriter(destinationFolder + fileName), false);

        PdfAnnotation annotation = new PdfLineAnnotation(new Rectangle(10, 10, 200, 200),
                new float[] {50, 750, 50, 750});
        document.getPage(1).setAdditionalAction(PdfName.O, PdfAction.createHide(annotation, true));
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void createHiddenAnnotationsTest() throws Exception {
        String fileName = "createHiddenAnnotationsTest.pdf";
        PdfDocument document = createDocument(CompareTool.createTestPdfWriter(destinationFolder + fileName), false);

        PdfAnnotation[] annotations = new PdfAnnotation[] {
                new PdfLineAnnotation(new Rectangle(10, 10, 200, 200), new float[] {50, 750, 50, 750}),
                new PdfLineAnnotation(new Rectangle(200, 200, 200, 200), new float[] {50, 750, 50, 750})
        };
        document.getPage(1).setAdditionalAction(PdfName.O, PdfAction.createHide(annotations, true));
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void createHiddenByFieldNameTest() throws Exception {
        String fileName = "createHiddenByFieldNameTest.pdf";
        PdfDocument document = createDocument(CompareTool.createTestPdfWriter(destinationFolder + fileName), false);

        document.getPage(1).setAdditionalAction(PdfName.O, PdfAction.createHide("name", true));
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void createHiddenByFieldNamesTest() throws Exception {
        String fileName = "createHiddenByFieldNamesTest.pdf";
        PdfDocument document = createDocument(CompareTool.createTestPdfWriter(destinationFolder + fileName), false);

        document.getPage(1).setAdditionalAction(PdfName.O, PdfAction.createHide(new String[] {"name1", "name2"}, true));
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void createNamedTest() throws Exception {
        String fileName = "createNamedTest.pdf";
        PdfDocument document = createDocument(CompareTool.createTestPdfWriter(destinationFolder + fileName), false);

        document.getPage(1).setAdditionalAction(PdfName.O, PdfAction.createNamed(PdfName.LastPage));
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void createJavaScriptTest() throws Exception {
        String fileName = "createJavaScriptTest.pdf";
        PdfDocument document = createDocument(CompareTool.createTestPdfWriter(destinationFolder + fileName), false);

        String javaScriptRotatePages = "this.setPageRotations(0,2,90)";
        document.getPage(1).setAdditionalAction(PdfName.O, PdfAction.createJavaScript(javaScriptRotatePages));
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void soundAndNextJavaScriptActionTest() throws Exception {
        String fileName = "soundAndNextJavaScriptActionTest.pdf";
        PdfDocument document = createDocument(CompareTool.createTestPdfWriter(destinationFolder + fileName), false);

        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sample.aif");
        PdfStream sound1 = new PdfStream(document, is);
        sound1.put(PdfName.R, new PdfNumber(32117));
        sound1.put(PdfName.E, PdfName.Signed);
        sound1.put(PdfName.B, new PdfNumber(16));
        sound1.put(PdfName.C, new PdfNumber(1));

        PdfAction action = PdfAction.createSound(sound1);
        action.next(PdfAction.createJavaScript("this.setPageRotations(0,2,90)"));
        document.getPage(2).setAdditionalAction(PdfName.O, action);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void soundAndTwoNextJavaScriptActionTest() throws Exception {
        String fileName = "soundAndTwoNextJavaScriptActionTest.pdf";
        PdfDocument document = createDocument(CompareTool.createTestPdfWriter(destinationFolder + fileName), false);

        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sample.aif");
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

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
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
