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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateStatus;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IRevokedStatus;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Class that allows you to verify a certificate against
 * one or more OCSP responses.
 *
 * @deprecated starting from 8.0.5.
 * {@link com.itextpdf.signatures.validation.OCSPValidator} should be used instead.
 */
@Deprecated
public class OCSPVerifier extends RootStoreVerifier {

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    /**
     * The Logger instance
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(OCSPVerifier.class);

    protected final static String id_kp_OCSPSigning = "1.3.6.1.5.5.7.3.9";

    /**
     * The list of {@link IBasicOCSPResp} OCSP response wrappers.
     */
    protected List<IBasicOCSPResp> ocsps;

    /**
     * Ocsp client to check OCSP Authorized Responder's revocation data.
     */
    private IOcspClient ocspClient;
    /**
     * Ocsp client to check OCSP Authorized Responder's revocation data.
     */
    private ICrlClient crlClient;

    /**
     * Creates an OCSPVerifier instance.
     *
     * @param verifier the next verifier in the chain
     * @param ocsps    a list of {@link IBasicOCSPResp} OCSP response wrappers for the certificate verification
     */
    public OCSPVerifier(CertificateVerifier verifier, List<IBasicOCSPResp> ocsps) {
        super(verifier);
        this.ocsps = ocsps;
    }

    /**
     * Sets OCSP client to provide OCSP responses for verifying of the OCSP signer's certificate (an Authorized
     * Responder). Also, should be used in case responder's certificate doesn't have any method of revocation checking.
     *
     * <p>
     * See RFC6960 4.2.2.2.1. Revocation Checking of an Authorized Responder.
     *
     * <p>
     * Optional. Default one is {@link OcspClientBouncyCastle}.
     *
     * @param ocspClient {@link IOcspClient} to provide an Authorized Responder revocation data.
     */
    public void setOcspClient(IOcspClient ocspClient) {
        this.ocspClient = ocspClient;
    }

    /**
     * Sets CRL client to provide CRL responses for verifying of the OCSP signer's certificate (an Authorized Responder)
     * that also should be used in case responder's certificate doesn't have any method of revocation checking.
     *
     * <p>
     * See RFC6960 4.2.2.2.1. Revocation Checking of an Authorized Responder.
     *
     * <p>
     * Optional. Default one is {@link CrlClientOnline}.
     *
     * @param crlClient {@link ICrlClient} to provide an Authorized Responder revocation data.
     */
    public void setCrlClient(ICrlClient crlClient) {
        this.crlClient = crlClient;
    }

    /**
     * Verifies if a valid OCSP response is found for the certificate.
     * If this method returns false, it doesn't mean the certificate isn't valid.
     * It means we couldn't verify it against any OCSP response that was available.
     *
     * @param signCert   the certificate that needs to be checked
     * @param issuerCert issuer of the certificate to be checked
     * @param signDate   the date the certificate needs to be valid
     *
     * @return a list of <code>VerificationOK</code> objects.
     * The list will be empty if the certificate couldn't be verified.
     *
     * @see com.itextpdf.signatures.RootStoreVerifier#verify(java.security.cert.X509Certificate,
     * java.security.cert.X509Certificate, java.util.Date)
     */
    public List<VerificationOK> verify(X509Certificate signCert, X509Certificate issuerCert, Date signDate)
            throws GeneralSecurityException {
        List<VerificationOK> result = new ArrayList<>();
        int validOCSPsFound = 0;
        // First check in the list of OCSP responses that was provided.
        if (ocsps != null) {
            for (IBasicOCSPResp ocspResp : ocsps) {
                if (verify(ocspResp, signCert, issuerCert, signDate)) {
                    validOCSPsFound++;
                }
            }
        }
        // Then check online if allowed.
        boolean online = false;
        int validOCSPsFoundOnline = 0;
        if (onlineCheckingAllowed && verify(getOcspResponse(signCert, issuerCert), signCert, issuerCert, signDate)) {
            validOCSPsFound++;
            validOCSPsFoundOnline++;
            online = true;
        }
        // Show how many valid OCSP responses were found.
        LOGGER.info("Valid OCSPs found: " + validOCSPsFound);
        if (validOCSPsFound > 0) {
            result.add(new VerificationOK(signCert, this.getClass(),
                    "Valid OCSPs Found: " + validOCSPsFound + (online ?
                            (" (" + validOCSPsFoundOnline + " online)") : "")));
        }
        // Verify using the previous verifier in the chain (if any).
        if (verifier != null) {
            result.addAll(verifier.verify(signCert, issuerCert, signDate));
        }
        return result;
    }

    /**
     * Verifies a certificate against a single OCSP response.
     *
     * @param ocspResp   {@link IBasicOCSPResp} the OCSP response wrapper for a certificate verification
     * @param signCert   the certificate that needs to be checked
     * @param issuerCert the certificate that issued signCert – immediate parent. This certificate is considered
     *                   trusted and valid by this method.
     * @param signDate   sign date (or the date the certificate needs to be valid)
     *
     * @return {@code true} in case check is successful, false otherwise.
     *
     * @throws GeneralSecurityException if OCSP response verification cannot be done or failed.
     */
    public boolean verify(IBasicOCSPResp ocspResp, X509Certificate signCert, X509Certificate issuerCert, Date signDate)
            throws GeneralSecurityException {
        if (ocspResp == null) {
            return false;
        }
        // Getting the responses.
        ISingleResp[] resp = ocspResp.getResponses();
        for (ISingleResp iSingleResp : resp) {
            // SingleResp contains the basic information of the status of the certificate identified by the certID.
            // Check if the serial numbers of the signCert and certID corresponds:
            if (!signCert.getSerialNumber().equals(iSingleResp.getCertID().getSerialNumber())) {
                continue;
            }
            // Check if the issuer of the certID and signCert matches, i.e. check that issuerNameHash and issuerKeyHash
            // fields of the certID is the hash of the issuer's name and public key:
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
            // So, since the issuer name and serial number identify a unique certificate, we found the single response
            // for the signCert.

            // OCSP response can be created after the signing, so we won't compare signDate with thisUpdate.

            // If nextUpdate is not set, the responder is indicating that newer revocation information
            // is available all the time.
            if (iSingleResp.getNextUpdate() != null && signDate.after(iSingleResp.getNextUpdate())) {
                LOGGER.info(MessageFormatUtil.format("OCSP is no longer valid: {0} after {1}", signDate,
                        iSingleResp.getNextUpdate()));
                continue;
            }
            // Check the status of the certificate:
            ICertificateStatus status = iSingleResp.getCertStatus();
            IRevokedStatus revokedStatus = BOUNCY_CASTLE_FACTORY.createRevokedStatus(status);
            boolean isStatusGood = BOUNCY_CASTLE_FACTORY.createCertificateStatus().getGood().equals(status);
            if (isStatusGood || (revokedStatus != null && signDate.before(revokedStatus.getRevocationTime()))) {
                // Check if the OCSP response was genuine.
                isValidResponse(ocspResp, issuerCert, signDate);
                if (!isStatusGood) {
                    LOGGER.warn(MessageFormatUtil.format(SignLogMessageConstant.VALID_CERTIFICATE_IS_REVOKED,
                            revokedStatus.getRevocationTime()));
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Verifies if an OCSP response is genuine.
     * If it doesn't verify against the issuer certificate and response's certificates, it may verify
     * using a trusted anchor or cert.
     *
     * @param ocspResp   {@link IBasicOCSPResp} the OCSP response wrapper
     * @param issuerCert the issuer certificate. This certificate is considered trusted and valid by this method.
     * @param signDate   sign date for backwards compatibility
     *
     * @throws GeneralSecurityException if OCSP response verification cannot be done or failed.
     */
    public void isValidResponse(IBasicOCSPResp ocspResp, X509Certificate issuerCert, Date signDate)
            throws GeneralSecurityException {
        // OCSP response might be signed by the issuer certificate or
        // the Authorized OCSP responder certificate containing the id-kp-OCSPSigning extended key usage extension.
        X509Certificate responderCert = null;

        // First check if the issuer certificate signed the response since it is expected to be the most common case:
        if (isSignatureValid(ocspResp, issuerCert)) {
            responderCert = issuerCert;
        }

        // If the issuer certificate didn't sign the ocsp response, look for authorized ocsp responses
        // from the properties or from the certificate chain received with response.
        if (responderCert == null) {
            if (ocspResp.getCerts().length > 0) {
                // Look for the existence of an Authorized OCSP responder inside the cert chain in the ocsp response.
                Iterable<X509Certificate> certs = SignUtils.getCertsFromOcspResponse(ocspResp);
                for (X509Certificate cert : certs) {
                    try {
                        List keyPurposes = cert.getExtendedKeyUsage();
                        if (keyPurposes != null && keyPurposes.contains(id_kp_OCSPSigning)
                                && isSignatureValid(ocspResp, cert)) {
                            responderCert = cert;
                            break;
                        }
                    } catch (CertificateParsingException ignored) {
                    }
                }

                // Certificate signing the ocsp response is not found in ocsp response's certificate chain received
                // and is not signed by the issuer certificate.
                // RFC 6960 4.2.1. ASN.1 Specification of the OCSP Response: "The responder MAY include certificates in
                // the certs field of BasicOCSPResponse that help the OCSP client verify the responder's signature.
                // If no certificates are included, then certs SHOULD be absent".
                if (responderCert == null) {
                    throw new VerificationException(issuerCert, "OCSP response could not be verified");
                }

                // RFC 6960 4.2.2.2. Authorized Responders:
                // "Systems relying on OCSP responses MUST recognize a delegation certificate as being issued
                // by the CA that issued the certificate in question only if the delegation certificate and the
                // certificate being checked for revocation were signed by the same key."
                // and "This certificate MUST be issued directly by the CA that is identified in the request".
                responderCert.verify(issuerCert.getPublicKey());

                // Check if the lifetime of the certificate is valid. Responder cert could be created after the signing.
                responderCert.checkValidity(ocspResp.getProducedAt());

                // Validating ocsp signer's certificate (responderCert).
                // See RFC6960 4.2.2.2.1. Revocation Checking of an Authorized Responder.

                // 1. Check if responders certificate has id-pkix-ocsp-nocheck extension, in which case we do not
                // validate (perform revocation check on) ocsp certs for the lifetime of the responder certificate.
                if (SignUtils.getExtensionValueByOid(responderCert, BOUNCY_CASTLE_FACTORY.createOCSPObjectIdentifiers()
                        .getIdPkixOcspNoCheck().getId()) != null) {
                    return;
                }

                // 2.1. Try to check responderCert for revocation using provided responder OCSP/CRL clients.
                if (ocspClient != null) {
                    IBasicOCSPResp responderOcspResp = null;
                    byte[] basicOcspRespBytes = ocspClient.getEncoded(responderCert, issuerCert, null);
                    if (basicOcspRespBytes != null) {
                        try {
                            responderOcspResp = BOUNCY_CASTLE_FACTORY.createBasicOCSPResp(
                                    BOUNCY_CASTLE_FACTORY.createBasicOCSPResponse(BOUNCY_CASTLE_FACTORY.createASN1Primitive(
                                            basicOcspRespBytes)));
                        } catch (IOException ignored) {
                        }
                    }
                    if (verifyOcsp(responderOcspResp, responderCert, issuerCert, ocspResp.getProducedAt())) {
                        return;
                    }
                }
                if (crlClient != null &&
                        checkCrlResponses(crlClient, responderCert, issuerCert, ocspResp.getProducedAt())) {
                    return;
                }
                // 2.2. Try to check responderCert for revocation using Authority Information Access for OCSP responses
                // or CRL Distribution Points for CRL responses using default clients.
                IBasicOCSPResp responderOcspResp = new OcspClientBouncyCastle()
                        .getBasicOCSPResp(responderCert, issuerCert, null);
                if (verifyOcsp(responderOcspResp, responderCert, issuerCert, ocspResp.getProducedAt())) {
                    return;
                }
                if (checkCrlResponses(new CrlClientOnline(), responderCert, issuerCert, ocspResp.getProducedAt())) {
                    return;
                }

                // 3. "A CA may choose not to specify any method of revocation checking for the responder's
                // certificate, in which case it would be up to the OCSP client's local security policy
                // to decide whether that certificate should be checked for revocation or not".
                throw new VerificationException(responderCert,
                        "Authorized OCSP responder certificate revocation status cannot be checked");
            } else {
                // Certificate chain is not present in the response received.
                // Try to verify using rootStore according to RFC 6960 2.2. Response:
                // "The key used to sign the response MUST belong to one of the following:
                // - ...
                // - a Trusted Responder whose public key is trusted by the requester;
                // - ..."
                if (rootStore != null) {
                    try {
                        for (X509Certificate anchor : SignUtils.getCertificates(rootStore)) {
                            if (isSignatureValid(ocspResp, anchor)) {
                                // Certificate from the root store is considered trusted and valid by this method.
                                responderCert = anchor;
                                break;
                            }
                        }
                    } catch (Exception ignored) {
                        // Ignore.
                    }
                }

                if (responderCert == null) {
                    throw new VerificationException(issuerCert, "OCSP response could not be verified: it does not contain certificate chain and response is not signed by issuer certificate or any from the root store.");
                }
            }
        }
        // Check if the lifetime of the certificate is valid.
        responderCert.checkValidity(ocspResp.getProducedAt());
    }

    /**
     * Checks if an OCSP response is genuine.
     *
     * @param ocspResp      {@link IBasicOCSPResp} the OCSP response wrapper
     * @param responderCert the responder certificate
     *
     * @return true if the OCSP response verifies against the responder certificate.
     */
    public boolean isSignatureValid(IBasicOCSPResp ocspResp, Certificate responderCert) {
        try {
            return SignUtils.isSignatureValid(ocspResp, responderCert, BOUNCY_CASTLE_FACTORY.getProviderName());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets an OCSP response online and returns it without further checking.
     *
     * @param signCert   the signing certificate
     * @param issuerCert the issuer certificate
     *
     * @return {@link IBasicOCSPResp} an OCSP response wrapper.
     */
    public IBasicOCSPResp getOcspResponse(X509Certificate signCert, X509Certificate issuerCert) {
        if (signCert == null && issuerCert == null) {
            return null;
        }
        OcspClientBouncyCastle ocsp = new OcspClientBouncyCastle();
        return ocsp.getBasicOCSPResp(signCert, issuerCert, null);
    }

    private boolean verifyOcsp(IBasicOCSPResp ocspResp, X509Certificate certificate, X509Certificate issuerCert,
                               Date signDate) throws GeneralSecurityException {
        if (ocspResp == null) {
            // Unable to verify.
            return false;
        }
        return this.verify(ocspResp, certificate, issuerCert, signDate);
    }

    private boolean checkCrlResponses(ICrlClient client, X509Certificate responderCert, X509Certificate issuerCert,
                                      Date signDate) throws GeneralSecurityException {
        Collection<byte[]> crlBytesCollection = client.getEncoded(responderCert, null);
        for (byte[] crlBytes : crlBytesCollection) {
            CRL crl = SignUtils.parseCrlFromStream(new ByteArrayInputStream(crlBytes));
            if (verifyCrl(crl, responderCert, issuerCert, signDate)) {
                return true;
            }
        }
        return false;
    }

    private boolean verifyCrl(CRL crl, X509Certificate certificate, X509Certificate issuerCert, Date signDate)
            throws GeneralSecurityException {
        if (crl instanceof X509CRL) {
            CRLVerifier crlVerifier = new CRLVerifier(null, null);
            crlVerifier.setRootStore(rootStore);
            crlVerifier.setOnlineCheckingAllowed(onlineCheckingAllowed);
            return crlVerifier.verify((X509CRL) crl, certificate, issuerCert, signDate);
        }
        // Unable to verify.
        return false;
    }
}
