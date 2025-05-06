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
package com.itextpdf.pdfua.checkers;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.StandardNamespaces;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class PdfUATableOfContentsTest extends ExtendedITextTest {

    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/pdfua/PdfUATableOfContentsTest/";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";

    private UaValidationTestFramework framework;

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @BeforeEach
    public void initializeFramework() {
        framework = new UaValidationTestFramework(DESTINATION_FOLDER);
    }

    public static java.util.List<PdfUAConformance> testSources() {
        return UaValidationTestFramework.getConformanceList();
    }

    @ParameterizedTest
    @MethodSource("testSources")
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.VERSION_INCOMPATIBILITY_FOR_DICTIONARY_ENTRY), ignore = true)
    public void checkTableOfContentsTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDocument -> {
            Document document = new Document(pdfDocument);
            PdfFont font = getFont();
            document.setFont(font);
            Paragraph paragraph = new Paragraph("Table of Contents");
            document.add(paragraph);
            Paragraph tociRef = new Paragraph("The referenced paragraph");
            document.add(tociRef);
            TagTreePointer pointer = new TagTreePointer(pdfDocument);
            pointer.moveToKid(1, StandardRoles.P);
            PdfNamespace namespace = new PdfNamespace(StandardNamespaces.PDF_1_7);
            Div tocDiv = new Div();
            tocDiv.getAccessibilityProperties().setRole(StandardRoles.TOC).setNamespace(namespace);
            Div firstTociDiv = new Div();
            firstTociDiv.getAccessibilityProperties().setRole(StandardRoles.TOCI).setNamespace(namespace);
            firstTociDiv.add(new Paragraph("first toci"));
            firstTociDiv.getAccessibilityProperties().addRef(pointer);
            Div secondTociDiv = new Div();
            secondTociDiv.getAccessibilityProperties().setRole(StandardRoles.TOCI).setNamespace(namespace);
            secondTociDiv.add(new Paragraph("second toci"));
            secondTociDiv.getAccessibilityProperties().addRef(pointer);
            tocDiv.add(firstTociDiv);
            tocDiv.add(secondTociDiv);
            document.add(tocDiv);
        });
        framework.assertBothValid("tableOfContentsTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("testSources")
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.VERSION_INCOMPATIBILITY_FOR_DICTIONARY_ENTRY), ignore = true)
    public void checkTableOfContentsWithReferenceChildTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDocument -> {
            addTableOfContentsWithRefInChild(pdfDocument, StandardRoles.REFERENCE);
        });
        framework.assertBothValid("checkTableOfContentsWithReferenceChildTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("testSources")
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.VERSION_INCOMPATIBILITY_FOR_DICTIONARY_ENTRY), ignore = true)
    public void checkTableOfContentsWithRefOnDivChildTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDocument -> {
            addTableOfContentsWithRefInChild(pdfDocument, StandardRoles.DIV);
        });
        framework.assertBothValid("checkTableOfContentsWithRefOnDivChildTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("testSources")
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.VERSION_INCOMPATIBILITY_FOR_DICTIONARY_ENTRY), ignore = true)
    public void checkTableOfContentsWithRefOnArtifactChildTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDocument -> {
            addTableOfContentsWithRefInChild(pdfDocument, StandardRoles.ARTIFACT);
        });
        if (PdfUAConformance.PDF_UA_1 == pdfUAConformance) {
            framework.assertBothValid("checkTableOfContentsWithRefOnArtifactChildTest", pdfUAConformance);
        } else if (PdfUAConformance.PDF_UA_2 == pdfUAConformance) {
            framework.assertBothFail("checkTableOfContentsWithRefOnArtifactChildTest",
                    PdfUAExceptionMessageConstants.TOCI_SHALL_IDENTIFY_REF, pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("testSources")
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.VERSION_INCOMPATIBILITY_FOR_DICTIONARY_ENTRY), ignore = true)
    public void checkTableOfContentsWithRefOnGrandchildTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDocument -> {
            addTableOfContentsWithRefInGrandchild(pdfDocument, StandardRoles.REFERENCE);
        });
        framework.assertBothValid("checkTableOfContentsWithRefOnGrandchildTest", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("testSources")
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.VERSION_INCOMPATIBILITY_FOR_DICTIONARY_ENTRY), ignore = true)
    public void checkTableOfContentsWithRefOnGrandchildTest2(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDocument -> {
            addTableOfContentsWithRefInGrandchild(pdfDocument, StandardRoles.P);
        });
        framework.assertBothValid("checkTableOfContentsWithRefOnGrandchildTest2", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("testSources")
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.VERSION_INCOMPATIBILITY_FOR_DICTIONARY_ENTRY), ignore = true)
    public void checkTableOfContentsNoRefTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDocument -> {
            Document document = new Document(pdfDocument);
            PdfFont font = getFont();
            document.setFont(font);

            PdfNamespace namespace = new PdfNamespace(StandardNamespaces.PDF_1_7);
            Div tocDiv = new Div();
            tocDiv.getAccessibilityProperties().setRole(StandardRoles.TOC).setNamespace(namespace);

            Div firstTociDiv = new Div();
            firstTociDiv.getAccessibilityProperties().setRole(StandardRoles.TOCI).setNamespace(namespace);
            firstTociDiv.add(new Paragraph("first toci"));

            Div secondTociDiv = new Div();
            secondTociDiv.getAccessibilityProperties().setRole(StandardRoles.TOCI).setNamespace(namespace);
            secondTociDiv.add(new Paragraph("second toci"));

            tocDiv.add(firstTociDiv);
            tocDiv.add(secondTociDiv);
            document.add(tocDiv);
        });
        if (PdfUAConformance.PDF_UA_1 == pdfUAConformance) {
            framework.assertBothValid("checkTableOfContentsNoRefTest", pdfUAConformance);
        } else if (PdfUAConformance.PDF_UA_2 == pdfUAConformance) {
            framework.assertBothFail("checkTableOfContentsNoRefTest",
                    PdfUAExceptionMessageConstants.TOCI_SHALL_IDENTIFY_REF, pdfUAConformance);
        }
    }

    @ParameterizedTest
    @MethodSource("testSources")
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.VERSION_INCOMPATIBILITY_FOR_DICTIONARY_ENTRY), ignore = true)
    public void checkInvalidStructureTableOfContentsTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDocument -> {
            Document document = new Document(pdfDocument);
            PdfFont font = getFont();
            document.setFont(font);

            PdfNamespace namespace = new PdfNamespace(StandardNamespaces.PDF_1_7);
            Paragraph tocTitle = new Paragraph("Table of Contents\n");
            tocTitle.getAccessibilityProperties().setRole(StandardRoles.TOC).setNamespace(namespace);
            Paragraph tociElement = new Paragraph("- TOCI element");
            tociElement.getAccessibilityProperties().setRole(StandardRoles.TOCI).setNamespace(namespace);
            Paragraph tociRef = new Paragraph("The referenced paragraph");
            document.add(tociRef);
            TagTreePointer pointer = new TagTreePointer(pdfDocument);
            pointer.moveToKid(StandardRoles.P);
            tociElement.getAccessibilityProperties().addRef(pointer);
            tocTitle.add(tociElement);
            document.add(tocTitle);
        });
        framework.assertBothValid("invalidStructureTableOfContentsTest", pdfUAConformance);
    }

    private static void addTableOfContentsWithRefInChild(PdfDocument pdfDocument, String childRole) {
        Document document = new Document(pdfDocument);
        PdfFont font = getFont();
        document.setFont(font);
        Paragraph paragraph = new Paragraph("Table of Contents");
        document.add(paragraph);
        Paragraph tociRef = new Paragraph("The referenced paragraph");
        document.add(tociRef);
        TagTreePointer pointer = new TagTreePointer(pdfDocument);
        pointer.moveToKid(1, StandardRoles.P);
        PdfNamespace namespace = new PdfNamespace(StandardNamespaces.PDF_1_7);

        Div childWithRef = new Div();
        childWithRef.getAccessibilityProperties().setRole(childRole).setNamespace(namespace);
        childWithRef.getAccessibilityProperties().addRef(pointer);

        Div tocDiv = new Div();
        tocDiv.getAccessibilityProperties().setRole(StandardRoles.TOC).setNamespace(namespace);

        Div firstTociDiv = new Div();
        firstTociDiv.getAccessibilityProperties().setRole(StandardRoles.TOCI).setNamespace(namespace);
        firstTociDiv.add(new Paragraph("first toci"));
        firstTociDiv.add(childWithRef);

        Div secondTociDiv = new Div();
        secondTociDiv.getAccessibilityProperties().setRole(StandardRoles.TOCI).setNamespace(namespace);
        secondTociDiv.add(new Paragraph("second toci"));
        secondTociDiv.add(childWithRef);

        tocDiv.add(firstTociDiv);
        tocDiv.add(secondTociDiv);
        document.add(tocDiv);
    }

    private static void addTableOfContentsWithRefInGrandchild(PdfDocument pdfDocument, String reference) {
        Document document = new Document(pdfDocument);
        PdfFont font = getFont();
        document.setFont(font);
        Paragraph paragraph = new Paragraph("Table of Contents");
        document.add(paragraph);
        Paragraph tociRef = new Paragraph("The referenced paragraph");
        document.add(tociRef);
        TagTreePointer pointer = new TagTreePointer(pdfDocument);
        pointer.moveToKid(1, StandardRoles.P);
        PdfNamespace namespace = new PdfNamespace(StandardNamespaces.PDF_1_7);

        Div child = new Div();
        child.getAccessibilityProperties().setRole(reference).setNamespace(namespace);
        Div grandchild = new Div();
        grandchild.getAccessibilityProperties().setRole(StandardRoles.LBL).setNamespace(namespace);
        grandchild.getAccessibilityProperties().addRef(pointer);
        child.add(grandchild);

        Div tocDiv = new Div();
        tocDiv.getAccessibilityProperties().setRole(StandardRoles.TOC).setNamespace(namespace);

        Div firstTociDiv = new Div();
        firstTociDiv.getAccessibilityProperties().setRole(StandardRoles.TOCI).setNamespace(namespace);
        firstTociDiv.add(new Paragraph("first toci"));
        firstTociDiv.add(child);

        Div secondTociDiv = new Div();
        secondTociDiv.getAccessibilityProperties().setRole(StandardRoles.TOCI).setNamespace(namespace);
        secondTociDiv.add(new Paragraph("second toci"));
        secondTociDiv.add(child);

        tocDiv.add(firstTociDiv);
        tocDiv.add(secondTociDiv);
        document.add(tocDiv);
    }

    private static PdfFont getFont() {
        PdfFont font = null;
        try {
            font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.PREFER_EMBEDDED);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return font;
    }
}
