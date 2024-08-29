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
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.pdfua.PdfUATestPdfDocument;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.UaValidationTestFramework.Generator;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.pdfua.exceptions.PdfUALogMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)

import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfUAHeadingsTest extends ExtendedITextTest {
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUAHeadingsTest/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfua/PdfUAHeadingsTest/";
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

    // -------- Negative tests --------
    @Test
    public void addH2AsFirstHeaderTest() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph h2 = new Paragraph("Header level 2");
                h2.setFont(loadFont());
                h2.getAccessibilityProperties().setRole(StandardRoles.H2);

                return h2;
            }
        });
        framework.assertBothFail("addH2FirstHeaderTest",
                PdfUAExceptionMessageConstants.H1_IS_SKIPPED);
    }

    @Test
    public void brokenHnParallelSequenceTest() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph h1 = new Paragraph("Header level 1");
                h1.setFont(loadFont());
                h1.getAccessibilityProperties().setRole(StandardRoles.H1);
                return h1;
            }
        });
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph h3 = new Paragraph("Header level 3");
                h3.setFont(loadFont());
                h3.getAccessibilityProperties().setRole(StandardRoles.H3);
                return h3;
            }
        });
        framework.assertBothFail("brokenHnParallelSequenceTest",
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.HN_IS_SKIPPED, 2));
    }

    @Test
    public void brokenHnInheritedSequenceTest1() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph h1 = new Paragraph("Header level 1");
                h1.setFont(loadFont());
                h1.getAccessibilityProperties().setRole(StandardRoles.H1);

                Paragraph h3 = new Paragraph("Header level 3");
                h3.setFont(loadFont());
                h3.getAccessibilityProperties().setRole(StandardRoles.H3);
                h1.add(h3);
                return h1;
            }
        });
        framework.assertBothFail("brokenHnInheritedSequenceTest1",
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.HN_IS_SKIPPED, 2));
    }

    @Test
    public void brokenHnMixedSequenceTest() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
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
            }
        });
        framework.assertBothFail("brokenHnMixedSequenceTest",
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.HN_IS_SKIPPED, 3));
    }

    @Test
    public void brokenHnMixedSequenceTest2() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
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
            }
        });
        framework.assertBothFail("brokenHnMixedSequenceTest2",
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.HN_IS_SKIPPED, 3));
    }

    @Test
    public void fewHInOneNodeTest() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
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
            }
        });
        framework.assertBothFail("fewHInOneNodeTest",
                PdfUAExceptionMessageConstants.MORE_THAN_ONE_H_TAG);
    }

    @Test
    public void fewHInDocumentTest() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph header1 = new Paragraph("Header");
                header1.setFont(loadFont());
                header1.getAccessibilityProperties().setRole(StandardRoles.H);
                return header1;
            }
        });
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph header2 = new Paragraph("Header");
                header2.setFont(loadFont());
                header2.getAccessibilityProperties().setRole(StandardRoles.H);
                return header2;
            }
        });
        framework.assertBothFail("fewHInDocumentTest",
                PdfUAExceptionMessageConstants.MORE_THAN_ONE_H_TAG);
    }

    @Test
    public void hAndHnInDocumentTest1() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph header1 = new Paragraph("Header");
                header1.setFont(loadFont());
                header1.getAccessibilityProperties().setRole(StandardRoles.H);
                return header1;
            }
        });
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph h1 = new Paragraph("Header level 1");
                h1.setFont(loadFont());
                h1.getAccessibilityProperties().setRole(StandardRoles.H1);
                return h1;
            }
        });
        framework.assertBothFail("hAndHnInDocumentTest1",
                PdfUAExceptionMessageConstants.DOCUMENT_USES_BOTH_H_AND_HN);
    }

    @Test
    public void hAndHnInDocumentTest2() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph h1 = new Paragraph("Header level 1");
                h1.setFont(loadFont());
                h1.getAccessibilityProperties().setRole(StandardRoles.H1);
                return h1;
            }
        });
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph header1 = new Paragraph("Header");
                header1.setFont(loadFont());
                header1.getAccessibilityProperties().setRole(StandardRoles.H);
                return header1;
            }
        });
        framework.assertBothFail("hAndHnInDocumentTest2",
                PdfUAExceptionMessageConstants.DOCUMENT_USES_BOTH_H_AND_HN);
    }

    @Test
    public void hAndHnInDocumentTest3() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
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
            }
        });
        framework.assertBothFail("hAndHnInDocumentTest3",
                PdfUAExceptionMessageConstants.DOCUMENT_USES_BOTH_H_AND_HN);
    }

    @Test
    public void roleMappingTest() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph h1 = new Paragraph("Header level 1");
                h1.setFont(loadFont());
                h1.getAccessibilityProperties().setRole("header1");

                Paragraph h2 = new Paragraph("Header level 5");
                h2.setFont(loadFont());
                h2.getAccessibilityProperties().setRole("header5");
                h1.add(h2);
                return h1;
            }
        });
        framework.addBeforeGenerationHook((pdfDocument)->{
            PdfStructTreeRoot root = pdfDocument.getStructTreeRoot();
            root.addRoleMapping("header1", StandardRoles.H1);
            root.addRoleMapping("header5", StandardRoles.H5);

        });
        framework.assertBothFail("rolemappingTest");
    }

    @Test
    public void roleMappingTestValid() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph h1 = new Paragraph("Header level 1");
                h1.setFont(loadFont());
                h1.getAccessibilityProperties().setRole("header1");

                Paragraph h2 = new Paragraph("Header level 5");
                h2.setFont(loadFont());
                h2.getAccessibilityProperties().setRole("header5");
                h1.add(h2);
                return h1;
            }
        });
        framework.addBeforeGenerationHook((pdfDocument)->{
            PdfStructTreeRoot root = pdfDocument.getStructTreeRoot();
            root.addRoleMapping("header1", StandardRoles.H1);
            root.addRoleMapping("header5", StandardRoles.H2);

        });
        framework.assertBothValid("rolemappingValid");
    }

    @Test
    public void directWritingToCanvasTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "directWritingToCanvasTest.pdf";

        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));

        TagTreePointer pointer = new TagTreePointer(pdfDoc);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        pointer.setPageForTagging(page);

        TagTreePointer tmp = pointer.addTag(StandardRoles.H3);
        canvas.openTag(tmp.getTagReference());
        canvas.writeLiteral("Heading level 3");
        canvas.closeTag();
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(PdfUAExceptionMessageConstants.H1_IS_SKIPPED, e.getMessage());
    }

    // -------- Positive tests --------
    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = PdfUALogMessageConstants.PAGE_FLUSHING_DISABLED)})
    public void flushPreviousPageTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "hugeDocumentTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_hugeDocumentTest.pdf";

        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));

        Document doc = new Document(pdfDoc);

        String longHeader = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
                + "Donec ac malesuada tellus. "
                + "Quisque a arcu semper, tristique nibh eu, convallis lacus. "
                + "Donec neque justo, condimentum sed molestie ac, mollis eu nibh. "
                + "Vivamus pellentesque condimentum fringilla. "
                + "Nullam euismod ac risus a semper. "
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
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void hnInheritedSequenceTest() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
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
            }
        });
        framework.assertBothValid("hnInheritedSequenceTest");
    }

    @Test
    public void hnCompareWithLastFromAnotherBranchTest() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
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
            }
        });
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph h5 = new Paragraph("Second Header level 5 in doc");
                h5.setFont(loadFont());
                h5.getAccessibilityProperties().setRole(StandardRoles.H5);
                return h5;
            }
        });
        framework.assertBothValid("hnInheritedSequenceTest");
    }

    @Test
    public void hnCompareWithLastFromAnotherBranchTest2() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
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
            }
        });
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph h33 = new Paragraph("Second Header level 3 in doc");
                h33.setFont(loadFont());
                h33.getAccessibilityProperties().setRole(StandardRoles.H3);
                return h33;
            }
        });
        framework.assertBothValid("hnCompareWithLastFromAnotherBranchTest2");
    }

    @Test
    public void hnInheritedSequenceTest2() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
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
            }
        });
        framework.assertBothValid("hnCompareWithLastFromAnotherBranchTest2");
    }

    @Test
    public void hnParallelSequenceTest() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph h1 = new Paragraph("Header level 1");
                h1.setFont(loadFont());
                h1.getAccessibilityProperties().setRole(StandardRoles.H1);
                return h1;
            }
        });
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph h2 = new Paragraph("Header level 2");
                h2.setFont(loadFont());
                h2.getAccessibilityProperties().setRole(StandardRoles.H2);
                return h2;
            }
        });
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph h3 = new Paragraph("Header level 3");
                h3.setFont(loadFont());
                h3.getAccessibilityProperties().setRole(StandardRoles.H3);
                return h3;
            }
        });
        framework.assertBothValid("hnParallelSequenceTest");
    }

    @Test
    public void usualHTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "usualHTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_usualHTest.pdf";

        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));

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

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
        // VeraPdf here throw exception that "A node contains more than one H tag", because
        // it seems that VeraPdf consider div as a not grouping element. See usualHTest2 test
        // with the same code, but div role is replaced by section role
    }

    @Test
    public void usualHTest2() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
                Paragraph header = new Paragraph("Header");
                header.setFont(loadFont());
                header.getAccessibilityProperties().setRole(StandardRoles.H);
                return header;
            }
        });
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
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
            }
        });
        framework.assertBothValid("hnParallelSequenceTest");
    }

    @Test
    public void hnMixedSequenceTest() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
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
            }
        });
        framework.assertBothValid("hnMixedSequenceTest");
    }

    @Test
    public void hnMixedSequenceTest2() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
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
            }
        });
        framework.assertBothValid("hnMixedSequenceTest2");
    }

    @Test
    public void hnMixedSequenceTest3() throws IOException {
        framework.addSuppliers(new Generator<IBlockElement>() {
            @Override
            public IBlockElement generate() {
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
            }
        });
        framework.assertBothValid("hnMixedSequenceTest3");
    }

    private static PdfFont loadFont() {
        try {
            return PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
