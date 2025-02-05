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

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.codec.TIFFConstants;
import com.itextpdf.io.codec.TIFFDirectory;
import com.itextpdf.io.codec.TIFFField;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
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
import com.itextpdf.test.ExtendedITextTest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;


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
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6445 fix different DeflaterOutputStream behavior)
    public void testAscii85Filters() throws Exception {
        testFile("ASCII85_RunLengthDecode.pdf", "Im9", "png");
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6445 fix different DeflaterOutputStream behavior)
    public void testCcittFilters() throws Exception {
        testFile("ccittfaxdecode.pdf", "background0", "png");
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6445 fix different DeflaterOutputStream behavior)
    public void testFlateDecodeFilters() throws Exception {
        // TODO DEVSIX-2941: extracted indexed devicegray RunLengthDecode gets color inverted
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
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6445 fix different DeflaterOutputStream behavior)
    public void testFlateRgbIcc() throws Exception {
        testFile("img_rgb_icc.pdf", "Im1", "png");
    }

    @Test
    public void testFlateRgb() throws Exception {
        testFile("img_rgb.pdf", "Im1", "png");
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6445 fix different DeflaterOutputStream behavior)
    public void testFlateCalRgb() throws Exception {
        testFile("img_calrgb.pdf", "Im1", "png");
    }

    @Test
    public void testJPXDecode() throws Exception {
        testFile("JPXDecode.pdf", "Im1", "jp2");
    }

    @Test
    // TODO: DEVSIX-3538 (update test after fix)
    // Android-Conversion-Ignore-Test (TODO DEVSIX-7079 increase memory available for virtual machine while test running)
    public void testSeparationCSWithICCBasedAsAlternative() throws Exception {
        testFile("separationCSWithICCBasedAsAlternative.pdf", "Im1", "png");
    }

    @Test
    // TODO: DEVSIX-3538 (update test after fix)
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6445 fix different DeflaterOutputStream behavior)
    public void testSeparationCSWithDeviceCMYKAsAlternative() throws Exception {
        Assertions.assertThrows(UnsupportedOperationException.class, () ->
        {
            testFile("separationCSWithDeviceCMYKAsAlternative.pdf", "Im1", "png");
        });
    }

    @Test
    public void testGrayScalePng() throws Exception {
        testFile("grayImages.pdf", "Im1", "png");
    }

    @Test
    // TODO: DEVSIX-3538 (update test after fix)
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6445 fix different DeflaterOutputStream behavior)
    public void testSeparationCSWithDeviceRGBAsAlternative() throws Exception {
        testFile("separationCSWithDeviceRgbAsAlternative.pdf", "Im1", "png");
    }

    @Test
    // TODO: DEVSIX-3538 (update test after fix)
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6445 fix different DeflaterOutputStream behavior)
    public void testSeparationCSWithDeviceRGBAsAlternative2() throws Exception {
        testFile("spotColorImagesSmall.pdf", "Im1", "png");
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
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6445 fix different DeflaterOutputStream behavior)
    public void testRGBSeparationCSWithFlateDecoderAndFunctionType0() throws Exception {
        testFile("RGBFlateF0.pdf", "Im1", "png");
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
    public void testRGBSeparationCSWithFlateDecoderAndFunctionType2() throws Exception {
        testFile("RGBFlateF2.pdf", "Im1", "png");
    }

    @Test
    public void extractByteAlignedG4TiffImageTest() throws IOException {
        String inFileName = SOURCE_FOLDER + "extractByteAlignedG4TiffImage.pdf";
        String outImageFileName = DESTINATION_FOLDER + "extractedByteAlignedImage.png";
        String cmpImageFileName = SOURCE_FOLDER + "cmp_extractByteAlignedG4TiffImage.png";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFileName));

        ImageExtractor listener = new ImageExtractor();
        PdfCanvasProcessor processor = new PdfCanvasProcessor(listener);
        processor.processPageContent(pdfDocument.getPage(1));

        java.util.List<byte[]> images = listener.getImages();
        Assertions.assertEquals(1, images.size());

        try (OutputStream fos = FileUtil.getFileOutputStream(outImageFileName)) {
            fos.write(images.get(0), 0, images.size());
        }

        // expected and actual are swapped here for simplicity
        int expectedLen = images.get(0).length;
        byte[] buf = new byte[expectedLen];
        try (InputStream is = FileUtil.getInputStreamForFile(cmpImageFileName)) {
            int read = is.read(buf, 0, buf.length);
            Assertions.assertEquals(expectedLen, read);
            read = is.read(buf, 0, buf.length);
            Assertions.assertTrue(read <= 0);
        }
        Assertions.assertArrayEquals(images.get(0), buf);
    }

    @Test
    public void expectedByteAlignedTiffImageExtractionTest() throws IOException {
        //Byte-aligned image is expected in pdf file, but in fact it's not
        String inFileName = SOURCE_FOLDER + "expectedByteAlignedTiffImageExtraction.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFileName));

        ImageExtractor listener = new ImageExtractor();
        PdfCanvasProcessor processor = new PdfCanvasProcessor(listener);

        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> processor.processPageContent(pdfDocument.getPage(1))
        );
        Assertions.assertEquals(MessageFormatUtil
                        .format(IoExceptionMessageConstant.EXPECTED_TRAILING_ZERO_BITS_FOR_BYTE_ALIGNED_LINES),
                e.getMessage());
    }

    private void testFile(String filename, String objectid, String expectedImageFormat) throws Exception {
        testFile(filename, objectid, expectedImageFormat, false);
    }

    private void testFile(String filename, String objectid, String expectedImageFormat, boolean saveResult)
            throws Exception {
        try (PdfReader reader = new PdfReader(SOURCE_FOLDER + filename);
                PdfDocument pdfDocument = new PdfDocument(reader)) {
            PdfResources resources = pdfDocument.getPage(1).getResources();
            PdfDictionary xobjects = resources.getResource(PdfName.XObject);
            PdfObject obj = xobjects.get(new PdfName(objectid));
            if (obj == null) {
                throw new IllegalArgumentException("Reference " + objectid
                        + " not found - Available keys are " + xobjects.keySet());
            }

            PdfImageXObject img = new PdfImageXObject((PdfStream) obj);

            Assertions.assertEquals(expectedImageFormat, img.identifyImageFileExtension());

            byte[] result = img.getImageBytes(true);
            if (saveResult) {
                Files.write(Paths.get(
                                SOURCE_FOLDER,
                                filename.substring(0, filename.length() - 4) + ".new." + expectedImageFormat),
                        result);
            }
            byte[] cmpBytes = Files.readAllBytes(Paths.get(
                    SOURCE_FOLDER, filename.substring(0, filename.length() - 4) + "." + expectedImageFormat));
            if (img.identifyImageFileExtension().equals("tif")) {
                compareTiffImages(cmpBytes, result);
            } else {
                Assertions.assertArrayEquals(cmpBytes, result);
            }
        }
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

    private class ImageExtractor implements IEventListener {
        private final java.util.List<byte[]> images = new ArrayList<>();

        public void eventOccurred(IEventData data, EventType type) {
            switch (type) {
                case RENDER_IMAGE:
                    ImageRenderInfo renderInfo = (ImageRenderInfo) data;
                    byte[] bytes = renderInfo.getImage().getImageBytes();
                    images.add(bytes);
                    break;
                default:
                    break;
            }
        }

        public Set<EventType> getSupportedEvents() {
            return null;
        }

        public java.util.List<byte[]> getImages() {
            return images;
        }
    }
}
