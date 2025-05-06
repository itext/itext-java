/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
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
