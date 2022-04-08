package com.itextpdf.kernel.pdf.colorspace;

import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@Category(UnitTest.class)
@RunWith(Parameterized.class)
public class PdfShadingParameterizedTest extends ExtendedITextTest {

    private String shadingName;
    private int shadingType;

    public PdfShadingParameterizedTest(Object name, Object type)
    {
        shadingName = (String) name;
        shadingType = (int) type;
    }

    @Parameterized.Parameters (name = "{0}")
    public static Iterable<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
                {"FreeFormGouraudShadedTriangleMesh", 4},
                {"LatticeFormGouraudShadedTriangleMesh", 5},
                {"CoonsPatchMesh", 6},
                {"TensorProductPatchMesh", 7}
        });

    }

    @Test
    public void AllAboveType3FromDictionaryShouldFailTest() {
        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.ShadingType, new PdfNumber(shadingType));
        dict.put(PdfName.ColorSpace, PdfName.DeviceRGB);
        Exception e = Assert.assertThrows("Creating " + shadingName + " should throw PdfException.", PdfException.class, () -> PdfShading.makeShading(dict));

        Assert.assertEquals(KernelExceptionMessageConstant.UNEXPECTED_SHADING_TYPE, e.getMessage());
    }
}
