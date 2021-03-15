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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.DeflaterOutputStream;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.crypto.securityhandler.UnsupportedSecurityHandlerException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader.StrictnessLevel;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.nio.charset.StandardCharsets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class PdfDocumentTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/PdfDocumentTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/PdfDocumentTest/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void missingProducerTest() throws IOException {
        String inputFile = SOURCE_FOLDER + "missingProducer.pdf";

        try (PdfDocument document = new PdfDocument(new PdfReader(inputFile))) {
            PdfDocumentInfo documentInfo = document.getDocumentInfo();
            Assert.assertNull(documentInfo.getPdfObject().get(PdfName.Producer));
            Assert.assertNull(documentInfo.getProducer());
        }

        try (PdfDocument document = new PdfDocument(new PdfReader(inputFile), new PdfWriter(new ByteArrayOutputStream()))) {
            PdfDocumentInfo documentInfo = document.getDocumentInfo();
            Assert.assertNotNull(documentInfo.getPdfObject().get(PdfName.Producer));
            Assert.assertNotNull(document.getDocumentInfo().getProducer());
        }
    }

    @Test
    public void nullProducerTest() throws IOException {
        String inputFile = SOURCE_FOLDER + "nullProducer.pdf";

        try (PdfDocument document = new PdfDocument(new PdfReader(inputFile))) {
            PdfDocumentInfo documentInfo = document.getDocumentInfo();
            Assert.assertEquals(PdfNull.PDF_NULL, documentInfo.getPdfObject().get(PdfName.Producer));
            Assert.assertNull(documentInfo.getProducer());
        }

        try (PdfDocument document = new PdfDocument(new PdfReader(inputFile), new PdfWriter(new ByteArrayOutputStream()))) {
            PdfDocumentInfo documentInfo = document.getDocumentInfo();
            Assert.assertNotNull(documentInfo.getPdfObject().get(PdfName.Producer));
            Assert.assertNotNull(document.getDocumentInfo().getProducer());
        }
    }

    @Test
    public void nameProducerTest() throws IOException {
        String inputFile = SOURCE_FOLDER + "nameProducer.pdf";

        try (PdfDocument document = new PdfDocument(new PdfReader(inputFile))) {
            PdfDocumentInfo documentInfo = document.getDocumentInfo();
            Assert.assertEquals(new PdfName("producerAsName"), documentInfo.getPdfObject().get(PdfName.Producer));
            Assert.assertNull(documentInfo.getProducer());
        }

        try (PdfDocument document = new PdfDocument(new PdfReader(inputFile), new PdfWriter(new ByteArrayOutputStream()))) {
            PdfDocumentInfo documentInfo = document.getDocumentInfo();
            Assert.assertNotNull(documentInfo.getPdfObject().get(PdfName.Producer));
            Assert.assertNotNull(document.getDocumentInfo().getProducer());
        }
    }

    @Test
    public void writingVersionTest01() throws IOException {
        // There is a possibility to override version in stamping mode
        String out = DESTINATION_FOLDER + "writing_pdf_version.pdf";

        PdfDocument pdfDoc = new PdfDocument(
                new PdfWriter(out, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));

        Assert.assertEquals(PdfVersion.PDF_2_0, pdfDoc.getPdfVersion());

        pdfDoc.addNewPage();
        pdfDoc.close();

        PdfDocument assertPdfDoc = new PdfDocument(new PdfReader(out));
        Assert.assertEquals(PdfVersion.PDF_2_0, assertPdfDoc.getPdfVersion());
        assertPdfDoc.close();
    }

    //We have this test in PdfOutlineTest as well, because we had some issues with outlines before. One test worked
    // fine, while another one failed.
    @Test
    public void addOutlinesWithNamedDestinations01() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "outlinesWithNamedDestinations01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "iphone_user_guide.pdf"),
                new PdfWriter(filename));
        PdfArray array1 = new PdfArray();
        array1.add(pdfDoc.getPage(2).getPdfObject());
        array1.add(PdfName.XYZ);
        array1.add(new PdfNumber(36));
        array1.add(new PdfNumber(806));
        array1.add(new PdfNumber(0));

        PdfArray array2 = new PdfArray();
        array2.add(pdfDoc.getPage(3).getPdfObject());
        array2.add(PdfName.XYZ);
        array2.add(new PdfNumber(36));
        array2.add(new PdfNumber(806));
        array2.add(new PdfNumber(1.25));

        PdfArray array3 = new PdfArray();
        array3.add(pdfDoc.getPage(4).getPdfObject());
        array3.add(PdfName.XYZ);
        array3.add(new PdfNumber(36));
        array3.add(new PdfNumber(806));
        array3.add(new PdfNumber(1));

        pdfDoc.addNamedDestination("test1", array2);
        pdfDoc.addNamedDestination("test2", array3);
        pdfDoc.addNamedDestination("test3", array1);

        PdfOutline root = pdfDoc.getOutlines(false);

        PdfOutline firstOutline = root.addOutline("Test1");
        firstOutline.addDestination(PdfDestination.makeDestination(new PdfString("test1")));
        PdfOutline secondOutline = root.addOutline("Test2");
        secondOutline.addDestination(PdfDestination.makeDestination(new PdfString("test2")));
        PdfOutline thirdOutline = root.addOutline("Test3");
        thirdOutline.addDestination(PdfDestination.makeDestination(new PdfString("test3")));
        pdfDoc.close();

        Assert.assertNull(new CompareTool()
                .compareByContent(filename, SOURCE_FOLDER + "cmp_outlinesWithNamedDestinations01.pdf",
                        DESTINATION_FOLDER,
                        "diff_"));
    }

    @Test
    public void freeReferencesInObjectStream() throws IOException {
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "styledLineArts_Redacted.pdf");
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfDocument document = new PdfDocument(reader, writer, new StampingProperties().useAppendMode());
        PdfDictionary dict = new PdfDictionary();
        dict.makeIndirect(document);
        Assert.assertTrue(dict.getIndirectReference().getObjNumber() > 0);
    }

    @Test
    public void removeUnusedObjectsInWriterModeTest() throws IOException {
        String filename = "removeUnusedObjectsInWriter.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(DESTINATION_FOLDER + filename));

        pdfDocument.addNewPage();

        PdfDictionary unusedDictionary = new PdfDictionary();
        PdfArray unusedArray = (PdfArray) new PdfArray().makeIndirect(pdfDocument);
        unusedArray.add(new PdfNumber(42));
        unusedDictionary.put(new PdfName("testName"), unusedArray);

        unusedDictionary.makeIndirect(pdfDocument);

        Assert.assertEquals(pdfDocument.getXref().size(), 8);
        //on closing, all unused objects shall not be written to resultant document
        pdfDocument.close();

        PdfDocument testerDocument = new PdfDocument(new PdfReader(DESTINATION_FOLDER + filename));
        Assert.assertEquals(testerDocument.getXref().size(), 6);
        testerDocument.close();
    }

    @Test
    public void removeUnusedObjectsInStampingModeTest() throws IOException {
        String filenameIn = "docWithUnusedObjects_1.pdf";
        String filenameOut = "removeUnusedObjectsInStamping.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(DESTINATION_FOLDER + filenameIn));

        pdfDocument.addNewPage();

        PdfDictionary unusedDictionary = new PdfDictionary();
        PdfArray unusedArray = (PdfArray) new PdfArray().makeIndirect(pdfDocument);
        unusedArray.add(new PdfNumber(42));
        unusedDictionary.put(new PdfName("testName"), unusedArray);

        unusedDictionary.makeIndirect(pdfDocument).flush();
        pdfDocument.close();

        PdfDocument doc = new PdfDocument(new PdfReader(DESTINATION_FOLDER + filenameIn),
                new PdfWriter(DESTINATION_FOLDER + filenameOut));
        Assert.assertEquals(doc.getXref().size(), 8);
        //on closing, all unused objects shall not be written to resultant document
        doc.close();

        PdfDocument testerDocument = new PdfDocument(new PdfReader(DESTINATION_FOLDER + filenameOut));
        Assert.assertEquals(testerDocument.getXref().size(), 6);
        testerDocument.close();
    }


    @Test
    public void addUnusedObjectsInWriterModeTest() throws IOException {
        String filename = "addUnusedObjectsInWriter.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(DESTINATION_FOLDER + filename));

        pdfDocument.addNewPage();

        PdfDictionary unusedDictionary = new PdfDictionary();
        PdfArray unusedArray = (PdfArray) new PdfArray().makeIndirect(pdfDocument);
        unusedArray.add(new PdfNumber(42));
        unusedDictionary.put(new PdfName("testName"), unusedArray);

        unusedDictionary.makeIndirect(pdfDocument);

        Assert.assertEquals(pdfDocument.getXref().size(), 8);
        pdfDocument.setFlushUnusedObjects(true);
        pdfDocument.close();

        PdfDocument testerDocument = new PdfDocument(new PdfReader(DESTINATION_FOLDER + filename));
        Assert.assertEquals(testerDocument.getXref().size(), 8);
        testerDocument.close();
    }

    @Test
    public void addUnusedObjectsInStampingModeTest() throws IOException {
        String filenameIn = "docWithUnusedObjects_2.pdf";
        String filenameOut = "addUnusedObjectsInStamping.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(DESTINATION_FOLDER + filenameIn));

        pdfDocument.addNewPage();

        PdfDictionary unusedDictionary = new PdfDictionary();
        PdfArray unusedArray = (PdfArray) new PdfArray().makeIndirect(pdfDocument);
        unusedArray.add(new PdfNumber(42));
        unusedDictionary.put(new PdfName("testName"), unusedArray);

        unusedDictionary.makeIndirect(pdfDocument).flush();
        pdfDocument.close();

        PdfDocument doc = new PdfDocument(new PdfReader(DESTINATION_FOLDER + filenameIn),
                new PdfWriter(DESTINATION_FOLDER + filenameOut));
        Assert.assertEquals(doc.getXref().size(), 8);
        doc.setFlushUnusedObjects(true);
        doc.close();

        PdfDocument testerDocument = new PdfDocument(new PdfReader(DESTINATION_FOLDER + filenameOut));
        Assert.assertEquals(testerDocument.getXref().size(), 8);
        testerDocument.close();
    }

    @Test
    public void addUnusedStreamObjectsTest() throws IOException {
        String filenameIn = "docWithUnusedObjects_3.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(DESTINATION_FOLDER + filenameIn));

        pdfDocument.addNewPage();

        PdfDictionary unusedDictionary = new PdfDictionary();
        PdfArray unusedArray = (PdfArray) new PdfArray().makeIndirect(pdfDocument);
        unusedArray.add(new PdfNumber(42));
        PdfStream stream = new PdfStream(new byte[] {1, 2, 34, 45}, 0);
        unusedArray.add(stream);
        unusedDictionary.put(new PdfName("testName"), unusedArray);
        unusedDictionary.makeIndirect(pdfDocument).flush();
        pdfDocument.setFlushUnusedObjects(true);
        pdfDocument.close();

        PdfDocument testerDocument = new PdfDocument(new PdfReader(DESTINATION_FOLDER + filenameIn));
        Assert.assertEquals(testerDocument.getXref().size(), 9);
        testerDocument.close();
    }

    @Test
    public void testImageCompressLevel() throws IOException {
        byte[] b = ImageDataFactory.create(SOURCE_FOLDER + "berlin2013.jpg").getData();
        com.itextpdf.io.source.ByteArrayOutputStream image = new com.itextpdf.io.source.ByteArrayOutputStream();
        image.assignBytes(b, b.length);

        ByteArrayOutputStream byteArrayStream1 = new com.itextpdf.io.source.ByteArrayOutputStream();
        DeflaterOutputStream zip = new DeflaterOutputStream(byteArrayStream1, 9);
        image.writeTo(zip);

        ByteArrayOutputStream byteArrayStream2 = new com.itextpdf.io.source.ByteArrayOutputStream();
        DeflaterOutputStream zip2 = new DeflaterOutputStream(byteArrayStream2, -1);
        image.writeTo(zip2);

        Assert.assertTrue(byteArrayStream1.size() == byteArrayStream2.size());
        zip.close();
        zip2.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.FLUSHED_OBJECT_CONTAINS_FREE_REFERENCE))
    public void testFreeReference() throws IOException, InterruptedException {
        PdfWriter writer = new PdfWriter(DESTINATION_FOLDER + "freeReference.pdf",
                new WriterProperties().setFullCompressionMode(false));
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + "baseFreeReference.pdf"), writer);
        pdfDocument.getPage(1).getResources().getPdfObject().getAsArray(new PdfName("d")).get(0).getIndirectReference()
                .setFree();
        PdfStream pdfStream = new PdfStream();
        pdfStream.setData(new byte[] {24, 23, 67});
        pdfStream.makeIndirect(pdfDocument);
        pdfDocument.getPage(1).getResources().getPdfObject().getAsArray(new PdfName("d")).add(pdfStream);
        pdfDocument.close();
        Assert.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + "freeReference.pdf", SOURCE_FOLDER + "cmp_freeReference.pdf",
                        DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void fullCompressionAppendMode() throws IOException, InterruptedException {
        PdfWriter writer = new PdfWriter(DESTINATION_FOLDER + "fullCompressionAppendMode.pdf",
                new WriterProperties()
                        .setFullCompressionMode(true)
                        .setCompressionLevel(CompressionConstants.NO_COMPRESSION));
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + "fullCompressionDoc.pdf"), writer,
                new StampingProperties().useAppendMode());

        PdfPage page = pdfDocument.getPage(1);
        PdfStream contentStream = new PdfStream();
        String contentStr = new String(pdfDocument.getPage(1).getFirstContentStream().getBytes(),
                StandardCharsets.US_ASCII);
        contentStream.setData(contentStr.replace("/F1 16", "/F1 24").getBytes(StandardCharsets.US_ASCII));
        page.getPdfObject().put(PdfName.Contents, contentStream);
        page.setModified();

        pdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "fullCompressionAppendMode.pdf",
                SOURCE_FOLDER + "cmp_fullCompressionAppendMode.pdf", DESTINATION_FOLDER, "diff_"));

        PdfDocument assertDoc = new PdfDocument(new PdfReader(DESTINATION_FOLDER + "fullCompressionAppendMode.pdf"));
        Assert.assertTrue(assertDoc.getPdfObject(9).isStream());
        Assert.assertEquals(1, ((PdfDictionary) assertDoc.getPdfObject(9)).getAsNumber(PdfName.N).intValue());
    }

    @Test
    public void checkAndResolveCircularReferences() throws IOException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + "datasheet.pdf"),
                new PdfWriter(DESTINATION_FOLDER + "datasheet_mode.pdf"));
        PdfDictionary pdfObject = (PdfDictionary) pdfDocument.getPdfObject(53);
        pdfDocument.getPage(1).getResources().addForm((PdfStream) pdfObject);
        pdfDocument.close();
        Assert.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + "datasheet_mode.pdf", SOURCE_FOLDER + "cmp_datasheet_mode.pdf",
                        DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void readEncryptedDocumentWithFullCompression() throws IOException {
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "source.pdf",
                new ReaderProperties().setPassword("123".getBytes()));
        PdfDocument pdfDocument = new PdfDocument(reader);

        PdfDictionary form = pdfDocument.getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm);

        PdfDictionary field = form.getAsArray(PdfName.Fields).getAsDictionary(0);

        Assert.assertEquals("ch", field.getAsString(PdfName.T).toUnicodeString());
        Assert.assertEquals("SomeStringValueInDictionary",
                field.getAsDictionary(new PdfName("TestDic")).getAsString(new PdfName("TestString")).toUnicodeString());
        Assert.assertEquals("SomeStringValueInArray",
                field.getAsArray(new PdfName("TestArray")).getAsString(0).toUnicodeString());
        pdfDocument.close();
    }

    @Test
    public void addAssociatedFilesTest01() throws IOException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(DESTINATION_FOLDER + "add_associated_files01.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();
        pdfDocument.addAssociatedFile("af_1", PdfFileSpec
                .createEmbeddedFileSpec(pdfDocument, "Associated File 1".getBytes(), "af_1.txt", PdfName.Data));
        pdfDocument.addNewPage();
        pdfDocument.getFirstPage().addAssociatedFile("af_2", PdfFileSpec
                .createEmbeddedFileSpec(pdfDocument, "Associated File 2".getBytes(), "af_2.txt", PdfName.Data));

        PdfStructTreeRoot root = pdfDocument.getStructTreeRoot();
        root.addAssociatedFile("af_3", PdfFileSpec
                .createEmbeddedFileSpec(pdfDocument, "Associated File 3".getBytes(), "af_3.txt", PdfName.Data));

        PdfFileSpec af5 = PdfFileSpec
                .createEmbeddedFileSpec(pdfDocument, "Associated File 5".getBytes(), "af_5", "af_5.txt", PdfName.Data);
        PdfTextAnnotation textannot = new PdfTextAnnotation(new Rectangle(100, 600, 50, 40));
        textannot.setText(new PdfString("Text Annotation 01")).setContents(new PdfString("Some contents..."));
        textannot.addAssociatedFile(af5);
        pdfDocument.getFirstPage().addAnnotation(textannot);

        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "add_associated_files01.pdf",
                SOURCE_FOLDER + "cmp_add_associated_files01.pdf", DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addAssociatedFilesTest02() throws IOException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(DESTINATION_FOLDER + "add_associated_files02.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();
        PdfCanvas pageCanvas = new PdfCanvas(pdfDocument.addNewPage());

        PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "berlin2013.jpg"));
        imageXObject.addAssociatedFile(PdfFileSpec
                .createEmbeddedFileSpec(pdfDocument, "Associated File 1".getBytes(), "af_1.txt", PdfName.Data));

        pageCanvas.addXObject(imageXObject, 40, 400);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(200, 200));
        PdfCanvas formCanvas = new PdfCanvas(formXObject, pdfDocument);
        formCanvas
                .saveState()
                .circle(100, 100, 50)
                .setColor(ColorConstants.BLACK, true)
                .fill()
                .restoreState();
        formCanvas.release();
        formXObject.addAssociatedFile(PdfFileSpec
                .createEmbeddedFileSpec(pdfDocument, "Associated File 2".getBytes(), "af_2.txt", PdfName.Data));

        pageCanvas.addXObject(formXObject, 40, 100);

        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "add_associated_files02.pdf",
                SOURCE_FOLDER + "cmp_add_associated_files02.pdf", DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void ignoreTagStructureTest() throws IOException {
        String srcFile = SOURCE_FOLDER + "ignoreTagStructureTest.pdf";
        PdfDocument doNotIgnoreTagStructureDocument = new PdfDocument(new PdfReader(srcFile));
        IgnoreTagStructurePdfDocument ignoreTagStructureDocument = new IgnoreTagStructurePdfDocument(
                new PdfReader(srcFile));
        Assert.assertTrue(doNotIgnoreTagStructureDocument.isTagged());
        Assert.assertFalse(ignoreTagStructureDocument.isTagged());

        doNotIgnoreTagStructureDocument.close();
        ignoreTagStructureDocument.close();
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.OUTLINE_DESTINATION_PAGE_NUMBER_IS_OUT_OF_BOUNDS, logLevel = LogLevelConstants.WARN)
    })
    public void removePageWithInvalidOutlineTest() throws IOException, InterruptedException {
        String source = SOURCE_FOLDER + "invalid_outline.pdf";
        String destination = DESTINATION_FOLDER + "invalid_outline.pdf";
        String cmp = SOURCE_FOLDER + "cmp_invalid_outline.pdf";

        PdfDocument document = new PdfDocument(new PdfReader(new FileInputStream(source)),
                new PdfWriter(new FileOutputStream(destination)));

        document.removePage(4);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destination, cmp, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.DOCUMENT_VERSION_IN_CATALOG_CORRUPTED,
                    logLevel = LogLevelConstants.ERROR)
    })
    public void openDocumentWithInvalidCatalogVersionTest() throws IOException {
        try (PdfReader reader = new PdfReader(SOURCE_FOLDER + "sample-with-invalid-catalog-version.pdf");
                PdfDocument pdfDocument = new PdfDocument(reader)) {
            Assert.assertNotNull(pdfDocument);
        }
    }

    @Test
    public void openDocumentWithInvalidCatalogVersionAndConservativeStrictnessReadingTest() throws IOException {
        try (PdfReader reader = new PdfReader(SOURCE_FOLDER + "sample-with-invalid-catalog-version.pdf")
                .setStrictnessLevel(StrictnessLevel.CONSERVATIVE)) {

            junitExpectedException.expect(PdfException.class);
            junitExpectedException.expectMessage(LogMessageConstant.DOCUMENT_VERSION_IN_CATALOG_CORRUPTED);
            new PdfDocument(reader);
        }
    }


    private static class IgnoreTagStructurePdfDocument extends PdfDocument {

        IgnoreTagStructurePdfDocument(PdfReader reader) {
            super(reader);
        }

        @Override
        protected void tryInitTagStructure(PdfDictionary str) {
        }
    }
}
