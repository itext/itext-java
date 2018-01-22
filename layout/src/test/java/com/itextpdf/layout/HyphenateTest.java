/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
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
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Category(UnitTest.class)
public class HyphenateTest extends ExtendedITextTest {

    private List<TestParams> params = Arrays.<TestParams>asList(
            new TestParams("af"),
			new TestParams("as", false),
			new TestParams("bg", false),
			new TestParams("bn", false),
			new TestParams("ca", false),
			new TestParams("cop", false),
			new TestParams("cs"),
			new TestParams("cy"),
			new TestParams("da"),
			new TestParams("de"),
			new TestParams("de_1901"),
			new TestParams("de_CH"),
			new TestParams("de_DR"),
			new TestParams("el", false),
			new TestParams("el_Polyton", false),
			new TestParams("en"),
			new TestParams("en_GB"),
			new TestParams("en_US"),
			new TestParams("eo"),
			new TestParams("es", false),
			new TestParams("et", false),
			new TestParams("eu", false),
			new TestParams("fi", false),
			new TestParams("fr"),
			new TestParams("ga"),
			new TestParams("gl"),
			new TestParams("grc", false),
			new TestParams("gu", false),
			new TestParams("hi", false),
			new TestParams("hr"),
			new TestParams("hsb"),
			new TestParams("hu", false),
			new TestParams("hy", false),
			new TestParams("ia"),
			new TestParams("id"),
			new TestParams("is"),
			new TestParams("it"),
			new TestParams("kmr"),
			new TestParams("kn", false),
			new TestParams("la"),
			new TestParams("lo", false),
			new TestParams("lt", false),
			new TestParams("lv", false),
			new TestParams("ml", false),
			new TestParams("mn", false),
			new TestParams("mr", false),
			new TestParams("nb"),
			new TestParams("nl"),
			new TestParams("nn"),
			new TestParams("no"),
			new TestParams("or", false),
			new TestParams("pa", false),
			new TestParams("pl"),
			new TestParams("pt"),
			new TestParams("ro"),
			new TestParams("ru", "здравствуй"),
			new TestParams("sa"),
			new TestParams("sk"),
			new TestParams("sl"),
			new TestParams("sr_Cyrl", false),
			new TestParams("sr_Latn"),
			new TestParams("sv", false),
			new TestParams("ta", false),
			new TestParams("te", false),
			new TestParams("tk"),
			new TestParams("tr", false),
			new TestParams("uk", "здравствуй"),
			new TestParams("zh_Latn")
        );

    private List<String> errors = new ArrayList<>();

    @Test
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
