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
package com.itextpdf.pdfa.checker;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformance;
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
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfA2CheckerTransparencyTest extends ExtendedITextTest {

    private PdfA2Checker pdfA2Checker;

    @BeforeEach
    public void before() {
        pdfA2Checker = new PdfA2Checker(PdfAConformance.PDF_A_2B);
        pdfA2Checker.setFullCheckMode(true);
    }

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

            Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                    () -> pdfA2Checker.checkSinglePage(pageToCheck)
            );
            Assertions.assertEquals(PdfaExceptionMessageConstant.THE_DOCUMENT_DOES_NOT_CONTAIN_A_PDFA_OUTPUTINTENT_BUT_PAGE_CONTAINS_TRANSPARENCY_AND_DOES_NOT_CONTAIN_BLENDING_COLOR_SPACE,
                    e.getMessage());
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

            Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                    () -> pdfA2Checker.checkSinglePage(pageToCheck)
            );
            Assertions.assertEquals(PdfaExceptionMessageConstant.THE_DOCUMENT_DOES_NOT_CONTAIN_A_PDFA_OUTPUTINTENT_BUT_PAGE_CONTAINS_TRANSPARENCY_AND_DOES_NOT_CONTAIN_BLENDING_COLOR_SPACE,
                    e.getMessage());
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

            Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                    () -> pdfA2Checker.checkSinglePage(pageToCheck)
            );
            Assertions.assertEquals(PdfaExceptionMessageConstant.THE_DOCUMENT_DOES_NOT_CONTAIN_A_PDFA_OUTPUTINTENT_BUT_PAGE_CONTAINS_TRANSPARENCY_AND_DOES_NOT_CONTAIN_BLENDING_COLOR_SPACE,
                    e.getMessage());
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
