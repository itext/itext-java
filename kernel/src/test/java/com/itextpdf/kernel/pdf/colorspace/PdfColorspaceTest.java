package com.itextpdf.kernel.pdf.colorspace;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs.Indexed;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class PdfColorspaceTest extends ExtendedITextTest {

    @Test
    public void getColorspaceNameTest() {
        PdfArray indexedValues = new PdfArray();
        indexedValues.add(PdfName.Indexed);
        indexedValues.add(PdfName.DeviceGray);
        indexedValues.add(new PdfNumber(2));
        PdfString lookup = new PdfString(new byte[] {0x00, (byte) 0xff});
        lookup.setHexWriting(true);
        indexedValues.add(lookup);
        Indexed indexed = new Indexed(indexedValues);
        Assertions.assertEquals("Indexed", indexed.getColorspaceName().getValue());

        Assertions.assertEquals(TestColorSpace.class.getSimpleName(), new TestColorSpace().getColorspaceName().getValue());
    }

    @Test
    public void getNameTest() {
        PdfArray indexedValues = new PdfArray();
        indexedValues.add(PdfName.Indexed);
        indexedValues.add(PdfName.DeviceGray);
        indexedValues.add(new PdfNumber(2));
        PdfString lookup = new PdfString(new byte[] {0x00, (byte) 0xff});
        lookup.setHexWriting(true);
        indexedValues.add(lookup);
        Indexed indexed = new Indexed(indexedValues);
        Assertions.assertEquals("Indexed", indexed.getName().getValue());

        Assertions.assertEquals(TestColorSpace.class.getSimpleName(), new TestColorSpace().getName().getValue());
    }

    private static class TestColorSpace extends PdfColorSpace {
        protected TestColorSpace() {
            super(new PdfArray());
        }

        @Override
        public int getNumberOfComponents() {
            return 0;
        }

        @Override
        protected boolean isWrappedObjectMustBeIndirect() {
            return false;
        }
    }
}
