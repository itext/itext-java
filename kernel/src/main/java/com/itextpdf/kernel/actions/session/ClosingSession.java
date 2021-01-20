/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.actions.session;

import com.itextpdf.kernel.pdf.PdfDocument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The class allows to share properties during closing of the document.
 */
public class ClosingSession {
    private final PdfDocument document;
    private List<String> producer;
    private final Map<String, Object> properties;

    /**
     * Creates a closing session for the document.
     *
     * @param document is a document to be close
     */
    public ClosingSession(PdfDocument document) {
        this.document = document;
        this.properties = new HashMap<>();
    }

    /**
     * Obtains closing document.
     *
     * @return closing document
     */
    public PdfDocument getDocument() {
        return document;
    }

    /**
     * Gets metadata about products involved into document processing.
     *
     * @return metadata
     */
    public List<String> getProducer() {
        return producer;
    }

    /**
     * Sets metadata about products involved into document processing.
     *
     * @param producer is a meta data
     */
    public void setProducer(List<String> producer) {
        this.producer = producer;
    }

    /**
     * Gets additional property associated with the provided key.
     *
     * @param key is a key
     * @return value associated with the key
     */
    public Object getProperty(String key) {
        return properties.get(key);
    }

    /**
     * Stores new property.
     *
     * @param key is a key
     * @param value is a value to be associated with the key
     */
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }
}
