/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfEncryption;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.CertificateStatus;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.bouncycastle.ocsp.OCSPRespStatus;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OcspClient implementation using BouncyCastle.
 *
 * @author Paulo Soarees
 */
public class OcspClientBouncyCastle implements OcspClient {

    /**
     * The Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OcspClientBouncyCastle.class);

    private final OCSPVerifier verifier;

    /**
     * Create {@code OcspClient}
     * @param verifier will be used for response verification. {@see OCSPVerifier}.
     */
    public OcspClientBouncyCastle(OCSPVerifier verifier) {
        this.verifier = verifier;
    }

    /**
     * Gets OCSP response. If {@see OCSPVerifier} was setted, the response will be checked.
     */
    public BasicOCSPResp getBasicOCSPResp(X509Certificate checkCert, X509Certificate rootCert, String url) {
        try {
            OCSPResp ocspResponse = getOcspResponse(checkCert, rootCert, url);
            if (ocspResponse == null) {
                return null;
            }
            if (ocspResponse.getStatus() != OCSPRespStatus.SUCCESSFUL) {
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
     * Gets an encoded byte array with OCSP validation. The method should not throw an exception.
     *
     * @param checkCert to certificate to check
     * @param rootCert  the parent certificate
     * @param url       to get the verification. It it's null it will be taken
     *                  from the check cert or from other implementation specific source
     * @return a byte array with the validation or null if the validation could not be obtained
     */
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
                    } else if (status instanceof org.bouncycastle.ocsp.RevokedStatus) {
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
        CertificateID id = new CertificateID(
                new JcaDigestCalculatorProviderBuilder().build().get(CertificateID.HASH_SHA1),
                new JcaX509CertificateHolder(issuerCert), serialNumber);

        // basic request generation with nonce
        OCSPReqBuilder gen = new OCSPReqBuilder();
        gen.addRequest(id);

        Extension ext = new Extension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, false, new DEROctetString(new DEROctetString(PdfEncryption.generateNewDocumentId()).getEncoded()));
        gen.setRequestExtensions(new Extensions(new Extension[]{ext}));
        return gen.build();
    }

    private OCSPResp getOcspResponse(X509Certificate checkCert, X509Certificate rootCert, String url) throws GeneralSecurityException, OCSPException, IOException, OperatorException {
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
        HttpURLConnection con = (HttpURLConnection) urlt.openConnection();
        con.setRequestProperty("Content-Type", "application/ocsp-request");
        con.setRequestProperty("Accept", "application/ocsp-response");
        con.setDoOutput(true);
        OutputStream out = con.getOutputStream();
        DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
        dataOut.write(array);
        dataOut.flush();
        dataOut.close();
        if (con.getResponseCode() / 100 != 2) {
            throw new PdfException(PdfException.InvalidHttpResponse1).setMessageParams(con.getResponseCode());
        }
        //Get Response
        InputStream in = (InputStream) con.getContent();
        return new OCSPResp(StreamUtil.inputStreamToArray(in));
    }
}
