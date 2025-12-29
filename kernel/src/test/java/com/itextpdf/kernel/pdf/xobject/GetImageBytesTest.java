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
package com.itextpdf.kernel.pdf.xobject;

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.codec.TIFFConstants;
import com.itextpdf.io.codec.TIFFDirectory;
import com.itextpdf.io.codec.TIFFField;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject.ImageBytesRetrievalProperties;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


@Tag("IntegrationTest")
public class GetImageBytesTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/xobject"
            + "/GetImageBytesTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/xobject/GetImageBytesTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void testMultiStageFilters() throws Exception {
        // TODO DEVSIX-2940: extracted image is blank
        testFile("multistagefilter1.pdf", "Obj13", "jpg");
    }

    @Test
    @org.junit.jupiter.api.Disabled
    public void testAscii85Filters() throws Exception {
        testFile("ASCII85_RunLengthDecode.pdf", "Im9", "png");
    }

    @Test
    @org.junit.jupiter.api.Disabled
    public void testCcittFilters() throws Exception {
        testFile("ccittfaxdecode.pdf", "background0", "png");
    }

    @Test
    @org.junit.jupiter.api.Disabled
    public void testFlateDecodeFilters() throws Exception {
        testFile("flatedecode_runlengthdecode.pdf", "Im9", "png");
    }

    @Test
    public void testDctDecodeFilters() throws Exception {
        // TODO DEVSIX-2940: extracted image is upside down
        testFile("dctdecode.pdf", "im1", "jpg");
    }

    @Test
    public void testjbig2Filters() throws Exception {
        // TODO DEVSIX-2942: extracted jbig2 image is not readable by most popular image viewers
        testFile("jbig2decode.pdf", "2", "jbig2");
    }

    @Test
    public void testFlateCmyk() throws Exception {
        testFile("img_cmyk.pdf", "Im1", "tif");
    }

    @Test
    public void testFlateCmykIcc() throws Exception {
        testFile("img_cmyk_icc.pdf", "Im1", "tif");
    }

    @Test
    public void testFlateIndexed() throws Exception {
        testFile("img_indexed.pdf", "Im1", "png");
    }

    @Test
    @org.junit.jupiter.api.Disabled
    public void testFlateRgbIcc() throws Exception {
        testFile("img_rgb_icc.pdf", "Im1", "png");
    }

    @Test
    public void testFlateRgb() throws Exception {
        testFile("img_rgb.pdf", "Im1", "png");
    }

    @Test
    @org.junit.jupiter.api.Disabled
    public void testFlateCalRgb() throws Exception {
        testFile("img_calrgb.pdf", "Im1", "png");
    }
    @Test
    public void testJPXDecode() throws Exception {
        testFile("JPXDecode.pdf", "Im1", "jp2");
    }

    @Test
    public void testSeparationCSWithICCBasedAsAlternativeWithColorTrans() {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyTintTransformations(true);
        Exception e = Assertions.assertThrows(UnsupportedOperationException.class,
                () -> testFile("separationCSWithICCBasedAsAlternative.pdf", "Im1", "png",".ColorTrans", properties));
        Assertions.assertEquals(KernelExceptionMessageConstant
                        .GET_IMAGEBYTES_FOR_SEPARATION_COLOR_ONLY_SUPPORTS_RGB,
                e.getMessage());
    }

    @Test
    public void testSeparationCSWithICCBasedAsAlternative() throws Exception {
        testFile("separationCSWithICCBasedAsAlternative.pdf", "Im1", "png");
    }

    @Test
    // TODO: DEVSIX-6757 (update test after fix)
    @org.junit.jupiter.api.Disabled
    public void testSeparationCSWithDeviceCMYKAsAlternativeWithColorTrans() {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyTintTransformations(true);
        Assertions.assertThrows(UnsupportedOperationException.class, () ->
        {
            testFile("separationCSWithDeviceCMYKAsAlternative.pdf", "Im1", "png",".ColorTrans", properties);
        });
    }

    @Test
    // TODO: DEVSIX-6757 (update test after fix)
    @org.junit.jupiter.api.Disabled
    public void testSeparationCSWithDeviceCMYKAsAlternative() throws Exception {
        testFile("separationCSWithDeviceCMYKAsAlternative.pdf", "Im1", "png");
    }

    //TODO DEVSIX-5751: update image
    @Test
    public void testGrayScalePng() throws Exception {
        testFile("grayImages.pdf", "Im1", "png");
    }

    @Test
    // TODO: DEVSIX-6757 (update test after fix)
    @org.junit.jupiter.api.Disabled
    public void testSeparationCSWithDeviceRGBAsAlternative() throws Exception {
        testFile("separationCSWithDeviceRgbAsAlternative.pdf", "Im1", "png");
    }

    @Test
    // TODO: DEVSIX-6757 (update test after fix)
    @org.junit.jupiter.api.Disabled
    public void testSeparationCSWithDeviceRGBAsAlternativeWithColorTrans() throws Exception {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyTintTransformations(true);
        testFile("separationCSWithDeviceRgbAsAlternative.pdf", "Im1", "png", ".ColorTrans", properties);
    }

    @Test
    // TODO: DEVSIX-6757 (update test after fix)
    @org.junit.jupiter.api.Disabled
    public void testSeparationCSWithDeviceRGBAsAlternative2() throws Exception {
        testFile("spotColorImagesSmall.pdf", "Im1", "png");
    }

    @Test
    // TODO: DEVSIX-6757 (update test after fix)
    @org.junit.jupiter.api.Disabled
    public void testSeparationCSWithDeviceRGBAsAlternative2WithColorTrans() throws Exception {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyTintTransformations(true);
        testFile("spotColorImagesSmall.pdf", "Im1", "png", ".ColorTrans", properties);
    }

    @Test
    public void testRGBSeparationCSWithJPXDecoderAndFunctionType0() throws Exception {
        testFile("RGBJpxF0.pdf", "Im1", "jp2");
    }

    @Test
    public void testRGBSeparationCSWithDCTDecoderAndFunctionType0() throws Exception {
        testFile("RGBDctF0.pdf", "Im1", "jpg");
    }

    @Test
    @org.junit.jupiter.api.Disabled
    public void testRGBSeparationCSWithFlateDecoderAndFunctionType0() throws Exception {
        testFile("RGBFlateF0.pdf", "Im1", "png");
    }

    @Test
    @org.junit.jupiter.api.Disabled
    public void testRGBSeparationCSWithFlateDecoderAndFunctionType0WithColorTrans() throws Exception {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyTintTransformations(true);
        testFile("RGBFlateF0.pdf", "Im1", "png", ".ColorTrans", properties);
    }

    @Test
    public void testCMYKSeparationCSWithJPXDecoderAndFunctionType2() throws Exception {
        testFile("CMYKJpxF2.pdf", "Im1", "jp2");
    }

    @Test
    public void testRGBSeparationCSWithJPXDecoderAndFunctionType2() throws Exception {
        testFile("RGBJpxF2.pdf", "Im1", "jp2");
    }

    @Test
    public void testCMYKSeparationCSWithDCTDecoderAndFunctionType2() throws Exception {
        testFile("CMYKDctF2.pdf", "Im1", "jpg");
    }

    @Test
    public void testRGBSeparationCSWithDCTDecoderAndFunctionType2() throws Exception {
        testFile("RGBDctF2.pdf", "Im1", "jpg");
    }

    @Test
    public void testRGBSeparationCSWithFlateDecoderAndFunctionType2WithColorTrans() throws Exception {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyTintTransformations(true);
        testFile("RGBFlateF2.pdf", "Im1", "png", ".ColorTrans", properties);
    }

    @Test
    public void extractByteAlignedG4TiffImageTest() throws IOException {
        String inFileName = SOURCE_FOLDER + "extractByteAlignedG4TiffImage.pdf";
        String outImageFileName = DESTINATION_FOLDER + "extractedByteAlignedImage.png";
        String cmpImageFileName = SOURCE_FOLDER + "cmp_extractByteAlignedG4TiffImage.png";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFileName));

        ImageAndTypeExtractor listener = new ImageAndTypeExtractor();
        PdfCanvasProcessor processor = new PdfCanvasProcessor(listener);
        processor.processPageContent(pdfDocument.getPage(1));

        List<Tuple2<String, byte[]>> images = listener.getImages();
        Assertions.assertEquals(1, images.size());

        try (OutputStream fos = FileUtil.getFileOutputStream(outImageFileName)) {
            fos.write(images.get(0).getSecond(), 0, images.size());
        }

        // expected and actual are swapped here for simplicity
        int expectedLen = images.get(0).getSecond().length;
        byte[] buf = new byte[expectedLen];
        try (InputStream is = FileUtil.getInputStreamForFile(cmpImageFileName)) {
            int read = is.read(buf, 0, buf.length);
            Assertions.assertEquals(expectedLen, read);
            read = is.read(buf, 0, buf.length);
            Assertions.assertTrue(read <= 0);
        }
        Assertions.assertArrayEquals(images.get(0).getSecond(), buf);
    }

    @Test
    public void expectedByteAlignedTiffImageExtractionTest() throws IOException {
        //Byte-aligned image is expected in pdf file, but in fact it's not
        String inFileName = SOURCE_FOLDER + "expectedByteAlignedTiffImageExtraction.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFileName));

        ImageAndTypeExtractor listener = new ImageAndTypeExtractor();
        PdfCanvasProcessor processor = new PdfCanvasProcessor(listener);

        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> processor.processPageContent(pdfDocument.getPage(1))
        );
        Assertions.assertEquals(MessageFormatUtil
                        .format(IoExceptionMessageConstant.EXPECTED_TRAILING_ZERO_BITS_FOR_BYTE_ALIGNED_LINES),
                e.getMessage());
    }


    @Test
    public void inlineImageColorDepth1Test() throws IOException {
        //Byte-aligned image is expected in pdf file, but in fact it's not
        String inFileName = SOURCE_FOLDER + "inline_image_with_cs_object.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFileName));

        ImageAndTypeExtractor listener = new ImageAndTypeExtractor();
        PdfCanvasProcessor processor = new PdfCanvasProcessor(listener);
        processor.processPageContent(pdfDocument.getPage(1));


        Files.write(Paths.get(
                        DESTINATION_FOLDER,
                        "inline_image_with_cs_object.new." + listener.images.get(0).getFirst()),
                listener.images.get(0).getSecond());


        Assertions.assertEquals(1, listener.images.size());
        Assertions.assertEquals("png", listener.images.get(0).getFirst());

        byte[] cmpBytes = Files.readAllBytes(Paths.get(
                SOURCE_FOLDER, "inline_image_with_cs_object.png"));
        Assertions.assertArrayEquals(cmpBytes, listener.images.get(0).getSecond());

    }

    @Test
    public void deviceGray8bitTest() throws Exception {
        testFile("deviceGray8bit.pdf", "fzImg0", "png");
    }

    @Test
    public void deviceGray8bitFlateDecodeTest() throws Exception {
        testFile("deviceGray8bitFlateDecode.pdf", "Im1", "png");
    }

    @Test
    public void deviceGray1bitFlateDecodeInvertedTest() throws Exception {
        testFile("deviceGray1bitFlateDecodeInverted.pdf", "Im0", "png");
    }

    @Test
    public void deviceGray1bitFlateDecodeInvertedWithDecodeTest() throws Exception {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyDecodeArray(true);
        testFile("deviceGray1bitFlateDecodeInverted.pdf", "Im0", "png", ".decode", properties);
    }


    @Test
    public void deviceGray4bitFlateDecodeInvertedTest() throws Exception {
        testFile("deviceGray4bitFlateDecodeInverted.pdf", "Im1", "png");
    }

    @Test
    public void deviceGray4bitFlateDecodeInvertedWithDecodeTest() throws Exception {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyDecodeArray(true);
        testFile("deviceGray4bitFlateDecodeInverted.pdf", "Im1", "png", ".decode", properties);
    }

    @Test
    public void deviceGray8bitFlateDecodeWithMaskTest() throws Exception {
        testFile("deviceGray8bitFlateDecodeWithMask.pdf", "Im1", "png");
    }

    @Test
    public void deviceGray8bitFlateDecodeWithMaskTransparencyTest() throws Exception {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyTransparency(true);
        testFile("deviceGray8bitFlateDecodeWithMask.pdf", "Im1", "png", ".trans", properties);
    }

    @Test
    public void deviceGray8bitDctDecodeTest() throws Exception {
        testFile("deviceGray8bitDctDecode.pdf", "fzImg0", "jpg");
    }

    @Test
    public void deviceGray8bitJPXDecodeTest() throws Exception {
        testFile("deviceGray8bitJPXDecode.pdf", "fzImg0", "jp2");
    }

    @Test
    public void deviceGray1bitCCITTFaxDecodeTest() throws Exception {
        testFile("deviceGray1bitCCITTFaxDecode.pdf", "Im1", "png");
    }

    @Test
    public void deviceGray8bitFlateDecodeMaskRotatedTest() throws Exception {
        testFile("deviceGray8bitFlateDecodeMaskRotated.pdf", "Im1", "png");
    }

    @Test
    public void deviceGray8bitFlateDecodeMaskRotatedWithTransparencyTest() throws Exception {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyTransparency(true);
        testFile("deviceGray8bitFlateDecodeMaskRotated.pdf", "Im1", "png", ".trans", properties);
    }

    @Test
    public void deviceGray8bitFlateDecodeScaledTest() throws Exception {
        testFile("deviceGray8bitFlateDecodeScaled.pdf", "Im1", "png");
    }

    @Test
    public void deviceGray8bitFlateCombinedTransformationTest() throws Exception {
        testFile("deviceGray8bitFlateCombinedTransformation.pdf", "Im1", "png");
    }

    @Test
    public void dRgb1BitDecodeInvertTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> testFile("dRgb1BitDecodeInvert.pdf", "Im1", "png"));
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.COLOR_DEPTH_IS_NOT_SUPPORTED_FOR_COLORSPACE,
                1, PdfName.DeviceRGB), e.getMessage());
    }

    @Test
    public void dRgb1BitDecodeTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> testFile("dRgb1BitDecode.pdf", "Im1", "png"));
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.COLOR_DEPTH_IS_NOT_SUPPORTED_FOR_COLORSPACE,
                1, PdfName.DeviceRGB), e.getMessage());
    }

    @Test
    public void dRgb1BitTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> testFile("dRgb1Bit.pdf", "Im1", "png"));
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.COLOR_DEPTH_IS_NOT_SUPPORTED_FOR_COLORSPACE,
                1, PdfName.DeviceRGB), e.getMessage());
    }

    @Test
    public void dRgb4BitDecodeInvertTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> testFile("dRgb4BitDecodeInvert.pdf", "Im1", "png"));
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.COLOR_DEPTH_IS_NOT_SUPPORTED_FOR_COLORSPACE,
                4, PdfName.DeviceRGB), e.getMessage());
    }

    @Test
    public void dRgb4BitDecodeTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> testFile("dRgb4BitDecode.pdf", "Im1", "png"));
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.COLOR_DEPTH_IS_NOT_SUPPORTED_FOR_COLORSPACE,
                4, PdfName.DeviceRGB), e.getMessage());
    }

    @Test
    public void dRgb4BitTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> testFile("dRgb4Bit.pdf", "Im1", "png"));
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.COLOR_DEPTH_IS_NOT_SUPPORTED_FOR_COLORSPACE,
                4, PdfName.DeviceRGB), e.getMessage());
    }

    @Test
    public void dRgbDctDecodeInvertTest() throws Exception {
        testFile("dRgbDctDecodeInvert.pdf", "Im1", "jpg");
    }

    @Test
    public void dRgbDctDecodeTest() throws Exception {
        testFile("dRgbDctDecode.pdf", "Im1", "jpg");
    }

    @Test
    public void dRgbDctMaskedTest() throws Exception {
        testFile("dRgbDctMasked.pdf", "Im1", "jpg");
    }

    @Test
    public void dRgbDCTMaskedDecodeTest() throws Exception {
        testFile("dRgbDCTMaskedDecode.pdf", "Im1", "jpg");
    }

    @Test
    public void dRgbDCTMaskedInvertTest() throws Exception {
        testFile("dRgbDCTMaskedInvert.pdf", "Im1", "jpg");
    }

    @Test
    public void dRgbDCTtransformationsDecodeInvertTest() throws Exception {
        testFile("dRgbDCTtransformationsDecodeInvert.pdf", "Im1", "jpg");
    }

    @Test
    public void dRgbDCTtransformationsDecodeTest() throws Exception {
        testFile("dRgbDCTtransformationsDecode.pdf", "Im1", "jpg");
    }

    @Test
    public void dRgbDCTtransformationsMaskedDecodeInvertTest() throws Exception {
        testFile("dRgbDCTtransformationsMaskedDecodeInvert.pdf", "Im1", "jpg");
    }

    @Test
    public void dRgbDCTtransformationsMaskedDecodeTest() throws Exception {
        testFile("dRgbDCTtransformationsMaskedDecode.pdf", "Im1", "jpg");
    }

    @Test
    public void dRgbDCTtransformationsTest() throws Exception {
        testFile("dRgbDCTtransformations.pdf", "Im1", "jpg");
    }

    @Test
    public void dRgbDCTTransparancyDecodeInvertTest() throws Exception {
        testFile("dRgbDCTTransparancyDecodeInvert.pdf", "Im1", "jpg");
    }

    @Test
    public void dRgbDCTTransparancyDecodeTest() throws Exception {
        testFile("dRgbDCTTransparancyDecode.pdf", "Im1", "jpg");
    }

    @Test
    public void dRgbDCTTransparancyMaskDecodeInvertTest() throws Exception {
        testFile("dRgbDCTTransparancyMaskDecodeInvert.pdf", "Im1", "jpg");
    }

    @Test
    public void dRgbDCTTransparancyMaskDecodeTest() throws Exception {
        testFile("dRgbDCTTransparancyMaskDecode.pdf", "Im1", "jpg");
    }

    @Test
    public void dRgbDCTTransparancyMaskTest() throws Exception {
        testFile("dRgbDCTTransparancyMask.pdf", "Im1", "jpg");
    }

    @Test
    public void dRgbDCTTransparancyTest() throws Exception {
        testFile("dRgbDCTTransparancy.pdf", "Im1", "jpg");
    }

    @Test
    public void dRgbDCTTransparancyTransformDecodeInvertTest() throws Exception {
        testFile("dRgbDCTTransparancyTransformDecodeInvert.pdf", "Im1", "jpg");
    }

    @Test
    public void dRgbDCTTransparancyTransformDecodeTest() throws Exception {
        testFile("dRgbDCTTransparancyTransformDecode.pdf", "Im1", "jpg");
    }

    @Test
    public void dRgbDCTTransparancyTransformMaskDecodeInvertTest() throws Exception {
        testFile("dRgbDCTTransparancyTransformMaskDecodeInvert.pdf", "Im1", "jpg");
    }

    @Test
    public void dRgbDCTTransparancyTransformMaskDecodeTest() throws Exception {
        testFile("dRgbDCTTransparancyTransformMaskDecode.pdf", "Im1", "jpg");
    }

    @Test
    public void dRgbDCTTransparancyTransformMaskTest() throws Exception {
        testFile("dRgbDCTTransparancyTransformMask.pdf", "Im1", "jpg");
    }

    @Test
    public void dRgbDCTTransparancyTransformTest() throws Exception {
        testFile("dRgbDCTTransparancyTransform.pdf", "Im1", "jpg");
    }

    @Test
    public void dRgbFlateTest() throws Exception {
        testFile("dRgbFlate.pdf", "Im0", "png");
    }

    @Test
    public void dRgbFlateTransparencyTest() throws Exception {
        testFile("dRgbFlateTransparency.pdf", "Im0", "png");
    }

    @Test
    public void dRgbFlateInvertedTest() throws Exception {
        testFile("dRgbFlateInverted.pdf", "Im0", "png");
    }

    @Test
    public void dRgbFlateInvertedWithDecodeTest() throws Exception {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyDecodeArray(true);
        testFile("dRgbFlateInverted.pdf", "Im0", "png", ".decode", properties);
    }

    @Test
    public void dRgbFlateRotatedTest() throws Exception {
        testFile("dRgbFlateRotated.pdf", "Im0", "png");
    }

    @Test
    public void dRgbFlateRotatedInvertedTest() throws Exception {
        testFile("dRgbFlateRotatedInverted.pdf", "Im0", "png");
    }

    @Test
    public void dRgbFlateRotatedInvertedWithDecodeTest() throws Exception {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyDecodeArray(true);
        testFile("dRgbFlateRotatedInverted.pdf", "Im0", "png", ".decode", properties);
    }

    @Test
    public void dRgbFlate1bitTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> testFile("dRgbFlate1bit.pdf", "Im0", "png"));
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.COLOR_DEPTH_IS_NOT_SUPPORTED_FOR_COLORSPACE,
                1, PdfName.DeviceRGB), e.getMessage());
    }

    @Test
    public void dRgbFlate4bitTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> testFile("dRgbFlate4bit.pdf", "Im0", "png"));
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.COLOR_DEPTH_IS_NOT_SUPPORTED_FOR_COLORSPACE,
                4, PdfName.DeviceRGB), e.getMessage());
    }

    @Test
    //TODO DEVSIX-1682: Update after supporting
    public void dRGBDCTSmaskTest() throws Exception {
        testFile("dRGBDCTSmask.pdf", "Im1", "jpg");
    }

    @Test
    //TODO DEVSIX-1682: Update after supporting
    @org.junit.jupiter.api.Disabled
    public void dRGBFlateSmaskTest() throws Exception {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyTransparency(true);
        testFile("dRGBFlateSmask.pdf", "Im1", "png", ".trans", properties);
    }

    @Test
    //TODO DEVSIX-1682: Update after supporting
    public void dGrayDCTSmaskTest() throws Exception {
        testFile("dGrayDCTSmask.pdf", "Im1", "jpg");
    }

    @Test
    //TODO DEVSIX-1682: Update after supporting
    public void dGrayFlateSmaskTest() throws Exception {
        testFile("dGrayFlateSmask.pdf", "Im0", "png");
    }

    @Test
    public void ICCBasedDctMaskedInvertedTest() throws Exception {
        testFile("ICCBasedDctMaskedInverted.pdf", "Im1", "jpg");
    }

    @Test
    public void ICCBasedDCTTransformMaskedDecodeTest() throws Exception {
        testFile("ICCBasedDCTTransformMaskedDecode.pdf", "Im1", "jpg");
    }

    @Test
    public void ICCBasedDCTTransformMaskedDecodeInvertTest() throws Exception {
        testFile("ICCBasedDCTTransformMaskedDecodeInvert.pdf", "Im1", "jpg");
    }

    @Test
    public void ICCBasedFlateTransformMaskedDecodeTest() throws Exception {
        testFile("ICCBasedFlateTransformMaskedDecode.pdf", "Im1", "jpg");
    }

    @Test
    public void ICCBasedFlateTransformMaskedDecodeInvertTest() throws Exception {
        testFile("ICCBasedFlateTransformMaskedDecodeInvert.pdf", "Im1", "jpg");
    }

    @Test
    public void deviceCMYKTest() throws Exception {
        testFile("deviceCMYK.pdf", "Im1", "tif");
    }

    @Test
    public void deviceCMYKFlateDecodeInvertedTest() throws Exception {
        testFile("deviceCMYKFlateDecodeInverted.pdf", "Im1", "tif");
    }

    @Test
    public void deviceCMYKFlateDecodeInvertedWithDecodeTest() throws Exception {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyDecodeArray(true);
        testFile("deviceCMYKFlateDecodeInverted.pdf", "Im1", "tif");
    }


    @Test
    public void calGray8bitTest() throws Exception {
        testFile("calGray8bit.pdf", "Im1", "png");
    }

    @Test
    public void calGray8bitGamma22Test() throws Exception {
        testFile("calGray8bitGamma22.pdf", "Im1", "png");
    }

    @Test
    public void calGray8bitGamma18InvertedTest() throws Exception {
        testFile("calGray8bitGamma18Inverted.pdf", "Im1", "png");
    }

    @Test
    public void calGray8bitGamma18InvertedWithDecodeTest() throws Exception {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyDecodeArray(true);
        testFile("calGray8bitGamma18Inverted.pdf", "Im1", "png",".decode", properties);
    }

    @Test
    public void calGray1bitTest() throws Exception {
        testFile("calGray1bit.pdf", "Im1", "png");
    }

    @Test
    public void calGray1bitInvertedTest() throws Exception {
        testFile("calGray1bitInverted.pdf", "Im1", "png");
    }

    @Test
    public void calGray1bitInvertedWithDecodingTest() throws Exception {
        ImageBytesRetrievalProperties properties =  ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyDecodeArray(true);
        testFile("calGray1bitInverted.pdf", "Im1", "png", ".decode", properties);
    }

    @Test
    public void calGray4bitGamma22Test() throws Exception {
        testFile("calGray4bitGamma22.pdf", "Im1", "png");
    }

    @Test
    public void calGray4bitGamma10InvertedTest() throws Exception {
        testFile("calGray4bitGamma10Inverted.pdf", "Im1", "png");
    }

    @Test
    public void calGray4bitGamma10InvertedWithDecodingTest() throws Exception {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyDecodeArray(true);
        testFile("calGray4bitGamma10Inverted.pdf", "Im1", "png", ".decode", properties);
    }

    @Test
    public void calGray8bitExtGStateTest() throws Exception {
        testFile("calGray8bitExtGStateTest.pdf", "Im1", "png");
    }

    @Test
    public void calRGB8bitTest() throws Exception {
        testFile("calRGB8bit.pdf", "Im1", "png");
    }

    @Test
    public void calRGB8bitCustomGammaTest() throws Exception {
        testFile("calRGB8bitCustomGamma.pdf", "Im1", "png");
    }

    @Test
    public void calRGB8bitInvertedTest() throws Exception {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyDecodeArray(true);
        testFile("calRGB8bitInverted.pdf", "Im1", "png", ".decode", properties);
    }

    @Test
    public void calRGB4bitTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> testFile("calRGB4bit.pdf", "Im1", "png"));
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.COLOR_DEPTH_IS_NOT_SUPPORTED_FOR_COLORSPACE,
                "4", PdfName.CalRGB), e.getMessage());
    }

    @Test
    public void calRGB8bitNoFilterTest() throws Exception {
        testFile("calRGB8bitNoFilter.pdf", "Im1", "png");
    }

    @Test
    public void calRGB8bitSMaskTest() throws Exception {
        testFile("calRGB8bitSMask.pdf", "Im1", "png");
    }

    //TODO DEVSIX-1682: update after supporting
    @Test
    public void calRGB8bitSMaskWithTransparencyTest() throws Exception {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyTransparency(true);
        testFile("calRGB8bitSMask.pdf", "Im1", "png",".trans", properties);
    }

    @Test
    public void calRGB8bitExtGStateTest() throws Exception {
        testFile("calRGB8bitExtGState.pdf", "Im1", "png");
    }

    @Test
    public void calRGB8bitCustomWhitePointTest() throws Exception {
        testFile("calRGB8bitCustomWhitePoint.pdf", "Im1", "png");
    }

    @Test
    public void calRGB1bitTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> testFile("calRGB1bit.pdf", "Im1", "tif"));
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.COLOR_DEPTH_IS_NOT_SUPPORTED_FOR_COLORSPACE,
                "1", PdfName.CalRGB), e.getMessage());
    }

    @Test
    public void calRGB2bitTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> testFile("calRGB2bit.pdf", "Im1", "tif"));
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.COLOR_DEPTH_IS_NOT_SUPPORTED_FOR_COLORSPACE,
                "2", PdfName.CalRGB), e.getMessage());
    }

    @Test
    public void lab8bitTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> testFile("lab8bit.pdf", "Im1", "tif"));
        Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.COLOR_SPACE_IS_NOT_SUPPORTED,
                PdfName.Lab), e.getMessage());
    }

    @Test
    public void labDctMaskedTest() throws Exception {
        testFile("labDctMasked.pdf", "Im1", "jpg");
    }

    @Test
    public void labDctTransformTest() throws Exception {
        testFile("labDctTransform.pdf", "Im1", "jpg");
    }

    @Test
    public void labDctTransparancyTest() throws Exception {
        testFile("labDctTransparancy.pdf", "Im1", "jpg");
    }

    @Test
    public void labDctTransparancyMaskTest() throws Exception {
        testFile("labDctTransparancyMask.pdf", "Im1", "jpg");
    }

    @Test
    public void labDctTransparancyTransformTest() throws Exception {
        testFile("labDctTransparancyTransform.pdf", "Im1", "jpg");
    }

    @Test
    public void labDctTransparancyTransformMaskTest() throws Exception {
        testFile("labDctTransparancyTransformMask.pdf", "Im1", "jpg");
    }

    @Test
    public void indexed1bitTest() throws Exception {
        testFile("indexed1bit.pdf", "Im0", "png");
    }

    @Test
    public void indexed2bitTest() throws Exception {
        testFile("indexed2bit.pdf", "Im0", "png");
    }

    @Test
    public void indexed4bitTest() throws Exception {
        testFile("indexed4bit.pdf", "Im0", "png");
    }

    @Test
    public void indexed8bitTest() throws Exception {
        testFile("indexed8bit.pdf", "Im0", "png");
    }

    @Test
    public void indexed8bitGradientTest() throws Exception {
        testFile("indexed8bitGradient.pdf", "Im0", "png");
    }

    //TODO DEVSIX-1682: update after supporting
    @Test
    public void indexed8bitSMaskTest() throws Exception {
        testFile("indexed8bitSMask.pdf", "Im0", "png");
    }

    //TODO DEVSIX-1682: update after supporting
    @Test
    public void indexed8bitSMaskWithTransparencyTest() throws Exception {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyTransparency(true);
        testFile("indexed8bitSMask.pdf", "Im0", "png", ".trans", properties);
    }

    @Test
    public void separation1bitDeviceCMYKTest() throws Exception {
        testFile("separation1bitDeviceCMYK.pdf", "Im0", "png");
    }

    @Test
    public void separation1bitDeviceCMYKWithColorTransTest() {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyTintTransformations(true);
        Exception e = Assertions.assertThrows(UnsupportedOperationException.class,
                () -> testFile("separation1bitDeviceCMYK.pdf", "Im0", "png", ".ColorTrans", properties));
        Assertions.assertEquals(KernelExceptionMessageConstant.GET_IMAGEBYTES_FOR_SEPARATION_COLOR_ONLY_SUPPORTS_RGB,
                e.getMessage());
    }

    @Test
    public void separation8bitDeviceCMYKWithColorTransTest() {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyTintTransformations(true);
        Exception e = Assertions.assertThrows(UnsupportedOperationException.class,
                () -> testFile("separation8bitDeviceCMYK.pdf", "Im0", "png", ".ColorTrans", properties));
        Assertions.assertEquals(KernelExceptionMessageConstant.GET_IMAGEBYTES_FOR_SEPARATION_COLOR_ONLY_SUPPORTS_RGB,
                e.getMessage());
    }

    @Test
    public void separation8bitDeviceCMYKTest() throws Exception {
        testFile("separation8bitDeviceCMYK.pdf", "Im0", "png");
    }

    @Test
    public void separation8bitDeviceRGBTest() throws Exception {
        testFile("separation8bitDeviceRGB.pdf", "Im0", "png");
    }

    @Test
    public void separation8bitDeviceRGBWithColorTransTest() throws Exception {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyTintTransformations(true);
        testFile("separation8bitDeviceRGB.pdf", "Im0", "png", ".ColorTrans", properties);
    }

    @Test
    public void separation8bitLabTestWithColorTransTest() {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyTintTransformations(true);
        Exception e = Assertions.assertThrows(UnsupportedOperationException.class,
                () -> testFile("separation8bitLab.pdf", "Im0", "png",".ColorTrans", properties));
        Assertions.assertEquals(KernelExceptionMessageConstant
                        .GET_IMAGEBYTES_FOR_SEPARATION_COLOR_ONLY_SUPPORTS_RGB,
                e.getMessage());
    }

    @Test
    public void separation8bitLabTest() throws Exception {
        testFile("separation8bitLab.pdf", "Im0", "png");
    }

    @Test
    public void separation8bitDeviceCMYKExtGStateWithColorTransTest() {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyTintTransformations(true);
        Exception e = Assertions.assertThrows(UnsupportedOperationException.class,
                () -> testFile("separation8bitDeviceCMYKExtGState.pdf", "Im0", "png",".ColorTrans", properties));
        Assertions.assertEquals(KernelExceptionMessageConstant.GET_IMAGEBYTES_FOR_SEPARATION_COLOR_ONLY_SUPPORTS_RGB,
                e.getMessage());
    }

    @Test
    public void separation8bitDeviceCMYKExtGStateTest() throws Exception {
        testFile("separation8bitDeviceCMYKExtGState.pdf", "Im0", "png");
    }

    @Test
    public void separation8bitDeviceRGBTransparencyTest() throws Exception {
        testFile("separation8bitDeviceRGBTransparency.pdf", "Im0", "png");
    }

    @Test
    // transparency is set trough graphic state and is not determinable from the xobject
    public void separation8bitDeviceRGBTransparencyFullTest() throws Exception {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getFullOption();
        testFile("separation8bitDeviceRGBTransparency.pdf", "Im0", "png", ".Full", properties);
    }

    @Test
    public void separation8bitDeviceRGBTransparencyWithColorTransTest() throws Exception {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyTintTransformations(true);
        testFile("separation8bitDeviceRGBTransparency.pdf", "Im0", "png", ".ColorTrans", properties);
    }

    @Test
    public void separation8bitDeviceRGBDctDecodeTest() throws Exception {
        testFile("separation8bitDeviceRGBDctDecode.pdf", "Im0", "jpg");
    }

    @Test
    public void separation8bitDeviceRGBCustomDecodeRangeTest() throws Exception {
        testFile("separation8bitDeviceRGBCustomDecodeRange.pdf", "Im0", "png");
    }

    @Test
    public void separation8bitDeviceRGBCustomDecodeRangeAllOptionsTest() throws Exception {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getFullOption();
        testFile("separation8bitDeviceRGBCustomDecodeRange.pdf", "Im0", "png", ".AllOptions", properties);
    }

    @Test
    public void separation8bitDeviceRGBCustomDecodeRangeWithColorTransTest() throws Exception {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyTintTransformations(true);
        testFile("separation8bitDeviceRGBCustomDecodeRange.pdf", "Im0", "png", ".ColorTrans", properties);
    }

    @Test
    public void separation1bitDeviceRGBWithColorTransTest() {
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyTintTransformations(true);
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class, ()->
                testFile("separation1bitDeviceRGB.pdf", "Im0", "png",".ColorTrans", properties));
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.COLOR_DEPTH_IS_NOT_SUPPORTED_FOR_SEPARATION_ALTERNATE_COLORSPACE,
                "1", PdfName.DeviceRGB), e.getMessage());
    }

    @Test
    public void separation1bitDeviceRGBTest() throws Exception {
        testFile("separation1bitDeviceRGB.pdf", "Im0", "png");
    }

    @Test
    public void separation2bitDeviceRGBWithColorTransformTest(){
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyTintTransformations(true);
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class, ()->
                testFile("separation2bitDeviceRGB.pdf", "Im0", "png",".ColorTrans", properties));
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.COLOR_DEPTH_IS_NOT_SUPPORTED_FOR_SEPARATION_ALTERNATE_COLORSPACE,
                "2", PdfName.DeviceRGB), e.getMessage());
    }

    @Test
    public void separation2bitDeviceRGBTest() throws Exception {
        testFile("separation2bitDeviceRGB.pdf", "Im0", "png");
    }

    @Test
    public void separation4bitDeviceRGBWithColorTransTest(){
        ImageBytesRetrievalProperties properties = ImageBytesRetrievalProperties.getApplyFiltersOnly();
        properties.setApplyTintTransformations(true);
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class, ()->
                testFile("separation4bitDeviceRGB.pdf", "Im0", "png",".ColorTrans", properties));
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.COLOR_DEPTH_IS_NOT_SUPPORTED_FOR_SEPARATION_ALTERNATE_COLORSPACE,
                "4", PdfName.DeviceRGB), e.getMessage());
    }

    @Test
    public void separation4bitDeviceRGBTest() throws Exception {
        testFile("separation4bitDeviceRGB.pdf", "Im0", "png");
    }

    @Test
    public void deviceN8bitDeviceCMYKTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> testFile("deviceN8bitDeviceCMYK.pdf", "Im0", "tif"));
        Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.COLOR_SPACE_IS_NOT_SUPPORTED,
                "/DeviceN"), e.getMessage());
    }

    @Test
    public void deviceN8bitDeviceRGBTransparencyTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> testFile("deviceN8bitDeviceRGBTransparency.pdf", "Im0", "tif"));
        Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.COLOR_SPACE_IS_NOT_SUPPORTED,
                "/DeviceN"), e.getMessage());
    }

    @Test
    public void deviceN8bitDeviceRGBSpotASpotBTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> testFile("deviceN8bitDeviceRGBSpotASpotB.pdf", "Im0", "tif"));
        Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.COLOR_SPACE_IS_NOT_SUPPORTED,
                "/DeviceN"), e.getMessage());
    }

    @Test
    public void deviceN4bitDeviceCMYKTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> testFile("deviceN4bitDeviceCMYKTest.pdf", "Im0", "tif"));
        Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.COLOR_SPACE_IS_NOT_SUPPORTED,
                "/DeviceN"), e.getMessage());
    }

    @Test
    public void deviceN8bitDeviceCMYKTransparencyDCTDecodeTest() throws Exception {
        testFile("deviceN8bitDeviceCMYKTransparencyDCTDecode.pdf", "Im0", "jpg");
    }

    @Test
    public void deviceN8bit5ChannelsTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> testFile("deviceN8bit5Channels.pdf", "Im0", "tif"));
        Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.COLOR_SPACE_IS_NOT_SUPPORTED,
                "/DeviceN"), e.getMessage());
    }

    @Test
    public void deviceN8bitDeviceRGBCustomDecodeTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> testFile("deviceN8bitDeviceRGBCustomDecode.pdf", "Im0", "tif"));
        Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.COLOR_SPACE_IS_NOT_SUPPORTED,
                "/DeviceN"), e.getMessage());
    }

    @Test
    public void deviceN8bitDeviceCMYKFunctionType0Test() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> testFile("deviceN8bitDeviceCMYKFunctionType0.pdf", "Im0", "tif"));
        Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.COLOR_SPACE_IS_NOT_SUPPORTED,
                "/DeviceN"), e.getMessage());
    }

    @Test
    public void deviceN8bitDeviceRGBRotatedTest() {
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> testFile("deviceN8bitDeviceRGBRotated.pdf", "Im0", "tif"));
        Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.COLOR_SPACE_IS_NOT_SUPPORTED,
                "/DeviceN"), e.getMessage());
    }

    @Test
    //TODO DEVSIX-1683: Update after supporting
    public void customCalRGBColorSpaceTest() throws Exception {
        testFile("customColorSpaceRGB.pdf", "Im1", "jpg");
    }

    @Test
    //TODO DEVSIX-1683: Update after supporting
    @org.junit.jupiter.api.Disabled
    public void customIndexedColorSpaceTest() throws Exception {
        testFile("customColorIndexed.pdf", "Im1", "png");
    }

    @Test
    //TODO DEVSIX-1683: Update after supporting
    @org.junit.jupiter.api.Disabled
    public void customSeparationColorSpaceTest() throws Exception {
        testFile("customColorSpaceSeparation.pdf", "Im1", "png");
    }

    private void testFile(String filename, String objectid, String expectedImageFormat) throws Exception {
        testFile(filename, objectid, expectedImageFormat,
                "", ImageBytesRetrievalProperties.getApplyFiltersOnly());
    }

    private void testFile(String filename, String objectid, String expectedImageFormat,
            String compareFileMarker, PdfImageXObject.ImageBytesRetrievalProperties properties ) throws Exception {
        testFile(filename, objectid, expectedImageFormat, false, compareFileMarker, properties);
    }

    private void testFile(String filename, String objectid, String expectedImageFormat, boolean saveResult,
            String compareFileMarker, PdfImageXObject.ImageBytesRetrievalProperties properties)
            throws Exception {
        try (PdfReader reader = new PdfReader(SOURCE_FOLDER + filename);
                PdfDocument pdfDocument = new PdfDocument(reader)) {
            PdfResources resources = pdfDocument.getPage(1).getResources();
            PdfDictionary xobjects = resources.getResource(PdfName.XObject);

            PdfImageXObject img = findImageXObjectByName(xobjects, new PdfName(objectid));

            if (img == null) {
                throw new IllegalArgumentException("Image reference " + objectid
                        + " not found - Available keys are " + xobjects.keySet());
            }


            byte[] result = img.getImageBytes(properties);

            Assertions.assertEquals(expectedImageFormat, img.identifyImageFileExtension(properties));

            if (saveResult) {
                Files.write(Paths.get(
                                SOURCE_FOLDER,
                                filename.substring(0, filename.length() - 4) + compareFileMarker + ".new."
                                        + expectedImageFormat),
                        result);
            }

            byte[] cmpBytes = Files.readAllBytes(Paths.get(
                    SOURCE_FOLDER, filename.substring(0, filename.length() - 4) + compareFileMarker + "." + expectedImageFormat));
            if (img.identifyImageFileExtension().equals("tif")) {
                compareTiffImages(cmpBytes, result);
            } else {
                Assertions.assertArrayEquals(cmpBytes, result);
            }
        }
    }


    private PdfImageXObject findImageXObjectByName(PdfDictionary xobjects, PdfName targetName) {
        if (xobjects == null) {
            return null;
        }

        for (PdfName name : xobjects.keySet()) {
            PdfObject obj = xobjects.get(name);
            if (obj == null) continue;

            if (obj.isIndirectReference()) {
                obj = ((PdfIndirectReference) obj).getRefersTo();
            }

            if (!(obj instanceof PdfStream)) continue;
            PdfStream stream = (PdfStream) obj;
            PdfName subtype = stream.getAsName(PdfName.Subtype);

            if (PdfName.Image.equals(subtype) && name.equals(targetName)) {
                return new PdfImageXObject(stream);
            }

            if (PdfName.Form.equals(subtype)) {
                PdfDictionary innerResources = stream.getAsDictionary(PdfName.Resources);
                if (innerResources != null) {
                    PdfDictionary innerXObjects = innerResources.getAsDictionary(PdfName.XObject);
                    PdfImageXObject result = findImageXObjectByName(innerXObjects, targetName);
                    if (result != null) return result;
                }
            }
        }

        return null;
    }

    private void compareTiffImages(byte[] cmpBytes, byte[] resultBytes) throws IOException {
        int cmpNumDirectories = TIFFDirectory.getNumDirectories(new RandomAccessFileOrArray(
                new RandomAccessSourceFactory().createSource(cmpBytes)));
        int resultNumDirectories = TIFFDirectory.getNumDirectories(new RandomAccessFileOrArray(
                new RandomAccessSourceFactory().createSource(resultBytes)));

        Assertions.assertEquals(cmpNumDirectories, resultNumDirectories);

        for (int dirNum = 0; dirNum < cmpNumDirectories; ++dirNum) {
            TIFFDirectory cmpDir = new TIFFDirectory(new RandomAccessFileOrArray(
                    new RandomAccessSourceFactory().createSource(cmpBytes)), dirNum);
            TIFFDirectory resultDir = new TIFFDirectory(new RandomAccessFileOrArray(
                    new RandomAccessSourceFactory().createSource(resultBytes)), dirNum);

            Assertions.assertEquals(cmpDir.getNumEntries(), resultDir.getNumEntries());
            Assertions.assertEquals(cmpDir.getIFDOffset(), resultDir.getIFDOffset());
            Assertions.assertEquals(cmpDir.getNextIFDOffset(), resultDir.getNextIFDOffset());
            Assertions.assertArrayEquals(cmpDir.getTags(), resultDir.getTags());

            for (int tag : cmpDir.getTags()) {
                Assertions.assertEquals(cmpDir.isTagPresent(tag), resultDir.isTagPresent(tag));

                TIFFField cmpField = cmpDir.getField(tag);
                TIFFField resultField = resultDir.getField(tag);

                if (tag != TIFFConstants.TIFFTAG_SOFTWARE) {
                    compareFields(cmpField, resultField);
                }
            }

            compareImageData(cmpDir, resultDir, cmpBytes, resultBytes);
        }
    }

    private void compareFields(TIFFField cmpField, TIFFField resultField) {
        if (cmpField.getType() == TIFFField.TIFF_LONG) {
            Assertions.assertArrayEquals(cmpField.getAsLongs(), resultField.getAsLongs());
        } else if (cmpField.getType() == TIFFField.TIFF_BYTE) {
            Assertions.assertArrayEquals(cmpField.getAsBytes(), resultField.getAsBytes());
        } else if (cmpField.getType() == TIFFField.TIFF_SBYTE) {
            Assertions.assertArrayEquals(cmpField.getAsBytes(), resultField.getAsBytes());
        } else if (cmpField.getType() == TIFFField.TIFF_SHORT) {
            Assertions.assertArrayEquals(cmpField.getAsChars(), resultField.getAsChars());
        } else if (cmpField.getType() == TIFFField.TIFF_SLONG) {
            Assertions.assertArrayEquals(cmpField.getAsInts(), resultField.getAsInts());
        } else if (cmpField.getType() == TIFFField.TIFF_SSHORT) {
            Assertions.assertArrayEquals(cmpField.getAsChars(), resultField.getAsChars());
        } else if (cmpField.getType() == TIFFField.TIFF_UNDEFINED) {
            Assertions.assertArrayEquals(cmpField.getAsBytes(), resultField.getAsBytes());
        } else if (cmpField.getType() == TIFFField.TIFF_DOUBLE) {
            Assertions.assertArrayEquals(cmpField.getAsDoubles(), resultField.getAsDoubles(), 0);
        } else if (cmpField.getType() == TIFFField.TIFF_FLOAT) {
            Assertions.assertArrayEquals(cmpField.getAsFloats(), resultField.getAsFloats(), 0);
        } else if (cmpField.getType() == TIFFField.TIFF_RATIONAL) {
            Assertions.assertArrayEquals(cmpField.getAsRationals(), resultField.getAsRationals());
        } else if (cmpField.getType() == TIFFField.TIFF_SRATIONAL) {
            Assertions.assertArrayEquals(cmpField.getAsSRationals(), resultField.getAsSRationals());
        } else if (cmpField.getType() == TIFFField.TIFF_ASCII) {
            Assertions.assertArrayEquals(cmpField.getAsStrings(), resultField.getAsStrings());
        } else {
            Assertions.assertArrayEquals(cmpField.getAsBytes(), resultField.getAsBytes());
        }
    }

    private void compareImageData(TIFFDirectory cmpDir, TIFFDirectory resultDir, byte[] cmpBytes, byte[] resultBytes) {
        Assertions.assertTrue(cmpDir.isTagPresent(TIFFConstants.TIFFTAG_STRIPOFFSETS));
        Assertions.assertTrue(cmpDir.isTagPresent(TIFFConstants.TIFFTAG_STRIPBYTECOUNTS));
        Assertions.assertTrue(resultDir.isTagPresent(TIFFConstants.TIFFTAG_STRIPOFFSETS));
        Assertions.assertTrue(resultDir.isTagPresent(TIFFConstants.TIFFTAG_STRIPBYTECOUNTS));

        long[] cmpImageOffsets = cmpDir.getField(TIFFConstants.TIFFTAG_STRIPOFFSETS).getAsLongs();
        long[] cmpStripByteCountsArray = cmpDir.getField(TIFFConstants.TIFFTAG_STRIPOFFSETS).getAsLongs();
        long[] resultImageOffsets = resultDir.getField(TIFFConstants.TIFFTAG_STRIPOFFSETS).getAsLongs();
        long[] resultStripByteCountsArray = resultDir.getField(TIFFConstants.TIFFTAG_STRIPOFFSETS).getAsLongs();

        Assertions.assertEquals(cmpImageOffsets.length, resultImageOffsets.length);
        Assertions.assertEquals(cmpStripByteCountsArray.length, resultStripByteCountsArray.length);

        for (int i = 0; i < cmpImageOffsets.length; ++i) {
            int cmpOffset = (int) cmpImageOffsets[i], cmpCounts = (int) cmpStripByteCountsArray[i];
            int resultOffset = (int) resultImageOffsets[i], resultCounts = (int) resultStripByteCountsArray[i];

            Assertions.assertArrayEquals(subArray(cmpBytes, cmpOffset, (cmpOffset + cmpCounts - 1)),
                    subArray(resultBytes, resultOffset, (resultOffset + resultCounts - 1)));
        }
    }

    private byte[] subArray(byte[] array, int beg, int end) {
        return Arrays.copyOfRange(array, beg, end + 1);
    }

    private static class ImageAndTypeExtractor implements IEventListener {
        public final java.util.List<Tuple2<String, byte[]>> images = new ArrayList<>();


        public void eventOccurred(IEventData data, EventType type) {
            switch (type) {
                case RENDER_IMAGE:
                    ImageRenderInfo renderInfo = (ImageRenderInfo) data;
                    byte[] bytes = renderInfo.getImage().getImageBytes();
                    images.add(new Tuple2<>( renderInfo.getImage().identifyImageFileExtension(), bytes));
                    break;
                default:
                    break;
            }
        }

        public Set<EventType> getSupportedEvents() {
            return null;
        }

        public List<Tuple2<String, byte[]>> getImages() {
            return images;
        }
    }
}
