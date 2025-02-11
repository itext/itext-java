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
package com.itextpdf.signatures;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class that allows you to verify a certificate against
 * one or more Certificate Revocation Lists.
 *
 * @deprecated starting from 8.0.5.
 * {@link com.itextpdf.signatures.validation.CRLValidator} should be used instead.
 */
@Deprecated
public class CRLVerifier extends RootStoreVerifier {

    /** The Logger instance */
    protected static final Logger LOGGER = LoggerFactory.getLogger(CRLVerifier.class);

    /** The list of CRLs to check for revocation date. */
    List<X509CRL> crls;

    /**
     * Creates a CRLVerifier instance.
     * @param verifier	the next verifier in the chain
     * @param crls a list of CRLs
     */
    public CRLVerifier(CertificateVerifier verifier, List<X509CRL> crls) {
        super(verifier);
        this.crls = crls;
    }

    /**
     * Verifies whether a valid CRL is found for the certificate.
     * If this method returns false, it doesn't mean the certificate isn't valid.
     * It means we couldn't verify it against any CRL that was available.
     * @param signCert	the certificate that needs to be checked
     * @param issuerCert	its issuer
     * @return a list of <code>VerificationOK</code> objects.
     * The list will be empty if the certificate couldn't be verified.
     * @see com.itextpdf.signatures.RootStoreVerifier#verify(java.security.cert.X509Certificate,
     *         java.security.cert.X509Certificate, java.util.Date)
     */
    public List<VerificationOK> verify(X509Certificate signCert, X509Certificate issuerCert, Date signDate)
            throws GeneralSecurityException {
        List<VerificationOK> result = new ArrayList<>();
        int validCrlsFound = 0;
        // first check the list of CRLs that is provided
        if (crls != null) {
            for (X509CRL crl : crls) {
                if (verify(crl, signCert, issuerCert, signDate)) {
                    validCrlsFound++;
                }
            }
        }
        // then check online if allowed
        boolean online = false;
        if (onlineCheckingAllowed && validCrlsFound == 0) {
            if (verify(getCRL(signCert, issuerCert), signCert, issuerCert, signDate)) {
                validCrlsFound++;
                online = true;
            }
        }
        // show how many valid CRLs were found
        LOGGER.info("Valid CRLs found: " + validCrlsFound);
        if (validCrlsFound > 0) {
            result.add(new VerificationOK(signCert, this.getClass(),
                    "Valid CRLs found: " + validCrlsFound + (online ? " (online)" : "")));
        }
        if (verifier != null) {
            result.addAll(verifier.verify(signCert, issuerCert, signDate));
        }
        // verify using the previous verifier in the chain (if any)
        return result;
    }

    /**
     * Verifies a certificate against a single CRL.
     * @param crl	the Certificate Revocation List
     * @param signCert	a certificate that needs to be verified
     * @param issuerCert	its issuer
     * @param signDate		the sign date
     * @return true if the verification succeeded
     * @throws GeneralSecurityException thrown when certificate has been revoked
     */
    public boolean verify(X509CRL crl, X509Certificate signCert, X509Certificate issuerCert, Date signDate)
            throws GeneralSecurityException {
        if (crl == null || signDate == TimestampConstants.UNDEFINED_TIMESTAMP_DATE) {
            return false;
        }
        // We only check CRLs valid on the signing date for which the issuer matches
        if (crl.getIssuerX500Principal().equals(signCert.getIssuerX500Principal())
                && signDate.before(crl.getNextUpdate())) {
            // the signing certificate may not be revoked
            if (isSignatureValid(crl, issuerCert) && crl.isRevoked(signCert)) {
                throw new VerificationException(signCert, "The certificate has been revoked.");
            }
            return true;
        }
        return false;
    }

    /**
     * Fetches a CRL for a specific certificate online (without further checking).
     *
     * @param signCert   the certificate
     * @param issuerCert its issuer left for backwards compatibility
     *
     * @return an X509CRL object.
     */
    public X509CRL getCRL(X509Certificate signCert, X509Certificate issuerCert) {
        try {
            // gets the URL from the certificate
            List<String> crlurl = CertificateUtil.getCRLURLs(signCert);
            if (crlurl.isEmpty()) {
                return null;
            }
            LOGGER.info("Getting CRL from " + crlurl.get(0));
            return (X509CRL) SignUtils.parseCrlFromStream(new URL(crlurl.get(0)).openStream());
        } catch (IOException | GeneralSecurityException e) {
            return null;
        }
    }

    /**
     * Checks if a CRL verifies against the issuer certificate or a trusted anchor.
     * @param crl	the CRL
     * @param crlIssuer	the trusted anchor
     * @return	true if the CRL can be trusted
     */
    public boolean isSignatureValid(X509CRL crl, X509Certificate crlIssuer) {
        // check if the CRL was issued by the issuer
        if (crlIssuer != null) {
            try {
                crl.verify(crlIssuer.getPublicKey());
                return true;
            } catch (GeneralSecurityException e) {
                LOGGER.warn("CRL not issued by the same authority as the certificate that is being checked");
            }
        }
        // check the CRL against trusted anchors
        if (rootStore == null)
            return false;
        try {
            // loop over the certificate in the key store
            for (X509Certificate anchor : SignUtils.getCertificates(rootStore)) {
                try {
                    // check if the crl was signed by a trusted party (indirect CRLs)
                    crl.verify(anchor.getPublicKey());
                    return true;
                } catch (GeneralSecurityException e) {
                    // do nothing and continue
                }
            }
        }
        catch (GeneralSecurityException e) {
            // do nothing and return false at the end
        }
        return false;
    }
}
