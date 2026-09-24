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
package com.itextpdf.layout;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.VerticalTextOrientation;
import com.itextpdf.layout.properties.WritingMode;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Tag("IntegrationTest")
public class VerticalTextTaggingTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/VerticalTextTaggingTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/layout/VerticalTextTaggingTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void glyphOrientationForParagraphAndSpanTest() throws IOException {
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(
                DESTINATION_FOLDER + "glyphOrientationForParagraphAndSpanTest.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            pdfDocument.setTagged();

            Document document = new Document(pdfDocument);
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);

            Text span = new Text("Vertical tagged text");
            span.getAccessibilityProperties().setRole(PdfName.Span.getValue());
            paragraph.add(span);
            document.add(paragraph);

            PdfStructElem documentStructElem = (PdfStructElem) pdfDocument.getStructTreeRoot().getKids().get(0);
            PdfStructElem paragraphStructElem = findFirstStructElemByRole(documentStructElem, PdfName.P);
            PdfStructElem spanStructElem = findFirstStructElemByRole(documentStructElem, PdfName.Span);

            assertGlyphOrientationVertical(paragraphStructElem, true);
            assertGlyphOrientationVertical(spanStructElem, true);
            document.close();
        }
    }

    @Test
    public void glyphOrientationNotAppliedForHorizontalParagraphAndSpanTest() throws IOException {
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(
                DESTINATION_FOLDER + "glyphOrientationNotAppliedForHorizontalParagraphAndSpanTest.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            pdfDocument.setTagged();

            Document document = new Document(pdfDocument);
            Paragraph paragraph = new Paragraph();
            Text span = new Text("Horizontal tagged text");
            span.getAccessibilityProperties().setRole(PdfName.Span.getValue());
            paragraph.add(span);
            document.add(paragraph);

            PdfStructElem documentStructElem = (PdfStructElem) pdfDocument.getStructTreeRoot().getKids().get(0);
            PdfStructElem paragraphStructElem = findFirstStructElemByRole(documentStructElem, PdfName.P);
            PdfStructElem spanStructElem = findFirstStructElemByRole(documentStructElem, PdfName.Span);

            assertGlyphOrientationVertical(paragraphStructElem, false);
            assertGlyphOrientationVertical(spanStructElem, false);
            document.close();
        }
    }

    @Test
    public void glyphOrientationAppliedOnlyToVerticalTextInMixedParagraphTest() throws IOException {
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(
                DESTINATION_FOLDER + "glyphOrientationVerticalIsAppliedOnlyToVerticalTextInMixedParagraphTest.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            pdfDocument.setTagged();

            Document document = new Document(pdfDocument);
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();

            Text horizontalSpan = new Text("Horizontal span");
            horizontalSpan.getAccessibilityProperties().setRole(PdfName.Span.getValue());

            Text verticalSpan = new Text("Vertical span");
            verticalSpan.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            verticalSpan.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            verticalSpan.getAccessibilityProperties().setRole(PdfName.Span.getValue());

            paragraph.add(horizontalSpan);
            paragraph.add(verticalSpan);
            document.add(paragraph);

            PdfStructElem documentStructElem = (PdfStructElem) pdfDocument.getStructTreeRoot().getKids().get(0);
            PdfStructElem paragraphStructElem = findFirstStructElemByRole(documentStructElem, PdfName.P);
            List<PdfStructElem> spanElems = collectStructElemsByRole(documentStructElem, PdfName.Span);

            Assertions.assertEquals(2, spanElems.size());
            assertGlyphOrientationVertical(paragraphStructElem, false);
            assertGlyphOrientationVertical(spanElems.get(0), false);
            assertGlyphOrientationVertical(spanElems.get(1), true);
            document.close();
        }
    }

    @Test
    public void glyphOrientationVerticalForVerticalEmTagTest() throws IOException {
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(
                DESTINATION_FOLDER + "glyphOrientationVerticalIsAppliedForVerticalEmTagTest.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            pdfDocument.setTagged();

            Document document = new Document(pdfDocument);
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            Text emphasizedVerticalText = new Text("Vertical emphasized text");
            emphasizedVerticalText.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            emphasizedVerticalText.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            emphasizedVerticalText.getAccessibilityProperties().setRole(PdfName.Em.getValue());
            paragraph.add(emphasizedVerticalText);
            document.add(paragraph);

            PdfStructElem documentStructElem = (PdfStructElem) pdfDocument.getStructTreeRoot().getKids().get(0);
            PdfStructElem emStructElem = findFirstStructElemByRole(documentStructElem, PdfName.Em);
            assertGlyphOrientationVertical(emStructElem, true);
            document.close();
        }
    }

    @Test
    public void verticalTaggingDocumentTest() throws IOException, InterruptedException {
        String fileName = "verticalTagging";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(
                outFileName, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            pdfDocument.setTagged();
            Document document = new Document(pdfDocument);
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            paragraph.setHeight(70);
            Text span = new Text("Vertical tagged text improved recognition across wrapped lines.");
            span.getAccessibilityProperties().setRole(PdfName.Span.getValue());
            paragraph.add(span);
            document.add(paragraph);
            document.close();
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }


    private static PdfStructElem findFirstStructElemByRole(IStructureNode node, PdfName role) {
        if (node instanceof PdfStructElem) {
            PdfStructElem structElem = (PdfStructElem) node;
            if (role.equals(structElem.getRole())) {
                return structElem;
            }
        }

        List<IStructureNode> kids = node.getKids();
        if (kids == null) {
            return null;
        }
        for (IStructureNode kid : kids) {
            if (kid == null) {
                continue;
            }
            PdfStructElem found = findFirstStructElemByRole(kid, role);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private static List<PdfStructElem> collectStructElemsByRole(IStructureNode node, PdfName role) {
        List<PdfStructElem> result = new ArrayList<>();
        collectStructElemsByRole(node, role, result);
        return result;
    }

    private static void collectStructElemsByRole(IStructureNode node, PdfName role, List<PdfStructElem> result) {
        if (node instanceof PdfStructElem) {
            PdfStructElem structElem = (PdfStructElem) node;
            if (role.equals(structElem.getRole())) {
                result.add(structElem);
            }
        }

        List<IStructureNode> kids = node.getKids();
        if (kids == null) {
            return;
        }
        for (IStructureNode kid : kids) {
            if (kid != null) {
                collectStructElemsByRole(kid, role, result);
            }
        }
    }

    private static void assertGlyphOrientationVertical(PdfStructElem structElem, boolean expectedToBePresent) {
        Assertions.assertNotNull(structElem);

        PdfDictionary layoutAttributes = getLayoutAttributes(structElem);
        if (!expectedToBePresent) {
            if (layoutAttributes == null) {
                return;
            }
            Assertions.assertNull(layoutAttributes.get(PdfName.GlyphOrientationVertical));
            return;
        }

        Assertions.assertNotNull(layoutAttributes);
        PdfObject glyphOrientation = layoutAttributes.get(PdfName.GlyphOrientationVertical);
        Assertions.assertTrue(glyphOrientation instanceof PdfNumber);
        Assertions.assertEquals(0f, ((PdfNumber) glyphOrientation).floatValue(), 0.0001f);
    }

    private static PdfDictionary getLayoutAttributes(PdfStructElem structElem) {
        PdfObject attributes = structElem.getAttributes(false);
        if (attributes == null) {
            return null;
        }
        if (attributes.isDictionary()) {
            PdfDictionary dict = (PdfDictionary) attributes;
            return PdfName.Layout.equals(dict.getAsName(PdfName.O)) ? dict : null;
        }

        if (attributes.isArray()) {
            PdfArray array = (PdfArray) attributes;
            for (int i = 0; i < array.size(); i++) {
                PdfObject object = array.get(i);
                if (object != null && object.isDictionary()) {
                    PdfDictionary dict = (PdfDictionary) object;
                    if (PdfName.Layout.equals(dict.getAsName(PdfName.O))) {
                        return dict;
                    }
                }
            }
        }
        return null;
    }
}




