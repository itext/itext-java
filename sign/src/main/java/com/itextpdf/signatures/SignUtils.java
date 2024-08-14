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
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;
import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;
import com.itextpdf.commons.bouncycastle.asn1.tsp.ITSTInfo;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509CertificateConverter;
import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReq;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReqBuilder;
import com.itextpdf.commons.bouncycastle.cms.ISignerInformationVerifier;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.tsp.AbstractTSPException;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampToken;
import com.itextpdf.commons.utils.Base64;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfEncryption;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;

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
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.cert.X509CRL;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.security.auth.x500.X500Principal;

final class SignUtils {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

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

    static byte[] getExtensionValueByOid(CRL crl, String oid) {
        return ((X509CRL) crl).getExtensionValue(oid);
    }

    static MessageDigest getMessageDigest(String hashAlgorithm) throws GeneralSecurityException {
        return new BouncyCastleDigest().getMessageDigest(hashAlgorithm);
    }

    static MessageDigest getMessageDigest(String hashAlgorithm, IExternalDigest externalDigest) throws GeneralSecurityException {
        return externalDigest.getMessageDigest(hashAlgorithm);
    }

    static MessageDigest getMessageDigest(String hashAlgorithm, String provider)
            throws NoSuchAlgorithmException, NoSuchProviderException {
        if (provider == null || provider.startsWith("SunPKCS11") || provider.startsWith("SunMSCAPI")) {
            return MessageDigest.getInstance(DigestAlgorithms.normalizeDigestName(hashAlgorithm));
        } else {
            return MessageDigest.getInstance(hashAlgorithm, provider);
        }
    }

    static InputStream getHttpResponse(URL urlt) throws IOException {
        HttpURLConnection con = (HttpURLConnection) urlt.openConnection();
        if (con.getResponseCode() / 100 != 2) {
            throw new PdfException(SignExceptionMessageConstant.INVALID_HTTP_RESPONSE)
                    .setMessageParams(con.getResponseCode());
        }
        return (InputStream) con.getContent();
    }

    static ICertificateID generateCertificateId(X509Certificate issuerCert, BigInteger serialNumber,
            IAlgorithmIdentifier digestAlgorithmIdentifier)
            throws AbstractOperatorCreationException, CertificateEncodingException, AbstractOCSPException {
        return FACTORY.createCertificateID(
                FACTORY.createJcaDigestCalculatorProviderBuilder().build().get(digestAlgorithmIdentifier),
                FACTORY.createJcaX509CertificateHolder(issuerCert),
                serialNumber);
    }

    static ICertificateID generateCertificateId(X509Certificate issuerCert, BigInteger serialNumber,
            IASN1ObjectIdentifier identifier)
            throws AbstractOperatorCreationException, CertificateEncodingException, AbstractOCSPException {
        return FACTORY.createCertificateID(
                FACTORY.createJcaDigestCalculatorProviderBuilder().build().get(
                        FACTORY.createAlgorithmIdentifier(identifier, FACTORY.createDERNull())),
                FACTORY.createJcaX509CertificateHolder(issuerCert), serialNumber);
    }

    static IOCSPReq generateOcspRequestWithNonce(ICertificateID id) throws IOException, AbstractOCSPException {
        IOCSPReqBuilder gen = FACTORY.createOCSPReqBuilder();
        gen.addRequest(id);

        IDEROctetString derOctetString = FACTORY.createDEROctetString(
                FACTORY.createDEROctetString(PdfEncryption.generateNewDocumentId()).getEncoded());
        IExtension ext = FACTORY.createExtension(
                FACTORY.createOCSPObjectIdentifiers().getIdPkixOcspNonce(), false, derOctetString);
        gen.setRequestExtensions(FACTORY.createExtensions(ext));
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
            throw new PdfException(SignExceptionMessageConstant.INVALID_HTTP_RESPONSE)
                    .setMessageParams(con.getResponseCode());
        }
        //Get Response
        return (InputStream) con.getContent();
    }

    static boolean isSignatureValid(IBasicOCSPResp validator, Certificate certStoreX509, String provider)
            throws AbstractOperatorCreationException, AbstractOCSPException {
        if (provider == null) {
            provider = FACTORY.getProviderName();
        }
        return validator.isSignatureValid(FACTORY.createJcaContentVerifierProviderBuilder()
                .setProvider(provider).build(certStoreX509.getPublicKey()));
    }

    static void isSignatureValid(ITimeStampToken validator, X509Certificate certStoreX509, String provider)
            throws AbstractOperatorCreationException, AbstractTSPException {
        if (provider == null) {
            provider = FACTORY.getProviderName();
        }
        ISignerInformationVerifier verifier = FACTORY.createJcaSimpleSignerInfoVerifierBuilder().setProvider(provider)
                .build(certStoreX509);
        validator.validate(verifier);
    }

    static boolean checkIfIssuersMatch(ICertificateID certID, X509Certificate issuerCert)
            throws CertificateEncodingException, IOException, AbstractOCSPException, AbstractOperatorCreationException {
        return certID.matchesIssuer(
                FACTORY.createX509CertificateHolder(issuerCert.getEncoded()),
                FACTORY.createJcaDigestCalculatorProviderBuilder().build());
    }

    static Date add180Sec(Date date) {
        return new Date(date.getTime() + 180000L);
    }

    static Iterable<X509Certificate> getCertsFromOcspResponse(IBasicOCSPResp ocspResp) {
        List<X509Certificate> certs = new ArrayList<>();
        IX509CertificateHolder[] certHolders = ocspResp.getCerts();
        IJcaX509CertificateConverter converter = FACTORY.createJcaX509CertificateConverter();
        for (IX509CertificateHolder certHolder : certHolders) {
            try {
                certs.add(converter.getCertificate(certHolder));
            } catch (Exception ex) {
                // do nothing
            }
        }
        return certs;
    }

    static Collection<Certificate> readAllCerts(byte[] contentsKey) throws CertificateException {
        return SignUtils.readAllCerts(new ByteArrayInputStream(contentsKey), FACTORY.getProvider());
    }

    static Collection<Certificate> readAllCerts(InputStream contentsKey, Provider provider)
            throws CertificateException {
        final CertificateFactory factory = provider == null ? CertificateFactory.getInstance("X509") :
                CertificateFactory.getInstance("X509", provider);
        return new ArrayList<>(factory.generateCertificates(contentsKey));
    }

    static Certificate generateCertificate(InputStream data, Provider provider) throws CertificateException {
        final CertificateFactory factory = provider == null ? CertificateFactory.getInstance("X509") :
                CertificateFactory.getInstance("X509", provider);
        return factory.generateCertificate(data);
    }

    static Collection<CRL> readAllCRLs(byte[] contentsKey) throws CertificateException, CRLException {
        final CertificateFactory factory = CertificateFactory.getInstance("X509", FACTORY.getProvider());
        return new ArrayList<>(factory.generateCRLs(new ByteArrayInputStream(contentsKey)));
    }

    static <T> T getFirstElement(Iterable<T> iterable) {
        return iterable.iterator().next();
    }

    static X500Principal getIssuerX500Principal(IASN1Sequence issuerAndSerialNumber) throws IOException {
        return new X500Principal(issuerAndSerialNumber.getObjectAt(0).toASN1Primitive().getEncoded());
    }

    static class TsaResponse {
        String encoding;
        InputStream tsaResponseStream;
    }

    static TsaResponse getTsaResponseForUserRequest(String tsaUrl, byte[] requestBytes, String tsaUsername,
            String tsaPassword) throws IOException {
        URL url = new URL(tsaUrl);
        URLConnection tsaConnection;
        try {
            tsaConnection = url.openConnection();
        } catch (IOException ioe) {
            throw new PdfException(SignExceptionMessageConstant.FAILED_TO_GET_TSA_RESPONSE).setMessageParams(tsaUrl);
        }
        tsaConnection.setDoInput(true);
        tsaConnection.setDoOutput(true);
        tsaConnection.setUseCaches(false);
        tsaConnection.setRequestProperty("Content-Type", "application/timestamp-query");
        //tsaConnection.setRequestProperty("Content-Transfer-Encoding", "base64");
        tsaConnection.setRequestProperty("Content-Transfer-Encoding", "binary");

        if ((tsaUsername != null) && !tsaUsername.equals("")) {
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

    static Calendar getTimeStampDate(ITSTInfo timeStampTokenInfo) {
        GregorianCalendar calendar = new GregorianCalendar();
        try {
            calendar.setTime(timeStampTokenInfo.getGenTime());
        } catch (ParseException ignored) {
            // Do nothing.
        }
        return calendar;
    }

    static Signature getSignatureHelper(String algorithm, String provider)
            throws NoSuchProviderException, NoSuchAlgorithmException {
        return provider == null
                ? Signature.getInstance(algorithm)
                : Signature.getInstance(algorithm, provider);
    }

    static void setRSASSAPSSParamsWithMGF1(Signature signature, String digestAlgoName, int saltLen, int trailerField)
            throws InvalidAlgorithmParameterException {
        MGF1ParameterSpec mgf1Spec = new MGF1ParameterSpec(digestAlgoName);
        PSSParameterSpec spec = new PSSParameterSpec(digestAlgoName, "MGF1", mgf1Spec, saltLen, trailerField);
        signature.setParameter(spec);
    }

    public static void updateVerifier(Signature signature, byte[] attr) throws SignatureException {
        signature.update(attr);
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
                                if (keyStore.isCertificateEntry(alias) || keyStore.isKeyEntry(alias)) {
                                    nextCert = (X509Certificate) keyStore.getCertificate(alias);
                                    break;
                                }
                            } catch (KeyStoreException e) {
                                // do nothing and continue
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
