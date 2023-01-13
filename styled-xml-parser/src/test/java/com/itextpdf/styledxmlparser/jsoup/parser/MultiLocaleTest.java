/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
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
package com.itextpdf.styledxmlparser.jsoup.parser;

import com.itextpdf.styledxmlparser.jsoup.nodes.Attributes;
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
    public void caseSupport() {
        Locale.setDefault(locale);

        ParseSettings bothOn = new ParseSettings(true, true);
        ParseSettings bothOff = new ParseSettings(false, false);
        ParseSettings tagOn = new ParseSettings(true, false);
        ParseSettings attrOn = new ParseSettings(false, true);

        Assert.assertEquals("IMG", bothOn.normalizeTag("IMG"));
        Assert.assertEquals("ID", bothOn.normalizeAttribute("ID"));

        Assert.assertEquals("img", bothOff.normalizeTag("IMG"));
        Assert.assertEquals("id", bothOff.normalizeAttribute("ID"));

        Assert.assertEquals("IMG", tagOn.normalizeTag("IMG"));
        Assert.assertEquals("id", tagOn.normalizeAttribute("ID"));

        Assert.assertEquals("img", attrOn.normalizeTag("IMG"));
        Assert.assertEquals("ID", attrOn.normalizeAttribute("ID"));
    }

    @Test
    public void attributeCaseNormalization() {
        Locale.setDefault(locale);

        ParseSettings parseSettings = new ParseSettings(false, false);
        String normalizedAttribute = parseSettings.normalizeAttribute("HIDDEN");

        Assert.assertEquals("hidden", normalizedAttribute);
    }

    @Test
    public void attributesCaseNormalization() {
        Locale.setDefault(locale);

        ParseSettings parseSettings = new ParseSettings(false, false);
        Attributes attributes = new Attributes();
        attributes.put("ITEM", "1");

        Attributes normalizedAttributes = parseSettings.normalizeAttributes(attributes);

        Assert.assertEquals("item", normalizedAttributes.asList().get(0).getKey());
    }

    @Test
    public void canBeInsensitive() {
        Locale.setDefault(locale);

        Tag script1 = Tag.valueOf("script", ParseSettings.htmlDefault);
        Tag script2 = Tag.valueOf("SCRIPT", ParseSettings.htmlDefault);
        Assert.assertSame(script1, script2);
    }
}
