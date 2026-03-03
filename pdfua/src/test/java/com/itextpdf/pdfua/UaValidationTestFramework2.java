package com.itextpdf.pdfua;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.exceptions.Pdf20ConformanceException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WellTaggedPdfConformance;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.validation.ValidationContainer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.wtpdf.WellTaggedPdfConfig;
import com.itextpdf.pdfua.wtpdf.WellTaggedPdfDocument;
import com.itextpdf.test.pdfa.VeraPdfValidator;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class UaValidationTestFramework2 {
    private final boolean defaultCheckDocClosingByReopening;
    private final String destinationFolder;
    private PdfUAConformance uaConformance = null;
    private WellTaggedPdfConformance wtpdfConformance = null;
    private final List<UaValidationTestFramework2.Generator<IBlockElement>> elementProducers = new ArrayList<>();

    private final List<Consumer<PdfDocument>> beforeGeneratorHook = new ArrayList<>();
    private final List<Consumer<PdfDocument>> afterGeneratorHook = new ArrayList<>();

    public UaValidationTestFramework2(String destinationFolder, Object conformance) {
        this(destinationFolder, true, conformance);
    }

    public UaValidationTestFramework2(String destinationFolder, boolean defaultCheckDocClosingByReopening, Object conformance) {
        this.destinationFolder = destinationFolder;
        this.defaultCheckDocClosingByReopening = defaultCheckDocClosingByReopening;
        parseConformance(conformance);
    }

    private void parseConformance(Object conformance) {
        if (conformance instanceof PdfUAConformance) {
            if (conformance == PdfUAConformance.PDF_UA_1) {
                uaConformance = PdfUAConformance.PDF_UA_1;
            } else if (conformance == PdfUAConformance.PDF_UA_2) {
                uaConformance = PdfUAConformance.PDF_UA_2;
            }
        } else if (conformance instanceof WellTaggedPdfConformance) {
            if (conformance == WellTaggedPdfConformance.FOR_ACCESSIBILITY) {
                wtpdfConformance = WellTaggedPdfConformance.FOR_ACCESSIBILITY;
            } else if (conformance == WellTaggedPdfConformance.FOR_REUSE) {
                wtpdfConformance = WellTaggedPdfConformance.FOR_REUSE;
            }
        }
    }

    public void addSuppliers(UaValidationTestFramework2.Generator<IBlockElement>... suppliers) {
        Collections.addAll(elementProducers, suppliers);
    }

    public void assertBothFail(String filename) throws IOException {
        assertBothFail(filename, null);
    }

    public void assertBothFail(String filename, boolean checkDocClosing)
            throws IOException {
        assertBothFail(filename, null, checkDocClosing);
    }

    public void assertBothFail(String filename, String expectedMsg) throws IOException {
        assertBothFail(filename, expectedMsg, defaultCheckDocClosingByReopening);
    }

    public void assertBothFail(String filename, String expectedMsg, boolean checkDocClosing)
            throws IOException {
        checkError(checkErrorLayout("itext_" + filename + conformanceToString() + ".pdf"), expectedMsg);

        final String createdFileName = "vera_" + filename + conformanceToString() + ".pdf";
        veraPdfResult(createdFileName, true);

        if (checkDocClosing) {
            System.out.println("Checking closing");
            checkError(checkErrorOnClosing(createdFileName), expectedMsg);
        }
    }

    public void assertBothValid(String fileName) throws IOException {
        Exception e = checkErrorLayout("itext_" + fileName + conformanceToString() + ".pdf");
        String veraPdf = veraPdfResult("vera_" + fileName + conformanceToString() + ".pdf", false);
        Exception eClosing = checkErrorOnClosing("vera_" + fileName + conformanceToString() + ".pdf");
        if (e == null && veraPdf == null && eClosing == null) {
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
        if (eClosing != null){
            counter++;
            sb.append("OnClosing no exception expected but was:\n").append(eClosing);
        }
        if (counter != 3) {
            Assertions.fail("One of the checks threw an exception\n\n" + sb.toString());
        }
        Assertions.fail(sb.toString());
    }

    public void addBeforeGenerationHook(Consumer<PdfDocument> action) {
        this.beforeGeneratorHook.add(action);
    }

    public void addAfterGenerationHook(Consumer<PdfDocument> action) {
        this.afterGeneratorHook.add(action);
    }

    public void assertOnlyVeraPdfFail(String filename) throws IOException {
        veraPdfResult("vera_" + filename + conformanceToString() + ".pdf", true);
        Exception e = checkErrorLayout("itext_" + filename + conformanceToString() + ".pdf");
        Assertions.assertNull(e);
    }

    public void assertVeraPdfValid(String filename) throws IOException {
        String veraPdf = veraPdfResult("vera_" + filename + conformanceToString() + ".pdf", false);
        if (veraPdf == null) {
            return;
        }
        Assertions.fail("Expected no vera pdf message but was: \n" + veraPdf + "\n");
    }

    public void assertOnlyITextFail(String filename, String expectedMsg) throws IOException {
        checkError(checkErrorLayout("itext_" + filename + conformanceToString() + ".pdf"), expectedMsg);
        assertVeraPdfValid(filename);
    }

    // Android-Conversion-Skip-Block-Start (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    protected VeraPdfValidator getVerapdfValidator() {
        if (uaConformance != null) {
            return new VeraPdfValidator();
        } else if (wtpdfConformance == WellTaggedPdfConformance.FOR_ACCESSIBILITY) {
            return new VeraPdfValidator("WTPDF_ACCESSIBILITY");
        } else if (wtpdfConformance == WellTaggedPdfConformance.FOR_REUSE) {
            return new VeraPdfValidator("WTPDF_REUSE");
        }
        return null;
    }
    // Android-Conversion-Skip-Block-End

    protected PdfDocument createPdfDocument(String filename) throws IOException {
        if (uaConformance == PdfUAConformance.PDF_UA_1) {
            return new PdfUATestPdfDocument(new PdfWriter(filename));
        } else if (uaConformance == PdfUAConformance.PDF_UA_2) {
            return new PdfUA2TestPdfDocument(new PdfWriter(filename, new WriterProperties().setPdfVersion(
                    PdfVersion.PDF_2_0)));
        } else if (wtpdfConformance == WellTaggedPdfConformance.FOR_ACCESSIBILITY) {
            return new WellTaggedPdfDocument(new PdfWriter(filename,
                    new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)),
                    new WellTaggedPdfConfig(
                            WellTaggedPdfConformance.FOR_ACCESSIBILITY, "English pangram", "en-US"));
        } else if (wtpdfConformance == WellTaggedPdfConformance.FOR_REUSE) {
            return new WellTaggedPdfDocument(new PdfWriter(filename,
                    new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)),
                    new WellTaggedPdfConfig(
                            WellTaggedPdfConformance.FOR_REUSE, "English pangram", "en-US"));
        } else {
            throw new IllegalArgumentException("PdfConformance not specified");
        }
    }

    protected PdfDocument createPdfDocument(String inputFile, String outputFile)
            throws IOException {
        if (uaConformance == PdfUAConformance.PDF_UA_1) {
            return new PdfUATestPdfDocument(new PdfReader(inputFile), new PdfWriter(outputFile));
        } else if (uaConformance == PdfUAConformance.PDF_UA_2) {
            return new PdfUA2TestPdfDocument(new PdfReader(inputFile),
                    new PdfWriter(outputFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        } else if (wtpdfConformance == WellTaggedPdfConformance.FOR_ACCESSIBILITY) {
            return new WellTaggedPdfDocument(new PdfReader(inputFile),
                    new PdfWriter(outputFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)),
                    new WellTaggedPdfConfig(
                            WellTaggedPdfConformance.FOR_ACCESSIBILITY, "English pangram", "en-US"));
        } else if (wtpdfConformance == WellTaggedPdfConformance.FOR_REUSE) {
            return new WellTaggedPdfDocument(new PdfReader(inputFile),
                    new PdfWriter(outputFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)),
                    new WellTaggedPdfConfig(
                            WellTaggedPdfConformance.FOR_REUSE, "English pangram", "en-US"));
        } else {
            throw new IllegalArgumentException("PdfConformance not specified");
        }
    }

    private String veraPdfResult(String filename, boolean failureExpected)
            throws IOException {
        String outfile = UrlUtil.getNormalizedFileUriString(destinationFolder + filename);
        System.out.println(outfile);
        PdfDocument pdfDoc = createPdfDocument(destinationFolder + filename);
        pdfDoc.getDiContainer().register(ValidationContainer.class, new ValidationContainer());

        for (Consumer<PdfDocument> pdfDocumentConsumer : this.beforeGeneratorHook) {
            pdfDocumentConsumer.accept(pdfDoc);
        }
        try (Document document = new Document(pdfDoc)) {
            for (UaValidationTestFramework2.Generator<IBlockElement> blockElementSupplier : elementProducers) {
                document.add(blockElementSupplier.generate());
            }
            for (Consumer<PdfDocument> pdfDocumentConsumer : this.afterGeneratorHook) {
                pdfDocumentConsumer.accept(pdfDoc);
            }
        }
        VeraPdfValidator validator = getVerapdfValidator();// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
        String validate = null;
        if (failureExpected) {
            validator.validateFailure(destinationFolder + filename); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
        } else {
            validate = validator.validate(destinationFolder + filename); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
        }
        return validate;
    }

    private void checkError(Exception e, String expectedMsg) {
        Assertions.assertNotNull(e);
        if (!(e instanceof PdfUAConformanceException) && !(e instanceof Pdf20ConformanceException)) {
            System.out.println(printStackTrace(e));
            Assertions.fail();
        }
        if (expectedMsg != null) {
            Assertions.assertEquals(expectedMsg, e.getMessage());
        }
        System.out.println(printStackTrace(e));
    }

    private Exception checkErrorLayout(String filename) {
        try {
            final String outPath = destinationFolder + filename;
            System.out.println(UrlUtil.getNormalizedFileUriString(outPath));
            PdfDocument pdfDoc = createPdfDocument(outPath);
            for (Consumer<PdfDocument> pdfDocumentConsumer : this.beforeGeneratorHook) {
                pdfDocumentConsumer.accept(pdfDoc);
            }
            try (Document document = new Document(pdfDoc)) {
                for (UaValidationTestFramework2.Generator<IBlockElement> blockElementSupplier : elementProducers) {
                    document.add(blockElementSupplier.generate());
                }
                for (Consumer<PdfDocument> pdfDocumentConsumer : this.afterGeneratorHook) {
                    pdfDocumentConsumer.accept(pdfDoc);
                }
            }
        } catch (Exception e) {
            return e;
        }
        return null;
    }

    private Exception checkErrorOnClosing(String filename) {
        try {
            final String outPath = destinationFolder + "reopen_" + filename;
            final String inPath = destinationFolder + filename;
            System.out.println(UrlUtil.getNormalizedFileUriString(outPath));
            PdfDocument pdfDoc = createPdfDocument(inPath, outPath);

            pdfDoc.close();
        } catch (Exception e) {
            return e;
        }
        return null;
    }

    private static String printStackTrace(Exception e) {
        return e.toString();
    }
    
    private String conformanceToString() {
        if (uaConformance != null) {
            return MessageFormatUtil.format("_UA_{0}", uaConformance.getPart());
        } else if (wtpdfConformance == WellTaggedPdfConformance.FOR_ACCESSIBILITY) {
            return "WTPDF_FOR_ACCESSIBILITY";
        } else if (wtpdfConformance == WellTaggedPdfConformance.FOR_REUSE) {
            return "WTPDF_FOR_REUSE";
        }
        return null;
    }

    public static List<Object> getConformanceList() {
        return Arrays.asList(PdfUAConformance.PDF_UA_1, PdfUAConformance.PDF_UA_2, WellTaggedPdfConformance.FOR_REUSE);
    }

    public interface Generator<IBlockElement> {
        IBlockElement generate();
    }
}
