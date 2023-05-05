/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.utils.MessageFormatUtil;

import java.util.Objects;
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

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    /** The Logger instance */
    protected static final Logger LOGGER = LoggerFactory.getLogger(OCSPVerifier.class);

    protected final static String id_kp_OCSPSigning = "1.3.6.1.5.5.7.3.9";

    /** The list of {@link IBasicOCSPResp} OCSP response wrappers. */
    protected List<IBasicOCSPResp> ocsps;

    /**
     * Creates an OCSPVerifier instance.
     * @param verifier	the next verifier in the chain
     * @param ocsps a list of {@link IBasicOCSPResp} OCSP response wrappers
     */
    public OCSPVerifier(CertificateVerifier verifier, List<IBasicOCSPResp> ocsps) {
        super(verifier);
        this.ocsps = ocsps;
    }

    /**
     * Verifies if a valid OCSP response is found for the certificate.
     * If this method returns false, it doesn't mean the certificate isn't valid.
     * It means we couldn't verify it against any OCSP response that was available.
     * @param signCert	    the certificate that needs to be checked
     * @param issuerCert	its issuer
     *
     * @return a list of <code>VerificationOK</code> objects.
     * The list will be empty if the certificate couldn't be verified.
     * @see com.itextpdf.signatures.RootStoreVerifier#verify(java.security.cert.X509Certificate,
     *        java.security.cert.X509Certificate, java.util.Date)
     */
    public List<VerificationOK> verify(X509Certificate signCert, X509Certificate issuerCert, Date signDate)
            throws GeneralSecurityException {
        List<VerificationOK> result = new ArrayList<>();
        int validOCSPsFound = 0;
        // first check in the list of OCSP responses that was provided
        if (ocsps != null) {
            for (IBasicOCSPResp ocspResp : ocsps) {
                if (verify(ocspResp, signCert, issuerCert, signDate)) {
                    validOCSPsFound++;
                }
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
        if (validOCSPsFound > 0) {
            result.add(new VerificationOK(signCert, this.getClass(),
                    "Valid OCSPs Found: " + validOCSPsFound + (online ? " (online)" : "")));
        }
        if (verifier != null) {
            result.addAll(verifier.verify(signCert, issuerCert, signDate));
        }
        // verify using the previous verifier in the chain (if any)
        return result;
    }


    /**
     * Verifies a certificate against a single OCSP response
     * @param ocspResp   {@link IBasicOCSPResp} the OCSP response wrapper
     * @param signCert   the certificate that needs to be checked
     * @param issuerCert the certificate of CA (certificate that issued signCert). This certificate is considered trusted
     *                   and valid by this method.
     * @param signDate   sign date
     *
     * @return {@code true}, in case successful check, otherwise false.
     * @throws GeneralSecurityException if OCSP response verification cannot be done or failed
     */
    public boolean verify(IBasicOCSPResp ocspResp, X509Certificate signCert, X509Certificate issuerCert, Date signDate)
            throws GeneralSecurityException {
        if (ocspResp == null) {
            return false;
        }
        // Getting the responses
        ISingleResp[] resp = ocspResp.getResponses();
        for (ISingleResp iSingleResp : resp) {
            // check if the serial number corresponds
            if (!signCert.getSerialNumber().equals(iSingleResp.getCertID().getSerialNumber())) {
                continue;
            }
            // check if the issuer matches
            try {
                if (issuerCert == null) {
                    issuerCert = signCert;
                }
                if (!SignUtils.checkIfIssuersMatch(iSingleResp.getCertID(), issuerCert)) {
                    LOGGER.info("OCSP: Issuers doesn't match.");
                    continue;
                }
            } catch (IOException e) {
                throw new GeneralSecurityException(e.getMessage());
            } catch (AbstractOCSPException | AbstractOperatorCreationException e) {
                continue;
            }
            // check if the OCSP response was valid at the time of signing
            if (iSingleResp.getNextUpdate() == null) {
                Date nextUpdate = SignUtils.add180Sec(iSingleResp.getThisUpdate());
                LOGGER.info(MessageFormatUtil.format("No 'next update' for OCSP Response; assuming {0}", nextUpdate));
                if (signDate.after(nextUpdate)) {
                    LOGGER.info(MessageFormatUtil.format("OCSP no longer valid: {0} after {1}", signDate, nextUpdate));
                    continue;
                }
            } else {
                if (signDate.after(iSingleResp.getNextUpdate())) {
                    LOGGER.info(MessageFormatUtil.format("OCSP no longer valid: {0} after {1}", signDate,
                            iSingleResp.getNextUpdate()));
                    continue;
                }
            }
            // check the status of the certificate
            Object status = iSingleResp.getCertStatus();
            if (Objects.equals(status, BOUNCY_CASTLE_FACTORY.createCertificateStatus().getGood())) {
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
     * @param ocspResp {@link IBasicOCSPResp} the OCSP response wrapper
     * @param issuerCert the issuer certificate. This certificate is considered trusted and valid by this method.
     * @param signDate sign date
     *
     * @throws GeneralSecurityException if OCSP response verification cannot be done or failed
     */
    public void isValidResponse(IBasicOCSPResp ocspResp, X509Certificate issuerCert, Date signDate)
            throws GeneralSecurityException {
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
                        if ((keyPurposes != null) && keyPurposes.contains(id_kp_OCSPSigning)
                                && isSignatureValid(ocspResp, cert)) {
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
                if (responderCert.getExtensionValue(
                        BOUNCY_CASTLE_FACTORY.createOCSPObjectIdentifiers().getIdPkixOcspNoCheck().getId()) == null) {
                    CRL crl;
                    try {
                        // TODO DEVSIX-5210 Implement a check heck for Authority Information Access according to
                        // RFC6960 4.2.2.2.1. "Revocation Checking of an Authorized Responder"
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
                        LOGGER.error("Authorized OCSP responder certificate revocation status cannot be checked");
                        // TODO DEVSIX-5207 throw exception starting from iText version 7.2, but only after OCSPVerifier
                        // would allow explicit setting revocation check end points/provide revocation data
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
     * @param ocspResp	{@link IBasicOCSPResp} the OCSP response wrapper
     * @param responderCert	the responder certificate
     * @return	true if the OCSP response verifies against the responder certificate
     */
    public boolean isSignatureValid(IBasicOCSPResp ocspResp, Certificate responderCert) {
        try {
            return SignUtils.isSignatureValid(ocspResp, responderCert, BOUNCY_CASTLE_FACTORY.getProviderName());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets an OCSP response online and returns it if the status is GOOD
     * (without further checking!).
     * @param signCert	the signing certificate
     * @param issuerCert	the issuer certificate
     * @return {@link IBasicOCSPResp} an OCSP response wrapper
     */
    public IBasicOCSPResp getOcspResponse(X509Certificate signCert, X509Certificate issuerCert) {
        if (signCert == null && issuerCert == null) {
            return null;
        }
        OcspClientBouncyCastle ocsp = new OcspClientBouncyCastle(null);
        IBasicOCSPResp ocspResp = ocsp.getBasicOCSPResp(signCert, issuerCert, null);
        if (ocspResp == null) {
            return null;
        }
        ISingleResp[] resps = ocspResp.getResponses();
        for (ISingleResp resp : resps) {
            Object status = resp.getCertStatus();
            if (Objects.equals(status, BOUNCY_CASTLE_FACTORY.createCertificateStatus().getGood())) {
                return ocspResp;
            }
        }
        return null;
    }
}
