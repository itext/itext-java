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
package com.itextpdf.layout;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.layout.hyphenation.Hyphenation;
import com.itextpdf.layout.hyphenation.HyphenationConfig;
import com.itextpdf.test.ExtendedITextTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class HyphenateTest extends ExtendedITextTest {

	private List<String> errors = new ArrayList<>();

	public static Iterable<Object[]> hyphenationProperties() {
		return Arrays.asList(new Object[][]{
				{"African", "af", "country"},
				{"Assamese", "as", "\u09A8\u09AE\u09B8\u09CD\u0995\u09BE\u09F0"},
				{"Bulgarian", "bg", "\u0417\u0434\u0440\u0430\u0432\u0435\u0439"},
				{"Bengali", "bn", "\u0986\u09B2\u09BE\u0987\u0995\u09C1\u09AE"},
				{"Catalan", "ca", "Benvinguts"},
				{"Coptic", "cop", "\u2C98\u2C89\u2CA7\u2CA2\u2C89\u2C99\u0300\u2C9B\u2CAD\u2C8F\u2C99\u2C93"},
				{"Czech", "cs", "country"},
				{"Welsh", "cy", "country"},
				{"Danish", "da", "country"},
				{"German", "de", "country"},
				{"German Belgium", "de_DE","14\u00a0Tagen 14\u00a0Tagen 14\u00a0Tagen "},
				{"German Germany", "de_DE","14\u20110Tagen 14\u2011Tagen 14\u20110Tagen "},
				{"German Traditional", "de_1901", "country"},
				{"Swiss German", "de_CH", "country"},
				{"New German orthography", "de_DR", "country"},
				{"Modern Greek", "el", "\u03BA\u03B1\u03BB\u03B7\u03BC\u03AD\u03C1\u03B1"},
				{"Greek Polytonic", "el_Polyton", "\u03BA\u03B1\u03BB\u03B7\u03BC\u03AD\u03C1\u03B1"},
				{"English", "en", "country"},
				{"English Great Britain", "en_GB", "country"},
				{"English United States", "en_US", "country"},
				{"Esperanto", "eo", "country"},
				{"Spanish", "es", "gracias"},
				{"Estonian", "et", "Vabandust"},
				{"Basque", "eu", "euskara"},
				{"Finnish", "fi", "N\u00E4kemiin"},
				{"French", "fr", "country"},
				{"Irish", "ga", "country"},
				{"Galician", "gl", "country"},
				{"Ancient Greek", "grc", "\u03BA\u03B1\u03BB\u03B7\u03BC\u03AD\u03C1\u03B1"},
				{"Gujarati", "gu", "\u0A97\u0AC1\u0A9C\u0AB0\u0ABE\u0AA4\u0AC0"},
				{"Hindi", "hi", "\u0938\u0941\u092A\u094D\u0930\u092D\u093E\u0924\u092E\u094D"},
				{"Croatian", "hr", "country"},
				{"Upper Sorbian", "hsb", "country"},
				{"Hungarian", "hu", "sziasztok"},
				{"Armenian", "hy", "\u0577\u0576\u0578\u0580\u0570\u0561\u056F\u0561\u056C\u0578\u0582\u0569\u0575\u0578\u0582\u0576"},
				{"Interlingua", "ia", "country"},
				{"Indonesian", "id", "country"},
				{"Icelandic", "is", "country"},
				{"Italian", "it", "country"},
				{"Kurmanji", "kmr", "country"},
				{"Kannada", "kn", "\u0C95\u0CA8\u0CCD\u0CA8\u0CA1"},
				{"Latin", "la", "country"},
				{"Lao", "lo", "\u0E8D\u0EB4\u0E99\u0E94\u0EB5\u0E95\u0EC9\u0EAD\u0E99\u0EAE\u0EB1\u0E9A"},
				{"Lithuanian", "lt", "Labanakt"},
				{"Latvian", "lv", "Labvakar"},
				{"Malayalam", "ml", "\u0D38\u0D4D\u0D35\u0D3E\u0D17\u0D24\u0D02"},
				{"Mongolian", "mn", "\u04E8\u0440\u0448\u04E9\u04E9\u0433\u04E9\u04E9\u0440\u044D\u0439"},
				{"Marathi", "mr", "\u0928\u092E\u0938\u094D\u0915\u093E\u0930"},
				{"Norwegian Bokmål", "nb", "country"},
				{"Dutch; Flemish", "nl", "country"},
				{"Norwegian Nynorsk", "nn", "country"},
				{"Norwegian", "no", "country"},
				{"Oriya", "or", "\u0B28\u0B2E\u0B38\u0B4D\u0B15\u0B3E\u0B30"},
				{"Panjabi; Punjabi", "pa", "\u0A28\u0A2E\u0A38\u0A15\u0A3E\u0A30"},
				{"Polish", "pl", "country"},
				{"Portuguese", "pt", "country"},
				{"Romanian; Moldavian; Moldovan", "ro", "country"},
				{"Russian", "ru", "\u0437\u0434\u0440\u0430\u0432\u0441\u0442\u0432\u0443\u0439"},
				{"Sanskrit", "sa", "country"},
				{"Slovak", "sk", "country"},
				{"Slovenian", "sl", "country"},
				{"Serbian Cyrillic", "sr_Cyrl", "\u0414\u043E\u0431\u0440\u043E\u0434\u043E\u0448\u043B\u0438"},
				{"Serbian Latin", "sr_Latn", "country"},
				{"Swedish", "sv", "V\u00E4lkommen"},
				{"Tamil", "ta", "\u0BB5\u0BBE\u0BB0\u0BC1\u0B99\u0BCD\u0B95\u0BB3\u0BCD"},
				{"Telugu", "te", "\u0C38\u0C41\u0C38\u0C4D\u0C35\u0C3E\u0C17\u0C24\u0C02"},
				{"Turkmen", "tk", "country"},
				{"Turkish", "tr", "Merhaba"},
				{"Ukrainian", "uk", "\u0437\u0434\u0440\u0430\u0432\u0441\u0442\u0432\u0443\u0439"},
				{"Chinese Latin", "zh_Latn", "country"}
		});
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("hyphenationProperties")
	public void runTest(String name, String lang, String testWord) {
		errors.clear();
		tryHyphenate(lang, testWord);
		Assertions.assertTrue(errors.isEmpty(), buildReport());
	}

	private void tryHyphenate(String lang, String testWorld) {
		String[] parts = lang.split("_");
		lang = parts[0];
		String country = (parts.length == 2) ? parts[1] : null;
		HyphenationConfig config = new HyphenationConfig(lang, country, 3, 3);
		Hyphenation result = config.hyphenate(testWorld);
		if (result == null) {
			errors.add(MessageFormatUtil.format("\nLanguage: {0}, error on hyphenate({1})", lang, testWorld));
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
}
