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
package com.itextpdf.pdfua.checkers;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfScreenAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.utils.ValidationContainer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.pdfua.PdfUATestPdfDocument;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
public class PdfUATest extends ExtendedITextTest {

    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUATest/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfua/PdfUATest/";

    private static final String DOG = "./src/test/resources/com/itextpdf/pdfua/img/DOG.bmp";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";
    private static final String FOX = "./src/test/resources/com/itextpdf/pdfua/img/FOX.bmp";

    private UaValidationTestFramework framework;

    @BeforeClass
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Before
    public void initializeFramework() {
        framework = new UaValidationTestFramework(DESTINATION_FOLDER);
    }


    @Test
    public void checkPoint01_007_suspectsHasEntryTrue() {
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), PdfUATestPdfDocument.createWriterProperties()));
        PdfDictionary markInfo = (PdfDictionary) pdfDoc.getCatalog().getPdfObject().get(PdfName.MarkInfo);
        Assert.assertNotNull(markInfo);
        markInfo.put(PdfName.Suspects, new PdfBoolean(true));
        Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assert.assertEquals(PdfUAExceptionMessageConstants.SUSPECTS_ENTRY_IN_MARK_INFO_DICTIONARY_SHALL_NOT_HAVE_A_VALUE_OF_TRUE,
                e.getMessage());
    }


    @Test
    public void checkPoint01_007_suspectsHasEntryFalse() {
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), PdfUATestPdfDocument.createWriterProperties()));
        PdfDictionary markInfo = (PdfDictionary) pdfDoc.getCatalog().getPdfObject().get(PdfName.MarkInfo);
        markInfo.put(PdfName.Suspects, new PdfBoolean(false));
        AssertUtil.doesNotThrow(() -> pdfDoc.close());
    }

    @Test
    public void checkPoint01_007_suspectsHasNoEntry() {
        // suspects entry is optional so it is ok to not have it according to the spec
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), PdfUATestPdfDocument.createWriterProperties()));
        AssertUtil.doesNotThrow(() -> pdfDoc.close());
    }


    @Test
    public void emptyPageDocument() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "emptyPageDocument.pdf";
        try (PdfDocument pdfDocument = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()))) {
            pdfDocument.addNewPage();
        }
        Assert.assertNull(new CompareTool().compareByContent(outPdf, SOURCE_FOLDER + "cmp_emptyPageDocument.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void invalidUA1DocumentWithFlushedPageTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "invalidDocWithFlushedPageTest.pdf";
        try (PdfDocument pdfDocument = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()))) {
            PdfPage page = pdfDocument.addNewPage();
            PdfFileSpec spec = PdfFileSpec.createExternalFileSpec(pdfDocument, "sample.wav");
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            PdfAction action = PdfAction.createRendition("sample.wav",
                    spec, "audio/x-wav", screen);
            screen.setAction(action);
            screen.setContents("screen annotation");
            page.addAnnotation(screen);
            AssertUtil.doesNotThrow(() -> {
                page.flush();
            });
        }
        Assert.assertNull(new CompareTool().compareByContent(outPdf, SOURCE_FOLDER + "cmp_invalidDocWithFlushedPageTest.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assert.assertNotNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void documentWithNoLangEntryTest() throws IOException {
        final String outPdf = DESTINATION_FOLDER + "documentWithNoLangEntryTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf,
                new WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)));
        pdfDoc.setTagged();
        ValidationContainer validationContainer = new ValidationContainer();
        validationContainer.addChecker(new PdfUA1Checker(pdfDoc));
        pdfDoc.getDiContainer().register(ValidationContainer.class, validationContainer);

        pdfDoc.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
        PdfDocumentInfo info = pdfDoc.getDocumentInfo();
        info.setTitle("English pangram");

        Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assert.assertEquals(PdfUAExceptionMessageConstants.DOCUMENT_SHALL_CONTAIN_VALID_LANG_ENTRY,
                e.getMessage());
    }

    @Test
    public void documentWithEmptyStringLangEntryTest() throws IOException {
        final String outPdf = DESTINATION_FOLDER + "documentWithEmptyStringLangEntryTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf,
                new WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)));
        pdfDoc.setTagged();
        ValidationContainer validationContainer = new ValidationContainer();
        validationContainer.addChecker(new PdfUA1Checker(pdfDoc));
        pdfDoc.getDiContainer().register(ValidationContainer.class, validationContainer);
        pdfDoc.getCatalog().setLang(new PdfString(""));
        pdfDoc.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
        PdfDocumentInfo info = pdfDoc.getDocumentInfo();
        info.setTitle("English pangram");
        Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assert.assertEquals(PdfUAExceptionMessageConstants.DOCUMENT_SHALL_CONTAIN_VALID_LANG_ENTRY,
                e.getMessage());
    }

    @Test
    public void documentWithComplexLangEntryTest() throws IOException, InterruptedException {
        final String outPdf = DESTINATION_FOLDER + "documentWithComplexLangEntryTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf,
                new WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)));
        pdfDoc.setTagged();
        ValidationContainer validationContainer = new ValidationContainer();
        validationContainer.addChecker(new PdfUA1Checker(pdfDoc));
        pdfDoc.getDiContainer().register(ValidationContainer.class, validationContainer);
        pdfDoc.getCatalog().setLang(new PdfString("qaa-Qaaa-QM-x-southern"));
        pdfDoc.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
        PdfDocumentInfo info = pdfDoc.getDocumentInfo();
        info.setTitle("English pangram");
        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(outPdf, SOURCE_FOLDER + "cmp_documentWithComplexLangEntryTest.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void documentWithoutViewerPreferencesTest() throws IOException {
        final String outPdf = DESTINATION_FOLDER + "documentWithoutViewerPreferencesTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf,
                new WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)));
        pdfDoc.setTagged();
        ValidationContainer validationContainer = new ValidationContainer();
        validationContainer.addChecker(new PdfUA1Checker(pdfDoc));
        pdfDoc.getDiContainer().register(ValidationContainer.class, validationContainer);

        pdfDoc.getCatalog().setLang(new PdfString("en-US"));
        PdfDocumentInfo info = pdfDoc.getDocumentInfo();
        info.setTitle("English pangram");

        Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assert.assertEquals(PdfUAExceptionMessageConstants.MISSING_VIEWER_PREFERENCES,
                e.getMessage());
    }

    @Test
    public void documentWithEmptyViewerPreferencesTest() throws IOException {
        final String outPdf = DESTINATION_FOLDER + "documentWithEmptyViewerPreferencesTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf,
                new WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)));
        pdfDoc.setTagged();
        ValidationContainer validationContainer = new ValidationContainer();
        validationContainer.addChecker(new PdfUA1Checker(pdfDoc));
        pdfDoc.getDiContainer().register(ValidationContainer.class, validationContainer);

        pdfDoc.getCatalog().setViewerPreferences(new PdfViewerPreferences());
        pdfDoc.getCatalog().setLang(new PdfString("en-US"));
        PdfDocumentInfo info = pdfDoc.getDocumentInfo();
        info.setTitle("English pangram");

        Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assert.assertEquals(PdfUAExceptionMessageConstants.MISSING_VIEWER_PREFERENCES,
                e.getMessage());
    }

    @Test
    public void documentWithInvalidViewerPreferencesTest() throws IOException {
        final String outPdf = DESTINATION_FOLDER + "documentWithEmptyViewerPreferencesTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf,
                new WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)));
        pdfDoc.setTagged();
        ValidationContainer validationContainer = new ValidationContainer();
        validationContainer.addChecker(new PdfUA1Checker(pdfDoc));
        pdfDoc.getDiContainer().register(ValidationContainer.class, validationContainer);

        pdfDoc.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(false));
        pdfDoc.getCatalog().setLang(new PdfString("en-US"));
        PdfDocumentInfo info = pdfDoc.getDocumentInfo();
        info.setTitle("English pangram");

        Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assert.assertEquals(PdfUAExceptionMessageConstants.VIEWER_PREFERENCES_IS_FALSE,
                e.getMessage());
    }

    @Test
    public void checkNameEntryShouldPresentInAllOCGDictionariesTest() throws FileNotFoundException {
        framework.addBeforeGenerationHook((pdfDocument) -> {
            pdfDocument.addNewPage();
            PdfDictionary ocProperties = new PdfDictionary();
            PdfDictionary d = new PdfDictionary();
            PdfArray configs = new PdfArray();
            PdfDictionary config = new PdfDictionary();
            config.put(PdfName.Name, new PdfString("CustomName"));
            configs.add(config);
            ocProperties.put(PdfName.D, d);
            ocProperties.put(PdfName.Configs, configs);
            pdfDocument.getCatalog().put(PdfName.OCProperties, ocProperties);
        });
        framework.assertBothFail("pdfuaOCGPropertiesCheck01",
                PdfUAExceptionMessageConstants.NAME_ENTRY_IS_MISSING_OR_EMPTY_IN_OCG);
    }

    @Test
    public void checkAsKeyInContentConfigDictTest() throws FileNotFoundException {
        framework.addBeforeGenerationHook((pdfDocument) -> {
            pdfDocument.addNewPage();
            PdfDictionary ocProperties = new PdfDictionary();
            PdfArray configs = new PdfArray();
            PdfDictionary config = new PdfDictionary();
            config.put(PdfName.Name, new PdfString("CustomName"));
            config.put(PdfName.AS, new PdfArray());
            configs.add(config);
            ocProperties.put(PdfName.Configs, configs);
            pdfDocument.getCatalog().put(PdfName.OCProperties, ocProperties);
        });
        framework.assertBothFail("pdfuaOCGPropertiesCheck02",
                PdfUAExceptionMessageConstants.OCG_SHALL_NOT_CONTAIN_AS_ENTRY);
    }

    @Test
    public void nameEntryisEmptyTest() throws FileNotFoundException {
        framework.addBeforeGenerationHook((pdfDocument) -> {
            PdfDictionary ocProperties = new PdfDictionary();
            PdfDictionary d = new PdfDictionary();
            d.put(PdfName.Name, new PdfString(""));
            PdfArray configs = new PdfArray();
            PdfDictionary config = new PdfDictionary();
            config.put(PdfName.Name, new PdfString(""));
            configs.add(config);
            ocProperties.put(PdfName.D, d);
            ocProperties.put(PdfName.Configs, configs);

            pdfDocument.getCatalog().put(PdfName.OCProperties, ocProperties);
        });
        framework.assertBothFail("pdfuaOCGPropertiesCheck03",
                PdfUAExceptionMessageConstants.NAME_ENTRY_IS_MISSING_OR_EMPTY_IN_OCG);
    }

    @Test
    public void configsEntryisNotAnArrayTest() throws FileNotFoundException {
        framework.addBeforeGenerationHook((pdfDocument) -> {
            PdfDictionary ocProperties = new PdfDictionary();
            PdfDictionary d = new PdfDictionary();
            d.put(PdfName.Name, new PdfString(""));
            PdfDictionary configs = new PdfDictionary();
            ocProperties.put(PdfName.D, d);
            ocProperties.put(PdfName.Configs, configs);

            pdfDocument.getCatalog().put(PdfName.OCProperties, ocProperties);
        });
        framework.assertBothFail("pdfuaOCGPropertiesCheck04",
                PdfUAExceptionMessageConstants.OCG_PROPERTIES_CONFIG_SHALL_BE_AN_ARRAY);
    }

    @Test
    public void nameEntryShouldBeUniqueBetweenDefaultAndAdditionalConfigsTest() throws IOException, InterruptedException {
        framework.addBeforeGenerationHook((pdfDocument) -> {
            PdfDictionary ocProperties = new PdfDictionary();
            PdfDictionary d = new PdfDictionary();
            d.put(PdfName.Name, new PdfString("CustomName"));
            PdfArray configs = new PdfArray();
            PdfDictionary config = new PdfDictionary();
            config.put(PdfName.Name, new PdfString("CustomName"));
            configs.add(config);
            ocProperties.put(PdfName.D, d);
            ocProperties.put(PdfName.Configs, configs);

            pdfDocument.getCatalog().put(PdfName.OCProperties, ocProperties);
        });
        framework.assertBothValid("pdfuaOCGPropertiesCheck");
    }

    @Test
    public void validOCGsTest() throws IOException, InterruptedException {
        framework.addBeforeGenerationHook((pdfDocument) -> {
            PdfDictionary ocProperties = new PdfDictionary();
            PdfDictionary d = new PdfDictionary();
            d.put(PdfName.Name, new PdfString("CustomName"));
            PdfArray configs = new PdfArray();
            PdfArray ocgs = new PdfArray();
            PdfDictionary config = new PdfDictionary();
            config.put(PdfName.Name, new PdfString("CustomName"));
            configs.add(config);
            PdfDictionary ocg = new PdfDictionary();
            ocg.put(PdfName.Name, new PdfString("CustomName"));
            ocgs.add(ocg);
            ocProperties.put(PdfName.D, d);
            ocProperties.put(PdfName.Configs, configs);
            ocProperties.put(PdfName.OCGs, configs);

            pdfDocument.getCatalog().put(PdfName.OCProperties, ocProperties);
        });
        framework.assertBothValid("pdfuaOCGsPropertiesCheck");
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.NAME_ALREADY_EXISTS_IN_THE_NAME_TREE, count = 1)})
    public void documentWithDuplicatingIdInStructTree() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(os, PdfUATestPdfDocument.createWriterProperties());
        PdfDocument document = new PdfUATestPdfDocument(writer);

        PdfPage page1 = document.addNewPage();
        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.setPageForTagging(page1);

        PdfCanvas canvas = new PdfCanvas(page1);

        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.PREFER_EMBEDDED);
        canvas.beginText()
                .setFontAndSize(font, 12)
                .setTextMatrix(1, 0, 0, 1, 32, 512);

        DefaultAccessibilityProperties paraProps
                = new DefaultAccessibilityProperties(StandardRoles.P);
        tagPointer.addTag(paraProps).addTag(StandardRoles.SPAN);

        tagPointer.getProperties().setStructureElementIdString("hello-element");
        canvas.openTag(tagPointer.getTagReference())
                .showText("Hello ")
                .closeTag();
        tagPointer.moveToParent().addTag(StandardRoles.SPAN);

        tagPointer.getProperties().setStructureElementIdString("world-element");

        Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> tagPointer.getProperties().setStructureElementIdString("hello-element"));
        Assert.assertEquals(MessageFormatUtil.format(
                PdfUAExceptionMessageConstants.NON_UNIQUE_ID_ENTRY_IN_STRUCT_TREE_ROOT, "hello-element"),
                e.getMessage());
    }

    @Test
    public void openDocumentWithDuplicatingIdInStructTree() throws IOException {
        String source = SOURCE_FOLDER + "documentWithDuplicatingIdsInStructTree.pdf";
        try (PdfDocument pdfDocument = new PdfUATestPdfDocument(
                new PdfReader(new File(source)))) {
        }
        //Vera pdf doesn't complain on this document
        Assert.assertNull(new VeraPdfValidator().validate(source)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void manualPdfUaCreation() throws IOException, InterruptedException {

        final String outPdf = DESTINATION_FOLDER + "manualPdfUaCreation.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf,
                new WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)));
        Document document = new Document(pdfDoc, PageSize.A4.rotate());

        //TAGGED PDF
        //Make document tagged
        pdfDoc.setTagged();

        //PDF/UA
        //Set document metadata
        pdfDoc.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
        pdfDoc.getCatalog().setLang(new PdfString("en-US"));
        PdfDocumentInfo info = pdfDoc.getDocumentInfo();
        info.setTitle("English pangram");

        Paragraph p = new Paragraph();

        //PDF/UA
        //Embed font
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.PREFER_EMBEDDED);
        p.setFont(font);

        p.add("The quick brown ");

        Image img = new Image(ImageDataFactory.create(FOX));

        //PDF/UA
        //Set alt text
        img.getAccessibilityProperties().setAlternateDescription("Fox");
        p.add(img);
        p.add(" jumps over the lazy ");

        img = new Image(ImageDataFactory.create(DOG));

        //PDF/UA
        //Set alt text
        img.getAccessibilityProperties().setAlternateDescription("Dog");
        p.add(img);

        document.add(p);

        p = new Paragraph("\n\n\n\n\n\n\n\n\n\n\n\n").setFont(font).setFontSize(20);
        document.add(p);

        List list = new List().setFont(font).setFontSize(20);
        list.add(new ListItem("quick"));
        list.add(new ListItem("brown"));
        list.add(new ListItem("fox"));
        list.add(new ListItem("jumps"));
        list.add(new ListItem("over"));
        list.add(new ListItem("the"));
        list.add(new ListItem("lazy"));
        list.add(new ListItem("dog"));
        document.add(list);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf, SOURCE_FOLDER + "cmp_manualPdfUaCreation.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }
}
