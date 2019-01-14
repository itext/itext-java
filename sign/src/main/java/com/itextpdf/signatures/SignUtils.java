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

import com.itextpdf.io.codec.Base64;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfEncryption;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.esf.SigPolicyQualifierInfo;
import org.bouncycastle.asn1.esf.SigPolicyQualifiers;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.X509CertParser;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.x509.util.StreamParsingException;

final class SignUtils {
    static final Object UNDEFINED_TIMESTAMP_DATE = null;

    private static final int KEY_USAGE_DIGITAL_SIGNATURE = 0;
    private static final int KEY_USAGE_NON_REPUDIATION = 1;

    static String getPrivateKeyAlgorithm(PrivateKey pk) {
        String algorithm = pk.getAlgorithm();

        if (algorithm.equals("EC")) {
            algorithm = "ECDSA";
        }
        return algorithm;
    }

    /**
     * Parses a CRL from an InputStream.
     *
     * @param input                     The InputStream holding the unparsed CRL.
     * @return the parsed CRL object
     * @throws CertificateException     thrown when no provider has been found for X509
     * @throws CRLException             thrown during parsing the CRL
     */
    static CRL parseCrlFromStream(InputStream input) throws CertificateException, CRLException {
        return CertificateFactory.getInstance("X.509").generateCRL(input);
    }

    static byte[] getExtensionValueByOid(X509Certificate certificate, String oid) {
        return certificate.getExtensionValue(oid);
    }

    static MessageDigest getMessageDigest(String hashAlgorithm) throws GeneralSecurityException {
        return new BouncyCastleDigest().getMessageDigest(hashAlgorithm);
    }

    static MessageDigest getMessageDigest(String hashAlgorithm, IExternalDigest externalDigest) throws GeneralSecurityException {
        return externalDigest.getMessageDigest(hashAlgorithm);
    }

    static MessageDigest getMessageDigest(String hashAlgorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (provider == null || provider.startsWith("SunPKCS11") || provider.startsWith("SunMSCAPI")) {
            return MessageDigest.getInstance(DigestAlgorithms.normalizeDigestName(hashAlgorithm));
        } else {
            return MessageDigest.getInstance(hashAlgorithm, provider);
        }
    }

    static InputStream getHttpResponse(URL urlt) throws IOException {
        HttpURLConnection con = (HttpURLConnection)urlt.openConnection();
        if (con.getResponseCode() / 100 != 2) {
            throw new PdfException(PdfException.InvalidHttpResponse1).setMessageParams(con.getResponseCode());
        }
        return (InputStream) con.getContent();
    }

    static CertificateID generateCertificateId(X509Certificate issuerCert, BigInteger serialNumber, AlgorithmIdentifier digestAlgorithmIdentifier) throws OperatorCreationException, CertificateEncodingException, OCSPException {
        return new CertificateID(
                new JcaDigestCalculatorProviderBuilder().build().get(digestAlgorithmIdentifier),
                new JcaX509CertificateHolder(issuerCert), serialNumber);
    }

    static CertificateID generateCertificateId(X509Certificate issuerCert, BigInteger serialNumber, ASN1ObjectIdentifier identifier) throws OperatorCreationException, CertificateEncodingException, OCSPException {
        return new CertificateID(
                new JcaDigestCalculatorProviderBuilder().build().get(new AlgorithmIdentifier(identifier, DERNull.INSTANCE)),
                new JcaX509CertificateHolder(issuerCert), serialNumber);
    }

    static OCSPReq generateOcspRequestWithNonce(CertificateID id) throws IOException, OCSPException {
        OCSPReqBuilder gen = new OCSPReqBuilder();
        gen.addRequest(id);

        Extension ext = new Extension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, false, new DEROctetString(new DEROctetString(PdfEncryption.generateNewDocumentId()).getEncoded()));
        gen.setRequestExtensions(new Extensions(new Extension[]{ext}));
        return gen.build();
    }

    static InputStream getHttpResponseForOcspRequest(byte[] request, URL urlt) throws IOException {
        HttpURLConnection con = (HttpURLConnection) urlt.openConnection();
        con.setRequestProperty("Content-Type", "application/ocsp-request");
        con.setRequestProperty("Accept", "application/ocsp-response");
        con.setDoOutput(true);
        OutputStream out = con.getOutputStream();
        DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
        dataOut.write(request);
        dataOut.flush();
        dataOut.close();
        if (con.getResponseCode() / 100 != 2) {
            throw new PdfException(PdfException.InvalidHttpResponse1).setMessageParams(con.getResponseCode());
        }
        //Get Response
        return (InputStream) con.getContent();
    }

    static boolean isSignatureValid(BasicOCSPResp validator, Certificate certStoreX509, String provider) throws OperatorCreationException, OCSPException {
        if (provider == null) provider = "BC";
        return validator.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider(provider).build(certStoreX509.getPublicKey()));
    }

    static void isSignatureValid(TimeStampToken validator, X509Certificate certStoreX509, String provider) throws OperatorCreationException, TSPException {
        if (provider == null) provider = "BC";
        validator.validate(new JcaSimpleSignerInfoVerifierBuilder().setProvider(provider).build(certStoreX509));
    }

    static boolean checkIfIssuersMatch(CertificateID certID, X509Certificate issuerCert) throws CertificateEncodingException, IOException, OCSPException {
        return certID.matchesIssuer(new X509CertificateHolder(issuerCert.getEncoded()), new BcDigestCalculatorProvider());
    }

    static Date add180Sec(Date date) {
        return new Date(date.getTime() + 180000L);
    }

    static Iterable<X509Certificate> getCertsFromOcspResponse(BasicOCSPResp ocspResp) {
        List<X509Certificate> certs = new ArrayList<>();
        X509CertificateHolder[] certHolders = ocspResp.getCerts();
        JcaX509CertificateConverter converter = new JcaX509CertificateConverter();
        for (X509CertificateHolder certHolder : certHolders) {
            try {
                certs.add(converter.getCertificate(certHolder));
            } catch (Exception ex) {
            }
        }
        return certs;
    }

    static Collection<Certificate> readAllCerts(byte[] contentsKey) throws StreamParsingException {
        X509CertParser cr = new X509CertParser();
        cr.engineInit(new ByteArrayInputStream(contentsKey));
        return cr.engineReadAll();
    }

    static <T> T getFirstElement(Iterable<T> iterable) {
        return iterable.iterator().next();
    }

    static X509Principal getIssuerX509Name(ASN1Sequence issuerAndSerialNumber) throws IOException {
        return new X509Principal(issuerAndSerialNumber.getObjectAt(0).toASN1Primitive().getEncoded());
    }

    public static String dateToString(Calendar signDate) {
        return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z").format(signDate.getTime());
    }

    static class TsaResponse {
        String encoding;
        InputStream tsaResponseStream;
    }

    static TsaResponse getTsaResponseForUserRequest(String tsaUrl, byte[] requestBytes, String tsaUsername, String tsaPassword) throws IOException {
        URL url = new URL(tsaUrl);
        URLConnection tsaConnection;
        try {
            tsaConnection = url.openConnection();
        }
        catch (IOException ioe) {
            throw new PdfException(PdfException.FailedToGetTsaResponseFrom1).setMessageParams(tsaUrl);
        }
        tsaConnection.setDoInput(true);
        tsaConnection.setDoOutput(true);
        tsaConnection.setUseCaches(false);
        tsaConnection.setRequestProperty("Content-Type", "application/timestamp-query");
        //tsaConnection.setRequestProperty("Content-Transfer-Encoding", "base64");
        tsaConnection.setRequestProperty("Content-Transfer-Encoding", "binary");

        if ((tsaUsername != null) && !tsaUsername.equals("") ) {
            String userPassword = tsaUsername + ":" + tsaPassword;
            tsaConnection.setRequestProperty("Authorization", "Basic " +
                    Base64.encodeBytes(userPassword.getBytes(StandardCharsets.UTF_8), Base64.DONT_BREAK_LINES));
        }
        OutputStream out = tsaConnection.getOutputStream();
        out.write(requestBytes);
        out.close();

        TsaResponse response = new TsaResponse();
        response.tsaResponseStream = tsaConnection.getInputStream();
        response.encoding = tsaConnection.getContentEncoding();
        return response;
    }

    /**
     * Check if the provided certificate has a critical extension that iText doesn't support.
     *
     * @param cert X509Certificate instance to check
     * @return true if there are unsupported critical extensions, false if there are none
     */
    static boolean hasUnsupportedCriticalExtension(X509Certificate cert) {
        if ( cert == null ) {
            throw new IllegalArgumentException("X509Certificate can't be null.");
        }

        if (cert.hasUnsupportedCriticalExtension()) {
            for (String oid : cert.getCriticalExtensionOIDs()) {
                // KEY USAGE and DIGITAL SIGNING or NONREPUDIATION is ALLOWED
                if (OID.X509Extensions.KEY_USAGE.equals(oid)) {
                    if(cert.getKeyUsage()[KEY_USAGE_DIGITAL_SIGNATURE] || cert.getKeyUsage()[KEY_USAGE_NON_REPUDIATION]) { // allow digSig and nonRepudiation
                        continue;
                    }
                }

                // BASIC CONSTRAINTS is ALLOWED
                if (OID.X509Extensions.BASIC_CONSTRAINTS.equals(oid)) { // allow basicConstraints, can be checked later
                    continue;
                }

                try {
                    // EXTENDED KEY USAGE and TIMESTAMPING is ALLOWED
                    if (OID.X509Extensions.EXTENDED_KEY_USAGE.equals(oid) && cert.getExtendedKeyUsage().contains(OID.X509Extensions.ID_KP_TIMESTAMPING)) {
                        continue;
                    }
                } catch (CertificateParsingException e) {
                    // DO NOTHING;
                }

                return true;
            }
        }

        return false;
    }

    static Calendar getTimeStampDate(TimeStampToken timeStampToken) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(timeStampToken.getTimeStampInfo().getGenTime());
        return calendar;
    }

    static Signature getSignatureHelper(String algorithm, String provider) throws NoSuchProviderException, NoSuchAlgorithmException {
        return provider == null ? Signature.getInstance(algorithm) : Signature.getInstance(algorithm, provider);
    }

    static boolean verifyCertificateSignature(X509Certificate certificate, PublicKey issuerPublicKey, String provider) {
        boolean res = false;
        try {
            if (provider == null) {
                certificate.verify(issuerPublicKey);
            } else {
                certificate.verify(issuerPublicKey, provider);
            }
            res = true;
        } catch (Exception ignored) {
        }

        return res;
    }

    static SigPolicyQualifiers createSigPolicyQualifiers(SigPolicyQualifierInfo... sigPolicyQualifierInfo) {
        return new SigPolicyQualifiers(sigPolicyQualifierInfo);
    }

    static Iterable<X509Certificate> getCertificates(final KeyStore keyStore) throws KeyStoreException {
        final Enumeration<String> keyStoreAliases = keyStore.aliases();
        return new Iterable<X509Certificate>() {
            @Override
            public Iterator<X509Certificate> iterator() {
                return new Iterator<X509Certificate>() {
                    private X509Certificate nextCert;
                    @Override
                    public boolean hasNext() {
                        if (nextCert == null) {
                            tryToGetNextCertificate();
                        }
                        return nextCert != null;
                    }

                    @Override
                    public X509Certificate next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }
                        X509Certificate cert = nextCert;
                        nextCert = null;
                        return cert;
                    }

                    private void tryToGetNextCertificate() {
                        while (keyStoreAliases.hasMoreElements()) {
                            try {
                                String alias = keyStoreAliases.nextElement();
                                if (keyStore.isCertificateEntry(alias)) {
                                    nextCert = (X509Certificate) keyStore.getCertificate(alias);
                                    break;
                                }
                            } catch (KeyStoreException e) {
                                continue;
                            }
                        }
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("remove");
                    }
                };
            }
        };
    }
}
