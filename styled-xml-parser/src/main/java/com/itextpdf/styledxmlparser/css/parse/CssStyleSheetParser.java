/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.styledxmlparser.css.parse;

import com.itextpdf.styledxmlparser.PortUtil;
import com.itextpdf.styledxmlparser.css.CssStyleSheet;
import com.itextpdf.styledxmlparser.css.parse.syntax.CssParserStateController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * Utilities class to parse a CSS style sheet.
 */
public final class CssStyleSheetParser {
    /**
     * Creates a new {@link CssStyleSheetParser}.
     */
    private CssStyleSheetParser() {
    }

    /**
     * Parses a stream into a {@link CssStyleSheet}.
     *
     * @param stream the stream
     * @param baseUrl the base url
     *
     * @return the resulting {@link CssStyleSheet}
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static CssStyleSheet parse(InputStream stream, String baseUrl) throws IOException {
        return parse(stream, new CssParserStateController(baseUrl));
    }

    /**
     * Parses a stream into a {@link CssStyleSheet}.
     *
     * @param stream the stream
     * @param controller the parse state controller
     *
     * @return the resulting {@link CssStyleSheet}
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static CssStyleSheet parse(InputStream stream, CssParserStateController controller) throws IOException {
        // TODO determine charset correctly DEVSIX-1458
        Reader br = PortUtil.wrapInBufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        char[] buffer = new char[8192];
        int length;
        while ((length = br.read(buffer, 0, buffer.length)) > 0) {
            for(int i = 0 ; i < length; i++) {
                controller.process(buffer[i]);
            }
        }
        return controller.getParsingResult();
    }

    /**
     * Parses a stream into a {@link CssStyleSheet}.
     *
     * @param stream the stream
     *
     * @return the resulting {@link CssStyleSheet}
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static CssStyleSheet parse(InputStream stream) throws IOException {
        return parse(stream, (String) null);
    }

    /**
     * Parses a string into a {@link CssStyleSheet}.
     *
     * @param data the style sheet data
     * @param baseUrl the base url
     *
     * @return the resulting {@link CssStyleSheet}
     */
    public static CssStyleSheet parse(String data, String baseUrl) {
        ByteArrayInputStream stream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        try {
            return parse(stream, baseUrl);
        } catch (IOException exc) {
            return null;
        }
    }

    /**
     * Parses a string into a {@link CssStyleSheet}.
     *
     * @param data the data
     *
     * @return the resulting {@link CssStyleSheet}
     */
    public static CssStyleSheet parse(String data) {
        return parse(data, null);
    }
}
