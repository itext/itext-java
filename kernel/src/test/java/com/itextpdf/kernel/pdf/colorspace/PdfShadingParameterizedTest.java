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
package com.itextpdf.kernel.pdf.colorspace;

import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.colorspace.shading.AbstractPdfShading;
import com.itextpdf.test.ExtendedITextTest;

import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("UnitTest")
public class PdfShadingParameterizedTest extends ExtendedITextTest {

    public static Iterable<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
                {"FreeFormGouraudShadedTriangleMesh", 4},
                {"LatticeFormGouraudShadedTriangleMesh", 5},
                {"CoonsPatchMesh", 6},
                {"TensorProductPatchMesh", 7}
        });

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("parameters")
    public void AllAboveType3FromDictionaryShouldFailTest(String shadingName, int shadingType) {
        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.ShadingType, new PdfNumber(shadingType));
        dict.put(PdfName.ColorSpace, PdfName.DeviceRGB);
        Exception e = Assertions.assertThrows(PdfException.class, () -> AbstractPdfShading.makeShading(dict),
                "Creating " + shadingName + " should throw PdfException.");

        Assertions.assertEquals(KernelExceptionMessageConstant.UNEXPECTED_SHADING_TYPE, e.getMessage());
    }
}
