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
package com.itextpdf.styledxmlparser.jsoup.nodes;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.parser.Parser;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Tests for the DocumentType node
 *
 * @author Jonathan Hedley, http://jonathanhedley.com/
 */
@Category(UnitTest.class)
public class DocumentTypeTest extends ExtendedITextTest {
    @Test
    public void constructorValidationOkWithBlankName() {
        AssertUtil.doesNotThrow(() -> new DocumentType("","", ""));
    }

    @Test
    public void constructorValidationThrowsExceptionOnNulls() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new DocumentType("html", null, null));
    }

    @Test
    public void constructorValidationOkWithBlankPublicAndSystemIds() {
        AssertUtil.doesNotThrow(() -> new DocumentType("html","", ""));
    }

    @Test public void outerHtmlGeneration() {
        DocumentType html5 = new DocumentType("html", "", "");
        Assert.assertEquals("<!doctype html>", html5.outerHtml());

        DocumentType publicDocType = new DocumentType("html", "-//IETF//DTD HTML//", "");
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//IETF//DTD HTML//\">", publicDocType.outerHtml());

        DocumentType systemDocType = new DocumentType("html", "", "http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd");
        Assert.assertEquals("<!DOCTYPE html SYSTEM \"http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd\">", systemDocType.outerHtml());

        DocumentType combo = new DocumentType("notHtml", "--public", "--system");
        Assert.assertEquals("<!DOCTYPE notHtml PUBLIC \"--public\" \"--system\">", combo.outerHtml());
        Assert.assertEquals("notHtml", combo.name());
        Assert.assertEquals("--public", combo.publicId());
        Assert.assertEquals("--system", combo.systemId());
    }

    @Test public void testRoundTrip() {
        String base = "<!DOCTYPE html>";
        Assert.assertEquals("<!doctype html>", htmlOutput(base));
        Assert.assertEquals(base, xmlOutput(base));

        String publicDoc = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
        Assert.assertEquals(publicDoc, htmlOutput(publicDoc));
        Assert.assertEquals(publicDoc, xmlOutput(publicDoc));

        String systemDoc = "<!DOCTYPE html SYSTEM \"exampledtdfile.dtd\">";
        Assert.assertEquals(systemDoc, htmlOutput(systemDoc));
        Assert.assertEquals(systemDoc, xmlOutput(systemDoc));

        String legacyDoc = "<!DOCTYPE html SYSTEM \"about:legacy-compat\">";
        Assert.assertEquals(legacyDoc, htmlOutput(legacyDoc));
        Assert.assertEquals(legacyDoc, xmlOutput(legacyDoc));
    }

    private String htmlOutput(String in) {
        DocumentType type = (DocumentType) Jsoup.parse(in).childNode(0);
        return type.outerHtml();
    }

    private String xmlOutput(String in) {
        return Jsoup.parse(in, "", Parser.xmlParser()).childNode(0).outerHtml();
    }
}
