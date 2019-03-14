/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
 */
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
     * Verifies if a a valid CRL is found for the certificate.
     * If this method returns false, it doesn't mean the certificate isn't valid.
     * It means we couldn't verify it against any CRL that was available.
     * @param signCert	the certificate that needs to be checked
     * @param issuerCert	its issuer
     * @return a list of <code>VerificationOK</code> objects.
     * The list will be empty if the certificate couldn't be verified.
     * @see com.itextpdf.signatures.RootStoreVerifier#verify(java.security.cert.X509Certificate, java.security.cert.X509Certificate, java.util.Date)
     */
    public List<VerificationOK> verify(X509Certificate signCert, X509Certificate issuerCert, Date signDate)
            throws GeneralSecurityException, IOException {
        List<VerificationOK> result = new ArrayList<>();
        int validCrlsFound = 0;
        // first check the list of CRLs that is provided
        if (crls != null) {
            for (X509CRL crl : crls) {
                if (verify(crl, signCert, issuerCert, signDate))
                    validCrlsFound++;
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
            result.add(new VerificationOK(signCert, this.getClass(), "Valid CRLs found: " + validCrlsFound + (online ? " (online)" : "")));
        }
        if (verifier != null)
            result.addAll(verifier.verify(signCert, issuerCert, signDate));
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
     * @throws GeneralSecurityException
     */
    public boolean verify(X509CRL crl, X509Certificate signCert, X509Certificate issuerCert, Date signDate) throws GeneralSecurityException {
        if (crl == null || signDate == SignUtils.UNDEFINED_TIMESTAMP_DATE)
            return false;
        // We only check CRLs valid on the signing date for which the issuer matches
        if (crl.getIssuerX500Principal().equals(signCert.getIssuerX500Principal()) && signDate.before(crl.getNextUpdate())) {
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
     * @param signCert	the certificate
     * @param issuerCert	its issuer
     * @return	an X509CRL object
     */
    public X509CRL getCRL(X509Certificate signCert, X509Certificate issuerCert) {
        if (issuerCert == null)
            issuerCert = signCert;
        try {
            // gets the URL from the certificate
            String crlurl = CertificateUtil.getCRLURL(signCert);
            if (crlurl == null)
                return null;
            LOGGER.info("Getting CRL from " + crlurl);
            return (X509CRL) SignUtils.parseCrlFromStream(new URL(crlurl).openStream());
        }
        catch(IOException e) {
            return null;
        }
        catch(GeneralSecurityException e) {
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
                    continue;
                }
            }
        }
        catch (GeneralSecurityException e) {
            return false;
        }
        return false;
    }
}
