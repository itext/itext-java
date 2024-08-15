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
import com.itextpdf.io.source.IRandomAccessSource;
import com.itextpdf.io.source.RASInputStream;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputStream;
import com.itextpdf.kernel.pdf.PdfString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Class responsible for integrity protection in encrypted documents, which uses MAC container.
 */
public class MacIntegrityProtector {
    private static final IBouncyCastleFactory BC_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String ID_AUTHENTICATED_DATA = "1.2.840.113549.1.9.16.1.2";
    private static final String ID_KDF_PDFMACWRAPKDF = "1.0.32004.1.1";
    private static final String ID_CT_PDFMACINTEGRITYINFO = "1.0.32004.1.0";
    private static final String ID_CONTENT_TYPE = "1.2.840.113549.1.9.3";
    private static final String ID_CMS_ALGORITHM_PROTECTION = "1.2.840.113549.1.9.52";
    private static final String ID_MESSAGE_DIGEST = "1.2.840.113549.1.9.4";

    private static final String DIGEST_NOT_SUPPORTED = "Digest algorithm is not supported.";
    private static final String MAC_ALGORITHM_NOT_SUPPORTED = "This MAC algorithm is not supported.";
    private static final String WRAP_ALGORITHM_NOT_SUPPORTED = "This wrapping algorithm is not supported.";
    private static final String CONTAINER_GENERATION_EXCEPTION = "Exception occurred during MAC container generation.";
    private static final String CONTAINER_EMBEDDING_EXCEPTION =
            "IOException occurred while trying to embed MAC container into document output stream.";

    private final MacPdfObject macPdfObject;
    private final PdfDocument document;
    private final MacProperties macProperties;
    private final byte[] kdfSalt = new byte[32];
    private byte[] fileEncryptionKey = new byte[0];

    /**
     * Creates {@link MacIntegrityProtector} instance.
     *
     * @param document      {@link PdfDocument}, for which integrity protection is required
     * @param macProperties {@link MacProperties} used to provide MAC algorithm properties
     */
    public MacIntegrityProtector(PdfDocument document, MacProperties macProperties) {
        this.document = document;
        this.macProperties = macProperties;

        this.macPdfObject = new MacPdfObject(getContainerSizeEstimate());

        addDocumentEvents();
        SecureRandom sr = BC_FACTORY.getSecureRandom();
        sr.nextBytes(kdfSalt);
    }

    /**
     * Sets file encryption key to be used during MAC token calculation.
     *
     * @param fileEncryptionKey {@code byte[]} file encryption key bytes
     */
    public void setFileEncryptionKey(byte[] fileEncryptionKey) {
        this.fileEncryptionKey = fileEncryptionKey;
    }

    /**
     * Gets KDF salt bytes, which are used during MAC token calculation.
     *
     * @return {@code byte[]} KDF salt bytes.
     */
    public byte[] getKdfSalt() {
        return Arrays.copyOf(kdfSalt, kdfSalt.length);
    }

    private int getContainerSizeEstimate() {
        try {
            MessageDigest digest = getMessageDigest();
            digest.update(new byte[0]);
            return createMacContainer(digest.digest()).length * 2 + 2;
        } catch (GeneralSecurityException | IOException e) {
            throw new PdfException(CONTAINER_GENERATION_EXCEPTION, e);
        }
    }

    private void addDocumentEvents() {
        document.addEventHandler(PdfDocumentEvent.START_DOCUMENT_CLOSING,
                new MacPdfObjectAddingEvent(document, macPdfObject));
        document.addEventHandler(PdfDocumentEvent.END_WRITER_FLUSH, new MacContainerEmbedder());
    }

    private void embedMacContainer() throws IOException {
        byte[] documentBytes = getDocumentByteArrayOutputStream().toByteArray();
        long[] byteRange = macPdfObject.computeByteRange(documentBytes.length);

        long byteRangePosition = macPdfObject.getByteRangePosition();
        ByteArrayOutputStream localBaos = new ByteArrayOutputStream();
        PdfOutputStream os = new PdfOutputStream(localBaos);
        os.write('[');
        for (long l : byteRange) {
            os.writeLong(l).write(' ');
        }
        os.write(']');
        System.arraycopy(localBaos.toByteArray(), 0, documentBytes, (int) byteRangePosition, localBaos.size());

        IRandomAccessSource ras = new RandomAccessSourceFactory().createSource(documentBytes);
        // Here we should create MAC token
        byte[] macToken;
        try {
            byte[] dataDigest = digestBytes(ras, byteRange);
            macToken = createMacContainer(dataDigest);
        } catch (GeneralSecurityException e) {
            throw new PdfException(CONTAINER_GENERATION_EXCEPTION, e);
        }

        PdfString macString = new PdfString(macToken).setHexWriting(true);

        // fill in the MAC
        localBaos.reset();
        os.write(macString);
        System.arraycopy(localBaos.toByteArray(), 0, documentBytes, (int) byteRange[1], localBaos.size());
        getDocumentByteArrayOutputStream().reset();
        document.getWriter().getOutputStream().write(documentBytes, 0, documentBytes.length);
    }

    private ByteArrayOutputStream getDocumentByteArrayOutputStream() {
        return ((ByteArrayOutputStream) document.getWriter().getOutputStream());
    }

    private byte[] digestBytes(IRandomAccessSource ras, long[] byteRange) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = getMessageDigest();

        try (InputStream rg = new RASInputStream(new RandomAccessSourceFactory().createRanged(ras, byteRange))) {
            byte[] buf = new byte[8192];
            int rd;
            while ((rd = rg.read(buf, 0, buf.length)) > 0) {
                digest.update(buf, 0, rd);
            }
            return digest.digest();
        }
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

    private String getMessageDigestOid() {
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
                throw new PdfException(DIGEST_NOT_SUPPORTED);
        }
    }

    private byte[] generateMacToken(byte[] macKey, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
        switch (macProperties.getMacAlgorithm()) {
            case HMAC_WITH_SHA_256:
                return BC_FACTORY.generateHMACSHA256Token(macKey, data);
            default:
                throw new PdfException(MAC_ALGORITHM_NOT_SUPPORTED);
        }
    }

    private byte[] generateEncryptedKey(byte[] macKey, byte[] macKek) throws GeneralSecurityException {
        switch (macProperties.getKeyWrappingAlgorithm()) {
            case AES_256_NO_PADD:
                return BC_FACTORY.generateEncryptedKeyWithAES256NoPad(macKey, macKek);
            default:
                throw new PdfException(WRAP_ALGORITHM_NOT_SUPPORTED);
        }
    }

    private String getMacAlgorithmOid() {
        switch (macProperties.getMacAlgorithm()) {
            case HMAC_WITH_SHA_256:
                return "1.2.840.113549.2.9";
            default:
                throw new PdfException(MAC_ALGORITHM_NOT_SUPPORTED);
        }
    }

    private String getKeyWrappingAlgorithmOid() {
        switch (macProperties.getKeyWrappingAlgorithm()) {
            case AES_256_NO_PADD:
                return "2.16.840.1.101.3.4.1.45";
            default:
                throw new PdfException(WRAP_ALGORITHM_NOT_SUPPORTED);
        }
    }

    private byte[] createMacContainer(byte[] dataDigest) throws GeneralSecurityException, IOException {
        IASN1EncodableVector contentInfoV = BC_FACTORY.createASN1EncodableVector();
        contentInfoV.add(BC_FACTORY.createASN1ObjectIdentifier(ID_AUTHENTICATED_DATA));

        // Recipient info
        IASN1EncodableVector recInfoV = BC_FACTORY.createASN1EncodableVector();
        recInfoV.add(BC_FACTORY.createASN1Integer(0)); // version
        recInfoV.add(BC_FACTORY.createDERTaggedObject(0,
                BC_FACTORY.createASN1ObjectIdentifier(ID_KDF_PDFMACWRAPKDF)));
        recInfoV.add(BC_FACTORY.createDERSequence(BC_FACTORY.createASN1ObjectIdentifier(getKeyWrappingAlgorithmOid())));

        ////////////////////// KEK

        SecureRandom sr = BC_FACTORY.getSecureRandom();
        byte[] macKey = new byte[32];
        sr.nextBytes(macKey);
        byte[] macKek = BC_FACTORY.generateHKDF(fileEncryptionKey, kdfSalt, "PDFMAC".getBytes(StandardCharsets.UTF_8));

        byte[] encryptedKey = generateEncryptedKey(macKey, macKek);

        //////////////////////////////
        recInfoV.add(BC_FACTORY.createDEROctetString(encryptedKey));

        // Digest info
        IASN1EncodableVector digestInfoV = BC_FACTORY.createASN1EncodableVector();
        digestInfoV.add(BC_FACTORY.createASN1Integer(0)); // version
        digestInfoV.add(BC_FACTORY.createDEROctetString(dataDigest));
        byte[] messageBytes = BC_FACTORY.createDERSequence(digestInfoV).getEncoded();

        // Encapsulated content info
        IASN1EncodableVector encapContentInfoV = BC_FACTORY.createASN1EncodableVector();
        encapContentInfoV.add(BC_FACTORY.createASN1ObjectIdentifier(ID_CT_PDFMACINTEGRITYINFO));
        encapContentInfoV.add(BC_FACTORY.createDERTaggedObject(0, BC_FACTORY.createDEROctetString(messageBytes)));

        // Hash messageBytes to get messageDigest attribute
        MessageDigest digest = getMessageDigest();
        digest.update(messageBytes);
        byte[] messageDigest = digest.digest();

        // Content type - mac integrity info
        IASN1EncodableVector contentTypeInfoV = BC_FACTORY.createASN1EncodableVector();
        contentTypeInfoV.add(BC_FACTORY.createASN1ObjectIdentifier(ID_CONTENT_TYPE));
        contentTypeInfoV.add(BC_FACTORY.createDERSet(BC_FACTORY.createASN1ObjectIdentifier(ID_CT_PDFMACINTEGRITYINFO)));

        IASN1EncodableVector algorithmsInfoV = BC_FACTORY.createASN1EncodableVector();
        algorithmsInfoV.add(BC_FACTORY.createDERSequence(BC_FACTORY.createASN1ObjectIdentifier(getMessageDigestOid())));
        algorithmsInfoV.add(BC_FACTORY.createDERTaggedObject(2,
                BC_FACTORY.createASN1ObjectIdentifier(getMacAlgorithmOid())));

        // CMS algorithm protection
        IASN1EncodableVector algoProtectionInfoV = BC_FACTORY.createASN1EncodableVector();
        algoProtectionInfoV.add(BC_FACTORY.createASN1ObjectIdentifier(ID_CMS_ALGORITHM_PROTECTION));
        algoProtectionInfoV.add(BC_FACTORY.createDERSet(BC_FACTORY.createDERSequence(algorithmsInfoV)));

        // Message digest
        IASN1EncodableVector messageDigestV = BC_FACTORY.createASN1EncodableVector();
        messageDigestV.add(BC_FACTORY.createASN1ObjectIdentifier(ID_MESSAGE_DIGEST));
        messageDigestV.add(BC_FACTORY.createDERSet(BC_FACTORY.createDEROctetString(messageDigest)));

        IASN1EncodableVector authAttrsV = BC_FACTORY.createASN1EncodableVector();
        authAttrsV.add(BC_FACTORY.createDERSequence(contentTypeInfoV));
        authAttrsV.add(BC_FACTORY.createDERSequence(algoProtectionInfoV));
        authAttrsV.add(BC_FACTORY.createDERSequence(messageDigestV));

        // Create mac
        byte[] data = BC_FACTORY.createDERSet(authAttrsV).getEncoded();
        byte[] mac = generateMacToken(macKey, data);

        // Auth data
        IASN1EncodableVector authDataV = BC_FACTORY.createASN1EncodableVector();
        authDataV.add(BC_FACTORY.createASN1Integer(0)); // version
        authDataV.add(BC_FACTORY.createDERSet(BC_FACTORY.createDERTaggedObject(false, 3,
                BC_FACTORY.createDERSequence(recInfoV))));

        authDataV.add(BC_FACTORY.createDERSequence(BC_FACTORY.createASN1ObjectIdentifier(getMacAlgorithmOid())));
        authDataV.add(BC_FACTORY.createDERTaggedObject(1,
                BC_FACTORY.createASN1ObjectIdentifier(getMessageDigestOid())));
        authDataV.add(BC_FACTORY.createDERSequence(encapContentInfoV));
        authDataV.add(BC_FACTORY.createDERTaggedObject(false, 2, BC_FACTORY.createDERSet(authAttrsV)));
        authDataV.add(BC_FACTORY.createDEROctetString(mac));

        contentInfoV.add(BC_FACTORY.createDERTaggedObject(0, BC_FACTORY.createDERSequence(authDataV)));
        return BC_FACTORY.createDERSequence(contentInfoV).getEncoded();
    }

    private static class MacPdfObjectAddingEvent implements IEventHandler {
        private final PdfDocument document;
        private final MacPdfObject macPdfObject;

        MacPdfObjectAddingEvent(PdfDocument document, MacPdfObject macPdfObject) {
            this.document = document;
            this.macPdfObject = macPdfObject;
        }

        @Override
        public void handleEvent(Event event) {
            document.getTrailer().put(PdfName.AuthCode, macPdfObject.getPdfObject());
        }
    }

    private class MacContainerEmbedder implements IEventHandler {
        @Override
        public void handleEvent(Event event) {
            try {
                embedMacContainer();
            } catch (IOException e) {
                throw new PdfException(CONTAINER_EMBEDDING_EXCEPTION, e);
            }
        }
    }
}
