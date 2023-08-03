/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.styledxmlparser.jsoup.safety;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.TextUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

@RunWith(Parameterized.class)
@Category(UnitTest.class)
public class MultiLocaleTest extends ExtendedITextTest {

    private final Locale defaultLocale = Locale.getDefault();

    @Parameterized.Parameters
    public static Collection<Locale> locales() {
        return Arrays.asList(Locale.ENGLISH, new Locale("tr"));
    }

    @After
    public void setDefaultLocale() {
        Locale.setDefault(defaultLocale);
    }

    private Locale locale;

    public MultiLocaleTest(Locale locale) {
        this.locale = locale;
    }

    @Test
    public void safeListedProtocolShouldBeRetained() {
        Locale.setDefault(locale);

        Whitelist safelist = (Whitelist) Whitelist.none()
                .addTags("a")
                .addAttributes("a", "href")
                .addProtocols("a", "href", "something");

        String cleanHtml = Jsoup.clean("<a href=\"SOMETHING://x\"></a>", safelist);

        Assert.assertEquals("<a href=\"SOMETHING://x\"></a>", TextUtil.stripNewlines(cleanHtml));
    }

    @Test
    public void cleanerSafeListedProtocolShouldBeRetained() {
        Locale.setDefault(locale);

        Safelist safelist = Safelist.none()
                .addTags("a")
                .addAttributes("a", "href")
                .addProtocols("a", "href", "something");

        String cleanHtml = Jsoup.clean("<a href=\"SOMETHING://x\"></a>", safelist);

        Assert.assertEquals("<a href=\"SOMETHING://x\"></a>", TextUtil.stripNewlines(cleanHtml));
    }

    @Test
    public void compatibilitySafeListedProtocolShouldBeRetained() {
        Locale.setDefault(locale);

        Whitelist safelist = (Whitelist) Whitelist.none()
                .addTags("a")
                .addAttributes("a", "href")
                .addProtocols("a", "href", "something");

        String cleanHtml = Jsoup.clean("<a href=\"SOMETHING://x\"></a>", safelist);

        Assert.assertEquals("<a href=\"SOMETHING://x\"></a>", TextUtil.stripNewlines(cleanHtml));
    }
}
