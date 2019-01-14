/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.
    
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
// TODO refactor into interface
public final class CssStyleSheetParser {

    /**
     * Creates a new {@link CssStyleSheetParser}.
     */
    private CssStyleSheetParser() {
    }

    /**
     * Parses a stream into a {@link CssRuleSetParser}.
     *
     * @param stream the stream
     * @param baseUrl the base url
     * @return the resulting {@link CssStyleSheet}
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static CssStyleSheet parse(InputStream stream, String baseUrl) throws IOException {
        CssParserStateController controller = new CssParserStateController(baseUrl);
        Reader br = PortUtil.wrapInBufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)); // TODO determine charset correctly DEVSIX-1458
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
     * @return the resulting {@link CssStyleSheet}
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static CssStyleSheet parse(InputStream stream) throws IOException {
        return parse(stream, null);
    }

    /**
     * Parses a string into a {@link CssStyleSheet}.
     *
     * @param data the style sheet data
     * @param baseUrl the base url
     * @return the resulting {@link CssStyleSheet}
     */
    public static CssStyleSheet parse(String data, String baseUrl) {
        // TODO charset? better to create parse logic based on string completely
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
     * @return the resulting {@link CssStyleSheet}
     */
    public static CssStyleSheet parse(String data) {
        return parse(data, null);
    }
}
