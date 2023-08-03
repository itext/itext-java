/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
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


    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void linkAnnotationExplicitDestinationMissingTest() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_WITH_DESTINATION_EXPLICIT);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER, "LinkAnnotationExplicitDestinationMissing.pdf").toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 2, targetDoc, 2);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 2 and verify the annotation is not copied
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(2).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }

        Assert.assertNull(annot);
    }

    @Test
    public void linkAnnotationExplicitDestinationTargetBecomesPage5Test() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_WITH_DESTINATION_EXPLICIT);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER, "LinkAnnotationExplicitDestinationTargetBecomesPage5.pdf")
                .toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 3, targetDoc, 3);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 3 and verify it points to page 5
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(3).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }

        Assert.assertNotNull(annot);
        PdfDestination dest = PdfDestination.makeDestination(((PdfLinkAnnotation) annot).getDestinationObject());

        Assert.assertEquals(resultDoc.getPage(5).getPdfObject(),
                dest.getDestinationPage(resultDoc.getCatalog().getNameTree(PdfName.Dests)));
    }

    @Test
    public void linkAnnotationNamedDestinationMissingTest() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_WITH_DESTINATION_NAMED);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER, "LinkAnnotationNamedDestinationMissing.pdf").toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 2, targetDoc, 2);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 2 and verify the annotation is not copied
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(2).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }
        Assert.assertNull(annot);
        // verify wether name is removed
        Assert.assertTrue(resultDoc.getCatalog().getNameTree(PdfName.Dests).getNames().isEmpty());
    }

    @Test
    public void linkAnnotationNamedDestinationTargetBecomesPage5Test() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_WITH_DESTINATION_NAMED);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER, "LinkAnnotationNamedDestinationTargetBecomesPage5.pdf")
                .toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 3, targetDoc, 3);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 3 and verify it points to page 5
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(3).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }

        Assert.assertNotNull(annot);
        PdfDestination dest = PdfDestination.makeDestination(((PdfLinkAnnotation) annot).getDestinationObject());

        Assert.assertEquals(resultDoc.getPage(5).getPdfObject(),
                dest.getDestinationPage(resultDoc.getCatalog().getNameTree(PdfName.Dests)));
    }

    @Test
    public void linkAnnotationViaActionExplicitDestinationMissingTest() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_VIA_ACTION_WITH_DESTINATION_EXPLICIT);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER, "TestLinkAnnotationViaActionExplicitDestinationMissing.pdf")
                .toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 2, targetDoc, 2);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 2 and verify the annotation is not copied
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(2).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }

        Assert.assertNull(annot);
    }

    @Test
    public void linkAnnotationViaActionExplicitDestinationTargetBecomesPage5Test() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_VIA_ACTION_WITH_DESTINATION_EXPLICIT);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER,
                "LinkAnnotationViaActionExplicitDestinationTargetBecomesPage5.pdf").toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 3, targetDoc, 3);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 3 and verify it points to page 5
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(3).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }

        Assert.assertNotNull(annot);        PdfDestination dest = PdfDestination.makeDestination(
                ((PdfLinkAnnotation) annot).getAction().get(PdfName.D));

        Assert.assertEquals(resultDoc.getPage(5).getPdfObject(),
                dest.getDestinationPage(resultDoc.getCatalog().getNameTree(PdfName.Dests)));
    }

    @Test
    public void linkAnnotationViaActionNamedDestinationMissingTest() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_VIA_ACTION_WITH_DESTINATION_NAMED);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER, "LinkAnnotationViaActionNamedDestinationMissing.pdf")
                .toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 2, targetDoc, 2);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 2 and verify the annotation is not copied
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(2).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }

        Assert.assertNull(annot);

        // verify wether name is removed
        Assert.assertTrue(resultDoc.getCatalog().getNameTree(PdfName.Dests).getNames().isEmpty());
    }

    @Test
    public void linkAnnotationViaActionNamedDestinationTargetBecomesPage5Test() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_VIA_ACTION_WITH_DESTINATION_NAMED);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER,
                "LinkAnnotationViaActionNamedDestinationTargetBecomesPage5.pdf").toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 3, targetDoc, 3);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 3 and verify it points to page 5
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(3).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }

        Assert.assertNotNull(annot);PdfDestination dest = PdfDestination.makeDestination(
                ((PdfLinkAnnotation) annot).getAction().get(PdfName.D));

        Assert.assertEquals(resultDoc.getPage(5).getPdfObject(),
                dest.getDestinationPage(resultDoc.getCatalog().getNameTree(PdfName.Dests)));
    }


    @Test
    public void linkAnnotationViaNextActionExplicitDestinationMissingTest() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_VIA_NEXT_ACTION_WITH_DESTINATION_EXPLICIT);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER,
                "TestLinkAnnotationViaNextActionExplicitDestinationMissing.pdf").toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 2, targetDoc, 2);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 2 and verify the annotation is not copied
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(2).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }

        Assert.assertNotNull(annot);
        Assert.assertNull(((PdfLinkAnnotation) annot).getAction().get(PdfName.Next));
    }

    @Test
    public void linkAnnotationViaNextActionExplicitDestinationTargetBecomesPage5Test() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_VIA_NEXT_ACTION_WITH_DESTINATION_EXPLICIT);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER,
                "LinkAnnotationViaNextActionExplicitDestinationTargetBecomesPage5.pdf").toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 3, targetDoc, 3);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 3 and verify it points to page 5
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(3).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }

        Assert.assertNotNull(annot);
        PdfDestination dest = PdfDestination.makeDestination(
                ((PdfLinkAnnotation) annot).getAction().getAsDictionary(PdfName.Next).get(PdfName.D));

        Assert.assertEquals(resultDoc.getPage(5).getPdfObject(),
                dest.getDestinationPage(resultDoc.getCatalog().getNameTree(PdfName.Dests)));
    }

    @Test
    public void linkAnnotationViaNextActionNamedDestinationMissingTest() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_VIA_NEXT_ACTION_WITH_DESTINATION_NAMED);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER, "LinkAnnotationViaNextActionNamedDestinationMissing.pdf")
                .toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 2, targetDoc, 2);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 2 and verify the annotation is not copied
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(2).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }

        Assert.assertNotNull(annot);
        Assert.assertNull(((PdfLinkAnnotation) annot).getAction().get(PdfName.Next));
        // verify whether name is removed
        Assert.assertTrue(resultDoc.getCatalog().getNameTree(PdfName.Dests).getNames().isEmpty());
    }

    @Test
    public void linkAnnotationViaNextActionNamedDestinationTargetBecomesPage5Test() throws IOException {
        PdfReader sourceReader = new PdfReader(SOURCE_ANNOTATION_VIA_NEXT_ACTION_WITH_DESTINATION_NAMED);
        PdfDocument copySource = new PdfDocument(sourceReader);

        PdfReader targetReader = new PdfReader(TARGET_DOC);
        String outputPath = Paths.get(DESTINATION_FOLDER,
                "LinkAnnotationViaNextActionNamedDestinationTargetBecomesPage5.pdf").toString();
        PdfDocument targetDoc = new PdfDocument(targetReader, new PdfWriter(outputPath));

        copySource.copyPagesTo(1, 3, targetDoc, 3);

        copySource.close();
        targetDoc.close();

        PdfReader resultReader = new PdfReader(outputPath);
        PdfDocument resultDoc = new PdfDocument(resultReader);

        // get annotation on page 3 and verify it points to page 5
        PdfAnnotation annot = null;
        for (PdfAnnotation item : resultDoc.getPage(3).getAnnotations()) {
            if (item.getSubtype() == PdfName.Link) {
                annot = item;
                break;
            }
        }

        Assert.assertNotNull(annot);PdfDestination dest = PdfDestination.makeDestination(
                ((PdfLinkAnnotation) annot).getAction().getAsDictionary(PdfName.Next).get(PdfName.D));

        Assert.assertEquals(resultDoc.getPage(5).getPdfObject(),
                dest.getDestinationPage(resultDoc.getCatalog().getNameTree(PdfName.Dests)));
    }
}
