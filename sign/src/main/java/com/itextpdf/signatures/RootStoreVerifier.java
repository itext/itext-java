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
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Verifies a certificate against a <code>KeyStore</code>
 * containing trusted anchors.
 *
 * @deprecated starting from 8.0.5.
 * {@link com.itextpdf.signatures.validation.CertificateChainValidator} should be used instead.
 */
@Deprecated
public class RootStoreVerifier extends CertificateVerifier {

    /** A key store against which certificates can be verified. */
    protected KeyStore rootStore = null;

    /**
     * Creates a RootStoreVerifier in a chain of verifiers.
     *
     * @param verifier the next verifier in the chain
     */
    public RootStoreVerifier(CertificateVerifier verifier) {
        super(verifier);
    }

    /**
     * Sets the Key Store against which a certificate can be checked.
     *
     * @param keyStore a root store
     */
    public void setRootStore(KeyStore keyStore) {
        this.rootStore = keyStore;
    }

    /**
     * Verifies a single certificate against a key store (if present).
     *
     * @param signCert the certificate to verify
     * @param issuerCert the issuer certificate
     * @param signDate the date the certificate needs to be valid
     * @return a list of <code>VerificationOK</code> objects.
     * The list will be empty if the certificate couldn't be verified.
     */
    public List<VerificationOK> verify(X509Certificate signCert, X509Certificate issuerCert,
            Date signDate) throws GeneralSecurityException {
        // verify using the CertificateVerifier if root store is missing
        if (rootStore == null)
            return super.verify(signCert, issuerCert, signDate);
        try {
            List<VerificationOK> result = new ArrayList<>();
            // loop over the trusted anchors in the root store
            for (X509Certificate anchor : SignUtils.getCertificates(rootStore)) {
                try {
                    signCert.verify(anchor.getPublicKey());
                    result.add(new VerificationOK(signCert, this.getClass(),
                            "Certificate verified against root store."));
                    result.addAll(super.verify(signCert, issuerCert, signDate));
                    return result;
                } catch (GeneralSecurityException e) {
                    // do nothing and continue
                }
            }
            result.addAll(super.verify(signCert, issuerCert, signDate));
            return result;
        } catch (GeneralSecurityException e) {
            return super.verify(signCert, issuerCert, signDate);
        }
    }
}
