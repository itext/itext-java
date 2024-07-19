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
package com.itextpdf.io.source;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.source.PdfTokenizer.TokenType;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import static com.itextpdf.io.exceptions.IoExceptionMessageConstant.ERROR_AT_FILE_POINTER;

@Category(UnitTest.class)
public class PdfTokenizerTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/io/util/";

    @Test
    public void seekTest() throws IOException {
        String data = "/Name1 70";
        TokenType[] expectedTypes = new TokenType[] {TokenType.Name, TokenType.Number, TokenType.EndOfFile};

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(
                factory.createSource(data.getBytes(StandardCharsets.ISO_8859_1))));

        tok.seek(0);
        tok.nextValidToken();
        Assert.assertEquals(expectedTypes[0], tok.getTokenType());
        Assert.assertEquals("Name1", tok.getStringValue());

        tok.seek(7);
        tok.nextValidToken();
        Assert.assertEquals(expectedTypes[1], tok.getTokenType());
        Assert.assertEquals("70", tok.getStringValue());

        tok.seek(8);
        tok.nextValidToken();
        Assert.assertEquals(expectedTypes[1], tok.getTokenType());
        Assert.assertEquals("0", tok.getStringValue());

        tok.seek(9);
        tok.nextValidToken();
        Assert.assertEquals(expectedTypes[2], tok.getTokenType());
    }

    @Test
    public void peekTest() throws IOException {
        String data = "/Name1 70";

        PdfTokenizer tokenizer = new PdfTokenizer(new RandomAccessFileOrArray(
                new RandomAccessSourceFactory().createSource(data.getBytes(StandardCharsets.ISO_8859_1))));

        tokenizer.seek(0);
        int symbol = tokenizer.peek();
        Assert.assertEquals((int)'/', symbol);
        Assert.assertEquals(0, tokenizer.getPosition());

        tokenizer.seek(7);
        symbol = tokenizer.peek();
        Assert.assertEquals((int)'7', symbol);
        Assert.assertEquals(7, tokenizer.getPosition());

        tokenizer.seek(9);
        symbol = tokenizer.peek();
        Assert.assertEquals(-1, symbol);
        Assert.assertEquals(9, tokenizer.getPosition());

        byte[] name = new byte[6];
        tokenizer.seek(0);
        int read = tokenizer.peek(name);
        byte[] expected = "/Name1".getBytes();
        Assert.assertArrayEquals(expected, name);
        Assert.assertEquals(0, tokenizer.getPosition());
        Assert.assertEquals(6, read);

        byte[] bigBuffer = new byte[13];
        read = tokenizer.peek(bigBuffer);
        expected = new byte[] {(byte) 47, (byte) 78, (byte) 97, (byte) 109, (byte) 101, (byte) 49, (byte) 32,
                               (byte) 55, (byte) 48, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        Assert.assertArrayEquals(expected, bigBuffer);
        Assert.assertEquals(0, tokenizer.getPosition());
        Assert.assertEquals(9, read);
    }

    @Test
    public void getLongValueTest() throws IOException {
        String data = "21474836470";

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(
                factory.createSource(data.getBytes(StandardCharsets.ISO_8859_1))));

        tok.nextValidToken();
        Assert.assertEquals(TokenType.Number, tok.getTokenType());
        Assert.assertEquals(21474836470L, tok.getLongValue());
    }

    @Test
    public void getIntValueTest() throws IOException {
        String data = "15";

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(
                factory.createSource(data.getBytes(StandardCharsets.ISO_8859_1))));

        tok.nextValidToken();
        Assert.assertEquals(TokenType.Number, tok.getTokenType());
        Assert.assertEquals(15, tok.getIntValue());
    }

    @Test
    public void getPositionTest() throws IOException {
        String data = "/Name1 70";

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(
                factory.createSource(data.getBytes(StandardCharsets.ISO_8859_1))));

        Assert.assertEquals(0, tok.getPosition());
        tok.nextValidToken();
        Assert.assertEquals(6, tok.getPosition());
        tok.nextValidToken();
        Assert.assertEquals(11, tok.getPosition());
    }

    @Test
    public void lengthTest() throws IOException {
        String data = "/Name1";

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(
                factory.createSource(data.getBytes(StandardCharsets.ISO_8859_1))));

        Assert.assertEquals(6, tok.length());
    }

    @Test
    public void lengthTwoTokenTest() throws IOException {
        String data = "/Name1 15";

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(
                factory.createSource(data.getBytes(StandardCharsets.ISO_8859_1))));

        Assert.assertEquals(9, tok.length());
    }

    @Test
    public void readTest() throws IOException {
        String data = "/Name1 15";

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(
                factory.createSource(data.getBytes(StandardCharsets.ISO_8859_1))));

        byte[] read = new byte[] {
                (byte) tok.read(), (byte) tok.read(), (byte) tok.read(),
                (byte) tok.read(), (byte) tok.read(), (byte) tok.read(),
                (byte) tok.read()
        };
        Assert.assertEquals("/Name1 ", new String(read));
    }

    @Test
    public void readStringFullTest() throws IOException {
        String data = "/Name1 15";

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(
                factory.createSource(data.getBytes(StandardCharsets.ISO_8859_1))));

        Assert.assertEquals(data, tok.readString(data.length()));
    }

    @Test
    public void readStringShortTest() throws IOException {
        String data = "/Name1 15";

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(
                factory.createSource(data.getBytes(StandardCharsets.ISO_8859_1))));

        Assert.assertEquals("/Name", tok.readString(5));
    }

    @Test
    public void readStringLongerThenDataTest() throws IOException {
        String data = "/Name1 15";

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(
                factory.createSource(data.getBytes(StandardCharsets.ISO_8859_1))));

        Assert.assertEquals(data, tok.readString(data.length() + 10));
    }

    @Test
    public void readFullyPartThenReadStringTest() throws IOException {
        String data = "/Name1 15";

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(
                factory.createSource(data.getBytes(StandardCharsets.ISO_8859_1))));

        tok.readFully(new byte[6]);
        Assert.assertEquals(" 15", tok.readString(data.length()));
    }

    @Test
    public void readFullyThenReadStringTest() throws IOException {
        String data = "/Name1 15";

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(
                factory.createSource(data.getBytes(StandardCharsets.ISO_8859_1))));

        tok.readFully(new byte[7]);
        Assert.assertEquals("15", tok.readString(data.length()));
    }

    @Test
    public void getNextEofShortTextTest() throws IOException {
        String data = "some text to test \ngetting end of\n file logic%%EOF";

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        try (PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(
                factory.createSource(data.getBytes(StandardCharsets.ISO_8859_1))))) {
            long eofPosition = tok.getNextEof();
            Assert.assertEquals(data.length() + 1, eofPosition);
        }
    }

    @Test
    public void getNextEofLongTextTest() throws IOException {
        String data = "some text to test \ngetting end of\n file logic";
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 20; ++i) {
            stringBuilder.append(data);
        }
        stringBuilder.append("%%EOF");

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        try (PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(
                factory.createSource(stringBuilder.toString().getBytes(StandardCharsets.ISO_8859_1))))) {
            long eofPosition = tok.getNextEof();
            Assert.assertEquals(data.length() * 20 + 6, eofPosition);
        }
    }

    @Test
    public void getNextEofWhichIsCutTest() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        // We append 'a' 124 times because buffer has 128 bytes length.
        // This way '%%EOF' is cut and first string only contains '%%EO'
        for (int i = 0; i < 124; ++i) {
            stringBuilder.append("a");
        }
        stringBuilder.append("%%EOF");

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        try (PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(
                factory.createSource(stringBuilder.toString().getBytes(StandardCharsets.ISO_8859_1))))) {
            long eofPosition = tok.getNextEof();
            Assert.assertEquals(124 + 6, eofPosition);
        }
    }

    @Test
    public void getNextEofSeveralEofTest() throws IOException {
        String data = "some text %%EOFto test \nget%%EOFting end of\n fil%%EOFe logic%%EOF";

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        try (PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(
                factory.createSource(data.getBytes(StandardCharsets.ISO_8859_1))))) {
            long eofPosition = tok.getNextEof();
            Assert.assertEquals(data.indexOf("%%EOF") + 6, eofPosition);
        }
    }

    @Test
    public void getNextEofNoEofTest() throws IOException {
        String data = "some text to test \ngetting end of\n file logic";

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        try (PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(
                factory.createSource(data.getBytes(StandardCharsets.ISO_8859_1))))) {
            Assert.assertThrows(com.itextpdf.io.exceptions.IOException.class, () -> tok.getNextEof());
        }
    }

    @Test
    public void getDecodedStringContentTest() throws IOException {
        String data = "/Name1 15";

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(
                factory.createSource(data.getBytes(StandardCharsets.ISO_8859_1))));

        tok.nextToken();
        Assert.assertEquals("Name1", new String(tok.getDecodedStringContent()));

        tok.nextToken();
        Assert.assertEquals("15", new String(tok.getDecodedStringContent()));

        tok.nextToken();
        Assert.assertEquals("", new String(tok.getDecodedStringContent()));
    }

    @Test
    public void getDecodedStringContentHexTest() throws IOException {
        String data = "<736f6d652068657820737472696e67>";

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(
                factory.createSource(data.getBytes(StandardCharsets.ISO_8859_1))));

        tok.nextToken();
        Assert.assertTrue(tok.isHexString());
        Assert.assertEquals("some hex string", new String(tok.getDecodedStringContent()));
    }

    @Test
    public void throwErrorTest() {
        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(
                factory.createSource("/Name1".getBytes(StandardCharsets.ISO_8859_1))));

        Exception e = Assert.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () ->  tok.throwError(ERROR_AT_FILE_POINTER, 0)
        );
        Assert.assertEquals(MessageFormatUtil.format(ERROR_AT_FILE_POINTER, 0), e.getMessage());
    }

    @Test
    public void testOneNumber() throws Exception {
        checkTokenTypes("/Name1 70", TokenType.Name, TokenType.Number, TokenType.EndOfFile);
    }

    @Test
    public void testTwoNumbers() throws Exception {
        checkTokenTypes(
                "/Name1 70/Name 2",
                TokenType.Name, TokenType.Number, TokenType.Name, TokenType.Number, TokenType.EndOfFile
        );
    }

    @Test
    public void tokenTypesTest() throws Exception {
        checkTokenTypes(
                "<</Size 70/Root 46 0 R/Info 44 0 R/ID[<8C2547D58D4BD2C6F3D32B830BE3259D><8F69587888569A458EB681A4285D5879>]/Prev 116 >>",
                TokenType.StartDic, TokenType.Name, TokenType.Number, TokenType.Name, TokenType.Ref, TokenType.Name,
                TokenType.Ref, TokenType.Name, TokenType.StartArray, TokenType.String, TokenType.String,
                TokenType.EndArray, TokenType.Name, TokenType.Number, TokenType.EndDic, TokenType.EndOfFile
        );
    }

    @Test
    public void numberValueInTheEndTest() throws Exception {
        checkTokenValues(
                "123",
                new byte[]{49, 50, 51},
                //EndOfFile buffer
                new byte[]{}
        );
    }

    @Test
    public void tokenValueEqualsToTest() throws IOException {
        String data = "SomeString";

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(factory.createSource(data.getBytes(StandardCharsets.ISO_8859_1))));

        tok.nextToken();
        Assert.assertTrue(tok.tokenValueEqualsTo(data.getBytes(StandardCharsets.ISO_8859_1)));
    }

    @Test
    public void tokenValueEqualsToNullTest() throws IOException {
        String data = "SomeString";

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(factory.createSource(data.getBytes(StandardCharsets.ISO_8859_1))));

        tok.nextToken();
        Assert.assertFalse(tok.tokenValueEqualsTo(null));
    }

    @Test
    public void tokenValueEqualsToNotSameStringTest() throws IOException {
        String data = "SomeString";

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(factory.createSource(data.getBytes(StandardCharsets.ISO_8859_1))));

        tok.nextToken();
        Assert.assertFalse(tok.tokenValueEqualsTo((data + "s").getBytes(StandardCharsets.ISO_8859_1)));
    }

    @Test
    public void tokenValueEqualsToNotCaseSensitiveStringTest() throws IOException {
        String data = "SomeString";

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(factory.createSource(data.getBytes(StandardCharsets.ISO_8859_1))));

        tok.nextToken();
        Assert.assertFalse(tok.tokenValueEqualsTo("Somestring".getBytes(StandardCharsets.ISO_8859_1)));
    }

    @Test
    public void checkPdfHeaderTest() throws IOException {
        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(factory.createBestSource(sourceFolder + "test.pdf")));

        Assert.assertEquals("PDF-1.7", tok.checkPdfHeader());
    }

    @Test
    public void getHeaderOffsetTest() throws IOException {
        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(factory.createBestSource(sourceFolder + "test.pdf")));

        Assert.assertEquals(0, tok.getHeaderOffset());
    }

    @Test
    public void primitivesTest() throws Exception {
        String data = "<</Size 70.%comment\n" +
                "/Value#20 .1" +
                "/Root 46 0 R" +
                "/Info 44 0 R" +
                "/ID[<736f6d652068657820737472696e672>(some simple string )<8C2547D58D4BD2C6F3D32B830BE3259D2>-70.1--0.2]" +
                "/Name1 --15" +
                "/Prev ---116.23 >>";
        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(factory.createSource(data.getBytes(StandardCharsets.ISO_8859_1))));

        tok.nextValidToken();
        Assert.assertEquals(PdfTokenizer.TokenType.StartDic, tok.getTokenType());

        tok.nextValidToken();
        Assert.assertEquals(PdfTokenizer.TokenType.Name, tok.getTokenType());
        Assert.assertEquals("Size", new String(tok.getByteContent()));

        tok.nextValidToken();
        Assert.assertEquals(PdfTokenizer.TokenType.Number, tok.getTokenType());
        Assert.assertEquals("70.", new String(tok.getByteContent()));

        tok.nextValidToken();
        Assert.assertEquals(PdfTokenizer.TokenType.Name, tok.getTokenType());
        Assert.assertEquals("Value#20", new String(tok.getByteContent()));

        tok.nextValidToken();
        Assert.assertEquals(PdfTokenizer.TokenType.Number, tok.getTokenType());
        Assert.assertEquals(".1", new String(tok.getByteContent()));

        tok.nextValidToken();
        Assert.assertEquals(PdfTokenizer.TokenType.Name, tok.getTokenType());
        Assert.assertEquals("Root", new String(tok.getByteContent()));

        tok.nextValidToken();
        Assert.assertEquals(PdfTokenizer.TokenType.Ref, tok.getTokenType());
        Assert.assertEquals("46 0 R", "" + tok.getObjNr() + " " + tok.getGenNr()
                + " " + new String(tok.getByteContent()));

        tok.nextValidToken();
        Assert.assertEquals(PdfTokenizer.TokenType.Name, tok.getTokenType());
        Assert.assertEquals("Info", new String(tok.getByteContent()));

        tok.nextValidToken();
        Assert.assertEquals(PdfTokenizer.TokenType.Ref, tok.getTokenType());
        Assert.assertEquals("44 0 R", "" + tok.getObjNr() + " " + tok.getGenNr()
                + " " + new String(tok.getByteContent()));

        tok.nextValidToken();
        Assert.assertEquals(PdfTokenizer.TokenType.Name, tok.getTokenType());
        Assert.assertEquals("ID", new String(tok.getByteContent()));

        tok.nextValidToken();
        Assert.assertEquals(PdfTokenizer.TokenType.StartArray, tok.getTokenType());

        tok.nextValidToken();
        Assert.assertEquals(PdfTokenizer.TokenType.String, tok.getTokenType());
        Assert.assertTrue(tok.isHexString());
        Assert.assertEquals("736f6d652068657820737472696e672", new String(tok.getByteContent()));

        tok.nextValidToken();
        Assert.assertEquals(PdfTokenizer.TokenType.String, tok.getTokenType());
        Assert.assertFalse(tok.isHexString());
        Assert.assertEquals("some simple string ", new String(tok.getByteContent()));

        tok.nextValidToken();
        Assert.assertEquals(PdfTokenizer.TokenType.String, tok.getTokenType());
        Assert.assertTrue(tok.isHexString());
        Assert.assertEquals("8C2547D58D4BD2C6F3D32B830BE3259D2", new String(tok.getByteContent()));

        tok.nextValidToken();
        Assert.assertEquals(PdfTokenizer.TokenType.Number, tok.getTokenType());
        Assert.assertEquals("-70.1", new String(tok.getByteContent()));

        tok.nextValidToken();
        Assert.assertEquals(PdfTokenizer.TokenType.Number, tok.getTokenType());
        Assert.assertEquals("-0.2", new String(tok.getByteContent()));

        tok.nextValidToken();
        Assert.assertEquals(PdfTokenizer.TokenType.EndArray, tok.getTokenType());

        tok.nextValidToken();
        Assert.assertEquals(PdfTokenizer.TokenType.Name, tok.getTokenType());
        Assert.assertEquals("Name1", new String(tok.getByteContent()));

        tok.nextValidToken();
        Assert.assertEquals(PdfTokenizer.TokenType.Number, tok.getTokenType());
        Assert.assertEquals("0", new String(tok.getByteContent()));

        tok.nextValidToken();
        Assert.assertEquals(PdfTokenizer.TokenType.Name, tok.getTokenType());
        Assert.assertEquals("Prev", new String(tok.getByteContent()));

        tok.nextValidToken();
        Assert.assertEquals(PdfTokenizer.TokenType.Number, tok.getTokenType());
        Assert.assertEquals("-116.23", new String(tok.getByteContent()));
    }

    @Test
    public void octalNumberLong1Test() {
        // 49 equal to string "1", octal 1 equals to 1 in decimal
        byte[] bytes = new byte[] {92, 49};
        byte[] result = PdfTokenizer.decodeStringContent(bytes, false);
        Assert.assertArrayEquals(new byte[] {1}, result);
    }

    @Test
    public void octalNumberLong2Test() {
        // 49 50 equal to string "12", octal 12 equals to 10 in decimal
        byte[] bytes = new byte[] {92, 49, 50};
        byte[] result = PdfTokenizer.decodeStringContent(bytes, false);
        Assert.assertArrayEquals(new byte[] {10}, result);
    }

    @Test
    public void octalNumberLong3Test() {
        // 49 50 51 equal to string "123", octal 123 equals to 83 in decimal
        byte[] bytes = new byte[] {92, 49, 50, 51};
        byte[] result = PdfTokenizer.decodeStringContent(bytes, false);
        Assert.assertArrayEquals(new byte[] {83}, result);
    }

    @Test
    public void slashAfterShortOctalTest() {
        // \0\(
        byte[] bytes = new byte[] {92, 48, 92, 40};
        byte[] result = PdfTokenizer.decodeStringContent(bytes, false);
        Assert.assertArrayEquals(new byte[] {0, 40}, result);
    }

    @Test
    public void notOctalAfterShortOctalTest() {
        // \0&
        byte[] bytes = new byte[] {92, 48, 26};
        byte[] result = PdfTokenizer.decodeStringContent(bytes, false);
        Assert.assertArrayEquals(new byte[] {0, 26}, result);
    }

    @Test
    public void notOctalAfterShortOctalTest2() {
        // \12&
        byte[] bytes = new byte[] {92, 49, 50, 26};
        byte[] result = PdfTokenizer.decodeStringContent(bytes, false);
        Assert.assertArrayEquals(new byte[] {10, 26}, result);
    }

    @Test
    public void twoShortOctalsWithGarbageTest() {
        // \0\23 + 4 which should not be taken into account
        byte[] bytes = new byte[] {92, 48, 92, 50, 51, 52};
        byte[] result = PdfTokenizer.decodeStringContent(bytes, 0, 4, false);
        Assert.assertArrayEquals(new byte[] {0, 19}, result);
    }

    private void checkTokenTypes(String data, TokenType... expectedTypes) throws Exception {
        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(factory
                .createSource(data.getBytes(StandardCharsets.ISO_8859_1))));

        for (int i = 0; i < expectedTypes.length; i++) {
            tok.nextValidToken();
            Assert.assertEquals("Position " + i, expectedTypes[i], tok.getTokenType());
        }
    }

    private void checkTokenValues(String data, byte[]... expectedValues) throws Exception {
        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(factory
                .createSource(data.getBytes(StandardCharsets.ISO_8859_1))));

        for (int i = 0; i < expectedValues.length; i++) {
            tok.nextValidToken();
            Assert.assertArrayEquals("Position " + i, expectedValues[i], tok.getByteContent());
        }
    }
}
