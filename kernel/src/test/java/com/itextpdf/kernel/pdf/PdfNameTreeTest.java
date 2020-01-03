/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.annot.PdfAnnotationAppearance;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.Map;

@Category(IntegrationTest.class)
public class PdfNameTreeTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfNameTreeTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfNameTreeTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void embeddedFileAndJavascriptTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "FileWithSingleAttachment.pdf"));
        PdfNameTree embeddedFilesNameTree = pdfDocument.getCatalog().getNameTree(PdfName.EmbeddedFiles);
        Map<String, PdfObject> objs = embeddedFilesNameTree.getNames();
        PdfNameTree javascript = pdfDocument.getCatalog().getNameTree(PdfName.JavaScript);
        Map<String, PdfObject> objs2 = javascript.getNames();
        pdfDocument.close();
        Assert.assertEquals(1, objs.size());
        Assert.assertEquals(1, objs2.size());
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
        Map<String, PdfObject> embeddedFilesMap = embeddedFilesNameTree.getNames();

        Assert.assertTrue(embeddedFilesMap.size()>0);
        Assert.assertTrue(embeddedFilesMap.containsKey("Test File"));
    }

    @Test
    public void annotationAppearanceTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "AnnotationAppearanceTest.pdf"));
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
        Map<String, PdfObject> objs = appearance.getNames();
        pdfDocument.close();
        Assert.assertEquals(1, objs.size());
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
            Map<String, PdfObject> names = doc.getCatalog().getNameTree(PdfName.Dests).getNames();
            List<String> actualNames = new ArrayList<>(names.keySet());

            System.out.println("Actual names:   " + actualNames);

            Assert.assertEquals(expectedNames, actualNames);
        }

       doc.close();
    }

    private static void testSetModified(boolean isAppendMode) throws IOException {
        String[] expectedKeys = {
                "new_key1",
                "new_key2",
                "new_key3",
        };

        ByteArrayOutputStream sourceFile = createDocumentInMemory();
        ByteArrayOutputStream modifiedFile = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(new ByteArrayInputStream(sourceFile.toByteArray()));
        PdfDocument pdfDoc = isAppendMode
                ? new PdfDocument(reader, new PdfWriter(modifiedFile), new StampingProperties().useAppendMode())
                : new PdfDocument(reader, new PdfWriter(modifiedFile));
        PdfNameTree nameTree = pdfDoc.getCatalog().getNameTree(PdfName.Dests);
        Map<String, PdfObject> names = nameTree.getNames();
        ArrayList<String> keys = new ArrayList<>(names.keySet());

        for (int i = 0; i < keys.size(); i++) {
            names.put(expectedKeys[i], names.get(keys.get(i)));
            names.remove(keys.get(i));
        }

        nameTree.setModified();

        pdfDoc.close();

        reader = new PdfReader(new ByteArrayInputStream(modifiedFile.toByteArray()));
        pdfDoc = new PdfDocument(reader);
        nameTree = pdfDoc.getCatalog().getNameTree(PdfName.Dests);
        Set<String> actualKeys = nameTree.getNames().keySet();

        Assert.assertArrayEquals(expectedKeys, actualKeys.toArray());
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
