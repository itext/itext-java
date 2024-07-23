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
package com.itextpdf.styledxmlparser.css;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.css.font.CssFontFace;
import com.itextpdf.styledxmlparser.css.parse.CssStyleSheetParser;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.regex.Matcher;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CssFontFaceSrcTest extends ExtendedITextTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/styledxmlparser/css/CssFontFaceSrcTest/";

    @BeforeClass
    public static void beforeClass() {
    }

    @Test
    public void srcPropertyTest() throws Exception {

        final String fontSrc = "web-fonts/droid-serif-invalid.";

        CssStyleSheet styleSheet = CssStyleSheetParser.parse(FileUtil.getInputStreamForFile(sourceFolder + "srcs.css"));
        CssFontFaceRule fontFaceRule = (CssFontFaceRule)styleSheet.getStatements().get(0);
        CssDeclaration src = fontFaceRule.getProperties().get(0);

        Assert.assertEquals("src expected", "src", src.getProperty());

        String[] sources = src.getExpression().split(",");

        Assert.assertEquals("27 sources expected", 27 , sources.length);

        for (int i = 0; i < sources.length; i++) {
            Matcher m = CssFontFace.CssFontFaceSrc.UrlPattern.matcher(sources[i]);

            Assert.assertTrue("Expression doesn't match pattern: " + sources[i], m.matches());

            String format = m.group(CssFontFace.CssFontFaceSrc.FormatGroup);
            String source2 = MessageFormatUtil.format("{0}({1}){2}", m.group(CssFontFace.CssFontFaceSrc.TypeGroup), m.group(CssFontFace.CssFontFaceSrc.UrlGroup), format != null ? MessageFormatUtil.format(" format({0})", format) : "");
            String url = CssFontFace.CssFontFaceSrc.unquote(m.group(CssFontFace.CssFontFaceSrc.UrlGroup));

            Assert.assertTrue("Invalid url: " + url, url.startsWith(fontSrc));

            Assert.assertTrue("Invalid format: " + format, format == null || CssFontFace.CssFontFaceSrc.parseFormat(format) != CssFontFace.FontFormat.None);

            Assert.assertEquals("Group check fails: ", sources[i], source2);

            CssFontFace.CssFontFaceSrc fontFaceSrc = CssFontFace.CssFontFaceSrc.create(sources[i]);

            Assert.assertTrue("Invalid url: " + fontSrc, fontFaceSrc.getSrc().startsWith(fontSrc));

            String type = "url";
            if (fontFaceSrc.isLocal()) {
                type = "local";
            }
            Assert.assertTrue("Type '" + type + "' expected: " + sources[i], sources[i].startsWith(type));
            switch (fontFaceSrc.getFormat()) {
                case OpenType:
                    Assert.assertTrue("Format " + fontFaceSrc.getFormat()  + " expected: " + sources[i], sources[i].contains("opentype"));
                    break;
                case TrueType:
                    Assert.assertTrue("Format " + fontFaceSrc.getFormat()  + " expected: " + sources[i], sources[i].contains("truetype"));
                    break;
                case SVG:
                    Assert.assertTrue("Format " + fontFaceSrc.getFormat() + " expected: " + sources[i], sources[i].contains("svg"));
                    break;
                case None:
                    Assert.assertFalse("Format " + fontFaceSrc.getFormat()  + " expected: " +  sources[i], sources[i].contains("format("));
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

        Assert.assertEquals("src expected", "src", src.getProperty());

        String[] sources = CssFontFace.splitSourcesSequence(src.getExpression());

        Assert.assertEquals("8 sources expected", 8, sources.length);

        for (int i = 0; i < 6; i++) {
            Matcher m = CssFontFace.CssFontFaceSrc.UrlPattern.matcher(sources[i]);
            Assert.assertTrue("Expression doesn't match pattern: " + sources[i], m.matches());
        }

        for (int i = 6; i < sources.length; i++) {
            Matcher m = CssFontFace.CssFontFaceSrc.UrlPattern.matcher(sources[i]);
            Assert.assertFalse("Expression matches pattern (though it shouldn't!): " + sources[i], m.matches());
        }
    }
}

