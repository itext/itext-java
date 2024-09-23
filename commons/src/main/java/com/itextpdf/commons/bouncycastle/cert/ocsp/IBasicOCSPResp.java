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
package com.itextpdf.commons.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.operator.IContentVerifierProvider;

import java.io.IOException;
import java.util.Date;

/**
 * This interface represents the wrapper for BasicOCSPResp that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IBasicOCSPResp {
    /**
     * Calls actual {@code getResponses} method for the wrapped BasicOCSPResp object.
     *
     * @return wrapped SingleResp list.
     */
    ISingleResp[] getResponses();

    /**
     * Calls actual {@code isSignatureValid} method for the wrapped BasicOCSPResp object.
     *
     * @param provider ContentVerifierProvider wrapper
     *
     * @return boolean value.
     *
     * @throws AbstractOCSPException OCSPException wrapper.
     */
    boolean isSignatureValid(IContentVerifierProvider provider) throws AbstractOCSPException;

    /**
     * Calls actual {@code getCerts} method for the wrapped BasicOCSPResp object.
     *
     * @return wrapped certificates list.
     */
    IX509CertificateHolder[] getCerts();

    /**
     * Calls actual {@code getEncoded} method for the wrapped BasicOCSPResp object.
     *
     * @return the default encoding for the wrapped object.
     *
     * @throws IOException on encoding error.
     */
    byte[] getEncoded() throws IOException;

    /**
     * Calls actual {@code getProducedAt} method for the wrapped BasicOCSPResp object.
     *
     * @return produced at date.
     */
    Date getProducedAt();

    /**
     * Gets parsed value of the extension retrieved using actual {@code getExtension} method
     * for the wrapped BasicOCSPResp object.
     *
     * @param objectIdentifier extension object identifier
     *
     * @return wrapped extension parsed value.
     */
    IASN1Encodable getExtensionParsedValue(IASN1ObjectIdentifier objectIdentifier);


    /**
     * Calls actual {@code getEncoded} method for the wrapped BasicOCSPResp object.
     *
     * @return the Responder Id
     */
    IRespID getResponderId();
}
