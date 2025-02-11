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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

class FontSetCollection extends AbstractCollection<FontInfo> {

    private final Collection<FontInfo> primary;
    private final Collection<FontInfo> additional;

    FontSetCollection(Collection<FontInfo> primary, Collection<FontInfo> additional) {
        this.primary = primary;
        this.additional = additional;
    }

    public int size() {
        return primary.size() + (additional != null ? additional.size() : 0);
    }

    public Iterator<FontInfo> iterator() {
        return new Iterator<FontInfo>() {
            private Iterator<FontInfo> i = primary.iterator();
            boolean isPrimary = true;

            public boolean hasNext() {
                boolean hasNext = i.hasNext();
                if (!hasNext && isPrimary && additional != null) {
                    i = additional.iterator();
                    isPrimary = false;
                    return i.hasNext();
                } else {
                    return hasNext;
                }
            }

            public FontInfo next() {
                return i.next();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }
}
