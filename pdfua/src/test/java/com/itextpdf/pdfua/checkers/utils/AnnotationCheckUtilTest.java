package com.itextpdf.pdfua.checkers.utils;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.tagging.PdfMcrNumber;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("UnitTest")
@Deprecated
public class AnnotationCheckUtilTest extends ExtendedITextTest {

    @Test
    public void testIsAnnotationVisible() {
        assertTrue(AnnotationCheckUtil.isAnnotationVisible(new PdfDictionary()));
    }

    @Test
    public void annotationHandler(){
        AnnotationCheckUtil.AnnotationHandler handler = new AnnotationCheckUtil.AnnotationHandler(new PdfUAValidationContext(null));
        assertNotNull(handler);
        assertFalse(handler.accept(null));
        assertTrue(handler.accept(new PdfMcrNumber(new PdfNumber(2), null)));
        AssertUtil.doesNotThrow(() -> handler.processElement(null));
    }
}