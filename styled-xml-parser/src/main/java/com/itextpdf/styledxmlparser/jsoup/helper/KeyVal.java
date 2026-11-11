/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.helper;


import java.io.InputStream;

public class KeyVal {
    private String key;
    private String value;
    private InputStream stream;

    public static KeyVal create(String key, String value) {
        return (KeyVal) new KeyVal().key(key).value(value);
    }

    public static KeyVal create(String key, String filename, InputStream stream) {
        return (KeyVal) new KeyVal().key(key).value(filename).inputStream(stream);
    }

    private KeyVal() {}

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

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
