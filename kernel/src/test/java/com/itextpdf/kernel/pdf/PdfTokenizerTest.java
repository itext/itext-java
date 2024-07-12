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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Category(IntegrationTest.class)
public class PdfTokenizerTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfTokeniserTest/";

    @Test
    public void encodingTest() throws IOException {

        RandomAccessSourceFactory factory;
        PdfTokenizer tok;
        PdfString pdfString;

        // hex string parse and check
        String testHexString = "<0D0A09557365729073204775696465>";
        factory = new RandomAccessSourceFactory();
        tok = new PdfTokenizer(new RandomAccessFileOrArray(factory.createSource(testHexString.getBytes(StandardCharsets.ISO_8859_1))));
        tok.nextToken();
        pdfString = new PdfString(tok.getByteContent(), tok.isHexString());
        Assert.assertEquals("\r\n\tUser\u0090s Guide", pdfString.getValue());

        String testUnicodeString = "ΑΒΓΗ€•♣⋅";
        pdfString = new PdfString(PdfEncodings.convertToBytes(testUnicodeString, PdfEncodings.UNICODE_BIG), false);
        Assert.assertEquals(testUnicodeString, pdfString.toUnicodeString());

        pdfString = new PdfString("FEFF041F04400438043204350442".getBytes(StandardCharsets.ISO_8859_1), true);
        Assert.assertEquals("\u041F\u0440\u0438\u0432\u0435\u0442", pdfString.toUnicodeString());

        pdfString = new PdfString("FEFF041F04400438043204350442".getBytes(StandardCharsets.ISO_8859_1), false);
        Assert.assertEquals("FEFF041F04400438043204350442", pdfString.toUnicodeString());

        String specialCharacter = "\r\n\t\\n\\r\\t\\f";
        pdfString = new PdfString(specialCharacter.getBytes(StandardCharsets.ISO_8859_1), false);
        Assert.assertEquals("\n\t\n\r\t\f", pdfString.toUnicodeString());

        String symbol = "\u0001\u0004\u0006\u000E\u001F";
        pdfString = new PdfString(symbol.getBytes(StandardCharsets.ISO_8859_1), false);
        Assert.assertEquals(symbol, pdfString.toUnicodeString());


        String testString1 ="These\\\n two\\\r strings\\\n are the same";
        pdfString = new PdfString(testString1.getBytes(StandardCharsets.ISO_8859_1), false);
        Assert.assertEquals("These two strings are the same", pdfString.getValue());

        String testString2 ="This string contains \\245two octal characters\\307";
        pdfString = new PdfString(testString2.getBytes(StandardCharsets.ISO_8859_1), false);
        Assert.assertEquals("This string contains \u00A5two octal characters\u00C7", pdfString.getValue());


        String testString3 ="\\0053";
        pdfString = new PdfString(testString3.getBytes(StandardCharsets.ISO_8859_1), false);
        Assert.assertEquals("\u00053", pdfString.getValue());

        String testString4 ="\\053";
        pdfString = new PdfString(testString4.getBytes(StandardCharsets.ISO_8859_1), false);
        Assert.assertEquals("+", pdfString.getValue());

        byte[] b = new byte[]{(byte)46,(byte)56,(byte)40};
        pdfString = new PdfString(b,false);
        Assert.assertEquals(new String(b),pdfString.getValue());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE))
    public void readPdfStringTest() throws IOException {
        final String author = "This string9078 contains \u00A5two octal characters\u00C7";
        final String creator = "iText\r 6\n";
        final String title = "\u00DF\u00E3\u00EB\u00F0";
        final String subject = "+";
        String filename = sourceFolder + "writePdfString.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument d = new PdfDocument(reader);
        // text in pdf: int array ( 223,227, 235,240)
        Assert.assertEquals(d.getDocumentInfo().getTitle(), title);
        // text in pdf: This string\9078 contains \245two octal characters\307
        Assert.assertEquals(d.getDocumentInfo().getAuthor(), author);
        // text in pdf: iText\r 6\n
        Assert.assertEquals(d.getDocumentInfo().getCreator(), creator);
        // text in pdf: \053
        Assert.assertEquals(d.getDocumentInfo().getSubject(), subject);

    }

    @Test
    public void primitivesTest() throws Exception {
        String data = "<</Size 70." +
                "/Value#20 .1" +
                "/Root 46 0 R" +
                "/Info 44 0 R" +
                "/ID[<736f6d652068657820737472696e672>(some simple string )<8C2547D58D4BD2C6F3D32B830BE3259D2>-70.1--0.2]" +
                "/Name1 --15" +
                "/Prev ---116.23 >>";
        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokenizer tok = new PdfTokenizer(new RandomAccessFileOrArray(factory.createSource(data.getBytes(StandardCharsets.ISO_8859_1))));

        tok.nextValidToken();
        Assert.assertEquals(tok.getTokenType(), PdfTokenizer.TokenType.StartDic);

        tok.nextValidToken();
        Assert.assertEquals(tok.getTokenType(), PdfTokenizer.TokenType.Name);
        PdfName name = new PdfName(tok.getByteContent());
        Assert.assertEquals("Size", name.getValue());

        tok.nextValidToken();
        Assert.assertEquals(tok.getTokenType(), PdfTokenizer.TokenType.Number);
        PdfNumber num = new PdfNumber(tok.getByteContent());
        Assert.assertEquals("70.", num.toString());

        tok.nextValidToken();
        Assert.assertEquals(tok.getTokenType(), PdfTokenizer.TokenType.Name);
        name = new PdfName(tok.getByteContent());
        Assert.assertEquals("Value ", name.getValue());

        tok.nextValidToken();
        Assert.assertEquals(tok.getTokenType(), PdfTokenizer.TokenType.Number);
        num = new PdfNumber(tok.getByteContent());
        Assert.assertNotSame("0.1", num.toString());

        tok.nextValidToken();
        Assert.assertEquals(tok.getTokenType(), PdfTokenizer.TokenType.Name);
        name = new PdfName(tok.getByteContent());
        Assert.assertEquals("Root", name.getValue());

        tok.nextValidToken();
        Assert.assertEquals(tok.getTokenType(), PdfTokenizer.TokenType.Ref);
        PdfIndirectReference ref = new PdfIndirectReference(null, tok.getObjNr(), tok.getGenNr());
        Assert.assertEquals("46 0 R", ref.toString());

        tok.nextValidToken();
        Assert.assertEquals(tok.getTokenType(), PdfTokenizer.TokenType.Name);
        name = new PdfName(tok.getByteContent());
        Assert.assertEquals("Info", name.getValue());

        tok.nextValidToken();
        Assert.assertEquals(tok.getTokenType(), PdfTokenizer.TokenType.Ref);
        ref = new PdfIndirectReference(null, tok.getObjNr(), tok.getGenNr());
        Assert.assertEquals("44 0 R", ref.toString());

        tok.nextValidToken();
        Assert.assertEquals(tok.getTokenType(), PdfTokenizer.TokenType.Name);
        name = new PdfName(tok.getByteContent());
        Assert.assertEquals("ID", name.getValue());

        tok.nextValidToken();
        Assert.assertEquals(tok.getTokenType(), PdfTokenizer.TokenType.StartArray);

        tok.nextValidToken();
        Assert.assertEquals(tok.getTokenType(), PdfTokenizer.TokenType.String);
        Assert.assertTrue(tok.isHexString());
        PdfString str = new PdfString(tok.getByteContent(), tok.isHexString());
        Assert.assertEquals("some hex string ", str.getValue());

        tok.nextValidToken();
        Assert.assertEquals(tok.getTokenType(), PdfTokenizer.TokenType.String);
        Assert.assertFalse(tok.isHexString());
        str = new PdfString(tok.getByteContent(), tok.isHexString());
        Assert.assertEquals("some simple string ", str.getValue());

        tok.nextValidToken();
        Assert.assertEquals(tok.getTokenType(), PdfTokenizer.TokenType.String);
        Assert.assertTrue(tok.isHexString());
        str = new PdfString(tok.getByteContent(), tok.isHexString());
        Assert.assertEquals("\u008C%G\u00D5\u008DK\u00D2\u00C6\u00F3\u00D3+\u0083\u000B\u00E3%\u009D ", str.getValue());

        tok.nextValidToken();
        Assert.assertEquals(tok.getTokenType(), PdfTokenizer.TokenType.Number);
        num = new PdfNumber(tok.getByteContent());
        Assert.assertEquals("-70.1", num.toString());

        tok.nextValidToken();
        Assert.assertEquals(tok.getTokenType(), PdfTokenizer.TokenType.Number);
        num = new PdfNumber(tok.getByteContent());
        Assert.assertEquals("-0.2", num.toString());

        tok.nextValidToken();
        Assert.assertEquals(tok.getTokenType(), PdfTokenizer.TokenType.EndArray);

        tok.nextValidToken();
        Assert.assertEquals(tok.getTokenType(), PdfTokenizer.TokenType.Name);
        name = new PdfName(tok.getByteContent());
        Assert.assertEquals("Name1", name.getValue());

        tok.nextValidToken();
        Assert.assertEquals(tok.getTokenType(), PdfTokenizer.TokenType.Number);
        num = new PdfNumber(tok.getByteContent());
        Assert.assertEquals("0", num.toString());

        tok.nextValidToken();
        Assert.assertEquals(tok.getTokenType(), PdfTokenizer.TokenType.Name);
        name = new PdfName(tok.getByteContent());
        Assert.assertEquals("Prev", name.getValue());

        tok.nextValidToken();
        Assert.assertEquals(tok.getTokenType(), PdfTokenizer.TokenType.Number);
        num = new PdfNumber(tok.getByteContent());
        Assert.assertEquals("-116.23", num.toString());
    }
}
