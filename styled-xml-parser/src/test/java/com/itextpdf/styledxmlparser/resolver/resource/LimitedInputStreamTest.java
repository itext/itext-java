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
package com.itextpdf.styledxmlparser.resolver.resource;

import com.itextpdf.styledxmlparser.StyledXmlParserExceptionMessage;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class LimitedInputStreamTest extends ExtendedITextTest {
    private final String baseUri = "./src/test/resources/com/itextpdf/styledxmlparser/resolver/retrieveStreamTest/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void readingByteAfterFileReadingTest() throws IOException {
        UriResolver uriResolver = new UriResolver(baseUri);
        URL url = uriResolver.resolveAgainstBaseUri("retrieveStyleSheetTest.css.dat");
        // retrieveStyleSheetTest.css.dat size is 89 bytes
        InputStream stream = new LimitedInputStream(url.openStream(), 100);
        // The user can call the reading methods as many times as he want, and if the
        // stream has been read, then should not throw an ReadingByteLimitException exception
        for (int i = 0; i < 101; i++) {
            stream.read();
        }
    }

    @Test
    public void readingByteArrayAfterFileReadingTest() throws IOException {
        UriResolver uriResolver = new UriResolver(baseUri);
        URL url = uriResolver.resolveAgainstBaseUri("retrieveStyleSheetTest.css.dat");
        // retrieveStyleSheetTest.css.dat size is 89 bytes
        InputStream stream = new LimitedInputStream(url.openStream(), 100);
        // The user can call the reading methods as many times as he want, and if the
        // stream has been read, then should not throw an ReadingByteLimitException exception
        stream.read(new byte[100]);
        stream.read(new byte[1]);
    }

    @Test
    public void readingByteArrayWithOffsetAfterFileReadingTest() throws IOException {
        UriResolver uriResolver = new UriResolver(baseUri);
        URL url = uriResolver.resolveAgainstBaseUri("retrieveStyleSheetTest.css.dat");
        // retrieveStyleSheetTest.css.dat size is 89 bytes
        InputStream stream = new LimitedInputStream(url.openStream(), 100);
        // The user can call the reading methods as many times as he want, and if the
        // stream has been read, then should not throw an ReadingByteLimitException exception
        stream.read(new byte[100], 0, 100);
        stream.read(new byte[1], 0, 1);
    }

    @Test
    public void readingByteWithLimitOfOneLessThenFileSizeTest() throws IOException {
        UriResolver uriResolver = new UriResolver(baseUri);
        URL url = uriResolver.resolveAgainstBaseUri("retrieveStyleSheetTest.css.dat");
        // retrieveStyleSheetTest.css.dat size is 89 bytes
        InputStream stream = new LimitedInputStream(url.openStream(), 88);
        for (int i = 0; i < 88; i++) {
            Assert.assertNotEquals(-1, stream.read());
        }
        junitExpectedException.expect(ReadingByteLimitException.class);
        stream.read();
    }

    @Test
    public void readingByteArrayWithLimitOfOneLessThenFileSizeTest() throws IOException {
        UriResolver uriResolver = new UriResolver(baseUri);
        URL url = uriResolver.resolveAgainstBaseUri("retrieveStyleSheetTest.css.dat");
        // retrieveStyleSheetTest.css.dat size is 89 bytes
        InputStream stream = new LimitedInputStream(url.openStream(), 88);
        byte[] bytes = new byte[100];
        int numOfReadBytes = stream.read(bytes);
        Assert.assertEquals(88, numOfReadBytes);
        Assert.assertEquals(10, bytes[87]);
        Assert.assertEquals(0, bytes[88]);
        junitExpectedException.expect(ReadingByteLimitException.class);
        stream.read(new byte[1]);
    }

    @Test
    public void readingByteArrayWithOffsetAndLimitOfOneLessThenFileSizeTest() throws IOException {
        UriResolver uriResolver = new UriResolver(baseUri);
        URL url = uriResolver.resolveAgainstBaseUri("retrieveStyleSheetTest.css.dat");
        // retrieveStyleSheetTest.css.dat size is 89 bytes
        InputStream stream = new LimitedInputStream(url.openStream(), 88);
        byte[] bytes = new byte[100];
        int numOfReadBytes = stream.read(bytes, 0, 88);
        Assert.assertEquals(88, numOfReadBytes);
        Assert.assertEquals(10, bytes[87]);
        Assert.assertEquals(0, bytes[88]);
        junitExpectedException.expect(ReadingByteLimitException.class);
        stream.read(bytes, 88, 1);
    }

    @Test
    public void readingByteArrayWithSmallBufferTest() throws IOException {
        UriResolver uriResolver = new UriResolver(baseUri);
        URL url = uriResolver.resolveAgainstBaseUri("retrieveStyleSheetTest.css.dat");
        // retrieveStyleSheetTest.css.dat size is 89 bytes
        InputStream stream = new LimitedInputStream(url.openStream(), 89);
        byte[] bytes = new byte[20];
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while (true) {
            int read = stream.read(bytes);
            if (read < 1) {
                break;
            }
            output.write(bytes, 0, read);
        }
        Assert.assertEquals(89, output.size());
        output.close();
    }

    @Test
    public void readingByteArrayWithBigBufferTest() throws IOException {
        UriResolver uriResolver = new UriResolver(baseUri);
        URL url = uriResolver.resolveAgainstBaseUri("retrieveStyleSheetTest.css.dat");
        // retrieveStyleSheetTest.css.dat size is 89 bytes
        InputStream stream = new LimitedInputStream(url.openStream(), 89);
        byte[] bytes = new byte[100];

        Assert.assertEquals(89, stream.read(bytes));
        byte[] tempBytes = (byte[]) bytes.clone();
        Assert.assertEquals(-1, stream.read(bytes));
        // Check that the array has not changed when we have read the entire LimitedInputStream
        Assert.assertArrayEquals(tempBytes, bytes);
    }

    @Test
    public void readingByteArrayWithOffsetAndBigBufferTest() throws IOException {
        UriResolver uriResolver = new UriResolver(baseUri);
        URL url = uriResolver.resolveAgainstBaseUri("retrieveStyleSheetTest.css.dat");
        // retrieveStyleSheetTest.css.dat size is 89 bytes
        InputStream stream = new LimitedInputStream(url.openStream(), 89);
        byte[] bytes = new byte[100];

        Assert.assertEquals(89, stream.read(bytes, 0, 100));
        byte[] tempBytes = (byte[]) bytes.clone();
        Assert.assertEquals(-1, stream.read(bytes, 0, 100));
        // Check that the array has not changed when we have read the entire LimitedInputStream
        Assert.assertArrayEquals(tempBytes, bytes);
    }

    @Test
    public void byteArrayOverwritingTest() throws IOException {
        UriResolver uriResolver = new UriResolver(baseUri);
        URL url = uriResolver.resolveAgainstBaseUri("retrieveStyleSheetTest.css.dat");
        // retrieveStyleSheetTest.css.dat size is 89 bytes
        InputStream stream = new LimitedInputStream(url.openStream(), 90);
        byte[] bytes = new byte[100];
        bytes[89] = 13;

        Assert.assertEquals(89, stream.read(bytes));
        // Check that when calling the read(byte[]) method, as many bytes were copied into
        // the original array as were read, and not all bytes from the auxiliary array.
        Assert.assertEquals(13, bytes[89]);
    }

    @Test
    public void readingByteWithZeroLimitTest() throws IOException {
        LimitedInputStream stream = new LimitedInputStream(new ByteArrayInputStream(new byte[1]), 0);
        junitExpectedException.expect(ReadingByteLimitException.class);
        stream.read();
    }

    @Test
    public void readingByteArrayWithZeroLimitTest() throws IOException {
        LimitedInputStream stream = new LimitedInputStream(new ByteArrayInputStream(new byte[1]), 0);
        byte[] bytes = new byte[100];
        junitExpectedException.expect(ReadingByteLimitException.class);
        stream.read(bytes);
    }

    @Test
    public void readingByteArrayWithOffsetAndZeroLimitTest() throws IOException {
        LimitedInputStream stream = new LimitedInputStream(new ByteArrayInputStream(new byte[1]), 0);
        byte[] bytes = new byte[100];
        junitExpectedException.expect(ReadingByteLimitException.class);
        stream.read(bytes, 0, 100);
    }

    @Test
    public void readingEmptyByteWithZeroLimitTest() throws IOException {
        LimitedInputStream stream = new LimitedInputStream(new ByteArrayInputStream(new byte[0]), 0);
        Assert.assertEquals(-1, stream.read());
    }

    @Test
    public void readingEmptyByteArrayWithZeroLimitTest() throws IOException {
        LimitedInputStream stream = new LimitedInputStream(new ByteArrayInputStream(new byte[0]), 0);
        byte[] bytes = new byte[100];
        Assert.assertEquals(-1, stream.read(bytes));
    }

    @Test
    public void readingEmptyByteArrayWithOffsetAndZeroLimitTest() throws IOException {
        LimitedInputStream stream = new LimitedInputStream(new ByteArrayInputStream(new byte[0]), 0);
        byte[] bytes = new byte[100];
        Assert.assertEquals(-1, stream.read(bytes, 0, 100));
    }

    @Test
    public void illegalReadingByteLimitValueTest() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage(StyledXmlParserExceptionMessage.READING_BYTE_LIMIT_MUST_NOT_BE_LESS_ZERO);
        new LimitedInputStream(new ByteArrayInputStream(new byte[0]), -1);
    }
}
