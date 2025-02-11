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
package com.itextpdf.layout.font.selectorstrategy;

import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSelector;
import com.itextpdf.layout.font.FontSet;

/**
 * The class implements strategy where the best font for each symbol is used if possible.
 */
public class BestMatchFontSelectorStrategy extends AbstractFontSelectorStrategy {

    /**
     * Creates a new instance of {@link BestMatchFontSelectorStrategy}.
     *
     * @param fontProvider the font provider
     * @param fontSelector the font selector
     * @param additionalFonts the set of fonts to be used additionally to the fonts added to font provider.
     */
    public BestMatchFontSelectorStrategy(FontProvider fontProvider, FontSelector fontSelector,
            FontSet additionalFonts) {
        super(fontProvider, fontSelector, additionalFonts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isCurrentFontCheckRequired() {
        return true;
    }

    /**
     * The factory for {@link BestMatchFontSelectorStrategy}.
     */
    public static final class BestMatchFontSelectorStrategyFactory implements IFontSelectorStrategyFactory {
        /**
         * {@inheritDoc}
         */
        @Override
        public IFontSelectorStrategy createFontSelectorStrategy(FontProvider fontProvider, FontSelector fontSelector,
                FontSet additionalFonts) {
            return new BestMatchFontSelectorStrategy(fontProvider, fontSelector, additionalFonts);
        }
    }
}
