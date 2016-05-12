package com.itextpdf.kernel.crypto.securityhandler;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfEncryptor;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.security.IExternalDecryptionProcess;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.RecipientInformation;

class EncryptionUtils {

    static byte[] generateSeed(int seedLength) {
        byte[] seedBytes;
        KeyGenerator key;
        try {
            key = KeyGenerator.getInstance("AES");
            key.init(192, new SecureRandom());
            SecretKey sk = key.generateKey();
            seedBytes = new byte[seedLength];
            System.arraycopy(sk.getEncoded(), 0, seedBytes, 0, seedLength); // create the 20 bytes seed
        } catch (NoSuchAlgorithmException e) {
            seedBytes = SecureRandom.getSeed(seedLength);
        }
        return seedBytes;
    }

    static byte[] fetchEnvelopedData(Key certificateKey, Certificate certificate, String certificateKeyProvider,
                                     IExternalDecryptionProcess externalDecryptionProcess, PdfArray recipients) {
        boolean foundRecipient = false;
        byte[] envelopedData = null;

        X509CertificateHolder certHolder;
        try {
            certHolder = new X509CertificateHolder(certificate.getEncoded());
        } catch (Exception f) {
            throw new PdfException(PdfException.PdfDecryption, f);
        }
        if (externalDecryptionProcess == null) {
            for (int i = 0; i < recipients.size(); i++) {
                PdfString recipient = recipients.getAsString(i);
                CMSEnvelopedData data;
                try {
                    data = new CMSEnvelopedData(recipient.getValueBytes());
                    Iterator<RecipientInformation> recipientCertificatesIt = data.getRecipientInfos().getRecipients().iterator();
                    while (recipientCertificatesIt.hasNext()) {
                        RecipientInformation recipientInfo = recipientCertificatesIt.next();

                        if (recipientInfo.getRID().match(certHolder) && !foundRecipient) {
                            envelopedData = PdfEncryptor.getContent(recipientInfo, (PrivateKey) certificateKey, certificateKeyProvider);
                            foundRecipient = true;
                        }
                    }
                } catch (Exception f) {
                    throw new PdfException(PdfException.PdfDecryption, f);
                }
            }
        } else {
            for (int i = 0; i < recipients.size(); i++) {
                PdfString recipient = recipients.getAsString(i);
                CMSEnvelopedData data;
                try {
                    data = new CMSEnvelopedData(recipient.getValueBytes());
                    RecipientInformation recipientInfo = data.getRecipientInfos().get(externalDecryptionProcess.getCmsRecipientId());
                    if (recipientInfo != null) {
                        envelopedData = recipientInfo.getContent(externalDecryptionProcess.getCmsRecipient());
                        foundRecipient = true;
                    }
                } catch (Exception f) {
                    throw new PdfException(PdfException.PdfDecryption, f);
                }
            }
        }

        if (!foundRecipient || envelopedData == null) {
            throw new PdfException(PdfException.BadCertificateAndKey);
        }
        return envelopedData;
    }

    static byte[] cipherBytes(X509Certificate x509certificate, byte[] abyte0, AlgorithmIdentifier algorithmidentifier)
            throws GeneralSecurityException, IOException {
        Cipher cipher = Cipher.getInstance(algorithmidentifier.getAlgorithm().getId());
        try {
            cipher.init(1, x509certificate);
        } catch (InvalidKeyException e) {
            cipher.init(1, x509certificate.getPublicKey());
        }
        return cipher.doFinal(abyte0);
    }

    static DERForRecipientParams calculateDERForRecipientParams(byte[] in) throws IOException, GeneralSecurityException {
        String s = "1.2.840.113549.3.2";
        DERForRecipientParams parameters = new DERForRecipientParams();

        AlgorithmParameterGenerator algorithmparametergenerator = AlgorithmParameterGenerator.getInstance(s);
        AlgorithmParameters algorithmparameters = algorithmparametergenerator.generateParameters();
        ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(algorithmparameters.getEncoded("ASN.1"));
        ASN1InputStream asn1inputstream = new ASN1InputStream(bytearrayinputstream);
        ASN1Primitive derobject = asn1inputstream.readObject();
        KeyGenerator keygenerator = KeyGenerator.getInstance(s);
        keygenerator.init(128);
        SecretKey secretkey = keygenerator.generateKey();
        Cipher cipher = Cipher.getInstance(s);
        cipher.init(1, secretkey, algorithmparameters);

        parameters.abyte0 = secretkey.getEncoded();
        parameters.abyte1 = cipher.doFinal(in);
        parameters.algorithmIdentifier = new AlgorithmIdentifier(new ASN1ObjectIdentifier(s), derobject);

        return parameters;
    }

    static class DERForRecipientParams {
        byte[] abyte0;
        byte[] abyte1;
        AlgorithmIdentifier algorithmIdentifier;
    }
}
