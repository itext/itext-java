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
package com.itextpdf.styledxmlparser.css;

import com.itextpdf.styledxmlparser.css.font.CssFontFace;
import com.itextpdf.styledxmlparser.css.font.CssFontFace.CssFontFaceSrc;
import com.itextpdf.test.ExtendedITextTest;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CssFontFaceTest extends ExtendedITextTest {

    @Test
    public void createCssFontFaceTest() {
        List<CssDeclaration> properties = new ArrayList<>();
        properties.add(new CssDeclaration("font-family", "Droid Italic"));
        properties.add(new CssDeclaration("src", "url(\"web-fonts/droid-serif-italic.ttf\")"));

        CssFontFace fontFace = CssFontFace.create(properties);

        Assertions.assertNotNull(fontFace);
        Assertions.assertEquals("droid italic", fontFace.getFontFamily());
        List<CssFontFaceSrc> sources = fontFace.getSources();
        Assertions.assertNotNull(sources);
        Assertions.assertEquals(1, sources.size());
        Assertions.assertEquals("web-fonts/droid-serif-italic.ttf", sources.get(0).getSrc());
    }

    @Test
    public void createCssFontFaceNullSrcTest() {
        List<CssDeclaration> properties = new ArrayList<>();
        properties.add(new CssDeclaration("font-family", "Droid Italic"));
        properties.add(new CssDeclaration("src", null));

        CssFontFace fontFace = CssFontFace.create(properties);

        Assertions.assertNull(fontFace);
    }

    @Test
    public void createCssFontFaceNullFontFamilyTest() {
        List<CssDeclaration> properties = new ArrayList<>();
        properties.add(new CssDeclaration("font-family", ""));
        properties.add(new CssDeclaration("src", "some_directory/droid-serif-italic.ttf"));

        CssFontFace fontFace = CssFontFace.create(properties);

        Assertions.assertNull(fontFace);
    }

}
