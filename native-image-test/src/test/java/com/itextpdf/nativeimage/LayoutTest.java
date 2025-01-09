/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.nativeimage;

import com.itextpdf.layout.hyphenation.Hyphenation;
import com.itextpdf.layout.hyphenation.HyphenationConfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LayoutTest {

    @Test
    void afHyphenation() {
        Hyphenation hyph = getHyphenation("af", "Country");
        Assertions.assertArrayEquals(new int[]{4}, hyph.getHyphenationPoints());
    }

    @Test
    void asHyphenation() {
        Hyphenation hyph = getHyphenation("as", "\u09A8\u09AE\u09B8\u09CD\u0995\u09BE\u09F0");
        Assertions.assertArrayEquals(new int[]{2}, hyph.getHyphenationPoints());
    }

    @Test
    void bgHyphenation() {
        Hyphenation hyph = getHyphenation("bg", "\u0417\u0434\u0440\u0430\u0432\u0435\u0439");
        Assertions.assertArrayEquals(new int[]{4}, hyph.getHyphenationPoints());
    }

    @Test
    void bnHyphenation() {
        Hyphenation hyph = getHyphenation("bn", "\u0986\u09B2\u09BE\u0987\u0995\u09C1\u09AE");
        Assertions.assertArrayEquals(new int[]{}, hyph.getHyphenationPoints());
    }

    @Test
    void caHyphenation() {
        Hyphenation hyph = getHyphenation("ca", "Benvinguts");
        Assertions.assertArrayEquals(new int[]{3, 6}, hyph.getHyphenationPoints());
    }

    @Test
    void copHyphenation() {
        Hyphenation hyph = getHyphenation("cop", "\u2C98\u2C89\u2CA7\u2CA2\u2C89\u2C99\u0300\u2C9B\u2CAD\u2C8F\u2C99\u2C93");
        Assertions.assertArrayEquals(new int[]{2, 10}, hyph.getHyphenationPoints());
    }

    @Test
    void csHyphenation() {
        Hyphenation hyph = getHyphenation("cs", "country");
        Assertions.assertArrayEquals(new int[]{4, 5}, hyph.getHyphenationPoints());
    }

    @Test
    void cyHyphenation() {
        Hyphenation hyph = getHyphenation("cy", "country");
        Assertions.assertArrayEquals(new int[]{4, 5}, hyph.getHyphenationPoints());
    }

    @Test
    void daHyphenation() {
        Hyphenation hyph = getHyphenation("da", "country");
        Assertions.assertArrayEquals(new int[]{2, 4}, hyph.getHyphenationPoints());
    }

    @Test
    void deHyphenation() {
        Hyphenation hyph = getHyphenation("de", "Tage");
        Assertions.assertArrayEquals(new int[]{2}, hyph.getHyphenationPoints());
    }

    @Test
    void de1901Hyphenation() {
        Hyphenation hyph = getHyphenation("de_1901", "Tage");
        Assertions.assertArrayEquals(new int[]{2}, hyph.getHyphenationPoints());
    }

    @Test
    void deCHHyphenation() {
        Hyphenation hyph = getHyphenation("de_CH", "Tage");
        Assertions.assertArrayEquals(new int[]{2}, hyph.getHyphenationPoints());
    }

    @Test
    void deDRHyphenation() {
        Hyphenation hyph = getHyphenation("de_DR", "Tage");
        Assertions.assertArrayEquals(new int[]{2}, hyph.getHyphenationPoints());
    }

    @Test
    void elHyphenation() {
        Hyphenation hyph = getHyphenation("el", "\u03BA\u03B1\u03BB\u03B7\u03BC\u03AD\u03C1\u03B1");
        Assertions.assertArrayEquals(new int[]{2, 4}, hyph.getHyphenationPoints());
    }

    @Test
    void elPolytonHyphenation() {
        Hyphenation hyph = getHyphenation("el_Polyton", "\u03BA\u03B1\u03BB\u03B7\u03BC\u03AD\u03C1\u03B1");
        Assertions.assertArrayEquals(new int[]{2, 4, 6}, hyph.getHyphenationPoints());
    }

    @Test
    void enHyphenation() {
        Hyphenation hyph = getHyphenation("en", "country");
        Assertions.assertArrayEquals(new int[]{4}, hyph.getHyphenationPoints());
    }

    @Test
    void enGBHyphenation() {
        Hyphenation hyph = getHyphenation("en_GB", "country");
        Assertions.assertArrayEquals(new int[]{4}, hyph.getHyphenationPoints());
    }

    @Test
    void enUSHyphenation() {
        Hyphenation hyph = getHyphenation("en_US", "country");
        Assertions.assertArrayEquals(new int[]{4}, hyph.getHyphenationPoints());
    }

    @Test
    void eoHyphenation() {
        Hyphenation hyph = getHyphenation("eo", "country");
        Assertions.assertArrayEquals(new int[]{2, 4}, hyph.getHyphenationPoints());
    }

    @Test
    void esHyphenation() {
        Hyphenation hyph = getHyphenation("es", "gracias");
        Assertions.assertArrayEquals(new int[]{3}, hyph.getHyphenationPoints());
    }

    @Test
    void etHyphenation() {
        Hyphenation hyph = getHyphenation("et", "Vabandust");
        Assertions.assertArrayEquals(new int[]{2, 5}, hyph.getHyphenationPoints());
    }

    @Test
    void euHyphenation() {
        Hyphenation hyph = getHyphenation("eu", "euskara");
        Assertions.assertArrayEquals(new int[]{3, 5}, hyph.getHyphenationPoints());
    }

    @Test
    void fiHyphenation() {
        Hyphenation hyph = getHyphenation("fi", "N\u00E4kemiin");
        Assertions.assertArrayEquals(new int[]{2, 4}, hyph.getHyphenationPoints());
    }

    @Test
    void frHyphenation() {
        Hyphenation hyph = getHyphenation("fr", "country");
        Assertions.assertArrayEquals(new int[]{4}, hyph.getHyphenationPoints());
    }

    @Test
    void gaHyphenation() {
        Hyphenation hyph = getHyphenation("ga", "country");
        Assertions.assertArrayEquals(new int[]{4}, hyph.getHyphenationPoints());
    }

    @Test
    void glHyphenation() {
        Hyphenation hyph = getHyphenation("gl", "country");
        Assertions.assertArrayEquals(new int[]{2, 4}, hyph.getHyphenationPoints());
    }

    @Test
    void grcHyphenation() {
        Hyphenation hyph = getHyphenation("grc", "\u03BA\u03B1\u03BB\u03B7\u03BC\u03AD\u03C1\u03B1");
        Assertions.assertArrayEquals(new int[]{2, 4, 6}, hyph.getHyphenationPoints());
    }

    @Test
    void guHyphenation() {
        Hyphenation hyph = getHyphenation("hi", "\u0938\u0941\u092A\u094D\u0930\u092D\u093E\u0924\u092E\u094D");
        Assertions.assertArrayEquals(new int[]{5}, hyph.getHyphenationPoints());
    }

    @Test
    void hrHyphenation() {
        Hyphenation hyph = getHyphenation("hr", "country");
        Assertions.assertArrayEquals(new int[]{2, 4}, hyph.getHyphenationPoints());
    }

    @Test
    void hsbHyphenation() {
        Hyphenation hyph = getHyphenation("hsb", "country");
        Assertions.assertArrayEquals(new int[]{4}, hyph.getHyphenationPoints());
    }

    @Test
    void huHyphenation() {
        Hyphenation hyph = getHyphenation("hu", "sziasztok");
        Assertions.assertArrayEquals(new int[]{3, 6}, hyph.getHyphenationPoints());
    }

    @Test
    void hyHyphenation() {
        Hyphenation hyph = getHyphenation("hy", "\u0577\u0576\u0578\u0580\u0570\u0561\u056F\u0561\u056C\u0578\u0582\u0569\u0575\u0578\u0582\u0576");
        Assertions.assertArrayEquals(new int[]{6, 8}, hyph.getHyphenationPoints());
    }

    @Test
    void iaHyphenation() {
        Hyphenation hyph = getHyphenation("ia", "country");
        Assertions.assertArrayEquals(new int[]{3, 4}, hyph.getHyphenationPoints());
    }

    @Test
    void idHyphenation() {
        Hyphenation hyph = getHyphenation("id", "country");
        Assertions.assertArrayEquals(new int[]{2, 4}, hyph.getHyphenationPoints());
    }

    @Test
    void isHyphenation() {
        Hyphenation hyph = getHyphenation("is", "country");
        Assertions.assertArrayEquals(new int[]{4}, hyph.getHyphenationPoints());
    }

    @Test
    void itHyphenation() {
        Hyphenation hyph = getHyphenation("it", "country");
        Assertions.assertArrayEquals(new int[]{4}, hyph.getHyphenationPoints());
    }

    @Test
    void kmrHyphenation() {
        Hyphenation hyph = getHyphenation("kmr", "country");
        Assertions.assertArrayEquals(new int[]{4}, hyph.getHyphenationPoints());
    }

    @Test
    void knHyphenation() {
        Hyphenation hyph = getHyphenation("kn", "\u0C95\u0CA8\u0CCD\u0CA8\u0CA1");
        Assertions.assertArrayEquals(new int[]{}, hyph.getHyphenationPoints());
    }

    @Test
    void laHyphenation() {
        Hyphenation hyph = getHyphenation("la", "country");
        Assertions.assertArrayEquals(new int[]{4}, hyph.getHyphenationPoints());
    }

    @Test
    void loHyphenation() {
        Hyphenation hyph = getHyphenation("lo", "\u0E8D\u0EB4\u0E99\u0E94\u0EB5\u0E95\u0EC9\u0EAD\u0E99\u0EAE\u0EB1\u0E9A");
        Assertions.assertArrayEquals(new int[]{3}, hyph.getHyphenationPoints());
    }

    @Test
    void ltHyphenation() {
        Hyphenation hyph = getHyphenation("lt", "Labanakt");
        Assertions.assertArrayEquals(new int[]{2, 4}, hyph.getHyphenationPoints());
    }

    @Test
    void lvHyphenation() {
        Hyphenation hyph = getHyphenation("lv", "Labvakar");
        Assertions.assertArrayEquals(new int[]{3, 5}, hyph.getHyphenationPoints());
    }

    @Test
    void mlHyphenation() {
        Hyphenation hyph = getHyphenation("ml", "\u0D38\u0D4D\u0D35\u0D3E\u0D17\u0D24\u0D02");
        Assertions.assertArrayEquals(new int[]{}, hyph.getHyphenationPoints());
    }

    @Test
    void mnHyphenation() {
        Hyphenation hyph = getHyphenation("mn", "\u04E8\u0440\u0448\u04E9\u04E9\u0433\u04E9\u04E9\u0440\u044D\u0439");
        Assertions.assertArrayEquals(new int[]{2, 5, 8}, hyph.getHyphenationPoints());
    }

    @Test
    void mrHyphenation() {
        Hyphenation hyph = getHyphenation("mr", "\u0928\u092E\u0938\u094D\u0915\u093E\u0930");
        Assertions.assertArrayEquals(new int[]{2}, hyph.getHyphenationPoints());
    }

    @Test
    void nbHyphenation() {
        Hyphenation hyph = getHyphenation("nb", "country");
        Assertions.assertArrayEquals(new int[]{4}, hyph.getHyphenationPoints());
    }

    @Test
    void nlHyphenation() {
        Hyphenation hyph = getHyphenation("nl", "country");
        Assertions.assertArrayEquals(new int[]{4}, hyph.getHyphenationPoints());
    }

    @Test
    void nnHyphenation() {
        Hyphenation hyph = getHyphenation("nn", "country");
        Assertions.assertArrayEquals(new int[]{4}, hyph.getHyphenationPoints());
    }

    @Test
    void noHyphenation() {
        Hyphenation hyph = getHyphenation("no", "country");
        Assertions.assertArrayEquals(new int[]{4}, hyph.getHyphenationPoints());
    }

    @Test
    void orHyphenation() {
        Hyphenation hyph = getHyphenation("or", "\u0B28\u0B2E\u0B38\u0B4D\u0B15\u0B3E\u0B30");
        Assertions.assertArrayEquals(new int[]{2}, hyph.getHyphenationPoints());
    }

    @Test
    void paHyphenation() {
        Hyphenation hyph = getHyphenation("pa", "\u0A28\u0A2E\u0A38\u0A15\u0A3E\u0A30");
        Assertions.assertArrayEquals(new int[]{2}, hyph.getHyphenationPoints());
    }

    @Test
    void plHyphenation() {
        Hyphenation hyph = getHyphenation("pl", "country");
        Assertions.assertArrayEquals(new int[]{2, 4}, hyph.getHyphenationPoints());
    }

    @Test
    void ptHyphenation() {
        Hyphenation hyph = getHyphenation("pt", "country");
        Assertions.assertArrayEquals(new int[]{2, 4}, hyph.getHyphenationPoints());
    }

    @Test
    void roHyphenation() {
        Hyphenation hyph = getHyphenation("ro", "country");
        Assertions.assertArrayEquals(new int[]{2, 4}, hyph.getHyphenationPoints());
    }

    @Test
    void ruHyphenation() {
        Hyphenation hyph = getHyphenation("ru", "\u0437\u0434\u0440\u0430\u0432\u0441\u0442\u0432\u0443\u0439");
        Assertions.assertArrayEquals(new int[]{5}, hyph.getHyphenationPoints());
    }

    @Test
    void saHyphenation() {
        Hyphenation hyph = getHyphenation("sa", "country");
        Assertions.assertArrayEquals(new int[]{2, 3}, hyph.getHyphenationPoints());
    }

    @Test
    void skHyphenation() {
        Hyphenation hyph = getHyphenation("sk", "country");
        Assertions.assertArrayEquals(new int[]{4}, hyph.getHyphenationPoints());
    }

    @Test
    void slHyphenation() {
        Hyphenation hyph = getHyphenation("sl", "country");
        Assertions.assertArrayEquals(new int[]{2, 4}, hyph.getHyphenationPoints());
    }

    @Test
    void srCyrlHyphenation() {
        Hyphenation hyph = getHyphenation("sr_Cyrl", "\u0414\u043E\u0431\u0440\u043E\u0434\u043E\u0448\u043B\u0438");
        Assertions.assertArrayEquals(new int[]{2, 5, 7}, hyph.getHyphenationPoints());
    }

    @Test
    void srLatnHyphenation() {
        Hyphenation hyph = getHyphenation("sr_Latn", "country");
        Assertions.assertArrayEquals(new int[]{2, 4}, hyph.getHyphenationPoints());
    }

    @Test
    void svHyphenation() {
        Hyphenation hyph = getHyphenation("sv", "V\u00E4lkommen");
        Assertions.assertArrayEquals(new int[]{3, 6}, hyph.getHyphenationPoints());
    }

    @Test
    void taHyphenation() {
        Hyphenation hyph = getHyphenation("ta", "\u0BB5\u0BBE\u0BB0\u0BC1\u0B99\u0BCD\u0B95\u0BB3\u0BCD");
        Assertions.assertArrayEquals(new int[]{}, hyph.getHyphenationPoints());
    }

    @Test
    void teHyphenation() {
        Hyphenation hyph = getHyphenation("te", "\u0C38\u0C41\u0C38\u0C4D\u0C35\u0C3E\u0C17\u0C24\u0C02");
        Assertions.assertArrayEquals(new int[]{}, hyph.getHyphenationPoints());
    }

    @Test
    void tkHyphenation() {
        Hyphenation hyph = getHyphenation("tk", "country");
        Assertions.assertArrayEquals(new int[]{4, 5}, hyph.getHyphenationPoints());
    }

    @Test
    void trHyphenation() {
        Hyphenation hyph = getHyphenation("tr", "Merhaba");
        Assertions.assertArrayEquals(new int[]{3, 5}, hyph.getHyphenationPoints());
    }

    @Test
    void ukHyphenation() {
        Hyphenation hyph = getHyphenation("uk", "\u0437\u0434\u0440\u0430\u0432\u0441\u0442\u0432\u0443\u0439");
        Assertions.assertArrayEquals(new int[]{5}, hyph.getHyphenationPoints());
    }

    @Test
    void zhLatnHyphenation() {
        Hyphenation hyph = getHyphenation("zh_Latn", "country");
        Assertions.assertArrayEquals(new int[]{4}, hyph.getHyphenationPoints());
    }

    private Hyphenation getHyphenation(String lang, String word) {
        String[] parts = lang.split("_");
        lang = parts[0];
        String country = (parts.length == 2) ? parts[1] : null;
        HyphenationConfig config = new HyphenationConfig(lang, country, 2, 2);
        return config.hyphenate(word);
    }
}
