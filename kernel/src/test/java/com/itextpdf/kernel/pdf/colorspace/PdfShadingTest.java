/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfNumberTest;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs.Rgb;
import com.itextpdf.kernel.pdf.colorspace.PdfShading.Axial;
import com.itextpdf.kernel.pdf.colorspace.PdfShading.Radial;
import com.itextpdf.kernel.pdf.colorspace.PdfShading.ShadingType;
import com.itextpdf.kernel.pdf.function.PdfFunction;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfShadingTest extends ExtendedITextTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void axialShadingConstructorNullExtendArgumentTest() {
        boolean[] extendArray = null;

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("extend");
        Axial axial = new Axial(
                new Rgb(), 0f, 0f, new float[] {0f, 0f, 0f}, 0.5f, 0.5f, new float[]{0.5f, 0.5f, 0.5f},
                extendArray
        );
    }

    @Test
    public void axialShadingConstructorInvalidExtendArgumentTest() {
        boolean[] extendArray = new boolean[] {true};

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("extend");
        Axial axial = new Axial(
                new Rgb(), 0f, 0f, new float[] {0f, 0f, 0f}, 0.5f, 0.5f, new float[]{0.5f, 0.5f, 0.5f},
                extendArray
        );
    }

    @Test
    public void radialShadingConstructorNullExtendArgumentTest() {
        boolean[] extendArray = null;

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("extend");
        new Radial(
                new Rgb(), 0f, 0f, 0f, new float[] {0f, 0f, 0f}, 0.5f, 0.5f, 10f, new float[]{0.5f, 0.5f, 0.5f},
                extendArray
        );
    }

    @Test
    public void radialShadingConstructorInvalidExtendArgumentTest() {
        boolean[] extendArray = new boolean[] {true, false, false};

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("extend");
        new Radial(
                new Rgb(), 0f, 0f, 0f, new float[] {0f, 0f, 0f}, 0.5f, 0.5f, 10f, new float[]{0.5f, 0.5f, 0.5f},
                extendArray
        );
    }

    @Test
    public void axialShadingGettersTest() {
        float[] coordsArray = {0f, 0f, 0.5f, 0.5f};
        float[] domainArray = {0f, 0.8f};
        boolean[] extendArray = {true, false};

        PdfDictionary axialShadingDictionary = initShadingDictionary(coordsArray, domainArray, extendArray,
                ShadingType.AXIAL);

        Axial axial = new Axial(axialShadingDictionary);
        Assert.assertArrayEquals(coordsArray, axial.getCoords().toFloatArray(), 0f);
        Assert.assertArrayEquals(domainArray, axial.getDomain().toFloatArray(), 0f);
        Assert.assertArrayEquals(extendArray, axial.getExtend().toBooleanArray());
        Assert.assertEquals(ShadingType.AXIAL, axial.getShadingType());
    }

    @Test
    public void axialShadingGettersWithDomainExtendDefaultValuesTest() {
        float[] coordsArray = {0f, 0f, 0.5f, 0.5f};
        float[] defaultDomainArray = {0f, 1f};
        boolean[] defaultExtendArray = {false, false};

        PdfDictionary axialShadingDictionary = initShadingDictionary(coordsArray, null, null,
                ShadingType.AXIAL);

        Axial axial = new Axial(axialShadingDictionary);
        Assert.assertArrayEquals(coordsArray, axial.getCoords().toFloatArray(), 0f);
        Assert.assertArrayEquals(defaultDomainArray, axial.getDomain().toFloatArray(), 0f);
        Assert.assertArrayEquals(defaultExtendArray, axial.getExtend().toBooleanArray());
        Assert.assertEquals(ShadingType.AXIAL, axial.getShadingType());
    }

    @Test
    public void radialShadingGettersTest() {
        float[] coordsArray = {0f, 0f, 0f, 0.5f, 0.5f, 10f};
        float[] domainArray = {0f, 0.8f};
        boolean[] extendArray = {true, false};

        PdfDictionary radialShadingDictionary = initShadingDictionary(coordsArray, domainArray, extendArray,
                ShadingType.RADIAL);

        Radial radial = new Radial(radialShadingDictionary);
        Assert.assertArrayEquals(coordsArray, radial.getCoords().toFloatArray(), 0f);
        Assert.assertArrayEquals(domainArray, radial.getDomain().toFloatArray(), 0f);
        Assert.assertArrayEquals(extendArray, radial.getExtend().toBooleanArray());
        Assert.assertEquals(ShadingType.RADIAL, radial.getShadingType());
    }

    @Test
    public void radialShadingGettersWithDomainExtendDefaultValuesTest() {
        float[] coordsArray = {0f, 0f, 0f, 0.5f, 0.5f, 10f};
        float[] defaultDomainArray = {0f, 1f};
        boolean[] defaultExtendArray = {false, false};

        PdfDictionary radialShadingDictionary = initShadingDictionary(coordsArray, null, null,
                ShadingType.RADIAL);

        Radial radial = new Radial(radialShadingDictionary);
        Assert.assertArrayEquals(coordsArray, radial.getCoords().toFloatArray(), 0f);
        Assert.assertArrayEquals(defaultDomainArray, radial.getDomain().toFloatArray(), 0f);
        Assert.assertArrayEquals(defaultExtendArray, radial.getExtend().toBooleanArray());
        Assert.assertEquals(ShadingType.RADIAL, radial.getShadingType());
    }

    private static PdfDictionary initShadingDictionary(float[] coordsArray, float[] domainArray, boolean[] extendArray,
            int radial2) {
        PdfDictionary axialShadingDictionary = new PdfDictionary();
        axialShadingDictionary.put(PdfName.ColorSpace, PdfName.DeviceRGB);
        axialShadingDictionary.put(PdfName.Coords, new PdfArray(coordsArray));
        if (domainArray != null) {
            axialShadingDictionary.put(PdfName.Domain, new PdfArray(domainArray));
        }
        if (extendArray != null) {
            axialShadingDictionary.put(PdfName.Extend, new PdfArray(extendArray));
        }
        axialShadingDictionary.put(PdfName.ShadingType, new PdfNumber(radial2));
        PdfDictionary functionDictionary = new PdfDictionary();
        functionDictionary.put(PdfName.C0, new PdfArray(new float[] {0f, 0f, 0f}));
        functionDictionary.put(PdfName.C1, new PdfArray(new float[] {0.5f, 0.5f, 0.5f}));
        functionDictionary.put(PdfName.Domain, new PdfArray(new float[] {0f, 1f}));
        functionDictionary.put(PdfName.FunctionType, new PdfNumber(2));
        functionDictionary.put(PdfName.N, new PdfNumber(1));
        axialShadingDictionary.put(PdfName.Function, functionDictionary);
        return axialShadingDictionary;
    }
}
