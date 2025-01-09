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
package com.itextpdf.styledxmlparser.jsoup.helper;

import java.io.InputStream;

public class KeyVal {
    private String key;
    private String value;
    private InputStream stream;
    private String contentType;

    public static KeyVal create(String key, String value) {
        return new KeyVal(key, value);
    }

    public static KeyVal create(String key, String filename, InputStream stream) {
        return new KeyVal(key, filename)
                .inputStream(stream);
    }

    private KeyVal(String key, String value) {
        Validate.notEmpty(key, "Data key must not be empty");
        Validate.notNull(value, "Data value must not be null");
        this.key = key;
        this.value = value;
    }

    public KeyVal key(String key) {
        Validate.notEmpty(key, "Data key must not be empty");
        this.key = key;
        return this;
    }

    public String key() {
        return key;
    }

    public KeyVal value(String value) {
        Validate.notNull(value, "Data value must not be null");
        this.value = value;
        return this;
    }

    public String value() {
        return value;
    }

    public KeyVal inputStream(InputStream inputStream) {
        Validate.notNull(value, "Data input stream must not be null");
        this.stream = inputStream;
        return this;
    }

    public InputStream inputStream() {
        return stream;
    }

    public boolean hasInputStream() {
        return stream != null;
    }

    public KeyVal contentType(String contentType) {
        Validate.notEmpty(contentType);
        this.contentType = contentType;
        return this;
    }

    public String contentType() {
        return contentType;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
