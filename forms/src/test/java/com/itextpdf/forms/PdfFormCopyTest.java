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
package com.itextpdf.forms;

import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import java.io.IOException;

@Tag("IntegrationTest")
public class PdfFormCopyTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/PdfFormCopyTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/PdfFormCopyTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 14)
    })
    public void copyFieldsTest01() throws IOException, InterruptedException {
        String srcFilename1 = sourceFolder + "appearances1.pdf";
        String srcFilename2 = sourceFolder + "fieldsOn2-sPage.pdf";
        String srcFilename3 = sourceFolder + "fieldsOn3-sPage.pdf";

        String filename = destinationFolder + "copyFields01.pdf";

        PdfDocument doc1 = new PdfDocument(new PdfReader(srcFilename1));
        PdfDocument doc2 = new PdfDocument(new PdfReader(srcFilename2));
        PdfDocument doc3 = new PdfDocument(new PdfReader(srcFilename3));
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
        pdfDoc.initializeOutlines();

        doc3.copyPagesTo(1, doc3.getNumberOfPages(), pdfDoc, new PdfPageFormCopier());
        doc2.copyPagesTo(1, doc2.getNumberOfPages(), pdfDoc, new PdfPageFormCopier());
        doc1.copyPagesTo(1, doc1.getNumberOfPages(), pdfDoc, new PdfPageFormCopier());

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_copyFields01.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void copyFieldsTest02() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "hello_with_comments.pdf";

        String filename = destinationFolder + "copyFields02.pdf";

        PdfDocument doc1 = new PdfDocument(new PdfReader(srcFilename));
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
        pdfDoc.initializeOutlines();

        doc1.copyPagesTo(1, doc1.getNumberOfPages(), pdfDoc, new PdfPageFormCopier());

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_copyFields02.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void copyFieldsTest03() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "hello2_with_comments.pdf";

        String filename = destinationFolder + "copyFields03.pdf";

        PdfDocument doc1 = new PdfDocument(new PdfReader(srcFilename));
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
        pdfDoc.initializeOutlines();

        doc1.copyPagesTo(1, doc1.getNumberOfPages(), pdfDoc, new PdfPageFormCopier());

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_copyFields03.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void largeFileTest() throws IOException, InterruptedException {
        String srcFilename1 = sourceFolder + "frontpage.pdf";
        String srcFilename2 = sourceFolder + "largeFile.pdf";

        String filename = destinationFolder + "copyLargeFile.pdf";

        PdfDocument doc1 = new PdfDocument(new PdfReader(srcFilename1));
        PdfDocument doc2 = new PdfDocument(new PdfReader(srcFilename2));
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
        pdfDoc.initializeOutlines();

        PdfPageFormCopier formCopier = new PdfPageFormCopier();
        doc1.copyPagesTo(1, doc1.getNumberOfPages(), pdfDoc, formCopier);
        doc2.copyPagesTo(1, 10, pdfDoc, formCopier);

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_copyLargeFile.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD)
    })
    public void copyFieldsTest04() throws IOException {
        String srcFilename = sourceFolder + "srcFile1.pdf";

        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFilename));
        PdfDocument destDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfPageFormCopier formCopier = new PdfPageFormCopier();
        srcDoc.copyPagesTo(1, srcDoc.getNumberOfPages(), destDoc, formCopier);
        srcDoc.copyPagesTo(1, srcDoc.getNumberOfPages(), destDoc, formCopier);

        PdfAcroForm form = PdfFormCreator.getAcroForm(destDoc, false);
        Assertions.assertEquals(1, form.getFields().size());
        Assertions.assertNotNull(form.getField("Name1"));

        destDoc.close();
    }

    @Test
    public void copyFieldsTest05() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "srcFile1.pdf";
        String destFilename = destinationFolder + "copyFields05.pdf";

        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFilename));
        PdfDocument destDoc = new PdfDocument(new PdfWriter(destFilename));

        destDoc.addPage(srcDoc.getFirstPage().copyTo(destDoc, new PdfPageFormCopier()));
        destDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyFields05.pdf", destinationFolder, "diff_"));
    }


    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 9)
    })
    public void copyMultipleSubfieldsTest01() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "copyMultipleSubfieldsTest01.pdf";
        String destFilename = destinationFolder + "copyMultipleSubfieldsTest01.pdf";

        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFilename));
        PdfDocument destDoc = new PdfDocument(new PdfWriter(destFilename));

        PdfPageFormCopier pdfPageFormCopier = new PdfPageFormCopier();
        // copying the same page from the same document twice
        for (int i = 0; i < 4; ++i) {
            srcDoc.copyPagesTo(1, 1, destDoc, pdfPageFormCopier);
        }

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(destDoc, false);

        acroForm.getField("text_1").setValue("Text 1!");
        acroForm.getField("text_2").setValue("Text 2!");
        acroForm.getField("text.3").setValue("Text 3!");
        acroForm.getField("text.4").setValue("Text 4!");

        destDoc.close();
        srcDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyMultipleSubfieldsTest01.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 2)
    })
    public void copyMultipleSubfieldsTest02() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "copyMultipleSubfieldsTest02.pdf";
        String destFilename = destinationFolder + "copyMultipleSubfieldsTest02.pdf";

        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFilename));
        PdfDocument destDoc = new PdfDocument(new PdfWriter(destFilename));

        PdfPageFormCopier pdfPageFormCopier = new PdfPageFormCopier();
        // copying the same page from the same document twice
        for (int i = 0; i < 3; ++i) {
            srcDoc.copyPagesTo(1, 1, destDoc, pdfPageFormCopier);
        }

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(destDoc, false);

        acroForm.getField("text.3").setValue("Text 3!");

        destDoc.close();
        srcDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyMultipleSubfieldsTest02.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 2)
    })
    public void copyMultipleSubfieldsTest03() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "copyMultipleSubfieldsTest03.pdf";
        String destFilename = destinationFolder + "copyMultipleSubfieldsTest03.pdf";

        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFilename));
        PdfDocument destDoc = new PdfDocument(new PdfWriter(destFilename));

        PdfPageFormCopier pdfPageFormCopier = new PdfPageFormCopier();
        // copying the same page from the same document twice
        for (int i = 0; i < 3; ++i) {
            srcDoc.copyPagesTo(1, 1, destDoc, pdfPageFormCopier);
        }

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(destDoc, false);

        acroForm.getField("text_1").setValue("Text 1!");

        destDoc.close();
        srcDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyMultipleSubfieldsTest03.pdf", destinationFolder, "diff_"));
    }


    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 9)
    })
    public void copyMultipleSubfieldsSmartModeTest01() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "copyMultipleSubfieldsSmartModeTest01.pdf";
        String destFilename = destinationFolder + "copyMultipleSubfieldsSmartModeTest01.pdf";

        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFilename));
        PdfDocument destDoc = new PdfDocument(new PdfWriter(destFilename).setSmartMode(true));

        PdfPageFormCopier pdfPageFormCopier = new PdfPageFormCopier();
        // copying the same page from the same document twice
        for (int i = 0; i < 4; ++i) {
            srcDoc.copyPagesTo(1, 1, destDoc, pdfPageFormCopier);
        }

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(destDoc, false);

        acroForm.getField("text_1").setValue("Text 1!");
        acroForm.getField("text_2").setValue("Text 2!");
        acroForm.getField("text.3").setValue("Text 3!");
        acroForm.getField("text.4").setValue("Text 4!");

        destDoc.close();
        srcDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyMultipleSubfieldsSmartModeTest01.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 14)
    })
    public void copyFieldsTest06() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "datasheet.pdf";
        String destFilename = destinationFolder + "copyFields06.pdf";

        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFilename));
        PdfDocument destDoc = new PdfDocument(new PdfWriter(destFilename));

        PdfPageFormCopier pdfPageFormCopier = new PdfPageFormCopier();
        // copying the same page from the same document twice
        for (int i = 0; i < 2; ++i) {
            srcDoc.copyPagesTo(1, 1, destDoc, pdfPageFormCopier);
        }
        destDoc.close();
        srcDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyFields06.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 14)
    })
    public void copyFieldsTest07() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "datasheet.pdf";
        String destFilename = destinationFolder + "copyFields07.pdf";

        PdfDocument destDoc = new PdfDocument(new PdfWriter(destFilename));

        PdfPageFormCopier pdfPageFormCopier = new PdfPageFormCopier();
        // copying the same page from reopened document twice
        for (int i = 0; i < 2; ++i) {
            PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFilename));
            srcDoc.copyPagesTo(1, 1, destDoc, pdfPageFormCopier);
            srcDoc.close();
        }
        destDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyFields07.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 14)
    })
    public void copyFieldsTest08() throws IOException, InterruptedException {
        String srcFilename1 = sourceFolder + "appearances1.pdf";
        String srcFilename2 = sourceFolder + "fieldsOn2-sPage.pdf";
        String srcFilename3 = sourceFolder + "fieldsOn3-sPage.pdf";

        String filename = destinationFolder + "copyFields08.pdf";

        PdfDocument doc1 = new PdfDocument(new PdfReader(srcFilename1));
        PdfDocument doc2 = new PdfDocument(new PdfReader(srcFilename2));
        PdfDocument doc3 = new PdfDocument(new PdfReader(srcFilename3));
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
        pdfDoc.initializeOutlines();

        PdfPageFormCopier formCopier = new PdfPageFormCopier();
        doc3.copyPagesTo(1, doc3.getNumberOfPages(), pdfDoc, formCopier);
        doc2.copyPagesTo(1, doc2.getNumberOfPages(), pdfDoc, formCopier);
        doc1.copyPagesTo(1, doc1.getNumberOfPages(), pdfDoc, formCopier);

        pdfDoc.close();

        // comparing with cmp_copyFields01.pdf on purpose: result should be the same as in the first test
        Assertions.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_copyFields01.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void copyFieldsTest09() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "datasheet.pdf";
        String destFilename = destinationFolder + "copyFields09.pdf";
        PdfDocument destDoc = new PdfDocument(new PdfWriter(destFilename, new WriterProperties().useSmartMode()));
        // copying the same page from the same document twice
        PdfPageFormCopier copier = new PdfPageFormCopier();
        for (int i = 0; i < 3; ++i) {
            PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFilename));
            srcDoc.copyPagesTo(1, 1, destDoc, copier);
            destDoc.flushCopiedObjects(srcDoc);

            srcDoc.close();
        }
        destDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyFields09.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void copyFieldsTest10() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "datasheet.pdf";
        String destFilename = destinationFolder + "copyFields10.pdf";
        PdfDocument destDoc = new PdfDocument(new PdfWriter(destFilename, new WriterProperties().useSmartMode()));
        // copying the same page from the same document twice
        for (int i = 0; i < 3; ++i) {
            PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFilename));
            srcDoc.copyPagesTo(1, 1, destDoc, new PdfPageFormCopier());
            destDoc.flushCopiedObjects(srcDoc);
            srcDoc.close();
        }
        destDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyFields10.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void copyFieldsTest11() throws IOException, InterruptedException {
        String srcFilename1 = sourceFolder + "datasheet.pdf";
        String srcFilename2 = sourceFolder + "datasheet2.pdf";
        String destFilename = destinationFolder + "copyFields11.pdf";
        PdfDocument destDoc = new PdfDocument(new PdfWriter(destFilename, new WriterProperties()));

        PdfDocument srcDoc1 = new PdfDocument(new PdfReader(srcFilename1));
        srcDoc1.copyPagesTo(1, 1, destDoc, new PdfPageFormCopier());
        destDoc.flushCopiedObjects(srcDoc1);
        srcDoc1.close();

        PdfDocument srcDoc2 = new PdfDocument(new PdfReader(srcFilename2));
        srcDoc2.copyPagesTo(1, 1, destDoc, new PdfPageFormCopier());
        destDoc.flushCopiedObjects(srcDoc2);
        srcDoc2.close();

        destDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyFields11.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void copyFieldsTest12() throws IOException, InterruptedException {
        String srcFilename1 = sourceFolder + "datasheet.pdf";
        String srcFilename2 = sourceFolder + "datasheet2.pdf";
        String destFilename = destinationFolder + "copyFields12.pdf";
        PdfDocument destDoc = new PdfDocument(new PdfWriter(destFilename, new WriterProperties().useSmartMode()));

        PdfDocument srcDoc2 = new PdfDocument(new PdfReader(srcFilename2));
        srcDoc2.copyPagesTo(1, 1, destDoc, new PdfPageFormCopier());
        destDoc.flushCopiedObjects(srcDoc2);
        srcDoc2.close();

        PdfDocument srcDoc1 = new PdfDocument(new PdfReader(srcFilename1));
        srcDoc1.copyPagesTo(1, 1, destDoc, new PdfPageFormCopier());
        destDoc.flushCopiedObjects(srcDoc1);
        srcDoc1.close();

        destDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyFields12.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void copyFieldsTest13() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "copyFields13.pdf";
        String destFilename = destinationFolder + "copyFields13.pdf";

        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFilename));
        PdfDocument destDoc = new PdfDocument(new PdfWriter(destFilename));

        PdfPageFormCopier pdfPageFormCopier = new PdfPageFormCopier();

        for (int i = 0; i < 1; ++i) {
            srcDoc.copyPagesTo(1, 1, destDoc, pdfPageFormCopier);
        }

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(destDoc, false);

        acroForm.getField("text").setValue("Text!");

        destDoc.close();
        srcDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyFields13.pdf", destinationFolder, "diff_"));
    }


    @Test
    public void copyPagesWithInheritedResources() throws IOException, InterruptedException {
        String sourceFile = sourceFolder + "AnnotationSampleStandard.pdf";
        String destFile = destinationFolder + "AnnotationSampleStandard_copy.pdf";
        PdfDocument source = new PdfDocument(new PdfReader(sourceFile));
        PdfDocument target = new PdfDocument(new PdfWriter(destFile));
        target.initializeOutlines();
        source.copyPagesTo(1, source.getNumberOfPages(), target, new PdfPageFormCopier());
        target.close();
        Assertions.assertNull(new CompareTool().compareByContent(destFile, sourceFolder + "cmp_AnnotationSampleStandard_copy.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void unnamedFieldsHierarchyTest() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "unnamedFields.pdf";
        String destFilename = destinationFolder + "hierarchyTest.pdf";
        PdfDocument src = new PdfDocument(new PdfReader(srcFilename));
        PdfDocument merged = new PdfDocument(new PdfWriter(destFilename));
        src.copyPagesTo(1, 1, merged, new PdfPageFormCopier());
        merged.close();
        Assertions.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_unnamedFieldsHierarchyTest.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 45)
    })
    public void copyAndEditTextFields() throws IOException, InterruptedException {
        String srcFileName = sourceFolder + "checkPdfFormCopy_Source.pdf";
        String destFilename = destinationFolder + "copyAndEditTextFields.pdf";
        String cmpFileName = sourceFolder + "cmp_copyAndEditTextFields.pdf";

        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFileName));
        PdfDocument destDoc = new PdfDocument(new PdfWriter(destFilename));

        PdfPageFormCopier pdfPageFormCopier = new PdfPageFormCopier();
        for (int i = 0; i < 4; i++) {
            srcDoc.copyPagesTo(1, 1, destDoc, pdfPageFormCopier);
        }

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(destDoc, false);
        acroForm.getField("text_1").setValue("text_1");
        acroForm.getField("NumberField_text.2").setValue("-100.00");
        acroForm.getField("NumberField_text.2_1").setValue("3.00");
        acroForm.getField("text.3_1<!").setValue("text.3_1<!");
        acroForm.getField("text.4___#1+1").setValue("CHANGEDtext");

        destDoc.close();
        srcDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(destFilename, cmpFileName, destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 45)
    })
    public void copyAndEditCheckboxes() throws IOException, InterruptedException {
        String srcFileName = sourceFolder + "checkPdfFormCopy_Source.pdf";
        String destFilename = destinationFolder + "copyAndEditCheckboxes.pdf";
        String cmpFileName = sourceFolder + "cmp_copyAndEditCheckboxes.pdf";

        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFileName));
        PdfDocument destDoc = new PdfDocument(new PdfWriter(destFilename));

        PdfPageFormCopier pdfPageFormCopier = new PdfPageFormCopier();
        for (int i = 0; i < 4; i++) {
            srcDoc.copyPagesTo(1, 1, destDoc, pdfPageFormCopier);
        }

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(destDoc, false);
        acroForm.getField("CheckBox_1").setValue("On");
        acroForm.getField("Check Box.2").setValue("Off");
        acroForm.getField("CheckBox4.1#1").setValue("Off");

        destDoc.close();
        srcDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(destFilename, cmpFileName, destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 45)
    })
    public void copyAndEditRadioButtons() throws IOException, InterruptedException {
        String srcFileName = sourceFolder + "checkPdfFormCopy_Source.pdf";
        String destFilename = destinationFolder + "copyAndEditRadioButtons.pdf";
        String cmpFileName = sourceFolder + "cmp_copyAndEditRadioButtons.pdf";

        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFileName));
        PdfDocument destDoc = new PdfDocument(new PdfWriter(destFilename));

        PdfPageFormCopier pdfPageFormCopier = new PdfPageFormCopier();
        for (int i = 0; i < 4; i++) {
            srcDoc.copyPagesTo(1, 1, destDoc, pdfPageFormCopier);
        }

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(destDoc, false);
        acroForm.getField("Group.4").setValue("Choice_3!<>3.3.3");

        destDoc.close();
        srcDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(destFilename, cmpFileName, destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD)
    })
    public void mergeMergedFieldAndMergedFieldTest() throws IOException, InterruptedException {
        String srcFileName1 = sourceFolder + "fieldMergedWithWidget.pdf";
        String destFilename = destinationFolder + "mergeMergedFieldAndMergedFieldTest.pdf";
        String cmpFileName = sourceFolder + "cmp_mergeMergedFieldAndMergedFieldTest.pdf";

        try (
                PdfWriter writer = new PdfWriter(destFilename);
                PdfDocument resultPdfDocument = new PdfDocument(writer);
                PdfReader reader1 = new PdfReader(srcFileName1);
                PdfDocument sourceDoc1 = new PdfDocument(reader1)) {
            PdfFormCreator.getAcroForm(resultPdfDocument, true);
            PdfPageFormCopier formCopier = new PdfPageFormCopier();

            sourceDoc1.copyPagesTo(1, sourceDoc1.getNumberOfPages(), resultPdfDocument, formCopier);
            sourceDoc1.copyPagesTo(1, sourceDoc1.getNumberOfPages(), resultPdfDocument, formCopier);
        }

        Assertions.assertNull(new CompareTool().compareByContent(destFilename, cmpFileName, destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 1)
    })
    public void mergeMergedFieldAndTwoWidgetsTest() throws IOException, InterruptedException {
        String srcFileName1 = sourceFolder + "fieldMergedWithWidget.pdf";
        String srcFileName2 = sourceFolder + "fieldTwoWidgets.pdf";
        String destFilename = destinationFolder + "mergeMergedFieldAndTwoWidgetsTest.pdf";
        String cmpFileName = sourceFolder + "cmp_mergeMergedFieldAndTwoWidgetsTest.pdf";

        try (
                PdfWriter writer = new PdfWriter(destFilename);
                PdfDocument resultPdfDocument = new PdfDocument(writer);
                PdfReader reader1 = new PdfReader(srcFileName1);
                PdfDocument sourceDoc1 = new PdfDocument(reader1);
                PdfReader reader2 = new PdfReader(srcFileName2);
                PdfDocument sourceDoc2 = new PdfDocument(reader2)) {
            PdfFormCreator.getAcroForm(resultPdfDocument, true);
            PdfPageFormCopier formCopier = new PdfPageFormCopier();

            sourceDoc1.copyPagesTo(1, sourceDoc1.getNumberOfPages(), resultPdfDocument, formCopier);
            sourceDoc2.copyPagesTo(1, sourceDoc2.getNumberOfPages(), resultPdfDocument, formCopier);
        }

        Assertions.assertNull(new CompareTool().compareByContent(destFilename, cmpFileName, destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD)
    })
    public void mergeTwoWidgetsAndMergedFieldTest() throws IOException, InterruptedException {
        String srcFileName1 = sourceFolder + "fieldMergedWithWidget.pdf";
        String srcFileName2 = sourceFolder + "fieldTwoWidgets.pdf";
        String destFilename = destinationFolder + "mergeTwoWidgetsAndMergedFieldTest.pdf";
        String cmpFileName = sourceFolder + "cmp_mergeTwoWidgetsAndMergedFieldTest.pdf";

        try (
                PdfWriter writer = new PdfWriter(destFilename);
                PdfDocument resultPdfDocument = new PdfDocument(writer);
                PdfReader reader1 = new PdfReader(srcFileName1);
                PdfDocument sourceDoc1 = new PdfDocument(reader1);
                PdfReader reader2 = new PdfReader(srcFileName2);
                PdfDocument sourceDoc2 = new PdfDocument(reader2)) {
            PdfFormCreator.getAcroForm(resultPdfDocument, true);
            PdfPageFormCopier formCopier = new PdfPageFormCopier();

            sourceDoc2.copyPagesTo(1, sourceDoc2.getNumberOfPages(), resultPdfDocument, formCopier);
            sourceDoc1.copyPagesTo(1, sourceDoc1.getNumberOfPages(), resultPdfDocument, formCopier);
        }

        Assertions.assertNull(new CompareTool().compareByContent(destFilename, cmpFileName, destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD)
    })
    public void mergeTwoWidgetsAndTwoWidgetsTest() throws IOException, InterruptedException {
        String srcFileName2 = sourceFolder + "fieldTwoWidgets.pdf";
        String destFilename = destinationFolder + "mergeTwoWidgetsAndTwoWidgetsTest.pdf";
        String cmpFileName = sourceFolder + "cmp_mergeTwoWidgetsAndTwoWidgetsTest.pdf";

        try (
                PdfWriter writer = new PdfWriter(destFilename);
                PdfDocument resultPdfDocument = new PdfDocument(writer);
                PdfReader reader2 = new PdfReader(srcFileName2);
                PdfDocument sourceDoc2 = new PdfDocument(reader2)) {
            PdfFormCreator.getAcroForm(resultPdfDocument, true);
            PdfPageFormCopier formCopier = new PdfPageFormCopier();

            sourceDoc2.copyPagesTo(1, sourceDoc2.getNumberOfPages(), resultPdfDocument, formCopier);
            sourceDoc2.copyPagesTo(1, sourceDoc2.getNumberOfPages(), resultPdfDocument, formCopier);
        }

        Assertions.assertNull(new CompareTool().compareByContent(destFilename, cmpFileName, destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 2)
    })
    public void complexFieldsHierarchyTest() throws IOException, InterruptedException {
        String srcFileName = sourceFolder + "complexFieldsHierarchyTest.pdf";
        String destFilename = destinationFolder + "complexFieldsHierarchyTest.pdf";
        String cmpFileName = sourceFolder + "cmp_complexFieldsHierarchyTest.pdf";

        try (PdfDocument pdfDocMerged = new PdfDocument(new PdfReader(srcFileName), new PdfWriter(destFilename));
                PdfDocument pdfDoc = new PdfDocument(new PdfReader(srcFileName))) {
            pdfDoc.copyPagesTo(1, pdfDoc.getNumberOfPages(), pdfDocMerged, new PdfPageFormCopier());
            pdfDoc.copyPagesTo(1, pdfDoc.getNumberOfPages(), pdfDocMerged, new PdfPageFormCopier());
        }

        Assertions.assertNull(new CompareTool().compareByContent(destFilename, cmpFileName, destinationFolder, "diff_"));
    }

    @Test
    public void widgetContainsNoTEntryTest() throws IOException, InterruptedException {
        String sourceFileName = sourceFolder + "fieldThreeWidgets.pdf";
        String destFileName = destinationFolder + "widgetContainsNoTEntryTest.pdf";
        String cmpFileName = sourceFolder + "cmp_widgetContainsNoTEntryTest.pdf";
        PdfDocument sourcePdfDocument = new PdfDocument(new PdfReader(sourceFileName));
        PdfDocument resultPdfDocument = new PdfDocument(new PdfWriter(destFileName));
        sourcePdfDocument.copyPagesTo(1, sourcePdfDocument.getNumberOfPages(), resultPdfDocument, new PdfPageFormCopier());
        resultPdfDocument.close();
        Assertions.assertNull(new CompareTool().compareByContent(destFileName, cmpFileName, destinationFolder, "diff_"));
    }
}
