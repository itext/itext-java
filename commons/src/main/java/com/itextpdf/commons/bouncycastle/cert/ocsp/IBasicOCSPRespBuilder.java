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
package com.itextpdf.commons.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.asn1.x509.IExtensions;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;

import java.util.Date;

/**
 * This interface represents the wrapper for BasicOCSPRespBuilder that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IBasicOCSPRespBuilder {
    /**
     * Calls actual {@code setResponseExtensions} method for the wrapped BasicOCSPRespBuilder object.
     *
     * @param extensions response extensions wrapper
     *
     * @return {@link IBasicOCSPRespBuilder} this wrapper object.
     */
    IBasicOCSPRespBuilder setResponseExtensions(IExtensions extensions);

    /**
     * Calls actual {@code addResponse} method for the wrapped BasicOCSPRespBuilder object.
     *
     * @param certID            wrapped certificate ID details
     * @param certificateStatus wrapped status of the certificate - wrapped null if okay
     * @param time              date this response was valid on
     * @param time1             date when next update should be requested
     * @param extensions        optional wrapped extensions
     *
     * @return {@link IBasicOCSPRespBuilder} this wrapper object.
     */
    IBasicOCSPRespBuilder addResponse(ICertificateID certID, ICertificateStatus certificateStatus, Date time,
            Date time1, IExtensions extensions);

    /**
     * Calls actual {@code build} method for the wrapped BasicOCSPRespBuilder object.
     *
     * @param signer ContentSigner wrapper
     * @param chain  list of wrapped X509CertificateHolder objects
     * @param time   produced at
     *
     * @return {@link IBasicOCSPResp} wrapper for built BasicOCSPResp object.
     *
     * @throws AbstractOCSPException wrapped OCSPException.
     */
    IBasicOCSPResp build(IContentSigner signer, IX509CertificateHolder[] chain, Date time) throws AbstractOCSPException;
}
