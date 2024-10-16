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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateStatus;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReq;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.signatures.validation.OCSPValidator;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OcspClient implementation using BouncyCastle.
 */
public class OcspClientBouncyCastle implements IOcspClient {

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    /**
     * The Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OcspClientBouncyCastle.class);

    /**
     * Creates new {@link OcspClientBouncyCastle} instance.
     */
    public OcspClientBouncyCastle() {
        // Empty constructor in order for default one to not be removed if another one is added.
    }

    /**
     * Gets OCSP response.
     *
     * <p>
     * If required, {@link IBasicOCSPResp} can be checked using {@link OCSPValidator} class.
     *
     * @param checkCert the certificate to check
     * @param rootCert  parent certificate
     * @param url       to get the verification
     *
     * @return {@link IBasicOCSPResp} an OCSP response wrapper
     */
    public IBasicOCSPResp getBasicOCSPResp(X509Certificate checkCert, X509Certificate rootCert, String url) {
        try {
            IOCSPResp ocspResponse = getOcspResponse(checkCert, rootCert, url);
            if (ocspResponse == null) {
                return null;
            }
            if (ocspResponse.getStatus() != BOUNCY_CASTLE_FACTORY.createOCSPResponseStatus().getSuccessful()) {
                return null;
            }
            return BOUNCY_CASTLE_FACTORY.createBasicOCSPResp(ocspResponse.getResponseObject());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getEncoded(X509Certificate checkCert, X509Certificate rootCert, String url) {
        try {
            IBasicOCSPResp basicResponse = getBasicOCSPResp(checkCert, rootCert, url);
            if (basicResponse != null) {
                ISingleResp[] responses = basicResponse.getResponses();
                if (responses.length == 1) {
                    ISingleResp resp = responses[0];
                    ICertificateStatus status = resp.getCertStatus();
                    if (!BOUNCY_CASTLE_FACTORY.createCertificateStatus().getGood().equals(status)) {
                        if (BOUNCY_CASTLE_FACTORY.createRevokedStatus(status) == null) {
                            LOGGER.info(IoLogMessageConstant.OCSP_STATUS_IS_UNKNOWN);
                        } else {
                            LOGGER.info(IoLogMessageConstant.OCSP_STATUS_IS_REVOKED);
                        }
                    }
                    return basicResponse.getEncoded();
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
        return null;
    }


    /**
     * Generates an OCSP request using BouncyCastle.
     *
     * @param issuerCert   certificate of the issues
     * @param serialNumber serial number
     *
     * @return {@link IOCSPReq} an OCSP request wrapper
     *
     * @throws AbstractOCSPException is thrown if any errors occur while handling OCSP requests/responses
     * @throws IOException           signals that an I/O exception has occurred
     * @throws CertificateEncodingException is thrown if any errors occur while handling OCSP requests/responses
     * @throws AbstractOperatorCreationException is thrown if any errors occur while handling OCSP requests/responses
     */
    protected static IOCSPReq generateOCSPRequest(X509Certificate issuerCert, BigInteger serialNumber)
            throws AbstractOCSPException, IOException, CertificateEncodingException, AbstractOperatorCreationException {
        // Generate the id for the certificate we are looking for
        ICertificateID id = SignUtils.generateCertificateId(issuerCert, serialNumber,
                BOUNCY_CASTLE_FACTORY.createCertificateID().getHashSha1());

        // basic request generation with nonce
        return SignUtils.generateOcspRequestWithNonce(id);
    }

    /**
     * Retrieves certificate status from the OCSP response.
     *
     * @param basicOcspRespBytes encoded basic OCSP response
     *
     * @return good, revoked or unknown certificate status retrieved from the OCSP response, or null if an error occurs.
     */
    protected static ICertificateStatus getCertificateStatus(byte[] basicOcspRespBytes) {
        try {
            IBasicOCSPResp basicResponse = BOUNCY_CASTLE_FACTORY.createBasicOCSPResp(
                    BOUNCY_CASTLE_FACTORY.createBasicOCSPResponse(BOUNCY_CASTLE_FACTORY.createASN1Primitive(
                            basicOcspRespBytes)));
            if (basicResponse != null) {
                ISingleResp[] responses = basicResponse.getResponses();
                if (responses.length >= 1) {
                    return responses[0].getCertStatus();
                }
            }
        } catch (Exception ignored) {
            // Ignore exception.
        }
        return null;
    }

    /**
     * Gets an OCSP response object using BouncyCastle.
     *
     * @param checkCert to certificate to check
     * @param rootCert  the parent certificate
     * @param url       to get the verification. If it's null it will be taken
     *                  from the check cert or from other implementation specific source
     *
     * @return {@link IOCSPResp} an OCSP response wrapper
     *
     * @throws GeneralSecurityException          if any execution errors occur
     * @throws AbstractOCSPException             if any errors occur while handling OCSP requests/responses
     * @throws IOException                       if any I/O execution errors occur
     * @throws AbstractOperatorCreationException if any BC execution errors occur
     */
    IOCSPResp getOcspResponse(X509Certificate checkCert, X509Certificate rootCert, String url)
            throws GeneralSecurityException, AbstractOCSPException, IOException, AbstractOperatorCreationException {
        if (checkCert == null || rootCert == null) {
            return null;
        }
        if (url == null) {
            url = CertificateUtil.getOCSPURL(checkCert);
        }
        if (url == null) {
            return null;
        }
        InputStream in = createRequestAndResponse(checkCert, rootCert, url);
        return in == null ? null : BOUNCY_CASTLE_FACTORY.createOCSPResp(StreamUtil.inputStreamToArray(in));
    }

    /**
     * Create OCSP request and get the response for this request, represented as {@link InputStream}.
     * 
     * @param checkCert {@link X509Certificate} certificate to get OCSP response for
     * @param rootCert {@link X509Certificate} root certificate from which OCSP request will be built
     * @param url {@link URL} link, which is expected to be used to get OCSP response from
     * 
     * @return OCSP response bytes, represented as {@link InputStream}
     * 
     * @throws IOException if an I/O error occurs
     * @throws AbstractOperatorCreationException is thrown if any errors occur while handling OCSP requests/responses
     * @throws AbstractOCSPException is thrown if any errors occur while handling OCSP requests/responses
     * @throws CertificateEncodingException is thrown if any errors occur while handling OCSP requests/responses
     */
    protected InputStream createRequestAndResponse(X509Certificate checkCert, X509Certificate rootCert, String url)
            throws IOException, AbstractOperatorCreationException, AbstractOCSPException, CertificateEncodingException {
        LOGGER.info("Getting OCSP from " + url);
        IOCSPReq request = generateOCSPRequest(rootCert, checkCert.getSerialNumber());
        byte[] array = request.getEncoded();
        URL urlt = new URL(url);
        return SignUtils.getHttpResponseForOcspRequest(array, urlt);
    }
}
