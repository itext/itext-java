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
package com.itextpdf.pdfua;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.exceptions.Pdf20ConformanceException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.validation.ValidationContainer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;

/**
 * Class that helps to test PDF/UA conformance.
 * It creates two pdf documents, one with our checkers disabled to collect the veraPDf result,
 * one with our checkers enabled to check for exceptions.
 * It then compares if our checkers and veraPDF produce the same result.
 */
public class UaValidationTestFramework {

    private final boolean defaultCheckDocClosingByReopening;
    private final String destinationFolder;
    private final List<Generator<IBlockElement>> elementProducers = new ArrayList<>();

    private final List<Consumer<PdfDocument>> beforeGeneratorHook = new ArrayList<>();
    private final List<Consumer<PdfDocument>> afterGeneratorHook = new ArrayList<>();
    public UaValidationTestFramework(String destinationFolder) {
        this(destinationFolder, true);
    }

    public UaValidationTestFramework(String destinationFolder, boolean defaultCheckDocClosingByReopening) {
        this.destinationFolder = destinationFolder;
        this.defaultCheckDocClosingByReopening = defaultCheckDocClosingByReopening;
    }

    public void addSuppliers(Generator<IBlockElement>... suppliers) {
        Collections.addAll(elementProducers, suppliers);
    }

    public void assertBothFail(String filename, PdfUAConformance pdfUAConformance) throws IOException {
        assertBothFail(filename, null, pdfUAConformance);
    }

    public void assertBothFail(String filename, boolean checkDocClosing, PdfUAConformance pdfUAConformance)
            throws IOException {
        assertBothFail(filename, null, checkDocClosing, pdfUAConformance);
    }

    public void assertBothFail(String filename, String expectedMsg, PdfUAConformance pdfUAConformance) throws IOException {
        assertBothFail(filename, expectedMsg, defaultCheckDocClosingByReopening, pdfUAConformance);
    }

    public void assertBothFail(String filename, String expectedMsg, boolean checkDocClosing, PdfUAConformance pdfUAConformance)
            throws IOException {
        checkError(checkErrorLayout("itext_" + filename + getUAConformance(pdfUAConformance) + ".pdf", pdfUAConformance), expectedMsg);

        final String createdFileName = "vera_" + filename + getUAConformance(pdfUAConformance) + ".pdf";
        veraPdfResult(createdFileName, true, pdfUAConformance);

        if (checkDocClosing) {
            System.out.println("Checking closing");
            checkError(checkErrorOnClosing(createdFileName, pdfUAConformance), expectedMsg);
        }
    }

    public void assertITextValid(String fileName, PdfUAConformance pdfUAConformance) {
        Exception e = checkErrorLayout("itext_" + fileName + getUAConformance(pdfUAConformance) + ".pdf",
                pdfUAConformance);
        if (e == null) {
            return;
        }
        String sb = "No exception expected but was: "
                + e.getClass().getName() + " \n"
                + "Message: \n"
                + e.getMessage() + '\n'
                + "StackTrace:\n" + printStackTrace(e)
                + '\n';
        Assertions.fail(sb);
    }

    public void assertBothValid(String fileName, PdfUAConformance pdfUAConformance) throws IOException {
        Exception e = checkErrorLayout("itext_" + fileName + getUAConformance(pdfUAConformance) + ".pdf", pdfUAConformance);
        String veraPdf = veraPdfResult("vera_" + fileName + getUAConformance(pdfUAConformance) + ".pdf", false, pdfUAConformance);
        Exception eClosing = checkErrorOnClosing("vera_" + fileName + getUAConformance(pdfUAConformance) + ".pdf", pdfUAConformance);
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

    public void assertVeraPdfFail(String filename, PdfUAConformance pdfUAConformance) throws IOException {
        veraPdfResult("vera_" + filename + getUAConformance(pdfUAConformance) + ".pdf", true, pdfUAConformance);
    }

    public void assertOnlyVeraPdfFail(String filename, PdfUAConformance pdfUAConformance) throws IOException {
        veraPdfResult("vera_" + filename + getUAConformance(pdfUAConformance) + ".pdf", true, pdfUAConformance);
        Exception e = checkErrorLayout("itext_" + filename + getUAConformance(pdfUAConformance) + ".pdf", pdfUAConformance);
        Assertions.assertNull(e);
    }

    public void assertVeraPdfValid(String filename, PdfUAConformance pdfUAConformance) throws IOException {
        String veraPdf = veraPdfResult("vera_" + filename + getUAConformance(pdfUAConformance) + ".pdf", false, pdfUAConformance);
        if (veraPdf == null) {
            return;
        }
        Assertions.fail("Expected no vera pdf message but was: \n" + veraPdf + "\n");
    }

    public void assertOnlyITextFail(String filename, String expectedMsg, PdfUAConformance pdfUAConformance) throws IOException {
        checkError(checkErrorLayout("itext_" + filename + getUAConformance(pdfUAConformance) + ".pdf", pdfUAConformance), expectedMsg);
        assertVeraPdfValid(filename, pdfUAConformance);
    }

    private String veraPdfResult(String filename, boolean failureExpected, PdfUAConformance pdfUAConformance)
            throws IOException {
        String outfile = UrlUtil.getNormalizedFileUriString(destinationFolder + filename);
        System.out.println(outfile);
        PdfDocument pdfDoc = createPdfDocument(destinationFolder + filename, pdfUAConformance);
        pdfDoc.getDiContainer().register(ValidationContainer.class, new ValidationContainer());

        for (Consumer<PdfDocument> pdfDocumentConsumer : this.beforeGeneratorHook) {
            pdfDocumentConsumer.accept(pdfDoc);
        }
        try (Document document = new Document(pdfDoc)) {
            for (Generator<IBlockElement> blockElementSupplier : elementProducers) {
                document.add(blockElementSupplier.generate());
            }
            for (Consumer<PdfDocument> pdfDocumentConsumer : this.afterGeneratorHook) {
                pdfDocumentConsumer.accept(pdfDoc);
            }
        }
        String validate = null;
        if (failureExpected) {
        } else {
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

    private Exception checkErrorLayout(String filename, PdfUAConformance pdfUAConformance) {
        try {
            final String outPath = destinationFolder + filename;
            System.out.println(UrlUtil.getNormalizedFileUriString(outPath));
            PdfDocument pdfDoc = createPdfDocument(outPath, pdfUAConformance);
            for (Consumer<PdfDocument> pdfDocumentConsumer : this.beforeGeneratorHook) {
                pdfDocumentConsumer.accept(pdfDoc);
            }
            try (Document document = new Document(pdfDoc)) {
                for (Generator<IBlockElement> blockElementSupplier : elementProducers) {
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

    private Exception checkErrorOnClosing(String filename, PdfUAConformance pdfUAConformance) {
        try {
            final String outPath = destinationFolder + "reopen_" + filename;
            final String inPath = destinationFolder + filename;
            System.out.println(UrlUtil.getNormalizedFileUriString(outPath));
            PdfDocument pdfDoc = createPdfDocument(inPath, outPath, pdfUAConformance);

            pdfDoc.close();
        } catch (Exception e) {
            return e;
        }
        return null;
    }

    private static String printStackTrace(Exception e) {
        return e.toString();
    }

    private static PdfDocument createPdfDocument(String filename, PdfUAConformance pdfUAConformance) throws IOException {
        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            return new PdfUATestPdfDocument(new PdfWriter(filename));
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            return new PdfUA2TestPdfDocument(new PdfWriter(filename, new WriterProperties().setPdfVersion(
                    PdfVersion.PDF_2_0)));
        } else {
            throw new IllegalArgumentException("Unsupported PdfUAConformance: " + pdfUAConformance);
        }
    }

    private static PdfDocument createPdfDocument(String inputFile, String outputFile, PdfUAConformance pdfUAConformance)
            throws IOException {
        if (pdfUAConformance == PdfUAConformance.PDF_UA_1) {
            return new PdfUATestPdfDocument(new PdfReader(inputFile), new PdfWriter(outputFile));
        } else if (pdfUAConformance == PdfUAConformance.PDF_UA_2) {
            return new PdfUA2TestPdfDocument(new PdfReader(inputFile),
                    new PdfWriter(outputFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        } else {
            throw new IllegalArgumentException("Unsupported PdfUAConformance: " + pdfUAConformance);
        }
    }

    public static interface Generator<IBlockElement> {
        IBlockElement generate();
    }

    private static String getUAConformance(PdfUAConformance conformance) {
        return MessageFormatUtil.format("_UA_{0}", conformance.getPart());
    }

    public static List<PdfUAConformance> getConformanceList() {
        return Arrays.asList(PdfUAConformance.PDF_UA_1, PdfUAConformance.PDF_UA_2);
    }
}
