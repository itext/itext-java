/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.styledxmlparser.css;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.css.font.CssFontFace;
import com.itextpdf.styledxmlparser.css.parse.CssStyleSheetParser;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.util.regex.Matcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CssFontFaceSrcTest extends ExtendedITextTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/styledxmlparser/css/CssFontFaceSrcTest/";

    @BeforeAll
    public static void beforeClass() {
    }

    @Test
    public void srcPropertyTest() throws Exception {

        final String fontSrc = "web-fonts/droid-serif-invalid.";

        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(sourceFolder + "srcs.css"));
        CssFontFaceRule fontFaceRule = (CssFontFaceRule)styleSheet.getStatements().get(0);
        CssDeclaration src = fontFaceRule.getProperties().get(0);

        Assertions.assertEquals("src", src.getProperty(), "src expected");

        String[] sources = src.getExpression().split(",");

        Assertions.assertEquals(27 , sources.length, "27 sources expected");

        for (int i = 0; i < sources.length; i++) {
            Matcher m = CssFontFace.CssFontFaceSrc.UrlPattern.matcher(sources[i]);

            Assertions.assertTrue(m.matches(), "Expression doesn't match pattern: " + sources[i]);

            String format = m.group(CssFontFace.CssFontFaceSrc.FormatGroup);
            String source2 = MessageFormatUtil.format("{0}({1}){2}", m.group(CssFontFace.CssFontFaceSrc.TypeGroup), m.group(CssFontFace.CssFontFaceSrc.UrlGroup), format != null ? MessageFormatUtil.format(" format({0})", format) : "");
            String url = CssFontFace.CssFontFaceSrc.unquote(m.group(CssFontFace.CssFontFaceSrc.UrlGroup));

            Assertions.assertTrue(url.startsWith(fontSrc), "Invalid url: " + url);

            Assertions.assertTrue(format == null || CssFontFace.CssFontFaceSrc.parseFormat(format) != CssFontFace.FontFormat.None, "Invalid format: " + format);

            Assertions.assertEquals(sources[i], source2, "Group check fails: ");

            CssFontFace.CssFontFaceSrc fontFaceSrc = CssFontFace.CssFontFaceSrc.create(sources[i]);

            Assertions.assertTrue(fontFaceSrc.getSrc().startsWith(fontSrc), "Invalid url: " + fontSrc);

            String type = "url";
            if (fontFaceSrc.isLocal()) {
                type = "local";
            }
            Assertions.assertTrue(sources[i].startsWith(type), "Type '" + type + "' expected: " + sources[i]);
            switch (fontFaceSrc.getFormat()) {
                case OpenType:
                    Assertions.assertTrue(sources[i].contains("opentype"), "Format " + fontFaceSrc.getFormat()  + " expected: " + sources[i]);
                    break;
                case TrueType:
                    Assertions.assertTrue(sources[i].contains("truetype"), "Format " + fontFaceSrc.getFormat()  + " expected: " + sources[i]);
                    break;
                case SVG:
                    Assertions.assertTrue(sources[i].contains("svg"), "Format " + fontFaceSrc.getFormat() + " expected: " + sources[i]);
                    break;
                case None:
                    Assertions.assertFalse(sources[i].contains("format("), "Format " + fontFaceSrc.getFormat()  + " expected: " +  sources[i]);
                    break;
            }

        }
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.QUOTE_IS_NOT_CLOSED_IN_CSS_EXPRESSION)})
    public void parseBase64SrcTest() throws Exception {
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(sourceFolder + "srcs2.css"));
        CssFontFaceRule fontFaceRule = (CssFontFaceRule)styleSheet.getStatements().get(0);
        CssDeclaration src = fontFaceRule.getProperties().get(0);

        Assertions.assertEquals("src", src.getProperty(), "src expected");

        String[] sources = CssFontFace.splitSourcesSequence(src.getExpression());

        Assertions.assertEquals(8, sources.length, "8 sources expected");

        for (int i = 0; i < 6; i++) {
            Matcher m = CssFontFace.CssFontFaceSrc.UrlPattern.matcher(sources[i]);
            Assertions.assertTrue(m.matches(), "Expression doesn't match pattern: " + sources[i]);
        }

        for (int i = 6; i < sources.length; i++) {
            Matcher m = CssFontFace.CssFontFaceSrc.UrlPattern.matcher(sources[i]);
            Assertions.assertFalse(m.matches(), "Expression matches pattern (though it shouldn't!): " + sources[i]);
        }
    }
}

