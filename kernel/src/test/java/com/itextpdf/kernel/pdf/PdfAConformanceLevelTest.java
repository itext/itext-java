package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.impl.XMPMetaImpl;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfAConformanceLevelTest extends ExtendedITextTest {
    @Test
    public void getConformanceTest() {
        PdfAConformanceLevel level = PdfAConformanceLevel.getConformanceLevel("4", null);
        Assert.assertEquals(PdfAConformanceLevel.PDF_A_4, level);
    }

    @Test
    public void getXmpConformanceNullTest() throws XMPException {
        XMPMeta meta = new XMPMetaImpl();
        meta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, "4");
        PdfAConformanceLevel level = PdfAConformanceLevel.getConformanceLevel(meta);
        Assert.assertEquals(PdfAConformanceLevel.PDF_A_4, level);
    }

    @Test
    public void getXmpConformanceBTest() throws XMPException {
        XMPMeta meta = new XMPMetaImpl();
        meta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, "2");
        meta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.CONFORMANCE, "B");
        PdfAConformanceLevel level = PdfAConformanceLevel.getConformanceLevel(meta);
        Assert.assertEquals(PdfAConformanceLevel.PDF_A_2B, level);
    }
}
