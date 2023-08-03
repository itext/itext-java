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
package com.itextpdf.layout;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.layout.hyphenation.Hyphenation;
import com.itextpdf.layout.hyphenation.HyphenationConfig;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Category(IntegrationTest.class)
public class HyphenateTest extends ExtendedITextTest {

    private List<TestParams> params = Arrays.<TestParams>asList(
            new TestParams("af"),

			//নমস্কাৰ
			new TestParams("as", "\u09A8\u09AE\u09B8\u09CD\u0995\u09BE\u09F0"),

			//Здравей
			new TestParams("bg", "\u0417\u0434\u0440\u0430\u0432\u0435\u0439"),

			//আলাইকুম
			new TestParams("bn", "\u0986\u09B2\u09BE\u0987\u0995\u09C1\u09AE"),
			new TestParams("ca", "Benvinguts"),

			//ⲘⲉⲧⲢⲉⲙ̀ⲛⲭⲏⲙⲓ
			new TestParams("cop", "\u2C98\u2C89\u2CA7\u2CA2\u2C89\u2C99\u0300\u2C9B\u2CAD\u2C8F\u2C99\u2C93"),
			new TestParams("cs"),
			new TestParams("cy"),
			new TestParams("da"),
			new TestParams("de"),
			new TestParams("de_DE","14\u00a0Tagen 14\u00a0Tagen 14\u00a0Tagen "),
			new TestParams("de_DE","14\u20110Tagen 14\u2011Tagen 14\u20110Tagen "),
			new TestParams("de_1901"),
			new TestParams("de_CH"),
			new TestParams("de_DR"),

			//καλημέρα
			new TestParams("el", "\u03BA\u03B1\u03BB\u03B7\u03BC\u03AD\u03C1\u03B1"),

			//καλημέρα
			new TestParams("el_Polyton", "\u03BA\u03B1\u03BB\u03B7\u03BC\u03AD\u03C1\u03B1"),
			new TestParams("en"),
			new TestParams("en_GB"),
			new TestParams("en_US"),
			new TestParams("eo"),
			new TestParams("es", "gracias"),
			new TestParams("et", "Vabandust"),
			new TestParams("eu", "euskara"),

			//Näkemiin
			new TestParams("fi", "N\u00E4kemiin"),
			new TestParams("fr"),
			new TestParams("ga"),
			new TestParams("gl"),

			//καλημέρα
			new TestParams("grc", "\u03BA\u03B1\u03BB\u03B7\u03BC\u03AD\u03C1\u03B1"),

			//ગુજરાતી
			new TestParams("gu", "\u0A97\u0AC1\u0A9C\u0AB0\u0ABE\u0AA4\u0AC0"),

			//सुप्रभातम्
			new TestParams("hi", "\u0938\u0941\u092A\u094D\u0930\u092D\u093E\u0924\u092E\u094D"),
			new TestParams("hr"),
			new TestParams("hsb"),
			new TestParams("hu", "sziasztok"),

			//շնորհակալություն
			new TestParams("hy", "\u0577\u0576\u0578\u0580\u0570\u0561\u056F\u0561\u056C\u0578\u0582\u0569\u0575\u0578\u0582\u0576"),
			new TestParams("ia"),
			new TestParams("id"),
			new TestParams("is"),
			new TestParams("it"),
			new TestParams("kmr"),

			//ಕನ್ನಡ
			new TestParams("kn", "\u0C95\u0CA8\u0CCD\u0CA8\u0CA1"),
			new TestParams("la"),

			//ຍິນດີຕ້ອນຮັບ
			new TestParams("lo", "\u0E8D\u0EB4\u0E99\u0E94\u0EB5\u0E95\u0EC9\u0EAD\u0E99\u0EAE\u0EB1\u0E9A"),
			new TestParams("lt", "Labanakt"),
			new TestParams("lv", "Labvakar"),

			//സ്വാഗതം
			new TestParams("ml", "\u0D38\u0D4D\u0D35\u0D3E\u0D17\u0D24\u0D02"),

			//Өршөөгөөрэй
			new TestParams("mn", "\u04E8\u0440\u0448\u04E9\u04E9\u0433\u04E9\u04E9\u0440\u044D\u0439"),

			//नमस्कार
			new TestParams("mr", "\u0928\u092E\u0938\u094D\u0915\u093E\u0930"),
			new TestParams("nb"),
			new TestParams("nl"),
			new TestParams("nn"),
			new TestParams("no"),

			//ନମସ୍କାର
			new TestParams("or", "\u0B28\u0B2E\u0B38\u0B4D\u0B15\u0B3E\u0B30"),

			//ਨਮਸਕਾਰ
			new TestParams("pa", "\u0A28\u0A2E\u0A38\u0A15\u0A3E\u0A30"),
			new TestParams("pl"),
			new TestParams("pt"),
			new TestParams("ro"),

			//здравствуй
			new TestParams("ru", "\u0437\u0434\u0440\u0430\u0432\u0441\u0442\u0432\u0443\u0439"),
			new TestParams("sa"),
			new TestParams("sk"),
			new TestParams("sl"),

			//Добродошли
			new TestParams("sr_Cyrl", "\u0414\u043E\u0431\u0440\u043E\u0434\u043E\u0448\u043B\u0438"),
			new TestParams("sr_Latn"),

			//Välkommen
			new TestParams("sv", "V\u00E4lkommen"),

			//வாருங்கள்
			new TestParams("ta", "\u0BB5\u0BBE\u0BB0\u0BC1\u0B99\u0BCD\u0B95\u0BB3\u0BCD"),

			//సుస్వాగతం
			new TestParams("te", "\u0C38\u0C41\u0C38\u0C4D\u0C35\u0C3E\u0C17\u0C24\u0C02"),
			new TestParams("tk"),
			new TestParams("tr", "Merhaba"),

			//здравствуй
			new TestParams("uk", "\u0437\u0434\u0440\u0430\u0432\u0441\u0442\u0432\u0443\u0439"),
			new TestParams("zh_Latn")
        );

    private List<String> errors = new ArrayList<>();

    @Test
    public void runTest() {
    	// This test can't be sped up because it uses of a lot of input data
        for (TestParams param : params) {
            tryHyphenate(param.lang, param.testWorld, param.shouldPass);
        }
        Assert.assertTrue(buildReport(), errors.isEmpty());
    }

    private void tryHyphenate(String lang, String testWorld, boolean shouldPass) {
        String[] parts = lang.split("_");
        lang = parts[0];
        String country = (parts.length == 2) ? parts[1] : null;
        HyphenationConfig config = new HyphenationConfig(lang, country, 3, 3);
        Hyphenation result = config.hyphenate(testWorld);
        if ( (result == null) == shouldPass) {
            errors.add(MessageFormatUtil.format("\nLanguage: {0}, error on hyphenate({1}), shouldPass: {2}", lang, testWorld, shouldPass));
        }
    }

    private String buildReport() {
        StringBuilder builder = new StringBuilder();
        builder.append("There are ").append(errors.size()).append(" errors.");
        for (String message : errors) {
            builder.append(message);
        }
        return builder.toString();
    }
    
    private static class TestParams {
        String lang;
        String testWorld;
        boolean shouldPass;

        public TestParams(String lang, String testWorld, boolean shouldPass) {
            this.lang = lang;
            this.testWorld = testWorld;
            this.shouldPass = shouldPass;
        }

        public TestParams(String lang, String testWorld) {
            this(lang, testWorld, true);
        }

        public TestParams(String lang, boolean shouldPass) {
            this(lang, "country", shouldPass);
        }
        
        public TestParams(String lang) {
            this(lang, "country", true);
        }
    }
}
