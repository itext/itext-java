package com.itextpdf.io.font;

import java.util.Arrays;

abstract class FontCacheKey {
    static FontCacheKey create(String fontName) {
        return new FontCacheStringKey(fontName);
    }

    static FontCacheKey create(String fontName, int ttcIndex) {
        return new FontCacheTtcKey(fontName, ttcIndex);
    }

    static FontCacheKey create(byte[] fontProgram) {
        return new FontCacheBytesKey(fontProgram);
    }

    static FontCacheKey create(byte[] fontProgram, int ttcIndex) {
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
