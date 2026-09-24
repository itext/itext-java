/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.layout.properties.margins;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Text;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class FootnoteAnchorUnitTest extends ExtendedITextTest {

    @Test
    public void footnoteOnlyConstructorSetsDefaultStyleNeededFalseTest() {
        FootnoteAnchor anchor = new FootnoteAnchor(new Footnote("test"));

        Assertions.assertFalse(anchor.isDefaultStyleNeeded());
        Assertions.assertEquals("*", ((Text) anchor.getFootnoteAnchor()).getText());
    }

    @Test
    public void textConstructorSetsDefaultStyleNeededTrueTest() {
        FootnoteAnchor anchor = new FootnoteAnchor(new Text("1"), new Footnote("test"));

        Assertions.assertTrue(anchor.isDefaultStyleNeeded());
    }

    @Test
    public void stringConstructorSetsDefaultStyleNeededTrueTest() {
        FootnoteAnchor anchor = new FootnoteAnchor("1", new Footnote("test"));

        Assertions.assertTrue(anchor.isDefaultStyleNeeded());
    }

    @Test
    public void imageConstructorKeepsDefaultStyleDisabledTest() {
        FootnoteAnchor anchor = new FootnoteAnchor(
                new Image(ImageDataFactory.createRawImage(new byte[]{50, 21})), new Footnote("test"));

        Assertions.assertFalse(anchor.isDefaultStyleNeeded());
    }

    @Test
    public void changingAnchorToTextAfterFootnoteOnlyConstructorEnablesDefaultStyleTest() {
        FootnoteAnchor anchor = new FootnoteAnchor(new Footnote("test"));

        anchor.setFootnoteAnchor(new Text("new value"));

        Assertions.assertTrue(anchor.isDefaultStyleNeeded());
    }

    @Test
    public void changingAnchorToImageAfterFootnoteOnlyConstructorKeepsDefaultStyleDisabledTest() {
        FootnoteAnchor anchor = new FootnoteAnchor(new Footnote("test"));

        anchor.setFootnoteAnchor(new Image(ImageDataFactory.createRawImage(new byte[]{50, 20})));

        Assertions.assertFalse(anchor.isDefaultStyleNeeded());
    }

    @Test
    public void changingAnchorAfterFootnoteOnlyConstructorToAsteriskEnablesDefaultStyleTest() {
        FootnoteAnchor anchor = new FootnoteAnchor(new Footnote("test"));

        anchor.setFootnoteAnchor(new Text("*"));

        Assertions.assertTrue(anchor.isDefaultStyleNeeded());
    }

    @Test
    public void reassigningSameAnchorAfterFootnoteOnlyConstructorKeepsDefaultStyleDisabledTest() {
        FootnoteAnchor anchor = new FootnoteAnchor(new Footnote("test"));
        Text sameAnchor = (Text) anchor.getFootnoteAnchor();

        anchor.setFootnoteAnchor(sameAnchor);

        Assertions.assertFalse(anchor.isDefaultStyleNeeded());
    }
}
