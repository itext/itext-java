package com.itextpdf.kernel.pdf.filters;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class ASCIIHexDecodeFilterTest extends ExtendedITextTest {
    public static final String SOURCE_FILE =
            "./src/test/resources/com/itextpdf/kernel/pdf/filters/ASCIIHex.bin";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

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

        String decoded = new String(ASCIIHexDecodeFilter.ASCIIHexDecode(bytes));
        Assert.assertEquals(expectedResult, decoded);
    }

    @Test
    public void decodingIllegalaCharacterTest() {
        byte[] bytes = "4c6f72656d20697073756d2eg>".getBytes();
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(PdfException.IllegalCharacterInAsciihexdecode);
        ASCIIHexDecodeFilter.ASCIIHexDecode(bytes);
    }

    @Test
    public void decodingSkipWhitespacesTest() {
        byte[] bytes = "4c 6f 72 65 6d 20 69 70 73 75 6d 2e>".getBytes();
        String expectedResult = "Lorem ipsum.";

        String decoded = new String(ASCIIHexDecodeFilter.ASCIIHexDecode(bytes));
        Assert.assertEquals(expectedResult, decoded);
    }
}
