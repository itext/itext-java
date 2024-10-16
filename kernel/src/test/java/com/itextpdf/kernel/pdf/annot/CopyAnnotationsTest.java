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
package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.util.List;


@Tag("IntegrationTest")
public class CopyAnnotationsTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/annot"
            + "/CopyAnnotationsTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/annot/CopyAnnotationsTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @Test
    public void copyGoToRDestinationTest() throws IOException {
        String outFile = DESTINATION_FOLDER + "CopiedGoToRAnnotation.pdf";
        try (PdfDocument out = new PdfDocument(CompareTool.createTestPdfWriter(outFile))) {
            try (PdfDocument input = new PdfDocument(new PdfReader(SOURCE_FOLDER + "GoToRAnnotation.pdf"))) {
                input.copyPagesTo(1, input.getNumberOfPages(), out);
            }
        }
        List<PdfAnnotation> annotations = getAnnotationsFromPdf(outFile, 1);
        Assertions.assertEquals(1, annotations.size(), "Destination is not copied");
    }

    @Test
    public void copyMultipleGoToRDestinationTest() throws IOException {
        String outFile = DESTINATION_FOLDER + "CopiedMultiGoToRAnnotation.pdf";
        try (PdfDocument out = new PdfDocument(CompareTool.createTestPdfWriter(outFile))) {
            try (PdfDocument input = new PdfDocument(new PdfReader(SOURCE_FOLDER + "MultiDestinations.pdf"))) {
                input.copyPagesTo(1, input.getNumberOfPages(), out);
            }
        }
        List<PdfAnnotation> annotations = getAnnotationsFromPdf(outFile, 1);
        Assertions.assertEquals(2, annotations.size(), "Not all destinations are copied");
    }

    @Test
    public void copyGoToRWithoutTargetTest() throws IOException {
        String outFile = DESTINATION_FOLDER + "CopiedGoToRNoTarget.pdf";
        try (PdfDocument out = new PdfDocument(CompareTool.createTestPdfWriter(outFile))) {
            try (PdfDocument input = new PdfDocument(new PdfReader(SOURCE_FOLDER + "namedDest.pdf"))) {
                input.copyPagesTo(2, 6, out);
            }
        }
        List<PdfAnnotation> annotations = getAnnotationsFromPdf(outFile, 5);
        Assertions.assertTrue(annotations.isEmpty(), "Destinations are copied but should not");
    }

    @Test
    public void copyGoToRNamedDestinationTest() throws IOException {
        String outFile = DESTINATION_FOLDER + "CopiedGoToRNamedDest.pdf";
        try (PdfDocument out = new PdfDocument(CompareTool.createTestPdfWriter(outFile))) {
            try (PdfDocument input = new PdfDocument(new PdfReader(SOURCE_FOLDER + "namedDest.pdf"))) {
                input.copyPagesTo(1, 6, out);
            }
        }
        List<PdfAnnotation> annotations = getAnnotationsFromPdf(outFile, 6);
        Assertions.assertFalse(annotations.isEmpty(), "Annotation is copied");
        String destination = (annotations.get(0)).getPdfObject().get(PdfName.Dest).toString();
        Assertions.assertEquals("Destination_1", destination, "Destination is different from expected");
    }

    @Test
    public void fileAttachmentTargetTest() throws IOException {
        String outFile = DESTINATION_FOLDER + "CopiedFileAttachmentTarget.pdf";
        try (PdfDocument out = new PdfDocument(CompareTool.createTestPdfWriter(outFile))) {
            try (PdfDocument input = new PdfDocument(new PdfReader(SOURCE_FOLDER + "fileAttachmentTargetTest.pdf"))) {
                input.copyPagesTo(1, input.getNumberOfPages(), out);
            }
        }
        List<PdfAnnotation> annotations = getAnnotationsFromPdf(outFile, 2);
        Assertions.assertFalse(annotations.isEmpty(), "Annotation is not copied");
        String nm = annotations.get(0).getPdfObject().getAsString(PdfName.NM).toString();
        Assertions.assertEquals("FileAttachmentAnnotation1", nm, "File attachment name is different from expected");
    }

    @Test
    public void copyLinkWidgetTest() throws IOException {
        String outFile = DESTINATION_FOLDER + "CopiedLinkWidget.pdf";
        try (PdfDocument out = new PdfDocument(CompareTool.createTestPdfWriter(outFile))) {
            try (PdfDocument input = new PdfDocument(
                    new PdfReader(SOURCE_FOLDER + "LinkWidgetExplicitDestination.pdf"))) {
                input.copyPagesTo(1, input.getNumberOfPages(), out);
            }
        }
        List<PdfAnnotation> annotations = getAnnotationsFromPdf(outFile, 1);
        Assertions.assertFalse(annotations.isEmpty(), "Annotation is not copied");
        Assertions.assertEquals(PdfName.Widget, annotations.get(0).getSubtype(), "Annotation is of a different subtype");
    }

    @Test
    public void noPdfNameATest() throws IOException {
        String outFile = DESTINATION_FOLDER + "CopiedNoPdfNameA.pdf";
        try (PdfDocument out = new PdfDocument(CompareTool.createTestPdfWriter(outFile))) {
            try (PdfDocument input = new PdfDocument(new PdfReader(SOURCE_FOLDER + "GoToRAnnotation.pdf"))) {
                PdfAnnotation pdfAnnotation = input.getPage(1).getAnnotations().get(0);
                pdfAnnotation.getPdfObject().remove(PdfName.A);
                input.copyPagesTo(1, input.getNumberOfPages(), out);
            }
        }
        List<PdfAnnotation> annotations = getAnnotationsFromPdf(outFile, 1);
        Assertions.assertFalse(annotations.isEmpty(), "Annotation is not copied");
    }

    @Test
    public void noPdfNameDTest() throws IOException {
        String outFile = DESTINATION_FOLDER + "CopiedNoPdfNameD.pdf";
        try (PdfDocument out = new PdfDocument(CompareTool.createTestPdfWriter(outFile))) {
            try (PdfDocument input = new PdfDocument(new PdfReader(SOURCE_FOLDER + "GoToRAnnotation.pdf"))) {
                PdfAnnotation pdfAnnotation = input.getPage(1).getAnnotations().get(0);
                pdfAnnotation.getPdfObject().getAsDictionary(PdfName.A).remove(PdfName.D);

                input.copyPagesTo(1, input.getNumberOfPages(), out);
            }
        }
        List<PdfAnnotation> annotations = getAnnotationsFromPdf(outFile, 1);
        Assertions.assertFalse(annotations.isEmpty(), "Annotation is not copied");
    }

    @Test
    public void noPdfNameSTest() throws IOException {
        String outFile = DESTINATION_FOLDER + "CopiedNoPdfNameS.pdf";
        try (PdfDocument out = new PdfDocument(CompareTool.createTestPdfWriter(outFile))) {
            try (PdfDocument input = new PdfDocument(new PdfReader(SOURCE_FOLDER + "GoToRAnnotation.pdf"))) {
                PdfAnnotation pdfAnnotation = input.getPage(1).getAnnotations().get(0);
                pdfAnnotation.getPdfObject().getAsDictionary(PdfName.A).remove(PdfName.S);
                input.copyPagesTo(1, input.getNumberOfPages(), out);
            }
        }
        List<PdfAnnotation> annotations = getAnnotationsFromPdf(outFile, 1);
        Assertions.assertTrue(annotations.isEmpty(), "Annotation is copied");
    }

    @Test
    public void noPdfNameDWithGoToRTest() throws IOException {
        String outFile = DESTINATION_FOLDER + "CopiedNoPdfNameDGoToR.pdf";
        try (PdfDocument out = new PdfDocument(CompareTool.createTestPdfWriter(outFile))) {
            try (PdfDocument input = new PdfDocument(new PdfReader(SOURCE_FOLDER + "GoToRAnnotation.pdf"))) {
                PdfAnnotation pdfAnnotation = input.getPage(1).getAnnotations().get(0);
                PdfDictionary aDictionary = pdfAnnotation.getPdfObject().getAsDictionary(PdfName.A);
                aDictionary.remove(PdfName.D);
                aDictionary.remove(PdfName.S);
                aDictionary.put(PdfName.S, PdfName.GoToR);
                input.copyPagesTo(1, input.getNumberOfPages(), out);
            }
        }
        List<PdfAnnotation> annotations = getAnnotationsFromPdf(outFile, 1);
        Assertions.assertFalse(annotations.isEmpty(), "Annotation is not copied");
    }

    @Test
    public void linkInsideArray() throws IOException {
        String outFile = DESTINATION_FOLDER + "CopiedLinkInArray.pdf";
        try (PdfDocument out = new PdfDocument(CompareTool.createTestPdfWriter(outFile))) {
            try (PdfDocument input = new PdfDocument(new PdfReader(SOURCE_FOLDER + "LinkInArray.pdf"))) {
                input.copyPagesTo(1, 1, out);
            }
        }
        List<PdfAnnotation> annotations = getAnnotationsFromPdf(outFile, 1);
        Assertions.assertTrue(annotations.isEmpty(), "Annotation is copied");
    }

    private List<PdfAnnotation> getAnnotationsFromPdf(String outFilePath, int pageNumber) throws IOException {
        List<PdfAnnotation> annotations;
        try (PdfDocument result = new PdfDocument(CompareTool.createOutputReader(outFilePath))) {
            annotations = result.getPage(pageNumber).getAnnotations();
        }
        return annotations;
    }
}
