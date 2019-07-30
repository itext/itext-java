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
package com.itextpdf.kernel.pdf.layer;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfLayerTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/layer/PdfLayerTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/layer/PdfLayerTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void layerDefaultIntents() {
        PdfLayer pdfLayer = PdfLayerTestUtils.prepareNewLayer();

        Collection<PdfName> defaultIntents = pdfLayer.getIntents();

        Assert.assertArrayEquals(new PdfName[] {PdfName.View}, defaultIntents.toArray(new PdfName[1]));
    }

    @Test
    public void layerSetSingleIntent() {
        PdfLayer pdfLayer = PdfLayerTestUtils.prepareLayerDesignIntent();

        Collection<PdfName> defaultIntents = pdfLayer.getIntents();

        Assert.assertArrayEquals(new PdfName[] {PdfName.Design}, defaultIntents.toArray(new PdfName[1]));
    }

    @Test
    public void layerSetSeveralIntents() {
        PdfName custom = new PdfName("Custom");
        PdfLayer pdfLayer = PdfLayerTestUtils.prepareLayerDesignAndCustomIntent(custom);

        Collection<PdfName> defaultIntents = pdfLayer.getIntents();

        Assert.assertArrayEquals(new PdfName[] {PdfName.Design, custom}, defaultIntents.toArray(new PdfName[2]));
    }

    @Test
    public void layerSetIntentsNull() {
        PdfName custom = new PdfName("Custom");
        PdfLayer pdfLayer = PdfLayerTestUtils.prepareLayerDesignAndCustomIntent(custom);

        pdfLayer.setIntents(null);
        Collection<PdfName> postNullIntents = pdfLayer.getIntents();

        Assert.assertArrayEquals(new PdfName[] {PdfName.View}, postNullIntents.toArray(new PdfName[1]));
    }

    @Test
    public void layerSetIntentsEmpty() {
        PdfName custom = new PdfName("Custom");
        PdfLayer pdfLayer = PdfLayerTestUtils.prepareLayerDesignAndCustomIntent(custom);

        pdfLayer.setIntents(Collections.<PdfName>emptyList());
        Collection<PdfName> postNullIntents = pdfLayer.getIntents();

        Assert.assertArrayEquals(new PdfName[] {PdfName.View}, postNullIntents.toArray(new PdfName[1]));
    }


    @Test
    public void nestedLayers() throws IOException {
        String outPdf = destinationFolder + "nestedLayers.pdf";
        String cmpPdf = sourceFolder + "cmp_nestedLayers.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));

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
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));

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
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));

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
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));

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
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));

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
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));

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
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf));

        // init OCProperties to check how they are processed
        pdfDoc.getCatalog().getOCProperties(true);

        pdfDoc.close();

        // start of test assertion logic
        PdfDocument resPdf = new PdfDocument(new PdfReader(outPdf));
        PdfDictionary d = resPdf.getCatalog().getPdfObject().getAsDictionary(PdfName.OCProperties).getAsDictionary(PdfName.D);
        Assert.assertEquals(PdfOCProperties.OC_CONFIG_NAME_PATTERN + "2", d.getAsString(PdfName.Name).toUnicodeString());

        PdfLayerTestUtils.compareLayers(outPdf, cmpPdf);
    }

    @Test
    public void processTitledHierarchies() throws IOException {
        String srcPdf = sourceFolder + "titledHierarchies.pdf";
        String outPdf = destinationFolder + "processTitledHierarchies.pdf";
        String cmpPdf = sourceFolder + "cmp_processTitledHierarchies.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf));

        // init OCProperties to check how they are processed
        pdfDoc.getCatalog().getOCProperties(true);

        pdfDoc.close();

        PdfLayerTestUtils.compareLayers(outPdf, cmpPdf);
    }

    @Test
    public void setCreatorInfoAndLanguage() throws IOException {
        String outPdf = destinationFolder + "setCreatorInfoAndLanguage.pdf";
        String cmpPdf = sourceFolder + "cmp_setCreatorInfoAndLanguage.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));

        PdfFont font = PdfFontFactory.createFont();
        PdfLayer layer = new PdfLayer("CreatorAndLanguageInfo", pdfDoc);
        layer.setCreatorInfo("iText", "Technical");
        layer.setLanguage("en-AU", true); // australian english

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
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));

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
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));

        boolean view = true;
        createCustomExportLayers(pdfDoc, view);

        PdfLayerTestUtils.compareLayers(outPdf, cmpPdf);
    }

    @Test
    public void setExportViewIsFalse() throws IOException {
        String outPdf = destinationFolder + "setExportViewIsFalse.pdf";
        String cmpPdf = sourceFolder + "cmp_setExportViewIsFalse.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));

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
                new PdfWriter(destinationFolder + "output_copy_layered.pdf"));
        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "output_copy_layered.pdf", sourceFolder + "input_layered.pdf", destinationFolder, "diff"));
    }

    @Test
    public void testInStamperMode2() throws IOException, InterruptedException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "input_layered.pdf"),
                new PdfWriter(destinationFolder + "output_layered.pdf"));

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
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "output_layered.pdf", sourceFolder + "cmp_output_layered.pdf", destinationFolder, "diff"));
    }

}
