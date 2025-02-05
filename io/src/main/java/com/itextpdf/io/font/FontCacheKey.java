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
package com.itextpdf.io.font;

import java.util.Arrays;

public abstract class FontCacheKey {

    public static FontCacheKey create(String fontName) {
        return new FontCacheStringKey(fontName);
    }

    public static FontCacheKey create(String fontName, int ttcIndex) {
        return new FontCacheTtcKey(fontName, ttcIndex);
    }

    public static FontCacheKey create(byte[] fontProgram) {
        return new FontCacheBytesKey(fontProgram);
    }

    public static FontCacheKey create(byte[] fontProgram, int ttcIndex) {
        return new FontCacheTtcKey(fontProgram, ttcIndex);
    }

    private static class FontCacheStringKey extends FontCacheKey {
        private String fontName;

        FontCacheStringKey(String fontName) {
            this.fontName = fontName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FontCacheStringKey that = (FontCacheStringKey) o;

            return fontName != null ? fontName.equals(that.fontName) : that.fontName == null;
        }

        @Override
        public int hashCode() {
            return fontName != null ? fontName.hashCode() : 0;
        }
    }

    private static class FontCacheBytesKey extends FontCacheKey {
        private byte[] firstFontBytes;
        private int fontLength;

        private int hashcode;

        FontCacheBytesKey(byte[] fontBytes) {
            if (fontBytes != null) {
                int maxBytesNum = 10000;
                this.firstFontBytes = fontBytes.length > maxBytesNum ? Arrays.copyOf(fontBytes, maxBytesNum) : fontBytes;
                this.fontLength = fontBytes.length;
            }
            this.hashcode = calcHashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FontCacheBytesKey that = (FontCacheBytesKey) o;

            if (fontLength != that.fontLength) return false;
            return Arrays.equals(firstFontBytes, that.firstFontBytes);
        }

        @Override
        public int hashCode() {
            return hashcode;
        }

        private int calcHashCode() {
            int result = Arrays.hashCode(firstFontBytes);
            result = 31 * result + fontLength;
            return result;
        }
    }

    private static class FontCacheTtcKey extends FontCacheKey {
        private FontCacheKey ttcKey;
        private int ttcIndex;

        FontCacheTtcKey(String fontName, int ttcIndex) {
            this.ttcKey = new FontCacheStringKey(fontName);
            this.ttcIndex = ttcIndex;
        }

        FontCacheTtcKey(byte[] fontBytes, int ttcIndex) {
            this.ttcKey = new FontCacheBytesKey(fontBytes);
            this.ttcIndex = ttcIndex;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FontCacheTtcKey that = (FontCacheTtcKey) o;

            if (ttcIndex != that.ttcIndex) return false;
            return ttcKey.equals(that.ttcKey);
        }

        @Override
        public int hashCode() {
            int result = ttcKey.hashCode();
            result = 31 * result + ttcIndex;
            return result;
        }
    }
}
