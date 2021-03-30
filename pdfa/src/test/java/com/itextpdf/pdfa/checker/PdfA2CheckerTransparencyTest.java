/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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
package com.itextpdf.pdfa.checker;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfPopupAnnotation;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfTransparencyGroup;
import com.itextpdf.pdfa.PdfAConformanceException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfA2CheckerTransparencyTest extends ExtendedITextTest {

    private PdfA2Checker pdfA2Checker;

    @Before
    public void before() {
        pdfA2Checker = new PdfA2Checker(PdfAConformanceLevel.PDF_A_2B);
        pdfA2Checker.setFullCheckMode(true);
    }

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void checkPatternWithFormResourceCycle() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                PdfWriter writer = new PdfWriter(bos);
                PdfDocument document = new PdfDocument(writer)) {

            PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(0f, 0f));
            formXObject.getResources().addForm(formXObject);

            PdfPattern.Tiling tillingPattern = new PdfPattern.Tiling(0f, 0f);
            tillingPattern.getResources().addForm(formXObject);

            PdfPage pageToCheck = document.addNewPage();
            PdfResources pageResources = pageToCheck.getResources();
            pageResources.addPattern(new PdfPattern.Shading(new PdfDictionary()));
            pageResources.addPattern(tillingPattern);

            ensureTransparencyObjectsNotEmpty();

            // no assertions as we want to check that no exceptions would be thrown
            pdfA2Checker.checkSinglePage(pageToCheck);
        }
    }

    @Test
    public void checkAppearanceStreamsWithCycle() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                PdfWriter writer = new PdfWriter(bos);
                PdfDocument document = new PdfDocument(writer)) {

            PdfDictionary normalAppearance = new PdfDictionary();
            normalAppearance.put(PdfName.ON, normalAppearance);

            normalAppearance.makeIndirect(document);

            PdfAnnotation annotation = new PdfPopupAnnotation(new Rectangle(0f, 0f));
            annotation.setAppearance(PdfName.N, normalAppearance);

            PdfPage pageToCheck = document.addNewPage();
            pageToCheck.addAnnotation(annotation);

            ensureTransparencyObjectsNotEmpty();

            // no assertions as we want to check that no exceptions would be thrown
            pdfA2Checker.checkPageTransparency(pageToCheck.getPdfObject(), pageToCheck.getResources().getPdfObject());
        }
    }

    @Test
    public void checkPatternWithTransparentFormResource() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                PdfWriter writer = new PdfWriter(bos);
                PdfDocument document = new PdfDocument(writer)) {

            PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(0f, 0f));
            formXObject.setGroup(new PdfTransparencyGroup());

            PdfPattern.Tiling tillingPattern = new PdfPattern.Tiling(0f, 0f);
            tillingPattern.getResources().addForm(formXObject);

            PdfPage pageToCheck = document.addNewPage();
            PdfResources pageResources = pageToCheck.getResources();
            pageResources.addPattern(new PdfPattern.Shading(new PdfDictionary()));
            pageResources.addPattern(tillingPattern);

            junitExpectedException.expect(PdfAConformanceException.class);
            junitExpectedException.expectMessage(
                    PdfAConformanceException.THE_DOCUMENT_DOES_NOT_CONTAIN_A_PDFA_OUTPUTINTENT_BUT_PAGE_CONTAINS_TRANSPARENCY_AND_DOES_NOT_CONTAIN_BLENDING_COLOR_SPACE);

            pdfA2Checker.checkSinglePage(pageToCheck);
        }
    }

    @Test
    public void checkPatternWithoutTransparentFormResource() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                PdfWriter writer = new PdfWriter(bos);
                PdfDocument document = new PdfDocument(writer)) {

            PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(0f, 0f));

            PdfPattern.Tiling tillingPattern = new PdfPattern.Tiling(0f, 0f);
            tillingPattern.getResources().addForm(formXObject);

            PdfPage pageToCheck = document.addNewPage();
            PdfResources pageResources = pageToCheck.getResources();
            pageResources.addPattern(new PdfPattern.Shading(new PdfDictionary()));
            pageResources.addPattern(tillingPattern);

            ensureTransparencyObjectsNotEmpty();

            // no assertions as we want to check that no exceptions would be thrown
            pdfA2Checker.checkSinglePage(pageToCheck);
        }
    }

    @Test
    public void checkAppearanceStreamWithTransparencyGroup() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                PdfWriter writer = new PdfWriter(bos);
                PdfDocument document = new PdfDocument(writer)) {

            PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(0f, 0f));
            formXObject.setGroup(new PdfTransparencyGroup());

            PdfAnnotation annotation = new PdfPopupAnnotation(new Rectangle(0f, 0f));
            annotation.setNormalAppearance(formXObject.getPdfObject());

            PdfPage pageToCheck = document.addNewPage();

            pageToCheck.addAnnotation(new PdfPopupAnnotation(new Rectangle(0f, 0f)));
            pageToCheck.addAnnotation(annotation);

            junitExpectedException.expect(PdfAConformanceException.class);
            junitExpectedException.expectMessage(
                    PdfAConformanceException.THE_DOCUMENT_DOES_NOT_CONTAIN_A_PDFA_OUTPUTINTENT_BUT_PAGE_CONTAINS_TRANSPARENCY_AND_DOES_NOT_CONTAIN_BLENDING_COLOR_SPACE);

            pdfA2Checker.checkSinglePage(pageToCheck);
        }
    }

    @Test
    public void checkAppearanceStreamWithTransparencyGroup2() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                PdfWriter writer = new PdfWriter(bos);
                PdfDocument document = new PdfDocument(writer)) {

            PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(0f, 0f));
            formXObject.setGroup(new PdfTransparencyGroup());

            PdfFormXObject formStream = new PdfFormXObject(new Rectangle(0f, 0f));
            formStream.getResources().addForm(formXObject);

            PdfAnnotation annotation = new PdfPopupAnnotation(new Rectangle(0f, 0f));
            annotation.setNormalAppearance(formStream.getPdfObject());

            PdfPage pageToCheck = document.addNewPage();

            pageToCheck.addAnnotation(new PdfPopupAnnotation(new Rectangle(0f, 0f)));
            pageToCheck.addAnnotation(annotation);

            junitExpectedException.expect(PdfAConformanceException.class);
            junitExpectedException.expectMessage(
                    PdfAConformanceException.THE_DOCUMENT_DOES_NOT_CONTAIN_A_PDFA_OUTPUTINTENT_BUT_PAGE_CONTAINS_TRANSPARENCY_AND_DOES_NOT_CONTAIN_BLENDING_COLOR_SPACE);

            pdfA2Checker.checkSinglePage(pageToCheck);
        }
    }

    @Test
    public void checkAppearanceStreamWithoutTransparencyGroup() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                PdfWriter writer = new PdfWriter(bos);
                PdfDocument document = new PdfDocument(writer)) {

            PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(0f, 0f));

            PdfAnnotation annotation = new PdfPopupAnnotation(new Rectangle(0f, 0f));
            annotation.setNormalAppearance(formXObject.getPdfObject());

            PdfPage pageToCheck = document.addNewPage();

            pageToCheck.addAnnotation(new PdfPopupAnnotation(new Rectangle(0f, 0f)));
            pageToCheck.addAnnotation(annotation);

            ensureTransparencyObjectsNotEmpty();

            // no assertions as we want to check that no exceptions would be thrown
            pdfA2Checker.checkSinglePage(pageToCheck);
        }
    }

    private void ensureTransparencyObjectsNotEmpty() {
        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(0f, 0f));
        formXObject.setGroup(new PdfTransparencyGroup());

        pdfA2Checker.checkFormXObject(formXObject.getPdfObject());
    }
}
