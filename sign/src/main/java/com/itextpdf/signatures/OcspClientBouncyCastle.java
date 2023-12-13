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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.StreamUtil;
import org.bouncycastle.asn1.ocsp.OCSPResponseStatus;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.CertificateStatus;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.RevokedStatus;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.bouncycastle.operator.OperatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

/**
 * OcspClient implementation using BouncyCastle.
 *
 * @author Paulo Soarees
 */
public class OcspClientBouncyCastle implements IOcspClient {

    /**
     * The Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OcspClientBouncyCastle.class);

    private final OCSPVerifier verifier;

    /**
     * Create {@code OcspClient}
     *
     * @param verifier will be used for response verification.
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
     * @return OCSP response
     */
    public BasicOCSPResp getBasicOCSPResp(X509Certificate checkCert, X509Certificate rootCert, String url) {
        try {
            OCSPResp ocspResponse = getOcspResponse(checkCert, rootCert, url);
            if (ocspResponse == null) {
                return null;
            }
            if (ocspResponse.getStatus() != OCSPResponseStatus.SUCCESSFUL) {
                return null;
            }
            BasicOCSPResp basicResponse = (BasicOCSPResp) ocspResponse.getResponseObject();
            if (verifier != null) {
                verifier.isValidResponse(basicResponse, rootCert);
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
            BasicOCSPResp basicResponse = getBasicOCSPResp(checkCert, rootCert, url);
            if (basicResponse != null) {
                SingleResp[] responses = basicResponse.getResponses();
                if (responses.length == 1) {
                    SingleResp resp = responses[0];
                    Object status = resp.getCertStatus();
                    if (status == CertificateStatus.GOOD) {
                        return basicResponse.getEncoded();
                    } else if (status instanceof RevokedStatus) {
                        throw new java.io.IOException(LogMessageConstant.OCSP_STATUS_IS_REVOKED);
                    } else {
                        throw new java.io.IOException(LogMessageConstant.OCSP_STATUS_IS_UNKNOWN);
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
     * @return an OCSP request
     * @throws OCSPException
     * @throws IOException
     */
    private static OCSPReq generateOCSPRequest(X509Certificate issuerCert, BigInteger serialNumber) throws OCSPException, IOException,
            OperatorException, CertificateEncodingException {
        //Add provider BC
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // Generate the id for the certificate we are looking for
        CertificateID id = SignUtils.generateCertificateId(issuerCert, serialNumber, CertificateID.HASH_SHA1);

        // basic request generation with nonce
        return SignUtils.generateOcspRequestWithNonce(id);
    }

    /**
     * Gets an OCSP response object using BouncyCastle.
     *
     * @param checkCert to certificate to check
     * @param rootCert  the parent certificate
     * @param url       to get the verification. It it's null it will be taken
     *                  from the check cert or from other implementation specific source
     * @return an OCSP response
     * @throws GeneralSecurityException if any execution errors occur
     * @throws OCSPException if any errors occur while handling OCSP requests/responses
     * @throws IOException if any I/O execution errors occur
     * @throws OperatorException if any BC execution errors occur
     */
    OCSPResp getOcspResponse(X509Certificate checkCert, X509Certificate rootCert, String url)
            throws GeneralSecurityException, OCSPException, IOException, OperatorException {
        if (checkCert == null || rootCert == null)
            return null;
        if (url == null) {
            url = CertificateUtil.getOCSPURL(checkCert);
        }
        if (url == null)
            return null;
        LOGGER.info("Getting OCSP from " + url);
        OCSPReq request = generateOCSPRequest(rootCert, checkCert.getSerialNumber());
        byte[] array = request.getEncoded();
        URL urlt = new URL(url);
        InputStream in = SignUtils.getHttpResponseForOcspRequest(array, urlt);
        return new OCSPResp(StreamUtil.inputStreamToArray(in));
    }
}
