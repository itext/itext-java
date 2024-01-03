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
package com.itextpdf.kernel.pdf.colorspace;

import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@Category(UnitTest.class)
@RunWith(Parameterized.class)
public class PdfShadingParameterizedTest extends ExtendedITextTest {

    private final String shadingName;
    private final int shadingType;

    public PdfShadingParameterizedTest(Object name, Object type) {
        shadingName = (String) name;
        shadingType = (int) type;
    }

    @Parameterized.Parameters(name = "{0}")
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
        Exception e = Assert.assertThrows("Creating " + shadingName + " should throw PdfException.", PdfException.class,
                () -> PdfShading.makeShading(dict));

        Assert.assertEquals(KernelExceptionMessageConstant.UNEXPECTED_SHADING_TYPE, e.getMessage());
    }
}
