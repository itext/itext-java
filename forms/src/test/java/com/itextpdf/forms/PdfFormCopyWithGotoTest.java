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
package com.itextpdf.forms;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class
PdfFormCopyWithGotoTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/forms/PdfFormCopyWithGotoTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/forms/PdfFormCopyWithGotoTest/";
    public static final String TARGET_DOC = SOURCE_FOLDER + "Target.pdf";

    public static final String SOURCE_WIDGET_ACTION_WITH_DESTINATION_EXPLICIT =
            SOURCE_FOLDER + "LinkWidgetExplicitDestination.pdf";
    public static final String SOURCE_WIDGET_ACTION_WITH_DESTINATION_NAMED =
            SOURCE_FOLDER + "LinkWidgetNamedDestination.pdf";

    public static final String SOURCE_WIDGET_ADDITIONAL_ACTION_DOWN_DESTINATION_EXPLICIT =
            SOURCE_FOLDER + "LinkWidgetAAMouseDownExplicitDestination.pdf";
    public static final String SOURCE_WIDGET_ADDITIONAL_ACTION_DOWN_DESTINATION_NAMED =
            SOURCE_FOLDER + "LinkWidgetAAMouseDownNamedDestination.pdf";

    public static final String SOURCE_WIDGET_ADDITIONAL_ACTION_UP_DESTINATION_EXPLICIT =
            SOURCE_FOLDER + "LinkWidgetAAMouseUpExplicitDestination.pdf";
    public static final String SOURCE_WIDGET_ADDITIONAL_ACTION_UP_DESTINATION_NAMED =
            SOURCE_FOLDER + "LinkWidgetAAMouseUpNamedDestination.pdf";


    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void testLinkWidgetNamedDestinationMissing() throws IOException, InterruptedException {
        PdfReader sourceReader = new PdfReader(SOURCE_WIDGET_ACTION_WITH_DESTINATION_NAMED);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER, "TestLinkWidgetNamedDestinationMissing.pdf").toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 2, targetDoc, 2, new PdfPageFormCopier());

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 2 and verify the annotation is not copied
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(2).getAnnotations()) {
            if (item.getSubtype() == PdfName.Widget) {
                annot = item;
                break;
            }
        }
        Assertions.assertNull(((PdfWidgetAnnotation) annot).getAction());
        // verify wether name is removed
        Assertions.assertTrue(resultDoc.getCatalog().getNameTree(PdfName.Dests).getNames().isEmpty());
    }

    @Test
    public void testLinkWidgetNamedDestinationTargetBecomesPage5() throws IOException, InterruptedException {
        PdfReader sourceReader = new PdfReader(SOURCE_WIDGET_ACTION_WITH_DESTINATION_NAMED);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER, "TestLinkWidgetNamedDestinationTargetBecomesPage5.pdf")
                .toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 3, targetDoc, 3, new PdfPageFormCopier());

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 3 and verify it points to page 5
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(3).getAnnotations()) {
            if (item.getSubtype() == PdfName.Widget) {
                annot = item;
                break;
            }
        }
        Assertions.assertNotNull(annot);
        PdfDestination dest = PdfDestination.makeDestination(
                ((PdfWidgetAnnotation) annot).getAction().get(PdfName.D));

        Assertions.assertEquals(resultDoc.getPage(5).getPdfObject(),
                dest.getDestinationPage(resultDoc.getCatalog().getNameTree(PdfName.Dests)));
    }


    @Test
    public void testLinkWidgetExplicitDestinationMissing() throws IOException, InterruptedException {
        PdfReader sourceReader = new PdfReader(SOURCE_WIDGET_ACTION_WITH_DESTINATION_EXPLICIT);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER, "TestLinkWidgetExplicitDestinationMissing.pdf").toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 2, targetDoc, 2, new PdfPageFormCopier());

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 2 and verify the annotation is not copied
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(2).getAnnotations()) {
            if (item.getSubtype() == PdfName.Widget) {
                annot = item;
                break;
            }
        }
        Assertions.assertNull(((PdfWidgetAnnotation) annot).getAction());
        // verify wether name is removed
        Assertions.assertTrue(resultDoc.getCatalog().getNameTree(PdfName.Dests).getNames().isEmpty());
    }

    @Test
    public void testLinkWidgetExplicitDestinationTargetBecomesPage5() throws IOException, InterruptedException {
        PdfReader sourceReader = new PdfReader(SOURCE_WIDGET_ACTION_WITH_DESTINATION_EXPLICIT);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER, "TestLinkWidgetExplicitDestinationTargetBecomesPage5.pdf")
                .toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 3, targetDoc, 3, new PdfPageFormCopier());

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 3 and verify it points to page 5
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(3).getAnnotations()) {
            if (item.getSubtype() == PdfName.Widget) {
                annot = item;
                break;
            }
        }
        Assertions.assertNotNull(annot);
        PdfDestination dest = PdfDestination.makeDestination(
                ((PdfWidgetAnnotation) annot).getAction().get(PdfName.D));

        Assertions.assertEquals(resultDoc.getPage(5).getPdfObject(),
                dest.getDestinationPage(resultDoc.getCatalog().getNameTree(PdfName.Dests)));
    }


    @Test
    public void testLinkWidgetAAUpNamedDestinationMissing() throws IOException, InterruptedException {
        PdfReader sourceReader = new PdfReader(SOURCE_WIDGET_ADDITIONAL_ACTION_UP_DESTINATION_NAMED);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER, "TestLinkWidgetAAUpNamedDestinationMissing.pdf").toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 2, targetDoc, 2, new PdfPageFormCopier());

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 2 and verify the annotation is not copied
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(2).getAnnotations()) {
            if (item.getSubtype() == PdfName.Widget) {
                annot = item;
                break;
            }
        }
        Assertions.assertNull(((PdfWidgetAnnotation) annot).getAction());
        // verify wether name is removed
        Assertions.assertTrue(resultDoc.getCatalog().getNameTree(PdfName.Dests).getNames().isEmpty());
    }

    @Test
    public void testLinkWidgetAAUpNamedDestinationTargetBecomesPage5() throws IOException, InterruptedException {
        PdfReader sourceReader = new PdfReader(SOURCE_WIDGET_ADDITIONAL_ACTION_UP_DESTINATION_NAMED);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER, "TestLinkWidgetAAUpNamedDestinationTargetBecomesPage5.pdf")
                .toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 3, targetDoc, 3, new PdfPageFormCopier());

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 3 and verify it points to page 5
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(3).getAnnotations()) {
            if (item.getSubtype() == PdfName.Widget) {
                annot = item;
                break;
            }
        }
        Assertions.assertNotNull(annot);
        PdfDestination dest = PdfDestination.makeDestination(
                ((PdfWidgetAnnotation) annot).getAdditionalAction().getAsDictionary(PdfName.U).get(PdfName.D));

        Assertions.assertEquals(resultDoc.getPage(5).getPdfObject(),
                dest.getDestinationPage(resultDoc.getCatalog().getNameTree(PdfName.Dests)));
    }


    @Test
    public void testLinkWidgetAAUpExplicitDestinationMissing() throws IOException, InterruptedException {
        PdfReader sourceReader = new PdfReader(SOURCE_WIDGET_ADDITIONAL_ACTION_UP_DESTINATION_EXPLICIT);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER, "TestLinkWidgetAAUpExplicitDestinationMissing.pdf")
                .toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 2, targetDoc, 2, new PdfPageFormCopier());

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 2 and verify the annotation is not copied
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(2).getAnnotations()) {
            if (item.getSubtype() == PdfName.Widget) {
                annot = item;
                break;
            }
        }
        Assertions.assertNull(((PdfWidgetAnnotation) annot).getAction());
        // verify wether name is removed
        Assertions.assertTrue(resultDoc.getCatalog().getNameTree(PdfName.Dests).getNames().isEmpty());
    }

    @Test
    public void testLinkWidgetAAUpExplicitDestinationTargetBecomesPage5() throws IOException, InterruptedException {
        PdfReader sourceReader = new PdfReader(SOURCE_WIDGET_ADDITIONAL_ACTION_UP_DESTINATION_EXPLICIT);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER, "TestLinkWidgetAAUpExplicitDestinationTargetBecomesPage5.pdf")
                .toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 3, targetDoc, 3, new PdfPageFormCopier());

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 3 and verify it points to page 5
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(3).getAnnotations()) {
            if (item.getSubtype() == PdfName.Widget) {
                annot = item;
                break;
            }
        }
        Assertions.assertNotNull(annot);
        PdfDestination dest = PdfDestination.makeDestination(
                ((PdfWidgetAnnotation) annot).getAdditionalAction().getAsDictionary(PdfName.U).get(PdfName.D));

        Assertions.assertEquals(resultDoc.getPage(5).getPdfObject(),
                dest.getDestinationPage(resultDoc.getCatalog().getNameTree(PdfName.Dests)));
    }


    @Test
    public void testLinkWidgetAADownNamedDestinationMissing() throws IOException, InterruptedException {
        PdfReader sourceReader = new PdfReader(SOURCE_WIDGET_ADDITIONAL_ACTION_DOWN_DESTINATION_NAMED);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER, "TestLinkWidgetAADownNamedDestinationMissing.pdf").toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 2, targetDoc, 2, new PdfPageFormCopier());

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 2 and verify the annotation is not copied
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(2).getAnnotations()) {
            if (item.getSubtype() == PdfName.Widget) {
                annot = item;
                break;
            }
        }
        Assertions.assertNull(((PdfWidgetAnnotation) annot).getAction());
        // verify wether name is removed
        Assertions.assertTrue(resultDoc.getCatalog().getNameTree(PdfName.Dests).getNames().isEmpty());
    }

    @Test
    public void testLinkWidgetAADownNamedDestinationTargetBecomesPage5() throws IOException, InterruptedException {
        PdfReader sourceReader = new PdfReader(SOURCE_WIDGET_ADDITIONAL_ACTION_DOWN_DESTINATION_NAMED);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER, "TestLinkWidgetAADownNamedDestinationTargetBecomesPage5.pdf")
                .toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 3, targetDoc, 3, new PdfPageFormCopier());

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 3 and verify it points to page 5
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(3).getAnnotations()) {
            if (item.getSubtype() == PdfName.Widget) {
                annot = item;
                break;
            }
        }
        Assertions.assertNotNull(annot);
        PdfDestination dest = PdfDestination.makeDestination(
                ((PdfWidgetAnnotation) annot).getAdditionalAction().getAsDictionary(PdfName.D).get(PdfName.D));

        Assertions.assertEquals(resultDoc.getPage(5).getPdfObject(),
                dest.getDestinationPage(resultDoc.getCatalog().getNameTree(PdfName.Dests)));
    }


    @Test
    public void testLinkWidgetAADownExplicitDestinationMissing() throws IOException, InterruptedException {
        PdfReader sourceReader = new PdfReader(SOURCE_WIDGET_ADDITIONAL_ACTION_DOWN_DESTINATION_EXPLICIT);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER, "TestLinkWidgetAADownExplicitDestinationMissing.pdf")
                .toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 2, targetDoc, 2, new PdfPageFormCopier());

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 2 and verify the annotation is not copied
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(2).getAnnotations()) {
            if (item.getSubtype() == PdfName.Widget) {
                annot = item;
                break;
            }
        }
        Assertions.assertNull(((PdfWidgetAnnotation) annot).getAction());
        // verify wether name is removed
        Assertions.assertTrue(resultDoc.getCatalog().getNameTree(PdfName.Dests).getNames().isEmpty());
    }

    @Test
    public void testLinkWidgetAADownExplicitDestinationTargetBecomesPage5() throws IOException, InterruptedException {
        PdfReader sourceReader = new PdfReader(SOURCE_WIDGET_ADDITIONAL_ACTION_DOWN_DESTINATION_EXPLICIT);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER,
                "TestLinkWidgetAADownExplicitDestinationTargetBecomesPage5.pdf").toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 3, targetDoc, 3, new PdfPageFormCopier());

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 3 and verify it points to page 5
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(3).getAnnotations()) {
            if (item.getSubtype() == PdfName.Widget) {
                annot = item;
                break;
            }
        }
        Assertions.assertNotNull(annot);
        PdfDestination dest = PdfDestination.makeDestination(
                ((PdfWidgetAnnotation) annot).getAdditionalAction().getAsDictionary(PdfName.D).get(PdfName.D));

        Assertions.assertEquals(resultDoc.getPage(5).getPdfObject(),
                dest.getDestinationPage(resultDoc.getCatalog().getNameTree(PdfName.Dests)));
    }

}
