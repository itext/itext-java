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

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.kernel.pdf.layer.PdfLayer;
import com.itextpdf.kernel.pdf.layer.PdfLayerMembership;
import com.itextpdf.kernel.pdf.layer.PdfOCProperties;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class OcgPropertiesCopierTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/OcgPropertiesCopierTest/";

    @Test
    public void copySamePageTwiceTest() throws IOException {
        byte[] fromDocBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = fromDocument.addNewPage();

                PdfResources pdfResource = page.getResources();
                pdfResource.addProperties(new PdfLayer("name", fromDocument).getPdfObject());
                pdfResource.makeIndirect(fromDocument);

                fromDocument.getCatalog().getOCProperties(true);
            }
            fromDocBytes = outputStream.toByteArray();
        }

        List<String> names = new ArrayList<>();
        names.add("name");
        try (PdfDocument toDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(fromDocBytes)))) {
                fromDocument.copyPagesTo(1, 1, toDocument);
                fromDocument.copyPagesTo(1, 1, toDocument);

                OcgPropertiesCopierTest.checkLayersNameInToDocument(toDocument, names);
            }
        }
    }

    @Test
    @LogMessages(
            messages = @LogMessage(messageTemplate = IoLogMessageConstant.OCG_COPYING_ERROR, logLevel = LogLevelConstants.ERROR)
    )
    public void attemptToCopyInvalidOCGTest() throws IOException {
        byte[] fromDocBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = fromDocument.addNewPage();

                PdfResources pdfResource = page.getResources();
                PdfDictionary layer = new PdfLayer("name1", fromDocument).getPdfObject();
                layer.remove(PdfName.Name);
                pdfResource.addProperties(layer);
                pdfResource.makeIndirect(fromDocument);

                fromDocument.getCatalog().getOCProperties(true);
            }
            fromDocBytes = outputStream.toByteArray();
        }

        List<String> names = new ArrayList<>();
        OcgPropertiesCopierTest.copyPagesAndAssertLayersName(names, fromDocBytes);
    }

    @Test
    public void copyDifferentPageWithSameOcgTest() throws IOException {
        byte[] fromDocBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfDictionary layer = new PdfLayer("name", fromDocument).getPdfObject();

                for (int i = 0; i < 5; i++) {
                    PdfPage page = fromDocument.addNewPage();
                    PdfResources pdfResource = page.getResources();
                    pdfResource.addProperties(layer);
                    pdfResource.makeIndirect(fromDocument);
                }

                fromDocument.getCatalog().getOCProperties(true);
            }
            fromDocBytes = outputStream.toByteArray();
        }

        List<String> names = new ArrayList<>();
        names.add("name");
        try (PdfDocument toDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(fromDocBytes)))) {
                fromDocument.copyPagesTo(1, 2, toDocument);
                fromDocument.copyPagesTo(3, 5, toDocument);

                // The test verifies that identical layers on different pages are copied exactly once
                OcgPropertiesCopierTest.checkLayersNameInToDocument(toDocument, names);
            }
        }
    }

    @Test
    public void copyOcgOnlyFromCopiedPagesTest() throws IOException {
        byte[] fromDocBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = fromDocument.addNewPage();

                PdfResources pdfResource = page.getResources();
                pdfResource.addProperties(new PdfLayer("name1", fromDocument).getPdfObject());
                pdfResource.makeIndirect(fromDocument);

                page = fromDocument.addNewPage();

                pdfResource = page.getResources();
                pdfResource.addProperties(new PdfLayer("name2", fromDocument).getPdfObject());
                pdfResource.makeIndirect(fromDocument);

                fromDocument.getCatalog().getOCProperties(true);
            }
            fromDocBytes = outputStream.toByteArray();
        }

        List<String> names = new ArrayList<>();
        names.add("name1");
        OcgPropertiesCopierTest.copyPagesAndAssertLayersName(names, fromDocBytes);
    }

    @Test
    public void copyOcgWithEmptyOCGsInOCPropertiesTest() throws IOException {
        byte[] fromDocBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = fromDocument.addNewPage();

                PdfResources pdfResource = page.getResources();
                PdfDictionary ocg = new PdfDictionary();
                ocg.put(PdfName.Type, PdfName.OCG);
                ocg.put(PdfName.Name, new PdfString("name"));
                ocg.makeIndirect(fromDocument);
                pdfResource.addProperties(ocg);
                pdfResource.makeIndirect(fromDocument);

                fromDocument.getCatalog().getOCProperties(true);
            }
            fromDocBytes = outputStream.toByteArray();
        }

        List<String> names = new ArrayList<>();
        names.add("name");
        try (PdfDocument toDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(fromDocBytes)))) {
                // This test verifies that if the PDF is invalid, i.e. if OCProperties.OCGs is empty in the document,
                // but there are OCGs that are used on the page, then OCGs will be copied
                Assertions.assertTrue(fromDocument.getCatalog().getPdfObject().getAsDictionary(PdfName.OCProperties).getAsArray(PdfName.OCGs).isEmpty());

                fromDocument.copyPagesTo(1, 1, toDocument);

                OcgPropertiesCopierTest.checkLayersNameInToDocument(toDocument, names);
            }
        }
    }

    @Test
    public void notCopyConfigsTest() throws IOException {
        byte[] fromDocBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = fromDocument.addNewPage();

                PdfResources pdfResource = page.getResources();
                pdfResource.addProperties(new PdfLayer("name1", fromDocument).getPdfObject());
                pdfResource.makeIndirect(fromDocument);
                PdfObject ocg = new PdfLayer("name2", fromDocument).getPdfObject();

                fromDocument.getCatalog().getOCProperties(true);
                PdfDictionary ocProperties = fromDocument.getCatalog().getOCProperties(false).getPdfObject();
                PdfDictionary config = new PdfDictionary();
                config.put(PdfName.Name, new PdfString("configName", PdfEncodings.UNICODE_BIG));
                PdfArray ocgs = new PdfArray();
                ocgs.add(ocg);
                config.put(PdfName.OCGs, ocgs);
                ocProperties.put(PdfName.Configs, new PdfArray(config));
            }
            fromDocBytes = outputStream.toByteArray();
        }

        List<String> names = new ArrayList<>();
        names.add("name1");
        try (PdfDocument toDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(fromDocBytes)))) {
                fromDocument.copyPagesTo(1, 1, toDocument);

                OcgPropertiesCopierTest.checkLayersNameInToDocument(toDocument, names);

                PdfOCProperties ocProperties = toDocument.getCatalog().getOCProperties(false);
                // Check that the Configs field has not been copied
                Assertions.assertFalse(ocProperties.getPdfObject().containsKey(PdfName.Configs));
            }
        }
    }

    @Test
    @LogMessages(
            messages = @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_HAS_CONFLICTING_OCG_NAMES)
    )
    public void copyOCGsWithConflictNamesTest() throws IOException {
        byte[] fromDocBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = fromDocument.addNewPage();
                PdfResources pdfResource = page.getResources();

                PdfLayer layer1 = new PdfLayer("Layer1", fromDocument);
                pdfResource.addProperties(layer1.getPdfObject());

                PdfLayer layer2 = new PdfLayer("Layer1_2", fromDocument);
                pdfResource.addProperties(layer2.getPdfObject());

                new PdfLayer("Layer1_3", fromDocument);
            }
            fromDocBytes = outputStream.toByteArray();
        }

        byte[] toDocBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument toDocument = new PdfDocument(new PdfWriter(outputStream))) {
                toDocument.addNewPage();
                new PdfLayer("Layer1", toDocument);
                new PdfLayer("Layer1_0", toDocument);
                new PdfLayer("Layer1_1", toDocument);
            }
            toDocBytes = outputStream.toByteArray();
        }

        List<String> names = new ArrayList<>();
        names.add("Layer1");
        names.add("Layer1_0");
        names.add("Layer1_1");

        names.add("Layer1_2");
        // NOTE: Two layers with the same name in the output document after the merge, due
        // to the fact that we do not check names for conflicts in the original documents
        names.add("Layer1_2");

        OcgPropertiesCopierTest.copyPagesAndAssertLayersName(names, fromDocBytes, toDocBytes);
    }

    // Copying different fields from the dictionary D test block

    @Test
    public void copySameRBGroupFromDifferentPages() throws IOException {
        byte[] fromDocBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfLayer radio1 = new PdfLayer("Radio1", fromDocument);
                PdfPage page = fromDocument.addNewPage();
                PdfResources pdfResource = page.getResources();
                pdfResource.addProperties(radio1.getPdfObject());
                pdfResource.makeIndirect(fromDocument);

                PdfLayer radio2 = new PdfLayer("Radio2", fromDocument);
                page = fromDocument.addNewPage();
                pdfResource = page.getResources();
                pdfResource.addProperties(radio2.getPdfObject());
                pdfResource.makeIndirect(fromDocument);

                PdfLayer radio3 = new PdfLayer("Radio3", fromDocument);
                page = fromDocument.addNewPage();
                pdfResource = page.getResources();
                pdfResource.addProperties(radio3.getPdfObject());
                pdfResource.makeIndirect(fromDocument);

                // Should be removed
                PdfLayer radio4 = new PdfLayer("Radio4", fromDocument);

                List<PdfLayer> options = new ArrayList<>();
                options.add(radio1);
                options.add(radio2);
                options.add(radio3);
                options.add(radio4);
                PdfLayer.addOCGRadioGroup(fromDocument, options);


                page = fromDocument.addNewPage();
                pdfResource = page.getResources();
                pdfResource.addProperties(radio3.getPdfObject());
                pdfResource.makeIndirect(fromDocument);

                PdfLayer radio5 = new PdfLayer("Radio5", fromDocument);
                options = new ArrayList<>();
                options.add(radio3);
                options.add(radio4);
                options.add(radio5);
                PdfLayer.addOCGRadioGroup(fromDocument, options);
            }
            fromDocBytes = outputStream.toByteArray();
        }

        Set<String> namesOrTitles = new HashSet<>();
        namesOrTitles.add("Radio1");
        namesOrTitles.add("Radio2");
        namesOrTitles.add("Radio3");


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument toDocument = new PdfDocument(new PdfWriter(outputStream));
        PdfDocument fromDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(fromDocBytes)));
        fromDocument.copyPagesTo(1, 1, toDocument);
        fromDocument.copyPagesTo(2, 2, toDocument);
        fromDocument.copyPagesTo(3, 3, toDocument);


        OcgPropertiesCopierTest.checkLayersOrTitleNameInToDocument(toDocument, namesOrTitles);
        PdfOCProperties ocProperties = toDocument.getCatalog().getOCProperties(false);
        ocProperties.fillDictionary();
        toDocument.close();
        byte[] toDocOutputBytes = outputStream.toByteArray();
        outputStream.close();

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(toDocOutputBytes)), new PdfWriter(new ByteArrayOutputStream()));
        ocProperties = pdfDoc.getCatalog().getOCProperties(true);
        PdfDictionary dDict =  ocProperties.getPdfObject().getAsDictionary(PdfName.D);


        PdfArray rbGroups = dDict.getAsArray(PdfName.RBGroups);
        Assertions.assertEquals(2, rbGroups.size());

        Assertions.assertEquals(3, rbGroups.getAsArray(0).size());
        Assertions.assertEquals("Radio1", rbGroups.getAsArray(0).getAsDictionary(0).getAsString(PdfName.Name).toUnicodeString());
        Assertions.assertEquals("Radio2", rbGroups.getAsArray(0).getAsDictionary(1).getAsString(PdfName.Name).toUnicodeString());
        Assertions.assertEquals("Radio3", rbGroups.getAsArray(0).getAsDictionary(2).getAsString(PdfName.Name).toUnicodeString());

        Assertions.assertEquals(1, rbGroups.getAsArray(1).size());
        Assertions.assertEquals("Radio3", rbGroups.getAsArray(1).getAsDictionary(0).getAsString(PdfName.Name).toUnicodeString());
    }

    @Test
    public void copySameOrderGroupFromDifferentPages() throws IOException {
        byte[] fromDocBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfLayer parent1 = new PdfLayer("parent1", fromDocument);
                PdfPage page = fromDocument.addNewPage();
                PdfResources pdfResource = page.getResources();
                pdfResource.addProperties(parent1.getPdfObject());
                pdfResource.makeIndirect(fromDocument);

                PdfLayer child1 = new PdfLayer("child1", fromDocument);
                page = fromDocument.addNewPage();
                pdfResource = page.getResources();
                pdfResource.addProperties(child1.getPdfObject());
                pdfResource.makeIndirect(fromDocument);

                PdfLayer child2 = new PdfLayer("child2", fromDocument);
                page = fromDocument.addNewPage();
                pdfResource = page.getResources();
                pdfResource.addProperties(child2.getPdfObject());
                pdfResource.makeIndirect(fromDocument);

                // Should be removed
                PdfLayer child3 = new PdfLayer("child3", fromDocument);

                parent1.addChild(child1);
                parent1.addChild(child2);
                parent1.addChild(child3);


                // Parent used
                PdfLayer parent2 = new PdfLayer("parent2", fromDocument);
                page = fromDocument.addNewPage();
                pdfResource = page.getResources();
                pdfResource.addProperties(parent2.getPdfObject());
                pdfResource.makeIndirect(fromDocument);
                PdfLayer child4 = new PdfLayer("child4", fromDocument);
                parent2.addChild(child4);

                // Child used
                PdfLayer child5 = new PdfLayer("child5", fromDocument);
                page = fromDocument.addNewPage();
                pdfResource = page.getResources();
                pdfResource.addProperties(child5.getPdfObject());
                pdfResource.makeIndirect(fromDocument);
                PdfLayer parent3 = new PdfLayer("parent3", fromDocument);
                parent3.addChild(child5);

                PdfLayer parent4 = PdfLayer.createTitle("parent4", fromDocument);
                PdfLayer child6 = new PdfLayer("child6", fromDocument);
                page = fromDocument.addNewPage();
                pdfResource = page.getResources();
                pdfResource.addProperties(child6.getPdfObject());
                pdfResource.makeIndirect(fromDocument);
                parent4.addChild(child6);

                PdfLayer parent5 = PdfLayer.createTitle("parent5", fromDocument);
                PdfLayer child7 = new PdfLayer("child7", fromDocument);
                parent5.addChild(child7);

                // Child used
                PdfLayer grandpa1 = new PdfLayer("grandpa1", fromDocument);
                PdfLayer parent6 = new PdfLayer("parent6", fromDocument);
                grandpa1.addChild(parent6);
                PdfLayer child8 = new PdfLayer("child8", fromDocument);
                parent6.addChild(child8);
                page = fromDocument.addNewPage();
                pdfResource = page.getResources();
                pdfResource.addProperties(child8.getPdfObject());
                pdfResource.makeIndirect(fromDocument);
                PdfLayer child9 = new PdfLayer("child9", fromDocument);
                parent6.addChild(child9);
                grandpa1.addChild(new PdfLayer("parent7", fromDocument));
            }
            fromDocBytes = outputStream.toByteArray();
        }

        Set<String> namesOrTitles = new HashSet<>();
        namesOrTitles.add("parent1");
        namesOrTitles.add("child1");
        namesOrTitles.add("child2");
        namesOrTitles.add("parent2");
        namesOrTitles.add("parent3");
        namesOrTitles.add("child5");
        namesOrTitles.add("parent4");
        namesOrTitles.add("child6");
        namesOrTitles.add("grandpa1");
        namesOrTitles.add("parent6");
        namesOrTitles.add("child8");


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument toDocument = new PdfDocument(new PdfWriter(outputStream));
        PdfDocument fromDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(fromDocBytes)));
        for (int i = 1; i <= fromDocument.getNumberOfPages(); i++) {
            fromDocument.copyPagesTo(i, i, toDocument);
        }


        OcgPropertiesCopierTest.checkLayersOrTitleNameInToDocument(toDocument, namesOrTitles);
        PdfOCProperties ocProperties = toDocument.getCatalog().getOCProperties(false);
        ocProperties.fillDictionary();
        toDocument.close();
        byte[] toDocOutputBytes = outputStream.toByteArray();
        outputStream.close();

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(toDocOutputBytes)), new PdfWriter(new ByteArrayOutputStream()));
        ocProperties = pdfDoc.getCatalog().getOCProperties(true);
        PdfDictionary dDict =  ocProperties.getPdfObject().getAsDictionary(PdfName.D);


        PdfArray order = dDict.getAsArray(PdfName.Order);
        Assertions.assertEquals(8, order.size());

        Assertions.assertEquals("parent1", order.getAsDictionary(0).getAsString(PdfName.Name).toUnicodeString());

        Assertions.assertEquals(2, order.getAsArray(1).size());
        Assertions.assertEquals("child1", order.getAsArray(1).getAsDictionary(0).getAsString(PdfName.Name).toUnicodeString());
        Assertions.assertEquals("child2", order.getAsArray(1).getAsDictionary(1).getAsString(PdfName.Name).toUnicodeString());

        Assertions.assertEquals("parent2", order.getAsDictionary(2).getAsString(PdfName.Name).toUnicodeString());

        Assertions.assertEquals("parent3", order.getAsDictionary(3).getAsString(PdfName.Name).toUnicodeString());

        Assertions.assertEquals(1, order.getAsArray(4).size());
        Assertions.assertEquals("child5", order.getAsArray(4).getAsDictionary(0).getAsString(PdfName.Name).toUnicodeString());

        Assertions.assertEquals(2, order.getAsArray(5).size());
        Assertions.assertEquals("parent4", order.getAsArray(5).getAsString(0).toUnicodeString());
        Assertions.assertEquals("child6", order.getAsArray(5).getAsDictionary(1).getAsString(PdfName.Name).toUnicodeString());

        Assertions.assertEquals("grandpa1", order.getAsDictionary(6).getAsString(PdfName.Name).toUnicodeString());

        Assertions.assertEquals(2, order.getAsArray(7).size());
        Assertions.assertEquals("parent6", order.getAsArray(7).getAsDictionary(0).getAsString(PdfName.Name).toUnicodeString());
        Assertions.assertEquals(1, order.getAsArray(7).getAsArray(1).size());
        Assertions.assertEquals("child8", order.getAsArray(7).getAsArray(1).getAsDictionary(0).getAsString(PdfName.Name).toUnicodeString());
    }

    @Test
    public void copyOrderToEmptyDocumentTest() throws IOException {
        byte[] fromDocBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = fromDocument.addNewPage();
                PdfResources pdfResource = page.getResources();

                // One layer which used in resources
                PdfLayer layer1 = new PdfLayer("Layer1", fromDocument);
                pdfResource.addProperties(layer1.getPdfObject());

                // One layer which not used in resources (will be ignored)
                new PdfLayer("Layer2", fromDocument);

                // Unused title with used children (level 1 in layers hierarchy)
                PdfLayer grandpa1 = PdfLayer.createTitle("Grandpa1", fromDocument);

                // Unused title with used children (level 2 in layers hierarchy)
                PdfLayer parent1 = PdfLayer.createTitle("Parent1", fromDocument);
                parent1.addChild(new PdfLayer("Child1", fromDocument));
                PdfLayer child2 = new PdfLayer("Child2", fromDocument);
                pdfResource.addProperties(child2.getPdfObject());
                parent1.addChild(child2);

                grandpa1.addChild(parent1);
                grandpa1.addChild(new PdfLayer("Child3", fromDocument));
                PdfLayer child4 = new PdfLayer("Child4", fromDocument);
                pdfResource.addProperties(child4.getPdfObject());
                grandpa1.addChild(child4);

                // Unused layer with used children
                PdfLayer parent2 = new PdfLayer("Parent2", fromDocument);
                PdfLayer child5 = new PdfLayer("Child5", fromDocument);
                parent2.addChild(child5);
                pdfResource.addProperties(child5.getPdfObject());
                parent2.addChild(new PdfLayer("Child6", fromDocument));

                // Unused title with unused children (will be ignored)
                PdfLayer parent3 = PdfLayer.createTitle("Parent3", fromDocument);
                parent3.addChild(new PdfLayer("Child7", fromDocument));
                parent3.addChild(new PdfLayer("Child8", fromDocument));

                // Unused layer with unused children (will be ignored)
                PdfLayer parent4 = new PdfLayer("Parent4", fromDocument);
                parent4.addChild(new PdfLayer("Child9", fromDocument));
                parent4.addChild(new PdfLayer("Child10", fromDocument));

                pdfResource.makeIndirect(fromDocument);
                fromDocument.getCatalog().getOCProperties(true);
            }
            fromDocBytes = outputStream.toByteArray();
        }

        Set<String> namesOrTitles = new HashSet<>();
        namesOrTitles.add("Layer1");
        namesOrTitles.add("Grandpa1");
        namesOrTitles.add("Parent1");
        namesOrTitles.add("Child2");
        namesOrTitles.add("Child4");
        namesOrTitles.add("Parent2");
        namesOrTitles.add("Child5");
        PdfArray order = OcgPropertiesCopierTest.copyPagesAndAssertLayersNameAndGetDDict(namesOrTitles, fromDocBytes, null).getAsArray(PdfName.Order);
        Assertions.assertEquals(4, order.size());
        Assertions.assertEquals("Layer1", order.getAsDictionary(0).getAsString(PdfName.Name).toUnicodeString());

        PdfArray subArray = order.getAsArray(1);
        Assertions.assertEquals(3, subArray.size());
        Assertions.assertEquals("Grandpa1", subArray.getAsString(0).toUnicodeString());

        Assertions.assertEquals(2, subArray.getAsArray(1).size());
        Assertions.assertEquals("Parent1", subArray.getAsArray(1).getAsString(0).toUnicodeString());
        Assertions.assertEquals("Child2", subArray.getAsArray(1).getAsDictionary(1).getAsString(PdfName.Name).toUnicodeString());

        Assertions.assertEquals("Child4", subArray.getAsDictionary(2).getAsString(PdfName.Name).toUnicodeString());


        Assertions.assertEquals("Parent2", order.getAsDictionary(2).getAsString(PdfName.Name).toUnicodeString());

        Assertions.assertEquals(1, order.getAsArray(3).size());
        Assertions.assertEquals("Child5", order.getAsArray(3).getAsDictionary(0).getAsString(PdfName.Name).toUnicodeString());
    }

    @Test
    public void copyDFieldsToEmptyDocumentTest() throws IOException {
        byte[] fromDocBytes = OcgPropertiesCopierTest.getDocumentWithAllDFields();

        Set<String> namesOrTitles = new HashSet<>();
        namesOrTitles.add("Parent1");
        namesOrTitles.add("Child1");
        namesOrTitles.add("Locked1");
        namesOrTitles.add("Radio1");
        namesOrTitles.add("Radio3");
        namesOrTitles.add("Radio4");
        namesOrTitles.add("On1");
        namesOrTitles.add("Off1");
        namesOrTitles.add("noPrint1");

        PdfDictionary dDict = OcgPropertiesCopierTest.copyPagesAndAssertLayersNameAndGetDDict(namesOrTitles, fromDocBytes, null);

        PdfArray locked = dDict.getAsArray(PdfName.Locked);
        Assertions.assertEquals(1, locked.size());
        Assertions.assertEquals("Locked1", locked.getAsDictionary(0).getAsString(PdfName.Name).toUnicodeString());

        PdfArray rbGroups = dDict.getAsArray(PdfName.RBGroups);
        Assertions.assertEquals(2, rbGroups.size());
        Assertions.assertEquals(2, rbGroups.getAsArray(0).size());
        Assertions.assertEquals("Radio1", rbGroups.getAsArray(0).getAsDictionary(0).getAsString(PdfName.Name).toUnicodeString());
        Assertions.assertEquals("Radio3", rbGroups.getAsArray(0).getAsDictionary(1).getAsString(PdfName.Name).toUnicodeString());
        Assertions.assertEquals(1, rbGroups.getAsArray(1).size());
        Assertions.assertEquals("Radio4", rbGroups.getAsArray(1).getAsDictionary(0).getAsString(PdfName.Name).toUnicodeString());

        Assertions.assertNull(dDict.getAsArray(PdfName.ON));

        PdfArray off = dDict.getAsArray(PdfName.OFF);
        Assertions.assertEquals(1, off.size());
        Assertions.assertEquals("Off1", off.getAsDictionary(0).getAsString(PdfName.Name).toUnicodeString());

        Assertions.assertNull(dDict.getAsArray(PdfName.Creator));

        Assertions.assertEquals("Name", dDict.getAsString(PdfName.Name).toUnicodeString());

        Assertions.assertEquals(PdfName.ON, dDict.getAsName(PdfName.BaseState));

        PdfArray asArray = dDict.getAsArray(PdfName.AS);
        Assertions.assertEquals(1, asArray.size());
        Assertions.assertEquals(1, asArray.getAsDictionary(0).getAsArray(PdfName.Category).size());
        Assertions.assertEquals(PdfName.Print, asArray.getAsDictionary(0).getAsArray(PdfName.Category).getAsName(0));
        Assertions.assertEquals("noPrint1", asArray.getAsDictionary(0).getAsArray(PdfName.OCGs).getAsDictionary(0).getAsString(PdfName.Name).toUnicodeString());

        Assertions.assertEquals(PdfName.View, dDict.getAsName(PdfName.Intent));

        Assertions.assertEquals(PdfName.VisiblePages, dDict.getAsName(PdfName.ListMode));
    }

    @Test
    public void copyDFieldsToDocumentWithDDictTest() throws IOException {
        byte[] fromDocBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = fromDocument.addNewPage();
                PdfResources pdfResource = page.getResources();

                // Order
                PdfLayer parent1 = new PdfLayer("from_Parent1", fromDocument);
                PdfLayer child1 = new PdfLayer("from_Child1", fromDocument);
                pdfResource.addProperties(child1.getPdfObject());
                parent1.addChild(child1);

                // Locked
                PdfLayer locked1 = new PdfLayer("from_Locked1", fromDocument);
                locked1.setLocked(true);
                pdfResource.addProperties(locked1.getPdfObject());

                // RBGroups
                PdfLayer radio1 = new PdfLayer("from_Radio1", fromDocument);
                pdfResource.addProperties(radio1.getPdfObject());
                List<PdfLayer> options = new ArrayList<>();
                options.add(radio1);
                PdfLayer.addOCGRadioGroup(fromDocument, options);

                // ON
                PdfLayer on1 = new PdfLayer("from_On1", fromDocument);
                on1.setOn(true);
                pdfResource.addProperties(on1.getPdfObject());

                // OFF
                PdfLayer off1 = new PdfLayer("from_Off1", fromDocument);
                off1.setOn(false);
                pdfResource.addProperties(off1.getPdfObject());

                pdfResource.makeIndirect(fromDocument);
                PdfOCProperties ocProperties = fromDocument.getCatalog().getOCProperties(true);
                // Creator (will be deleted and not copied)
                ocProperties.getPdfObject().put(PdfName.Creator, new PdfString("from_CreatorName", PdfEncodings.UNICODE_BIG));
                // Name (will be automatically changed)
                ocProperties.getPdfObject().put(PdfName.Name, new PdfString("from_Name", PdfEncodings.UNICODE_BIG));
                // BaseState (will be not copied)
                ocProperties.getPdfObject().put(PdfName.BaseState, new PdfName("Unchanged"));
                // AS (will be automatically changed)
                ocProperties.getPdfObject().put(PdfName.AS, new PdfArray());
                PdfLayer noPrint1 = new PdfLayer("from_noPrint1", fromDocument);
                pdfResource.addProperties(noPrint1.getPdfObject());
                noPrint1.setPrint("Print", false);
                // Intent (will be not copied)
                ocProperties.getPdfObject().put(PdfName.Intent, PdfName.View);
                // ListMode (will be not copied)
                ocProperties.getPdfObject().put(PdfName.ListMode, new PdfName("AllPages"));
            }
            fromDocBytes = outputStream.toByteArray();
        }

        byte[] toDocBytes = OcgPropertiesCopierTest.getDocumentWithAllDFields();

        Set<String> namesOrTitles = new HashSet<>();
        namesOrTitles.add("Parent1");
        namesOrTitles.add("Child1");
        namesOrTitles.add("Locked1");
        namesOrTitles.add("Locked2");
        namesOrTitles.add("Radio1");
        namesOrTitles.add("Radio2");
        namesOrTitles.add("Radio3");
        namesOrTitles.add("Radio4");
        namesOrTitles.add("On1");
        namesOrTitles.add("On2");
        namesOrTitles.add("Off1");
        namesOrTitles.add("Off2");
        namesOrTitles.add("noPrint1");

        namesOrTitles.add("from_Parent1");
        namesOrTitles.add("from_Child1");
        namesOrTitles.add("from_Locked1");
        namesOrTitles.add("from_Radio1");
        namesOrTitles.add("from_On1");
        namesOrTitles.add("from_Off1");
        namesOrTitles.add("from_noPrint1");

        PdfDictionary dDict = OcgPropertiesCopierTest.copyPagesAndAssertLayersNameAndGetDDict(namesOrTitles, fromDocBytes, toDocBytes);

        PdfArray locked = dDict.getAsArray(PdfName.Locked);
        Assertions.assertEquals(3, locked.size());
        Assertions.assertEquals("Locked1", locked.getAsDictionary(0).getAsString(PdfName.Name).toUnicodeString());
        Assertions.assertEquals("Locked2", locked.getAsDictionary(1).getAsString(PdfName.Name).toUnicodeString());
        Assertions.assertEquals("from_Locked1", locked.getAsDictionary(2).getAsString(PdfName.Name).toUnicodeString());

        PdfArray rbGroups = dDict.getAsArray(PdfName.RBGroups);
        Assertions.assertEquals(3, rbGroups.size());
        Assertions.assertEquals(3, rbGroups.getAsArray(0).size());
        Assertions.assertEquals("Radio1", rbGroups.getAsArray(0).getAsDictionary(0).getAsString(PdfName.Name).toUnicodeString());
        Assertions.assertEquals("Radio2", rbGroups.getAsArray(0).getAsDictionary(1).getAsString(PdfName.Name).toUnicodeString());
        Assertions.assertEquals("Radio3", rbGroups.getAsArray(0).getAsDictionary(2).getAsString(PdfName.Name).toUnicodeString());
        Assertions.assertEquals(1, rbGroups.getAsArray(1).size());
        Assertions.assertEquals("Radio4", rbGroups.getAsArray(1).getAsDictionary(0).getAsString(PdfName.Name).toUnicodeString());
        Assertions.assertEquals(1, rbGroups.getAsArray(2).size());
        Assertions.assertEquals("from_Radio1", rbGroups.getAsArray(2).getAsDictionary(0).getAsString(PdfName.Name).toUnicodeString());

        Assertions.assertNull(dDict.getAsArray(PdfName.ON));

        PdfArray off = dDict.getAsArray(PdfName.OFF);
        Assertions.assertEquals(3, off.size());
        Assertions.assertEquals("Off1", off.getAsDictionary(0).getAsString(PdfName.Name).toUnicodeString());
        Assertions.assertEquals("Off2", off.getAsDictionary(1).getAsString(PdfName.Name).toUnicodeString());
        Assertions.assertEquals("from_Off1", off.getAsDictionary(2).getAsString(PdfName.Name).toUnicodeString());

        Assertions.assertNull(dDict.getAsArray(PdfName.Creator));

        Assertions.assertEquals("Name", dDict.getAsString(PdfName.Name).toUnicodeString());

        Assertions.assertEquals(PdfName.ON, dDict.getAsName(PdfName.BaseState));

        PdfArray asArray = dDict.getAsArray(PdfName.AS);
        Assertions.assertEquals(1, asArray.size());
        Assertions.assertEquals(1, asArray.getAsDictionary(0).getAsArray(PdfName.Category).size());
        Assertions.assertEquals(PdfName.Print, asArray.getAsDictionary(0).getAsArray(PdfName.Category).getAsName(0));
        Assertions.assertEquals(2, asArray.getAsDictionary(0).getAsArray(PdfName.OCGs).size());
        Assertions.assertEquals("noPrint1", asArray.getAsDictionary(0).getAsArray(PdfName.OCGs).getAsDictionary(0).getAsString(PdfName.Name).toUnicodeString());
        Assertions.assertEquals("from_noPrint1", asArray.getAsDictionary(0).getAsArray(PdfName.OCGs).getAsDictionary(1).getAsString(PdfName.Name).toUnicodeString());

        Assertions.assertEquals(PdfName.View, dDict.getAsName(PdfName.Intent));

        Assertions.assertEquals(PdfName.VisiblePages, dDict.getAsName(PdfName.ListMode));
    }

    // Copy OCGs from different locations (OCMDs, annotations, content streams, xObjects) test block

    @Test
    public void copyOcgFromStreamPropertiesTest() throws IOException {
        byte[] fromDocBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = fromDocument.addNewPage();

                PdfResources pdfResource = page.getResources();
                pdfResource.addProperties(new PdfLayer("name", fromDocument).getPdfObject());
                pdfResource.makeIndirect(fromDocument);

                fromDocument.getCatalog().getOCProperties(true);
            }
            fromDocBytes = outputStream.toByteArray();
        }

        List<String> names = new ArrayList<>();
        names.add("name");
        OcgPropertiesCopierTest.copyPagesAndAssertLayersName(names, fromDocBytes);
    }

    @Test
    public void copyOcgFromAnnotationTest() throws IOException {
        byte[] fromDocBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = fromDocument.addNewPage();

                PdfAnnotation annotation = new PdfTextAnnotation(new Rectangle(50, 10));
                annotation.setLayer(new PdfLayer("someName", fromDocument));
                page.addAnnotation(annotation);

                fromDocument.getCatalog().getOCProperties(true);
            }
            fromDocBytes = outputStream.toByteArray();
        }

        List<String> names = new ArrayList<>();
        names.add("someName");
        OcgPropertiesCopierTest.copyPagesAndAssertLayersName(names, fromDocBytes);
    }

    @Test
    public void copyOcgFromApAnnotationTest() throws IOException {
        byte[] fromDocBytes;
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

                fromDocument.getCatalog().getOCProperties(true);
            }
            fromDocBytes = outputStream.toByteArray();
        }

        List<String> names = new ArrayList<>();
        names.add("someName1");
        names.add("someName2");
        names.add("someName3");
        names.add("someName4");
        OcgPropertiesCopierTest.copyPagesAndAssertLayersName(names, fromDocBytes);
    }

    @Test
    public void copyOcgFromImageXObjectTest() throws IOException {
        byte[] fromDocBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = fromDocument.addNewPage();
                PdfResources pdfResource = page.getResources();

                ImageData imageData = ImageDataFactory.create(SOURCE_FOLDER + "smallImage.png");
                PdfImageXObject imageXObject = new PdfImageXObject(imageData);
                imageXObject.setLayer(new PdfLayer("someName", fromDocument));
                imageXObject.makeIndirect(fromDocument);
                pdfResource.addImage(imageXObject);

                pdfResource.makeIndirect(fromDocument);
                fromDocument.getCatalog().getOCProperties(true);
            }
            fromDocBytes = outputStream.toByteArray();
        }

        List<String> names = new ArrayList<>();
        names.add("someName");
        OcgPropertiesCopierTest.copyPagesAndAssertLayersName(names, fromDocBytes);
    }

    @Test
    public void copyOcgsFromFormXObjectRecursivelyTest() throws IOException {
        byte[] fromDocBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = fromDocument.addNewPage();
                PdfResources pdfResource = page.getResources();

                PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(50, 10));
                formXObject.setLayer(new PdfLayer("someName1", fromDocument));
                PdfResources formResources = formXObject.getResources();
                formResources.addProperties(new PdfLayer("someName2", fromDocument).getPdfObject());

                ImageData imageData = ImageDataFactory.create(SOURCE_FOLDER + "smallImage.png");
                PdfImageXObject imageXObject = new PdfImageXObject(imageData);
                imageXObject.setLayer(new PdfLayer("someName3", fromDocument));
                imageXObject.makeIndirect(fromDocument);
                formResources.addImage(imageXObject);
                formResources.makeIndirect(fromDocument);
                formXObject.makeIndirect(fromDocument);
                pdfResource.addForm(formXObject);

                pdfResource.makeIndirect(fromDocument);
                fromDocument.getCatalog().getOCProperties(true);
            }
            fromDocBytes = outputStream.toByteArray();
        }

        List<String> names = new ArrayList<>();
        names.add("someName1");
        names.add("someName2");
        names.add("someName3");
        OcgPropertiesCopierTest.copyPagesAndAssertLayersName(names, fromDocBytes);
    }

    @Test
    public void copyOcmdByDictionaryFromStreamPropertiesTest() throws IOException {
        byte[] fromDocBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = fromDocument.addNewPage();

                PdfResources pdfResource = page.getResources();
                // Pass one name to the createOcmdDict method, so the OCMD.OCGs field will be a dictionary, not an array
                pdfResource.addProperties(OcgPropertiesCopierTest.createOcmdDict(new String[] {"name1"}, fromDocument));
                pdfResource.makeIndirect(fromDocument);

                fromDocument.getCatalog().getOCProperties(true);
            }
            fromDocBytes = outputStream.toByteArray();
        }

        List<String> names = new ArrayList<>();
        names.add("name1");
        OcgPropertiesCopierTest.copyPagesAndAssertLayersName(names, fromDocBytes);
    }

    @Test
    public void copyOcmdByArrayFromStreamPropertiesTest() throws IOException {
        byte[] fromDocBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = fromDocument.addNewPage();

                PdfResources pdfResource = page.getResources();
                pdfResource.addProperties(OcgPropertiesCopierTest.createOcmdDict(new String[] {"name1", "name2"}, fromDocument));
                pdfResource.makeIndirect(fromDocument);

                fromDocument.getCatalog().getOCProperties(true);
            }
            fromDocBytes = outputStream.toByteArray();
        }

        List<String> names = new ArrayList<>();
        names.add("name1");
        names.add("name2");
        OcgPropertiesCopierTest.copyPagesAndAssertLayersName(names, fromDocBytes);
    }

    @Test
    public void copyOcmdFromAnnotationTest() throws IOException {
        byte[] fromDocBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = fromDocument.addNewPage();

                PdfAnnotation annotation = new PdfTextAnnotation(new Rectangle(50, 10));
                annotation.setLayer(new PdfLayerMembership(OcgPropertiesCopierTest.createOcmdDict(new String[] {"someName1", "someName2"}, fromDocument)));
                page.addAnnotation(annotation);

                fromDocument.getCatalog().getOCProperties(true);
            }
            fromDocBytes = outputStream.toByteArray();
        }

        List<String> names = new ArrayList<>();
        names.add("someName1");
        names.add("someName2");
        OcgPropertiesCopierTest.copyPagesAndAssertLayersName(names, fromDocBytes);
    }

    @Test
    public void copyOcmdFromImageXObjectTest() throws IOException {
        byte[] fromDocBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = fromDocument.addNewPage();
                PdfResources pdfResource = page.getResources();

                ImageData imageData = ImageDataFactory.create(SOURCE_FOLDER + "smallImage.png");
                PdfImageXObject imageXObject = new PdfImageXObject(imageData);
                imageXObject.setLayer(new PdfLayerMembership(OcgPropertiesCopierTest.createOcmdDict(new String[] {"someName1", "someName2"}, fromDocument)));
                imageXObject.makeIndirect(fromDocument);
                pdfResource.addImage(imageXObject);

                pdfResource.makeIndirect(fromDocument);
                fromDocument.getCatalog().getOCProperties(true);
            }
            fromDocBytes = outputStream.toByteArray();
        }

        List<String> names = new ArrayList<>();
        names.add("someName1");
        names.add("someName2");
        OcgPropertiesCopierTest.copyPagesAndAssertLayersName(names, fromDocBytes);
    }

    @Test
    public void copyOcmdsFromFormXObjectRecursivelyTest() throws IOException {
        byte[] fromDocBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = fromDocument.addNewPage();
                PdfResources pdfResource = page.getResources();

                PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(50, 10));
                formXObject.setLayer(new PdfLayerMembership(OcgPropertiesCopierTest.createOcmdDict(new String[] {"someName1", "someName2"}, fromDocument)));
                PdfResources formResources = formXObject.getResources();
                formResources.addProperties(OcgPropertiesCopierTest.createOcmdDict(new String[] {"someName3", "someName4"}, fromDocument));

                ImageData imageData = ImageDataFactory.create(SOURCE_FOLDER + "smallImage.png");
                PdfImageXObject imageXObject = new PdfImageXObject(imageData);
                imageXObject.setLayer(new PdfLayerMembership(OcgPropertiesCopierTest.createOcmdDict(new String[] {"someName5", "someName6"}, fromDocument)));
                imageXObject.makeIndirect(fromDocument);
                formResources.addImage(imageXObject);
                formResources.makeIndirect(fromDocument);
                formXObject.makeIndirect(fromDocument);
                pdfResource.addForm(formXObject);

                pdfResource.makeIndirect(fromDocument);
                fromDocument.getCatalog().getOCProperties(true);
            }
            fromDocBytes = outputStream.toByteArray();
        }

        List<String> names = new ArrayList<>();
        names.add("someName1");
        names.add("someName2");
        names.add("someName3");
        names.add("someName4");
        names.add("someName5");
        names.add("someName6");
        OcgPropertiesCopierTest.copyPagesAndAssertLayersName(names, fromDocBytes);
    }

    @Test
    public void copyEmptyOcgTest() throws IOException {
        byte[] fromDocBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfDictionary DDic = new PdfDictionary();
                DDic.put(PdfName.ON, new PdfArray());
                DDic.put(PdfName.Order, new PdfArray());
                DDic.put(PdfName.RBGroups, new PdfArray());

                PdfDictionary OcDic = new PdfDictionary();
                OcDic.put(PdfName.D, DDic);
                OcDic.put(PdfName.OCGs, new PdfArray());

                fromDocument.getCatalog().put(PdfName.OCProperties,OcDic);
            }
            fromDocBytes = outputStream.toByteArray();
        }

        try (PdfDocument toDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(fromDocBytes)))) {
                fromDocument.copyPagesTo(1, 1, toDocument);

                Assertions.assertNull(toDocument.getCatalog().getOCProperties(false));
            }
        }
    }

    private static byte[] getDocumentWithAllDFields() throws IOException {
        byte[] fromDocBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = fromDocument.addNewPage();
                PdfResources pdfResource = page.getResources();

                // Order
                PdfLayer parent1 = new PdfLayer("Parent1", fromDocument);
                PdfLayer child1 = new PdfLayer("Child1", fromDocument);
                pdfResource.addProperties(child1.getPdfObject());
                parent1.addChild(child1);

                // Locked
                PdfLayer locked1 = new PdfLayer("Locked1", fromDocument);
                locked1.setLocked(true);
                pdfResource.addProperties(locked1.getPdfObject());
                PdfLayer locked2 = new PdfLayer("Locked2", fromDocument);
                locked2.setLocked(true);

                // RBGroups
                PdfLayer radio1 = new PdfLayer("Radio1", fromDocument);
                pdfResource.addProperties(radio1.getPdfObject());
                PdfLayer radio2 = new PdfLayer("Radio2", fromDocument);
                PdfLayer radio3 = new PdfLayer("Radio3", fromDocument);
                pdfResource.addProperties(radio3.getPdfObject());
                List<PdfLayer> options = new ArrayList<>();
                options.add(radio1);
                options.add(radio2);
                options.add(radio3);
                PdfLayer.addOCGRadioGroup(fromDocument, options);
                options = new ArrayList<>();
                PdfLayer radio4 = new PdfLayer("Radio4", fromDocument);
                options.add(radio4);
                pdfResource.addProperties(radio4.getPdfObject());
                PdfLayer.addOCGRadioGroup(fromDocument, options);

                // ON
                PdfLayer on1 = new PdfLayer("On1", fromDocument);
                on1.setOn(true);
                pdfResource.addProperties(on1.getPdfObject());
                PdfLayer on2 = new PdfLayer("On2", fromDocument);
                on2.setOn(true);

                // OFF
                PdfLayer off1 = new PdfLayer("Off1", fromDocument);
                off1.setOn(false);
                pdfResource.addProperties(off1.getPdfObject());
                PdfLayer off2 = new PdfLayer("Off2", fromDocument);
                off2.setOn(false);

                pdfResource.makeIndirect(fromDocument);
                PdfOCProperties ocProperties = fromDocument.getCatalog().getOCProperties(true);
                PdfDictionary dDictionary = ocProperties.getPdfObject().getAsDictionary(PdfName.D);
                // Creator (will be not copied)
                dDictionary.put(PdfName.Creator, new PdfString("CreatorName", PdfEncodings.UNICODE_BIG));
                // Name (will be automatically changed)
                dDictionary.put(PdfName.Name, new PdfString("Name", PdfEncodings.UNICODE_BIG));
                // BaseState (will be not copied)
                dDictionary.put(PdfName.BaseState, PdfName.ON);
                // AS (will be automatically changed)
                PdfArray asArray = new PdfArray();
                PdfDictionary dict = new PdfDictionary();
                dict.put(PdfName.Event, PdfName.View);
                PdfArray categoryArray = new PdfArray();
                categoryArray.add(PdfName.Zoom);
                dict.put(PdfName.Category, categoryArray);
                PdfArray ocgs = new PdfArray();
                ocgs.add(locked1.getPdfObject());
                dict.put(PdfName.OCGs, ocgs);
                asArray.add(dict);
                dDictionary.put(PdfName.AS, asArray);

                PdfLayer noPrint1 = new PdfLayer("noPrint1", fromDocument);
                pdfResource.addProperties(noPrint1.getPdfObject());
                noPrint1.setPrint("Print", false);
                // Intent (will be not copied)
                dDictionary.put(PdfName.Intent, PdfName.View);
                // ListMode (will be not copied)
                dDictionary.put(PdfName.ListMode, PdfName.VisiblePages);
            }
            fromDocBytes = outputStream.toByteArray();
        }
        return fromDocBytes;
    }

    private static PdfDictionary createOcmdDict(String[] names, PdfDocument document) {
        PdfDictionary ocmd = new PdfDictionary();
        ocmd.put(PdfName.Type, PdfName.OCMD);
        if (names.length > 1) {
            PdfArray ocgs = new PdfArray();
            for (String name : names) {
                ocgs.add(new PdfLayer(name, document).getPdfObject());
            }
            ocmd.put(PdfName.OCGs, ocgs);
        } else {
            ocmd.put(PdfName.OCGs, new PdfLayer(names[0], document).getPdfObject());
        }
        ocmd.makeIndirect(document);
        return ocmd;
    }

    private static PdfDictionary copyPagesAndAssertLayersNameAndGetDDict(Set<String> namesOrTitles, byte[] fromDocBytes, byte[] toDocBytes) throws IOException {
        PdfDocument toDocument;
        if (toDocBytes == null) {
            toDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        } else {
            toDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(toDocBytes)), new PdfWriter(new ByteArrayOutputStream()));
        }

        PdfDocument fromDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(fromDocBytes)));
        fromDocument.copyPagesTo(1, 1, toDocument);

        OcgPropertiesCopierTest.checkLayersOrTitleNameInToDocument(toDocument, namesOrTitles);
        PdfOCProperties ocProperties = toDocument.getCatalog().getOCProperties(false);
        return ocProperties.getPdfObject().getAsDictionary(PdfName.D);
    }

    private static void copyPagesAndAssertLayersName(List<String> names, byte[] fromDocBytes, byte[] toDocBytes) throws IOException {
        PdfDocument toDocument;
        if (toDocBytes == null) {
            toDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        } else {
            toDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(toDocBytes)), new PdfWriter(new ByteArrayOutputStream()));
        }

        try (PdfDocument fromDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(fromDocBytes)))) {
            fromDocument.copyPagesTo(1, 1, toDocument);

            OcgPropertiesCopierTest.checkLayersNameInToDocument(toDocument, names);
        }

        toDocument.close();
    }

    private static void checkLayersOrTitleNameInToDocument(PdfDocument toDocument, Set<String> namesOrTitles) {
        Assertions.assertNotNull(toDocument.getCatalog());
        PdfOCProperties ocProperties = toDocument.getCatalog().getOCProperties(false);
        ocProperties.fillDictionary();
        Assertions.assertNotNull(ocProperties);
        Assertions.assertEquals(namesOrTitles.size(), ocProperties.getLayers().size());
        for (PdfLayer layer : ocProperties.getLayers()) {
            Assertions.assertNotNull(layer);
            String layerTitle = layer.getTitle();
            if (namesOrTitles.contains(layerTitle)) {
                Assertions.assertTrue(namesOrTitles.remove(layerTitle));
            } else {
                PdfDictionary layerDictionary = layer.getPdfObject();
                Assertions.assertNotNull(layerDictionary.get(PdfName.Name));
                String layerName = layerDictionary.get(PdfName.Name).toString();
                Assertions.assertTrue(namesOrTitles.remove(layerName));
            }
        }
    }

    private static void checkLayersNameInToDocument(PdfDocument toDocument, List<String> names) {
        Assertions.assertNotNull(toDocument.getCatalog());
        PdfOCProperties ocProperties = toDocument.getCatalog().getOCProperties(false);
        Assertions.assertNotNull(ocProperties);
        Assertions.assertEquals(names.size(), ocProperties.getLayers().size());
        for (PdfLayer layer : ocProperties.getLayers()) {
            Assertions.assertNotNull(layer);

            PdfDictionary layerDictionary = layer.getPdfObject();
            Assertions.assertNotNull(layerDictionary.get(PdfName.Name));
            String layerNameString = layerDictionary.get(PdfName.Name).toString();
            Assertions.assertTrue(names.contains(layerNameString));
            names.remove(layerNameString);
        }
    }

    private static void copyPagesAndAssertLayersName(List<String> names, byte[] fromDocBytes) throws IOException {
        OcgPropertiesCopierTest.copyPagesAndAssertLayersName(names, fromDocBytes, null);
    }
}
