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
package com.itextpdf.kernel.crypto.securityhandler;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cms.ICMSEnvelopedData;
import com.itextpdf.commons.bouncycastle.cms.IRecipientInformation;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfEncryptor;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.security.IExternalDecryptionProcess;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

final class EncryptionUtils {
    /*
    256-bit AES, PKCS#5 padding.
    SonarQube doesn't like the fact that we're using unauthenticated modes and bad padding schemes (and it rightly
    calls those out as insecure) but unfortunately this is the best that the standard allows.
    According to ISO 32000-2 (7.6.5.3 Public-key encryption algorithms):
        The algorithms that shall be used to encrypt the enveloped data in the CMS object are:
        • RC4 with key lengths up to 256-bits (deprecated);
        • DES, Triple DES, RC2 with key lengths up to 128 bits (deprecated);
        • 128-bit AES in Cipher Block Chaining (CBC) mode (deprecated);
        • 192-bit AES in CBC mode (deprecated);
        • 256-bit AES in CBC mode.
    */
    private static final String ENVELOPE_ENCRYPTION_ALGORITHM_OID = "2.16.840.1.101.3.4.1.42";
    private static final String ENVELOPE_ENCRYPTION_ALGORITHM_JCA_NAME = "AES/CBC/PKCS5Padding";
    private static final int ENVELOPE_ENCRYPTION_KEY_LENGTH = 256;

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final Set<String> UNSUPPORTED_ALGORITHMS = new HashSet<>();

    static {
        UNSUPPORTED_ALGORITHMS.add("1.2.840.10045.2.1");
    }

    static byte[] generateSeed(int seedLength) {
        byte[] seedBytes;
        KeyGenerator key;
        try {
            key = KeyGenerator.getInstance("AES");
            key.init(192, new SecureRandom());
            SecretKey sk = key.generateKey();
            seedBytes = new byte[seedLength];
            // create the 20 bytes seed
            System.arraycopy(sk.getEncoded(), 0, seedBytes, 0, seedLength);
        } catch (NoSuchAlgorithmException e) {
            seedBytes = SecureRandom.getSeed(seedLength);
        }
        return seedBytes;
    }

    static byte[] fetchEnvelopedData(Key certificateKey, Certificate certificate, String certificateKeyProvider,
            IExternalDecryptionProcess externalDecryptionProcess, PdfArray recipients) {
        boolean foundRecipient = false;
        byte[] envelopedData = null;

        IX509CertificateHolder certHolder;
        try {
            certHolder = BOUNCY_CASTLE_FACTORY.createX509CertificateHolder(certificate.getEncoded());
        } catch (Exception e) {
            throw new PdfException(KernelExceptionMessageConstant.PDF_DECRYPTION, e);
        }
        if (externalDecryptionProcess == null) {
            for (int i = 0; i < recipients.size(); i++) {
                PdfString recipient = recipients.getAsString(i);
                ICMSEnvelopedData data;
                try {
                    data = BOUNCY_CASTLE_FACTORY.createCMSEnvelopedData(recipient.getValueBytes());
                    for (IRecipientInformation recipientInfo : data.getRecipientInfos().getRecipients()) {
                        if (recipientInfo.getRID().match(certHolder) && !foundRecipient) {
                            envelopedData = PdfEncryptor.getContent(recipientInfo, (PrivateKey) certificateKey,
                                    certificateKeyProvider);
                            foundRecipient = true;
                        }
                    }
                } catch (Exception e) {
                    // First check if the feature is supported, it will throw if not
                    // Exact algorithm doesn't matter currently
                    BouncyCastleFactoryCreator.getFactory().isEncryptionFeatureSupported(0, true);
                    // Throw the original exception if the feature is supported
                    throw new PdfException(KernelExceptionMessageConstant.PDF_DECRYPTION, e);
                }
            }
        } else {
            for (int i = 0; i < recipients.size(); i++) {
                PdfString recipient = recipients.getAsString(i);
                ICMSEnvelopedData data;
                try {
                    data = BOUNCY_CASTLE_FACTORY.createCMSEnvelopedData(recipient.getValueBytes());
                    IRecipientInformation recipientInfo = data.getRecipientInfos()
                            .get(externalDecryptionProcess.getCmsRecipientId());
                    if (recipientInfo != null) {
                        envelopedData = recipientInfo.getContent(externalDecryptionProcess.getCmsRecipient());
                        foundRecipient = true;
                    }
                } catch (Exception f) {
                    throw new PdfException(KernelExceptionMessageConstant.PDF_DECRYPTION, f);
                }
            }
        }

        if (!foundRecipient || envelopedData == null) {
            throw new PdfException(KernelExceptionMessageConstant.BAD_CERTIFICATE_AND_KEY);
        }
        return envelopedData;
    }

    static byte[] cipherBytes(X509Certificate x509certificate, byte[] abyte0, IAlgorithmIdentifier algorithmIdentifier)
            throws GeneralSecurityException {
        String algorithm = algorithmIdentifier.getAlgorithm().getId();
        if (UNSUPPORTED_ALGORITHMS.contains(algorithm)) {
            throw new PdfException(MessageFormatUtil.format(
                    KernelExceptionMessageConstant.ALGORITHM_IS_NOT_SUPPORTED, algorithm));
        }
        return BOUNCY_CASTLE_FACTORY.createCipherBytes(x509certificate, abyte0, algorithmIdentifier);
    }

    static DERForRecipientParams calculateDERForRecipientParams(byte[] in)
            throws IOException, GeneralSecurityException {
        DERForRecipientParams parameters = new DERForRecipientParams();

        AlgorithmParameterGenerator algorithmparametergenerator = AlgorithmParameterGenerator.getInstance(
                ENVELOPE_ENCRYPTION_ALGORITHM_OID);
        AlgorithmParameters algorithmparameters = algorithmparametergenerator.generateParameters();
        ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(algorithmparameters.getEncoded("ASN.1"));
        IASN1Primitive derobject;
        try (IASN1InputStream asn1inputstream = BOUNCY_CASTLE_FACTORY.createASN1InputStream(bytearrayinputstream)) {
            derobject = asn1inputstream.readObject();
        }

        KeyGenerator keygenerator;
        if ("BC".equals(BOUNCY_CASTLE_FACTORY.getProviderName())) {
            // Do not pass bc provider and use default one here not to require bc provider for this functionality
            // Do not use bc provider in kernel
            keygenerator = KeyGenerator.getInstance(ENVELOPE_ENCRYPTION_ALGORITHM_OID);
        } else {
            keygenerator = KeyGenerator.getInstance(ENVELOPE_ENCRYPTION_ALGORITHM_OID,
                    BOUNCY_CASTLE_FACTORY.getProvider());
        }
        keygenerator.init(ENVELOPE_ENCRYPTION_KEY_LENGTH, BOUNCY_CASTLE_FACTORY.getSecureRandom());
        SecretKey secretkey = keygenerator.generateKey();

        Cipher cipher;
        if ("BC".equals(BOUNCY_CASTLE_FACTORY.getProviderName())) {
            // Do not pass bc provider and use default one here not to require bc provider for this functionality
            // Do not use bc provider in kernel
            cipher = Cipher.getInstance(ENVELOPE_ENCRYPTION_ALGORITHM_JCA_NAME);
        } else {
            cipher = Cipher.getInstance(ENVELOPE_ENCRYPTION_ALGORITHM_JCA_NAME, BOUNCY_CASTLE_FACTORY.getProvider());
        }
        cipher.init(Cipher.ENCRYPT_MODE, secretkey, algorithmparameters);

        parameters.abyte0 = secretkey.getEncoded();
        parameters.abyte1 = cipher.doFinal(in);
        parameters.algorithmIdentifier = BOUNCY_CASTLE_FACTORY.createAlgorithmIdentifier(
                BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(ENVELOPE_ENCRYPTION_ALGORITHM_OID), derobject);

        return parameters;
    }

    static class DERForRecipientParams {
        byte[] abyte0;
        byte[] abyte1;
        IAlgorithmIdentifier algorithmIdentifier;
    }
}
