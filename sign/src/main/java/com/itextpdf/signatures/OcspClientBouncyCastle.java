package com.itextpdf.signatures;

import com.itextpdf.core.PdfException;
import com.itextpdf.basics.source.StreamUtil;
import com.itextpdf.core.pdf.PdfEncryption;

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

    /**
     * Generates an OCSP request using BouncyCastle.
     *
     * @param issuerCert	certificate of the issues
     * @param serialNumber	serial number
     * @return	an OCSP request
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

        Extension ext = new Extension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, false, new DEROctetString(new DEROctetString(PdfEncryption.createDocumentId()).getEncoded()));
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
        HttpURLConnection con = (HttpURLConnection)urlt.openConnection();
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

    /**
     * Get the basic OCSP response.
     *
     * @param checkCert the signing certificate
     * @param rootCert the root certificate
     * @param url the url to the ocsp server
     * @return BasicOCSPResp
     */
    public BasicOCSPResp getBasicOCSPResp(X509Certificate checkCert, X509Certificate rootCert, String url) {
        try {
            OCSPResp ocspResponse = getOcspResponse(checkCert, rootCert, url);
            if (ocspResponse == null)
                return null;
            if (ocspResponse.getStatus() != 0)
                return null;
            return (BasicOCSPResp) ocspResponse.getResponseObject();
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
        return null;
    }

    /**
     * Gets an encoded byte array with OCSP validation. The method should not throw an exception.
     * @param checkCert to certificate to check
     * @param rootCert the parent certificate
     * @param url the url to get the verification. It it's null it will be taken
     * from the check cert or from other implementation specific source
     * @return	a byte array with the validation or null if the validation could not be obtained
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
                    }
                    else if (status instanceof org.bouncycastle.ocsp.RevokedStatus) {
                        throw new IOException(PdfException.OcspStatusIsRevoked);
                    }
                    else {
                        throw new IOException(PdfException.OcspStatusIsUnknown);
                    }
                }
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
        return null;
    }
}
