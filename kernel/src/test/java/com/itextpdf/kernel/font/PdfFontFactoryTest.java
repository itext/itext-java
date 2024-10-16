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
package com.itextpdf.kernel.font;

import com.itextpdf.io.font.FontNames;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.font.Type1Font;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfFontFactoryTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/font/";

    @Test
    public void standardFontForceEmbeddedTest() throws IOException {
        Type1Font fontProgram = (Type1Font) FontProgramFactory.createFont(StandardFonts.HELVETICA);

        Exception e = Assertions.assertThrows(PdfException.class,
                () -> PdfFontFactory.createFont(fontProgram, PdfEncodings.UTF8, EmbeddingStrategy.FORCE_EMBEDDED)
        );
        Assertions.assertEquals(KernelExceptionMessageConstant.CANNOT_EMBED_STANDARD_FONT, e.getMessage());
    }

    @Test
    public void standardFontPreferEmbeddedTest() throws IOException {
        Type1Font fontProgram = (Type1Font) FontProgramFactory.createFont(StandardFonts.HELVETICA);

        PdfType1Font font = (PdfType1Font) PdfFontFactory.createFont(
                fontProgram, PdfEncodings.UTF8, EmbeddingStrategy.PREFER_EMBEDDED);
        Assertions.assertNotNull(font);
        Assertions.assertFalse(font.isEmbedded());
    }

    @Test
    public void standardFontPreferNotEmbeddedTest() throws IOException {
        Type1Font fontProgram = (Type1Font) FontProgramFactory.createFont(StandardFonts.HELVETICA);

        PdfType1Font font = (PdfType1Font) PdfFontFactory.createFont(
                fontProgram, PdfEncodings.UTF8, EmbeddingStrategy.PREFER_NOT_EMBEDDED);
        Assertions.assertNotNull(font);
        Assertions.assertFalse(font.isEmbedded());
    }

    @Test
    public void standardFontForceNotEmbeddedTest() throws IOException {
        Type1Font fontProgram = (Type1Font) FontProgramFactory.createFont(StandardFonts.HELVETICA);

        PdfType1Font font = (PdfType1Font) PdfFontFactory.createFont(
                fontProgram, PdfEncodings.UTF8, EmbeddingStrategy.FORCE_NOT_EMBEDDED);
        Assertions.assertNotNull(font);
        Assertions.assertFalse(font.isEmbedded());
    }

    @Test
    public void customType1FontForceEmbeddedTest() throws IOException {
        Type1Font fontProgram = new CustomType1FontProgram();

        PdfType1Font font = (PdfType1Font) PdfFontFactory.createFont(
                fontProgram, PdfEncodings.UTF8, EmbeddingStrategy.FORCE_EMBEDDED);
        Assertions.assertNotNull(font);
        Assertions.assertTrue(font.isEmbedded());
    }

    @Test
    public void customType1FontPreferEmbeddedTest() throws IOException {
        Type1Font fontProgram = new CustomType1FontProgram();

        PdfType1Font font = (PdfType1Font) PdfFontFactory.createFont(
                fontProgram, PdfEncodings.UTF8, EmbeddingStrategy.PREFER_EMBEDDED);
        Assertions.assertNotNull(font);
        Assertions.assertTrue(font.isEmbedded());
    }

    @Test
    public void customType1FontPreferNotEmbeddedTest() throws IOException {
        Type1Font fontProgram = new CustomType1FontProgram();

        PdfType1Font font = (PdfType1Font) PdfFontFactory.createFont(
                fontProgram, PdfEncodings.UTF8, EmbeddingStrategy.PREFER_NOT_EMBEDDED);
        Assertions.assertNotNull(font);
        Assertions.assertFalse(font.isEmbedded());
    }

    @Test
    public void customType1FontForceNotEmbeddedTest() throws IOException {
        Type1Font fontProgram = new CustomType1FontProgram();

        PdfType1Font font = (PdfType1Font) PdfFontFactory.createFont(
                fontProgram, PdfEncodings.UTF8, EmbeddingStrategy.FORCE_NOT_EMBEDDED);
        Assertions.assertNotNull(font);
        Assertions.assertFalse(font.isEmbedded());
    }

    @Test
    public void trueTypeFontProgramUTF8AllowEmbeddingEncodingForceEmbeddedTest() {
        TrueTypeFont fontProgram = new CustomTrueTypeFontProgram(true);

        PdfTrueTypeFont font = (PdfTrueTypeFont) PdfFontFactory.createFont(
                fontProgram, PdfEncodings.UTF8, EmbeddingStrategy.FORCE_EMBEDDED);
        Assertions.assertNotNull(font);
        Assertions.assertTrue(font.isEmbedded());
    }

    @Test
    public void trueTypeFontProgramUTF8AllowEmbeddingEncodingPreferEmbeddedTest() {
        TrueTypeFont fontProgram = new CustomTrueTypeFontProgram(true);

        PdfTrueTypeFont font = (PdfTrueTypeFont) PdfFontFactory.createFont(
                fontProgram, PdfEncodings.UTF8, EmbeddingStrategy.PREFER_EMBEDDED);
        Assertions.assertNotNull(font);
        Assertions.assertTrue(font.isEmbedded());
    }

    @Test
    public void trueTypeFontProgramUTF8AllowEmbeddingEncodingPreferNotEmbeddedTest() {
        TrueTypeFont fontProgram = new CustomTrueTypeFontProgram(true);

        PdfTrueTypeFont font = (PdfTrueTypeFont) PdfFontFactory.createFont(
                fontProgram, PdfEncodings.UTF8, EmbeddingStrategy.PREFER_NOT_EMBEDDED);
        Assertions.assertNotNull(font);
        Assertions.assertFalse(font.isEmbedded());
    }

    @Test
    public void trueTypeFontProgramUTF8AllowEmbeddingEncodingForceNotEmbeddedTest() {
        TrueTypeFont fontProgram = new CustomTrueTypeFontProgram(true);

        PdfTrueTypeFont font = (PdfTrueTypeFont) PdfFontFactory.createFont(
                fontProgram, PdfEncodings.UTF8, EmbeddingStrategy.FORCE_NOT_EMBEDDED);
        Assertions.assertNotNull(font);
        Assertions.assertFalse(font.isEmbedded());
    }

    @Test
    public void trueTypeFontProgramUTF8NotAllowEmbeddingEncodingForceEmbeddedTest() {
        TrueTypeFont fontProgram = new CustomTrueTypeFontProgram(false);

        Exception e = Assertions.assertThrows(PdfException.class,
                () -> PdfFontFactory.createFont(fontProgram, PdfEncodings.UTF8, EmbeddingStrategy.FORCE_EMBEDDED)
        );
        Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.CANNOT_BE_EMBEDDED_DUE_TO_LICENSING_RESTRICTIONS, "CustomNameCustomStyle"),
                e.getMessage());
    }

    @Test
    public void trueTypeFontProgramUTF8NotAllowEmbeddingEncodingPreferEmbeddedTest() {
        TrueTypeFont fontProgram = new CustomTrueTypeFontProgram(false);

        PdfTrueTypeFont font = (PdfTrueTypeFont) PdfFontFactory.createFont(
                fontProgram, PdfEncodings.UTF8, EmbeddingStrategy.PREFER_EMBEDDED);
        Assertions.assertNotNull(font);
        Assertions.assertFalse(font.isEmbedded());
    }

    @Test
    public void trueTypeFontProgramUTF8NotAllowEmbeddingEncodingPreferNotEmbeddedTest() {
        TrueTypeFont fontProgram = new CustomTrueTypeFontProgram(false);

        PdfTrueTypeFont font = (PdfTrueTypeFont) PdfFontFactory.createFont(
                fontProgram, PdfEncodings.UTF8, EmbeddingStrategy.PREFER_NOT_EMBEDDED);
        Assertions.assertNotNull(font);
        Assertions.assertFalse(font.isEmbedded());
    }

    @Test
    public void trueTypeFontProgramUTF8NotAllowEmbeddingEncodingForceNotEmbeddedTest() {
        TrueTypeFont fontProgram = new CustomTrueTypeFontProgram(false);

        PdfTrueTypeFont font = (PdfTrueTypeFont) PdfFontFactory.createFont(
                fontProgram, PdfEncodings.UTF8, EmbeddingStrategy.FORCE_NOT_EMBEDDED);
        Assertions.assertNotNull(font);
        Assertions.assertFalse(font.isEmbedded());
    }

    @Test
    public void trueTypeFontProgramIdentityHAllowEmbeddingEncodingForceEmbeddedTest() {
        TrueTypeFont fontProgram = new CustomTrueTypeFontProgram(true);

        PdfType0Font font = (PdfType0Font) PdfFontFactory.createFont(
                fontProgram, PdfEncodings.IDENTITY_H, EmbeddingStrategy.FORCE_EMBEDDED);
        Assertions.assertNotNull(font);
        Assertions.assertTrue(font.isEmbedded());
    }

    @Test
    public void trueTypeFontProgramIdentityHAllowEmbeddingEncodingPreferEmbeddedTest() {
        TrueTypeFont fontProgram = new CustomTrueTypeFontProgram(true);

        PdfType0Font font = (PdfType0Font) PdfFontFactory.createFont(
                fontProgram, PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_EMBEDDED);
        Assertions.assertNotNull(font);
        Assertions.assertTrue(font.isEmbedded());
    }

    @Test
    public void trueTypeFontProgramIdentityHAllowEmbeddingEncodingPreferNotEmbeddedTest() {
        TrueTypeFont fontProgram = new CustomTrueTypeFontProgram(true);

        PdfType0Font font = (PdfType0Font) PdfFontFactory.createFont(
                fontProgram, PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_NOT_EMBEDDED);
        Assertions.assertNotNull(font);
        Assertions.assertTrue(font.isEmbedded());
    }

    @Test
    public void trueTypeFontProgramIdentityHAllowEmbeddingEncodingForceNotEmbeddedTest() {
        TrueTypeFont fontProgram = new CustomTrueTypeFontProgram(true);

        Exception e = Assertions.assertThrows(PdfException.class,
                () -> PdfFontFactory.createFont(fontProgram, PdfEncodings.IDENTITY_H, EmbeddingStrategy.FORCE_NOT_EMBEDDED)
        );
        Assertions.assertEquals(KernelExceptionMessageConstant.CANNOT_CREATE_TYPE_0_FONT_WITH_TRUE_TYPE_FONT_PROGRAM_WITHOUT_EMBEDDING_IT, e.getMessage());
    }

    @Test
    public void trueTypeFontProgramIdentityHNotAllowEmbeddingEncodingForceEmbeddedTest() {
        TrueTypeFont fontProgram = new CustomTrueTypeFontProgram(false);

        Exception e = Assertions.assertThrows(PdfException.class,
                () -> PdfFontFactory.createFont(fontProgram, PdfEncodings.IDENTITY_H, EmbeddingStrategy.FORCE_EMBEDDED)
        );
        Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.CANNOT_BE_EMBEDDED_DUE_TO_LICENSING_RESTRICTIONS,
                "CustomNameCustomStyle"), e.getMessage());
    }

    @Test
    public void trueTypeFontProgramIdentityHNotAllowEmbeddingEncodingPreferEmbeddedTest() {
        TrueTypeFont fontProgram = new CustomTrueTypeFontProgram(false);

        Exception e = Assertions.assertThrows(PdfException.class,
                () -> PdfFontFactory.createFont(fontProgram, PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_EMBEDDED)
        );
        Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.CANNOT_BE_EMBEDDED_DUE_TO_LICENSING_RESTRICTIONS,
                "CustomNameCustomStyle"), e.getMessage());
    }

    @Test
    public void trueTypeFontProgramIdentityHNotAllowEmbeddingEncodingPreferNotEmbeddedTest() {
        TrueTypeFont fontProgram = new CustomTrueTypeFontProgram(false);

        Exception e = Assertions.assertThrows(PdfException.class,
                () -> PdfFontFactory.createFont(fontProgram, PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_NOT_EMBEDDED)
        );
        Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.CANNOT_BE_EMBEDDED_DUE_TO_LICENSING_RESTRICTIONS,
                "CustomNameCustomStyle"), e.getMessage());
    }

    @Test
    public void trueTypeFontProgramIdentityHNotAllowEmbeddingEncodingForceNotEmbeddedTest() {
        TrueTypeFont fontProgram = new CustomTrueTypeFontProgram(false);

        Exception e = Assertions.assertThrows(PdfException.class,
                () -> PdfFontFactory.createFont(fontProgram, PdfEncodings.IDENTITY_H, EmbeddingStrategy.FORCE_NOT_EMBEDDED)
        );
        Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.CANNOT_BE_EMBEDDED_DUE_TO_LICENSING_RESTRICTIONS,
                "CustomNameCustomStyle"), e.getMessage());
    }

    @Test
    public void standardFontCachedWithoutDocumentTest() throws IOException {
        // this test ensures that method which allows caching into the document does not fail
        // if the document is null
        PdfDocument cacheTo = null;
        PdfType1Font font = (PdfType1Font) PdfFontFactory.createFont(
                StandardFonts.HELVETICA, PdfEncodings.UTF8, cacheTo);
        Assertions.assertNotNull(font);
        Assertions.assertFalse(font.isEmbedded());
    }

    @Test
    public void createFontFromNullDictionaryTest() {
        PdfDictionary dictionary = null;

        Exception e = Assertions.assertThrows(PdfException.class, () -> PdfFontFactory.createFont(dictionary));
        Assertions.assertEquals(KernelExceptionMessageConstant.CANNOT_CREATE_FONT_FROM_NULL_PDF_DICTIONARY, e.getMessage());
    }

    @Test
    public void createFontFromEmptyDictionaryTest() {
        PdfDictionary dictionary = new PdfDictionary();

        Exception e = Assertions.assertThrows(PdfException.class, () -> PdfFontFactory.createFont(dictionary));
        Assertions.assertEquals(KernelExceptionMessageConstant.DICTIONARY_DOES_NOT_HAVE_SUPPORTED_FONT_DATA, e.getMessage());
    }

    private static class CustomType1FontProgram extends Type1Font {
        @Override
        public boolean isBuiltInFont() {
            return false;
        }
    }

    private static class CustomTrueTypeFontProgram extends TrueTypeFont {
        public CustomTrueTypeFontProgram(boolean allowEmbedding) {
            this.fontNames = new CustomFontNames(allowEmbedding);
        }
    }

    private static class CustomFontNames extends FontNames {
        public CustomFontNames(boolean allowEmbedding) {
            this.setAllowEmbedding(allowEmbedding);
            this.setFontName("CustomName");
            this.setStyle("CustomStyle");
        }
    }
}
