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
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.StandardNamespaces;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.pdfua.logs.PdfUALogMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class PdfUAHeadingsTest extends ExtendedITextTest {
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUAHeadingsTest/";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public static List<PdfConformance> data() {
        return UaValidationTestFramework.getConformanceList();
    }

    // -------- Negative tests --------
    @ParameterizedTest
    @MethodSource("data")
    public void addH2AsFirstHeaderTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Paragraph h2 = new Paragraph("Header level 2");
            h2.setFont(loadFont());
            h2.getAccessibilityProperties().setRole(StandardRoles.H2);

            return h2;
        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothFail("addH2FirstHeaderTest", PdfUAExceptionMessageConstants.H1_IS_SKIPPED);
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            framework.assertBothValid("addH2FirstHeaderTest");
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void brokenHnParallelSequenceTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Paragraph h1 = new Paragraph("Header level 1");
            h1.setFont(loadFont());
            h1.getAccessibilityProperties().setRole(StandardRoles.H1);
            return h1;
        });
        framework.addSuppliers( document -> {
            Paragraph h3 = new Paragraph("Header level 3");
            h3.setFont(loadFont());
            h3.getAccessibilityProperties().setRole(StandardRoles.H3);
            return h3;
        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothFail("brokenHnParallelSequenceTest",
                    MessageFormatUtil.format(PdfUAExceptionMessageConstants.HN_IS_SKIPPED, 2));
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            framework.assertBothValid("brokenHnParallelSequenceTest");
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void brokenHnInheritedSequenceTest1(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Paragraph h1 = new Paragraph("Header level 1");
            h1.setFont(loadFont());
            h1.getAccessibilityProperties().setRole(StandardRoles.H1);

            Paragraph h3 = new Paragraph("Header level 3");
            h3.setFont(loadFont());
            h3.getAccessibilityProperties().setRole(StandardRoles.H3);
            h1.add(h3);
            return h1;
        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothFail("brokenHnInheritedSequenceTest1",
                    MessageFormatUtil.format(PdfUAExceptionMessageConstants.HN_IS_SKIPPED, 2));
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            final String expectedMessage = MessageFormatUtil.format(
                    KernelExceptionMessageConstant.PARENT_CHILD_ROLE_RELATION_IS_NOT_ALLOWED, "H1", "H3");
            framework.assertBothFail("brokenHnInheritedSequenceTest1", expectedMessage);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void brokenHnMixedSequenceTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Paragraph h1 = new Paragraph("Header level 1");
            h1.setFont(loadFont());
            h1.getAccessibilityProperties().setRole(StandardRoles.H1);

            Paragraph h2 = new Paragraph("Header level 2");
            h2.setFont(loadFont());
            h2.getAccessibilityProperties().setRole(StandardRoles.H2);
            h1.add(h2);

            Paragraph h5 = new Paragraph("Header level 5");
            h5.setFont(loadFont());
            h5.getAccessibilityProperties().setRole(StandardRoles.H5);
            h1.add(h5);
            return h1;
        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothFail("brokenHnMixedSequenceTest",
                    MessageFormatUtil.format(PdfUAExceptionMessageConstants.HN_IS_SKIPPED, 3));
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            final String expectedMessage = MessageFormatUtil.format(
                    KernelExceptionMessageConstant.PARENT_CHILD_ROLE_RELATION_IS_NOT_ALLOWED, "H1", "H2");
            framework.assertBothFail("brokenHnMixedSequenceTest", expectedMessage);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void brokenHnMixedSequenceTest2(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Paragraph h1 = new Paragraph("Header level 1");
            h1.setFont(loadFont());
            h1.getAccessibilityProperties().setRole(StandardRoles.H1);

            Div div = new Div();
            div.setBackgroundColor(ColorConstants.CYAN);
            h1.add(div);

            Paragraph h2 = new Paragraph("Header level 2");
            h2.setFont(loadFont());
            h2.getAccessibilityProperties().setRole(StandardRoles.H2);
            div.add(h2);

            Paragraph h5 = new Paragraph("Header level 5");
            h5.setFont(loadFont());
            h5.getAccessibilityProperties().setRole(StandardRoles.H5);
            div.add(h5);
            return h1;
        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothFail("brokenHnMixedSequenceTest2",
                    MessageFormatUtil.format(PdfUAExceptionMessageConstants.HN_IS_SKIPPED, 3));
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            String message = MessageFormatUtil.format(
                    KernelExceptionMessageConstant.PARENT_CHILD_ROLE_RELATION_IS_NOT_ALLOWED, "H1", "Div");
            framework.assertBothFail("brokenHnMixedSequenceTest2", message);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void fewHInOneNodeTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Div div = new Div();
            div.setBackgroundColor(ColorConstants.CYAN);

            Paragraph header1 = new Paragraph("Header");
            header1.setFont(loadFont());
            header1.getAccessibilityProperties().setRole(StandardRoles.H);
            div.add(header1);

            Paragraph header2 = new Paragraph("Header");
            header2.setFont(loadFont());
            header2.getAccessibilityProperties().setRole(StandardRoles.H);
            div.add(header2);
            return div;
        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothFail("fewHInOneNodeTest", PdfUAExceptionMessageConstants.MORE_THAN_ONE_H_TAG);
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            framework.assertBothFail("fewHInOneNodeTest", PdfUAExceptionMessageConstants.DOCUMENT_USES_H_TAG);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void fewHInDocumentTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Paragraph header1 = new Paragraph("Header");
            header1.setFont(loadFont());
            header1.getAccessibilityProperties().setRole(StandardRoles.H);
            return header1;
        });
        framework.addSuppliers( document -> {
            Paragraph header2 = new Paragraph("Header");
            header2.setFont(loadFont());
            header2.getAccessibilityProperties().setRole(StandardRoles.H);
            return header2;
        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothFail("fewHInDocumentTest", PdfUAExceptionMessageConstants.MORE_THAN_ONE_H_TAG);
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            framework.assertBothFail("fewHInDocumentTest", PdfUAExceptionMessageConstants.DOCUMENT_USES_H_TAG);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void hAndHnInDocumentTest1(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Paragraph header1 = new Paragraph("Header");
            header1.setFont(loadFont());
            header1.getAccessibilityProperties().setRole(StandardRoles.H);
            return header1;
        });
        framework.addSuppliers( document -> {
            Paragraph h1 = new Paragraph("Header level 1");
            h1.setFont(loadFont());
            h1.getAccessibilityProperties().setRole(StandardRoles.H1);
            return h1;
        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothFail("hAndHnInDocumentTest1",
                    PdfUAExceptionMessageConstants.DOCUMENT_USES_BOTH_H_AND_HN);
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            framework.assertBothFail("hAndHnInDocumentTest1", PdfUAExceptionMessageConstants.DOCUMENT_USES_H_TAG);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void hAndHnInDocumentTest2(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Paragraph h1 = new Paragraph("Header level 1");
            h1.setFont(loadFont());
            h1.getAccessibilityProperties().setRole(StandardRoles.H1);
            return h1;
        });
        framework.addSuppliers( document -> {
            Paragraph header1 = new Paragraph("Header");
            header1.setFont(loadFont());
            header1.getAccessibilityProperties().setRole(StandardRoles.H);
            return header1;
        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothFail("hAndHnInDocumentTest2",
                    PdfUAExceptionMessageConstants.DOCUMENT_USES_BOTH_H_AND_HN);
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            framework.assertBothFail("hAndHnInDocumentTest2", PdfUAExceptionMessageConstants.DOCUMENT_USES_H_TAG);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void hAndHnInDocumentTest3(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Paragraph h1 = new Paragraph("Header level 1");
            h1.setFont(loadFont());
            h1.getAccessibilityProperties().setRole(StandardRoles.H1);

            Paragraph h2 = new Paragraph("Header level 2");
            h2.setFont(loadFont());
            h2.getAccessibilityProperties().setRole(StandardRoles.H2);
            h1.add(h2);

            Paragraph header1 = new Paragraph("Header");
            header1.setFont(loadFont());
            header1.getAccessibilityProperties().setRole(StandardRoles.H);
            h2.add(header1);
            return h1;
        });
        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothFail("hAndHnInDocumentTest3",
                    PdfUAExceptionMessageConstants.DOCUMENT_USES_BOTH_H_AND_HN);
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            framework.assertBothFail("hAndHnInDocumentTest3", PdfUAExceptionMessageConstants.DOCUMENT_USES_H_TAG);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void roleMappingTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Paragraph h1 = new Paragraph("Header level 1");
            h1.setFont(loadFont());
            h1.getAccessibilityProperties().setRole("header1");

            Paragraph h2 = new Paragraph("Header level 5");
            h2.setFont(loadFont());
            h2.getAccessibilityProperties().setRole("header5");
            h1.add(h2);
            return h1;
        });
        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfStructTreeRoot root = pdfDocument.getStructTreeRoot();
            if (conformance.equals(PdfConformance.PDF_UA_2)) {
                PdfNamespace namespace = new PdfNamespace(StandardNamespaces.PDF_2_0);
                pdfDocument.getTagStructureContext().setDocumentDefaultNamespace(namespace);
                pdfDocument.getStructTreeRoot().addNamespace(namespace);
                namespace.addNamespaceRoleMapping("header1", StandardRoles.H1);
                namespace.addNamespaceRoleMapping("header5", StandardRoles.H5);
            }
            root.addRoleMapping("header1", StandardRoles.H1);
            root.addRoleMapping("header5", StandardRoles.H5);

        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothFail("rolemappingTest");
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            String message = MessageFormatUtil.format(
                    KernelExceptionMessageConstant.PARENT_CHILD_ROLE_RELATION_IS_NOT_ALLOWED, "H1", "header5");
            framework.assertBothFail("rolemappingTest", message);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void roleMappingTestValid(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Paragraph h1 = new Paragraph("Header level 1");
            h1.setFont(loadFont());
            h1.getAccessibilityProperties().setRole("header1");

            Paragraph h2 = new Paragraph("Header level 5");
            h2.setFont(loadFont());
            h2.getAccessibilityProperties().setRole("header5");
            h1.add(h2);
            return h1;
        });
        framework.addBeforeGenerationHook(pdfDocument -> {
            if (conformance.equals(PdfConformance.PDF_UA_2)) {
                PdfNamespace namespace = new PdfNamespace(StandardNamespaces.PDF_2_0);
                pdfDocument.getTagStructureContext().setDocumentDefaultNamespace(namespace);
                pdfDocument.getStructTreeRoot().addNamespace(namespace);
                namespace.addNamespaceRoleMapping("header1", StandardRoles.H1);
                namespace.addNamespaceRoleMapping("header5", StandardRoles.H2);
            }
            PdfStructTreeRoot root = pdfDocument.getStructTreeRoot();
            root.addRoleMapping("header1", StandardRoles.H1);
            root.addRoleMapping("header5", StandardRoles.H2);

        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothValid("rolemappingValid");
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            String message = MessageFormatUtil.format(
                    KernelExceptionMessageConstant.PARENT_CHILD_ROLE_RELATION_IS_NOT_ALLOWED, "H1", "header5");
            framework.assertBothFail("rolemappingValid", message);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void directWritingToCanvasTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            TagTreePointer pointer = new TagTreePointer(pdfDoc);
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            pointer.setPageForTagging(page);

            TagTreePointer tmp = pointer.addTag(StandardRoles.H3);
            canvas.openTag(tmp.getTagReference());
            canvas.writeLiteral("Heading level 3");
            canvas.closeTag();
        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothFail("directWritingToCanvas", PdfUAExceptionMessageConstants.H1_IS_SKIPPED);
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            framework.assertBothValid("directWritingToCanvas");
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void hInDocumentTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Paragraph header1 = new Paragraph("Header");
            header1.setFont(loadFont());
            header1.getAccessibilityProperties().setRole(StandardRoles.H);
            return header1;
        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothValid("hInDocumentTest");
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            framework.assertBothFail("hInDocumentTest", PdfUAExceptionMessageConstants.DOCUMENT_USES_H_TAG);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void hAndHnInDocumentTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Paragraph h1 = new Paragraph("Header level 1");
            h1.setFont(loadFont());
            h1.getAccessibilityProperties().setRole(StandardRoles.H1);

            Paragraph h2 = new Paragraph("Header level 2");
            h2.setFont(loadFont());
            h2.getAccessibilityProperties().setRole(StandardRoles.H2);
            h1.add(h2);

            Paragraph header1 = new Paragraph("Header");
            header1.setFont(loadFont());
            header1.getAccessibilityProperties().setRole(StandardRoles.H);
            h2.add(header1);
            return h1;
        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothFail("hAndHnInDocumentTest");
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            framework.assertBothFail("hAndHnInDocumentTest", PdfUAExceptionMessageConstants.DOCUMENT_USES_H_TAG);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void incorrectHeadingLevelInUA2Test(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Div div = new Div();
            div.setBackgroundColor(ColorConstants.CYAN);

            Paragraph h2 = new Paragraph("1.2 Header level 2");
            h2.setFont(loadFont());
            h2.getAccessibilityProperties().setRole(StandardRoles.H2);
            div.add(h2);

            Paragraph h1 = new Paragraph("1.2.3 Header level 3");
            h1.setFont(loadFont());
            h1.getAccessibilityProperties().setRole(StandardRoles.H1);
            div.add(h1);
            return h2;
        });
        // Where a heading’s level is evident, the heading level of the structure element enclosing it shall match that
        // heading level, e.g. a heading with the real content “5.1.6.4 Some header” is evidently at heading level 4.
        // This requirement is not checked by both iText and veraPDF.
        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothFail("incorrectHeadingLevelInUA2Test");
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            framework.assertBothValid("incorrectHeadingLevelInUA2Test");
        }
    }

    // -------- Positive tests --------
    @ParameterizedTest
    @MethodSource("data")
    @LogMessages(messages = {
            @LogMessage(messageTemplate = PdfUALogMessageConstants.PAGE_FLUSHING_DISABLED, ignore = true)})
    public void flushPreviousPageTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document doc = new Document(pdfDoc);

            String longHeader =
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " + "Donec ac malesuada tellus. "
                            + "Quisque a arcu semper, tristique nibh eu, convallis lacus. "
                            + "Donec neque justo, condimentum sed molestie ac, mollis eu nibh. "
                            + "Vivamus pellentesque condimentum fringilla. " + "Nullam euismod ac risus a semper. "
                            + "Etiam hendrerit scelerisque sapien tristique varius.";

            for (int i = 0; i < 10; i++) {
                Paragraph h1 = new Paragraph(longHeader);
                h1.setFont(loadFont());
                h1.getAccessibilityProperties().setRole(StandardRoles.H1);

                Paragraph h2 = new Paragraph(longHeader);
                h2.setFont(loadFont());
                h2.getAccessibilityProperties().setRole(StandardRoles.H2);
                h1.add(h2);

                Paragraph h3 = new Paragraph(longHeader);
                h3.setFont(loadFont());
                h3.getAccessibilityProperties().setRole(StandardRoles.H3);
                h2.add(h3);

                Paragraph h4 = new Paragraph(longHeader);
                h4.setFont(loadFont());
                h4.getAccessibilityProperties().setRole(StandardRoles.H4);
                h3.add(h4);

                Paragraph h5 = new Paragraph(longHeader);
                h5.setFont(loadFont());
                h5.getAccessibilityProperties().setRole(StandardRoles.H5);
                h4.add(h5);

                Paragraph h6 = new Paragraph(longHeader);
                h6.setFont(loadFont());
                h6.getAccessibilityProperties().setRole(StandardRoles.H6);
                h5.add(h6);

                doc.add(h1);
                if (pdfDoc.getNumberOfPages() > 1) {
                    pdfDoc.getPage(pdfDoc.getNumberOfPages() - 1).flush();
                }
            }
        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothValid("hugeDocumentTest");
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            framework.assertBothFail("hugeDocumentTest",
                    MessageFormatUtil.format(KernelExceptionMessageConstant.PARENT_CHILD_ROLE_RELATION_IS_NOT_ALLOWED,
                            "H1", "H2"));
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void hnInheritedSequenceTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Paragraph h1 = new Paragraph("Header level 1");
            h1.setFont(loadFont());
            h1.getAccessibilityProperties().setRole(StandardRoles.H1);

            Paragraph h2 = new Paragraph("Header level 2");
            h2.setFont(loadFont());
            h2.getAccessibilityProperties().setRole(StandardRoles.H2);
            h1.add(h2);

            Paragraph h3 = new Paragraph("Header level 3");
            h3.setFont(loadFont());
            h3.getAccessibilityProperties().setRole(StandardRoles.H3);
            h2.add(h3);
            return h1;
        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothValid("hnInheritedSequenceTest");
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            String message = MessageFormatUtil.format(
                    KernelExceptionMessageConstant.PARENT_CHILD_ROLE_RELATION_IS_NOT_ALLOWED, "H1", "H2");
            framework.assertBothFail("hnInheritedSequenceTest", message);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void hnCompareWithLastFromAnotherBranchTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Paragraph h1 = new Paragraph("Header level 1");
            h1.setFont(loadFont());
            h1.getAccessibilityProperties().setRole(StandardRoles.H1);

            Paragraph h2 = new Paragraph("Header level 2");
            h2.setFont(loadFont());
            h2.getAccessibilityProperties().setRole(StandardRoles.H2);
            h1.add(h2);

            Paragraph h3 = new Paragraph("Header level 3");
            h3.setFont(loadFont());
            h3.getAccessibilityProperties().setRole(StandardRoles.H3);
            h2.add(h3);

            Paragraph h4 = new Paragraph("Header level 4");
            h4.setFont(loadFont());
            h4.getAccessibilityProperties().setRole(StandardRoles.H4);
            h2.add(h4);
            return h1;
        });
        framework.addSuppliers( document -> {
            Paragraph h5 = new Paragraph("Second Header level 5 in doc");
            h5.setFont(loadFont());
            h5.getAccessibilityProperties().setRole(StandardRoles.H5);
            return h5;
        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothValid("hnCompareWithLastFromAnotherBranchTest");
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            String message = MessageFormatUtil.format(
                    KernelExceptionMessageConstant.PARENT_CHILD_ROLE_RELATION_IS_NOT_ALLOWED, "H1", "H2");
            framework.assertBothFail("hnCompareWithLastFromAnotherBranchTest", message);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void hnCompareWithLastFromAnotherBranchTest2(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Paragraph h1 = new Paragraph("Header level 1");
            h1.setFont(loadFont());
            h1.getAccessibilityProperties().setRole(StandardRoles.H1);

            Paragraph h2 = new Paragraph("Header level 2");
            h2.setFont(loadFont());
            h2.getAccessibilityProperties().setRole(StandardRoles.H2);
            h1.add(h2);

            Paragraph h3 = new Paragraph("Header level 3");
            h3.setFont(loadFont());
            h3.getAccessibilityProperties().setRole(StandardRoles.H3);
            h2.add(h3);

            Paragraph h4 = new Paragraph("Header level 4");
            h4.setFont(loadFont());
            h4.getAccessibilityProperties().setRole(StandardRoles.H4);
            h2.add(h4);
            return h1;
        });
        framework.addSuppliers( document -> {
            Paragraph h33 = new Paragraph("Second Header level 3 in doc");
            h33.setFont(loadFont());
            h33.getAccessibilityProperties().setRole(StandardRoles.H3);
            return h33;
        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothValid("hnCompareWithLastFromAnotherBranchTest2");
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            String message = MessageFormatUtil.format(
                    KernelExceptionMessageConstant.PARENT_CHILD_ROLE_RELATION_IS_NOT_ALLOWED, "H1", "H2");
            framework.assertBothFail("hnCompareWithLastFromAnotherBranchTest2", message);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void hnInheritedSequenceTest2(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Paragraph h1 = new Paragraph("Header level 1");
            h1.setFont(loadFont());
            h1.getAccessibilityProperties().setRole(StandardRoles.H1);

            Paragraph h2 = new Paragraph("Header level 2");
            h2.setFont(loadFont());
            h2.getAccessibilityProperties().setRole(StandardRoles.H2);
            h1.add(h2);

            Paragraph h3 = new Paragraph("Header level 3");
            h3.setFont(loadFont());
            h3.getAccessibilityProperties().setRole(StandardRoles.H3);
            h2.add(h3);

            Paragraph secH1 = new Paragraph("Second header level 1");
            secH1.setFont(loadFont());
            secH1.getAccessibilityProperties().setRole(StandardRoles.H1);
            h3.add(secH1);
            return h1;
        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothValid("hnInheritedSequenceTest2");
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            String message = MessageFormatUtil.format(
                    KernelExceptionMessageConstant.PARENT_CHILD_ROLE_RELATION_IS_NOT_ALLOWED, "H1", "H2");
            framework.assertBothFail("hnInheritedSequenceTest2", message);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void hnParallelSequenceTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Paragraph h1 = new Paragraph("Header level 1");
            h1.setFont(loadFont());
            h1.getAccessibilityProperties().setRole(StandardRoles.H1);
            return h1;
        });
        framework.addSuppliers( document -> {
            Paragraph h2 = new Paragraph("Header level 2");
            h2.setFont(loadFont());
            h2.getAccessibilityProperties().setRole(StandardRoles.H2);
            return h2;
        });
        framework.addSuppliers( document -> {
            Paragraph h3 = new Paragraph("Header level 3");
            h3.setFont(loadFont());
            h3.getAccessibilityProperties().setRole(StandardRoles.H3);
            return h3;
        });
        framework.assertBothValid("hnParallelSequenceTest");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void usualHTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document doc = new Document(pdfDoc);

            Paragraph header = new Paragraph("Header");
            header.setFont(loadFont());
            header.getAccessibilityProperties().setRole(StandardRoles.H);
            doc.add(header);

            Div div = new Div();
            div.setHeight(50);
            div.setWidth(50);
            div.setBackgroundColor(ColorConstants.CYAN);

            Paragraph header2 = new Paragraph("Header 2");
            header2.setFont(loadFont());
            header2.getAccessibilityProperties().setRole(StandardRoles.H);
            div.add(header2);

            doc.add(div);
        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertOnlyVeraPdfFail("usualHTest");
        } else {
            framework.assertBothFail("usualHTest", PdfUAExceptionMessageConstants.DOCUMENT_USES_H_TAG);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void usualHTest2(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Paragraph header = new Paragraph("Header");
            header.setFont(loadFont());
            header.getAccessibilityProperties().setRole(StandardRoles.H);
            return header;
        });
        framework.addSuppliers( document -> {
            Div div = new Div();
            div.setHeight(50);
            div.setWidth(50);
            div.setBackgroundColor(ColorConstants.CYAN);
            // The test code is the same as in usualHTest with one exception:
            // the next line where another grouping element is defined.
            div.getAccessibilityProperties().setRole(StandardRoles.SECT);

            Paragraph header2 = new Paragraph("Header 2");
            header2.setFont(loadFont());
            header2.getAccessibilityProperties().setRole(StandardRoles.H);
            div.add(header2);
            return div;
        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothValid("usualHTest2");
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            framework.assertBothFail("usualHTest2", PdfUAExceptionMessageConstants.DOCUMENT_USES_H_TAG);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void hnMixedSequenceTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Paragraph h1 = new Paragraph("Header level 1");
            h1.setFont(loadFont());
            h1.getAccessibilityProperties().setRole(StandardRoles.H1);

            Paragraph h2 = new Paragraph("Header level 2");
            h2.setFont(loadFont());
            h2.getAccessibilityProperties().setRole(StandardRoles.H2);
            h1.add(h2);

            Div div = new Div();
            div.setHeight(50);
            div.setWidth(50);
            div.setBackgroundColor(ColorConstants.CYAN);
            h1.add(div);

            Paragraph h3 = new Paragraph("Header level 3");
            h3.setFont(loadFont());
            h3.getAccessibilityProperties().setRole(StandardRoles.H3);
            h1.add(h3);
            return h1;
        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothValid("hnMixedSequenceTest");
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            String message = MessageFormatUtil.format(
                    KernelExceptionMessageConstant.PARENT_CHILD_ROLE_RELATION_IS_NOT_ALLOWED, "H1", "H2");
            framework.assertBothFail("hnMixedSequenceTest", message);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void hnMixedSequenceTest2(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Paragraph h1 = new Paragraph("Header level 1");
            h1.setFont(loadFont());
            h1.getAccessibilityProperties().setRole(StandardRoles.H1);

            Paragraph h2 = new Paragraph("Header level 2");
            h2.setFont(loadFont());
            h2.getAccessibilityProperties().setRole(StandardRoles.H2);
            h1.add(h2);

            Paragraph h3 = new Paragraph("Header level 3");
            h3.setFont(loadFont());
            h3.getAccessibilityProperties().setRole(StandardRoles.H3);
            h1.add(h3);
            return h1;
        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothValid("hnMixedSequenceTest2");
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            String message = MessageFormatUtil.format(
                    KernelExceptionMessageConstant.PARENT_CHILD_ROLE_RELATION_IS_NOT_ALLOWED, "H1", "H2");
            framework.assertBothFail("hnMixedSequenceTest2", message);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void hnMixedSequenceTest3(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Paragraph h1 = new Paragraph("Header level 1");
            h1.setFont(loadFont());
            h1.getAccessibilityProperties().setRole(StandardRoles.H1);

            Div div = new Div();
            div.setBackgroundColor(ColorConstants.CYAN);
            h1.add(div);

            Paragraph h2 = new Paragraph("Header level 2");
            h2.setFont(loadFont());
            h2.getAccessibilityProperties().setRole(StandardRoles.H2);
            div.add(h2);

            Paragraph h3 = new Paragraph("Header level 3");
            h3.setFont(loadFont());
            h3.getAccessibilityProperties().setRole(StandardRoles.H3);
            div.add(h3);
            return h1;
        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothValid("hnMixedSequenceTest3");
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            String message = MessageFormatUtil.format(
                    KernelExceptionMessageConstant.PARENT_CHILD_ROLE_RELATION_IS_NOT_ALLOWED, "H1", "Div");
            framework.assertBothFail("hnMixedSequenceTest3", message);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void nonSequentialHeadersTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Div div = new Div();
            div.setBackgroundColor(ColorConstants.CYAN);

            Paragraph h2 = new Paragraph("Header level 2");
            h2.setFont(loadFont());
            h2.getAccessibilityProperties().setRole(StandardRoles.H2);
            div.add(h2);

            Paragraph h1 = new Paragraph("Header level 1");
            h1.setFont(loadFont());
            h1.getAccessibilityProperties().setRole(StandardRoles.H1);
            div.add(h1);
            return h2;
        });

        if (conformance.equals(PdfConformance.PDF_UA_1)) {
            framework.assertBothFail("nonSequentialHeadersTest");
        } else if (conformance.equals(PdfConformance.PDF_UA_2)) {
            framework.assertBothValid("nonSequentialHeadersTest");
        }
    }

    private static PdfFont loadFont() {
        try {
            return PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
        } catch (IOException e) {
            throw new PdfException(e.getMessage());
        }
    }
}
