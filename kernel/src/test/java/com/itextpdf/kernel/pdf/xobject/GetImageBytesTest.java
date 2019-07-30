/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf.xobject;

import com.itextpdf.io.codec.TIFFConstants;
import com.itextpdf.io.codec.TIFFDirectory;
import com.itextpdf.io.codec.TIFFField;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.Version;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class GetImageBytesTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/xobject/GetImageBytesTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/xobject/GetImageBytesTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void testMultiStageFilters() throws Exception {
        // TODO DEVSIX-2940: extracted image is blank
        testFile("multistagefilter1.pdf", "Obj13", "jpg");
    }

    @Test
    public void testAscii85Filters() throws Exception {
        testFile("ASCII85_RunLengthDecode.pdf", "Im9", "png");
    }

    @Test
    public void testCcittFilters() throws Exception {
        testFile("ccittfaxdecode.pdf", "background0", "png");
    }

    @Test
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
    public void testFlateRgbIcc() throws Exception {
        testFile("img_rgb_icc.pdf", "Im1", "png");
    }

    @Test
    public void testFlateRgb() throws Exception {
        testFile("img_rgb.pdf", "Im1", "png");
    }

    @Test
    public void testFlateCalRgb() throws Exception {
        testFile("img_calrgb.pdf", "Im1", "png");
    }

    private void testFile(String filename, String objectid, String expectedImageFormat) throws Exception {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + filename));
        try {
            PdfResources resources = pdfDocument.getPage(1).getResources();
            PdfDictionary xobjects = resources.getResource(PdfName.XObject);
            PdfObject obj = xobjects.get(new PdfName(objectid));
            if (obj == null) {
                throw new IllegalArgumentException("Reference " + objectid + " not found - Available keys are " + xobjects.keySet());
            }
            PdfImageXObject img = new PdfImageXObject((PdfStream) (obj.isIndirectReference() ? ((PdfIndirectReference) obj).getRefersTo() : obj));
            Assert.assertEquals(expectedImageFormat, img.identifyImageFileExtension());


            byte[] result = img.getImageBytes(true);
            byte[] cmpBytes = Files.readAllBytes(Paths.get(sourceFolder, filename.substring(0, filename.length() - 4) + "." + expectedImageFormat));

            if (img.identifyImageFileExtension().equals("tif")) {
                compareTiffImages(cmpBytes, result);
            } else {
                Assert.assertArrayEquals(cmpBytes, result);
            }
        } finally {
            pdfDocument.close();
        }
    }

    private void compareTiffImages(byte[] cmpBytes, byte[] resultBytes) throws IOException {
        int cmpNumDirectories = TIFFDirectory.getNumDirectories(new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(cmpBytes)));
        int resultNumDirectories = TIFFDirectory.getNumDirectories(new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(resultBytes)));

        Assert.assertEquals(cmpNumDirectories, resultNumDirectories);

        for (int dirNum = 0; dirNum < cmpNumDirectories; ++dirNum) {
            TIFFDirectory cmpDir = new TIFFDirectory(new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(cmpBytes)), dirNum);
            TIFFDirectory resultDir = new TIFFDirectory(new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(resultBytes)), dirNum);

            Assert.assertEquals(cmpDir.getNumEntries(), resultDir.getNumEntries());
            Assert.assertEquals(cmpDir.getIFDOffset(), resultDir.getIFDOffset());
            Assert.assertEquals(cmpDir.getNextIFDOffset(), resultDir.getNextIFDOffset());
            Assert.assertArrayEquals(cmpDir.getTags(), resultDir.getTags());

            for (int tag : cmpDir.getTags()) {
                Assert.assertEquals(cmpDir.isTagPresent(tag), resultDir.isTagPresent(tag));

                TIFFField cmpField = cmpDir.getField(tag);
                TIFFField resultField = resultDir.getField(tag);

                if (tag == TIFFConstants.TIFFTAG_SOFTWARE) {
                    compareSoftwareVersion(cmpField, resultField);
                } else {
                    compareFields(cmpField, resultField);
                }
            }

            compareImageData(cmpDir, resultDir, cmpBytes, resultBytes);
        }
    }

    private void compareSoftwareVersion(TIFFField cmpField, TIFFField resultField) {
        byte[] versionBytes = resultField.getAsString(0).getBytes(StandardCharsets.US_ASCII);
        byte[] versionToCompare = subArray(versionBytes, 0, versionBytes.length - 2); //drop last always zero byte

        Assert.assertArrayEquals(Version.getInstance().getVersion().getBytes(StandardCharsets.US_ASCII), versionToCompare);
    }

    private void compareFields(TIFFField cmpField, TIFFField resultField) {
        if (cmpField.getType() == TIFFField.TIFF_LONG) {
            Assert.assertArrayEquals(cmpField.getAsLongs(), resultField.getAsLongs());
        } else if (cmpField.getType() == TIFFField.TIFF_BYTE) {
            Assert.assertArrayEquals(cmpField.getAsBytes(), resultField.getAsBytes());
        } else if (cmpField.getType() == TIFFField.TIFF_SBYTE) {
            Assert.assertArrayEquals(cmpField.getAsBytes(), resultField.getAsBytes());
        } else if (cmpField.getType() == TIFFField.TIFF_SHORT) {
            Assert.assertArrayEquals(cmpField.getAsChars(), resultField.getAsChars());
        } else if (cmpField.getType() == TIFFField.TIFF_SLONG) {
            Assert.assertArrayEquals(cmpField.getAsInts(), resultField.getAsInts());
        } else if (cmpField.getType() == TIFFField.TIFF_SSHORT) {
            Assert.assertArrayEquals(cmpField.getAsChars(), resultField.getAsChars());
        } else if (cmpField.getType() == TIFFField.TIFF_UNDEFINED) {
            Assert.assertArrayEquals(cmpField.getAsBytes(), resultField.getAsBytes());
        } else if (cmpField.getType() == TIFFField.TIFF_DOUBLE) {
            Assert.assertArrayEquals(cmpField.getAsDoubles(), resultField.getAsDoubles(), 0);
        } else if (cmpField.getType() == TIFFField.TIFF_FLOAT) {
            Assert.assertArrayEquals(cmpField.getAsFloats(), resultField.getAsFloats(), 0);
        } else if (cmpField.getType() == TIFFField.TIFF_RATIONAL) {
            Assert.assertArrayEquals(cmpField.getAsRationals(), resultField.getAsRationals());
        } else if (cmpField.getType() == TIFFField.TIFF_SRATIONAL) {
            Assert.assertArrayEquals(cmpField.getAsSRationals(), resultField.getAsSRationals());
        } else if (cmpField.getType() == TIFFField.TIFF_ASCII) {
            Assert.assertArrayEquals(cmpField.getAsStrings(), resultField.getAsStrings());
        } else {
            Assert.assertArrayEquals(cmpField.getAsBytes(), resultField.getAsBytes());
        }
    }

    private void compareImageData(TIFFDirectory cmpDir, TIFFDirectory resultDir, byte[] cmpBytes, byte[] resultBytes) {
        Assert.assertTrue(cmpDir.isTagPresent(TIFFConstants.TIFFTAG_STRIPOFFSETS));
        Assert.assertTrue(cmpDir.isTagPresent(TIFFConstants.TIFFTAG_STRIPBYTECOUNTS));
        Assert.assertTrue(resultDir.isTagPresent(TIFFConstants.TIFFTAG_STRIPOFFSETS));
        Assert.assertTrue(resultDir.isTagPresent(TIFFConstants.TIFFTAG_STRIPBYTECOUNTS));

        long[] cmpImageOffsets = cmpDir.getField(TIFFConstants.TIFFTAG_STRIPOFFSETS).getAsLongs();
        long[] cmpStripByteCountsArray = cmpDir.getField(TIFFConstants.TIFFTAG_STRIPOFFSETS).getAsLongs();
        long[] resultImageOffsets = resultDir.getField(TIFFConstants.TIFFTAG_STRIPOFFSETS).getAsLongs();
        long[] resultStripByteCountsArray = resultDir.getField(TIFFConstants.TIFFTAG_STRIPOFFSETS).getAsLongs();

        Assert.assertEquals(cmpImageOffsets.length, resultImageOffsets.length);
        Assert.assertEquals(cmpStripByteCountsArray.length, resultStripByteCountsArray.length);

        for (int i = 0; i < cmpImageOffsets.length; ++i) {
            int cmpOffset = (int) cmpImageOffsets[i], cmpCounts = (int) cmpStripByteCountsArray[i];
            int resultOffset = (int) resultImageOffsets[i], resultCounts = (int) resultStripByteCountsArray[i];

            Assert.assertArrayEquals(subArray(cmpBytes, cmpOffset, (cmpOffset + cmpCounts - 1)),
                    subArray(resultBytes, resultOffset, (resultOffset + resultCounts - 1)));
        }
    }

    private byte[] subArray(byte[] array, int beg, int end) {
        return Arrays.copyOfRange(array, beg, end + 1);
    }
}
