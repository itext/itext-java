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
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implementation class for {@link IExternalSignatureContainer}.
 * This external signature container is implemented based on PCS7 standard and {@link PdfPKCS7} class.
 */
public class PKCS7ExternalSignatureContainer implements IExternalSignatureContainer {

    private final Certificate[] chain;
    private final PrivateKey privateKey;
    private final String hashAlgorithm;
    private IOcspClient ocspClient;
    private ICrlClient crlClient;
    private ITSAClient tsaClient;
    private PdfSigner.CryptoStandard sigType = PdfSigner.CryptoStandard.CMS;
    private SignaturePolicyInfo signaturePolicy;

    /**
     * Creates an instance of PKCS7ExternalSignatureContainer
     *
     * @param privateKey    The private key to sign with
     * @param chain         The certificate chain
     * @param hashAlgorithm The hash algorithm to use
     */
    public PKCS7ExternalSignatureContainer(PrivateKey privateKey, Certificate[] chain, String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
        this.chain = chain;
        this.privateKey = privateKey;
    }

    /**
     * {@inheritDoc}
     *
     * @param data {@inheritDoc}
     *
     * @return {@inheritDoc}
     *
     * @throws GeneralSecurityException {@inheritDoc}
     */
    @Override
    public byte[] sign(InputStream data) throws GeneralSecurityException {
        PdfPKCS7 sgn = new PdfPKCS7((PrivateKey) null, chain, hashAlgorithm, null, new BouncyCastleDigest(), false);
        if (signaturePolicy != null) {
            sgn.setSignaturePolicy(signaturePolicy);
        }
        byte[] hash;
        try {
            hash = DigestAlgorithms.digest(data, SignUtils.getMessageDigest(hashAlgorithm));
        } catch (IOException e) {
            throw new PdfException(e);
        }

        Collection<byte[]> crlBytes = null;
        int i = 0;
        while (crlClient != null && crlBytes == null && i < chain.length) {
            crlBytes = crlClient.getEncoded((X509Certificate) chain[i++], null);
        }

        List<byte[]> ocspList = new ArrayList<>();
        if (chain.length > 1 && ocspClient != null) {
            for (int j = 0; j < chain.length - 1; ++j) {
                byte[] ocsp = ocspClient.getEncoded((X509Certificate) chain[j], (X509Certificate) chain[j + 1], null);
                if (ocsp != null && BouncyCastleFactoryCreator.getFactory().createCertificateStatus().getGood().equals(
                        OcspClientBouncyCastle.getCertificateStatus(ocsp))) {
                    ocspList.add(ocsp);
                }
            }
        }
        byte[] sh = sgn.getAuthenticatedAttributeBytes(hash, sigType, ocspList, crlBytes);

        PrivateKeySignature pkSign = new PrivateKeySignature(privateKey, hashAlgorithm,
                BouncyCastleFactoryCreator.getFactory().getProviderName());
        byte[] signData = pkSign.sign(sh);

        sgn.setExternalSignatureValue(
                signData,
                null,
                pkSign.getSignatureAlgorithmName(),
                pkSign.getSignatureMechanismParameters()
        );

        return sgn.getEncodedPKCS7(hash, sigType, tsaClient, ocspList, crlBytes);
    }

    /**
     * {@inheritDoc}
     *
     * @param signDic {@inheritDoc}
     */
    @Override
    public void modifySigningDictionary(PdfDictionary signDic) {
        signDic.put(PdfName.Filter, PdfName.Adobe_PPKLite);
        signDic.put(PdfName.SubFilter, sigType == PdfSigner.CryptoStandard.CADES
                ? PdfName.ETSI_CAdES_DETACHED
                : PdfName.Adbe_pkcs7_detached);
    }

    /**
     * Set the OcspClient if you want revocation data collected trough Ocsp to be added to the signature
     *
     * @param ocspClient the client to be used
     */
    public void setOcspClient(IOcspClient ocspClient) {
        this.ocspClient = ocspClient;
    }

    /**
     * Set the CrlClient if you want revocation data collected trough Crl to be added to the signature
     *
     * @param crlClient the client to be used
     */
    public void setCrlClient(ICrlClient crlClient) {
        this.crlClient = crlClient;
    }

    /**
     * Set the TsaClient if you want a TSA timestamp added to the signature
     *
     * @param tsaClient the client to use
     */
    public void setTsaClient(ITSAClient tsaClient) {
        this.tsaClient = tsaClient;
    }

    /**
     * Set the signature policy if you want it to be added to the signature
     *
     * @param signaturePolicy the signature to be set.
     */
    public void setSignaturePolicy(SignaturePolicyInfo signaturePolicy) {
        this.signaturePolicy = signaturePolicy;
    }

    /**
     * Set a custom signature type, default value {@link PdfSigner.CryptoStandard#CMS}
     *
     * @param sigType the type  of signature to be created
     */
    public void setSignatureType(PdfSigner.CryptoStandard sigType) {
        this.sigType = sigType;
    }
}
