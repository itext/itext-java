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
package com.itextpdf.forms.xfa;

import com.itextpdf.io.util.XmlUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.utils.DefaultSafeXmlParserFactory;
import com.itextpdf.test.ExceptionTestUtil;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class SecurityTestXmlParserFactory extends DefaultSafeXmlParserFactory {

    @Override
    public DocumentBuilder createDocumentBuilderInstance(boolean namespaceAware, boolean ignoringComments) {
        DocumentBuilder db;
        try {
            db = XmlUtil.getDocumentBuilderFactory().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new PdfException(e.getMessage(), e);
        }

        db.setEntityResolver(new TestEntityResolver());
        return db;
    }

    private static class TestEntityResolver implements EntityResolver {
        public InputSource resolveEntity(String publicId, String systemId) {
            throw new PdfException(ExceptionTestUtil.getXxeTestMessage());
        }
    }
}
