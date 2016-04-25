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
package com.itextpdf.kernel.crypto.securityhandler;

import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfEncryptor;
import com.itextpdf.kernel.pdf.PdfLiteral;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.security.IExternalDecryptionProcess;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientIdentifier;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.TBSCertificateStructure;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.RecipientInformation;

/**
 * @author Aiken Sam (aikensam@ieee.org)
 */
public abstract class PubKeySecurityHandler extends SecurityHandler {

    private static final int SEED_LENGTH = 20;

    private List<PublicKeyRecipient> recipients = null;

    private byte[] seed = new byte[SEED_LENGTH];

    public PubKeySecurityHandler() {
        KeyGenerator key;
        try {
            key = KeyGenerator.getInstance("AES");
            key.init(192, new SecureRandom());
            SecretKey sk = key.generateKey();
            System.arraycopy(sk.getEncoded(), 0, seed, 0, SEED_LENGTH); // create the 20 bytes seed
        } catch (NoSuchAlgorithmException e) {
            seed = SecureRandom.getSeed(SEED_LENGTH);
        }

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
            if (!encryptMetadata)
                md.update(new byte[]{(byte) 255, (byte) 255, (byte) 255,
                        (byte) 255});
        } catch (Exception e) {
            throw new PdfException(PdfException.PdfEncryption, e);
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
                            envelopedData = PdfEncryptor.getContent(recipientInfo, certificateKey, certificateKeyProvider);
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
                md.update(new byte[]{(byte) 255, (byte) 255, (byte) 255, (byte) 255});
            }
            encryptionKey = md.digest();
        } catch (Exception f) {
            throw new PdfException(PdfException.PdfDecryption, f);
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
            throw new PdfException(PdfException.PdfEncryption, e);
        }
        return recipients;
    }

    protected abstract void setPubSecSpecificHandlerDicEntries(PdfDictionary encryptionDictionary, boolean encryptMetadata, boolean embeddedFilesOnly);
    protected abstract String getDigestAlgorithm();
    protected abstract void initKey(byte[] globalKey, int keyLength);

    protected void initKeyAndFillDictionary(PdfDictionary encryptionDictionary, Certificate[] certs, int[] permissions,
                                          boolean encryptMetadata, boolean embeddedFilesOnly) {
        addAllRecipients(certs, permissions);

        Integer keyLen = encryptionDictionary.getAsInt(PdfName.Length);
        int keyLength = keyLen != null ? keyLen : 40;

        String digestAlgorithm = getDigestAlgorithm();
        byte[] digest = computeGlobalKey(digestAlgorithm, encryptMetadata);
        initKey(digest, keyLength);

        setPubSecSpecificHandlerDicEntries(encryptionDictionary, encryptMetadata, embeddedFilesOnly);
    }

    protected void initKeyAndReadDictionary(PdfDictionary encryptionDictionary, Key certificateKey, Certificate certificate,
                                          String certificateKeyProvider, IExternalDecryptionProcess externalDecryptionProcess,
                                          boolean encryptMetadata) {
        String digestAlgorithm = getDigestAlgorithm();
        byte[] encryptionKey = computeGlobalKeyOnReading(encryptionDictionary, (PrivateKey) certificateKey, certificate,
                certificateKeyProvider, externalDecryptionProcess, encryptMetadata, digestAlgorithm);

        Integer keyLen = encryptionDictionary.getAsInt(PdfName.Length);
        int keyLength = keyLen != null ? keyLen : 40;
        initKey(encryptionKey, keyLength);
    }


    private void addRecipient(Certificate cert, int permission) {
        recipients.add(new PublicKeyRecipient(cert, permission));
    }

    private byte[] getSeed() {
        return seed.clone();
    }

    private int getRecipientsSize() {
        return recipients.size();
    }

    private byte[] getEncodedRecipient(int index) throws IOException, GeneralSecurityException {
        //Certificate certificate = recipient.getX509();
        PublicKeyRecipient recipient = recipients.get(index);
        byte[] cms = recipient.getCms();

        if (cms != null) return cms;

        Certificate certificate = recipient.getCertificate();
        //constants permissions: PdfWriter.AllowCopy | PdfWriter.AllowPrinting | PdfWriter.AllowScreenReaders | PdfWriter.AllowAssembly;
        int permission = recipient.getPermission();
        // NOTE! Added while porting to itext7
        // Previous strange code was:
        // int revision = 3;
        // permission |= revision == 3 ? 0xfffff0c0 : 0xffffffc0;
        // revision value never changed, so code have been replaced to this:
        permission |= 0xfffff0c0;
        permission &= 0xfffffffc;
        permission += 1;

        byte[] pkcs7input = new byte[24];

        byte one = (byte) permission;
        byte two = (byte) (permission >> 8);
        byte three = (byte) (permission >> 16);
        byte four = (byte) (permission >> 24);

        System.arraycopy(seed, 0, pkcs7input, 0, 20); // put this seed in the pkcs7 input

        pkcs7input[20] = four;
        pkcs7input[21] = three;
        pkcs7input[22] = two;
        pkcs7input[23] = one;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DEROutputStream k = new DEROutputStream(baos);
        ASN1Primitive obj = createDERForRecipient(pkcs7input, (X509Certificate) certificate);
        k.writeObject(obj);
        cms = baos.toByteArray();
        recipient.setCms(cms);

        return cms;
    }

    private PdfArray getEncodedRecipients() throws IOException, GeneralSecurityException {
        PdfArray EncodedRecipients = new PdfArray();
        byte[] cms;
        for (int i = 0; i < recipients.size(); i++) {
            try {
                cms = getEncodedRecipient(i);
                EncodedRecipients.add(new PdfLiteral(StreamUtil.createEscapedString(cms)));
            } catch (GeneralSecurityException e) {
                EncodedRecipients = null;
                // break was added while porting to itext7
                break;
            } catch (IOException e) {
                EncodedRecipients = null;
                // break was added while porting to itext7
                break;
            }
        }

        return EncodedRecipients;
    }

    private ASN1Primitive createDERForRecipient(byte[] in, X509Certificate cert)
            throws IOException, GeneralSecurityException {
        String s = "1.2.840.113549.3.2";

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
        byte[] abyte1 = cipher.doFinal(in);
        DEROctetString deroctetstring = new DEROctetString(abyte1);
        KeyTransRecipientInfo keytransrecipientinfo = computeRecipientInfo(cert, secretkey.getEncoded());
        DERSet derset = new DERSet(new RecipientInfo(keytransrecipientinfo));
        AlgorithmIdentifier algorithmidentifier = new AlgorithmIdentifier(new ASN1ObjectIdentifier(s), derobject);
        EncryptedContentInfo encryptedcontentinfo =
                new EncryptedContentInfo(PKCSObjectIdentifiers.data, algorithmidentifier, deroctetstring);
        EnvelopedData env = new EnvelopedData(null, derset, encryptedcontentinfo, (ASN1Set) null);
        ContentInfo contentinfo = new ContentInfo(PKCSObjectIdentifiers.envelopedData, env);
        return contentinfo.toASN1Primitive();
    }

    private KeyTransRecipientInfo computeRecipientInfo(X509Certificate x509certificate, byte[] abyte0)
            throws GeneralSecurityException, IOException {
        ASN1InputStream asn1inputstream = new ASN1InputStream(new ByteArrayInputStream(x509certificate.getTBSCertificate()));
        TBSCertificateStructure tbscertificatestructure = TBSCertificateStructure.getInstance(asn1inputstream.readObject());
        assert tbscertificatestructure != null;
        AlgorithmIdentifier algorithmidentifier = tbscertificatestructure.getSubjectPublicKeyInfo().getAlgorithm();
        IssuerAndSerialNumber issuerandserialnumber = new IssuerAndSerialNumber(
                tbscertificatestructure.getIssuer(),
                tbscertificatestructure.getSerialNumber().getValue());
        Cipher cipher = Cipher.getInstance(algorithmidentifier.getAlgorithm().getId());
        try {
            cipher.init(1, x509certificate);
        } catch (InvalidKeyException e) {
            cipher.init(1, x509certificate.getPublicKey());
        }
        DEROctetString deroctetstring = new DEROctetString(cipher.doFinal(abyte0));
        RecipientIdentifier recipId = new RecipientIdentifier(issuerandserialnumber);
        return new KeyTransRecipientInfo(recipId, algorithmidentifier, deroctetstring);
    }
}
