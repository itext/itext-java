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
package com.itextpdf.styledxmlparser.jsoup.safety;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.nodes.Entities;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 Tests for the deprecated {@link com.itextpdf.styledxmlparser.jsoup.safety.Whitelist} class source compatibility. Will be removed in
 <code>v.1.15.1</code>. No net new tests here so safe to blow up.
 */
@Category(UnitTest.class)
public class CompatibilityTest extends ExtendedITextTest {
    @Test
    public void resolvesRelativeLinks() {
        String html = "<a href='/foo'>Link</a><img src='/bar'>";
        String clean = Jsoup.clean(html, "http://example.com/", Whitelist.basicWithImages());
        Assert.assertEquals("<a href=\"http://example.com/foo\" rel=\"nofollow\">Link</a>\n<img src=\"http://example.com/bar\">", clean);
    }

    @Test
    public void testDropsUnknownTags() {
        String h = "<p><custom foo=true>Test</custom></p>";
        String cleanHtml = Jsoup.clean(h, Whitelist.relaxed());
        Assert.assertEquals("<p>Test</p>", cleanHtml);
    }

    @Test
    public void preservesRelativeLinksIfConfigured() {
        String html = "<a href='/foo'>Link</a><img src='/bar'> <img src='javascript:alert()'>";
        String clean = Jsoup.clean(html, "http://example.com/", Whitelist.basicWithImages().preserveRelativeLinks(true));
        Assert.assertEquals("<a href=\"/foo\" rel=\"nofollow\">Link</a>\n<img src=\"/bar\"> \n<img>", clean);
    }

    @Test
    public void handlesCustomProtocols() {
        String html = "<img src='cid:12345' /> <img src='data:gzzt' />";
        String dropped = Jsoup.clean(html, Whitelist.basicWithImages());
        Assert.assertEquals("<img> \n<img>", dropped);

        String preserved = Jsoup.clean(html, Whitelist.basicWithImages().addProtocols("img", "src", "cid", "data"));
        Assert.assertEquals("<img src=\"cid:12345\"> \n<img src=\"data:gzzt\">", preserved);
    }

    @Test
    public void handlesFramesets() {
        String dirty = "<html><head><script></script><noscript></noscript></head><frameset><frame src=\"foo\" /><frame src=\"foo\" /></frameset></html>";
        String clean = Jsoup.clean(dirty, Whitelist.basic());
        Assert.assertEquals("", clean); // nothing good can come out of that

        Document dirtyDoc = Jsoup.parse(dirty);
        Document cleanDoc = new Cleaner(Whitelist.basic()).clean(dirtyDoc);
        Assert.assertNotNull(cleanDoc);
        Assert.assertEquals(0, cleanDoc.body().childNodeSize());
    }

    @Test public void handlesCleanerFromWhitelist() {
        Cleaner cleaner = new Cleaner(Whitelist.basic());
        Document doc = Jsoup.parse("<script>Script</script><p>Text</p>");
        Document clean = cleaner.clean(doc);
        Assert.assertEquals("<p>Text</p>", clean.body().html());
    }

    @Test
    public void supplyOutputSettings() {
        // test that one can override the default document output settings
        Document.OutputSettings os = new Document.OutputSettings();
        os.prettyPrint(false);
        os.escapeMode(Entities.EscapeMode.extended);
        os.charset("ascii");

        String html = "<div><p>&bernou;</p></div>";
        String customOut = Jsoup.clean(html, "http://foo.com/", Whitelist.relaxed(), os);
        String defaultOut = Jsoup.clean(html, "http://foo.com/", Whitelist.relaxed());
        Assert.assertNotSame(defaultOut, customOut);

        Assert.assertEquals("<div><p>&Bscr;</p></div>", customOut); // entities now prefers shorted names if aliased
        Assert.assertEquals("<div>\n" +
            " <p>ℬ</p>\n" +
            "</div>", defaultOut);

        os.charset("ASCII");
        os.escapeMode(Entities.EscapeMode.base);
        String customOut2 = Jsoup.clean(html, "http://foo.com/", Whitelist.relaxed(), os);
        Assert.assertEquals("<div><p>&#x212c;</p></div>", customOut2);
    }
}
