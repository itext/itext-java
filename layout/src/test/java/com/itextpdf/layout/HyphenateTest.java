/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.layout;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.layout.hyphenation.Hyphenation;
import com.itextpdf.layout.hyphenation.HyphenationConfig;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Category(IntegrationTest.class)
public class HyphenateTest extends ExtendedITextTest {

    private List<TestParams> params = Arrays.<TestParams>asList(
            new TestParams("af"),
			new TestParams("as", "\u09A8\u09AE\u09B8\u09CD\u0995\u09BE\u09F0"), //নমস্কাৰ
			new TestParams("bg", "\u0417\u0434\u0440\u0430\u0432\u0435\u0439"), //Здравей
			new TestParams("bn", "\u0986\u09B2\u09BE\u0987\u0995\u09C1\u09AE"), //আলাইকুম
			new TestParams("ca", "Benvinguts"),
			new TestParams("cop", "\u2C98\u2C89\u2CA7\u2CA2\u2C89\u2C99\u0300\u2C9B\u2CAD\u2C8F\u2C99\u2C93"), //ⲘⲉⲧⲢⲉⲙ̀ⲛⲭⲏⲙⲓ
			new TestParams("cs"),
			new TestParams("cy"),
			new TestParams("da"),
			new TestParams("de"),
			new TestParams("de_DE","14\u00a0Tagen 14\u00a0Tagen 14\u00a0Tagen "),
			new TestParams("de_DE","14\u20110Tagen 14\u2011Tagen 14\u20110Tagen "),
			new TestParams("de_1901"),
			new TestParams("de_CH"),
			new TestParams("de_DR"),
			new TestParams("el", "\u03BA\u03B1\u03BB\u03B7\u03BC\u03AD\u03C1\u03B1"), //καλημέρα
			new TestParams("el_Polyton", "\u03BA\u03B1\u03BB\u03B7\u03BC\u03AD\u03C1\u03B1"), //καλημέρα
			new TestParams("en"),
			new TestParams("en_GB"),
			new TestParams("en_US"),
			new TestParams("eo"),
			new TestParams("es", "gracias"),
			new TestParams("et", "Vabandust"),
			new TestParams("eu", "euskara"),
			new TestParams("fi", "N\u00E4kemiin"), //Näkemiin
			new TestParams("fr"),
			new TestParams("ga"),
			new TestParams("gl"),
			new TestParams("grc", "\u03BA\u03B1\u03BB\u03B7\u03BC\u03AD\u03C1\u03B1"), //καλημέρα
			new TestParams("gu", "\u0A97\u0AC1\u0A9C\u0AB0\u0ABE\u0AA4\u0AC0"), //ગુજરાતી
			new TestParams("hi", "\u0938\u0941\u092A\u094D\u0930\u092D\u093E\u0924\u092E\u094D"), //सुप्रभातम्
			new TestParams("hr"),
			new TestParams("hsb"),
			new TestParams("hu", "sziasztok"),
			new TestParams("hy", "\u0577\u0576\u0578\u0580\u0570\u0561\u056F\u0561\u056C\u0578\u0582\u0569\u0575\u0578\u0582\u0576"), //շնորհակալություն
			new TestParams("ia"),
			new TestParams("id"),
			new TestParams("is"),
			new TestParams("it"),
			new TestParams("kmr"),
			new TestParams("kn", "\u0C95\u0CA8\u0CCD\u0CA8\u0CA1"), //ಕನ್ನಡ
			new TestParams("la"),
			new TestParams("lo", "\u0E8D\u0EB4\u0E99\u0E94\u0EB5\u0E95\u0EC9\u0EAD\u0E99\u0EAE\u0EB1\u0E9A"), //ຍິນດີຕ້ອນຮັບ
			new TestParams("lt", "Labanakt"),
			new TestParams("lv", "Labvakar"),
			new TestParams("ml", "\u0D38\u0D4D\u0D35\u0D3E\u0D17\u0D24\u0D02"), //സ്വാഗതം
			new TestParams("mn", "\u04E8\u0440\u0448\u04E9\u04E9\u0433\u04E9\u04E9\u0440\u044D\u0439"), //Өршөөгөөрэй
			new TestParams("mr", "\u0928\u092E\u0938\u094D\u0915\u093E\u0930"), //नमस्कार
			new TestParams("nb"),
			new TestParams("nl"),
			new TestParams("nn"),
			new TestParams("no"),
			new TestParams("or", "\u0B28\u0B2E\u0B38\u0B4D\u0B15\u0B3E\u0B30"), //ନମସ୍କାର
			new TestParams("pa", "\u0A28\u0A2E\u0A38\u0A15\u0A3E\u0A30"), //ਨਮਸਕਾਰ
			new TestParams("pl"),
			new TestParams("pt"),
			new TestParams("ro"),
			new TestParams("ru", "\u0437\u0434\u0440\u0430\u0432\u0441\u0442\u0432\u0443\u0439"), //здравствуй
			new TestParams("sa"),
			new TestParams("sk"),
			new TestParams("sl"),
			new TestParams("sr_Cyrl", "\u0414\u043E\u0431\u0440\u043E\u0434\u043E\u0448\u043B\u0438"), //Добродошли
			new TestParams("sr_Latn"),
			new TestParams("sv", "V\u00E4lkommen"), //Välkommen
			new TestParams("ta", "\u0BB5\u0BBE\u0BB0\u0BC1\u0B99\u0BCD\u0B95\u0BB3\u0BCD"), //வாருங்கள்
			new TestParams("te", "\u0C38\u0C41\u0C38\u0C4D\u0C35\u0C3E\u0C17\u0C24\u0C02"), //సుస్వాగతం
			new TestParams("tk"),
			new TestParams("tr", "Merhaba"),
			new TestParams("uk", "\u0437\u0434\u0440\u0430\u0432\u0441\u0442\u0432\u0443\u0439"), //здравствуй
			new TestParams("zh_Latn")
        );

    private List<String> errors = new ArrayList<>();

    @Test
    @Ignore("DEVSIX-2036")
    public void runTest() {
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
