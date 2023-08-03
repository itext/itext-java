/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.kernel.colors;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ColorTest extends ExtendedITextTest {

    private static final float EPS = 1e-4f;

    @Test
    public void convertCmykToRgbTest() {
        DeviceCmyk cmyk = new DeviceCmyk(0, 0, 0, 0);
        DeviceRgb rgb = new DeviceRgb(255, 255, 255);

        Assert.assertArrayEquals(rgb.colorValue, Color.convertCmykToRgb(cmyk).colorValue, EPS);
    }

    @Test
    public void convertRgbToCmykTest() {
        DeviceCmyk cmyk = new DeviceCmyk(0, 0, 0, 0);
        DeviceRgb rgb = new DeviceRgb(255, 255, 255);

        Assert.assertArrayEquals(cmyk.colorValue, Color.convertRgbToCmyk(rgb).colorValue, EPS);
    }

    @Test
    public void setColorValueIncorrectComponentsNumberTest() {
        float[] colorValues = new float[]{0.0f, 0.5f, 0.1f};
        Color color = Color.makeColor(PdfColorSpace.makeColorSpace(PdfName.DeviceRGB), colorValues);

        Exception e = Assert.assertThrows(PdfException.class,
                () -> color.setColorValue(new float[]{0.1f, 0.2f})
        );
        Assert.assertEquals(KernelExceptionMessageConstant.INCORRECT_NUMBER_OF_COMPONENTS, e.getMessage());
    }

    @Test
    public void equalsAndHashCodeTest() {
        float[] colorValues = new float[]{0.0f, 0.5f, 0.1f};
        Color color1 = Color.makeColor(PdfColorSpace.makeColorSpace(PdfName.DeviceRGB), colorValues);
        Color color2 = Color.makeColor(PdfColorSpace.makeColorSpace(PdfName.DeviceRGB), colorValues);

        boolean result = color1.equals(color2);
        Assert.assertTrue(result);

        Assert.assertEquals(color1.hashCode(), color2.hashCode());
    }

    @Test
    public void equalsAndHashCodeNullColorSpacesTest() {
        float[] colorValues = new float[]{0.0f, 0.5f, 0.1f};
        Color color1 = new Color(null, colorValues);
        Color color2 = new Color(null, colorValues);

        boolean result = color1.equals(color2);
        Assert.assertTrue(result);

        Assert.assertEquals(color1.hashCode(), color2.hashCode());
    }

    @Test
    public void equalsAndHashCodeNullColorValuesTest() {
        Color color1 = new Color(PdfColorSpace.makeColorSpace(PdfName.DeviceRGB), null);
        Color color2 = new Color(PdfColorSpace.makeColorSpace(PdfName.DeviceRGB), null);

        boolean result = color1.equals(color2);
        Assert.assertTrue(result);

        Assert.assertEquals(color1.hashCode(), color2.hashCode());
    }

    @Test
    public void notEqualsAndHashCodeDifferentColorSpacesTest() {
        float[] colorValues = new float[]{0.0f, 0.5f, 0.1f};
        Color color1 = Color.makeColor(PdfColorSpace.makeColorSpace(PdfName.DeviceRGB), colorValues);
        Color color2 = Color.makeColor(PdfColorSpace.makeColorSpace(PdfName.DeviceGray), colorValues);

        boolean result = color1.equals(color2);
        Assert.assertFalse(result);

        Assert.assertNotEquals(color1.hashCode(), color2.hashCode());
    }

    @Test
    public void notEqualsNullObjectTest() {
        float[] colorValues = new float[]{0.0f, 0.5f, 0.1f};
        Color color1 = Color.makeColor(PdfColorSpace.makeColorSpace(PdfName.DeviceRGB), colorValues);

        boolean result = color1.equals(null);
        Assert.assertFalse(result);
    }

    @Test
    public void notEqualsDifferentClassesTest() {
        float[] colorValues = new float[]{0.0f, 0.5f, 0.1f};
        Color color1 = Color.makeColor(PdfColorSpace.makeColorSpace(PdfName.DeviceRGB), colorValues);
        DeviceCmyk cmyk = new DeviceCmyk(0, 0, 0, 0);

        boolean result = color1.equals(cmyk);
        Assert.assertFalse(result);
    }

    @Test
    public void nullColorSpaceTest() {
        float[] colorValues = new float[]{0.0f, 0.5f, 0.1f};

        Exception e = Assert.assertThrows(PdfException.class, () -> Color.makeColor(null, colorValues));
        Assert.assertEquals("Unknown color space.", e.getMessage());
    }

    @Test
    public void makeDeviceGrayNullColorValuesTest() {
        PdfColorSpace colorSpace = PdfColorSpace.makeColorSpace(PdfName.DeviceGray);
        Color color = Color.makeColor(colorSpace);

        Assert.assertTrue(color instanceof DeviceGray);
        Assert.assertArrayEquals(new float[]{0.0f}, color.getColorValue(), EPS);
    }

    @Test
    public void makeDeviceGrayTest() {
        float[] colorValues = new float[]{0.7f, 0.5f, 0.1f};
        PdfColorSpace colorSpace = PdfColorSpace.makeColorSpace(PdfName.DeviceGray);
        Color color = Color.makeColor(colorSpace, colorValues);

        Assert.assertTrue(color instanceof DeviceGray);
        Assert.assertArrayEquals(new float[]{0.7f}, color.getColorValue(), EPS);
    }

    @Test
    public void makeDeviceCmykNullColorValuesTest() {
        PdfColorSpace colorSpace = PdfColorSpace.makeColorSpace(PdfName.DeviceCMYK);
        Color color = Color.makeColor(colorSpace);

        Assert.assertTrue(color instanceof DeviceCmyk);
        Assert.assertArrayEquals(new float[]{0.0f, 0.0f, 0.0f, 1.0f}, color.getColorValue(), EPS);
    }

    @Test
    public void makeDeviceCmykTest() {
        float[] colorValues = new float[]{0.7f, 0.5f, 0.1f, 0.3f};
        PdfColorSpace colorSpace = PdfColorSpace.makeColorSpace(PdfName.DeviceCMYK);
        Color color = Color.makeColor(colorSpace, colorValues);

        Assert.assertTrue(color instanceof DeviceCmyk);
        Assert.assertArrayEquals(colorValues, color.getColorValue(), EPS);
    }

    @Test
    public void unknownDeviceCsTest() {
        Exception e = Assert.assertThrows(PdfException.class, () -> Color.makeColor(new CustomDeviceCs(null)));
        Assert.assertEquals("Unknown color space.", e.getMessage());
    }

    @Test
    public void makeCalGrayNullColorValuesTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.Gamma, new PdfNumber(2.2));
        PdfArray calGray = new PdfArray();
        calGray.add(PdfName.CalGray);
        calGray.add(dictionary);
        PdfColorSpace colorSpace = PdfColorSpace.makeColorSpace(calGray);

        Color color = Color.makeColor(colorSpace);

        Assert.assertTrue(color instanceof CalGray);
        Assert.assertArrayEquals(new float[]{0.0f}, color.getColorValue(), EPS);
    }

    @Test
    public void makeCalGrayTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.Gamma, new PdfNumber(2.2));
        PdfArray calGray = new PdfArray();
        calGray.add(PdfName.CalGray);
        calGray.add(dictionary);
        PdfColorSpace colorSpace = PdfColorSpace.makeColorSpace(calGray);
        float[] colorValues = new float[]{0.7f, 0.5f, 0.1f};

        Color color = Color.makeColor(colorSpace, colorValues);

        Assert.assertTrue(color instanceof CalGray);
        Assert.assertArrayEquals(new float[]{0.7f}, color.getColorValue(), EPS);
    }

    @Test
    public void makeCalRgbNullColorValuesTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.Gamma, new PdfNumber(2.2));
        PdfArray calRgb = new PdfArray();
        calRgb.add(PdfName.CalRGB);
        calRgb.add(dictionary);
        PdfColorSpace colorSpace = PdfColorSpace.makeColorSpace(calRgb);

        Color color = Color.makeColor(colorSpace);

        Assert.assertTrue(color instanceof CalRgb);
        Assert.assertArrayEquals(new float[]{0.0f, 0.0f, 0.0f}, color.getColorValue(), EPS);
    }

    @Test
    public void makeCalRgbTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.Gamma, new PdfNumber(2.2));
        PdfArray calRgb = new PdfArray();
        calRgb.add(PdfName.CalRGB);
        calRgb.add(dictionary);
        PdfColorSpace colorSpace = PdfColorSpace.makeColorSpace(calRgb);
        float[] colorValues = new float[]{0.7f, 0.5f, 0.1f};

        Color color = Color.makeColor(colorSpace, colorValues);

        Assert.assertTrue(color instanceof CalRgb);
        Assert.assertArrayEquals(colorValues, color.getColorValue(), EPS);
    }

    @Test
    public void makeLabNullColorValuesTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.Gamma, new PdfNumber(2.2));
        PdfArray calLab = new PdfArray();
        calLab.add(PdfName.Lab);
        calLab.add(dictionary);
        PdfColorSpace colorSpace = PdfColorSpace.makeColorSpace(calLab);

        Color color = Color.makeColor(colorSpace);

        Assert.assertTrue(color instanceof Lab);
        Assert.assertArrayEquals(new float[]{0.0f, 0.0f, 0.0f}, color.getColorValue(), EPS);
    }

    @Test
    public void makeLabTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.Gamma, new PdfNumber(2.2));
        PdfArray calLab = new PdfArray();
        calLab.add(PdfName.Lab);
        calLab.add(dictionary);
        PdfColorSpace colorSpace = PdfColorSpace.makeColorSpace(calLab);
        float[] colorValues = new float[]{0.7f, 0.5f, 0.1f};

        Color color = Color.makeColor(colorSpace, colorValues);

        Assert.assertTrue(color instanceof Lab);
        Assert.assertArrayEquals(colorValues, color.getColorValue(), EPS);
    }

    @Test
    public void unknownCieBasedCsTest() {
        Exception e = Assert.assertThrows(PdfException.class,
                () -> Color.makeColor(new CustomPdfCieBasedCs(new PdfArray()))
        );
        Assert.assertEquals("Unknown color space.", e.getMessage());
    }

    @Test
    public void makeDeviceNNullColorValuesTest() {
        PdfArray deviceN = new PdfArray();
        deviceN.add(PdfName.DeviceN);
        deviceN.add(new PdfArray());
        deviceN.add(null);
        deviceN.add(null);
        PdfColorSpace colorSpace = PdfColorSpace.makeColorSpace(deviceN);

        Color color = Color.makeColor(colorSpace);

        Assert.assertTrue(color instanceof DeviceN);
        Assert.assertArrayEquals(new float[]{}, color.getColorValue(), EPS);
    }

    @Test
    public void makeDeviceNTest() {
        PdfArray deviceN = new PdfArray();
        deviceN.add(PdfName.DeviceN);
        deviceN.add(new PdfArray());
        deviceN.add(null);
        deviceN.add(null);
        PdfColorSpace colorSpace = PdfColorSpace.makeColorSpace(deviceN);
        float[] colorValues = new float[]{0.7f, 0.5f, 0.1f};

        Color color = Color.makeColor(colorSpace, colorValues);

        Assert.assertTrue(color instanceof DeviceN);
        Assert.assertArrayEquals(colorValues, color.getColorValue(), EPS);
    }

    @Test
    public void makeIndexedNullColorValuesTest() {
        PdfArray indexed = new PdfArray();
        indexed.add(PdfName.Indexed);
        indexed.add(new PdfArray());
        indexed.add(null);
        indexed.add(null);
        PdfColorSpace colorSpace = PdfColorSpace.makeColorSpace(indexed);

        Color color = Color.makeColor(colorSpace);

        Assert.assertTrue(color instanceof Indexed);
        Assert.assertArrayEquals(new float[]{0.0f}, color.getColorValue(), EPS);
    }

    @Test
    public void makeIndexedTest() {
        PdfArray indexed = new PdfArray();
        indexed.add(PdfName.Indexed);
        indexed.add(new PdfArray());
        indexed.add(null);
        indexed.add(null);
        PdfColorSpace colorSpace = PdfColorSpace.makeColorSpace(indexed);
        float[] colorValues = new float[]{1.0f, 0.5f, 0.1f};

        Color color = Color.makeColor(colorSpace, colorValues);

        Assert.assertTrue(color instanceof Indexed);
        Assert.assertArrayEquals(new float[]{1f}, color.getColorValue(), EPS);
    }

    @Test
    public void unknownSpecialCsTest() {
        Exception e = Assert.assertThrows(PdfException.class,
                () -> Color.makeColor(new CustomPdfSpecialCs(new PdfArray()))
        );
        Assert.assertEquals("Unknown color space.", e.getMessage());
    }

    private static class CustomDeviceCs extends PdfDeviceCs {

        public CustomDeviceCs(PdfName pdfObject) {
            super(pdfObject);
        }

        @Override
        public int getNumberOfComponents() {
            return 0;
        }
    }

    private static class CustomPdfCieBasedCs extends PdfCieBasedCs {

        public CustomPdfCieBasedCs(PdfArray pdfObject) {
            super(pdfObject);
        }

        @Override
        public int getNumberOfComponents() {
            return 0;
        }
    }

    private static class CustomPdfSpecialCs extends PdfSpecialCs {

        public CustomPdfSpecialCs(PdfArray pdfObject) {
            super(pdfObject);
        }

        @Override
        public int getNumberOfComponents() {
            return 0;
        }
    }
}
