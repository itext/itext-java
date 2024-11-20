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
package com.itextpdf.signatures.validation.extensions;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.kernel.crypto.OID;
import com.itextpdf.signatures.CertificateUtil;

import java.io.IOException;
import java.security.cert.X509Certificate;

/**
 * Class representing "Basic Constraints" certificate extension,
 * which uses provided amount of certificates in chain during the comparison.
 */
public class DynamicBasicConstraintsExtension extends DynamicCertificateExtension {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    /**
     * Create new instance of {@link DynamicBasicConstraintsExtension}.
     */
    public DynamicBasicConstraintsExtension() {
        super(OID.X509Extensions.BASIC_CONSTRAINTS, FACTORY.createBasicConstraints(true).toASN1Primitive());
    }

    /**
     * Check if this extension is present in the provided certificate.
     * In case of {@link DynamicBasicConstraintsExtension}, check if path length for this extension is less or equal
     * to the path length, specified in the certificate.
     *
     * @param certificate {@link X509Certificate} in which this extension shall be present
     *
     * @return {@code true} if this path length is less or equal to a one from the certificate, {@code false} otherwise
     */
    @Override
    public boolean existsInCertificate(X509Certificate certificate) {
        return certificate.getBasicConstraints() >= getCertificateChainSize() - 1;
    }
}
