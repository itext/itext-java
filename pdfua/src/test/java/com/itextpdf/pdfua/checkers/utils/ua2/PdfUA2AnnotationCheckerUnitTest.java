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
package com.itextpdf.pdfua.checkers.utils.ua2;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfCaretAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfCircleAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfFileAttachmentAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfInkAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLineAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfPopupAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfRedactAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfSquareAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWatermarkAnnotation;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.pdfua.PdfUAConfig;
import com.itextpdf.pdfua.PdfUADocument;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class PdfUA2AnnotationCheckerUnitTest extends ExtendedITextTest {

    @Test
    public void basicAnnotationBadParent() {
        PdfLineAnnotation lineAnnotation = new PdfLineAnnotation(new Rectangle(0, 0, 100, 100), new float[]{2, 3});
        PdfStructElem parent = new PdfStructElem(null, PdfName.Div);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            PdfUA2AnnotationChecker.checkAnnotation(lineAnnotation.getPdfObject(), parent);
        });
        Assertions.assertEquals(PdfUAExceptionMessageConstants.MARKUP_ANNOT_IS_NOT_TAGGED_AS_ANNOT,
                e.getMessage());
    }

    @Test
    public void basicLineAnnotation() {
        PdfLineAnnotation lineAnnotation = new PdfLineAnnotation(new Rectangle(0, 0, 100, 100), new float[]{2, 3});
        PdfStructElem parent = new PdfStructElem(null, PdfName.Annot);
        AssertUtil.doesNotThrow(() -> {
            PdfUA2AnnotationChecker.checkAnnotation(lineAnnotation.getPdfObject(), parent);
        });
    }

    @Test
    public void basicSquareAnnotation() {
        PdfSquareAnnotation squareAnnotation = new PdfSquareAnnotation(new Rectangle(0, 0, 100, 100));
        PdfStructElem parent = new PdfStructElem(null, PdfName.Annot);
        AssertUtil.doesNotThrow(() -> {
            PdfUA2AnnotationChecker.checkAnnotation(squareAnnotation.getPdfObject(), parent);
        });
    }

    @Test
    public void basicCircleAnnotation() {
        PdfCircleAnnotation circleAnnotation = new PdfCircleAnnotation(new Rectangle(0, 0, 100, 100));
        PdfStructElem parent = new PdfStructElem(null, PdfName.Annot);
        AssertUtil.doesNotThrow(() -> {
            PdfUA2AnnotationChecker.checkAnnotation(circleAnnotation.getPdfObject(), parent);
        });
    }

    @Test
    public void basicPolygonAnnotation() {
        PdfDictionary annotation = new PdfDictionary();
        annotation.put(PdfName.Subtype, PdfName.Polygon);
        PdfAnnotation annot = PdfAnnotation.makeAnnotation(annotation);
        PdfStructElem parent = new PdfStructElem(null, PdfName.Annot);
        AssertUtil.doesNotThrow(() -> {
            PdfUA2AnnotationChecker.checkAnnotation(annot.getPdfObject(), parent);
        });
    }

    @Test
    public void basicPolyLineAnnotation() {
        PdfDictionary annotation = new PdfDictionary();
        annotation.put(PdfName.Subtype, PdfName.PolyLine);
        PdfAnnotation annot = PdfAnnotation.makeAnnotation(annotation);
        PdfStructElem parent = new PdfStructElem(null, PdfName.Annot);
        AssertUtil.doesNotThrow(() -> {
            PdfUA2AnnotationChecker.checkAnnotation(annot.getPdfObject(), parent);
        });
    }

    @Test
    public void basicHighlightAnnotation() {
        PdfDictionary annotation = new PdfDictionary();
        annotation.put(PdfName.Subtype, PdfName.Highlight);
        PdfAnnotation annot = PdfAnnotation.makeAnnotation(annotation);
        PdfStructElem parent = new PdfStructElem(null, PdfName.Annot);
        AssertUtil.doesNotThrow(() -> {
            PdfUA2AnnotationChecker.checkAnnotation(annot.getPdfObject(), parent);
        });
    }

    @Test
    public void basicUnderlineAnnotation() {
        PdfDictionary annotation = new PdfDictionary();
        annotation.put(PdfName.Subtype, PdfName.Underline);
        PdfAnnotation annot = PdfAnnotation.makeAnnotation(annotation);
        PdfStructElem parent = new PdfStructElem(null, PdfName.Annot);
        AssertUtil.doesNotThrow(() -> {
            PdfUA2AnnotationChecker.checkAnnotation(annot.getPdfObject(), parent);
        });
    }

    @Test
    public void basicSquigglyAnnotation() {
        PdfDictionary annotation = new PdfDictionary();
        annotation.put(PdfName.Subtype, PdfName.Squiggly);
        PdfAnnotation annot = PdfAnnotation.makeAnnotation(annotation);
        PdfStructElem parent = new PdfStructElem(null, PdfName.Annot);
        AssertUtil.doesNotThrow(() -> {
            PdfUA2AnnotationChecker.checkAnnotation(annot.getPdfObject(), parent);
        });
    }

    @Test
    public void basicStrikeOutAnnotation() {
        PdfDictionary annotation = new PdfDictionary();
        annotation.put(PdfName.Subtype, PdfName.StrikeOut);
        PdfAnnotation annot = PdfAnnotation.makeAnnotation(annotation);
        PdfStructElem parent = new PdfStructElem(null, PdfName.Annot);
        AssertUtil.doesNotThrow(() -> {
            PdfUA2AnnotationChecker.checkAnnotation(annot.getPdfObject(), parent);
        });
    }

    @Test
    public void basicCaretAnnotation() {
        PdfCaretAnnotation annotation = new PdfCaretAnnotation(new Rectangle(2, 2, 100, 100));
        PdfStructElem parent = new PdfStructElem(null, PdfName.Annot);
        AssertUtil.doesNotThrow(() -> {
            PdfUA2AnnotationChecker.checkAnnotation(annotation.getPdfObject(), parent);
        });
    }

    @Test
    public void basicInkAnnotationThrowsOnNoContents() {
        PdfInkAnnotation annotation = new PdfInkAnnotation(new Rectangle(2, 2, 100, 100));
        PdfStructElem parent = new PdfStructElem(null, PdfName.Annot);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            PdfUA2AnnotationChecker.checkAnnotation(annotation.getPdfObject(), parent);
        });
        Assertions.assertEquals(PdfUAExceptionMessageConstants.ANNOT_CONTENTS_IS_NULL_OR_EMPTY, e.getMessage());
    }

    @Test
    public void basicInkAnnotationThrowsOnEmptyContents() {
        PdfInkAnnotation annotation = new PdfInkAnnotation(new Rectangle(2, 2, 100, 100));
        PdfStructElem parent = new PdfStructElem(null, PdfName.Annot);
        annotation.setContents("");
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            PdfUA2AnnotationChecker.checkAnnotation(annotation.getPdfObject(), parent);
        });
        Assertions.assertEquals(PdfUAExceptionMessageConstants.ANNOT_CONTENTS_IS_NULL_OR_EMPTY, e.getMessage());
    }

    @Test
    public void basicInkAnnotation() {
        PdfInkAnnotation annotation = new PdfInkAnnotation(new Rectangle(2, 2, 100, 100));
        annotation.setContents("Test");
        PdfStructElem parent = new PdfStructElem(null, PdfName.Annot);
        AssertUtil.doesNotThrow(() -> {
            PdfUA2AnnotationChecker.checkAnnotation(annotation.getPdfObject(), parent);
        });
    }

    @Test
    public void basicPopup() {
        PdfPopupAnnotation annotation = new PdfPopupAnnotation(new Rectangle(2, 2, 100, 100));
        PdfStructElem parent = new PdfStructElem(null, PdfName.Annot);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            PdfUA2AnnotationChecker.checkAnnotation(annotation.getPdfObject(), parent);
        });
        Assertions.assertEquals(PdfUAExceptionMessageConstants.POPUP_ANNOTATIONS_ARE_NOT_ALLOWED, e.getMessage());
    }

    @Test
    public void basicFileAttachment() {
        PdfFileAttachmentAnnotation annotation = new PdfFileAttachmentAnnotation(new Rectangle(2, 2, 100, 100));
        PdfStructElem parent = new PdfStructElem(null, PdfName.Annot);
        AssertUtil.doesNotThrow(() -> {
            PdfUA2AnnotationChecker.checkAnnotation(annotation.getPdfObject(), parent);
        });
    }

    @Test
    public void basicSoundNotAllowed() {
        PdfDictionary annotation = new PdfDictionary();
        annotation.put(PdfName.Subtype, PdfName.Sound);
        PdfAnnotation annot = PdfAnnotation.makeAnnotation(annotation);
        PdfStructElem parent = new PdfStructElem(null, PdfName.Annot);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            PdfUA2AnnotationChecker.checkAnnotation(annot.getPdfObject(), parent);
        });
        Assertions.assertEquals(MessageFormatUtil.format(
                        PdfUAExceptionMessageConstants.DEPRECATED_ANNOTATIONS_ARE_NOT_ALLOWED, PdfName.Sound.getValue()),
                e.getMessage());
    }

    @Test
    public void basicMovieNotAllowed() {
        PdfDictionary annotation = new PdfDictionary();
        annotation.put(PdfName.Subtype, PdfName.Movie);
        PdfAnnotation annot = PdfAnnotation.makeAnnotation(annotation);
        PdfStructElem parent = new PdfStructElem(null, PdfName.Annot);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            PdfUA2AnnotationChecker.checkAnnotation(annot.getPdfObject(), parent);
        });
        Assertions.assertEquals(MessageFormatUtil.format(
                        PdfUAExceptionMessageConstants.DEPRECATED_ANNOTATIONS_ARE_NOT_ALLOWED, PdfName.Movie.getValue()),
                e.getMessage());

    }

    @Test
    public void basicPrinterMark() {
        PdfDictionary annotation = new PdfDictionary();
        annotation.put(PdfName.Subtype, PdfName.PrinterMark);
        PdfAnnotation annot = PdfAnnotation.makeAnnotation(annotation);
        PdfStructElem parent = new PdfStructElem(null, PdfName.Annot);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            PdfUA2AnnotationChecker.checkAnnotation(annot.getPdfObject(), parent);
        });
        Assertions.assertEquals(PdfUAExceptionMessageConstants.PRINTER_MARK_SHALL_BE_AN_ARTIFACT, e.getMessage());
    }

    @Test
    public void basicTrapNet() {
        PdfDictionary annotation = new PdfDictionary();
        annotation.put(PdfName.Subtype, PdfName.TrapNet);
        PdfAnnotation annot = PdfAnnotation.makeAnnotation(annotation);
        PdfStructElem parent = new PdfStructElem(null, PdfName.Annot);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> {
            PdfUA2AnnotationChecker.checkAnnotation(annot.getPdfObject(), parent);
        });
        Assertions.assertEquals(MessageFormatUtil.format(
                        PdfUAExceptionMessageConstants.DEPRECATED_ANNOTATIONS_ARE_NOT_ALLOWED, PdfName.TrapNet.getValue()),
                e.getMessage());
    }

    @Test
    public void basicWatermark() {
        PdfWatermarkAnnotation annot = new PdfWatermarkAnnotation(new Rectangle(0, 0, 100, 100));
        PdfStructElem parent = new PdfStructElem(null, PdfName.Annot);
        AssertUtil.doesNotThrow(() -> {
            PdfUA2AnnotationChecker.checkAnnotation(annot.getPdfObject(), parent);
        });
    }

    @Test
    public void basicRedaction() {
        PdfRedactAnnotation annot = new PdfRedactAnnotation(new Rectangle(0, 0, 100, 100));
        PdfStructElem parent = new PdfStructElem(null, PdfName.Annot);
        AssertUtil.doesNotThrow(() -> {
            PdfUA2AnnotationChecker.checkAnnotation(annot.getPdfObject(), parent);
        });
    }

    @Test
    public void basicProjection() {
        PdfDictionary annotation = new PdfDictionary();
        annotation.put(PdfName.Subtype, PdfName.Projection);
        PdfAnnotation annot = PdfAnnotation.makeAnnotation(annotation);
        PdfStructElem parent = new PdfStructElem(null, PdfName.Annot);
        AssertUtil.doesNotThrow(() -> {
            PdfUA2AnnotationChecker.checkAnnotation(annot.getPdfObject(), parent);
        });
    }

    @Test
    public void pdfUAWithEmbeddedFilesWithoutAFRTest() {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfUADocument doc = new PdfUADocument(writer, new PdfUAConfig(PdfUAConformance.PDF_UA_2, "hello", "en-US"));
        doc.addNewPage();

        PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(
                doc, "file".getBytes(), "description", "file.txt", null, null, null);
        PdfDictionary fsDict = (PdfDictionary) fs.getPdfObject();
        fsDict.remove(PdfName.AFRelationship);

        PdfFileAttachmentAnnotation annotation = new PdfFileAttachmentAnnotation(new Rectangle(2, 2, 100, 100), fs);
        doc.getPage(1).addAnnotation(annotation);

        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfUAExceptionMessageConstants.FILE_SPEC_SHALL_CONTAIN_AFRELATIONSHIP, e.getMessage());
    }
}
