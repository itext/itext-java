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
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs.Rgb;
import com.itextpdf.kernel.pdf.colorspace.PdfShading.Axial;
import com.itextpdf.kernel.pdf.colorspace.PdfShading.CoonsPatchMesh;
import com.itextpdf.kernel.pdf.colorspace.PdfShading.FreeFormGouraudShadedTriangleMesh;
import com.itextpdf.kernel.pdf.colorspace.PdfShading.FunctionBased;
import com.itextpdf.kernel.pdf.colorspace.PdfShading.LatticeFormGouraudShadedTriangleMesh;
import com.itextpdf.kernel.pdf.colorspace.PdfShading.Radial;
import com.itextpdf.kernel.pdf.colorspace.PdfShading.ShadingType;
import com.itextpdf.kernel.pdf.colorspace.PdfShading.TensorProductPatchMesh;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs.Pattern;
import com.itextpdf.kernel.pdf.function.IPdfFunction;
import com.itextpdf.kernel.pdf.function.PdfType4Function;
import com.itextpdf.test.ExtendedITextTest;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfShadingTest extends ExtendedITextTest {

    @Test
    public void axialShadingConstructorNullExtendArgumentTest() {
        boolean[] extendArray = null;
        Rgb color = new Rgb();

        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Axial(color, 0f, 0f, new float[] {0f, 0f, 0f}, 0.5f, 0.5f, new float[] {0.5f, 0.5f, 0.5f},
                        extendArray));
        Assertions.assertEquals("extend", e.getMessage());
    }

    @Test
    public void axialShadingConstructorInvalidExtendArgumentTest() {
        boolean[] extendArray = new boolean[] {true};
        Rgb color = new Rgb();

        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Axial(color, 0f, 0f, new float[] {0f, 0f, 0f}, 0.5f, 0.5f, new float[] {0.5f, 0.5f, 0.5f},
                        extendArray));
        Assertions.assertEquals("extend", e.getMessage());
    }

    @Test
    public void radialShadingConstructorNullExtendArgumentTest() {
        boolean[] extendArray = null;
        Rgb color = new Rgb();

        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Radial(color, 0f, 0f, 0f, new float[] {0f, 0f, 0f}, 0.5f, 0.5f, 10f,
                        new float[] {0.5f, 0.5f, 0.5f}, extendArray));
        Assertions.assertEquals("extend", e.getMessage());
    }

    @Test
    public void radialShadingConstructorInvalidExtendArgumentTest() {
        boolean[] extendArray = new boolean[] {true, false, false};
        Rgb color = new Rgb();

        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Radial(color, 0f, 0f, 0f, new float[] {0f, 0f, 0f}, 0.5f, 0.5f, 10f,
                        new float[] {0.5f, 0.5f, 0.5f}, extendArray));
        Assertions.assertEquals("extend", e.getMessage());
    }

    @Test
    public void axialShadingGettersTest() {
        float[] coordsArray = {0f, 0f, 0.5f, 0.5f};
        float[] domainArray = {0f, 0.8f};
        boolean[] extendArray = {true, false};

        PdfDictionary axialShadingDictionary = initShadingDictionary(coordsArray, domainArray, extendArray,
                ShadingType.AXIAL);

        Axial axial = new Axial(axialShadingDictionary);
        Assertions.assertArrayEquals(coordsArray, axial.getCoords().toFloatArray(), 0f);
        Assertions.assertArrayEquals(domainArray, axial.getDomain().toFloatArray(), 0f);
        Assertions.assertArrayEquals(extendArray, axial.getExtend().toBooleanArray());
        Assertions.assertEquals(ShadingType.AXIAL, axial.getShadingType());
        Assertions.assertEquals(PdfName.DeviceRGB, axial.getColorSpace());
    }

    @Test
    public void setFunctionsTest() {
        float[] coordsArray = {0f, 0f, 0.5f, 0.5f};
        float[] domainArray = {0f, 0.8f};
        boolean[] extendArray = {true, false};

        PdfDictionary axialShadingDictionary = initShadingDictionary(coordsArray, domainArray, extendArray, ShadingType.AXIAL);

        Axial axial = new Axial(axialShadingDictionary);
        Assertions.assertTrue(axial.getFunction() instanceof PdfDictionary);

        byte[] ps = "{2 copy sin abs sin abs 3 index 10 mul sin  1 sub abs}".getBytes(StandardCharsets.ISO_8859_1);
        float[] domain = new float[] {0, 1000, 0, 1000};
        float[] range = new float[] {0, 1, 0, 1, 0, 1};
        IPdfFunction[] functions = new IPdfFunction[] {new PdfType4Function(domain, range, ps)};

        axial.setFunction(functions);
        final PdfObject funcObj = axial.getFunction();
        Assertions.assertTrue(funcObj instanceof PdfArray);
        Assertions.assertEquals(1, ((PdfArray) funcObj).size());
        Assertions.assertEquals(functions[0].getAsPdfObject(), ((PdfArray) funcObj).get(0));
    }

    @Test
    public void axialShadingViaPdfObjectTest() {
        float[] coordsArray = {0f, 0f, 0.5f, 0.5f};
        float[] domainArray = {0f, 0.8f};
        boolean[] extendArray = {true, false};

        PdfDictionary axialShadingDictionary = initShadingDictionary(coordsArray, domainArray, extendArray,
                ShadingType.AXIAL);

        Axial axial = (Axial) PdfShading.makeShading(axialShadingDictionary);

        Assertions.assertArrayEquals(coordsArray, axial.getCoords().toFloatArray(), 0f);
        Assertions.assertArrayEquals(domainArray, axial.getDomain().toFloatArray(), 0f);
        Assertions.assertArrayEquals(extendArray, axial.getExtend().toBooleanArray());
        Assertions.assertEquals(ShadingType.AXIAL, axial.getShadingType());
    }

    @Test
    public void axialShadingGettersWithDomainExtendDefaultValuesTest() {
        float[] coordsArray = {0f, 0f, 0.5f, 0.5f};
        float[] defaultDomainArray = {0f, 1f};
        boolean[] defaultExtendArray = {false, false};

        PdfDictionary axialShadingDictionary = initShadingDictionary(coordsArray, null, null, ShadingType.AXIAL);

        Axial axial = new Axial(axialShadingDictionary);
        Assertions.assertArrayEquals(coordsArray, axial.getCoords().toFloatArray(), 0f);
        Assertions.assertArrayEquals(defaultDomainArray, axial.getDomain().toFloatArray(), 0f);
        Assertions.assertArrayEquals(defaultExtendArray, axial.getExtend().toBooleanArray());
        Assertions.assertEquals(ShadingType.AXIAL, axial.getShadingType());
    }

    @Test
    public void radialShadingGettersTest() {
        float[] coordsArray = {0f, 0f, 0f, 0.5f, 0.5f, 10f};
        float[] domainArray = {0f, 0.8f};
        boolean[] extendArray = {true, false};

        PdfDictionary radialShadingDictionary = initShadingDictionary(coordsArray, domainArray, extendArray,
                ShadingType.RADIAL);

        Radial radial = new Radial(radialShadingDictionary);
        Assertions.assertArrayEquals(coordsArray, radial.getCoords().toFloatArray(), 0f);
        Assertions.assertArrayEquals(domainArray, radial.getDomain().toFloatArray(), 0f);
        Assertions.assertArrayEquals(extendArray, radial.getExtend().toBooleanArray());
        Assertions.assertEquals(ShadingType.RADIAL, radial.getShadingType());
    }

    @Test
    public void radialShadingViaMakeShadingTest() {
        float[] coordsArray = {0f, 0f, 0f, 0.5f, 0.5f, 10f};
        float[] domainArray = {0f, 0.8f};
        boolean[] extendArray = {true, false};

        PdfDictionary radialShadingDictionary = initShadingDictionary(coordsArray, domainArray, extendArray,
                ShadingType.RADIAL);

        Radial radial = (Radial) PdfShading.makeShading(radialShadingDictionary);
        Assertions.assertArrayEquals(coordsArray, radial.getCoords().toFloatArray(), 0f);
        Assertions.assertArrayEquals(domainArray, radial.getDomain().toFloatArray(), 0f);
        Assertions.assertArrayEquals(extendArray, radial.getExtend().toBooleanArray());
        Assertions.assertEquals(ShadingType.RADIAL, radial.getShadingType());
    }

    @Test
    public void radialShadingGettersWithDomainExtendDefaultValuesTest() {
        float[] coordsArray = {0f, 0f, 0f, 0.5f, 0.5f, 10f};
        float[] defaultDomainArray = {0f, 1f};
        boolean[] defaultExtendArray = {false, false};

        PdfDictionary radialShadingDictionary = initShadingDictionary(coordsArray, null, null, ShadingType.RADIAL);

        Radial radial = new Radial(radialShadingDictionary);
        Assertions.assertArrayEquals(coordsArray, radial.getCoords().toFloatArray(), 0f);
        Assertions.assertArrayEquals(defaultDomainArray, radial.getDomain().toFloatArray(), 0f);
        Assertions.assertArrayEquals(defaultExtendArray, radial.getExtend().toBooleanArray());
        Assertions.assertEquals(ShadingType.RADIAL, radial.getShadingType());
    }

    @Test
    public void makeShadingShouldFailOnMissingShadeType() {
        PdfDictionary shade = new PdfDictionary();
        shade.put(PdfName.ColorSpace, new PdfArray());
        Exception error = Assertions.assertThrows(PdfException.class, () -> PdfShading.makeShading(shade));
        Assertions.assertEquals(KernelExceptionMessageConstant.SHADING_TYPE_NOT_FOUND, error.getMessage());
    }

    @Test
    public void makeShadingShouldFailOnMissingColorSpace() {
        PdfDictionary shade = new PdfDictionary();
        shade.put(PdfName.ShadingType, new PdfArray());
        Exception error = Assertions.assertThrows(PdfException.class, () -> PdfShading.makeShading(shade));
        Assertions.assertEquals(KernelExceptionMessageConstant.COLOR_SPACE_NOT_FOUND, error.getMessage());
    }

    @Test
    public void usingPatternColorSpaceThrowsException() {
        byte[] ps = "{2 copy sin abs sin abs 3 index 10 mul sin  1 sub abs}".getBytes(StandardCharsets.ISO_8859_1);
        IPdfFunction function = new PdfType4Function(new float[] {0, 1000, 0, 1000},
                new float[] {0, 1, 0, 1, 0, 1}, ps);

        Pattern colorSpace = new Pattern();
        Exception ex = Assertions.assertThrows(IllegalArgumentException.class,
                () -> new FunctionBased(colorSpace, function));

        Assertions.assertEquals("colorSpace", ex.getMessage());
    }

    @Test
    public void makeShadingFunctionBased1Test() {
        byte[] ps = "{2 copy sin abs sin abs 3 index 10 mul sin  1 sub abs}".getBytes(StandardCharsets.ISO_8859_1);
        float[] domain = new float[] {0, 1000, 0, 1000};
        float[] range = new float[] {0, 1, 0, 1, 0, 1};
        IPdfFunction function = new PdfType4Function(domain,
                range, ps);

        FunctionBased shade = new FunctionBased(new PdfDeviceCs.Rgb(), function);

        PdfDictionary object = shade.getPdfObject();
        Assertions.assertEquals(1, object.getAsInt(PdfName.ShadingType).intValue());
        Assertions.assertEquals(PdfName.DeviceRGB, object.getAsName(PdfName.ColorSpace));
        PdfStream functionStream = object.getAsStream(PdfName.Function);

        PdfArray functionDomain = functionStream.getAsArray(PdfName.Domain);
        Assertions.assertArrayEquals(domain, functionDomain.toFloatArray(), 0.0f);

        PdfArray functionRange = functionStream.getAsArray(PdfName.Range);
        Assertions.assertArrayEquals(range, functionRange.toFloatArray(), 0.0f);
        Assertions.assertEquals(4, functionStream.getAsInt(PdfName.FunctionType).intValue());
    }

    @Test
    public void makeShadingFunctionBased2Test() {
        byte[] ps = "{2 copy sin abs sin abs 3 index 10 mul sin  1 sub abs}".getBytes(StandardCharsets.ISO_8859_1);
        PdfArray domain = new PdfArray(new float[] {0, 1000, 0, 1000});
        PdfArray range = new PdfArray(new float[] {0, 1, 0, 1, 0, 1});
        PdfDictionary shadingDict = new PdfDictionary();
        shadingDict.put(PdfName.ShadingType, new PdfNumber(1));
        shadingDict.put(PdfName.ColorSpace, PdfName.DeviceRGB);
        PdfStream stream = new PdfStream();
        stream.put(PdfName.Domain, domain);
        stream.put(PdfName.Range, range);
        stream.put(PdfName.FunctionType, new PdfNumber(4));
        shadingDict.put(PdfName.Function, stream);

        stream.setData(ps);

        shadingDict.put(PdfName.Function, stream);

        PdfShading shade = PdfShading.makeShading(shadingDict);

        PdfDictionary object = shade.getPdfObject();
        Assertions.assertEquals(1, object.getAsInt(PdfName.ShadingType).intValue());
        Assertions.assertEquals(PdfName.DeviceRGB, object.getAsName(PdfName.ColorSpace));
        PdfStream functionStream = object.getAsStream(PdfName.Function);

        PdfArray functionDomain = functionStream.getAsArray(PdfName.Domain);
        Assertions.assertArrayEquals(domain.toDoubleArray(), functionDomain.toDoubleArray(), 0.0);

        PdfArray functionRange = functionStream.getAsArray(PdfName.Range);
        Assertions.assertArrayEquals(range.toDoubleArray(), functionRange.toDoubleArray(), 0.0);

        Assertions.assertEquals(4, functionStream.getAsInt(PdfName.FunctionType).intValue());

        Assertions.assertEquals(functionStream, shade.getFunction());
    }

    @Test
    public void makeShadingWithInvalidShadeType() {
        float[] coordsArray = {0f, 0f, 0f, 0.5f, 0.5f, 10f};

        PdfDictionary radialShadingDictionary = initShadingDictionary(coordsArray, null, null, 21);

        Exception e = Assertions.assertThrows(PdfException.class, () -> PdfShading.makeShading(radialShadingDictionary));
        Assertions.assertEquals(KernelExceptionMessageConstant.UNEXPECTED_SHADING_TYPE, e.getMessage());
    }

    @Test
    public void makeFreeFormGouraudShadedTriangleMeshTest() {
        int x = 36;
        int y = 400;

        // Side of an equilateral triangle
        int side = 500;

        byte[] data = toMultiWidthBytes(new int[] {1, 4, 4, 1, 1, 1}, 0, 0, 0, 250, 0, 0, 0, side, 0, 0, 250, 0, 0,
                side / 4, (int) (y - (side * Math.sin(Math.PI / 3))), 0, 0, 250);

        PdfStream stream = new PdfStream(data, CompressionConstants.DEFAULT_COMPRESSION);
        stream.put(PdfName.ColorSpace, PdfName.DeviceRGB);
        stream.put(PdfName.ShadingType, new PdfNumber(4));
        stream.put(PdfName.BitsPerCoordinate, new PdfNumber(32));
        stream.put(PdfName.BitsPerComponent, new PdfNumber(8));
        stream.put(PdfName.BitsPerFlag, new PdfNumber(8));
        stream.put(PdfName.Decode,
                new PdfArray(new float[] {x, x + side, y, y + (int) (side * Math.sin(Math.PI / 3)), 0, 1, 0, 1, 0, 1}));
        stream.put(PdfName.Matrix, new PdfArray(new float[] {1, 0, 0, -1, 0, 0}));

        FreeFormGouraudShadedTriangleMesh shade = (FreeFormGouraudShadedTriangleMesh) PdfShading.makeShading(stream);

        Assertions.assertEquals(PdfName.DeviceRGB, shade.getColorSpace());
        Assertions.assertEquals(4, shade.getShadingType());
        Assertions.assertEquals(32, shade.getBitsPerCoordinate());
        Assertions.assertEquals(8, shade.getBitsPerComponent());
        Assertions.assertEquals(8, shade.getBitsPerFlag());
        Assertions.assertEquals(y, shade.getDecode().getAsNumber(2).intValue());
    }

    @Test
    public void makeLatticeFormGouraudShadedTriangleMeshTest() {
        int x = 36;
        int y = 400;

        // Side of an equilateral triangle
        int side = 500;

        byte[] data = toMultiWidthBytes(new int[] {4, 4, 1, 1, 1}, 500, 0, 250, 0, 0, 500, 500, 0, 250, 0, 0, 0, 0, 0,
                250, 0, 500, 250, 0, 0);
        PdfStream stream = new PdfStream(data, CompressionConstants.DEFAULT_COMPRESSION);
        stream.put(PdfName.ColorSpace, PdfName.DeviceRGB);
        stream.put(PdfName.ShadingType, new PdfNumber(5));
        stream.put(PdfName.BitsPerCoordinate, new PdfNumber(32));
        stream.put(PdfName.BitsPerComponent, new PdfNumber(8));
        stream.put(PdfName.VerticesPerRow, new PdfNumber(2));
        stream.put(PdfName.Decode,
                new PdfArray(new float[] {x, x + side, y, y + (int) (side * Math.sin(Math.PI / 3)), 0, 1, 0, 1, 0, 1}));
        stream.put(PdfName.Matrix, new PdfArray(new float[] {1, 0, 0, -1, 0, 0}));

        LatticeFormGouraudShadedTriangleMesh shade = (LatticeFormGouraudShadedTriangleMesh) PdfShading.makeShading(
                stream);

        Assertions.assertEquals(PdfName.DeviceRGB, shade.getColorSpace());
        Assertions.assertEquals(5, shade.getShadingType());
        Assertions.assertEquals(32, shade.getBitsPerCoordinate());
        Assertions.assertEquals(8, shade.getBitsPerComponent());
        Assertions.assertEquals(2, shade.getVerticesPerRow());
        Assertions.assertEquals(y, shade.getDecode().getAsNumber(2).intValue());
    }

    @Test
    public void coonsPatchMeshGradientTest() {

        int x = 36;
        int y = 400;

        // Side of an equilateral triangle
        int side = 500;
        PdfStream stream = new PdfStream(CompressionConstants.DEFAULT_COMPRESSION);
        stream.setData(toMultiWidthBytes(
                new int[] {1, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 1, 1, 1, 1, 1, 1,
                        1, 1, 1, 1, 1, 1}, 0, //flag
                0, 0,  //p1
                0, 0,  //p2 cp 1 o
                0, 100, //p3 cp 4 i
                0, 100, //p4
                0, 100, //p5 cp4 o
                100, 100, //p6 cp 7 i
                100, 100, // p7
                100, 100, // p8 cp 7 o
                110, 10, //p9 cp 10 i
                100, 0, // p10
                100, 0, // p11 cp 10 o
                0, 0, // p12 cp 1 i
                250, 0, 0, // c p1
                0, 250, 0, // c p4
                0, 0, 250, // c p7
                250, 250, 250)); // c p10
        stream.setData(
                toMultiWidthBytes(new int[] {1, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 1, 1, 1, 1, 1, 1}, 2,
                        //flag
                        200, 0, //p17 cp 18 i
                        200, 0, // p18
                        200, 0, // p19 cp 18 o
                        200, 100, // p20 cp 10 i
                        200, 100, //p13 cp4 o
                        200, 100, //p14 cp 15 i
                        200, 100, // p15
                        200, 100, // p16 cp 15 o
                        250, 0, 0, // c p15
                        0, 250, 0  // c p18
                ), true); // c p10
        stream.put(PdfName.ColorSpace, PdfName.DeviceRGB);
        stream.put(PdfName.ShadingType, new PdfNumber(6));
        stream.put(PdfName.BitsPerCoordinate, new PdfNumber(32));
        stream.put(PdfName.BitsPerComponent, new PdfNumber(8));
        stream.put(PdfName.BitsPerFlag, new PdfNumber(8));
        stream.put(PdfName.Decode,
                new PdfArray(new float[] {x, x + side, y, y + (int) (side * Math.sin(Math.PI / 3)), 0, 1, 0, 1, 0, 1}));
        stream.put(PdfName.Matrix, new PdfArray(new float[] {1, 0, 0, -1, 0, 0}));

        CoonsPatchMesh shade = (CoonsPatchMesh) PdfShading.makeShading(stream);

        Assertions.assertEquals(PdfName.DeviceRGB, shade.getColorSpace());
        Assertions.assertEquals(6, shade.getShadingType());
        Assertions.assertEquals(32, shade.getBitsPerCoordinate());
        Assertions.assertEquals(8, shade.getBitsPerComponent());
        Assertions.assertEquals(8, shade.getBitsPerFlag());
        Assertions.assertEquals(y, shade.getDecode().getAsNumber(2).intValue());
    }

    @Test
    public void TensorProductPatchMeshShadingTest() {
        int x = 36;
        int y = 400;

        // Side of an equilateral triangle
        int side = 500;
        PdfStream stream = new PdfStream(CompressionConstants.DEFAULT_COMPRESSION);
        stream.setData(toMultiWidthBytes(
                new int[] {1, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
                        4, 4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, 0, //flag
                50, 0,// p00
                50, 0,// p01
                100, 0,// p02
                100, 0,// p03
                100, 0,// p13
                100, 100,// p23
                100, 100,// p33
                100, 100,// p32
                50, 100,// p31
                50, 100,// p30
                50, 100,// p20
                50, 0,// p10
                50, 0,// p11
                100, 0,// p12
                100, 100,// p22
                50, 100, // p21
                250, 0, 0, // c00
                0, 250, 0, // c03
                0, 0, 250, // c33
                250, 0, 250)); // c30
        stream.setData(toMultiWidthBytes(
                new int[] {1, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 1, 1, 1, 1, 1, 1},
                1, //flag
                100, 100,// p13
                150, 100,// p23
                150, 100,// p33
                150, 100,// p32
                150, 0,// p31
                150, 0,// p30
                150, 0,// p20
                100, 0,// p10
                100, 0,// p11
                100, 100,// p12
                150, 100,// p22
                150, 0,// p21

                250, 0, 0, // c p33
                0, 250, 250  // c p30
        ), true); // c p10

        stream.put(PdfName.ColorSpace, PdfName.DeviceRGB);
        stream.put(PdfName.ShadingType, new PdfNumber(7));
        stream.put(PdfName.BitsPerCoordinate, new PdfNumber(32));
        stream.put(PdfName.BitsPerComponent, new PdfNumber(8));
        stream.put(PdfName.BitsPerFlag, new PdfNumber(8));
        stream.put(PdfName.Decode,
                new PdfArray(new float[] {x, x + side, y, y + (int) (side * Math.sin(Math.PI / 3)), 0, 1, 0, 1, 0, 1}));
        stream.put(PdfName.Matrix, new PdfArray(new float[] {-1, 0, 0, 1, 0, 0}));

        TensorProductPatchMesh shade = (TensorProductPatchMesh) PdfShading.makeShading(stream);

        Assertions.assertEquals(PdfName.DeviceRGB, shade.getColorSpace());
        Assertions.assertEquals(7, shade.getShadingType());
        Assertions.assertEquals(32, shade.getBitsPerCoordinate());
        Assertions.assertEquals(8, shade.getBitsPerComponent());
        Assertions.assertEquals(8, shade.getBitsPerFlag());
        Assertions.assertEquals(y, shade.getDecode().getAsNumber(2).intValue());
    }

    @Test
    public void invalidShadingTypeShouldFailTest() {
        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.ShadingType, new PdfNumber(8));
        dict.put(PdfName.ColorSpace, PdfName.DeviceRGB);
        Exception e = Assertions.assertThrows(PdfException.class, () -> PdfShading.makeShading(dict));

        Assertions.assertEquals(KernelExceptionMessageConstant.UNEXPECTED_SHADING_TYPE, e.getMessage());
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

    /**
     * A helper function to create a mixed width byte array.
     *
     * <p>
     *
     * @param pattern the width pattern, each element represents the number of bytes that it will
     *                occupy in the resulting byte array
     * @param ints    the values to be converted
     *
     * @return a byte array where the ints are represented in widths represented by the pattern
     */
    private static byte[] toMultiWidthBytes(int[] pattern, int... ints) {
        if (ints.length % pattern.length != 0) {
            throw new IllegalArgumentException(
                    "The number of elements must be an exact multiple of" + " the pattern length");
        }
        int patternSize = 0;
        for (int i = 0; i < pattern.length; i++) {
            patternSize += pattern[i];
        }
        byte[] result = new byte[ints.length / pattern.length * patternSize];
        int targetSize;
        int ri = 0;
        for (int i = 0; i < ints.length; i++) {
            targetSize = pattern[i % pattern.length];
            for (int p = 0; p < targetSize; p++) {
                result[ri] = (byte) (ints[i] >> p * 8);
                ri++;
            }
        }
        return result;
    }
}
