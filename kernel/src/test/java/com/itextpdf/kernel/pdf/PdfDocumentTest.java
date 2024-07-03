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
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.DeflaterOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfReader.StrictnessLevel;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.xmp.options.SerializeOptions;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleIntegrationTest")
public class PdfDocumentTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/PdfDocumentTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/PdfDocumentTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @Test
    public void missingProducerTest() throws IOException {
        String inputFile = SOURCE_FOLDER + "missingProducer.pdf";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PdfDocument document = new PdfDocument(new PdfReader(inputFile), new PdfWriter(outputStream))) {
            PdfDocumentInfo documentInfo = document.getDocumentInfo();
            Assertions.assertNull(documentInfo.getPdfObject().get(PdfName.Producer));
            Assertions.assertNull(documentInfo.getProducer());
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        try (PdfDocument document = new PdfDocument(new PdfReader(inputStream),
                new PdfWriter(new ByteArrayOutputStream()))) {
            PdfDocumentInfo documentInfo = document.getDocumentInfo();
            Assertions.assertNotNull(documentInfo.getPdfObject().get(PdfName.Producer));
            Assertions.assertNotNull(document.getDocumentInfo().getProducer());
        }
    }

    @Test
    public void nullProducerTest() throws IOException {
        String inputFile = SOURCE_FOLDER + "nullProducer.pdf";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PdfDocument document = new PdfDocument(new PdfReader(inputFile), new PdfWriter(outputStream))) {
            PdfDocumentInfo documentInfo = document.getDocumentInfo();
            Assertions.assertEquals(PdfNull.PDF_NULL, documentInfo.getPdfObject().get(PdfName.Producer));
            Assertions.assertNull(documentInfo.getProducer());
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        try (PdfDocument document = new PdfDocument(new PdfReader(inputStream), new PdfWriter(new ByteArrayOutputStream()))) {
            PdfDocumentInfo documentInfo = document.getDocumentInfo();
            Assertions.assertNotNull(documentInfo.getPdfObject().get(PdfName.Producer));
            Assertions.assertNotNull(document.getDocumentInfo().getProducer());
        }
    }

    @Test
    public void nameProducerTest() throws IOException {
        String inputFile = SOURCE_FOLDER + "nameProducer.pdf";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PdfDocument document = new PdfDocument(new PdfReader(inputFile), new PdfWriter(outputStream))) {
            PdfDocumentInfo documentInfo = document.getDocumentInfo();
            Assertions.assertEquals(new PdfName("producerAsName"), documentInfo.getPdfObject().get(PdfName.Producer));
            Assertions.assertNull(documentInfo.getProducer());
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        try (PdfDocument document = new PdfDocument(new PdfReader(inputStream), new PdfWriter(new ByteArrayOutputStream()))) {
            PdfDocumentInfo documentInfo = document.getDocumentInfo();
            Assertions.assertNotNull(documentInfo.getPdfObject().get(PdfName.Producer));
            Assertions.assertNotNull(document.getDocumentInfo().getProducer());
        }
    }

    @Test
    public void writingVersionTest01() throws IOException {
        // There is a possibility to override version in stamping mode
        String out = DESTINATION_FOLDER + "writing_pdf_version.pdf";

        PdfDocument pdfDoc = new PdfDocument(
                CompareTool.createTestPdfWriter(out, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));

        Assertions.assertEquals(PdfVersion.PDF_2_0, pdfDoc.getPdfVersion());

        pdfDoc.addNewPage();
        pdfDoc.close();

        PdfDocument assertPdfDoc = new PdfDocument(CompareTool.createOutputReader(out));
        Assertions.assertEquals(PdfVersion.PDF_2_0, assertPdfDoc.getPdfVersion());
        assertPdfDoc.close();
    }

    //We have this test in PdfOutlineTest as well, because we had some issues with outlines before. One test worked
    // fine, while another one failed.
    @Test
    public void addOutlinesWithNamedDestinations01() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "outlinesWithNamedDestinations01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "iphone_user_guide.pdf"),
                CompareTool.createTestPdfWriter(filename));
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

        Assertions.assertNull(new CompareTool()
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
        Assertions.assertTrue(dict.getIndirectReference().getObjNumber() > 0);
    }

    @Test
    public void removeUnusedObjectsInWriterModeTest() throws IOException {
        String filename = "removeUnusedObjectsInWriter.pdf";

        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename));

        pdfDocument.addNewPage();

        PdfDictionary unusedDictionary = new PdfDictionary();
        PdfArray unusedArray = (PdfArray) new PdfArray().makeIndirect(pdfDocument);
        unusedArray.add(new PdfNumber(42));
        unusedDictionary.put(new PdfName("testName"), unusedArray);

        unusedDictionary.makeIndirect(pdfDocument);

        Assertions.assertEquals(pdfDocument.getXref().size(), 8);
        //on closing, all unused objects shall not be written to resultant document
        pdfDocument.close();

        PdfDocument testerDocument = new PdfDocument(CompareTool.createOutputReader(DESTINATION_FOLDER + filename));
        Assertions.assertEquals(testerDocument.getXref().size(), 6);
        testerDocument.close();
    }

    @Test
    public void removeUnusedObjectsInStampingModeTest() throws IOException {
        String filenameIn = "docWithUnusedObjects_1.pdf";
        String filenameOut = "removeUnusedObjectsInStamping.pdf";

        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filenameIn));

        pdfDocument.addNewPage();

        PdfDictionary unusedDictionary = new PdfDictionary();
        PdfArray unusedArray = (PdfArray) new PdfArray().makeIndirect(pdfDocument);
        unusedArray.add(new PdfNumber(42));
        unusedDictionary.put(new PdfName("testName"), unusedArray);

        unusedDictionary.makeIndirect(pdfDocument).flush();
        pdfDocument.close();

        PdfDocument doc = new PdfDocument(CompareTool.createOutputReader(DESTINATION_FOLDER + filenameIn),
                CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filenameOut));
        Assertions.assertEquals(doc.getXref().size(), 8);
        //on closing, all unused objects shall not be written to resultant document
        doc.close();

        PdfDocument testerDocument = new PdfDocument(CompareTool.createOutputReader(DESTINATION_FOLDER + filenameOut));
        Assertions.assertEquals(testerDocument.getXref().size(), 6);
        testerDocument.close();
    }


    @Test
    public void addUnusedObjectsInWriterModeTest() throws IOException {
        String filename = "addUnusedObjectsInWriter.pdf";

        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename));

        pdfDocument.addNewPage();

        PdfDictionary unusedDictionary = new PdfDictionary();
        PdfArray unusedArray = (PdfArray) new PdfArray().makeIndirect(pdfDocument);
        unusedArray.add(new PdfNumber(42));
        unusedDictionary.put(new PdfName("testName"), unusedArray);

        unusedDictionary.makeIndirect(pdfDocument);

        Assertions.assertEquals(pdfDocument.getXref().size(), 8);
        pdfDocument.setFlushUnusedObjects(true);
        pdfDocument.close();

        PdfDocument testerDocument = new PdfDocument(CompareTool.createOutputReader(DESTINATION_FOLDER + filename));
        Assertions.assertEquals(testerDocument.getXref().size(), 8);
        testerDocument.close();
    }

    @Test
    public void addUnusedObjectsInStampingModeTest() throws IOException {
        String filenameIn = "docWithUnusedObjects_2.pdf";
        String filenameOut = "addUnusedObjectsInStamping.pdf";

        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filenameIn));

        pdfDocument.addNewPage();

        PdfDictionary unusedDictionary = new PdfDictionary();
        PdfArray unusedArray = (PdfArray) new PdfArray().makeIndirect(pdfDocument);
        unusedArray.add(new PdfNumber(42));
        unusedDictionary.put(new PdfName("testName"), unusedArray);

        unusedDictionary.makeIndirect(pdfDocument).flush();
        pdfDocument.close();

        PdfDocument doc = new PdfDocument(CompareTool.createOutputReader(DESTINATION_FOLDER + filenameIn),
                CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filenameOut));
        Assertions.assertEquals(doc.getXref().size(), 8);
        doc.setFlushUnusedObjects(true);
        doc.close();

        PdfDocument testerDocument = new PdfDocument(CompareTool.createOutputReader(DESTINATION_FOLDER + filenameOut));
        Assertions.assertEquals(testerDocument.getXref().size(), 8);
        testerDocument.close();
    }

    @Test
    public void addUnusedStreamObjectsTest() throws IOException {
        String filenameIn = "docWithUnusedObjects_3.pdf";

        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filenameIn));

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

        PdfDocument testerDocument = new PdfDocument(CompareTool.createOutputReader(DESTINATION_FOLDER + filenameIn));
        Assertions.assertEquals(testerDocument.getXref().size(), 9);
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

        Assertions.assertTrue(byteArrayStream1.size() == byteArrayStream2.size());
        zip.close();
        zip2.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.FLUSHED_OBJECT_CONTAINS_FREE_REFERENCE))
    public void testFreeReference() throws IOException, InterruptedException {
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "freeReference.pdf",
                new WriterProperties().setFullCompressionMode(false));
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + "baseFreeReference.pdf"), writer);
        pdfDocument.getPage(1).getResources().getPdfObject().getAsArray(new PdfName("d")).get(0).getIndirectReference()
                .setFree();
        PdfStream pdfStream = new PdfStream();
        pdfStream.setData(new byte[] {24, 23, 67});
        pdfStream.makeIndirect(pdfDocument);
        pdfDocument.getPage(1).getResources().getPdfObject().getAsArray(new PdfName("d")).add(pdfStream);
        pdfDocument.close();
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + "freeReference.pdf", SOURCE_FOLDER + "cmp_freeReference.pdf",
                        DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void fullCompressionAppendMode() throws IOException, InterruptedException {
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "fullCompressionAppendMode.pdf",
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

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "fullCompressionAppendMode.pdf",
                SOURCE_FOLDER + "cmp_fullCompressionAppendMode.pdf", DESTINATION_FOLDER, "diff_"));

        PdfDocument assertDoc = new PdfDocument(CompareTool.createOutputReader(DESTINATION_FOLDER + "fullCompressionAppendMode.pdf"));
        Assertions.assertTrue(assertDoc.getPdfObject(9).isStream());
        Assertions.assertEquals(1, ((PdfDictionary) assertDoc.getPdfObject(9)).getAsNumber(PdfName.N).intValue());
    }

    @Test
    public void checkAndResolveCircularReferences() throws IOException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + "datasheet.pdf"),
                CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "datasheet_mode.pdf"));
        PdfDictionary pdfObject = (PdfDictionary) pdfDocument.getPdfObject(53);
        pdfDocument.getPage(1).getResources().addForm((PdfStream) pdfObject);
        pdfDocument.close();
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + "datasheet_mode.pdf", SOURCE_FOLDER + "cmp_datasheet_mode.pdf",
                        DESTINATION_FOLDER, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT, 
            ignore = true))
    public void readEncryptedDocumentWithFullCompression() throws IOException {
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "source.pdf",
                new ReaderProperties().setPassword("123".getBytes()));
        PdfDocument pdfDocument = new PdfDocument(reader);

        PdfDictionary form = pdfDocument.getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm);

        PdfDictionary field = form.getAsArray(PdfName.Fields).getAsDictionary(0);

        Assertions.assertEquals("ch", field.getAsString(PdfName.T).toUnicodeString());
        Assertions.assertEquals("SomeStringValueInDictionary",
                field.getAsDictionary(new PdfName("TestDic")).getAsString(new PdfName("TestString")).toUnicodeString());
        Assertions.assertEquals("SomeStringValueInArray",
                field.getAsArray(new PdfName("TestArray")).getAsString(0).toUnicodeString());
        pdfDocument.close();
    }

    @Test
    public void addAssociatedFilesTest01() throws IOException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "add_associated_files01.pdf",
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
        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "add_associated_files01.pdf",
                SOURCE_FOLDER + "cmp_add_associated_files01.pdf", DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void addAssociatedFilesTest02() throws IOException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "add_associated_files02.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();
        PdfCanvas pageCanvas = new PdfCanvas(pdfDocument.addNewPage());

        PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "berlin2013.jpg"));
        imageXObject.addAssociatedFile(PdfFileSpec
                .createEmbeddedFileSpec(pdfDocument, "Associated File 1".getBytes(), "af_1.txt", PdfName.Data));

        pageCanvas.addXObjectAt(imageXObject, 40, 400);

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

        pageCanvas.addXObjectAt(formXObject, 40, 100);

        pdfDocument.close();
        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "add_associated_files02.pdf",
                SOURCE_FOLDER + "cmp_add_associated_files02.pdf", DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void ignoreTagStructureTest() throws IOException {
        String srcFile = SOURCE_FOLDER + "ignoreTagStructureTest.pdf";
        PdfDocument doNotIgnoreTagStructureDocument = new PdfDocument(new PdfReader(srcFile));
        IgnoreTagStructurePdfDocument ignoreTagStructureDocument = new IgnoreTagStructurePdfDocument(
                new PdfReader(srcFile));
        Assertions.assertTrue(doNotIgnoreTagStructureDocument.isTagged());
        Assertions.assertFalse(ignoreTagStructureDocument.isTagged());

        doNotIgnoreTagStructureDocument.close();
        ignoreTagStructureDocument.close();
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.OUTLINE_DESTINATION_PAGE_NUMBER_IS_OUT_OF_BOUNDS, logLevel = LogLevelConstants.WARN)
    })
    public void removePageWithInvalidOutlineTest() throws IOException, InterruptedException {
        String source = SOURCE_FOLDER + "invalid_outline.pdf";
        String destination = DESTINATION_FOLDER + "invalid_outline.pdf";
        String cmp = SOURCE_FOLDER + "cmp_invalid_outline.pdf";

        PdfDocument document = new PdfDocument(new PdfReader(FileUtil.getInputStreamForFile(source)),
                CompareTool.createTestPdfWriter(destination));

        document.removePage(4);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destination, cmp, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_VERSION_IN_CATALOG_CORRUPTED,
                    logLevel = LogLevelConstants.ERROR)
    })
    public void openDocumentWithInvalidCatalogVersionTest() throws IOException {
        try (PdfReader reader = new PdfReader(SOURCE_FOLDER + "sample-with-invalid-catalog-version.pdf");
                PdfDocument pdfDocument = new PdfDocument(reader)) {
            Assertions.assertNotNull(pdfDocument);
        }
    }

    @Test
    public void openDocumentWithInvalidCatalogVersionAndConservativeStrictnessReadingTest() throws IOException {
        try (PdfReader reader = new PdfReader(SOURCE_FOLDER + "sample-with-invalid-catalog-version.pdf")
                .setStrictnessLevel(StrictnessLevel.CONSERVATIVE)) {

            Exception e = Assertions.assertThrows(PdfException.class,
                    () -> new PdfDocument(reader)
            );
            Assertions.assertEquals(IoLogMessageConstant.DOCUMENT_VERSION_IN_CATALOG_CORRUPTED, e.getMessage());
        }
    }

    @Test
    public void widgetDaEntryRemovePageTest() throws IOException, InterruptedException {
        final String testName = "widgetDaEntryRemovePage.pdf";
        final String outPdf = DESTINATION_FOLDER + testName;
        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "widgetWithDaEntry.pdf"), CompareTool.createTestPdfWriter(outPdf))) {
            pdfDocument.removePage(3);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, SOURCE_FOLDER + "cmp_" + testName,
                DESTINATION_FOLDER));
    }

    @Test
    public void mergedAndSimpleWidgetsRemovePageTest() throws IOException, InterruptedException {
        final String testName = "mergedAndSimpleWidgetsRemovePage.pdf";
        final String outPdf = DESTINATION_FOLDER + testName;
        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "mergedAndSimpleWidgets.pdf"), CompareTool.createTestPdfWriter(outPdf))) {
            pdfDocument.removePage(1);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, SOURCE_FOLDER + "cmp_" + testName,
                DESTINATION_FOLDER));
    }

    @Test
    public void mergedSiblingWidgetsRemovePageTest() throws IOException, InterruptedException {
        final String testName = "mergedSiblingWidgetsRemovePage.pdf";
        final String outPdf = DESTINATION_FOLDER + testName;
        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(SOURCE_FOLDER + "mergedSiblingWidgets.pdf"), CompareTool.createTestPdfWriter(outPdf))) {
            pdfDocument.removePage(2);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, SOURCE_FOLDER + "cmp_" + testName,
                DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE))
    public void rootCannotBeReferenceFromTrailerTest() throws IOException {
        String filename = SOURCE_FOLDER + "rootCannotBeReferenceFromTrailerTest.pdf";
        PdfReader corruptedReader = new PdfReader(filename);
        Exception e = Assertions.assertThrows(PdfException.class, () -> new PdfDocument(corruptedReader));
        Assertions.assertEquals(KernelExceptionMessageConstant.CORRUPTED_ROOT_ENTRY_IN_TRAILER, e.getMessage());
    }

    @Test
    public void setSerializeOptionsTest() {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        SerializeOptions options = new SerializeOptions().setUseCanonicalFormat(true);
        document.setSerializeOptions(options);
        Assertions.assertEquals(options, document.getSerializeOptions());
    }

    @Test
    public void getDiContainer() {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Assertions.assertNotNull(document.getDiContainer());
    }

    @Test
    public void getDefaultConformanceLevelTest() {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Assertions.assertNull(document.getConformanceLevel());
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
