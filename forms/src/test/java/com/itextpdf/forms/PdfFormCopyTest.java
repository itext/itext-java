/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.forms;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class PdfFormCopyTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/PdfFormFieldsCopyTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/PdfFormFieldsCopyTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 13)
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

        Assert.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_copyFields01.pdf", destinationFolder, "diff_"));
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

        Assert.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_copyFields02.pdf", destinationFolder, "diff_"));
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

        Assert.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_copyFields03.pdf", destinationFolder, "diff_"));
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

        Assert.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_copyLargeFile.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD)
    })
    public void copyFieldsTest04() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "srcFile1.pdf";

        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFilename));
        PdfDocument destDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfPageFormCopier formCopier = new PdfPageFormCopier();
        srcDoc.copyPagesTo(1, srcDoc.getNumberOfPages(), destDoc, formCopier);
        srcDoc.copyPagesTo(1, srcDoc.getNumberOfPages(), destDoc, formCopier);

        PdfAcroForm form = PdfAcroForm.getAcroForm(destDoc, false);
        Assert.assertEquals(1, form.getFields().size());
        Assert.assertNotNull(form.getField("Name1"));
        Assert.assertNotNull(form.getField("Name1.1"));

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

        Assert.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyFields05.pdf", destinationFolder, "diff_"));
    }


    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 12)
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

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(destDoc, false);

        acroForm.getField("text_1").setValue("Text 1!");
        acroForm.getField("text_2").setValue("Text 2!");
        acroForm.getField("text.3").setValue("Text 3!");
        acroForm.getField("text.4").setValue("Text 4!");

        destDoc.close();
        srcDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyMultipleSubfieldsTest01.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 2)
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

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(destDoc, false);

        acroForm.getField("text.3").setValue("Text 3!");

        destDoc.close();
        srcDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyMultipleSubfieldsTest02.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 2)
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

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(destDoc, false);

        acroForm.getField("text_1").setValue("Text 1!");

        destDoc.close();
        srcDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyMultipleSubfieldsTest03.pdf", destinationFolder, "diff_"));
    }


    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 12)
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

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(destDoc, false);

        acroForm.getField("text_1").setValue("Text 1!");
        acroForm.getField("text_2").setValue("Text 2!");
        acroForm.getField("text.3").setValue("Text 3!");
        acroForm.getField("text.4").setValue("Text 4!");

        destDoc.close();
        srcDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyMultipleSubfieldsSmartModeTest01.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 13)
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

        Assert.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyFields06.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 13)
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

        Assert.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyFields07.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 13)
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
        Assert.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_copyFields01.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 26)
    })
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

        Assert.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyFields09.pdf", destinationFolder, "diff_"));
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

        Assert.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyFields10.pdf", destinationFolder, "diff_"));
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

        Assert.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyFields11.pdf", destinationFolder, "diff_"));
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

        Assert.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyFields12.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 1)
    })
    public void copyFieldsTest13() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "copyFields13.pdf";
        String destFilename = destinationFolder + "copyFields13.pdf";

        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFilename));
        PdfDocument destDoc = new PdfDocument(new PdfWriter(destFilename));

        PdfPageFormCopier pdfPageFormCopier = new PdfPageFormCopier();

        for (int i = 0; i < 1; ++i) {
            srcDoc.copyPagesTo(1, 1, destDoc, pdfPageFormCopier);
        }

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(destDoc, false);

        acroForm.getField("text").setValue("Text!");

        destDoc.close();
        srcDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_copyFields13.pdf", destinationFolder, "diff_"));
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
        Assert.assertNull(new CompareTool().compareByContent(destFile, sourceFolder + "cmp_AnnotationSampleStandard_copy.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void unnamedFieldsHierarchyTest() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "unnamedFields.pdf";
        String destFilename = destinationFolder + "hierarchyTest.pdf";
        PdfDocument src = new PdfDocument(new PdfReader(srcFilename));
        PdfDocument merged = new PdfDocument(new PdfWriter(destFilename));
        src.copyPagesTo(1, 1, merged, new PdfPageFormCopier());
        merged.close();
        Assert.assertNull(new CompareTool().compareByContent(destFilename, sourceFolder + "cmp_unnamedFieldsHierarchyTest.pdf", destinationFolder, "diff_"));
    }
}
