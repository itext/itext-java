package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfObjectWrapperUnitTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void directObjectsCouldntBeWrappedTest01() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException
                .expectMessage(KernelExceptionMessageConstant.OBJECT_MUST_BE_INDIRECT_TO_WORK_WITH_THIS_WRAPPER);

        PdfDictionary pdfDictionary = new PdfDictionary();
        Assert.assertTrue(null == pdfDictionary.getIndirectReference());
        // PdfCatalog is one of PdfObjectWrapper implementations. The issue could be reproduced on any of them, not only on this one
        PdfCatalog pdfCatalog = new PdfCatalog(pdfDictionary);
    }
}
