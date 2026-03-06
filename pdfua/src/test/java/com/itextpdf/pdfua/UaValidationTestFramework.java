package com.itextpdf.pdfua;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.exceptions.Pdf20ConformanceException;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WellTaggedPdfConformance;
import com.itextpdf.kernel.validation.ValidationContainer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.wtpdf.WellTaggedPdfConfig;
import com.itextpdf.pdfua.wtpdf.WellTaggedPdfDocument;
import com.itextpdf.test.pdfa.VeraPdfValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.jupiter.api.Assertions;

public class UaValidationTestFramework {
    private final boolean defaultCheckDocClosingByReopening;
    private final String destinationFolder;
    private final List<Function<PdfDocument, IBlockElement>> elementProducers = new ArrayList<>();
    private final List<Consumer<PdfDocument>> beforeGeneratorHook = new ArrayList<>();
    private final List<Consumer<PdfDocument>> afterGeneratorHook = new ArrayList<>();
    private final PdfConformance conformance;

    public UaValidationTestFramework(String destinationFolder, PdfConformance conformance) {
        this(destinationFolder, true, conformance);
    }

    public UaValidationTestFramework(String destinationFolder, boolean defaultCheckDocClosingByReopening,
            PdfConformance conformance) {
        this.destinationFolder = destinationFolder;
        this.defaultCheckDocClosingByReopening = defaultCheckDocClosingByReopening;
        this.conformance = conformance;
    }

    public static List<PdfConformance> getConformanceList(boolean includeBelowPdf2Specification) {
        final List<PdfConformance> conformances = new ArrayList<>();

        if (includeBelowPdf2Specification) {
            conformances.add(new PdfConformance(PdfUAConformance.PDF_UA_1));
        }
        conformances.add(new PdfConformance(PdfUAConformance.PDF_UA_2));
        conformances.add(new PdfConformance(WellTaggedPdfConformance.FOR_REUSE));

        conformances.add(new PdfConformance(WellTaggedPdfConformance.FOR_ACCESSIBILITY));
        return conformances;
    }


    public static List<PdfConformance> getConformanceList() {
        return getConformanceList(true);
    }


    @SafeVarargs
    public final void addSuppliers(Function<PdfDocument, IBlockElement>... suppliers) {
        Collections.addAll(elementProducers, suppliers);
    }

    public void assertBothFail(String filename) throws IOException {
        assertBothFail(filename, null);
    }

    public void assertBothFail(String filename, boolean checkDocClosing) throws IOException {
        assertBothFail(filename, null, checkDocClosing);
    }

    public void assertBothFail(String filename, String expectedMsg) throws IOException {
        assertBothFail(filename, expectedMsg, defaultCheckDocClosingByReopening);
    }

    public void assertBothFail(String filename, String expectedMsg, boolean checkDocClosing) throws IOException {
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
            sb.append("No exception expected but was: ").append(e.getClass().getName()).append(" \nMessage: \n")
                    .append(e.getMessage()).append('\n').append("StackTrace:\n")
                    .append(printStackTrace(e)).append('\n');
        }
        if (veraPdf != null) {
            counter++;
            sb.append("Expected no vera pdf message but was: \n").append(veraPdf).append('\n');
        }
        if (eClosing != null) {
            counter++;
            sb.append("OnClosing no exception expected but was:\nStackTrace:\n")
                    .append(printStackTrace(eClosing)).append(eClosing);
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

    public boolean isPdf2Based(PdfConformance conformance) {
        if (conformance.isWtpdf()) {
            return true;
        }
        if (conformance.isPdfUA() && conformance.getUAConformance() == PdfUAConformance.PDF_UA_2) {
            return true;
        }
        return false;
    }

    public PdfDocument createPdfDocument(String inputFileName, String outputFileName, String title, String language)
            throws IOException {
        PdfWriter writer = new PdfWriter(outputFileName);
        writer.getProperties().setPdfVersion(isPdf2Based(conformance) ? PdfVersion.PDF_2_0 : PdfVersion.PDF_1_7);
        PdfReader reader = inputFileName == null ? null : new PdfReader(inputFileName);
        if (reader != null) {
            if (conformance.isPdfUA()) {
                return new PdfUADocument(reader, writer,
                        new PdfUAConfig(conformance.getUAConformance(), title, language));
            } else if (conformance.isWtpdf()) {
                return new WellTaggedPdfDocument(reader, writer,
                        new WellTaggedPdfConfig(conformance.getWtpdfConformances(), title, language));
            } else {
                throw new IllegalArgumentException("PdfConformance not specified");
            }
        } else {
            if (conformance.isPdfUA()) {
                return new PdfUADocument(writer, new PdfUAConfig(conformance.getUAConformance(), title, language));
            } else if (conformance.isWtpdf()) {
                return new WellTaggedPdfDocument(writer,
                        new WellTaggedPdfConfig(conformance.getWtpdfConformances(), title, language));
            } else {
                throw new IllegalArgumentException("PdfConformance not specified");
            }
        }

    }


    public PdfDocument createPdfDocument(String inputFile, String outputFile) throws IOException {
        return createPdfDocument(inputFile, outputFile, "English pangram", "en-US");
    }

    public PdfDocument createPdfDocument(String outputFile) throws IOException {
        return createPdfDocument(null, outputFile, "English pangram", "en-US");
    }

    private String veraPdfResult(String filename, boolean failureExpected) throws IOException {
        String outfile = UrlUtil.getNormalizedFileUriString(destinationFolder + filename);
        System.out.println(outfile);
        PdfDocument pdfDoc = createPdfDocument(destinationFolder + filename);
        pdfDoc.getDiContainer().register(ValidationContainer.class, new ValidationContainer());

        for (Consumer<PdfDocument> pdfDocumentConsumer : this.beforeGeneratorHook) {
            pdfDocumentConsumer.accept(pdfDoc);
        }
        try (Document document = new Document(pdfDoc)) {
            for (Function<PdfDocument, IBlockElement> blockElementSupplier : elementProducers) {
                document.add(blockElementSupplier.apply(pdfDoc));
            }
            for (Consumer<PdfDocument> pdfDocumentConsumer : this.afterGeneratorHook) {
                pdfDocumentConsumer.accept(pdfDoc);
            }
        }
        VeraPdfValidator validator = new VeraPdfValidator();
        String validate = null;
        if (failureExpected) {
            validator.validateFailure(destinationFolder + filename);
        } else {
            validate = validator.validate(destinationFolder + filename);
        }
        return validate;
    }

    private void checkError(Exception e, String expectedMsg) {
        if (e == null) {
            Assertions.fail("Expected exception but no exception was thrown");
        }
        if (!(e instanceof PdfUAConformanceException) && !(e instanceof Pdf20ConformanceException)) {
            System.out.println(printStackTrace(e));
            Assertions.fail(
                    "Expected exception of type PdfUAConformanceException or Pdf20ConformanceException but was: "
                            + e.getClass().getName());
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
                for (Function<PdfDocument, IBlockElement> blockElementSupplier : elementProducers) {
                    document.add(blockElementSupplier.apply(pdfDoc));
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

    private String conformanceToString() {
        if (conformance.getUAConformance() != null) {
            return MessageFormatUtil.format("_UA_{0}", conformance.getUAConformance().getPart());
        } else if (conformance.conformsTo(WellTaggedPdfConformance.FOR_ACCESSIBILITY)) {
            return "WTPDF_FOR_ACCESSIBILITY";
        } else if (conformance.conformsTo(WellTaggedPdfConformance.FOR_REUSE)) {
            return "WTPDF_FOR_REUSE";
        }
        return null;
    }

    private static String printStackTrace(Exception e) {
        return e.toString();
    }

}
