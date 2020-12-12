package com.itextpdf.kernel.font;

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfType3FontTest extends ExtendedITextTest {
    private static final float EPS = 1e-4f;

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void addDifferentGlyphsInConstructorTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.FontMatrix, new PdfArray());
        PdfDictionary charProcs = new PdfDictionary();
        charProcs.put(new PdfName("space"), new PdfStream());
        charProcs.put(new PdfName("A"), new PdfStream());
        dictionary.put(PdfName.CharProcs, charProcs);
        dictionary.put(PdfName.Widths, new PdfArray());
        dictionary.put(PdfName.ToUnicode, PdfName.IdentityH);
        dictionary.put(PdfName.Encoding, new PdfName("zapfdingbatsencoding"));
        PdfType3Font type3Font = new PdfType3Font(dictionary) {
            @Override
            protected PdfDocument getDocument() {
                return null;
            }
        };
        Assert.assertNotNull(type3Font.getFontProgram());
        int spaceGlyphCode = 32;
        Glyph glyph = type3Font.getFontProgram().getGlyph(spaceGlyphCode);
        Assert.assertEquals(new Glyph(spaceGlyphCode, 0, new char[]{' '}), glyph);

        int AGlyphCode = 65;
        glyph = type3Font.getFontProgram().getGlyph(AGlyphCode);
        Assert.assertEquals(new Glyph(AGlyphCode, 0, new char[] {'A'}), glyph);
    }

    @Test
    public void addAlreadyExistingGlyphTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.FontMatrix, new PdfArray());
        PdfDictionary charProcs = new PdfDictionary();
        charProcs.put(new PdfName("A"), new PdfStream());
        dictionary.put(PdfName.CharProcs, charProcs);
        dictionary.put(PdfName.Widths, new PdfArray());
        PdfType3Font type3Font = new PdfType3Font(dictionary) {
            @Override
            protected PdfDocument getDocument() {
                return null;
            }
        };

        Type3Glyph type3Glyph = type3Font.addGlyph('A', 1, 2, 3, 5, 8);
        Assert.assertEquals(0, type3Glyph.getWx(), EPS);
        Assert.assertEquals(0, type3Glyph.getLlx(), EPS);
        Assert.assertEquals(0, type3Glyph.getLly(), EPS);
        Assert.assertEquals(0, type3Glyph.getUrx(), EPS);
        Assert.assertEquals(0, type3Glyph.getUry(), EPS);
    }

    @Test
    public void setFontStretchTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.FontMatrix, new PdfArray());
        PdfDictionary charProcs = new PdfDictionary();
        dictionary.put(PdfName.CharProcs, charProcs);
        dictionary.put(PdfName.Widths, new PdfArray());
        PdfType3Font type3Font = new PdfType3Font(dictionary);

        String fontStretch = "test";
        type3Font.setFontStretch(fontStretch);
        Assert.assertNotNull(type3Font.fontProgram);
        Assert.assertNotNull(type3Font.fontProgram.getFontNames());
        Assert.assertEquals(fontStretch, type3Font.fontProgram.getFontNames().getFontStretch());
    }

    @Test
    public void setPdfFontFlagsTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.FontMatrix, new PdfArray());
        PdfDictionary charProcs = new PdfDictionary();
        dictionary.put(PdfName.CharProcs, charProcs);
        dictionary.put(PdfName.Widths, new PdfArray());
        PdfType3Font type3Font = new PdfType3Font(dictionary);

        int randomTestFontFlagsValue = 5;
        type3Font.setPdfFontFlags(randomTestFontFlagsValue);
        Assert.assertNotNull(type3Font.fontProgram);
        Assert.assertEquals(randomTestFontFlagsValue, type3Font.fontProgram.getPdfFontFlags());
    }

    @Test
    public void glyphWithUnicodeBiggerThan32CannotBeEncodedTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.FontMatrix, new PdfArray());
        PdfDictionary charProcs = new PdfDictionary();
        dictionary.put(PdfName.CharProcs, charProcs);
        dictionary.put(PdfName.Widths, new PdfArray());
        PdfType3Font type3Font = new PdfType3Font(dictionary);

        int cannotEncodeAndAUnicodeBiggerThan32TestValue = 333;
        Assert.assertNull(type3Font.getGlyph(cannotEncodeAndAUnicodeBiggerThan32TestValue));
    }

    @Test
    public void containsGlyphTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.FontMatrix, new PdfArray());
        PdfDictionary charProcs = new PdfDictionary();
        dictionary.put(PdfName.CharProcs, charProcs);
        dictionary.put(PdfName.Widths, new PdfArray());
        PdfType3Font type3Font = new PdfType3Font(dictionary) {
            @Override
            protected PdfDocument getDocument() {
                return null;
            }
        };

        Assert.assertFalse(type3Font.containsGlyph(333));
        Assert.assertFalse(type3Font.containsGlyph(-5));
        Assert.assertFalse(type3Font.containsGlyph(32));
        type3Font.addGlyph(' ', 0, 0, 0, 1, 1);
        Assert.assertTrue(type3Font.containsGlyph(32));
        type3Font.addGlyph('A', 0, 0, 0, 0, 0);
        Assert.assertTrue(type3Font.containsGlyph(65));
    }

    @Test
    public void flushExceptionTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(PdfException.NoGlyphsDefinedForType3Font);
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.FontMatrix, new PdfArray());
        PdfDictionary charProcs = new PdfDictionary();
        dictionary.put(PdfName.CharProcs, charProcs);
        dictionary.put(PdfName.Widths, new PdfArray());
        PdfType3Font type3Font = new DisableEnsureUnderlyingObjectHasIndirectReference(dictionary);

        type3Font.flush();
    }

    @Test
    public void fillFontDescriptorTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.FontMatrix, new PdfArray());
        PdfDictionary charProcs = new PdfDictionary();
        dictionary.put(PdfName.CharProcs, charProcs);
        dictionary.put(PdfName.Widths, new PdfArray());
        PdfDictionary fontDescriptor = new PdfDictionary();
        String fontStretch = "test";
        fontDescriptor.put(PdfName.FontStretch, new PdfName(fontStretch));
        dictionary.put(PdfName.FontDescriptor, fontDescriptor);
        PdfType3Font type3Font = new PdfType3Font(dictionary) {
            @Override
            protected PdfDocument getDocument() {
                return null;
            }
        };
        Assert.assertNotNull(type3Font.fontProgram);
        Assert.assertNotNull(type3Font.fontProgram.getFontNames());
        Assert.assertEquals(fontStretch, type3Font.fontProgram.getFontNames().getFontStretch());
    }

    private class DisableEnsureUnderlyingObjectHasIndirectReference extends PdfType3Font {

        DisableEnsureUnderlyingObjectHasIndirectReference(PdfDictionary fontDictionary) {
            super(fontDictionary);
        }

        @Override
        protected void ensureUnderlyingObjectHasIndirectReference() {
        }
    }
}
