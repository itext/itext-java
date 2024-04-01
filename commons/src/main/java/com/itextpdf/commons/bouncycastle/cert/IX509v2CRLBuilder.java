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
package com.itextpdf.commons.bouncycastle.cert;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;

/**
 * This interface represents the wrapper for X509v2CRLBuilder that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IX509v2CRLBuilder {
    /**
     * Calls actual {@code addCRLEntry} method for the wrapped X509v2CRLBuilder object.
     *
     * @param bigInteger serial number of revoked certificate
     * @param date       date of certificate revocation
     * @param i          the reason code, as indicated in CRLReason, i.e CRLReason.keyCompromise, or 0 if not to be used
     *
     * @return {@link IX509v2CRLBuilder} the current wrapper object.
     */
    IX509v2CRLBuilder addCRLEntry(BigInteger bigInteger, Date date, int i);

    /**
     * Calls actual {@code addExtension} method for the wrapped X509v2CRLBuilder object.
     *
     * @param objectIdentifier extension object identifier
     * @param isCritical       specifies if extension is critical or not
     * @param extension        encoded extension value
     *
     * @return {@link IX509v2CRLBuilder} the current wrapper object.
     *
     * @throws IOException if an I/O error occurs.
     */
    IX509v2CRLBuilder addExtension(IASN1ObjectIdentifier objectIdentifier, boolean isCritical,
                                   IASN1Encodable extension) throws IOException;

    /**
     * Calls actual {@code setNextUpdate} method for the wrapped X509v2CRLBuilder object.
     *
     * @param nextUpdate date of next CRL update
     *
     * @return {@link IX509v2CRLBuilder} the current wrapper object.
     */
    IX509v2CRLBuilder setNextUpdate(Date nextUpdate);

    /**
     * Calls actual {@code build} method for the wrapped X509v2CRLBuilder object.
     *
     * @param signer ContentSigner wrapper
     *
     * @return {@link IX509CRLHolder} the wrapper for built X509CRLHolder object.
     */
    IX509CRLHolder build(IContentSigner signer);
}
