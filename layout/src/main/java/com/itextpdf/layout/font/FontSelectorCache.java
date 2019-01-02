/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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

    FontSelector get(FontSelectorKey key, FontSet fontSet) {
        if (fontSet == null) {
            return get(key);
        } else {
            FontSetSelectors selectors = caches.get(fontSet.getId());
            if (selectors == null) {
                caches.put(fontSet.getId(), selectors = new FontSetSelectors());
            }
            if (update(selectors, fontSet)) {
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
