package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.RandomAccessFileOrArray;
import com.itextpdf.basics.io.RandomAccessSourceFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class PdfTokeniserTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    private void checkTokenTypes(String data, PdfTokeniser.TokenType... expectedTypes) throws Exception {
        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokeniser tok = new PdfTokeniser(new RandomAccessFileOrArray(factory.createSource(data.getBytes())));

        for (int i = 0; i < expectedTypes.length; i++) {
            tok.nextValidToken();
            //System.out.println(tok.getTokenType() + " -> " + tok.getStringValue());
            Assert.assertEquals("Position " + i, expectedTypes[i], tok.getTokenType());
        }
    }

    @Test
    public void testOneNumber() throws Exception {
        checkTokenTypes(
                "/Name1 70",
                PdfTokeniser.TokenType.Name,
                PdfTokeniser.TokenType.Number,
                PdfTokeniser.TokenType.EndOfFile
        );
    }

    @Test
    public void testTwoNumbers() throws Exception {
        checkTokenTypes(
                "/Name1 70/Name 2",
                PdfTokeniser.TokenType.Name,
                PdfTokeniser.TokenType.Number,
                PdfTokeniser.TokenType.Name,
                PdfTokeniser.TokenType.Number,
                PdfTokeniser.TokenType.EndOfFile
        );
    }

    @Test
    public void tokenTypesTest() throws Exception {
        checkTokenTypes(
                "<</Size 70/Root 46 0 R/Info 44 0 R/ID[<8C2547D58D4BD2C6F3D32B830BE3259D><8F69587888569A458EB681A4285D5879>]/Prev 116 >>",
                PdfTokeniser.TokenType.StartDic,
                PdfTokeniser.TokenType.Name,
                PdfTokeniser.TokenType.Number,
                PdfTokeniser.TokenType.Name,
                PdfTokeniser.TokenType.Ref,
                PdfTokeniser.TokenType.Name,
                PdfTokeniser.TokenType.Ref,
                PdfTokeniser.TokenType.Name,
                PdfTokeniser.TokenType.StartArray,
                PdfTokeniser.TokenType.String,
                PdfTokeniser.TokenType.String,
                PdfTokeniser.TokenType.EndArray,
                PdfTokeniser.TokenType.Name,
                PdfTokeniser.TokenType.Number,
                PdfTokeniser.TokenType.EndDic,
                PdfTokeniser.TokenType.EndOfFile
        );
    }

    @Test
    public void primitivesTest() throws Exception {
        String data = "<</Size 70." +
                "/Value#20 .1" +
                "/Root 46 0 R" +
                "/Info 44 0 R" +
                "/ID[<736f6d652068657820737472696e672>(some simple string )<8C2547D58D4BD2C6F3D32B830BE3259D2>-70.1--0.2]" +
                "/Prev ---116.23 >>";
        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokeniser tok = new PdfTokeniser(new RandomAccessFileOrArray(factory.createSource(data.getBytes())));

        tok.nextValidToken();
        Assert.assertSame(tok.getTokenType(), PdfTokeniser.TokenType.StartDic);

        tok.nextValidToken();
        Assert.assertSame(tok.getTokenType(), PdfTokeniser.TokenType.Name);
        PdfName name = new PdfName(tok.getByteContent());
        Assert.assertEquals("Size", name.getValue());

        tok.nextValidToken();
        Assert.assertSame(tok.getTokenType(), PdfTokeniser.TokenType.Number);
        PdfNumber num = new PdfNumber(tok.getByteContent());
        Assert.assertEquals("70.", num.toString());

        tok.nextValidToken();
        Assert.assertSame(tok.getTokenType(), PdfTokeniser.TokenType.Name);
        name = new PdfName(tok.getByteContent());
        Assert.assertEquals("Value ", name.getValue());

        tok.nextValidToken();
        Assert.assertSame(tok.getTokenType(), PdfTokeniser.TokenType.Number);
        num = new PdfNumber(tok.getByteContent());
        Assert.assertNotSame("0.1", num.toString());

        tok.nextValidToken();
        Assert.assertSame(tok.getTokenType(), PdfTokeniser.TokenType.Name);
        name = new PdfName(tok.getByteContent());
        Assert.assertEquals("Root", name.getValue());

        tok.nextValidToken();
        Assert.assertSame(tok.getTokenType(), PdfTokeniser.TokenType.Ref);
        PdfIndirectReference ref = new PdfIndirectReference(null, tok.getObjNr(), tok.getGenNr(), null);
        Assert.assertEquals("46 0 R", ref.toString());

        tok.nextValidToken();
        Assert.assertSame(tok.getTokenType(), PdfTokeniser.TokenType.Name);
        name = new PdfName(tok.getByteContent());
        Assert.assertEquals("Info", name.getValue());

        tok.nextValidToken();
        Assert.assertSame(tok.getTokenType(), PdfTokeniser.TokenType.Ref);
        ref = new PdfIndirectReference(null, tok.getObjNr(), tok.getGenNr(), null);
        Assert.assertEquals("44 0 R", ref.toString());

        tok.nextValidToken();
        Assert.assertSame(tok.getTokenType(), PdfTokeniser.TokenType.Name);
        name = new PdfName(tok.getByteContent());
        Assert.assertEquals("ID", name.getValue());

        tok.nextValidToken();
        Assert.assertSame(tok.getTokenType(), PdfTokeniser.TokenType.StartArray);

        tok.nextValidToken();
        Assert.assertSame(tok.getTokenType(), PdfTokeniser.TokenType.String);
        Assert.assertSame(tok.isHexString(), true);
        PdfString str = new PdfString(tok.getByteContent(), tok.isHexString());
        Assert.assertEquals("some hex string ", str.getValue());

        tok.nextValidToken();
        Assert.assertSame(tok.getTokenType(), PdfTokeniser.TokenType.String);
        Assert.assertSame(tok.isHexString(), false);
        str = new PdfString(tok.getByteContent(), tok.isHexString());
        Assert.assertEquals("some simple string ", str.getValue());

        tok.nextValidToken();
        Assert.assertSame(tok.getTokenType(), PdfTokeniser.TokenType.String);
        Assert.assertSame(tok.isHexString(), true);
        str = new PdfString(tok.getByteContent(), tok.isHexString());
        Assert.assertEquals("\u008C%GÕ\u008DKÒÆóÓ+\u0083\u000Bã%\u009D ", str.getValue());

        tok.nextValidToken();
        Assert.assertSame(tok.getTokenType(), PdfTokeniser.TokenType.Number);
        num = new PdfNumber(tok.getByteContent());
        Assert.assertEquals("-70.1", num.toString());

        tok.nextValidToken();
        Assert.assertSame(tok.getTokenType(), PdfTokeniser.TokenType.Number);
        num = new PdfNumber(tok.getByteContent());
        Assert.assertEquals("0.2", num.toString());

        tok.nextValidToken();
        Assert.assertSame(tok.getTokenType(), PdfTokeniser.TokenType.EndArray);

        tok.nextValidToken();
        Assert.assertSame(tok.getTokenType(), PdfTokeniser.TokenType.Name);
        name = new PdfName(tok.getByteContent());
        Assert.assertEquals("Prev", name.getValue());

        tok.nextValidToken();
        Assert.assertSame(tok.getTokenType(), PdfTokeniser.TokenType.Number);
        num = new PdfNumber(tok.getByteContent());
        Assert.assertEquals("-116.23", num.toString());
    }

    @Test
    public void tokenValueEqualsToTest() throws PdfException, IOException {
        String data = "SomeString";
        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
        PdfTokeniser tok = new PdfTokeniser(new RandomAccessFileOrArray(factory.createSource(data.getBytes())));
        tok.nextToken();
        Assert.assertTrue(tok.tokenValueEqualsTo(data.getBytes()));
    }
}
