/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.io.font.otf;

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class LanguageRecordTest extends ExtendedITextTest {

    @Test
    public void featuresRequiredTest() {
        LanguageRecord languageRecord = new LanguageRecord();
        languageRecord.setFeatureRequired(1);

        Assertions.assertEquals(1, languageRecord.getFeatureRequired());
    }

    @Test
    public void taggingTest() {
        LanguageRecord languageRecord = new LanguageRecord();
        languageRecord.setTag("tagname");

        Assertions.assertEquals("tagname", languageRecord.getTag());
    }

    @Test
    public void featuresTest() {
        LanguageRecord languageRecord = new LanguageRecord();
        int[] features = new int[2];
        languageRecord.setFeatures(features);

        Assertions.assertEquals(2, languageRecord.getFeatures().length);
    }
}

