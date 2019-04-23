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

import com.itextpdf.io.util.DateTimeUtil;
import com.itextpdf.io.util.MessageFormatUtil;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateStatus;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class that allows you to verify a certificate against
 * one or more OCSP responses.
 */
public class OCSPVerifier extends RootStoreVerifier {

    /** The Logger instance */
    protected static final Logger LOGGER = LoggerFactory.getLogger(OCSPVerifier.class);

    protected final static String id_kp_OCSPSigning = "1.3.6.1.5.5.7.3.9";

    /** The list of OCSP responses. */
    protected List<BasicOCSPResp> ocsps;

    /**
     * Creates an OCSPVerifier instance.
     * @param verifier	the next verifier in the chain
     * @param ocsps a list of OCSP responses
     */
    public OCSPVerifier(CertificateVerifier verifier, List<BasicOCSPResp> ocsps) {
        super(verifier);
        this.ocsps = ocsps;
    }

    /**
     * Verifies if a a valid OCSP response is found for the certificate.
     * If this method returns false, it doesn't mean the certificate isn't valid.
     * It means we couldn't verify it against any OCSP response that was available.
     * @param signCert	the certificate that needs to be checked
     * @param issuerCert	its issuer
     * @return a list of <code>VerificationOK</code> objects.
     * The list will be empty if the certificate couldn't be verified.
     * @see com.itextpdf.signatures.RootStoreVerifier#verify(java.security.cert.X509Certificate, java.security.cert.X509Certificate, java.util.Date)
     */
    public List<VerificationOK> verify(X509Certificate signCert,
                                       X509Certificate issuerCert, Date signDate)
            throws GeneralSecurityException, IOException {
        List<VerificationOK> result = new ArrayList<>();
        int validOCSPsFound = 0;
        // first check in the list of OCSP responses that was provided
        if (ocsps != null) {
            for (BasicOCSPResp ocspResp : ocsps) {
                if (verify(ocspResp, signCert, issuerCert, signDate))
                    validOCSPsFound++;
            }
        }
        // then check online if allowed
        boolean online = false;
        if (onlineCheckingAllowed && validOCSPsFound == 0) {
            if (verify(getOcspResponse(signCert, issuerCert), signCert, issuerCert, signDate)) {
                validOCSPsFound++;
                online = true;
            }
        }
        // show how many valid OCSP responses were found
        LOGGER.info("Valid OCSPs found: " + validOCSPsFound);
        if (validOCSPsFound > 0)
            result.add(new VerificationOK(signCert, this.getClass(), "Valid OCSPs Found: " + validOCSPsFound + (online ? " (online)" : "")));
        if (verifier != null)
            result.addAll(verifier.verify(signCert, issuerCert, signDate));
        // verify using the previous verifier in the chain (if any)
        return result;
    }


    /**
     * Verifies a certificate against a single OCSP response
     * @param ocspResp the OCSP response
     * @param signCert the certificate that needs to be checked
     * @param issuerCert the certificate of CA (certificate that issued signCert). This certificate is considered trusted and valid by this method.
     * @param signDate sign date
     * @return {@code true}, in case successful check, otherwise false.
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public boolean verify(BasicOCSPResp ocspResp, X509Certificate signCert, X509Certificate issuerCert, Date signDate) throws GeneralSecurityException, IOException {
        if (ocspResp == null)
            return false;
        // Getting the responses
        SingleResp[] resp = ocspResp.getResponses();
        for (int i = 0; i < resp.length; i++) {
            // check if the serial number corresponds
            if (!signCert.getSerialNumber().equals(resp[i].getCertID().getSerialNumber())) {
                continue;
            }
            // check if the issuer matches
            try {
                if (issuerCert == null) issuerCert = signCert;
                if (!SignUtils.checkIfIssuersMatch(resp[i].getCertID(), issuerCert)) {
                    LOGGER.info("OCSP: Issuers doesn't match.");
                    continue;
                }
            } catch (OCSPException e) {
                continue;
            }
            // check if the OCSP response was valid at the time of signing
            if (resp[i].getNextUpdate() == null) {
                Date nextUpdate = SignUtils.add180Sec(resp[i].getThisUpdate());
                LOGGER.info(MessageFormatUtil.format("No 'next update' for OCSP Response; assuming {0}", nextUpdate));
                if (signDate.after(nextUpdate)) {
                    LOGGER.info(MessageFormatUtil.format("OCSP no longer valid: {0} after {1}", signDate, nextUpdate));
                    continue;
                }
            } else {
                if (signDate.after(resp[i].getNextUpdate())) {
                    LOGGER.info(MessageFormatUtil.format("OCSP no longer valid: {0} after {1}", signDate, resp[i].getNextUpdate()));
                    continue;
                }
            }
            // check the status of the certificate
            Object status = resp[i].getCertStatus();
            if (status == CertificateStatus.GOOD) {
                // check if the OCSP response was genuine
                isValidResponse(ocspResp, issuerCert, signDate);
                return true;
            }
        }
        return false;
    }

    /**
     * Verifies if an OCSP response is genuine
     * If it doesn't verify against the issuer certificate and response's certificates, it may verify
     * using a trusted anchor or cert.
     * @param ocspResp the OCSP response
     * @param issuerCert the issuer certificate. This certificate is considered trusted and valid by this method.
     * @throws GeneralSecurityException
     * @throws IOException
     * @deprecated Will be removed in iText 7.2. Use {@link #isValidResponse(BasicOCSPResp, X509Certificate, Date)} instead
     */
    @Deprecated
    public void isValidResponse(BasicOCSPResp ocspResp, X509Certificate issuerCert) throws GeneralSecurityException, IOException {
        isValidResponse(ocspResp, issuerCert, DateTimeUtil.getCurrentTimeDate());
    }

    /**
     * Verifies if an OCSP response is genuine
     * If it doesn't verify against the issuer certificate and response's certificates, it may verify
     * using a trusted anchor or cert.
     * @param ocspResp the OCSP response
     * @param issuerCert the issuer certificate. This certificate is considered trusted and valid by this method.
     * @param signDate sign date
     * @throws GeneralSecurityException
     */
    public void isValidResponse(BasicOCSPResp ocspResp, X509Certificate issuerCert, Date signDate) throws GeneralSecurityException {
        // OCSP response might be signed by the issuer certificate or
        // the Authorized OCSP responder certificate containing the id-kp-OCSPSigning extended key usage extension
        X509Certificate responderCert = null;

        // first check if the issuer certificate signed the response
        // since it is expected to be the most common case
        if (isSignatureValid(ocspResp, issuerCert)) {
            responderCert = issuerCert;
        }

        // if the issuer certificate didn't sign the ocsp response, look for authorized ocsp responses
        // from properties or from certificate chain received with response
        if (responderCert == null) {
            if (ocspResp.getCerts() != null) {
                //look for existence of Authorized OCSP responder inside the cert chain in ocsp response
                Iterable<X509Certificate> certs = SignUtils.getCertsFromOcspResponse(ocspResp);
                for (X509Certificate cert : certs) {
                    List keyPurposes = null;
                    try {
                        keyPurposes = cert.getExtendedKeyUsage();
                        if ((keyPurposes != null) && keyPurposes.contains(id_kp_OCSPSigning) && isSignatureValid(ocspResp, cert)) {
                            responderCert = cert;
                            break;
                        }
                    } catch (CertificateParsingException ignored) {
                    }
                }

                // Certificate signing the ocsp response is not found in ocsp response's certificate chain received
                // and is not signed by the issuer certificate.
                if (responderCert == null) {
                    throw new VerificationException(issuerCert, "OCSP response could not be verified");
                }

                // RFC 6960 4.2.2.2. Authorized Responders:
                // "Systems relying on OCSP responses MUST recognize a delegation certificate as being issued
                // by the CA that issued the certificate in question only if the delegation certificate and the
                // certificate being checked for revocation were signed by the same key."
                // and
                // "This certificate MUST be issued directly by the CA that is identified in the request"
                responderCert.verify(issuerCert.getPublicKey());

                // check if lifetime of certificate is ok
                responderCert.checkValidity(signDate);

                // validating ocsp signers certificate
                // Check if responders certificate has id-pkix-ocsp-nocheck extension,
                // in which case we do not validate (perform revocation check on) ocsp certs for lifetime of certificate
                if (responderCert.getExtensionValue(OCSPObjectIdentifiers.id_pkix_ocsp_nocheck.getId()) == null) {
                    CRL crl;
                    try {
                        // TODO should also check for Authority Information Access according to RFC6960 4.2.2.2.1. "Revocation Checking of an Authorized Responder"
                        // TODO should also respect onlineCheckingAllowed property?
                        crl = CertificateUtil.getCRL(responderCert);
                    } catch (Exception ignored) {
                        crl = (CRL) null;
                    }
                    if (crl != null && crl instanceof X509CRL) {
                        CRLVerifier crlVerifier = new CRLVerifier(null, null);
                        crlVerifier.setRootStore(rootStore);
                        crlVerifier.setOnlineCheckingAllowed(onlineCheckingAllowed);
                        if (!crlVerifier.verify((X509CRL)crl, responderCert, issuerCert, signDate)) {
                            throw new VerificationException(issuerCert, "Authorized OCSP responder certificate was revoked.");
                        }
                    } else {
                        Logger logger = LoggerFactory.getLogger(OCSPVerifier.class);
                        logger.error("Authorized OCSP responder certificate revocation status cannot be checked");
                        // TODO throw exception starting from iText version 7.2, but only after OCSPVerifier would allow explicit setting revocation check end points/provide revocation data
                        // throw new VerificationException(issuerCert, "Authorized OCSP responder certificate revocation status cannot be checked.");
                    }
                }

            } else {
                // certificate chain is not present in response received
                // try to verify using rootStore according to RFC 6960 2.2. Response:
                // "The key used to sign the response MUST belong to one of the following:
                // - ...
                // - a Trusted Responder whose public key is trusted by the requestor;
                // - ..."
                if (rootStore != null) {
                    try {
                        for (X509Certificate anchor : SignUtils.getCertificates(rootStore) ) {
                            if (isSignatureValid(ocspResp, anchor)) {
                                // certificate from the root store is considered trusted and valid by this method
                                responderCert = anchor;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        responderCert = (X509Certificate) null;
                    }
                }

                if (responderCert == null) {
                    throw new VerificationException(issuerCert, "OCSP response could not be verified: it does not contain certificate chain and response is not signed by issuer certificate or any from the root store.");
                }
            }
        }
    }

    /**
     * Checks if an OCSP response is genuine
     * @param ocspResp	the OCSP response
     * @param responderCert	the responder certificate
     * @return	true if the OCSP response verifies against the responder certificate
     */
    public boolean isSignatureValid(BasicOCSPResp ocspResp, Certificate responderCert) {
        try {
            return SignUtils.isSignatureValid(ocspResp, responderCert, "BC");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets an OCSP response online and returns it if the status is GOOD
     * (without further checking!).
     * @param signCert	the signing certificate
     * @param issuerCert	the issuer certificate
     * @return an OCSP response
     */
    public BasicOCSPResp getOcspResponse(X509Certificate signCert, X509Certificate issuerCert) {
        if (signCert == null && issuerCert == null) {
            return null;
        }
        OcspClientBouncyCastle ocsp = new OcspClientBouncyCastle(null);
        BasicOCSPResp ocspResp = ocsp.getBasicOCSPResp(signCert, issuerCert, null);
        if (ocspResp == null) {
            return null;
        }
        SingleResp[] resps = ocspResp.getResponses();
        for (SingleResp resp : resps) {
            Object status = resp.getCertStatus();
            if (status == CertificateStatus.GOOD) {
                return ocspResp;
            }
        }
        return null;
    }
}
