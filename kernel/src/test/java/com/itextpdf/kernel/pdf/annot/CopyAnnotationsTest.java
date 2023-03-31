package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.List;


@Category(IntegrationTest.class)
public class CopyAnnotationsTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/annot"
            + "/CopyAnnotationsTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/annot/CopyAnnotationsTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void copyGoToRDestinationTest() throws IOException {
        String outFile = DESTINATION_FOLDER + "CopiedGoToRAnnotation.pdf";
        try (PdfDocument out = new PdfDocument(new PdfWriter(outFile))) {
            try (PdfDocument input = new PdfDocument(new PdfReader(SOURCE_FOLDER + "GoToRAnnotation.pdf"))) {
                input.copyPagesTo(1, input.getNumberOfPages(), out);
            }
        }
        List<PdfAnnotation> annotations = getAnnotationsFromPdf(outFile, 1);
        Assert.assertEquals("Destination is not copied", 1, annotations.size());
    }

    @Test
    public void copyMultipleGoToRDestinationTest() throws IOException {
        String outFile = DESTINATION_FOLDER + "CopiedMultiGoToRAnnotation.pdf";
        try (PdfDocument out = new PdfDocument(new PdfWriter(outFile))) {
            try (PdfDocument input = new PdfDocument(new PdfReader(SOURCE_FOLDER + "MultiDestinations.pdf"))) {
                input.copyPagesTo(1, input.getNumberOfPages(), out);
            }
        }
        List<PdfAnnotation> annotations = getAnnotationsFromPdf(outFile, 1);
        Assert.assertEquals("Not all destinations are copied", 2, annotations.size());
    }

    @Test
    public void copyGoToRWithoutTargetTest() throws IOException {
        String outFile = DESTINATION_FOLDER + "CopiedGoToRNoTarget.pdf";
        try (PdfDocument out = new PdfDocument(new PdfWriter(outFile))) {
            try (PdfDocument input = new PdfDocument(new PdfReader(SOURCE_FOLDER + "namedDest.pdf"))) {
                input.copyPagesTo(2, 6, out);
            }
        }
        List<PdfAnnotation> annotations = getAnnotationsFromPdf(outFile, 5);
        Assert.assertTrue("Destinations are copied but should not", annotations.isEmpty());
    }

    @Test
    public void copyGoToRNamedDestinationTest() throws IOException {
        String outFile = DESTINATION_FOLDER + "CopiedGoToRNamedDest.pdf";
        try (PdfDocument out = new PdfDocument(new PdfWriter(outFile))) {
            try (PdfDocument input = new PdfDocument(new PdfReader(SOURCE_FOLDER + "namedDest.pdf"))) {
                input.copyPagesTo(1, 6, out);
            }
        }
        List<PdfAnnotation> annotations = getAnnotationsFromPdf(outFile, 6);
        Assert.assertFalse("Annotation is copied", annotations.isEmpty());
        String destination = (annotations.get(0)).getPdfObject().get(PdfName.Dest).toString();
        Assert.assertEquals("Destination is different from expected", "Destination_1", destination);
    }

    @Test
    public void fileAttachmentTargetTest() throws IOException {
        String outFile = DESTINATION_FOLDER + "CopiedFileAttachmentTarget.pdf";
        try (PdfDocument out = new PdfDocument(new PdfWriter(outFile))) {
            try (PdfDocument input = new PdfDocument(new PdfReader(SOURCE_FOLDER + "fileAttachmentTargetTest.pdf"))) {
                input.copyPagesTo(1, input.getNumberOfPages(), out);
            }
        }
        List<PdfAnnotation> annotations = getAnnotationsFromPdf(outFile, 2);
        Assert.assertFalse("Annotation is not copied", annotations.isEmpty());
        String nm = annotations.get(0).getPdfObject().getAsString(PdfName.NM).toString();
        Assert.assertEquals("File attachment name is different from expected", "FileAttachmentAnnotation1", nm);
    }

    @Test
    public void copyLinkWidgetTest() throws IOException {
        String outFile = DESTINATION_FOLDER + "CopiedLinkWidget.pdf";
        try (PdfDocument out = new PdfDocument(new PdfWriter(outFile))) {
            try (PdfDocument input = new PdfDocument(
                    new PdfReader(SOURCE_FOLDER + "LinkWidgetExplicitDestination.pdf"))) {
                input.copyPagesTo(1, input.getNumberOfPages(), out);
            }
        }
        List<PdfAnnotation> annotations = getAnnotationsFromPdf(outFile, 1);
        Assert.assertFalse("Annotation is not copied", annotations.isEmpty());
        Assert.assertEquals("Annotation is of a different subtype", PdfName.Widget, annotations.get(0).getSubtype());
    }

    @Test
    public void noPdfNameATest() throws IOException {
        String outFile = DESTINATION_FOLDER + "CopiedNoPdfNameA.pdf";
        try (PdfDocument out = new PdfDocument(new PdfWriter(outFile))) {
            try (PdfDocument input = new PdfDocument(new PdfReader(SOURCE_FOLDER + "GoToRAnnotation.pdf"))) {
                PdfAnnotation pdfAnnotation = input.getPage(1).getAnnotations().get(0);
                pdfAnnotation.getPdfObject().remove(PdfName.A);
                input.copyPagesTo(1, input.getNumberOfPages(), out);
            }
        }
        List<PdfAnnotation> annotations = getAnnotationsFromPdf(outFile, 1);
        Assert.assertFalse("Annotation is not copied", annotations.isEmpty());
    }

    @Test
    public void noPdfNameDTest() throws IOException {
        String outFile = DESTINATION_FOLDER + "CopiedNoPdfNameD.pdf";
        try (PdfDocument out = new PdfDocument(new PdfWriter(outFile))) {
            try (PdfDocument input = new PdfDocument(new PdfReader(SOURCE_FOLDER + "GoToRAnnotation.pdf"))) {
                PdfAnnotation pdfAnnotation = input.getPage(1).getAnnotations().get(0);
                pdfAnnotation.getPdfObject().getAsDictionary(PdfName.A).remove(PdfName.D);

                input.copyPagesTo(1, input.getNumberOfPages(), out);
            }
        }
        List<PdfAnnotation> annotations = getAnnotationsFromPdf(outFile, 1);
        Assert.assertFalse("Annotation is not copied", annotations.isEmpty());
    }

    @Test
    public void noPdfNameSTest() throws IOException {
        String outFile = DESTINATION_FOLDER + "CopiedNoPdfNameS.pdf";
        try (PdfDocument out = new PdfDocument(new PdfWriter(outFile))) {
            try (PdfDocument input = new PdfDocument(new PdfReader(SOURCE_FOLDER + "GoToRAnnotation.pdf"))) {
                PdfAnnotation pdfAnnotation = input.getPage(1).getAnnotations().get(0);
                pdfAnnotation.getPdfObject().getAsDictionary(PdfName.A).remove(PdfName.S);
                input.copyPagesTo(1, input.getNumberOfPages(), out);
            }
        }
        List<PdfAnnotation> annotations = getAnnotationsFromPdf(outFile, 1);
        Assert.assertTrue("Annotation is copied", annotations.isEmpty());
    }

    @Test
    public void noPdfNameDWithGoToRTest() throws IOException {
        String outFile = DESTINATION_FOLDER + "CopiedNoPdfNameDGoToR.pdf";
        try (PdfDocument out = new PdfDocument(new PdfWriter(outFile))) {
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
        Assert.assertFalse("Annotation is not copied", annotations.isEmpty());
    }

    @Test
    public void linkInsideArray() throws IOException {
        String outFile = DESTINATION_FOLDER + "CopiedLinkInArray.pdf";
        try (PdfDocument out = new PdfDocument(new PdfWriter(outFile))) {
            try (PdfDocument input = new PdfDocument(new PdfReader(SOURCE_FOLDER + "LinkInArray.pdf"))) {
                input.copyPagesTo(1, 1, out);
            }
        }
        List<PdfAnnotation> annotations = getAnnotationsFromPdf(outFile, 1);
        Assert.assertTrue("Annotation is copied", annotations.isEmpty());
    }

    private List<PdfAnnotation> getAnnotationsFromPdf(String outFilePath, int pageNumber) throws IOException {
        List<PdfAnnotation> annotations;
        try (PdfDocument result = new PdfDocument(new PdfReader(outFilePath))) {
            annotations = result.getPage(pageNumber).getAnnotations();
        }
        return annotations;
    }
}
