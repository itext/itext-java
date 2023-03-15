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
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateStatus;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReq;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.util.StreamUtil;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OcspClient implementation using BouncyCastle.
 *
 * @author Paulo Soarees
 */
public class OcspClientBouncyCastle implements IOcspClient {

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    /**
     * The Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OcspClientBouncyCastle.class);

    private final OCSPVerifier verifier;

    /**
     * Creates {@code OcspClient}.
     *
     * @param verifier will be used for response verification.
     *
     * @see OCSPVerifier
     */
    public OcspClientBouncyCastle(OCSPVerifier verifier) {
        this.verifier = verifier;
    }

    /**
     * Gets OCSP response. If {@link OCSPVerifier} was set, the response will be checked.
     *
     * @param checkCert to certificate to check
     * @param rootCert  the parent certificate
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
            IBasicOCSPResp basicResponse = BOUNCY_CASTLE_FACTORY.createBasicOCSPResp(ocspResponse.getResponseObject());
            if (verifier != null) {
                verifier.isValidResponse(basicResponse, rootCert, DateTimeUtil.getCurrentTimeDate());
            }
            return basicResponse;
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
                    if (Objects.equals(status, BOUNCY_CASTLE_FACTORY.createCertificateStatus().getGood())) {
                        return basicResponse.getEncoded();
                    } else if (BOUNCY_CASTLE_FACTORY.createRevokedStatus(status) == null) {
                        throw new IOException(IoLogMessageConstant.OCSP_STATUS_IS_UNKNOWN);
                    } else {
                        throw new IOException(IoLogMessageConstant.OCSP_STATUS_IS_REVOKED);
                    }
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
     */
    private static IOCSPReq generateOCSPRequest(X509Certificate issuerCert, BigInteger serialNumber)
            throws AbstractOCSPException, IOException, CertificateEncodingException, AbstractOperatorCreationException {
        //Add provider BC
        Security.addProvider(BOUNCY_CASTLE_FACTORY.getProvider());

        // Generate the id for the certificate we are looking for
        ICertificateID id = SignUtils.generateCertificateId(issuerCert, serialNumber,
                BOUNCY_CASTLE_FACTORY.createCertificateID().getHashSha1());

        // basic request generation with nonce
        return SignUtils.generateOcspRequestWithNonce(id);
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
        LOGGER.info("Getting OCSP from " + url);
        IOCSPReq request = generateOCSPRequest(rootCert, checkCert.getSerialNumber());
        byte[] array = request.getEncoded();
        URL urlt = new URL(url);
        InputStream in = SignUtils.getHttpResponseForOcspRequest(array, urlt);
        return BOUNCY_CASTLE_FACTORY.createOCSPResp(StreamUtil.inputStreamToArray(in));
    }
}
