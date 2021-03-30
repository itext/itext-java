/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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
package com.itextpdf.io.source;

import com.itextpdf.io.source.PdfTokenizer.TokenType;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import static com.itextpdf.io.IOException.ErrorAtFilePointer1;

@Category(UnitTest.class)
public class PdfTokenizerTest extends ExtendedITextTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
        expectedException.expect(com.itextpdf.io.IOException.class);
        expectedException.expectMessage(MessageFormatUtil.format(ErrorAtFilePointer1, 0));

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(
                factory.createSource("/Name1".getBytes(StandardCharsets.ISO_8859_1))));

        tok.throwError(ErrorAtFilePointer1, 0);
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
