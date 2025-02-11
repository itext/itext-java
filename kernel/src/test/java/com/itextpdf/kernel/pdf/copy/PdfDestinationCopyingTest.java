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
package com.itextpdf.kernel.pdf.copy;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfDestinationCopyingTest extends ExtendedITextTest {

    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/PdfDestinationCopyingTest/";
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf"
            + "/PdfDestinationCopyingTest/";
    public static final String TARGET_DOC = "./src/test/resources/com/itextpdf/kernel/pdf/PdfDestinationCopyingTest"
            + "/Target.pdf";

    public static final String SOURCE_ANNOTATION_WITH_DESTINATION_EXPLICIT =
            SOURCE_FOLDER + "LinkAnnotationViaDestExplicitDestination.pdf";
    public static final String SOURCE_ANNOTATION_WITH_DESTINATION_NAMED =
            SOURCE_FOLDER + "LinkAnnotationViaDestNamedDestination.pdf";

    public static final String SOURCE_ANNOTATION_VIA_ACTION_WITH_DESTINATION_EXPLICIT =
            SOURCE_FOLDER + "LinkAnnotationViaActionExplicitDestination.pdf";
    public static final String SOURCE_ANNOTATION_VIA_ACTION_WITH_DESTINATION_NAMED =
            SOURCE_FOLDER + "LinkAnnotationViaActionNamedDestination.pdf";

    public static final String SOURCE_ANNOTATION_VIA_NEXT_ACTION_WITH_DESTINATION_EXPLICIT =
            SOURCE_FOLDER + "LinkAnnotationViaActionWithNextActionExplicitDestination.pdf";
    public static final String SOURCE_ANNOTATION_VIA_NEXT_ACTION_WITH_DESTINATION_NAMED =
            SOURCE_FOLDER + "LinkAnnotationViaActionWithNextActionNamedDestination.pdf";


    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @Test
    public void linkAnnotationExplicitDestinationMissingTest() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_WITH_DESTINATION_EXPLICIT);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = DESTINATION_FOLDER + "LinkAnnotationExplicitDestinationMissing.pdf";
        PdfDocument targetDoc = new PdfDocument(targetReader, CompareTool.createTestPdfWriter(outputPath));

        copySource.copyPagesTo(1, 2, targetDoc, 2);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = CompareTool.createOutputReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 2 and verify the annotation is not copied
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(2).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }

        Assertions.assertNull(annot);
    }

    @Test
    public void linkAnnotationExplicitDestinationTargetBecomesPage5Test() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_WITH_DESTINATION_EXPLICIT);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = DESTINATION_FOLDER + "LinkAnnotationExplicitDestinationTargetBecomesPage5.pdf";
        PdfDocument targetDoc = new PdfDocument(targetReader, CompareTool.createTestPdfWriter(outputPath));

        copySource.copyPagesTo(1, 3, targetDoc, 3);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = CompareTool.createOutputReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 3 and verify it points to page 5
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(3).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }

        Assertions.assertNotNull(annot);
        PdfDestination dest = PdfDestination.makeDestination(((PdfLinkAnnotation) annot).getDestinationObject());

        Assertions.assertEquals(resultDoc.getPage(5).getPdfObject(),
                dest.getDestinationPage(resultDoc.getCatalog().getNameTree(PdfName.Dests)));
    }

    @Test
    public void linkAnnotationNamedDestinationMissingTest() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_WITH_DESTINATION_NAMED);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = DESTINATION_FOLDER + "LinkAnnotationNamedDestinationMissing.pdf";
        PdfDocument targetDoc = new PdfDocument(targetReader, CompareTool.createTestPdfWriter(outputPath));

        copySource.copyPagesTo(1, 2, targetDoc, 2);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = CompareTool.createOutputReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 2 and verify the annotation is not copied
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(2).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }
        Assertions.assertNull(annot);
        // verify wether name is removed
        Assertions.assertTrue(resultDoc.getCatalog().getNameTree(PdfName.Dests).getNames().isEmpty());
    }

    @Test
    public void linkAnnotationNamedDestinationTargetBecomesPage5Test() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_WITH_DESTINATION_NAMED);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = DESTINATION_FOLDER + "LinkAnnotationNamedDestinationTargetBecomesPage5.pdf";
        PdfDocument targetDoc = new PdfDocument(targetReader, CompareTool.createTestPdfWriter(outputPath));

        copySource.copyPagesTo(1, 3, targetDoc, 3);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = CompareTool.createOutputReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 3 and verify it points to page 5
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(3).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }

        Assertions.assertNotNull(annot);
        PdfDestination dest = PdfDestination.makeDestination(((PdfLinkAnnotation) annot).getDestinationObject());

        Assertions.assertEquals(resultDoc.getPage(5).getPdfObject(),
                dest.getDestinationPage(resultDoc.getCatalog().getNameTree(PdfName.Dests)));
    }

    @Test
    public void linkAnnotationViaActionExplicitDestinationMissingTest() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_VIA_ACTION_WITH_DESTINATION_EXPLICIT);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = DESTINATION_FOLDER + "TestLinkAnnotationViaActionExplicitDestinationMissing.pdf";
        PdfDocument targetDoc = new PdfDocument(targetReader, CompareTool.createTestPdfWriter(outputPath));

        copySource.copyPagesTo(1, 2, targetDoc, 2);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = CompareTool.createOutputReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 2 and verify the annotation is not copied
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(2).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }

        Assertions.assertNull(annot);
    }

    @Test
    public void linkAnnotationViaActionExplicitDestinationTargetBecomesPage5Test() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_VIA_ACTION_WITH_DESTINATION_EXPLICIT);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = DESTINATION_FOLDER + "LinkAnnotationViaActionExplicitDestinationTargetBecomesPage5.pdf";
        PdfDocument targetDoc = new PdfDocument(targetReader, CompareTool.createTestPdfWriter(outputPath));

        copySource.copyPagesTo(1, 3, targetDoc, 3);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = CompareTool.createOutputReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 3 and verify it points to page 5
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(3).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }

        Assertions.assertNotNull(annot);        PdfDestination dest = PdfDestination.makeDestination(
                ((PdfLinkAnnotation) annot).getAction().get(PdfName.D));

        Assertions.assertEquals(resultDoc.getPage(5).getPdfObject(),
                dest.getDestinationPage(resultDoc.getCatalog().getNameTree(PdfName.Dests)));
    }

    @Test
    public void linkAnnotationViaActionNamedDestinationMissingTest() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_VIA_ACTION_WITH_DESTINATION_NAMED);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = DESTINATION_FOLDER + "LinkAnnotationViaActionNamedDestinationMissing.pdf";
        PdfDocument targetDoc = new PdfDocument(targetReader, CompareTool.createTestPdfWriter(outputPath));

        copySource.copyPagesTo(1, 2, targetDoc, 2);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = CompareTool.createOutputReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 2 and verify the annotation is not copied
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(2).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }

        Assertions.assertNull(annot);

        // verify wether name is removed
        Assertions.assertTrue(resultDoc.getCatalog().getNameTree(PdfName.Dests).getNames().isEmpty());
    }

    @Test
    public void linkAnnotationViaActionNamedDestinationTargetBecomesPage5Test() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_VIA_ACTION_WITH_DESTINATION_NAMED);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = DESTINATION_FOLDER + "LinkAnnotationViaActionNamedDestinationTargetBecomesPage5.pdf";
        PdfDocument targetDoc = new PdfDocument(targetReader, CompareTool.createTestPdfWriter(outputPath));

        copySource.copyPagesTo(1, 3, targetDoc, 3);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = CompareTool.createOutputReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 3 and verify it points to page 5
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(3).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }

        Assertions.assertNotNull(annot);PdfDestination dest = PdfDestination.makeDestination(
                ((PdfLinkAnnotation) annot).getAction().get(PdfName.D));

        Assertions.assertEquals(resultDoc.getPage(5).getPdfObject(),
                dest.getDestinationPage(resultDoc.getCatalog().getNameTree(PdfName.Dests)));
    }


    @Test
    public void linkAnnotationViaNextActionExplicitDestinationMissingTest() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_VIA_NEXT_ACTION_WITH_DESTINATION_EXPLICIT);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = DESTINATION_FOLDER + "TestLinkAnnotationViaNextActionExplicitDestinationMissing.pdf";
        PdfDocument targetDoc = new PdfDocument(targetReader, CompareTool.createTestPdfWriter(outputPath));

        copySource.copyPagesTo(1, 2, targetDoc, 2);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = CompareTool.createOutputReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 2 and verify the annotation is not copied
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(2).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }

        Assertions.assertNotNull(annot);
        Assertions.assertNull(((PdfLinkAnnotation) annot).getAction().get(PdfName.Next));
    }

    @Test
    public void linkAnnotationViaNextActionExplicitDestinationTargetBecomesPage5Test() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_VIA_NEXT_ACTION_WITH_DESTINATION_EXPLICIT);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = DESTINATION_FOLDER + "LinkAnnotationViaNextActionExplicitDestinationTargetBecomesPage5.pdf";
        PdfDocument targetDoc = new PdfDocument(targetReader, CompareTool.createTestPdfWriter(outputPath));

        copySource.copyPagesTo(1, 3, targetDoc, 3);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = CompareTool.createOutputReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 3 and verify it points to page 5
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(3).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }

        Assertions.assertNotNull(annot);
        PdfDestination dest = PdfDestination.makeDestination(
                ((PdfLinkAnnotation) annot).getAction().getAsDictionary(PdfName.Next).get(PdfName.D));

        Assertions.assertEquals(resultDoc.getPage(5).getPdfObject(),
                dest.getDestinationPage(resultDoc.getCatalog().getNameTree(PdfName.Dests)));
    }

    @Test
    public void linkAnnotationViaNextActionNamedDestinationMissingTest() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_VIA_NEXT_ACTION_WITH_DESTINATION_NAMED);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = DESTINATION_FOLDER + "LinkAnnotationViaNextActionNamedDestinationMissing.pdf";
        PdfDocument targetDoc = new PdfDocument(targetReader, CompareTool.createTestPdfWriter(outputPath));

        copySource.copyPagesTo(1, 2, targetDoc, 2);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = CompareTool.createOutputReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 2 and verify the annotation is not copied
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(2).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }

        Assertions.assertNotNull(annot);
        Assertions.assertNull(((PdfLinkAnnotation) annot).getAction().get(PdfName.Next));
        // verify whether name is removed
        Assertions.assertTrue(resultDoc.getCatalog().getNameTree(PdfName.Dests).getNames().isEmpty());
    }

    @Test
    public void linkAnnotationViaNextActionNamedDestinationTargetBecomesPage5Test() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_VIA_NEXT_ACTION_WITH_DESTINATION_NAMED);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = DESTINATION_FOLDER + "LinkAnnotationViaNextActionNamedDestinationTargetBecomesPage5.pdf";
        PdfDocument targetDoc = new PdfDocument(targetReader, CompareTool.createTestPdfWriter(outputPath));

        copySource.copyPagesTo(1, 3, targetDoc, 3);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = CompareTool.createOutputReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 3 and verify it points to page 5
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(3).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }

        Assertions.assertNotNull(annot);PdfDestination dest = PdfDestination.makeDestination(
                ((PdfLinkAnnotation) annot).getAction().getAsDictionary(PdfName.Next).get(PdfName.D));

        Assertions.assertEquals(resultDoc.getPage(5).getPdfObject(),
                dest.getDestinationPage(resultDoc.getCatalog().getNameTree(PdfName.Dests)));
    }
}
