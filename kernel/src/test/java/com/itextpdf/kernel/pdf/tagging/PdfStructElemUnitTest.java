package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.Pdf3DAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfStructElemUnitTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void noParentObjectTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException
                .expectMessage(KernelExceptionMessageConstant.STRUCTURE_ELEMENT_SHALL_CONTAIN_PARENT_OBJECT);

        PdfDictionary parent = new PdfDictionary();
        PdfArray kid = new PdfArray();
        PdfStructElem.addKidObject(parent, 1, kid);
    }

    @Test
    public void annotationHasNoReferenceToPageTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.ANNOTATION_SHALL_HAVE_REFERENCE_TO_PAGE);

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfName pdfName = new PdfName("test");
        PdfAnnotation annotation = new Pdf3DAnnotation(new Rectangle(100, 100), pdfName);
        PdfStructElem structElem = new PdfStructElem(pdfDoc, pdfName, annotation);
    }
}
