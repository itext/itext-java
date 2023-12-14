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
package com.itextpdf.styledxmlparser.resolver.font;

import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;

/**
 * A basic {@link FontProvider} that allows configuring in the constructor which fonts are loaded by default.
 */
public class BasicFontProvider extends FontProvider {

    private static final String DEFAULT_FONT_FAMILY = "Times";

    /**
     * Creates a new {@link BasicFontProvider} instance.
     */
    public BasicFontProvider() {
        this(true, false);
    }

    /**
     * Creates a new {@link BasicFontProvider} instance.
     *
     * @param registerStandardPdfFonts use true if you want to register the standard Type 1 fonts (can't be embedded)
     * @param registerSystemFonts      use true if you want to register the system fonts (can require quite some resources)
     */
    public BasicFontProvider(boolean registerStandardPdfFonts, boolean registerSystemFonts) {
        this(registerStandardPdfFonts, registerSystemFonts, DEFAULT_FONT_FAMILY);
    }

    /**
     * Creates a new {@link BasicFontProvider} instance.
     *
     * @param registerStandardPdfFonts use true if you want to register the standard Type 1 fonts (can't be embedded)
     * @param registerSystemFonts      use true if you want to register the system fonts (can require quite some resources)
     * @param defaultFontFamily        default font family
     */
    public BasicFontProvider(boolean registerStandardPdfFonts, boolean registerSystemFonts, String defaultFontFamily) {
        super(defaultFontFamily);
        if (registerStandardPdfFonts) {
            addStandardPdfFonts();
        }
        if (registerSystemFonts) {
            addSystemFonts();
        }
    }

    /**
     * Creates a new {@link BasicFontProvider} instance.
     *
     * @param fontSet           predefined set of fonts, could be null.
     * @param defaultFontFamily default font family.
     */
    public BasicFontProvider(FontSet fontSet, String defaultFontFamily) {
        super(fontSet, defaultFontFamily);
    }
}
