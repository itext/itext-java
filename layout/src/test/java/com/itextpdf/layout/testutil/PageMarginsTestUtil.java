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
package com.itextpdf.layout.testutil;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.PageMarginsTest;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.margins.MarginBoxName;
import com.itextpdf.layout.properties.margins.PageMarginContent;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared utility methods for page margin box test classes.
 *
 * <p>Provides the standard {@link PageMarginContent} configurations used
 * across {@link PageMarginsTest} and its related test classes. Extracting
 * them here avoids duplication in standalone test classes that cannot extend
 * {@link PageMarginsTest}.
 */
public final class PageMarginsTestUtil {

    private PageMarginsTestUtil() {
    }

    /**
     * Returns a four-sided margin box configuration with coloured, labelled
     * content in each region:
     */
    public static List<PageMarginContent> getPageMargins1() {
        List<PageMarginContent> elements = new ArrayList<>();
        elements.add(new PageMarginContent(MarginBoxName.TOP, new Div()
                .add(new Paragraph("TEST TOP MARGIN"))
                .setBackgroundColor(ColorConstants.PINK).setHeight(200)));
        elements.add(new PageMarginContent(MarginBoxName.RIGHT, new Div()
                .add(new Paragraph("TEST RIGHT MARGIN"))
                .setBackgroundColor(ColorConstants.YELLOW).setWidth(200)));
        elements.add(new PageMarginContent(MarginBoxName.BOTTOM, new Div()
                .add(new Paragraph("TEST BOTTOM MARGIN\nTEST BOTTOM MARGIN\nTEST BOTTOM MARGIN"))
                .setBackgroundColor(ColorConstants.GREEN)));
        elements.add(new PageMarginContent(MarginBoxName.LEFT, new Div()
                .add(new Paragraph("TEST LEFT MARGIN, TEST LEFT MARGIN"))
                .setBackgroundColor(ColorConstants.BLUE)));
        return elements;
    }

    /**
     * Returns a four-sided margin box configuration with a lighter colour
     * palette and smaller fixed dimensions:
     */
    public static List<PageMarginContent> getPageMargins2() {
        List<PageMarginContent> elements = new ArrayList<>();
        elements.add(new PageMarginContent(MarginBoxName.TOP, new Div()
                .add(new Paragraph("TEST TOP MARGIN"))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY).setHeight(100)));
        elements.add(new PageMarginContent(MarginBoxName.RIGHT, new Div()
                .add(new Paragraph("TEST RIGHT MARGIN"))
                .setBackgroundColor(ColorConstants.CYAN)));
        elements.add(new PageMarginContent(MarginBoxName.BOTTOM, new Div()
                .add(new Paragraph("TEST BOTTOM MARGIN"))
                .setBackgroundColor(ColorConstants.ORANGE)));
        elements.add(new PageMarginContent(MarginBoxName.LEFT, new Div()
                .add(new Paragraph("TEST LEFT MARGIN"))
                .setBackgroundColor(ColorConstants.RED).setWidth(100)));
        return elements;
    }
}
