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
package com.itextpdf.kernel.mac;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableVector;
import com.itextpdf.commons.bouncycastle.asn1.IDERSequence;
import com.itextpdf.commons.bouncycastle.asn1.IDERSet;
import com.itextpdf.io.source.IRandomAccessSource;
import com.itextpdf.io.source.RASInputStream;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.mac.MacProperties.MacDigestAlgorithm;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Class responsible for integrity protection in encrypted documents, which uses MAC container.
 */
public abstract class AbstractMacIntegrityProtector {
    private static final IBouncyCastleFactory BC_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String ID_AUTHENTICATED_DATA = "1.2.840.113549.1.9.16.1.2";
    private static final String ID_KDF_PDF_MAC_WRAP_KDF = "1.0.32004.1.1";
    private static final String ID_CT_PDF_MAC_INTEGRITY_INFO = "1.0.32004.1.0";
    private static final String ID_CONTENT_TYPE = "1.2.840.113549.1.9.3";
    private static final String ID_CMS_ALGORITHM_PROTECTION = "1.2.840.113549.1.9.52";
    private static final String ID_MESSAGE_DIGEST = "1.2.840.113549.1.9.4";
    private static final String PDF_MAC = "PDFMAC";

    protected final PdfDocument document;
    protected final MacProperties macProperties;
    protected byte[] kdfSalt = null;
    protected byte[] fileEncryptionKey = new byte[0];
    private final MacContainerReader macContainerReader;

    /**
     * Creates {@link AbstractMacIntegrityProtector} instance from the provided {@link MacProperties}.
     *
     * @param document      {@link PdfDocument} for which integrity protection is required
     * @param macProperties {@link MacProperties} used to provide MAC algorithm properties
     */
    protected AbstractMacIntegrityProtector(PdfDocument document, MacProperties macProperties) {
        this.document = document;
        this.macContainerReader = null;
        this.macProperties = macProperties;
    }

    /**
     * Creates {@link AbstractMacIntegrityProtector} instance from the Auth dictionary.
     *
     * @param document          {@link PdfDocument} for which integrity protection is required
     * @param authDictionary    {@link PdfDictionary} representing Auth dictionary in which MAC container is stored
     */
    protected AbstractMacIntegrityProtector(PdfDocument document, PdfDictionary authDictionary) {
        this.document = document;
        this.macContainerReader = MacContainerReader.getInstance(authDictionary);
        this.macProperties = new MacProperties(getMacDigestAlgorithm(macContainerReader.parseDigestAlgorithm()));
    }

    /**
     * Sets file encryption key to be used during MAC calculation.
     *
     * @param fileEncryptionKey {@code byte[]} file encryption key bytes
     */
    public void setFileEncryptionKey(byte[] fileEncryptionKey) {
        this.fileEncryptionKey = fileEncryptionKey;
    }

    /**
     * Gets KDF salt bytes, which are used during MAC key encryption.
     *
     * @return {@code byte[]} KDF salt bytes.
     */
    public byte[] getKdfSalt() {
        if (kdfSalt == null) {
            kdfSalt = generateRandomBytes(32);
        }
        return Arrays.copyOf(kdfSalt, kdfSalt.length);
    }

    /**
     * Sets KDF salt bytes, to be used during MAC key encryption.
     *
     * @param kdfSalt {@code byte[]} KDF salt bytes.
     */
    public void setKdfSalt(byte[] kdfSalt) {
        this.kdfSalt = Arrays.copyOf(kdfSalt, kdfSalt.length);
    }

    /**
     * Validates MAC container integrity. This method throws {@link PdfException} in case of any modifications,
     * introduced to the document in question, after MAC container is integrated.
     */
    public void validateMacToken() {
        try {
            byte[] macKey = generateDecryptedKey(macContainerReader.parseMacKey());
            long[] byteRange = macContainerReader.getByteRange();
            byte[] dataDigest;
            IRandomAccessSource randomAccessSource = document.getReader().getSafeFile().createSourceView();
            try (InputStream rg = new RASInputStream(
                    new RandomAccessSourceFactory().createRanged(randomAccessSource, byteRange))) {
                dataDigest = digestBytes(rg);
            }

            byte[] expectedData = macContainerReader.parseAuthAttributes().getEncoded();
            byte[] expectedMac = generateMac(macKey, expectedData);
            byte[] signatureDigest = digestBytes(macContainerReader.getSignature());
            byte[] expectedMessageDigest = createMessageDigestSequence(
                    createPdfMacIntegrityInfo(dataDigest, signatureDigest)).getEncoded();

            byte[] actualMessageDigest = macContainerReader.parseMessageDigest().getEncoded();
            byte[] actualMac = macContainerReader.parseMac();

            if (!Arrays.equals(expectedMac, actualMac) ||
                    !Arrays.equals(expectedMessageDigest, actualMessageDigest)) {
                throw new PdfException(KernelExceptionMessageConstant.MAC_VALIDATION_FAILED);
            }
        } catch (PdfException e) {
            throw e;
        } catch (Exception e) {
            throw new PdfException(KernelExceptionMessageConstant.MAC_VALIDATION_EXCEPTION, e);
        }
    }

    /**
     * Digests provided bytes based on hash algorithm, specified for this class instance.
     *
     * @param bytes {@code byte[]} to be digested
     *
     * @return digested bytes.
     *
     * @throws NoSuchAlgorithmException in case of digesting algorithm related exceptions
     * @throws IOException in case of input-output related exceptions
     */
    protected byte[] digestBytes(byte[] bytes) throws NoSuchAlgorithmException, IOException {
        return bytes == null ? null : digestBytes(new ByteArrayInputStream(bytes));
    }

    /**
     * Digests provided input stream based on hash algorithm, specified for this class instance.
     *
     * @param inputStream {@link InputStream} to be digested
     *
     * @return digested bytes.
     *
     * @throws NoSuchAlgorithmException in case of digesting algorithm related exceptions
     * @throws IOException in case of input-output related exceptions
     */
    protected byte[] digestBytes(InputStream inputStream) throws NoSuchAlgorithmException, IOException {
        if (inputStream == null) {
            return null;
        }
        MessageDigest digest = getMessageDigest();
        byte[] buf = new byte[8192];
        int rd;
        while ((rd = inputStream.read(buf, 0, buf.length)) > 0) {
            digest.update(buf, 0, rd);
        }
        return digest.digest();
    }

    /**
     * Creates MAC container as ASN1 object based on data digest, MAC key and signature parameters.
     *
     * @param dataDigest data digest as {@code byte[]} to be used during MAC container creation
     * @param macKey MAC key as {@code byte[]} to be used during MAC container creation
     * @param signature signature value as {@code byte[]} to be used during MAC container creation
     *
     * @return MAC container as {@link IDERSequence}.
     *
     * @throws GeneralSecurityException in case of security related exceptions
     * @throws IOException in case of input-output related exceptions
     */
    protected IDERSequence createMacContainer(byte[] dataDigest, byte[] macKey, byte[] signature)
            throws GeneralSecurityException, IOException {
        IASN1EncodableVector contentInfoV = BC_FACTORY.createASN1EncodableVector();
        contentInfoV.add(BC_FACTORY.createASN1ObjectIdentifier(ID_AUTHENTICATED_DATA));

        // Recipient info
        IASN1EncodableVector recInfoV = BC_FACTORY.createASN1EncodableVector();
        recInfoV.add(BC_FACTORY.createASN1Integer(0)); // version
        recInfoV.add(BC_FACTORY.createDERTaggedObject(0,
                BC_FACTORY.createASN1ObjectIdentifier(ID_KDF_PDF_MAC_WRAP_KDF)));
        recInfoV.add(BC_FACTORY.createDERSequence(BC_FACTORY.createASN1ObjectIdentifier(getKeyWrappingAlgorithmOid())));

        ////////////////////// KEK

        byte[] macKek = BC_FACTORY.generateHKDF(fileEncryptionKey, kdfSalt, PDF_MAC.getBytes(StandardCharsets.UTF_8));
        byte[] encryptedKey = generateEncryptedKey(macKey, macKek);

        recInfoV.add(BC_FACTORY.createDEROctetString(encryptedKey));

        // Digest info
        byte[] messageBytes = createPdfMacIntegrityInfo(dataDigest, signature == null ? null : digestBytes(signature));

        // Encapsulated content info
        IASN1EncodableVector encapContentInfoV = BC_FACTORY.createASN1EncodableVector();
        encapContentInfoV.add(BC_FACTORY.createASN1ObjectIdentifier(ID_CT_PDF_MAC_INTEGRITY_INFO));
        encapContentInfoV.add(BC_FACTORY.createDERTaggedObject(0, BC_FACTORY.createDEROctetString(messageBytes)));

        IDERSet authAttrs = createAuthAttributes(messageBytes);

        // Create mac
        byte[] data = authAttrs.getEncoded();
        byte[] mac = generateMac(macKey, data);

        // Auth data
        IASN1EncodableVector authDataV = BC_FACTORY.createASN1EncodableVector();
        authDataV.add(BC_FACTORY.createASN1Integer(0)); // version
        authDataV.add(BC_FACTORY.createDERSet(BC_FACTORY.createDERTaggedObject(false, 3,
                BC_FACTORY.createDERSequence(recInfoV))));

        authDataV.add(BC_FACTORY.createDERSequence(BC_FACTORY.createASN1ObjectIdentifier(getMacAlgorithmOid())));
        authDataV.add(BC_FACTORY.createDERTaggedObject(false, 1,
                BC_FACTORY.createDERSequence(BC_FACTORY.createASN1ObjectIdentifier(getMacDigestOid()))));
        authDataV.add(BC_FACTORY.createDERSequence(encapContentInfoV));
        authDataV.add(BC_FACTORY.createDERTaggedObject(false, 2, authAttrs));
        authDataV.add(BC_FACTORY.createDEROctetString(mac));

        contentInfoV.add(BC_FACTORY.createDERTaggedObject(0, BC_FACTORY.createDERSequence(authDataV)));
        return BC_FACTORY.createDERSequence(contentInfoV);
    }

    private MessageDigest getMessageDigest() throws NoSuchAlgorithmException {
        switch (macProperties.getMacDigestAlgorithm()) {
            case SHA_256:
                return MessageDigest.getInstance("SHA256");
            case SHA_384:
                return MessageDigest.getInstance("SHA384");
            case SHA_512:
                return MessageDigest.getInstance("SHA512");
            case SHA3_256:
                return MessageDigest.getInstance("SHA3-256");
            case SHA3_384:
                return MessageDigest.getInstance("SHA3-384");
            case SHA3_512:
                return MessageDigest.getInstance("SHA3-512");
            default:
                throw new PdfException("This digest algorithm is not supported by MAC.");
        }
    }

    private String getMacDigestOid() {
        switch (macProperties.getMacDigestAlgorithm()) {
            case SHA_256:
                return "2.16.840.1.101.3.4.2.1";
            case SHA_384:
                return "2.16.840.1.101.3.4.2.2";
            case SHA_512:
                return "2.16.840.1.101.3.4.2.3";
            case SHA3_256:
                return "2.16.840.1.101.3.4.2.8";
            case SHA3_384:
                return "2.16.840.1.101.3.4.2.9";
            case SHA3_512:
                return "2.16.840.1.101.3.4.2.10";
            default:
                throw new PdfException(KernelExceptionMessageConstant.DIGEST_NOT_SUPPORTED);
        }
    }

    private byte[] generateMac(byte[] macKey, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
        switch (macProperties.getMacAlgorithm()) {
            case HMAC_WITH_SHA_256:
                return BC_FACTORY.generateHMACSHA256Token(macKey, data);
            default:
                throw new PdfException(KernelExceptionMessageConstant.MAC_ALGORITHM_NOT_SUPPORTED);
        }
    }

    private byte[] generateEncryptedKey(byte[] macKey, byte[] macKek) throws GeneralSecurityException {
        switch (macProperties.getKeyWrappingAlgorithm()) {
            case AES_256_NO_PADD:
                return BC_FACTORY.generateEncryptedKeyWithAES256NoPad(macKey, macKek);
            default:
                throw new PdfException(KernelExceptionMessageConstant.WRAP_ALGORITHM_NOT_SUPPORTED);
        }
    }

    private byte[] generateDecryptedKey(byte[] encryptedMacKey) throws GeneralSecurityException {
        byte[] macKek = BC_FACTORY.generateHKDF(fileEncryptionKey, kdfSalt, PDF_MAC.getBytes(StandardCharsets.UTF_8));
        switch (macProperties.getKeyWrappingAlgorithm()) {
            case AES_256_NO_PADD:
                return BC_FACTORY.generateDecryptedKeyWithAES256NoPad(encryptedMacKey, macKek);
            default:
                throw new PdfException(KernelExceptionMessageConstant.WRAP_ALGORITHM_NOT_SUPPORTED);
        }
    }

    private String getMacAlgorithmOid() {
        switch (macProperties.getMacAlgorithm()) {
            case HMAC_WITH_SHA_256:
                return "1.2.840.113549.2.9";
            default:
                throw new PdfException(KernelExceptionMessageConstant.MAC_ALGORITHM_NOT_SUPPORTED);
        }
    }

    private String getKeyWrappingAlgorithmOid() {
        switch (macProperties.getKeyWrappingAlgorithm()) {
            case AES_256_NO_PADD:
                return "2.16.840.1.101.3.4.1.45";
            default:
                throw new PdfException(KernelExceptionMessageConstant.WRAP_ALGORITHM_NOT_SUPPORTED);
        }
    }

    private IDERSequence createMessageDigestSequence(byte[] messageBytes) throws NoSuchAlgorithmException, IOException {
        // Hash messageBytes to get messageDigest attribute
        MessageDigest digest = getMessageDigest();
        digest.update(messageBytes);
        byte[] messageDigest = digestBytes(messageBytes);

        // Message digest
        IASN1EncodableVector messageDigestV = BC_FACTORY.createASN1EncodableVector();
        messageDigestV.add(BC_FACTORY.createASN1ObjectIdentifier(ID_MESSAGE_DIGEST));
        messageDigestV.add(BC_FACTORY.createDERSet(BC_FACTORY.createDEROctetString(messageDigest)));

        return BC_FACTORY.createDERSequence(messageDigestV);
    }

    private IDERSet createAuthAttributes(byte[] messageBytes) throws NoSuchAlgorithmException, IOException {
        // Content type - mac integrity info
        IASN1EncodableVector contentTypeInfoV = BC_FACTORY.createASN1EncodableVector();
        contentTypeInfoV.add(BC_FACTORY.createASN1ObjectIdentifier(ID_CONTENT_TYPE));
        contentTypeInfoV.add(BC_FACTORY.createDERSet(
                BC_FACTORY.createASN1ObjectIdentifier(ID_CT_PDF_MAC_INTEGRITY_INFO)));

        IASN1EncodableVector algorithmsInfoV = BC_FACTORY.createASN1EncodableVector();
        algorithmsInfoV.add(BC_FACTORY.createDERSequence(BC_FACTORY.createASN1ObjectIdentifier(getMacDigestOid())));
        algorithmsInfoV.add(BC_FACTORY.createDERTaggedObject(2,
                BC_FACTORY.createASN1ObjectIdentifier(getMacAlgorithmOid())));

        // CMS algorithm protection
        IASN1EncodableVector algoProtectionInfoV = BC_FACTORY.createASN1EncodableVector();
        algoProtectionInfoV.add(BC_FACTORY.createASN1ObjectIdentifier(ID_CMS_ALGORITHM_PROTECTION));
        algoProtectionInfoV.add(BC_FACTORY.createDERSet(BC_FACTORY.createDERSequence(algorithmsInfoV)));

        IASN1EncodableVector authAttrsV = BC_FACTORY.createASN1EncodableVector();
        authAttrsV.add(BC_FACTORY.createDERSequence(contentTypeInfoV));
        authAttrsV.add(BC_FACTORY.createDERSequence(algoProtectionInfoV));
        authAttrsV.add(createMessageDigestSequence(messageBytes));

        return BC_FACTORY.createDERSet(authAttrsV);
    }

    private static byte[] createPdfMacIntegrityInfo(byte[] dataDigest, byte[] signatureDigest) throws IOException {
        IASN1EncodableVector digestInfoV = BC_FACTORY.createASN1EncodableVector();
        digestInfoV.add(BC_FACTORY.createASN1Integer(0));
        digestInfoV.add(BC_FACTORY.createDEROctetString(dataDigest));
        if (signatureDigest != null) {
            digestInfoV.add(BC_FACTORY.createDERTaggedObject(false, 0,
                    BC_FACTORY.createDEROctetString(signatureDigest)));
        }
        return BC_FACTORY.createDERSequence(digestInfoV).getEncoded();
    }

    protected static byte[] generateRandomBytes(int length) {
        byte[] randomBytes = new byte[length];
        BC_FACTORY.getSecureRandom().nextBytes(randomBytes);
        return randomBytes;
    }

    private static MacDigestAlgorithm getMacDigestAlgorithm(String oid) {
        switch (oid) {
            case "2.16.840.1.101.3.4.2.1":
                return MacDigestAlgorithm.SHA_256;
            case "2.16.840.1.101.3.4.2.2":
                return MacDigestAlgorithm.SHA_384;
            case "2.16.840.1.101.3.4.2.3":
                return MacDigestAlgorithm.SHA_512;
            case "2.16.840.1.101.3.4.2.8":
                return MacDigestAlgorithm.SHA3_256;
            case "2.16.840.1.101.3.4.2.9":
                return MacDigestAlgorithm.SHA3_384;
            case "2.16.840.1.101.3.4.2.10":
                return MacDigestAlgorithm.SHA3_512;
            default:
                throw new PdfException(KernelExceptionMessageConstant.DIGEST_NOT_SUPPORTED);
        }
    }
}
