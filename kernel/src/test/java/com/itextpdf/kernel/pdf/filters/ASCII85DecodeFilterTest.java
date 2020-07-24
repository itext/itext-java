package com.itextpdf.kernel.pdf.filters;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ASCII85DecodeFilterTest extends ExtendedITextTest {
    public static final String SOURCE_FILE =
            "./src/test/resources/com/itextpdf/kernel/pdf/filters/ASCII85.bin";

    @Test
    public void decodingTest() throws IOException {
        File file = new File(SOURCE_FILE);
        byte[] bytes = Files.readAllBytes(file.toPath());

        String expectedResult = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
                + "Donec ac malesuada tellus. "
                + "Quisque a arcu semper, tristique nibh eu, convallis lacus. "
                + "Donec neque justo, condimentum sed molestie ac, mollis eu nibh. "
                + "Vivamus pellentesque condimentum fringilla. "
                + "Nullam euismod ac risus a semper. "
                + "Etiam hendrerit scelerisque sapien tristique varius.";

        ASCII85DecodeFilter filter = new ASCII85DecodeFilter();
        String decoded = new String(filter.decode(bytes, null, null, new PdfDictionary()));

        Assert.assertEquals(expectedResult, decoded);
    }

    @Test
    public void decodingWithZeroBytesTest() {
        byte[] bytes = "z9Q+r_D#".getBytes();

        String expectedResult = new String(new byte[]{0,0,0,0,(byte)'L',(byte)'o',(byte)'r',(byte)'e',(byte)'m'});

        ASCII85DecodeFilter filter = new ASCII85DecodeFilter();
        String decoded = new String(filter.decode(bytes, null, null, new PdfDictionary()));

        Assert.assertEquals(expectedResult, decoded);
    }
}
