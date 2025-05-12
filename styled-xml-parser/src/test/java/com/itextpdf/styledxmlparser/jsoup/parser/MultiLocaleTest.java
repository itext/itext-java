/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.parser;

import com.itextpdf.styledxmlparser.jsoup.nodes.Attributes;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

@org.junit.jupiter.api.Tag("UnitTest")
public class MultiLocaleTest extends ExtendedITextTest {

    private final Locale defaultLocale = Locale.getDefault();

    public static Collection<Locale> locales() {
        return Arrays.asList(Locale.ENGLISH, new Locale("tr"));
    }

    @AfterEach
    public void setDefaultLocale() {
        Locale.setDefault(defaultLocale);
    }

    @ParameterizedTest
    @MethodSource("locales")
    public void caseSupport(Locale locale) {
        Locale.setDefault(locale);

        ParseSettings bothOn = new ParseSettings(true, true);
        ParseSettings bothOff = new ParseSettings(false, false);
        ParseSettings tagOn = new ParseSettings(true, false);
        ParseSettings attrOn = new ParseSettings(false, true);

        Assertions.assertEquals("IMG", bothOn.normalizeTag("IMG"));
        Assertions.assertEquals("ID", bothOn.normalizeAttribute("ID"));

        Assertions.assertEquals("img", bothOff.normalizeTag("IMG"));
        Assertions.assertEquals("id", bothOff.normalizeAttribute("ID"));

        Assertions.assertEquals("IMG", tagOn.normalizeTag("IMG"));
        Assertions.assertEquals("id", tagOn.normalizeAttribute("ID"));

        Assertions.assertEquals("img", attrOn.normalizeTag("IMG"));
        Assertions.assertEquals("ID", attrOn.normalizeAttribute("ID"));
    }

    @ParameterizedTest
    @MethodSource("locales")
    public void attributeCaseNormalization(Locale locale) {
        Locale.setDefault(locale);

        ParseSettings parseSettings = new ParseSettings(false, false);
        String normalizedAttribute = parseSettings.normalizeAttribute("HIDDEN");

        Assertions.assertEquals("hidden", normalizedAttribute);
    }

    @ParameterizedTest
    @MethodSource("locales")
    public void attributesCaseNormalization(Locale locale) {
        Locale.setDefault(locale);

        ParseSettings parseSettings = new ParseSettings(false, false);
        Attributes attributes = new Attributes();
        attributes.put("ITEM", "1");

        Attributes normalizedAttributes = parseSettings.normalizeAttributes(attributes);

        Assertions.assertEquals("item", normalizedAttributes.asList().get(0).getKey());
    }

    @ParameterizedTest
    @MethodSource("locales")
    public void canBeInsensitive(Locale locale) {
        Locale.setDefault(locale);

        Tag script1 = Tag.valueOf("script", ParseSettings.htmlDefault);
        Tag script2 = Tag.valueOf("SCRIPT", ParseSettings.htmlDefault);
        Assertions.assertSame(script1, script2);
    }
}
