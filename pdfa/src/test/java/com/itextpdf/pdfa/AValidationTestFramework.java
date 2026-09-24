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
package com.itextpdf.pdfa;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.exceptions.Pdf20ConformanceException;
import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.validation.ValidationContainer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.test.pdfa.VeraPdfValidator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.jupiter.api.Assertions;

public class AValidationTestFramework {
    private static final String ICC_PROFILE =
            "./src/test/resources/com/itextpdf/pdfa/sRGB Color Space Profile.icm";

    private final boolean defaultCheckDocClosingByReopening;
    private final String destinationFolder;
    private final PdfAConformance conformance;
    private final List<Function<PdfDocument, IBlockElement>> elementProducers = new ArrayList<>();
    private final List<Consumer<PdfDocument>> beforeGeneratorHook = new ArrayList<>();
    private final List<Consumer<PdfDocument>> afterGeneratorHook = new ArrayList<>();

    public AValidationTestFramework(String destinationFolder, boolean defaultCheckDocClosingByReopening,
            PdfAConformance conformance) {
        if (conformance == null) {
            throw new IllegalArgumentException("PDF/A conformance not specified");
        }
        this.destinationFolder = destinationFolder;
        this.defaultCheckDocClosingByReopening = defaultCheckDocClosingByReopening;
        this.conformance = conformance;
    }

    @SafeVarargs
    public final void addSuppliers(Function<PdfDocument, IBlockElement>... suppliers) {
        Collections.addAll(elementProducers, suppliers);
    }

    public void addBeforeGenerationHook(Consumer<PdfDocument> action) {
        beforeGeneratorHook.add(action);
    }

    public void addAfterGenerationHook(Consumer<PdfDocument> action) {
        afterGeneratorHook.add(action);
    }

    public void assertBothFail(String filename, String expectedMsg) throws IOException {
        checkError(checkErrorLayout(fileName("itext_", filename)), expectedMsg);
        String veraFileName = fileName("vera_", filename);
        veraPdfResult(veraFileName, true);
        if (defaultCheckDocClosingByReopening) {
            checkError(checkErrorOnClosing(veraFileName), expectedMsg);
        }
    }

    public void assertBothValid(String filename) throws IOException {
        Exception e = checkErrorLayout(fileName("itext_", filename));
        String veraFileName = fileName("vera_", filename);
        String veraPdf = veraPdfResult(veraFileName, false);
        Exception eClosing = defaultCheckDocClosingByReopening ? checkErrorOnClosing(veraFileName) : null;

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

    public void assertVeraPdfFailITextValid(String filename) throws IOException {
        veraPdfResult(fileName("vera_", filename), true);
        Exception e = checkErrorLayout(fileName("itext_", filename));
        Assertions.assertNull(e);
    }

    public void assertITextFailVeraPdfValid(String filename, String expectedMsg) throws IOException {
        checkError(checkErrorLayout(fileName("itext_", filename)), expectedMsg);
        assertVeraPdfValid(filename);
    }

    private void assertVeraPdfValid(String filename) throws IOException {
        Assertions.assertNull(veraPdfResult(fileName("vera_", filename), false),
                "Expected no veraPDF validation errors");
    }

    private PdfADocument createPdfDocument(String outputFile) throws IOException {
        return createPdfDocument(null, outputFile);
    }

    private PdfADocument createPdfDocument(String inputFile, String outputFile) throws IOException {
        if (inputFile != null) {
            return new PdfADocument(new PdfReader(inputFile), new PdfWriter(outputFile));
        }
        PdfOutputIntent outputIntent;
        try (InputStream profile = FileUtil.getInputStreamForFile(ICC_PROFILE)) {
            outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", profile);
        }
        return new PdfADocument(new PdfWriter(outputFile), conformance, outputIntent);
    }

    private String pathSafeConformance() {
        return "_A_" + conformance.getPart()
                + (conformance.getLevel() == null ? "" : conformance.getLevel());
    }

    private String fileName(String prefix, String filename) {
        return prefix + filename + pathSafeConformance() + ".pdf";
    }

    private void generateDocument(String filename, boolean disableValidation) throws IOException {
        String outPath = destinationFolder + filename;
        System.out.println(UrlUtil.getNormalizedFileUriString(outPath));
        try (PdfADocument pdfDoc = createPdfDocument(outPath)) {
            if (disableValidation) {
                pdfDoc.getDiContainer().register(ValidationContainer.class, new ValidationContainer());
            }
            for (Consumer<PdfDocument> hook : beforeGeneratorHook) {
                hook.accept(pdfDoc);
            }
            try (Document document = new Document(pdfDoc)) {
                for (Function<PdfDocument, IBlockElement> supplier : elementProducers) {
                    document.add(supplier.apply(pdfDoc));
                }
                for (Consumer<PdfDocument> hook : afterGeneratorHook) {
                    hook.accept(pdfDoc);
                }
            }
        }
    }

    private String veraPdfResult(String filename, boolean failureExpected) throws IOException {
        generateDocument(filename, true);
        VeraPdfValidator validator = new VeraPdfValidator();
        if (failureExpected) {
            validator.validateFailure(destinationFolder + filename);
            return null;
        }
        return validator.validate(destinationFolder + filename);
    }

    private Exception checkErrorLayout(String filename) {
        try {
            generateDocument(filename, false);
        } catch (Exception e) {
            return e;
        }
        return null;
    }

    private Exception checkErrorOnClosing(String filename) {
        String outPath = destinationFolder + "reopen_" + filename;
        System.out.println(UrlUtil.getNormalizedFileUriString(outPath));
        try (PdfADocument document = createPdfDocument(destinationFolder + filename, outPath)) {
            // Closing validates the serialized document, without running generation hooks again.
        } catch (Exception e) {
            return e;
        }
        return null;
    }

    private static void checkError(Exception e, String expectedMsg) {
        if (e == null) {
            Assertions.fail("Expected exception but no exception was thrown");
        }
        if (!(e instanceof PdfAConformanceException) && !(e instanceof Pdf20ConformanceException)) {
            System.out.println(printStackTrace(e));
            Assertions.fail(
                    "Expected exception of type PdfAConformanceException or Pdf20ConformanceException but was: "
                            + e.getClass().getName());
        }
        if (expectedMsg != null) {
            Assertions.assertEquals(expectedMsg, e.getMessage());
        }
        System.out.println(printStackTrace(e));
    }

    private static String printStackTrace(Exception e) {
        return e.toString();
    }
}
