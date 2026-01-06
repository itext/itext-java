/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.kernel.pdf.layer;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class PdfLayerTest extends ExtendedITextTest {

    public static final String destinationFolder = TestUtil.getOutputPath() + "/kernel/pdf/layer/PdfLayerTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/layer/PdfLayerTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }
    
    @Test
    public void layerDefaultIntents() {
        PdfLayer pdfLayer = PdfLayerTestUtils.prepareNewLayer();

        Collection<PdfName> defaultIntents = pdfLayer.getIntents();

        Assertions.assertArrayEquals(new PdfName[] {PdfName.View}, defaultIntents.toArray(new PdfName[1]));
    }

    @Test
    public void layerSetSingleIntent() {
        PdfLayer pdfLayer = PdfLayerTestUtils.prepareLayerDesignIntent();

        Collection<PdfName> defaultIntents = pdfLayer.getIntents();

        Assertions.assertArrayEquals(new PdfName[] {PdfName.Design}, defaultIntents.toArray(new PdfName[1]));
    }

    @Test
    public void layerSetSeveralIntents() {
        PdfName custom = new PdfName("Custom");
        PdfLayer pdfLayer = PdfLayerTestUtils.prepareLayerDesignAndCustomIntent(custom);

        Collection<PdfName> defaultIntents = pdfLayer.getIntents();

        Assertions.assertArrayEquals(new PdfName[] {PdfName.Design, custom}, defaultIntents.toArray(new PdfName[2]));
    }

    @Test
    public void layerSetIntentsNull() {
        PdfName custom = new PdfName("Custom");
        PdfLayer pdfLayer = PdfLayerTestUtils.prepareLayerDesignAndCustomIntent(custom);

        pdfLayer.setIntents(null);
        Collection<PdfName> postNullIntents = pdfLayer.getIntents();

        Assertions.assertArrayEquals(new PdfName[] {PdfName.View}, postNullIntents.toArray(new PdfName[1]));
    }

    @Test
    public void layerSetIntentsEmpty() {
        PdfName custom = new PdfName("Custom");
        PdfLayer pdfLayer = PdfLayerTestUtils.prepareLayerDesignAndCustomIntent(custom);

        pdfLayer.setIntents(Collections.<PdfName>emptyList());
        Collection<PdfName> postNullIntents = pdfLayer.getIntents();

        Assertions.assertArrayEquals(new PdfName[] {PdfName.View}, postNullIntents.toArray(new PdfName[1]));
    }


    @Test
    public void nestedLayers() throws IOException {
        String outPdf = destinationFolder + "nestedLayers.pdf";
        String cmpPdf = sourceFolder + "cmp_nestedLayers.pdf";
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outPdf));

        PdfFont font = PdfFontFactory.createFont();

        PdfLayer nested = new PdfLayer("Parent layer", pdfDoc);
        PdfLayer nested_1 = new PdfLayer("Nested layer 1", pdfDoc);
        PdfLayer nested_2 = new PdfLayer("Nested layer 2", pdfDoc);
        nested.addChild(nested_1);
        nested.addChild(nested_2);

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.setFontAndSize(font, 12);

        PdfLayerTestUtils.addTextInsideLayer(nested, canvas, "Parent layer text", 50, 755);

        PdfLayerTestUtils.addTextInsideLayer(nested_1, canvas, "Nested layer 1 text", 100, 700);
        PdfLayerTestUtils.addTextInsideLayer(nested_2, canvas, "Nested layers 2 text", 100, 650);

        pdfDoc.close();

        PdfLayerTestUtils.compareLayers(outPdf, cmpPdf);
    }

    @Test
    public void lockedLayer() throws IOException {
        String outPdf = destinationFolder + "lockedLayer.pdf";
        String cmpPdf = sourceFolder + "cmp_lockedLayer.pdf";
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outPdf));

        PdfFont font = PdfFontFactory.createFont();

        PdfLayer layer1 = new PdfLayer("Layer 1", pdfDoc);
        PdfLayer layer2 = new PdfLayer("Layer 2", pdfDoc);
        layer2.setLocked(true);

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.setFontAndSize(font, 12);

        PdfLayerTestUtils.addTextInsideLayer(layer1, canvas, "Layer 1 text", 100, 700);
        PdfLayerTestUtils.addTextInsideLayer(layer2, canvas, "Layer 2 text", 100, 650);

        pdfDoc.close();

        PdfLayerTestUtils.compareLayers(outPdf, cmpPdf);
    }

    @Test
    public void layerGroup() throws IOException {
        String outPdf = destinationFolder + "layerGroup.pdf";
        String cmpPdf = sourceFolder + "cmp_layerGroup.pdf";
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outPdf));

        PdfFont font = PdfFontFactory.createFont();

        PdfLayer group = PdfLayer.createTitle("Grouped layers", pdfDoc);
        PdfLayer layer1 = new PdfLayer("Group: layer 1", pdfDoc);
        PdfLayer layer2 = new PdfLayer("Group: layer 2", pdfDoc);
        group.addChild(layer1);
        group.addChild(layer2);

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.setFontAndSize(font, 12);

        PdfLayerTestUtils.addTextInsideLayer(layer1, canvas, "layer 1 in the group", 50, 700);
        PdfLayerTestUtils.addTextInsideLayer(layer2, canvas, "layer 2 in the group", 50, 675);

        pdfDoc.close();

        PdfLayerTestUtils.compareLayers(outPdf, cmpPdf);
    }

    @Test
    public void layersRadioGroup() throws IOException {
        String outPdf = destinationFolder + "layersRadioGroup.pdf";
        String cmpPdf = sourceFolder + "cmp_layersRadioGroup.pdf";
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outPdf));

        PdfFont font = PdfFontFactory.createFont();

        PdfLayer radiogroup = PdfLayer.createTitle("Radio group", pdfDoc);
        PdfLayer radio1 = new PdfLayer("Radiogroup: layer 1", pdfDoc);
        radio1.setOn(true);
        PdfLayer radio2 = new PdfLayer("Radiogroup: layer 2", pdfDoc);
        radio2.setOn(false);
        PdfLayer radio3 = new PdfLayer("Radiogroup: layer 3", pdfDoc);
        radio3.setOn(false);
        radiogroup.addChild(radio1);
        radiogroup.addChild(radio2);
        radiogroup.addChild(radio3);
        List<PdfLayer> options = new ArrayList<>();
        options.add(radio1);
        options.add(radio2);
        options.add(radio3);
        PdfLayer.addOCGRadioGroup(pdfDoc, options);

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

        canvas.setFontAndSize(font, 12);
        PdfLayerTestUtils.addTextInsideLayer(radio1, canvas, "layer option 1", 50, 600);
        PdfLayerTestUtils.addTextInsideLayer(radio2, canvas, "layer option 2", 50, 575);
        PdfLayerTestUtils.addTextInsideLayer(radio3, canvas, "layer option 3", 50, 550);

        pdfDoc.close();

        PdfLayerTestUtils.compareLayers(outPdf, cmpPdf);
    }

    @Test
    public void notPrintNotOnPanel() throws IOException {
        String outPdf = destinationFolder + "notPrintNotOnPanel.pdf";
        String cmpPdf = sourceFolder + "cmp_notPrintNotOnPanel.pdf";
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outPdf));

        PdfFont font = PdfFontFactory.createFont();

        PdfLayer notPrintedNotOnPanel = new PdfLayer("not printed", pdfDoc);
        notPrintedNotOnPanel.setOnPanel(false);
        notPrintedNotOnPanel.setPrint("Print", false);

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

        canvas.setFontAndSize(font, 14);
        PdfLayerTestUtils.addTextInsideLayer(null, canvas, "Normal page content, hello lorem ispum!", 100, 750);

        canvas.beginLayer(notPrintedNotOnPanel);
        canvas.beginText().setFontAndSize(font, 24).moveText(100, 700).showText("WHEN PRINTED THIS LINE IS NOT THERE").endText();
        canvas.beginText().setFontAndSize(font, 16).moveText(100, 680).showText("(this text layer is not in the layers panel as well)").endText();
        canvas.endLayer();

        pdfDoc.close();

        PdfLayerTestUtils.compareLayers(outPdf, cmpPdf);
    }

    @Test
    public void zoomNotOnPanel() throws IOException {
        String outPdf = destinationFolder + "zoomNotOnPanel.pdf";
        String cmpPdf = sourceFolder + "cmp_zoomNotOnPanel.pdf";
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outPdf));

        PdfFont font = PdfFontFactory.createFont();
        PdfLayer zoom = new PdfLayer("Zoom 0.75-1.25", pdfDoc);
        zoom.setOnPanel(false);
        zoom.setZoom(0.75f, 1.25f);

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

        canvas.setFontAndSize(font, 14);
        PdfLayerTestUtils.addTextInsideLayer(zoom, canvas, "Only visible if the zoomfactor is between 75 and 125%", 30, 530);

        pdfDoc.close();

        PdfLayerTestUtils.compareLayers(outPdf, cmpPdf);
    }

    @Test
    public void ocConfigUniqueName() throws IOException {
        String srcPdf = sourceFolder + "ocpConfigs.pdf";
        String outPdf = destinationFolder + "ocConfigUniqueName.pdf";
        String cmpPdf = sourceFolder + "cmp_ocConfigUniqueName.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(srcPdf), CompareTool.createTestPdfWriter(outPdf));

        // init OCProperties to check how they are processed
        pdfDoc.getCatalog().getOCProperties(true);

        pdfDoc.close();

        // start of test assertion logic
        PdfDocument resPdf = new PdfDocument(CompareTool.createOutputReader(outPdf));
        PdfDictionary d = resPdf.getCatalog().getPdfObject().getAsDictionary(PdfName.OCProperties).getAsDictionary(PdfName.D);
        Assertions.assertEquals(PdfOCProperties.OC_CONFIG_NAME_PATTERN + "2", d.getAsString(PdfName.Name).toUnicodeString());

        PdfLayerTestUtils.compareLayers(outPdf, cmpPdf);
    }

    @Test
    public void processTitledHierarchies() throws IOException {
        String srcPdf = sourceFolder + "titledHierarchies.pdf";
        String outPdf = destinationFolder + "processTitledHierarchies.pdf";
        String cmpPdf = sourceFolder + "cmp_processTitledHierarchies.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(srcPdf), CompareTool.createTestPdfWriter(outPdf));

        // init OCProperties to check how they are processed
        pdfDoc.getCatalog().getOCProperties(true);

        pdfDoc.close();

        PdfLayerTestUtils.compareLayers(outPdf, cmpPdf);
    }

    @Test
    public void setCreatorInfoAndLanguage() throws IOException {
        String outPdf = destinationFolder + "setCreatorInfoAndLanguage.pdf";
        String cmpPdf = sourceFolder + "cmp_setCreatorInfoAndLanguage.pdf";
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outPdf));

        PdfFont font = PdfFontFactory.createFont();
        PdfLayer layer = new PdfLayer("CreatorAndLanguageInfo", pdfDoc);
        layer.setCreatorInfo("iText", "Technical");
        // australian english
        layer.setLanguage("en-AU", true);

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

        canvas.setFontAndSize(font, 14);
        PdfLayerTestUtils.addTextInsideLayer(layer, canvas, "Some technical data in English.", 30, 530);

        pdfDoc.close();

        PdfLayerTestUtils.compareLayers(outPdf, cmpPdf);
    }

    @Test
    public void setUserAndPageElement() throws IOException {
        String outPdf = destinationFolder + "setUserAndPageElement.pdf";
        String cmpPdf = sourceFolder + "cmp_setUserAndPageElement.pdf";
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outPdf));

        PdfFont font = PdfFontFactory.createFont();
        PdfLayer layer = new PdfLayer("UserAndPageElement", pdfDoc);
        layer.setUser("Org", "iText");
        layer.setPageElement("HF");

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

        canvas.setFontAndSize(font, 14);
        PdfLayerTestUtils.addTextInsideLayer(layer, canvas, "Page 1 of 1.", 30, 780);

        pdfDoc.close();

        PdfLayerTestUtils.compareLayers(outPdf, cmpPdf);
    }

    @Test
    public void setExportViewIsTrue() throws IOException {
        String outPdf = destinationFolder + "setExportViewIsTrue.pdf";
        String cmpPdf = sourceFolder + "cmp_setExportViewIsTrue.pdf";
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outPdf));

        boolean view = true;
        createCustomExportLayers(pdfDoc, view);

        PdfLayerTestUtils.compareLayers(outPdf, cmpPdf);
    }

    @Test
    public void setExportViewIsFalse() throws IOException {
        String outPdf = destinationFolder + "setExportViewIsFalse.pdf";
        String cmpPdf = sourceFolder + "cmp_setExportViewIsFalse.pdf";
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outPdf));

        boolean view = false;
        createCustomExportLayers(pdfDoc, view);

        PdfLayerTestUtils.compareLayers(outPdf, cmpPdf);
    }

    private void createCustomExportLayers(PdfDocument pdfDoc, boolean view) throws IOException {
        PdfFont font = PdfFontFactory.createFont();
        PdfLayer layerTrue = new PdfLayer("Export - true", pdfDoc);
        layerTrue.setExport(true);
        layerTrue.setView(view);

        PdfLayer layerFalse = new PdfLayer("Export - false", pdfDoc);
        layerFalse.setExport(false);
        layerFalse.setView(view);

        PdfLayer layerDflt = new PdfLayer("Export - default", pdfDoc);
        layerDflt.setView(view);

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

        canvas.setFontAndSize(font, 24);
        PdfLayerTestUtils.addTextInsideLayer(null, canvas, "Export this PDF as image!", 30, 580);

        canvas.setFontAndSize(font, 14);
        PdfLayerTestUtils.addTextInsideLayer(layerTrue, canvas, "Export layer - true.", 30, 780);
        PdfLayerTestUtils.addTextInsideLayer(null, canvas, "When saved as image text above is expected to be shown.", 30, 765);

        PdfLayerTestUtils.addTextInsideLayer(layerFalse, canvas, "Export layer - false.", 30, 730);
        PdfLayerTestUtils.addTextInsideLayer(null, canvas, "When saved as image text above is expected to be hidden.", 30, 715);

        PdfLayerTestUtils.addTextInsideLayer(layerDflt, canvas, "Export layer - default.", 30, 680);
        PdfLayerTestUtils.addTextInsideLayer(null, canvas, "When saved as image text above is expected to have layer visibility.", 30, 665);

        pdfDoc.close();
    }

    @Test
    public void testInStamperMode1() throws IOException, InterruptedException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "input_layered.pdf"),
                CompareTool.createTestPdfWriter(destinationFolder + "output_copy_layered.pdf"));
        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "output_copy_layered.pdf", sourceFolder + "cmp_output_copy_layered.pdf", destinationFolder, "diff"));
    }

    @Test
    public void testInStamperMode2() throws IOException, InterruptedException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "input_layered.pdf"),
                CompareTool.createTestPdfWriter(destinationFolder + "output_layered.pdf"));

        PdfCanvas canvas = new PdfCanvas(pdfDoc, 1);

        PdfLayer newLayer = new PdfLayer("appended", pdfDoc);
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);
        PdfLayerTestUtils.addTextInsideLayer(newLayer, canvas, "APPENDED CONTENT", 200, 600);

        List<PdfLayer> allLayers = pdfDoc.getCatalog().getOCProperties(true).getLayers();
        for (PdfLayer layer : allLayers) {
            if (layer.isLocked())
                layer.setLocked(false);
            if ("Grouped layers".equals(layer.getTitle())) {
                layer.addChild(newLayer);
            }
        }

        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "output_layered.pdf", sourceFolder + "cmp_output_layered.pdf", destinationFolder, "diff"));
    }

    @Test
    public void testReadAllLayersFromPage1() throws IOException, InterruptedException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "input_layered.pdf"),
                CompareTool.createTestPdfWriter(destinationFolder + "output_layered_2.pdf"));

        PdfCanvas canvas = new PdfCanvas(pdfDoc, 1);

        //create layer on page
        PdfLayer newLayer = new PdfLayer("appended", pdfDoc);
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 18);
        PdfLayerTestUtils.addTextInsideLayer(newLayer, canvas, "APPENDED CONTENT", 200, 600);

        List<PdfLayer> layersFromCatalog = pdfDoc.getCatalog().getOCProperties(true).getLayers();
        Assertions.assertEquals(13, layersFromCatalog.size());
        PdfPage page = pdfDoc.getPage(1);
        Set<PdfLayer> layersFromPage = page.getPdfLayers();
        Assertions.assertEquals(11, layersFromPage.size());

        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "output_layered_2.pdf", sourceFolder + "cmp_output_layered_2.pdf", destinationFolder, "diff"));
    }

    @Test
    public void testReadAllLayersFromDocumentWithComplexOCG() throws IOException, InterruptedException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "input_complex_layers.pdf"),
                CompareTool.createTestPdfWriter(destinationFolder + "output_complex_layers.pdf"));

        List<PdfLayer> layersFromCatalog = pdfDoc.getCatalog().getOCProperties(true).getLayers();
        Assertions.assertEquals(12, layersFromCatalog.size());
        PdfPage page = pdfDoc.getPage(1);
        Set<PdfLayer> layersFromPage = page.getPdfLayers();
        Assertions.assertEquals(10, layersFromPage.size());
        pdfDoc.close();
    }


    //Read OCGs from different locations (annotations, content streams, xObjects) test block

    @Test
    public void testReadOcgFromStreamProperties() throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument document = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = document.addNewPage();

                PdfResources pdfResource = page.getResources();
                pdfResource.addProperties(new PdfLayer("name", document).getPdfObject());
                pdfResource.makeIndirect(document);

                Set<PdfLayer> layersFromPage = page.getPdfLayers();
                Assertions.assertEquals(1, layersFromPage.size());
            }
        }
    }

    @Test
    public void testReadOcgFromAnnotation() throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = fromDocument.addNewPage();
                PdfAnnotation annotation = new PdfTextAnnotation(new Rectangle(50, 10));
                annotation.setLayer(new PdfLayer("name", fromDocument));
                page.addAnnotation(annotation);

                Set<PdfLayer> layersFromPage = page.getPdfLayers();
                Assertions.assertEquals(1, layersFromPage.size());
            }
        }
    }

    @Test
    public void testReadOcgFromFlushedAnnotation() throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = fromDocument.addNewPage();
                PdfAnnotation annotation = new PdfTextAnnotation(new Rectangle(50, 10));
                annotation.setLayer(new PdfLayer("name", fromDocument));
                page.addAnnotation(annotation);
                annotation.flush();

                Set<PdfLayer> layersFromPage = page.getPdfLayers();
                Assertions.assertEquals(1, layersFromPage.size());
            }
        }
    }

    @Test
    public void testReadOcgFromApAnnotation() throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = fromDocument.addNewPage();

                PdfAnnotation annotation = new PdfTextAnnotation(new Rectangle(50, 10));

                PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(50, 10));
                formXObject.setLayer(new PdfLayer("someName1", fromDocument));
                formXObject.makeIndirect(fromDocument);
                PdfDictionary nDict = new PdfDictionary();
                nDict.put(PdfName.ON, formXObject.getPdfObject());
                annotation.setAppearance(PdfName.N, nDict);

                formXObject = new PdfFormXObject(new Rectangle(50, 10));
                formXObject.setLayer(new PdfLayer("someName2", fromDocument));
                PdfResources formResources = formXObject.getResources();
                formResources.addProperties(new PdfLayer("someName3", fromDocument).getPdfObject());
                formXObject.makeIndirect(fromDocument);
                PdfDictionary rDict = new PdfDictionary();
                rDict.put(PdfName.OFF, formXObject.getPdfObject());
                annotation.setAppearance(PdfName.R, rDict);

                formXObject = new PdfFormXObject(new Rectangle(50, 10));
                formXObject.setLayer(new PdfLayer("someName4", fromDocument));
                formXObject.makeIndirect(fromDocument);
                annotation.setAppearance(PdfName.D, formXObject.getPdfObject());

                page.addAnnotation(annotation);

                Set<PdfLayer> layersFromPage = page.getPdfLayers();
                Assertions.assertEquals(4, layersFromPage.size());
            }
        }
    }

    @Test
    public void nestedLayerTwoParentsTest() throws IOException {
        String outPdf = destinationFolder + "nestedLayerTwoParents.pdf";
        String cmpPdf = sourceFolder + "cmp_nestedLayerTwoParents.pdf";
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outPdf));

        PdfFont font = PdfFontFactory.createFont();

        PdfLayer parentLayer1 = new PdfLayer("Parent layer 1", pdfDoc);
        PdfLayer parentLayer2 = new PdfLayer("Parent layer 2", pdfDoc);
        PdfLayer nestedLayer = new PdfLayer("Nested layer 1", pdfDoc);

        parentLayer1.addChild(nestedLayer);
        parentLayer2.addChild(nestedLayer);
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.setFontAndSize(font, 12);

        PdfLayerTestUtils.addTextInsideLayer(parentLayer1, canvas, "Parent layer 1 text", 50, 750);
        PdfLayerTestUtils.addTextInsideLayer(parentLayer2, canvas, "Parent layer 2 text", 50, 700);
        PdfLayerTestUtils.addTextInsideLayer(nestedLayer, canvas, "Nested layer 1 text", 100, 650);
        canvas.release();
        pdfDoc.close();

        PdfLayerTestUtils.compareLayers(outPdf, cmpPdf);
    }

    @Test
    public void nestedLayerTwoParentsWithOneParentTest() throws IOException {
        String outPdf = destinationFolder + "nestedLayerTwoParentsWithOneParent.pdf";
        String cmpPdf = sourceFolder + "cmp_nestedLayerTwoParentsWithOneParent.pdf";
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outPdf));

        PdfFont font = PdfFontFactory.createFont();

        PdfLayer parentLayer = new PdfLayer("Parent layer", pdfDoc);
        PdfLayer layer1 = new PdfLayer("Layer 1", pdfDoc);
        PdfLayer layer2 = new PdfLayer("Layer 2", pdfDoc);
        PdfLayer nestedLayer = new PdfLayer("Nested layer 1", pdfDoc);

        layer1.addChild(nestedLayer);
        layer2.addChild(nestedLayer);
        parentLayer.addChild(layer1);
        parentLayer.addChild(layer2);
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.setFontAndSize(font, 12);

        PdfLayerTestUtils.addTextInsideLayer(parentLayer, canvas, "Parent layer text", 50, 750);
        PdfLayerTestUtils.addTextInsideLayer(layer1, canvas, "layer 1 text", 100, 700);
        PdfLayerTestUtils.addTextInsideLayer(layer2, canvas, "layer 2 text", 100, 650);
        PdfLayerTestUtils.addTextInsideLayer(nestedLayer, canvas, "Nested layer text", 150, 600);
        canvas.release();
        pdfDoc.close();

        PdfLayerTestUtils.compareLayers(outPdf, cmpPdf);
    }

    @Test
    public void duplicatedNestedLayersTest() throws IOException {
        String outPdf = destinationFolder + "duplicatedNestedLayers.pdf";
        String cmpPdf = sourceFolder + "cmp_duplicatedNestedLayers.pdf";
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outPdf));

        PdfFont font = PdfFontFactory.createFont();

        PdfLayer parentLayer = new PdfLayer("Parent layer", pdfDoc);
        PdfLayer nestedLayer1 = new PdfLayer("Nested layer", pdfDoc);

        parentLayer.addChild(nestedLayer1);
        parentLayer.addChild(nestedLayer1);
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.setFontAndSize(font, 12);

        PdfLayerTestUtils.addTextInsideLayer(parentLayer, canvas, "Parent layer text", 50, 750);
        PdfLayerTestUtils.addTextInsideLayer(nestedLayer1, canvas, "Nested layer text", 100, 700);
        canvas.release();
        pdfDoc.close();

        PdfLayerTestUtils.compareLayers(outPdf, cmpPdf);
    }

    @Test
    public void addOcgLayerInAppendModeWhenNoLayersExist() throws IOException {
        // Create a base document without any OCProperties / OCGs.
        ByteArrayOutputStream baseBaos = new ByteArrayOutputStream();
        try (PdfDocument baseDoc = new PdfDocument(new PdfWriter(baseBaos))) {
            baseDoc.addNewPage();
        }

        byte[] baseBytes = baseBaos.toByteArray();
        ByteArrayOutputStream resultBaos = new ByteArrayOutputStream();

        // Append: add a single OCG layer in append mode.
        try (PdfDocument pdfDoc = new PdfDocument(
                new PdfReader(new ByteArrayInputStream(baseBytes)),
                new PdfWriter(resultBaos),
                new StampingProperties().useAppendMode())) {

            PdfFont font = PdfFontFactory.createFont();
            PdfLayer layer = new PdfLayer("AppendLayer", pdfDoc);
            PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
            canvas.setFontAndSize(font, 12);
            PdfLayerTestUtils.addTextInsideLayer(layer, canvas, "Content in appended layer", 50, 700);
        }

        // Assert that the resulting document has exactly one OCG with the expected name.
        try (PdfDocument resultDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(resultBaos.toByteArray())))) {
            PdfOCProperties ocProps = resultDoc.getCatalog().getOCProperties(false);
            // OCProperties must be present after appending a layer
            Assertions.assertNotNull(ocProps);

            List<PdfLayer> layers = ocProps.getLayers();
            // Exactly one layer is expected
            Assertions.assertEquals(1, layers.size());

            PdfLayer layer = layers.get(0);
            Assertions.assertEquals("AppendLayer",
                    layer.getPdfObject().getAsString(PdfName.Name).toUnicodeString());
        }
    }

    @Test
    public void addOcgLayerInAppendModeWhenLayersExist() throws IOException {
        // Phase 1: create base document with a single OCG.
        ByteArrayOutputStream baseBaos = new ByteArrayOutputStream();
        try (PdfDocument baseDoc = new PdfDocument(new PdfWriter(baseBaos))) {
            PdfFont font = PdfFontFactory.createFont();
            PdfCanvas canvas = new PdfCanvas(baseDoc.addNewPage());
            canvas.setFontAndSize(font, 12);

            PdfLayer baseLayer = new PdfLayer("BaseLayer", baseDoc);
            PdfLayerTestUtils.addTextInsideLayer(baseLayer, canvas, "Base layer content", 50, 750);
        }

        byte[] baseBytes = baseBaos.toByteArray();
        ByteArrayOutputStream resultBaos = new ByteArrayOutputStream();

        // Phase 2: append a new OCG to the existing OCProperties.
        try (PdfDocument pdfDoc = new PdfDocument(
                new PdfReader(new ByteArrayInputStream(baseBytes)),
                new PdfWriter(resultBaos),
                new StampingProperties().useAppendMode())) {

            PdfFont font = PdfFontFactory.createFont();
            PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
            canvas.setFontAndSize(font, 12);

            PdfLayer appendedLayer = new PdfLayer("AppendedLayer", pdfDoc);
            PdfLayerTestUtils.addTextInsideLayer(appendedLayer, canvas, "Appended layer content", 50, 700);
        }

        // Assert that both the original and the appended layers are present.
        try (PdfDocument resultDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(resultBaos.toByteArray())))) {
            PdfOCProperties ocProps = resultDoc.getCatalog().getOCProperties(false);
            Assertions.assertNotNull(ocProps);

            List<PdfLayer> layers = ocProps.getLayers();
            // Two layers expected after append
            Assertions.assertEquals(2, layers.size());

            List<String> names = new ArrayList<>();
            for (PdfLayer l : layers) {
                names.add(l.getPdfObject().getAsString(PdfName.Name).toUnicodeString());
            }
            Assertions.assertTrue(names.contains("BaseLayer"));
            Assertions.assertTrue(names.contains("AppendedLayer"));
        }
    }

    @Test
    public void removeOcgLayersInAppendMode() throws IOException {
        // Phase 1: create base document with two OCGs.
        ByteArrayOutputStream baseBaos = new ByteArrayOutputStream();
        try (PdfDocument baseDoc = new PdfDocument(new PdfWriter(baseBaos))) {
            PdfFont font = PdfFontFactory.createFont();
            PdfCanvas canvas = new PdfCanvas(baseDoc.addNewPage());
            canvas.setFontAndSize(font, 12);

            PdfLayer layer1 = new PdfLayer("LayerToRemove1", baseDoc);
            PdfLayer layer2 = new PdfLayer("LayerToRemove2", baseDoc);

            PdfLayerTestUtils.addTextInsideLayer(layer1, canvas, "Layer 1 content", 50, 750);
            PdfLayerTestUtils.addTextInsideLayer(layer2, canvas, "Layer 2 content", 50, 700);
        }

        byte[] baseBytes = baseBaos.toByteArray();
        ByteArrayOutputStream resultBaos = new ByteArrayOutputStream();

        // Phase 2: in append mode, remove all OCGs directly from the OCProperties dictionary.
        try (PdfDocument pdfDoc = new PdfDocument(
                new PdfReader(new ByteArrayInputStream(baseBytes)),
                new PdfWriter(resultBaos),
                new StampingProperties().useAppendMode())) {

            PdfDictionary catalogDict = pdfDoc.getCatalog().getPdfObject();
            PdfDictionary ocPropsDict = catalogDict.getAsDictionary(PdfName.OCProperties);
            // OCProperties must exist in base document
            Assertions.assertNotNull(ocPropsDict);

            PdfArray ocgsArray = ocPropsDict.getAsArray(PdfName.OCGs);
            // OCGs array must exist in base document
            Assertions.assertNotNull(ocgsArray);
            // Base document is expected to have OCGs
            Assertions.assertFalse(ocgsArray.isEmpty());

            //TODO DEVSIX-778: use OCGRemover alternative here
            ocgsArray.clear();
            ocPropsDict.setModified();
        }

        // Phase 3: verify that resulting document has no OCGs registered.
        try (PdfDocument resultDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(resultBaos.toByteArray())))) {
            PdfDictionary catalogDict = resultDoc.getCatalog().getPdfObject();
            PdfDictionary ocPropsDict = catalogDict.getAsDictionary(PdfName.OCProperties);
            // OCProperties dictionary should still exist
            Assertions.assertNotNull(ocPropsDict);

            PdfArray ocgs = ocPropsDict.getAsArray(PdfName.OCGs);
            // OCGs array is expected to be empty after removal in append mode
            Assertions.assertTrue(ocgs == null || ocgs.isEmpty());

            // Additionally assert via PdfOCProperties API that no layers are reported.
            PdfOCProperties ocProps = resultDoc.getCatalog().getOCProperties(false);
            if (ocProps != null) {
                // No layers should be registered after OCG removal
                Assertions.assertTrue(ocProps.getLayers().isEmpty());
            }
        }
    }

    @Test
    public void modifyExistingOcgLayersInAppendMode() throws IOException {
        // Phase 1: create base document with one visible (ON) layer.
        ByteArrayOutputStream baseBaos = new ByteArrayOutputStream();
        try (PdfDocument baseDoc = new PdfDocument(new PdfWriter(baseBaos))) {
            PdfFont font = PdfFontFactory.createFont();
            PdfCanvas canvas = new PdfCanvas(baseDoc.addNewPage());
            canvas.setFontAndSize(font, 12);

            PdfLayer layer = new PdfLayer("ModifiableLayer", baseDoc);
            layer.setOn(true);
            PdfLayerTestUtils.addTextInsideLayer(layer, canvas, "Initially visible layer", 50, 750);
        }

        byte[] baseBytes = baseBaos.toByteArray();
        ByteArrayOutputStream resultBaos = new ByteArrayOutputStream();

        // Phase 2: in append mode, modify the existing layer (e.g. turn it OFF and lock it).
        try (PdfDocument pdfDoc = new PdfDocument(
                new PdfReader(new ByteArrayInputStream(baseBytes)),
                new PdfWriter(resultBaos),
                new StampingProperties().useAppendMode())) {

            PdfOCProperties ocProps = pdfDoc.getCatalog().getOCProperties(false);
            Assertions.assertNotNull(ocProps);

            List<PdfLayer> layers = ocProps.getLayers();
            // Single layer expected in base document
            Assertions.assertEquals(1, layers.size());

            PdfLayer layer = layers.get(0);
            Assertions.assertEquals("ModifiableLayer",
                    layer.getPdfObject().getAsString(PdfName.Name).toUnicodeString());

            // Modify: make the layer OFF and locked.
            layer.setOn(false);
            layer.setLocked(true);
        }

        // Phase 3: verify modification persisted in resulting document.
        try (PdfDocument resultDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(resultBaos.toByteArray())))) {
            PdfOCProperties ocProps = resultDoc.getCatalog().getOCProperties(false);
            Assertions.assertNotNull(ocProps);

            List<PdfLayer> layers = ocProps.getLayers();
            Assertions.assertEquals(1, layers.size());

            PdfLayer layer = layers.get(0);
            Assertions.assertEquals("ModifiableLayer",
                    layer.getPdfObject().getAsString(PdfName.Name).toUnicodeString());
            // Layer should be OFF after modification in append mode
            Assertions.assertFalse(layer.isOn());
            // Layer should be locked after modification in append mode
            Assertions.assertTrue(layer.isLocked());
        }
    }

}
