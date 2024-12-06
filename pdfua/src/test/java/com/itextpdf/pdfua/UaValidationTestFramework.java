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
package com.itextpdf.pdfua;

import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.validation.ValidationContainer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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
        checkError(checkErrorLayout("layout_" + filename + ".pdf"), expectedMsg);

        final String createdFileName = "vera_" + filename + ".pdf";
        verAPdfResult(createdFileName, true);

        if (checkDocClosing) {
            System.out.println("Checking closing");
            checkError(checkErrorOnClosing(createdFileName), expectedMsg);
        }
    }

    public void assertBothValid(String fileName) throws IOException {
        Exception e = checkErrorLayout("layout_" + fileName + ".pdf");
        String veraPdf = verAPdfResult("vera_" + fileName + ".pdf", false);
        Exception eClosing =  checkErrorOnClosing("vera_" + fileName + ".pdf");
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
            sb.append("OnClosing no expection expected but was:\n").append(eClosing);
        }
        if (counter != 3) {
            Assertions.fail("One of the checks did not throw\n\n" + sb.toString());
        }
        Assertions.fail(sb.toString());
    }

    public void addBeforeGenerationHook(Consumer<PdfDocument> action) {
        this.beforeGeneratorHook.add(action);
    }

    private String verAPdfResult(String filename, boolean failureExpected) throws IOException {
        String outfile = UrlUtil.getNormalizedFileUriString(destinationFolder + filename);
        System.out.println(outfile);
        PdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(destinationFolder + filename));

        Document document = new Document(pdfDoc);
        document.getPdfDocument().getDiContainer().register(ValidationContainer.class, new ValidationContainer());
        for (Consumer<PdfDocument> pdfDocumentConsumer : this.beforeGeneratorHook) {
            pdfDocumentConsumer.accept(pdfDoc);
        }
        for (Generator<IBlockElement> blockElementSupplier : elementProducers) {
            document.add(blockElementSupplier.generate());
        }
        document.close();
        VeraPdfValidator validator = new VeraPdfValidator();// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
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
        if (!(e instanceof PdfUAConformanceException)) {
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
            PdfDocument pdfDoc = new PdfUATestPdfDocument(
                    new PdfWriter(outPath));
            for (Consumer<PdfDocument> pdfDocumentConsumer : this.beforeGeneratorHook) {
                pdfDocumentConsumer.accept(pdfDoc);
            }
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

    private Exception checkErrorOnClosing(String filename) {
        try {
            final String outPath = destinationFolder + "reopen_" + filename;
            final String inPath = destinationFolder + filename;
            System.out.println(UrlUtil.getNormalizedFileUriString(outPath));
            PdfDocument pdfDoc = new PdfUATestPdfDocument(
                    new PdfReader(inPath),
                    new PdfWriter(outPath));

            pdfDoc.close();
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
