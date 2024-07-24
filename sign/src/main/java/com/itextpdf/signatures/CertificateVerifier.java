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

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Superclass for a series of certificate verifiers that will typically
 * be used in a chain. It wraps another <code>CertificateVerifier</code>
 * that is the next element in the chain of which the <code>verify()</code>
 * method will be called.
 *
 * @deprecated starting from 8.0.5.
 * {@link com.itextpdf.signatures.validation.CertificateChainValidator} should be used instead.
 */
@Deprecated
public class CertificateVerifier {

    /** The previous CertificateVerifier in the chain of verifiers. */
    protected CertificateVerifier verifier;

    /** Indicates if going online to verify a certificate is allowed. */
    protected boolean onlineCheckingAllowed = true;

    /**
     * Creates the final CertificateVerifier in a chain of verifiers.
     *
     * @param verifier	the previous verifier in the chain
     */
    public CertificateVerifier(CertificateVerifier verifier) {
        this.verifier = verifier;
    }

    /**
     * Decide whether or not online checking is allowed.
     *
     * @param onlineCheckingAllowed a boolean indicating whether the certificate can be verified using online verification results.
     */
    public void setOnlineCheckingAllowed(boolean onlineCheckingAllowed) {
        this.onlineCheckingAllowed = onlineCheckingAllowed;
    }

    /**
     * Checks the validity of the certificate, and calls the next
     * verifier in the chain, if any.
     * @param signCert	                the certificate that needs to be checked
     * @param issuerCert	            its issuer
     * @param signDate		            the date the certificate needs to be valid
     * @return a list of <code>VerificationOK</code> objects. The list will be empty if the certificate couldn't be verified.
     * @throws GeneralSecurityException thrown if the certificate has expired, isn't valid yet, or if an exception has been thrown in {@link java.security.cert.Certificate#verify(PublicKey) Certificate#verify}.
     */
    public List<VerificationOK> verify(X509Certificate signCert, X509Certificate issuerCert, Date signDate)
            throws GeneralSecurityException {
        // Check if the certificate is valid on the signDate
        if (signDate != null) {
            signCert.checkValidity(signDate);
        }
        // Check if the signature is valid
        if (issuerCert != null) {
            signCert.verify(issuerCert.getPublicKey());
        }
        // Also in case, the certificate is self-signed
        else {
            signCert.verify(signCert.getPublicKey());
        }
        List<VerificationOK> result = new ArrayList<>();
        if (verifier != null)
            result.addAll(verifier.verify(signCert, issuerCert, signDate));
        return result;
    }
}
