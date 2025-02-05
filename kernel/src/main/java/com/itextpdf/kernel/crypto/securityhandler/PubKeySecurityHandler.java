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
package com.itextpdf.kernel.crypto.securityhandler;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OutputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;
import com.itextpdf.commons.bouncycastle.asn1.IDERSet;
import com.itextpdf.commons.bouncycastle.asn1.cms.IContentInfo;
import com.itextpdf.commons.bouncycastle.asn1.cms.IEncryptedContentInfo;
import com.itextpdf.commons.bouncycastle.asn1.cms.IEnvelopedData;
import com.itextpdf.commons.bouncycastle.asn1.cms.IIssuerAndSerialNumber;
import com.itextpdf.commons.bouncycastle.asn1.cms.IKeyTransRecipientInfo;
import com.itextpdf.commons.bouncycastle.asn1.cms.IRecipientIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.ITBSCertificate;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.crypto.CryptoUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfLiteral;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.security.IExternalDecryptionProcess;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public abstract class PubKeySecurityHandler extends SecurityHandler {

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final int SEED_LENGTH = 20;
    private static final int DEFAULT_KEY_LENGTH = 40;

    private List<PublicKeyRecipient> recipients = null;

    private byte[] seed;

    protected PubKeySecurityHandler() {
        seed = EncryptionUtils.generateSeed(SEED_LENGTH);
        recipients = new ArrayList<>();
    }

    protected byte[] computeGlobalKey(String messageDigestAlgorithm, boolean encryptMetadata) {
        MessageDigest md;
        byte[] encodedRecipient;

        try {
            md = MessageDigest.getInstance(messageDigestAlgorithm);
            md.update(getSeed());
            for (int i = 0; i < getRecipientsSize(); i++) {
                encodedRecipient = getEncodedRecipient(i);
                md.update(encodedRecipient);
            }
            if (!encryptMetadata) {
                md.update(new byte[] {(byte) 255, (byte) 255, (byte) 255,
                        (byte) 255});
            }
        } catch (PdfException pdfException) {
            throw pdfException;
        } catch (Exception e) {
            throw new PdfException(KernelExceptionMessageConstant.PDF_ENCRYPTION, e);
        }

        return md.digest();
    }

    protected static byte[] computeGlobalKeyOnReading(PdfDictionary encryptionDictionary, PrivateKey certificateKey,
            Certificate certificate, String certificateKeyProvider,
            IExternalDecryptionProcess externalDecryptionProcess,
            boolean encryptMetadata, String digestAlgorithm) {
        PdfArray recipients = encryptionDictionary.getAsArray(PdfName.Recipients);
        if (recipients == null) {
            recipients = encryptionDictionary.getAsDictionary(PdfName.CF)
                    .getAsDictionary(PdfName.DefaultCryptFilter)
                    .getAsArray(PdfName.Recipients);
        }

        byte[] envelopedData = EncryptionUtils.fetchEnvelopedData(certificateKey, certificate, certificateKeyProvider,
                externalDecryptionProcess, recipients);

        byte[] encryptionKey;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(digestAlgorithm);
            md.update(envelopedData, 0, 20);
            for (int i = 0; i < recipients.size(); i++) {
                byte[] encodedRecipient = recipients.getAsString(i).getValueBytes();
                md.update(encodedRecipient);
            }
            if (!encryptMetadata) {
                md.update(new byte[] {(byte) 255, (byte) 255, (byte) 255, (byte) 255});
            }
            encryptionKey = md.digest();
        } catch (Exception f) {
            throw new PdfException(KernelExceptionMessageConstant.PDF_DECRYPTION, f);
        }
        return encryptionKey;
    }

    protected void addAllRecipients(Certificate[] certs, int[] permissions) {
        if (certs != null) {
            for (int i = 0; i < certs.length; i++) {
                addRecipient(certs[i], permissions[i]);
            }
        }
    }

    protected PdfArray createRecipientsArray() {
        PdfArray recipients;
        try {
            recipients = getEncodedRecipients();
        } catch (Exception e) {
            throw new PdfException(KernelExceptionMessageConstant.PDF_ENCRYPTION, e);
        }
        return recipients;
    }

    protected abstract void setPubSecSpecificHandlerDicEntries(PdfDictionary encryptionDictionary,
            boolean encryptMetadata, boolean embeddedFilesOnly);

    protected abstract String getDigestAlgorithm();

    protected abstract void initKey(byte[] globalKey, int keyLength);

    protected void initKeyAndFillDictionary(PdfDictionary encryptionDictionary, Certificate[] certs, int[] permissions,
            boolean encryptMetadata, boolean embeddedFilesOnly) {
        addAllRecipients(certs, permissions);

        int keyLength = getKeyLength(encryptionDictionary);

        String digestAlgorithm = getDigestAlgorithm();
        byte[] digest = computeGlobalKey(digestAlgorithm, encryptMetadata);
        initKey(digest, keyLength);

        setPubSecSpecificHandlerDicEntries(encryptionDictionary, encryptMetadata, embeddedFilesOnly);
    }

    protected void initKeyAndReadDictionary(PdfDictionary encryptionDictionary, Key certificateKey,
            Certificate certificate,
            String certificateKeyProvider, IExternalDecryptionProcess externalDecryptionProcess,
            boolean encryptMetadata) {
        String digestAlgorithm = getDigestAlgorithm();
        byte[] encryptionKey = computeGlobalKeyOnReading(encryptionDictionary, (PrivateKey) certificateKey, certificate,
                certificateKeyProvider, externalDecryptionProcess, encryptMetadata, digestAlgorithm);

        int keyLength = getKeyLength(encryptionDictionary);
        initKey(encryptionKey, keyLength);
    }


    private void addRecipient(Certificate cert, int permission) {
        recipients.add(new PublicKeyRecipient(cert, permission));
    }

    private byte[] getSeed() {
        byte[] clonedSeed = new byte[seed.length];
        System.arraycopy(seed, 0, clonedSeed, 0, seed.length);
        return clonedSeed;
    }

    private int getRecipientsSize() {
        return recipients.size();
    }

    private byte[] getEncodedRecipient(int index) throws IOException, GeneralSecurityException {
        //Certificate certificate = recipient.getX509();
        PublicKeyRecipient recipient = recipients.get(index);
        byte[] cms = recipient.getCms();

        if (cms != null) {
            return cms;
        }

        Certificate certificate = recipient.getCertificate();
        //constants permissions: PdfWriter.AllowCopy | PdfWriter.AllowPrinting | PdfWriter.AllowScreenReaders |
        // PdfWriter.AllowAssembly;
        int permission = recipient.getPermission();
        // Force set 1 to 1, 7, 8 bits and all bits above 13.
        // Basically to all not used bits.
        // Bit 13 we do not touch. It's handled separately in PdfEncryption.
        // Not sure about bit 1. But we always set it to 1 so let's not change for now.
        permission |= 0xffffe0c1;

        byte[] pkcs7input = new byte[24];

        byte one = (byte) permission;
        byte two = (byte) (permission >> 8);
        byte three = (byte) (permission >> 16);
        byte four = (byte) (permission >> 24);

        // put this seed in the pkcs7 input
        System.arraycopy(seed, 0, pkcs7input, 0, 20);

        pkcs7input[20] = four;
        pkcs7input[21] = three;
        pkcs7input[22] = two;
        pkcs7input[23] = one;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (IASN1OutputStream k =
                CryptoUtil.createAsn1OutputStream(baos, BOUNCY_CASTLE_FACTORY.createASN1Encoding().getDer())) {
            IASN1Primitive obj = createDERForRecipient(pkcs7input, (X509Certificate) certificate);
            k.writeObject(obj);
        }
        cms = baos.toByteArray();
        recipient.setCms(cms);

        return cms;
    }

    private PdfArray getEncodedRecipients() {
        PdfArray EncodedRecipients = new PdfArray();
        byte[] cms;
        for (int i = 0; i < recipients.size(); i++) {
            try {
                cms = getEncodedRecipient(i);
                EncodedRecipients.add(new PdfLiteral(StreamUtil.createEscapedString(cms)));
            } catch (GeneralSecurityException | IOException e) {
                EncodedRecipients = null;
                // break was added while porting to itext
                break;
            }
        }

        return EncodedRecipients;
    }

    private IASN1Primitive createDERForRecipient(byte[] in, X509Certificate cert)
            throws IOException, GeneralSecurityException {
        EncryptionUtils.DERForRecipientParams parameters = EncryptionUtils.calculateDERForRecipientParams(in);

        IKeyTransRecipientInfo keytransrecipientinfo = computeRecipientInfo(cert, parameters.abyte0);
        IDEROctetString deroctetstring = BOUNCY_CASTLE_FACTORY.createDEROctetString(parameters.abyte1);
        IDERSet derset = BOUNCY_CASTLE_FACTORY.createDERSet(
                BOUNCY_CASTLE_FACTORY.createRecipientInfo(keytransrecipientinfo));
        IEncryptedContentInfo encryptedcontentinfo =
                BOUNCY_CASTLE_FACTORY.createEncryptedContentInfo(
                        BOUNCY_CASTLE_FACTORY.createPKCSObjectIdentifiers().getData(), parameters.algorithmIdentifier,
                        deroctetstring);
        IEnvelopedData env = BOUNCY_CASTLE_FACTORY.createEnvelopedData(BOUNCY_CASTLE_FACTORY.createNullOriginatorInfo(),
                derset, encryptedcontentinfo, BOUNCY_CASTLE_FACTORY.createNullASN1Set());
        IContentInfo contentinfo = BOUNCY_CASTLE_FACTORY.createContentInfo(
                BOUNCY_CASTLE_FACTORY.createPKCSObjectIdentifiers().getEnvelopedData(), env);
        return contentinfo.toASN1Primitive();
    }

    private IKeyTransRecipientInfo computeRecipientInfo(X509Certificate x509Certificate, byte[] abyte0)
            throws GeneralSecurityException, IOException {
        ITBSCertificate tbsCertificate;
        try (IASN1InputStream asn1InputStream = BOUNCY_CASTLE_FACTORY.createASN1InputStream(
                new ByteArrayInputStream(x509Certificate.getTBSCertificate()))) {
            tbsCertificate = BOUNCY_CASTLE_FACTORY.createTBSCertificate(asn1InputStream.readObject());
        }
        IAlgorithmIdentifier algorithmIdentifier = tbsCertificate.getSubjectPublicKeyInfo().getAlgorithm();
        IIssuerAndSerialNumber issuerAndSerialNumber = BOUNCY_CASTLE_FACTORY.createIssuerAndSerialNumber(
                tbsCertificate.getIssuer(),
                tbsCertificate.getSerialNumber().getValue());
        byte[] cipheredBytes = EncryptionUtils.cipherBytes(x509Certificate, abyte0, algorithmIdentifier);
        IDEROctetString derOctetString = BOUNCY_CASTLE_FACTORY.createDEROctetString(cipheredBytes);
        IRecipientIdentifier recipId = BOUNCY_CASTLE_FACTORY.createRecipientIdentifier(issuerAndSerialNumber);
        return BOUNCY_CASTLE_FACTORY.createKeyTransRecipientInfo(recipId, algorithmIdentifier, derOctetString);
    }

    private int getKeyLength(PdfDictionary encryptionDict) {
        Integer keyLength = encryptionDict.getAsInt(PdfName.Length);
        return keyLength != null ? (int) keyLength : DEFAULT_KEY_LENGTH;
    }
}
