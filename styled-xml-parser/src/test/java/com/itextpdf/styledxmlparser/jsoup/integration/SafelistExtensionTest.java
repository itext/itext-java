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
package com.itextpdf.styledxmlparser.jsoup.integration;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.TextUtil;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attribute;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.safety.Safelist;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

/**
 Check that we can extend Safelist methods
 */
@Tag("IntegrationTest")
public class SafelistExtensionTest extends ExtendedITextTest {
    @Test public void canCustomizeSafeTests() {
        OpenSafelist openSafelist = new OpenSafelist(Safelist.relaxed());
        Safelist safelist = Safelist.relaxed();

        String html = "<p><opentag openattr>Hello</opentag></p>";

        String openClean = Jsoup.clean(html, openSafelist);
        String clean = Jsoup.clean(html, safelist);

        Assertions.assertEquals("<p><opentag openattr=\"\">Hello</opentag></p>", TextUtil.stripNewlines(openClean));
        Assertions.assertEquals("<p>Hello</p>", clean);
    }

    // passes tags and attributes starting with "open"
    private static class OpenSafelist extends Safelist {
        public OpenSafelist(Safelist safelist) {
            super(safelist);
        }

        @Override
        protected boolean isSafeAttribute(String tagName, Element el, Attribute attr) {
            if (attr.getKey().startsWith("open"))
                return true;
            return super.isSafeAttribute(tagName, el, attr);
        }

        @Override
        protected boolean isSafeTag(String tag) {
            if (tag.startsWith("open"))
                return true;
            return super.isSafeTag(tag);
        }
    }
}
