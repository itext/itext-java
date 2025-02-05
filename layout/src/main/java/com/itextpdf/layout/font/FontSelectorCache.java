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
package com.itextpdf.layout.font;

import java.util.HashMap;
import java.util.Map;

class FontSelectorCache {

    private final FontSetSelectors defaultSelectors;
    private final FontSet defaultFontSet;
    private final Map<Long, FontSetSelectors> caches = new HashMap<>();

    FontSelectorCache(FontSet defaultFontSet) {
        assert defaultFontSet != null;
        this.defaultSelectors = new FontSetSelectors();
        this.defaultSelectors.update(defaultFontSet);
        this.defaultFontSet = defaultFontSet;
    }

    FontSelector get(FontSelectorKey key) {
        if (update(null, null)) {
            return null;
        } else {
            return defaultSelectors.map.get(key);
        }
    }

    FontSelector get(FontSelectorKey key, FontSet additionalFonts) {
        if (additionalFonts == null) {
            return get(key);
        } else {
            FontSetSelectors selectors = caches.get(additionalFonts.getId());
            if (selectors == null) {
                caches.put(additionalFonts.getId(), selectors = new FontSetSelectors());
            }
            if (update(selectors, additionalFonts)) {
                return null;
            } else {
                return selectors.map.get(key);
            }
        }
    }

    void put(FontSelectorKey key, FontSelector fontSelector) {
        //update defaultSelectors to reset counter before pushing if needed.
        update(null, null);
        defaultSelectors.map.put(key, fontSelector);
    }

    void put(FontSelectorKey key, FontSelector fontSelector, FontSet fontSet) {
        if (fontSet == null) {
            put(key, fontSelector);
        } else {
            FontSetSelectors selectors = caches.get(fontSet.getId());
            if (selectors == null) {
                caches.put(fontSet.getId(), selectors = new FontSetSelectors());
            }
            //update selectors and defaultSelectors to reset counter before pushing if needed.
            update(selectors, fontSet);
            selectors.map.put(key, fontSelector);
        }
    }

    private boolean update(FontSetSelectors selectors, FontSet fontSet) {
        boolean updated = false;
        if (defaultSelectors.update(defaultFontSet)) {
            updated = true;
        }
        if (selectors != null && selectors.update(fontSet)) {
            updated = true;
        }
        return updated;
    }

    private static class FontSetSelectors {
        final Map<FontSelectorKey, FontSelector> map = new HashMap<>();
        private int fontSetSize = -1;

        boolean update(FontSet fontSet) {
            assert fontSet != null;
            if (fontSetSize == fontSet.size()) {
                return false;
            } else {
                map.clear();
                fontSetSize = fontSet.size();
                return true;
            }
        }
    }
}
