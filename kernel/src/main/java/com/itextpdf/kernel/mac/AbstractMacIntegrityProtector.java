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
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.crypto.OID;
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
import java.security.NoSuchProviderException;
import java.util.Arrays;

/**
 * Class responsible for integrity protection in encrypted documents, which uses MAC container.
 */
public abstract class AbstractMacIntegrityProtector {
    private static final IBouncyCastleFactory BC_FACTORY = BouncyCastleFactoryCreator.getFactory();

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
        if (kdfSalt == null) {
            throw new MacValidationException(KernelExceptionMessageConstant.MAC_VALIDATION_NO_SALT);
        }
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
                throw new MacValidationException(KernelExceptionMessageConstant.MAC_VALIDATION_FAILED);
            }
        } catch (PdfException e) {
            throw e;
        } catch (Exception e) {
            throw new MacValidationException(KernelExceptionMessageConstant.MAC_VALIDATION_EXCEPTION, e);
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
     * @throws NoSuchProviderException thrown when a particular security provider is
     * requested but is not available in the environment
     */
    protected byte[] digestBytes(byte[] bytes) throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
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
     * @throws NoSuchProviderException thrown when a particular security provider is
     * requested but is not available in the environment
     */
    protected byte[] digestBytes(InputStream inputStream)
            throws NoSuchAlgorithmException, IOException, NoSuchProviderException {

        if (inputStream == null) {
            return null;
        }
        final String algorithm = MacProperties.macDigestAlgorithmToString(macProperties.getMacDigestAlgorithm());
        MessageDigest digest = DigestAlgorithms.getMessageDigest(algorithm, BC_FACTORY.getProviderName());
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
        contentInfoV.add(BC_FACTORY.createASN1ObjectIdentifier(OID.AUTHENTICATED_DATA));

        // Recipient info
        IASN1EncodableVector recInfoV = BC_FACTORY.createASN1EncodableVector();
        recInfoV.add(BC_FACTORY.createASN1Integer(0)); // version
        recInfoV.add(BC_FACTORY.createDERTaggedObject(0,
                BC_FACTORY.createASN1ObjectIdentifier(OID.KDF_PDF_MAC_WRAP_KDF)));
        recInfoV.add(BC_FACTORY.createDERSequence(BC_FACTORY.createASN1ObjectIdentifier(getKeyWrappingAlgorithmOid())));

        ////////////////////// KEK

        byte[] macKek = BC_FACTORY.generateHKDF(fileEncryptionKey, kdfSalt, PDF_MAC.getBytes(StandardCharsets.UTF_8));
        byte[] encryptedKey = generateEncryptedKey(macKey, macKek);

        recInfoV.add(BC_FACTORY.createDEROctetString(encryptedKey));

        // Digest info
        byte[] messageBytes = createPdfMacIntegrityInfo(dataDigest, signature == null ? null : digestBytes(signature));

        // Encapsulated content info
        IASN1EncodableVector encapContentInfoV = BC_FACTORY.createASN1EncodableVector();
        encapContentInfoV.add(BC_FACTORY.createASN1ObjectIdentifier(OID.CT_PDF_MAC_INTEGRITY_INFO));
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
        final String algorithm = MacProperties.macDigestAlgorithmToString(macProperties.getMacDigestAlgorithm());
        final String macDigestOid = DigestAlgorithms.getAllowedDigest(algorithm);
        authDataV.add(BC_FACTORY.createDERTaggedObject(false, 1,
                BC_FACTORY.createDERSequence(BC_FACTORY.createASN1ObjectIdentifier(macDigestOid))));
        authDataV.add(BC_FACTORY.createDERSequence(encapContentInfoV));
        authDataV.add(BC_FACTORY.createDERTaggedObject(false, 2, authAttrs));
        authDataV.add(BC_FACTORY.createDEROctetString(mac));

        contentInfoV.add(BC_FACTORY.createDERTaggedObject(0, BC_FACTORY.createDERSequence(authDataV)));
        return BC_FACTORY.createDERSequence(contentInfoV);
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

    private IDERSequence createMessageDigestSequence(byte[] messageBytes)
            throws NoSuchAlgorithmException, IOException, NoSuchProviderException {

        final String algorithm = MacProperties.macDigestAlgorithmToString(macProperties.getMacDigestAlgorithm());
        // Hash messageBytes to get messageDigest attribute
        MessageDigest digest = DigestAlgorithms.getMessageDigest(algorithm, BC_FACTORY.getProviderName());
        digest.update(messageBytes);
        byte[] messageDigest = digestBytes(messageBytes);

        // Message digest
        IASN1EncodableVector messageDigestV = BC_FACTORY.createASN1EncodableVector();
        messageDigestV.add(BC_FACTORY.createASN1ObjectIdentifier(OID.MESSAGE_DIGEST));
        messageDigestV.add(BC_FACTORY.createDERSet(BC_FACTORY.createDEROctetString(messageDigest)));

        return BC_FACTORY.createDERSequence(messageDigestV);
    }

    private IDERSet createAuthAttributes(byte[] messageBytes)
            throws NoSuchAlgorithmException, IOException, NoSuchProviderException {

        // Content type - mac integrity info
        IASN1EncodableVector contentTypeInfoV = BC_FACTORY.createASN1EncodableVector();
        contentTypeInfoV.add(BC_FACTORY.createASN1ObjectIdentifier(OID.CONTENT_TYPE));
        contentTypeInfoV.add(BC_FACTORY.createDERSet(
                BC_FACTORY.createASN1ObjectIdentifier(OID.CT_PDF_MAC_INTEGRITY_INFO)));

        IASN1EncodableVector algorithmsInfoV = BC_FACTORY.createASN1EncodableVector();
        final String algorithm = MacProperties.macDigestAlgorithmToString(macProperties.getMacDigestAlgorithm());
        final String macDigestOid = DigestAlgorithms.getAllowedDigest(algorithm);
        algorithmsInfoV.add(BC_FACTORY.createDERSequence(BC_FACTORY.createASN1ObjectIdentifier(macDigestOid)));
        algorithmsInfoV.add(BC_FACTORY.createDERTaggedObject(2,
                BC_FACTORY.createASN1ObjectIdentifier(getMacAlgorithmOid())));

        // CMS algorithm protection
        IASN1EncodableVector algoProtectionInfoV = BC_FACTORY.createASN1EncodableVector();
        algoProtectionInfoV.add(BC_FACTORY.createASN1ObjectIdentifier(OID.CMS_ALGORITHM_PROTECTION));
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
            case OID.SHA_256:
                return MacDigestAlgorithm.SHA_256;
            case OID.SHA_384:
                return MacDigestAlgorithm.SHA_384;
            case OID.SHA_512:
                return MacDigestAlgorithm.SHA_512;
            case OID.SHA3_256:
                return MacDigestAlgorithm.SHA3_256;
            case OID.SHA3_384:
                return MacDigestAlgorithm.SHA3_384;
            case OID.SHA3_512:
                return MacDigestAlgorithm.SHA3_512;
            default:
                throw new PdfException(KernelExceptionMessageConstant.DIGEST_NOT_SUPPORTED);
        }
    }
}
