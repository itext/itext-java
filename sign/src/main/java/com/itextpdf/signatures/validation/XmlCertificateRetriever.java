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
package com.itextpdf.signatures.validation;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.utils.XmlProcessorCreator;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.security.cert.Certificate;
import java.util.List;


class XmlCertificateRetriever {

    private AbstractXmlCertificateHandler handler;

    XmlCertificateRetriever(AbstractXmlCertificateHandler handler) {
        this.handler = handler;
    }

    List<Certificate> getCertificates(String path) {
        if (!handler.getCertificateList().isEmpty()) {
            handler.clear();
        }

        XMLReader reader = XmlProcessorCreator.createSafeXMLReader(true, false);
        reader.setContentHandler(handler);
        try {
            reader.parse(path);
        } catch (IOException | SAXException e) {
            throw new PdfException(MessageFormatUtil.format(
                    SignExceptionMessageConstant.FAILED_TO_READ_CERTIFICATE_BYTES_FROM_XML, path), e);
        }

        return handler.getCertificateList();
    }

    IServiceContext getServiceContext(Certificate certificate) {
        return handler.getServiceContext(certificate);
    }
}