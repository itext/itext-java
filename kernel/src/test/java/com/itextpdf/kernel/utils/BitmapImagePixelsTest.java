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
package com.itextpdf.kernel.utils;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.test.ExtendedITextTest;

import java.net.MalformedURLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class BitmapImagePixelsTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/utils/BitmapImagePixelsTest/";

    @Test
    public void constructorWithImageByteArrayParameterTest() {
        byte[] imageBytes = new byte[] {1, 2, 3, 4, 5, 6};
        BitmapImagePixels imagePixels = new BitmapImagePixels(1, 3, 8, 2, imageBytes);
        Assertions.assertEquals(1, imagePixels.getWidth());
        Assertions.assertEquals(3, imagePixels.getHeight());
        Assertions.assertEquals(8, imagePixels.getBitsPerComponent());
        Assertions.assertEquals(2, imagePixels.getNumberOfComponents());
        Assertions.assertArrayEquals(imageBytes, imagePixels.getData());
    }

    @Test
    public void constructorWithImageByteArrayParameterInvalidParamsTest() {
        byte[] imageBytes = new byte[] {1, 2, 3, 4, 5};
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> new BitmapImagePixels(30, 40, 8, 3, imageBytes));
        Assertions.assertEquals(
                MessageFormatUtil.format(KernelExceptionMessageConstant.INVALID_DATA_LENGTH, 28800, 40),
                exception.getMessage());
    }

    @Test
    public void constructorWithParametersTest() {
        BitmapImagePixels imagePixels = new BitmapImagePixels(30, 40, 8, 3);
        Assertions.assertEquals(30, imagePixels.getWidth());
        Assertions.assertEquals(40, imagePixels.getHeight());
        Assertions.assertEquals(8, imagePixels.getBitsPerComponent());
        Assertions.assertEquals(3, imagePixels.getNumberOfComponents());
        byte[] expectedArray = new byte[30 * 40 * 8 * 3 / 8];
        Assertions.assertArrayEquals(expectedArray, imagePixels.getData());
    }

    @Test
    // Tests that each row ends on the byte border
    public void constructorWithParametersWithTrashBitsOnEahRowTest() {
        BitmapImagePixels imagePixels = new BitmapImagePixels(15, 15, 4, 3);
        Assertions.assertEquals(15, imagePixels.getWidth());
        Assertions.assertEquals(15, imagePixels.getHeight());
        Assertions.assertEquals(4, imagePixels.getBitsPerComponent());
        Assertions.assertEquals(3, imagePixels.getNumberOfComponents());
        Assertions.assertEquals(15, imagePixels.getMaxComponentValue());
        byte[] expectedArray = new byte[15 * (15 * 4 * 3 / 8 + 1)];
        Assertions.assertArrayEquals(expectedArray, imagePixels.getData());
    }

    @Test
    public void constructorWithPdfXObjectTest() throws MalformedURLException {
        final String sourceImage = SOURCE_FOLDER + "png-example.png";
        PdfImageXObject image = new PdfImageXObject(ImageDataFactory.create(sourceImage));
        BitmapImagePixels imagePixels = new BitmapImagePixels(image);
        Assertions.assertEquals(200, imagePixels.getWidth());
        Assertions.assertEquals(200, imagePixels.getHeight());
        Assertions.assertEquals(8, imagePixels.getBitsPerComponent());
        Assertions.assertEquals(3, imagePixels.getNumberOfComponents());
        Assertions.assertEquals(255, imagePixels.getMaxComponentValue());
        byte[] expectedArray = image.getPdfObject().getBytes();
        Assertions.assertArrayEquals(expectedArray, imagePixels.getData());
    }

    @Test
    public void constructorWithPdfXObjectWithoutBpcTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            final String sourceImage = SOURCE_FOLDER + "png-example.png";
            PdfImageXObject image = new PdfImageXObject(ImageDataFactory.create(sourceImage));
            image.getPdfObject().put(PdfName.BitsPerComponent, new PdfNumber(1));
            new BitmapImagePixels(image);
        });

        Assertions.assertEquals(
                MessageFormatUtil.format(KernelExceptionMessageConstant.INVALID_DATA_LENGTH, 120000, 960000),
                exception.getMessage());
    }

    @Test
    public void getPixelsAsLongs1bit1channelTest() {
        byte[] imageBytes = new byte[] {(byte) 0b10001000, 0b00100000,
                0b01000100, (byte) 0b10000000,
                0b00100000, (byte) 0b11100000};
        BitmapImagePixels imagePixels = new BitmapImagePixels(11, 3, 1, 1, imageBytes);

        for (int x = 0; x < 11; x++) {
            for (int y = 0; y < 3; y++) {
                final int mx = x;
                final int my = y;
                if (y==0 && x==0
                        || y==0 && x==4
                        || y==0 && x==10
                        || y==1 && x==1
                        || y==1 && x==5
                        || y==1 && x==8
                        || y==2 && x==2
                        || y==2 && x==8
                        || y==2 && x==9
                        || y==2 && x==10) {
                    Assertions.assertEquals(1, imagePixels.getPixelAsLongs(x,y)[0], () -> String.format("Expected a value of 1 for pixel %d, %d", my, mx));
                } else {
                    Assertions.assertEquals(0, imagePixels.getPixelAsLongs(x,y)[0], () -> String.format("Expected a value of 0 for pixel %d, %d", my, mx));
                }
            }
        }
    }

    @Test
    public void getPixelsAsLongs1bit3channelsTest() {
        byte[] imageBytes = new byte[] {
                /*              00011122           23334445           55666777           888
                /* 0 */(byte) 0b11100000, (byte) 0b00001110, (byte) 0b00111000, (byte) 0b11100000,
                /* 1 */(byte) 0b00011100, (byte) 0b01110000, (byte) 0b00000111, (byte) 0b00000000,
                /* 2 */(byte) 0b00000011, (byte) 0b10000001, (byte) 0b11000000, (byte) 0b00000000};
        BitmapImagePixels imagePixels = new BitmapImagePixels(9, 3, 1, 3, imageBytes);

        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 3; y++) {
                final int mx = x;
                final int my = y;
                if (y==0 && x==0
                        || y==0 && x==4
                        || y==0 && x==6
                        || y==0 && x==8
                        || y==1 && x==1
                        || y==1 && x==3
                        || y==1 && x==7
                        || y==2 && x==2
                        || y==2 && x==5) {
                    Assertions.assertEquals(1, imagePixels.getPixelAsLongs(x,y)[0], () -> String.format("Expected a value of 1 for pixel %d, %d", my, mx));
                } else {
                    Assertions.assertEquals(0, imagePixels.getPixelAsLongs(x,y)[0], () -> String.format("Expected a value of 0 for pixel %d, %d", my, mx));
                }
            }
        }
    }

    @Test
    public void getPixelsAsLongs2bit1channelTest() {
        byte[] imageBytes = new byte[] {
                /*              00112233           44556677           889900
                /* 0 */(byte) 0b11000000, (byte) 0b00001100, (byte) 0b00110000,
                /* 1 */(byte) 0b00001100, (byte) 0b00110000, (byte) 0b00000000,
                /* 2 */(byte) 0b00000011, (byte) 0b11000000, (byte) 0b11000000};
        BitmapImagePixels imagePixels = new BitmapImagePixels(11, 3, 2, 1, imageBytes);

        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 3; y++) {
                final int mx = x;
                final int my = y;
                if (y==0 && x==0
                        || y==0 && x==6
                        || y==0 && x==9
                        || y==1 && x==2
                        || y==1 && x==5
                        || y==2 && x==3
                        || y==2 && x==4
                        || y==2 && x==8) {
                    Assertions.assertEquals(3, imagePixels.getPixelAsLongs(x,y)[0], () -> String.format("Expected a value of 3 for pixel %d, %d", my, mx));
                } else {
                    Assertions.assertEquals(0, imagePixels.getPixelAsLongs(x,y)[0], () -> String.format("Expected a value of 0 for pixel %d, %d", my, mx));
                }
            }
        }
    }

    @Test
    public void getPixelsAsLongs2bit3channelTest() {
        byte[] imageBytes = new byte[] {
                /*              00000011           11112222           22333333           444444
                /* 0 */(byte) 0b11111100, (byte) 0b00001111, (byte) 0b11000000, (byte) 0b11111100,
                /* 1 */(byte) 0b00000011, (byte) 0b11110000, (byte) 0b00111111, (byte) 0b00000000,
                /* 2 */(byte) 0b00000000, (byte) 0b00001111, (byte) 0b11000000, (byte) 0b00000000};
        BitmapImagePixels imagePixels = new BitmapImagePixels(5, 3, 2, 3, imageBytes);

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 3; y++) {
                final int mx = x;
                final int my = y;
                if (y==0 && x==0
                        || y==0 && x==2
                        || y==0 && x==4
                        || y==1 && x==1
                        || y==1 && x==3
                        || y==2 && x==2) {
                    Assertions.assertEquals(3, imagePixels.getPixelAsLongs(x,y)[0], () -> String.format("Expected a value of 1 for pixel %d, %d", my, mx));
                    Assertions.assertEquals(3, imagePixels.getPixelAsLongs(x,y)[1], () -> String.format("Expected a value of 1 for pixel %d, %d", my, mx));
                    Assertions.assertEquals(3, imagePixels.getPixelAsLongs(x,y)[2], () -> String.format("Expected a value of 1 for pixel %d, %d", my, mx));
                } else {
                    Assertions.assertEquals(0, imagePixels.getPixelAsLongs(x,y)[0], () -> String.format("Expected a value of 0 for pixel %d, %d", my, mx));
                    Assertions.assertEquals(0, imagePixels.getPixelAsLongs(x,y)[1], () -> String.format("Expected a value of 0 for pixel %d, %d", my, mx));
                    Assertions.assertEquals(0, imagePixels.getPixelAsLongs(x,y)[2], () -> String.format("Expected a value of 0 for pixel %d, %d", my, mx));
                }
            }
        }
    }

    @Test
    public void getPixelsAsLongs4bit1channelTest() {
        byte[] imageBytes = new byte[] {
                /*              00001111           22223333           44445555           6666
                /* 0 */(byte) 0b11110000, (byte) 0b00001111, (byte) 0b11110000, (byte) 0b11110000,
                /* 1 */(byte) 0b00001111, (byte) 0b11110000, (byte) 0b00001111, (byte) 0b00000000,
                /* 2 */(byte) 0b00000000, (byte) 0b00001111, (byte) 0b00000000, (byte) 0b11110000};
        BitmapImagePixels imagePixels = new BitmapImagePixels(7, 3, 4, 1, imageBytes);

        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 3; y++) {
                final int mx = x;
                final int my = y;
                if (y==0 && x==0
                        || y==0 && x==3
                        || y==0 && x==4
                        || y==0 && x==6
                        || y==1 && x==1
                        || y==1 && x==2
                        || y==1 && x==5
                        || y==2 && x==3
                        || y==2 && x==6) {
                    Assertions.assertEquals(15, imagePixels.getPixelAsLongs(x,y)[0], () -> String.format("Expected a value of 15 for pixel %d, %d", my, mx));
                } else {
                    Assertions.assertEquals(0, imagePixels.getPixelAsLongs(x,y)[0], () -> String.format("Expected a value of 0 for pixel %d, %d", my, mx));
                }
            }
        }
    }

    @Test
    public void getPixelsAsLongs4bit3channelTest() {
        byte[] imageBytes = new byte[] {
                /*              00000000           00001111           11111111           22222222           22220000
                /* 0 */(byte) 0b11111111, (byte) 0b11110000, (byte) 0b00000000, (byte) 0b11111111, (byte) 0b11110000,
                /* 1 */(byte) 0b00000000, (byte) 0b00001111, (byte) 0b11111111, (byte) 0b00000000, (byte) 0b00000000,
                /* 2 */(byte) 0b00000000, (byte) 0b00001111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11110000};
        BitmapImagePixels imagePixels = new BitmapImagePixels(3, 3, 4, 3, imageBytes);

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                final int mx = x;
                final int my = y;
                if (y==0 && x==0
                        || y==0 && x==2
                        || y==1 && x==1
                        || y==2 && x==1
                        || y==2 && x==2) {
                    Assertions.assertEquals(15, imagePixels.getPixelAsLongs(x,y)[0], () -> String.format("Expected a value of 15 for pixel %d, %d", my, mx));
                    Assertions.assertEquals(15, imagePixels.getPixelAsLongs(x,y)[1], () -> String.format("Expected a value of 15 for pixel %d, %d", my, mx));
                    Assertions.assertEquals(15, imagePixels.getPixelAsLongs(x,y)[2], () -> String.format("Expected a value of 15 for pixel %d, %d", my, mx));
                } else {
                    Assertions.assertEquals(0, imagePixels.getPixelAsLongs(x,y)[0], () -> String.format("Expected a value of 0 for pixel %d, %d", my, mx));
                    Assertions.assertEquals(0, imagePixels.getPixelAsLongs(x,y)[1], () -> String.format("Expected a value of 0 for pixel %d, %d", my, mx));
                    Assertions.assertEquals(0, imagePixels.getPixelAsLongs(x,y)[2], () -> String.format("Expected a value of 0 for pixel %d, %d", my, mx));
                }
            }
        }
    }

    @Test
    public void getPixelsAsLongs16bit1channelTest() {
        byte[] imageBytes = new byte[] {
                /*              00000000           00000000           11111111           11111111
                /* 0 */(byte) 0b00000010, (byte) 0b00000010, (byte) 0b00000011, (byte) 0b00000011, /*514, 771*/
                /* 1 */(byte) 0b00000100, (byte) 0b00000100, (byte) 0b00000101, (byte) 0b00000101, /* 1028, 1258 */
                /* 2 */(byte) 0b00001000, (byte) 0b00001000, (byte) 0b00001001, (byte) 0b00001001}; /* 2056, 2313 */
        BitmapImagePixels imagePixels = new BitmapImagePixels(2, 3, 16, 1, imageBytes);

        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 3; y++) {
                final int mx = x;
                final int my = y;
                int expected = (2<<y) + x;
                expected <<= 8;
                expected += (2<<y) + x;
                final int mexpected = expected;
                Assertions.assertEquals(expected , imagePixels.getPixelAsLongs(x,y)[0], () -> String.format("Expected a value of %d for pixel %d, %d",mexpected, my, mx));
            }
        }
    }


    @Test
    public void getPixelsAsLongs16bit3channelTest() {
        final int[] testData = new int[] {
                /*       1r 1g 1b 2r 2g 2b 3r 3g 3b */
                /* 0 */  1, 2, 3, 4, 5, 6, 7, 8, 9,
                /* 1 */ 10,11,12,13,14,15,16,17,18,
                /* 2 */ 19,20,21,22,23,24,25,26,27};

        byte[] imageBytes = new byte[testData.length * 2];
        for (int i = 0; i < testData.length; i++) {
            imageBytes[i*2] = 0;
            imageBytes[i*2 + 1] = (byte) testData[i];
        }


        BitmapImagePixels imagePixels = new BitmapImagePixels(3, 3, 16, 3, imageBytes);

        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 3; y++) {
                final int mx = x;
                final int my = y;
                for (int c = 0; c < 3; c++) {
                    final int mc = c;
                    Assertions.assertEquals(testData[x*3+c +y*9] ,
                    imagePixels.getPixelAsLongs(x, y)[c], () -> String.format("Expected a value of %d for pixel %d, %d",
                                    testData[mx*3+mc +my*9], my, mx));
                }
            }
        }
    }



    @Test
    public void getPixelsAsLongsTest() throws MalformedURLException {
        final String sourceImage = SOURCE_FOLDER + "png-example.png";
        PdfImageXObject image = new PdfImageXObject(ImageDataFactory.create(sourceImage));
        BitmapImagePixels imagePixels = new BitmapImagePixels(image);
        long[] greyColor = new long[] {195, 195, 195};
        long[] redColor = new long[] {237, 28, 36};
        Assertions.assertArrayEquals(greyColor, imagePixels.getPixelAsLongs(0, 0));
        Assertions.assertArrayEquals(redColor, imagePixels.getPixelAsLongs(100, 50));
    }

    @Test
    public void getPixelsTest() throws MalformedURLException {
        final String sourceImage = SOURCE_FOLDER + "png-example.png";
        PdfImageXObject image = new PdfImageXObject(ImageDataFactory.create(sourceImage));
        BitmapImagePixels imagePixels = new BitmapImagePixels(image);
        double[] greyColor = new double[] {(double) 195 / 255, (double) 195 / 255, (double) 195 / 255};
        double[] redColor = new double[] {(double) 237 / 255, (double) 28 / 255, (double) 36 / 255};
        Assertions.assertArrayEquals(greyColor, imagePixels.getPixel(0, 0), 0.0001);
        Assertions.assertArrayEquals(redColor, imagePixels.getPixel(100, 50), 0.0001);
    }

    @Test
    public void setPixelsTest() throws MalformedURLException {
        final String sourceImage = SOURCE_FOLDER + "png-example.png";
        final String cmpImage = SOURCE_FOLDER + "png-example-modified.png";
        PdfImageXObject image = new PdfImageXObject(ImageDataFactory.create(sourceImage));
        BitmapImagePixels imagePixels = new BitmapImagePixels(image);
        double[] orangeColor = new double[] {(double) 255 / 255, (double) 170 / 255, (double) 0};
        for (int i = 0; i < imagePixels.getWidth(); i++) {
            imagePixels.setPixel(i, i, orangeColor);
        }
        PdfImageXObject cmpImageObject = new PdfImageXObject(ImageDataFactory.create(cmpImage));
        Assertions.assertArrayEquals(cmpImageObject.getPdfObject().getBytes(), imagePixels.getData());
    }

    @Test
    public void xCoordinateCannotBeNegativeGetterTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            BitmapImagePixels imagePixels = new BitmapImagePixels(30, 40, 8, 3);
            imagePixels.getPixel(-1, 0);
        });

        Assertions.assertEquals(
                MessageFormatUtil.format(KernelExceptionMessageConstant.PIXEL_OUT_OF_BORDERS, -1, 0, 30, 40),
                exception.getMessage());
    }

    @Test
    public void xCoordinateCannotBeNegativeSetterTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            BitmapImagePixels imagePixels = new BitmapImagePixels(30, 40, 8, 3);
            double[] orangePixel = new double[] {1., (double) 170 / 255, 0.};
            imagePixels.setPixel(-1, 0, orangePixel);
        });

        Assertions.assertEquals(
                MessageFormatUtil.format(KernelExceptionMessageConstant.PIXEL_OUT_OF_BORDERS, -1, 0, 30, 40),
                exception.getMessage());
    }

    @Test
    public void yCoordinateCannotBeNegativeGetterTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            BitmapImagePixels imagePixels = new BitmapImagePixels(30, 40, 8, 3);
            imagePixels.getPixel(0, -1);
        });

        Assertions.assertEquals(
                MessageFormatUtil.format(KernelExceptionMessageConstant.PIXEL_OUT_OF_BORDERS, 0, -1, 30, 40),
                exception.getMessage());

    }

    @Test
    public void yCoordinateCannotBeNegativeSetterTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            BitmapImagePixels imagePixels = new BitmapImagePixels(30, 40, 8, 3);
            double[] orangePixel = new double[] {1., (double) 170 / 255, 0.};
            imagePixels.setPixel(0, -1, orangePixel);
        });

        Assertions.assertEquals(
                MessageFormatUtil.format(KernelExceptionMessageConstant.PIXEL_OUT_OF_BORDERS, 0, -1, 30, 40),
                exception.getMessage());
    }

    @Test
    public void xCoordinateOutOfPictureGetterTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            BitmapImagePixels imagePixels = new BitmapImagePixels(30, 40, 8, 3);
            imagePixels.getPixel(31, 0);
        });

        Assertions.assertEquals(
                MessageFormatUtil.format(KernelExceptionMessageConstant.PIXEL_OUT_OF_BORDERS, 31, 0, 30, 40),
                exception.getMessage());
    }

    @Test
    public void xCoordinateOutOfPictureSetterTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            BitmapImagePixels imagePixels = new BitmapImagePixels(30, 40, 8, 3);
            double[] orangePixel = new double[] {1., (double) 170 / 255, 0.};
            imagePixels.setPixel(31, 0, orangePixel);
        });

        Assertions.assertEquals(
                MessageFormatUtil.format(KernelExceptionMessageConstant.PIXEL_OUT_OF_BORDERS, 31, 0, 30, 40),
                exception.getMessage());
    }

    @Test
    public void yCoordinateOutOfPictureGetterTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            BitmapImagePixels imagePixels = new BitmapImagePixels(30, 40, 8, 3);
            imagePixels.getPixel(0, 41);
        });

        Assertions.assertEquals(
                MessageFormatUtil.format(KernelExceptionMessageConstant.PIXEL_OUT_OF_BORDERS, 0, 41, 30, 40),
                exception.getMessage());
    }

    @Test
    public void yCoordinateOutOfPictureSetterTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            BitmapImagePixels imagePixels = new BitmapImagePixels(30, 40, 8, 3);
            double[] orangePixel = new double[] {1., (double) 170 / 255, 0.};
            imagePixels.setPixel(0, 41, orangePixel);
        });

        Assertions.assertEquals(
                MessageFormatUtil.format(KernelExceptionMessageConstant.PIXEL_OUT_OF_BORDERS, 0, 41, 30, 40),
                exception.getMessage());
    }

    @Test
    public void pixelArrayShouldMatchColorSpaceTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            BitmapImagePixels imagePixels = new BitmapImagePixels(30, 40, 8, 3);
            double[] blackPixel = new double[] {0., 0., 0., 0.};
            imagePixels.setPixel(0, 0, blackPixel);
        });

        Assertions.assertEquals(MessageFormatUtil.format(
                        KernelExceptionMessageConstant.LENGTH_OF_ARRAY_SHOULD_MATCH_NUMBER_OF_COMPONENTS, 4, 3),
                exception.getMessage());
    }

    @Test
    public void pixelArrayNormalizationTest() {
        BitmapImagePixels imagePixels = new BitmapImagePixels(30, 40, 8, 3);
        double[] greenPixel = new double[] {-10., 10., -10., };
        imagePixels.setPixel(0, 0, greenPixel);
        double[] expectedPixel = new double[] {0., 1., 0.};
        Assertions.assertArrayEquals(expectedPixel, imagePixels.getPixel(0, 0), 0.0001);
    }
}
