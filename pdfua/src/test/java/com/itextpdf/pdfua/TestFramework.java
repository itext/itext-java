package com.itextpdf.pdfua;

import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.ValidationContainer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.test.pdfa.VeraPdfValidator;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;

/**
 * Class that helps to test PDF/UA conformance.
 * It creates two pdf documents, one with our checkers disabled to collect the veraPDf result,
 * one with our checkers enabled to check for exceptions.
 * It then compares if our checkers and veraPDF produce the same result.
 */
public class TestFramework {

    private final String destinationFolder;
    private final List<Generator<IBlockElement>> elementProducers = new ArrayList<>();

    public TestFramework(String destinationFolder) {
        this.destinationFolder = destinationFolder;
    }

    public void addSuppliers(Generator<IBlockElement>... suppliers) {
        Collections.addAll(elementProducers, suppliers);
    }

    public void assertBothFail(String filename) throws FileNotFoundException {
        Exception e = checkErrorLayout("layout_" + filename + ".pdf");
        String veraPdf = verAPdfResult("vera_" + filename + ".pdf");
        System.out.println(veraPdf);
        if (!(e instanceof PdfUAConformanceException) && e != null) {
            System.out.println(printStackTrace(e));
            Assert.fail();
        }
        Assert.assertNotNull(e);
        System.out.println(printStackTrace(e));
        Assert.assertNotNull(veraPdf);
    }

    public void assertBothValid(String fileName) throws FileNotFoundException {
        Exception e = checkErrorLayout("layout_" + fileName + ".pdf");
        String veraPdf = verAPdfResult("vera_" + fileName + ".pdf");
        if (e == null && veraPdf == null) {
            return;
        }
        int counter = 0;
        StringBuilder sb = new StringBuilder();
        if (e != null) {
            counter++;
            sb.append("No exception expected but was: ")
                    .append(e.getClass().getName()).append(" \n")
                    .append("Message: \n")
                    .append(e.getMessage()).append('\n')
                    .append("StackTrace:\n").append(printStackTrace(e))
                    .append('\n');
        }
        if (veraPdf != null) {
            counter++;
            sb.append("Expected no vera pdf message but was: \n").append(veraPdf).append("\n");
        }
        if (counter != 2) {
            Assert.fail("One of the checks did not throw\n\n" + sb.toString());
        }
        Assert.fail(sb.toString());
    }

    public String verAPdfResult(String filename) throws FileNotFoundException {
        String outfile = UrlUtil.getNormalizedFileUriString(destinationFolder + filename);
        System.out.println(outfile);
        PdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(destinationFolder + filename, PdfUATestPdfDocument.createWriterProperties()));

        Document document = new Document(pdfDoc);
        document.getPdfDocument().getDiContainer().register(ValidationContainer.class, new ValidationContainer());
        for (Generator<IBlockElement> blockElementSupplier : elementProducers) {
            document.add(blockElementSupplier.generate());
        }
        document.close();
        VeraPdfValidator validator = new VeraPdfValidator();// Android-Conversion-Skip-Line (TODO DEVSIX-7377
        // introduce pdf/ua validation on Android)
        return validator.validate(destinationFolder
                + filename);// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    public Exception checkErrorLayout(String filename) {
        try {
            String outfile = UrlUtil.getNormalizedFileUriString(destinationFolder + filename);
            System.out.println(outfile);
            PdfDocument pdfDoc = new PdfUATestPdfDocument(
                    new PdfWriter(destinationFolder + filename, PdfUATestPdfDocument.createWriterProperties()));

            Document document = new Document(pdfDoc);
            for (Generator<IBlockElement> blockElementSupplier : elementProducers) {
                document.add(blockElementSupplier.generate());
            }
            document.close();
        } catch (Exception e) {
            return e;
        }
        return null;
    }

    private static String printStackTrace(Exception e) {
        return e.toString();
    }

    public static interface Generator<IBlockElement> {
        IBlockElement generate();
    }
}
