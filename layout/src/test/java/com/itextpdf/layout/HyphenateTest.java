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


import com.itextpdf.layout.hyphenation.HyphenationConfig;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

@Category(UnitTest.class)
@RunWith(Parameterized.class)
public class HyphenateTest extends ExtendedITextTest {

    private String lang;

    public HyphenateTest(String lang) {
        this.lang = lang;
    }

    @Parameterized.Parameters
    public static Collection langs() {
        return Arrays.asList(
			"af",
//			"as",
//			"bg",
//			"bn",
//			"ca",
//			"cop",
			"cs",
			"cy",
			"da",
			"de",
			"de_1901",
			"de_CH",
			"de_DR",
//			"el",
//			"el_Polyton",
			"en",
			"en_GB",
			"en_US",
			"eo",
//			"es",
//			"et",
//			"eu",
//			"fi",
			"fr",
			"ga",
			"gl",
//			"grc",
//			"gu",
//			"hi",
			"hr",
			"hsb",
//			"hu",
//			"hy",
			"ia",
			"id",
			"is",
			"it",
			"kmr",
//			"kn",
			"la",
//			"lo",
//			"lt",
//			"lv",
//			"ml",
//			"mn",
//			"mr",
			"nb",
			"nl",
			"nn",
			"no",
//			"or",
//			"pa",
			"pl",
			"pt",
			"ro",
//			"ru",
			"sa",
			"sk",
			"sl",
//			"sr_Cyrl",
			"sr_Latn",
//			"sv",
//			"ta",
//			"te",
			"tk",
//			"tr",
//			"uk",
			"zh_Latn"
        );
    }

    @Test
    public void loadConfigTest() throws IOException, InterruptedException {
        String[] parts = lang.split("_");
        lang = parts[0];
        String country = (parts.length == 2) ? parts[1] : null;
        HyphenationConfig config = new HyphenationConfig(lang, country, 3, 3);
        Assert.assertNotNull("Language: " + lang + ": hyphenate() returned null", config.hyphenate("country"));
    }
}
