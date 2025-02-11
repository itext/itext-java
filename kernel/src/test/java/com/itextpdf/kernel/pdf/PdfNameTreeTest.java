/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.annot.PdfAnnotationAppearance;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.Map;

@Tag("IntegrationTest")
public class PdfNameTreeTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfNameTreeTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfNameTreeTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }
    
    @Test
    public void embeddedFileAndJavascriptTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "FileWithSingleAttachment.pdf"));
        PdfNameTree embeddedFilesNameTree = pdfDocument.getCatalog().getNameTree(PdfName.EmbeddedFiles);
        Map<PdfString, PdfObject> objs = embeddedFilesNameTree.getNames();
        PdfNameTree javascript = pdfDocument.getCatalog().getNameTree(PdfName.JavaScript);
        Map<PdfString, PdfObject> objs2 = javascript.getNames();
        pdfDocument.close();
        Assertions.assertEquals(1, objs.size());
        Assertions.assertEquals(1, objs2.size());
    }

    @Test
    public void embeddedFileAddedInAppendModeTest() throws  IOException{
        //Create input document
        ByteArrayOutputStream boasEmpty = new ByteArrayOutputStream();
        PdfWriter emptyDocWriter = new PdfWriter(boasEmpty);
        PdfDocument emptyDoc = new PdfDocument(emptyDocWriter);
        emptyDoc.addNewPage();
        PdfDictionary emptyNamesDic = new PdfDictionary();
        emptyNamesDic.makeIndirect(emptyDoc);
        emptyDoc.getCatalog().getPdfObject().put(PdfName.Names,emptyNamesDic);

        emptyDoc.close();

        //Create input document
        ByteArrayOutputStream boasAttached = new ByteArrayOutputStream();
        PdfWriter attachDocWriter = new PdfWriter(boasAttached);
        PdfDocument attachDoc = new PdfDocument(attachDocWriter);
        attachDoc.addNewPage();
        attachDoc.close();

        //Attach file in append mode
        PdfReader appendReader = new PdfReader(new ByteArrayInputStream(boasEmpty.toByteArray()));
        ByteArrayOutputStream boasAppend = new ByteArrayOutputStream();
        PdfWriter appendWriter = new PdfWriter(boasAppend);
        PdfDocument appendDoc = new PdfDocument(appendReader,appendWriter,new StampingProperties().useAppendMode());

        appendDoc.addFileAttachment("Test File", PdfFileSpec.createEmbeddedFileSpec(appendDoc,boasAttached.toByteArray(),"Append Embedded File test","Test file",null));
        appendDoc.close();

        //Check final result
        PdfReader finalReader = new PdfReader(new ByteArrayInputStream(boasAppend.toByteArray()));
        PdfDocument finalDoc = new PdfDocument(finalReader);

        PdfNameTree embeddedFilesNameTree = finalDoc.getCatalog().getNameTree(PdfName.EmbeddedFiles);
        Map<PdfString, PdfObject> embeddedFilesMap = embeddedFilesNameTree.getNames();

        Assertions.assertTrue(embeddedFilesMap.size()>0);
        Assertions.assertTrue(embeddedFilesMap.containsKey(new PdfString("Test File")));
    }

    @Test
    public void annotationAppearanceTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "AnnotationAppearanceTest.pdf"));
        PdfPage page = pdfDocument.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColor(ColorConstants.MAGENTA).beginText().setFontAndSize(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN), 30)
                .setTextMatrix(25, 500).showText("This file has AP key in Names dictionary").endText();
        PdfArray array = new PdfArray();
        array.add(new PdfString("normalAppearance"));
        array.add(new PdfAnnotationAppearance().setState(PdfName.N, new PdfFormXObject(new Rectangle(50, 50 , 50, 50))).getPdfObject());

        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.Names, array);
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.AP, dict);
        pdfDocument.getCatalog().getPdfObject().put(PdfName.Names, dictionary);

        PdfNameTree appearance = pdfDocument.getCatalog().getNameTree(PdfName.AP);
        Map<PdfString, PdfObject> objs = appearance.getNames();
        pdfDocument.close();
        Assertions.assertEquals(1, objs.size());
    }

    @Test
    public void setModifiedFlagTest() throws IOException {
        testSetModified(false);
    }

    @Test
    public void setModifiedFlagAppendModeTest() throws IOException {
        testSetModified(true);
    }

    @Test
    public void checkNamesOrder() throws IOException {
        PdfDocument doc = new PdfDocument(new PdfReader(sourceFolder + "namedDestinations.pdf"));
        final List<String> expectedNames = new ArrayList<>();
        expectedNames.add("Destination_1");
        expectedNames.add("Destination_2");
        expectedNames.add("Destination_3");
        expectedNames.add("Destination_4");
        expectedNames.add("Destination_5");

        System.out.println("Expected names: " + expectedNames);

        for (int i = 0; i < 10; i++) {
            IPdfNameTreeAccess names = doc.getCatalog().getNameTree(PdfName.Dests);
            List<String> actualNames = new ArrayList<>();
            for (PdfString name : names.getKeys()) {
                actualNames.add(name.toUnicodeString());
            }

            System.out.println("Actual names:   " + actualNames);

            Assertions.assertEquals(expectedNames, actualNames);
        }

       doc.close();
    }

    private static void testSetModified(boolean isAppendMode) throws IOException {
        PdfString[] expectedKeys = {
                new PdfString("new_key1"),
                new PdfString("new_key2"),
                new PdfString("new_key3"),
        };

        ByteArrayOutputStream sourceFile = createDocumentInMemory();
        ByteArrayOutputStream modifiedFile = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(new ByteArrayInputStream(sourceFile.toByteArray()));
        PdfDocument pdfDoc = isAppendMode
                ? new PdfDocument(reader, new PdfWriter(modifiedFile), new StampingProperties().useAppendMode())
                : new PdfDocument(reader, new PdfWriter(modifiedFile));
        PdfNameTree nameTree = pdfDoc.getCatalog().getNameTree(PdfName.Dests);
        Map<PdfString, PdfObject> names = nameTree.getNames();
        List<PdfString> keys = new ArrayList<>(names.keySet());

        for (int i = 0; i < keys.size(); i++) {
            names.put(expectedKeys[i], names.get(keys.get(i)));
            names.remove(keys.get(i));
        }

        nameTree.setModified();

        pdfDoc.close();

        reader = new PdfReader(new ByteArrayInputStream(modifiedFile.toByteArray()));
        pdfDoc = new PdfDocument(reader);
        nameTree = pdfDoc.getCatalog().getNameTree(PdfName.Dests);
        Set<PdfString> actualKeys = nameTree.getNames().keySet();

        Assertions.assertArrayEquals(expectedKeys, actualKeys.toArray());
    }

    private static ByteArrayOutputStream createDocumentInMemory() {
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(boas));

        pdfDoc.addNewPage();
        pdfDoc.getCatalog().getNameTree(PdfName.Dests).addEntry("key1",
                new PdfArray(new float[] {0, 0, 0, 0}));
        pdfDoc.getCatalog().getNameTree(PdfName.Dests).addEntry("key2",
                new PdfArray(new float[] {1, 1, 1, 1}));
        pdfDoc.getCatalog().getNameTree(PdfName.Dests).addEntry("key3",
                new PdfArray(new float[] {2, 2, 2, 2}));

        pdfDoc.close();

        return boas;
    }
}
