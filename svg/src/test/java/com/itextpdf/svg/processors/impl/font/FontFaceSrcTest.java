/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.svg.processors.impl.font;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.CssFontFaceRule;
import com.itextpdf.styledxmlparser.css.CssStyleSheet;
import com.itextpdf.styledxmlparser.css.parse.CssStyleSheetParser;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;
import java.io.FileInputStream;
import java.util.regex.Matcher;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class FontFaceSrcTest extends ExtendedITextTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/processors/impl/font/FontFaceSrcTest/";

    @BeforeClass
    public static void beforeClass() {
    }

    @Test
    public void srcPropertyTest() throws Exception {

        final String fontSrc = "web-fonts/droid-serif-invalid.";

        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new FileInputStream(sourceFolder + "srcs.css"));
        CssFontFaceRule fontFaceRule = (CssFontFaceRule) styleSheet.getStatements().get(0);
        CssDeclaration src = fontFaceRule.getProperties().get(0);

        Assert.assertEquals("src expected", "src", src.getProperty());

        String[] sources = src.getExpression().split(",");

        Assert.assertEquals("27 sources expected", 27, sources.length);

        for (int i = 0; i < sources.length; i++) {
            Matcher m = FontFace.FontFaceSrc.UrlPattern.matcher(sources[i]);

            Assert.assertTrue("Expression doesn't match pattern: " + sources[i], m.matches());

            String format = m.group(FontFace.FontFaceSrc.FormatGroup);
            String source2 = MessageFormatUtil.format("{0}({1}){2}", m.group(FontFace.FontFaceSrc.TypeGroup), m.group(FontFace.FontFaceSrc.UrlGroup), format != null ? MessageFormatUtil.format(" format({0})", format) : "");
            String url = FontFace.FontFaceSrc.unquote(m.group(FontFace.FontFaceSrc.UrlGroup));

            Assert.assertTrue("Invalid url: " + url, url.startsWith(fontSrc));

            Assert.assertTrue("Invalid format: " + format, format == null || FontFace.FontFaceSrc.parseFormat(format) != FontFace.FontFormat.None);

            Assert.assertEquals("Group check fails: ", sources[i], source2);

            FontFace.FontFaceSrc fontFaceSrc = FontFace.FontFaceSrc.create(sources[i]);

            Assert.assertTrue("Invalid url: " + fontSrc, fontFaceSrc.src.startsWith(fontSrc));

            String type = "url";
            if (fontFaceSrc.isLocal) {
                type = "local";
            }
            Assert.assertTrue("Type '" + type + "' expected: " + sources[i], sources[i].startsWith(type));
            switch (fontFaceSrc.format) {
                case OpenType:
                    Assert.assertTrue("Format " + fontFaceSrc.format + " expected: " + sources[i], sources[i].contains("opentype"));
                    break;
                case TrueType:
                    Assert.assertTrue("Format " + fontFaceSrc.format + " expected: " + sources[i], sources[i].contains("truetype"));
                    break;
                case SVG:
                    Assert.assertTrue("Format " + fontFaceSrc.format + " expected: " + sources[i], sources[i].contains("svg"));
                    break;
                case None:
                    Assert.assertFalse("Format " + fontFaceSrc.format + " expected: " + sources[i], sources[i].contains("format("));
                    break;
            }
        }
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.QUOTE_IS_NOT_CLOSED_IN_CSS_EXPRESSION)})
    public void parseBase64SrcTest() throws Exception {
        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new FileInputStream(sourceFolder + "srcs2.css"));
        CssFontFaceRule fontFaceRule = (CssFontFaceRule) styleSheet.getStatements().get(0);
        CssDeclaration src = fontFaceRule.getProperties().get(0);

        Assert.assertEquals("src expected", "src", src.getProperty());

        String[] sources = FontFace.splitSourcesSequence(src.getExpression());

        Assert.assertEquals("8 sources expected", 8, sources.length);

        for (int i = 0; i < 6; i++) {
            Matcher m = FontFace.FontFaceSrc.UrlPattern.matcher(sources[i]);
            Assert.assertTrue("Expression doesn't match pattern: " + sources[i], m.matches());
        }

        for (int i = 6; i < sources.length; i++) {
            Matcher m = FontFace.FontFaceSrc.UrlPattern.matcher(sources[i]);
            Assert.assertFalse("Expression matches pattern (though it shouldn't!): " + sources[i], m.matches());
        }
    }
}
