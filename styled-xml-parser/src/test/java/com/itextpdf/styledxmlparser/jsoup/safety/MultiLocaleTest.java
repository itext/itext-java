/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.safety;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.TextUtil;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("UnitTest")
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
    public void safeListedProtocolShouldBeRetained(Locale locale) {
        Locale.setDefault(locale);

        Whitelist safelist = (Whitelist) Whitelist.none()
                .addTags("a")
                .addAttributes("a", "href")
                .addProtocols("a", "href", "something");

        String cleanHtml = Jsoup.clean("<a href=\"SOMETHING://x\"></a>", safelist);

        Assertions.assertEquals("<a href=\"SOMETHING://x\"></a>", TextUtil.stripNewlines(cleanHtml));
    }

    @ParameterizedTest
    @MethodSource("locales")
    public void cleanerSafeListedProtocolShouldBeRetained(Locale locale) {
        Locale.setDefault(locale);

        Safelist safelist = Safelist.none()
                .addTags("a")
                .addAttributes("a", "href")
                .addProtocols("a", "href", "something");

        String cleanHtml = Jsoup.clean("<a href=\"SOMETHING://x\"></a>", safelist);

        Assertions.assertEquals("<a href=\"SOMETHING://x\"></a>", TextUtil.stripNewlines(cleanHtml));
    }

    @ParameterizedTest
    @MethodSource("locales")
    public void compatibilitySafeListedProtocolShouldBeRetained(Locale locale) {
        Locale.setDefault(locale);

        Whitelist safelist = (Whitelist) Whitelist.none()
                .addTags("a")
                .addAttributes("a", "href")
                .addProtocols("a", "href", "something");

        String cleanHtml = Jsoup.clean("<a href=\"SOMETHING://x\"></a>", safelist);

        Assertions.assertEquals("<a href=\"SOMETHING://x\"></a>", TextUtil.stripNewlines(cleanHtml));
    }
}
