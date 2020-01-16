/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfMarkupAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfPopupAnnotation;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class PdfAnnotationCopyingTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfAnnotationCopyingTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfAnnotationCopyingTest/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    // TODO remove expected exception and thus enable assertions when DEVSIX-3585 is implemented
    public void testCopyingPageWithAnnotationContainingPopupKey() throws IOException {
        junitExpectedException.expect(AssertionError.class);

        String inFilePath = sourceFolder + "annotation-with-popup.pdf";
        String outFilePath = destinationFolder + "copy-annotation-with-popup.pdf";
        PdfDocument originalDocument = new PdfDocument(new PdfReader(inFilePath));
        PdfDocument outDocument = new PdfDocument(new PdfWriter(outFilePath));

        originalDocument.copyPagesTo(1, 1, outDocument);
        // During the second copy call we have to rebuild/preserve all the annotation relationship (Popup in this case),
        // so that we don't end up with annotation on one page referring to an annotation on another page as its popup
        // or as its parent
        originalDocument.copyPagesTo(1, 1, outDocument);

        originalDocument.close();
        outDocument.close();

        outDocument = new PdfDocument(new PdfReader(outFilePath));
        for (int pageNum = 1; pageNum <= outDocument.getNumberOfPages(); pageNum++) {
            PdfPage page = outDocument.getPage(pageNum);
            Assert.assertEquals(2, page.getAnnotsSize());
            Assert.assertEquals(2, page.getAnnotations().size());
            boolean foundMarkupAnnotation = false;
            for (PdfAnnotation annotation : page.getAnnotations()) {
                PdfDictionary annotationPageDict = annotation.getPageObject();
                if (annotationPageDict != null) {
                    Assert.assertSame(page.getPdfObject(), annotationPageDict);
                }
                if (annotation instanceof PdfMarkupAnnotation) {
                    foundMarkupAnnotation = true;
                    PdfPopupAnnotation popup = ((PdfMarkupAnnotation) annotation).getPopup();
                    Assert.assertTrue(MessageFormatUtil.format(
                            "Popup reference must point to annotation present on the same page (# {0})", pageNum),
                            page.containsAnnotation(popup));
                    PdfDictionary parentAnnotation = popup.getParentObject();
                    Assert.assertSame("Popup annotation parent must point to the annotation that specified it as Popup",
                            annotation.getPdfObject(), parentAnnotation);
                }
            }
            Assert.assertTrue("Markup annotation expected to be present but not found", foundMarkupAnnotation);
        }
        outDocument.close();
    }

    @Test
    // TODO remove expected exception and thus enable assertions when DEVSIX-3585 is implemented
    public void testCopyingPageWithAnnotationContainingIrtKey() throws IOException {
        junitExpectedException.expect(AssertionError.class);

        String inFilePath = sourceFolder + "annotation-with-irt.pdf";
        String outFilePath = destinationFolder + "copy-annotation-with-irt.pdf";
        PdfDocument originalDocument = new PdfDocument(new PdfReader(inFilePath));
        PdfDocument outDocument = new PdfDocument(new PdfWriter(outFilePath));

        originalDocument.copyPagesTo(1, 1, outDocument);
        // During the second copy call we have to rebuild/preserve all the annotation relationship (IRT in this case),
        // so that we don't end up with annotation on one page referring to an annotation on another page as its IRT
        // or as its parent
        originalDocument.copyPagesTo(1, 1, outDocument);

        originalDocument.close();
        outDocument.close();

        outDocument = new PdfDocument(new PdfReader(outFilePath));
        for (int pageNum = 1; pageNum <= outDocument.getNumberOfPages(); pageNum++) {
            PdfPage page = outDocument.getPage(pageNum);
            Assert.assertEquals(4, page.getAnnotsSize());
            Assert.assertEquals(4, page.getAnnotations().size());
            boolean foundMarkupAnnotation = false;
            for (PdfAnnotation annotation : page.getAnnotations()) {
                PdfDictionary annotationPageDict = annotation.getPageObject();
                if (annotationPageDict != null) {
                    Assert.assertSame(page.getPdfObject(), annotationPageDict);
                }
                if (annotation instanceof PdfMarkupAnnotation) {
                    foundMarkupAnnotation = true;
                    PdfDictionary inReplyTo = ((PdfMarkupAnnotation) annotation).getInReplyToObject();
                    Assert.assertTrue("IRT reference must point to annotation present on the same page",
                            page.containsAnnotation(PdfAnnotation.makeAnnotation(inReplyTo)));
                }
            }
            Assert.assertTrue("Markup annotation expected to be present but not found", foundMarkupAnnotation);
        }
        outDocument.close();
    }

}
