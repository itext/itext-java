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
package com.itextpdf.signatures;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;

import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

/**
 * An exception that is thrown when something is wrong with a certificate.
 *
 * @deprecated starting from 9.0.0 since all the classes that use this one are also deprecated in favour of
 * new signature validation logic in the {@link com.itextpdf.signatures.validation} package.
 */
@Deprecated
public class VerificationException extends GeneralSecurityException {
    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    /**
     * Creates a VerificationException.
     *
     * @param cert is a failed certificate
     * @param message is a reason of failure
     * @throws CertificateEncodingException if an encoding error occurs in {@link Certificate}.
     */
    public VerificationException(Certificate cert, String message) throws CertificateEncodingException {
        super(MessageFormatUtil.format(SignExceptionMessageConstant.CERTIFICATE_TEMPLATE_FOR_EXCEPTION_MESSAGE,
                cert == null ? "Unknown" : BOUNCY_CASTLE_FACTORY.createX500Name(
                        (X509Certificate) cert).toString(), message));
    }
}
