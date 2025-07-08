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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509CertificateConverter;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.validation.xml.IDefaultXmlHandler;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

abstract class AbstractXmlCertificateHandler implements IDefaultXmlHandler {

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final IJcaX509CertificateConverter X509_CERTIFICATE_CONVERTER = BOUNCY_CASTLE_FACTORY
            .createJcaX509CertificateConverter().setProvider(BOUNCY_CASTLE_FACTORY.getProvider());

    abstract IServiceContext getServiceContext(Certificate certificate);

    abstract List<Certificate> getCertificateList();

    Certificate getCertificateFromEncodedData(String certificateString) {
        try {
            byte[] bytes = Base64.getDecoder().decode(certificateString);
            IX509CertificateHolder certificateHolder = BOUNCY_CASTLE_FACTORY
                    .createX509CertificateHolder(bytes);
            return X509_CERTIFICATE_CONVERTER.getCertificate(certificateHolder);
        } catch (CertificateException | IOException e) {
            throw new PdfException(SignExceptionMessageConstant.FAILED_TO_RETRIEVE_CERTIFICATE, e);
        }
    }

    abstract void clear();
}
