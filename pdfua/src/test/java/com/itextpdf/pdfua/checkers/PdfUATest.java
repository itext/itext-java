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
package com.itextpdf.pdfua.checkers;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.Pdf20ConformanceException;
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
import com.itextpdf.kernel.pdf.PdfUAConformance;
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
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.validation.ValidationContainer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.pdfua.PdfUAConfig;
import com.itextpdf.pdfua.PdfUADocument;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.pdfua.logs.PdfUALogMessageConstants;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class PdfUATest extends ExtendedITextTest {

    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/pdfua/PdfUATest/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfua/PdfUATest/";

    private static final String DOG = "./src/test/resources/com/itextpdf/pdfua/img/DOG.bmp";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";
    private static final String FOX = "./src/test/resources/com/itextpdf/pdfua/img/FOX.bmp";

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public static java.util.List<PdfUAConformance> data() {
        return UaValidationTestFramework.getConformanceList();
    }

    @Test
    public void checkPoint01_007_suspectsHasEntryTrue() throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfDictionary markInfo = (PdfDictionary) pdfDoc.getCatalog().getPdfObject().get(PdfName.MarkInfo);
            Assertions.assertNotNull(markInfo);
            markInfo.put(PdfName.Suspects, new PdfBoolean(true));
        });
        framework.assertBothFail("suspectsHasEntryTrue",
                PdfUAExceptionMessageConstants.SUSPECTS_ENTRY_IN_MARK_INFO_DICTIONARY_SHALL_NOT_HAVE_A_VALUE_OF_TRUE,
                PdfUAConformance.PDF_UA_1);
    }


    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint01_007_suspectsHasEntryFalse(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfDictionary markInfo = (PdfDictionary) pdfDoc.getCatalog().getPdfObject().get(PdfName.MarkInfo);
            markInfo.put(PdfName.Suspects, new PdfBoolean(false));
        });
        framework.assertBothValid("suspectsHasEntryFalse", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkPoint01_007_suspectsHasNoEntry(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        // suspects entry is optional so it is ok to not have it according to the spec
        framework.assertBothValid("suspectsHasNoEntry", pdfUAConformance);
    }


    @ParameterizedTest
    @MethodSource("data")
    public void emptyPageDocument(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDocument -> {
            pdfDocument.addNewPage();
        });
        framework.assertBothValid("emptyPageDocument", pdfUAConformance);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = PdfUALogMessageConstants.PAGE_FLUSHING_DISABLED, count = 2)})
    public void invalidUA1DocumentWithFlushedPageTest() throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfPage page = pdfDocument.addNewPage();
            PdfFileSpec spec = PdfFileSpec.createExternalFileSpec(pdfDocument, "sample.wav");
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            PdfAction action = PdfAction.createRendition("sample.wav", spec, "audio/x-wav", screen);
            screen.setAction(action);
            screen.setContents("screen annotation");
            page.addAnnotation(screen);
            AssertUtil.doesNotThrow(() -> {
                page.flush();
            });
        });
        framework.assertBothFail("invalidDocWithFlushedPage", PdfUAConformance.PDF_UA_1);
    }

    @Test
    public void documentWithNoLangEntryTest() throws IOException {
        final String outPdf = DESTINATION_FOLDER + "documentWithNoLangEntryTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf, new WriterProperties()
                .addPdfUaXmpMetadata(PdfUAConformance.PDF_UA_1).setPdfVersion(PdfVersion.PDF_1_7)));
        pdfDoc.setTagged();
        ValidationContainer validationContainer = new ValidationContainer();
        validationContainer.addChecker(new PdfUA1Checker(pdfDoc));
        pdfDoc.getDiContainer().register(ValidationContainer.class, validationContainer);

        pdfDoc.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
        PdfDocumentInfo info = pdfDoc.getDocumentInfo();
        info.setTitle("English pangram");

        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(PdfUAExceptionMessageConstants.CATALOG_SHOULD_CONTAIN_LANG_ENTRY,
                e.getMessage());
    }

    @Test
    public void documentWithNoLangEntryUA2Test() throws IOException {
        final String outPdf = DESTINATION_FOLDER + "documentWithNoLangEntryUA2Test.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf, new WriterProperties()
                .addPdfUaXmpMetadata(PdfUAConformance.PDF_UA_2).setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDoc.setTagged();
        ValidationContainer validationContainer = new ValidationContainer();
        validationContainer.addChecker(new PdfUA2Checker(pdfDoc));
        pdfDoc.getDiContainer().register(ValidationContainer.class, validationContainer);

        pdfDoc.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
        PdfDocumentInfo info = pdfDoc.getDocumentInfo();
        info.setTitle("English pangram");

        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(PdfUAExceptionMessageConstants.CATALOG_SHOULD_CONTAIN_LANG_ENTRY,
                e.getMessage());
    }

    @Test
    public void documentWithEmptyStringLangEntryTest() throws IOException {
        final String outPdf = DESTINATION_FOLDER + "documentWithEmptyStringLangEntryTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf,
                new WriterProperties().addPdfUaXmpMetadata(PdfUAConformance.PDF_UA_1).setPdfVersion(PdfVersion.PDF_1_7)));
        pdfDoc.setTagged();
        ValidationContainer validationContainer = new ValidationContainer();
        validationContainer.addChecker(new PdfUA1Checker(pdfDoc));
        pdfDoc.getDiContainer().register(ValidationContainer.class, validationContainer);
        pdfDoc.getCatalog().setLang(new PdfString(""));
        pdfDoc.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
        PdfDocumentInfo info = pdfDoc.getDocumentInfo();
        info.setTitle("English pangram");
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(PdfUAExceptionMessageConstants.DOCUMENT_SHALL_CONTAIN_VALID_LANG_ENTRY,
                e.getMessage());
    }

    @Test
    public void documentWithEmptyStringLangEntryUA2Test() throws IOException {
        final String outPdf = DESTINATION_FOLDER + "documentWithEmptyStringLangEntryTestUA2.pdf";
        PdfDocument pdfDoc = new PdfUADocument(new PdfWriter(outPdf, new WriterProperties()
                .addPdfUaXmpMetadata(PdfUAConformance.PDF_UA_2).setPdfVersion(PdfVersion.PDF_2_0)),
                new PdfUAConfig(PdfUAConformance.PDF_UA_2, "English pangram", ""));
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(PdfUAExceptionMessageConstants.DOCUMENT_SHALL_CONTAIN_VALID_LANG_ENTRY,
                e.getMessage());
    }

    @Test
    public void documentWithInvalidLangEntryUA2Test() throws IOException {
        final String outPdf = DESTINATION_FOLDER + "documentWithInvalidLangEntryUA2Test.pdf";
        PdfDocument pdfDoc = new PdfUADocument(new PdfWriter(outPdf, new WriterProperties()
                .addPdfUaXmpMetadata(PdfUAConformance.PDF_UA_2).setPdfVersion(PdfVersion.PDF_2_0)),
                new PdfUAConfig(PdfUAConformance.PDF_UA_2, "English pangram", "inv:alid"));
        Exception e = Assertions.assertThrows(Pdf20ConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(KernelExceptionMessageConstant.DOCUMENT_SHALL_CONTAIN_VALID_LANG_ENTRY,
                e.getMessage());
    }

    @Test
    public void documentWithComplexLangEntryTest() throws IOException {
        final String outPdf = DESTINATION_FOLDER + "documentWithComplexLangEntryTest.pdf";
        PdfDocument pdfDoc = new PdfUADocument(new PdfWriter(outPdf), new PdfUAConfig(PdfUAConformance.PDF_UA_1, "English pangram", "qaa-Qaaa-QM-x-southern"));
        pdfDoc.close();

        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void documentWithoutViewerPreferencesTest() throws IOException {
        final String outPdf = DESTINATION_FOLDER + "documentWithoutViewerPreferencesTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf,
                new WriterProperties().addPdfUaXmpMetadata(PdfUAConformance.PDF_UA_1).setPdfVersion(PdfVersion.PDF_1_7)));
        pdfDoc.setTagged();
        ValidationContainer validationContainer = new ValidationContainer();
        validationContainer.addChecker(new PdfUA1Checker(pdfDoc));
        pdfDoc.getDiContainer().register(ValidationContainer.class, validationContainer);

        pdfDoc.getCatalog().setLang(new PdfString("en-US"));
        PdfDocumentInfo info = pdfDoc.getDocumentInfo();
        info.setTitle("English pangram");

        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(PdfUAExceptionMessageConstants.MISSING_VIEWER_PREFERENCES,
                e.getMessage());
    }

    @Test
    public void documentWithoutViewerPreferencesUA2Test() throws IOException {
        final String outPdf = DESTINATION_FOLDER + "documentWithoutViewerPreferencesUA2Test.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf, new WriterProperties()
                .addPdfUaXmpMetadata(PdfUAConformance.PDF_UA_2).setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDoc.setTagged();
        ValidationContainer validationContainer = new ValidationContainer();
        validationContainer.addChecker(new PdfUA2Checker(pdfDoc));
        pdfDoc.getDiContainer().register(ValidationContainer.class, validationContainer);

        pdfDoc.getCatalog().setLang(new PdfString("en-US"));
        PdfDocumentInfo info = pdfDoc.getDocumentInfo();
        info.setTitle("English pangram");

        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(PdfUAExceptionMessageConstants.MISSING_VIEWER_PREFERENCES,
                e.getMessage());
    }

    @Test
    public void documentWithEmptyViewerPreferencesTest() throws IOException {
        final String outPdf = DESTINATION_FOLDER + "documentWithEmptyViewerPreferencesTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf,
                new WriterProperties().addPdfUaXmpMetadata(PdfUAConformance.PDF_UA_1).setPdfVersion(PdfVersion.PDF_1_7)));
        pdfDoc.setTagged();
        ValidationContainer validationContainer = new ValidationContainer();
        validationContainer.addChecker(new PdfUA1Checker(pdfDoc));
        pdfDoc.getDiContainer().register(ValidationContainer.class, validationContainer);

        pdfDoc.getCatalog().setViewerPreferences(new PdfViewerPreferences());
        pdfDoc.getCatalog().setLang(new PdfString("en-US"));
        PdfDocumentInfo info = pdfDoc.getDocumentInfo();
        info.setTitle("English pangram");

        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(PdfUAExceptionMessageConstants.MISSING_VIEWER_PREFERENCES,
                e.getMessage());
    }

    @Test
    public void documentWithEmptyViewerPreferencesUA2Test() throws IOException {
        final String outPdf = DESTINATION_FOLDER + "documentWithEmptyViewerPreferencesUA2Test.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf, new WriterProperties()
                .addPdfUaXmpMetadata(PdfUAConformance.PDF_UA_2).setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDoc.setTagged();
        ValidationContainer validationContainer = new ValidationContainer();
        validationContainer.addChecker(new PdfUA2Checker(pdfDoc));
        pdfDoc.getDiContainer().register(ValidationContainer.class, validationContainer);

        pdfDoc.getCatalog().setViewerPreferences(new PdfViewerPreferences());
        pdfDoc.getCatalog().setLang(new PdfString("en-US"));
        PdfDocumentInfo info = pdfDoc.getDocumentInfo();
        info.setTitle("English pangram");

        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(PdfUAExceptionMessageConstants.MISSING_VIEWER_PREFERENCES,
                e.getMessage());
    }

    @Test
    public void documentWithInvalidViewerPreferencesTest() throws IOException {
        final String outPdf = DESTINATION_FOLDER + "documentWithInvalidViewerPreferencesTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf,
                new WriterProperties().addPdfUaXmpMetadata(PdfUAConformance.PDF_UA_1).setPdfVersion(PdfVersion.PDF_1_7)));
        pdfDoc.setTagged();
        ValidationContainer validationContainer = new ValidationContainer();
        validationContainer.addChecker(new PdfUA1Checker(pdfDoc));
        pdfDoc.getDiContainer().register(ValidationContainer.class, validationContainer);

        pdfDoc.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(false));
        pdfDoc.getCatalog().setLang(new PdfString("en-US"));
        PdfDocumentInfo info = pdfDoc.getDocumentInfo();
        info.setTitle("English pangram");

        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(PdfUAExceptionMessageConstants.VIEWER_PREFERENCES_IS_FALSE,
                e.getMessage());
    }

    @Test
    public void documentWithInvalidViewerPreferencesUA2Test() throws IOException {
        final String outPdf = DESTINATION_FOLDER + "documentWithInvalidViewerPreferencesUA2Test.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf, new WriterProperties()
                .addPdfUaXmpMetadata(PdfUAConformance.PDF_UA_2).setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDoc.setTagged();
        ValidationContainer validationContainer = new ValidationContainer();
        validationContainer.addChecker(new PdfUA2Checker(pdfDoc));
        pdfDoc.getDiContainer().register(ValidationContainer.class, validationContainer);

        pdfDoc.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(false));
        pdfDoc.getCatalog().setLang(new PdfString("en-US"));
        PdfDocumentInfo info = pdfDoc.getDocumentInfo();
        info.setTitle("English pangram");

        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(PdfUAExceptionMessageConstants.VIEWER_PREFERENCES_IS_FALSE,
                e.getMessage());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkNameEntryShouldPresentInAllOCGDictionariesTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDocument -> {
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
                PdfUAExceptionMessageConstants.NAME_ENTRY_IS_MISSING_OR_EMPTY_IN_OCG, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void checkAsKeyInContentConfigDictTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDocument -> {
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
                PdfUAExceptionMessageConstants.OCG_SHALL_NOT_CONTAIN_AS_ENTRY, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void nameEntryIsEmptyTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDocument -> {
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
                PdfUAExceptionMessageConstants.NAME_ENTRY_IS_MISSING_OR_EMPTY_IN_OCG, pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void configsEntryIsNotAnArrayTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfDictionary ocProperties = new PdfDictionary();
            PdfDictionary d = new PdfDictionary();
            d.put(PdfName.Name, new PdfString(""));
            PdfDictionary configs = new PdfDictionary();
            ocProperties.put(PdfName.D, d);
            ocProperties.put(PdfName.Configs, configs);

            pdfDocument.getCatalog().put(PdfName.OCProperties, ocProperties);
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertBothFail("pdfuaOCGPropertiesCheck04",
                    PdfUAExceptionMessageConstants.OCG_PROPERTIES_CONFIG_SHALL_BE_AN_ARRAY, pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothValid("pdfuaOCGPropertiesCheck04", pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void nameEntryShouldBeUniqueBetweenDefaultAndAdditionalConfigsTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDocument -> {
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
        framework.assertBothValid("pdfuaOCGPropertiesCheck", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void validOCGsTest(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDocument -> {
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
        framework.assertBothValid("pdfuaOCGsPropertiesCheck", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.NAME_ALREADY_EXISTS_IN_THE_NAME_TREE, count = 1, ignore = true)})
    public void documentWithDuplicatingIdInStructTree(PdfUAConformance pdfUAConformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER);
        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfPage page1 = pdfDocument.addNewPage();
            TagTreePointer tagPointer = new TagTreePointer(pdfDocument);
            tagPointer.setPageForTagging(page1);

            PdfCanvas canvas = new PdfCanvas(page1);

            PdfFont font = null;
            try {
                font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.PREFER_EMBEDDED);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
            canvas.beginText().setFontAndSize(font, 12).setTextMatrix(1, 0, 0, 1, 32, 512);

            DefaultAccessibilityProperties paraProps = new DefaultAccessibilityProperties(StandardRoles.P);
            tagPointer.addTag(paraProps).addTag(StandardRoles.SPAN);

            tagPointer.getProperties().setStructureElementIdString("hello-element");
            canvas.openTag(tagPointer.getTagReference()).showText("Hello ").closeTag();
            tagPointer.moveToParent().addTag(StandardRoles.SPAN);

            tagPointer.getProperties().setStructureElementIdString("world-element");
            tagPointer.getProperties().setStructureElementIdString("hello-element");
        });

        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            framework.assertOnlyITextFail("documentWithDuplicatingIdInStructTree", MessageFormatUtil.format(
                            PdfUAExceptionMessageConstants.NON_UNIQUE_ID_ENTRY_IN_STRUCT_TREE_ROOT, "hello-element"),
                    pdfUAConformance);
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            framework.assertBothValid("documentWithDuplicatingIdInStructTree", pdfUAConformance);
        }
    }

    @Test
    public void openDocumentWithDuplicatingIdInStructTree() throws IOException {
        String source = SOURCE_FOLDER + "documentWithDuplicatingIdsInStructTree.pdf";
        String dest = DESTINATION_FOLDER + "documentWithDuplicatingIdsInStructTree.pdf";
        Files.copy(new File(source).toPath(), new File(dest).toPath());
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(new File(source)))) {
            ValidationContainer validationContainer = new ValidationContainer();
            validationContainer.addChecker(new PdfUA1Checker(pdfDocument));
            pdfDocument.getDiContainer().register(ValidationContainer.class, validationContainer);
        }
        //Vera pdf doesn't complain on this document
        Assertions.assertNull(new VeraPdfValidator().validate(dest)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void manualPdfUaCreation() throws IOException {
        final String outPdf = DESTINATION_FOLDER + "manualPdfUaCreation.pdf";
        final WriterProperties properties = new WriterProperties().addPdfUaXmpMetadata(PdfUAConformance.PDF_UA_1)
                .setPdfVersion(PdfVersion.PDF_1_7);
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf, properties));
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

        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }
}
