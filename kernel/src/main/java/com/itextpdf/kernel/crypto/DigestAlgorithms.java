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
package com.itextpdf.kernel.crypto;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that contains a map with the different message digest algorithms.
 */
public class DigestAlgorithms {

    /**
     * Algorithm available for signatures since PDF 1.3.
     */
    public static final String SHA1 = "SHA-1";

    /**
     * Algorithm available for signatures since PDF 1.6.
     */
    public static final String SHA256 = "SHA-256";

    /**
     * Algorithm available for signatures since PDF 1.7.
     */
    public static final String SHA384 = "SHA-384";

    /**
     * Algorithm available for signatures since PDF 1.7.
     */
    public static final String SHA512 = "SHA-512";

    /**
     * Algorithm available for signatures since PDF 1.7.
     */
    public static final String RIPEMD160 = "RIPEMD160";

    /**
     * Algorithm available for signatures since PDF 2.0
     * extended by ISO/TS 32001.
     */
    public static final String SHA3_256 = "SHA3-256";

    /**
     * Algorithm available for signatures since PDF 2.0
     * extended by ISO/TS 32001.
     */
    public static final String SHA3_512 = "SHA3-512";

    /**
     * Algorithm available for signatures since PDF 2.0
     * extended by ISO/TS 32001.
     */
    public static final String SHA3_384 = "SHA3-384";

    /**
     * Algorithm available for signatures since PDF 2.0
     * extended by ISO/TS 32001.
     *
     * <p>
     * The output length is fixed at 512 bits (64 bytes).
     */
    public static final String SHAKE256 = "SHAKE256";

    /**
     * Maps the digest IDs with the human-readable name of the digest algorithm.
     */
    private static final Map<String, String> digestNames = new HashMap<>();

    /**
     * Maps digest algorithm that are unknown by the JDKs MessageDigest object to a known one.
     */
    private static final Map<String, String> fixNames = new HashMap<>();

    /**
     * Maps the name of a digest algorithm with its ID.
     */
    private static final Map<String, String> allowedDigests = new HashMap<>();

    /**
     * Maps algorithm names to output lengths in bits.
     */
    private static final Map<String, Integer> bitLengths = new HashMap<>();

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final Logger LOGGER = LoggerFactory.getLogger(DigestAlgorithms.class);

    static {
        digestNames.put("1.2.840.113549.2.5", "MD5");
        digestNames.put("1.2.840.113549.2.2", "MD2");
        digestNames.put("1.3.14.3.2.26", "SHA1");
        digestNames.put(OID.SHA_224, "SHA224");
        digestNames.put(OID.SHA_256, "SHA256");
        digestNames.put(OID.SHA_384, "SHA384");
        digestNames.put(OID.SHA_512, "SHA512");
        digestNames.put("1.3.36.3.2.2", "RIPEMD128");
        digestNames.put("1.3.36.3.2.1", "RIPEMD160");
        digestNames.put("1.3.36.3.2.3", "RIPEMD256");
        digestNames.put("1.2.840.113549.1.1.4", "MD5");
        digestNames.put("1.2.840.113549.1.1.2", "MD2");
        digestNames.put("1.2.840.113549.1.1.5", "SHA1");
        digestNames.put("1.2.840.113549.1.1.14", "SHA224");
        digestNames.put("1.2.840.113549.1.1.11", "SHA256");
        digestNames.put("1.2.840.113549.1.1.12", "SHA384");
        digestNames.put("1.2.840.113549.1.1.13", "SHA512");
        digestNames.put("1.2.840.10040.4.3", "SHA1");
        digestNames.put("2.16.840.1.101.3.4.3.1", "SHA224");
        digestNames.put("2.16.840.1.101.3.4.3.2", "SHA256");
        digestNames.put("2.16.840.1.101.3.4.3.3", "SHA384");
        digestNames.put("2.16.840.1.101.3.4.3.4", "SHA512");
        digestNames.put("1.3.36.3.3.1.3", "RIPEMD128");
        digestNames.put("1.3.36.3.3.1.2", "RIPEMD160");
        digestNames.put("1.3.36.3.3.1.4", "RIPEMD256");
        digestNames.put("1.2.643.2.2.9", "GOST3411");
        digestNames.put(OID.SHA3_224, "SHA3-224");
        digestNames.put(OID.SHA3_256, "SHA3-256");
        digestNames.put(OID.SHA3_384, "SHA3-384");
        digestNames.put(OID.SHA3_512, "SHA3-512");
        digestNames.put(OID.SHAKE_256, "SHAKE256");

        fixNames.put("SHA256", SHA256);
        fixNames.put("SHA384", SHA384);
        fixNames.put("SHA512", SHA512);

        allowedDigests.put("MD2", "1.2.840.113549.2.2");
        allowedDigests.put("MD-2", "1.2.840.113549.2.2");
        allowedDigests.put("MD5", "1.2.840.113549.2.5");
        allowedDigests.put("MD-5", "1.2.840.113549.2.5");
        allowedDigests.put("SHA1", "1.3.14.3.2.26");
        allowedDigests.put("SHA-1", "1.3.14.3.2.26");
        allowedDigests.put("SHA224", OID.SHA_224);
        allowedDigests.put("SHA-224", OID.SHA_224);
        allowedDigests.put("SHA256", OID.SHA_256);
        allowedDigests.put("SHA-256", OID.SHA_256);
        allowedDigests.put("SHA384", OID.SHA_384);
        allowedDigests.put("SHA-384", OID.SHA_384);
        allowedDigests.put("SHA512", OID.SHA_512);
        allowedDigests.put("SHA-512", OID.SHA_512);
        allowedDigests.put("RIPEMD128", "1.3.36.3.2.2");
        allowedDigests.put("RIPEMD-128", "1.3.36.3.2.2");
        allowedDigests.put("RIPEMD160", "1.3.36.3.2.1");
        allowedDigests.put("RIPEMD-160", "1.3.36.3.2.1");
        allowedDigests.put("RIPEMD256", "1.3.36.3.2.3");
        allowedDigests.put("RIPEMD-256", "1.3.36.3.2.3");
        allowedDigests.put("GOST3411", "1.2.643.2.2.9");
        allowedDigests.put("SHA3-224", OID.SHA3_224);
        allowedDigests.put("SHA3-256", OID.SHA3_256);
        allowedDigests.put("SHA3-384", OID.SHA3_384);
        allowedDigests.put("SHA3-512", OID.SHA3_512);
        allowedDigests.put("SHAKE256", OID.SHAKE_256);

        bitLengths.put("MD2", 128);
        bitLengths.put("MD-2", 128);
        bitLengths.put("MD5", 128);
        bitLengths.put("MD-5", 128);
        bitLengths.put("SHA1", 160);
        bitLengths.put("SHA-1", 160);
        bitLengths.put("SHA224", 224);
        bitLengths.put("SHA-224", 224);
        bitLengths.put("SHA256", 256);
        bitLengths.put("SHA-256", 256);
        bitLengths.put("SHA384", 384);
        bitLengths.put("SHA-384", 384);
        bitLengths.put("SHA512", 512);
        bitLengths.put("SHA-512", 512);
        bitLengths.put("RIPEMD128", 128);
        bitLengths.put("RIPEMD-128", 128);
        bitLengths.put("RIPEMD160", 160);
        bitLengths.put("RIPEMD-160", 160);
        bitLengths.put("RIPEMD256", 256);
        bitLengths.put("RIPEMD-256", 256);
        bitLengths.put("SHA3-224", 224);
        bitLengths.put("SHA3-256", 256);
        bitLengths.put("SHA3-384", 384);
        bitLengths.put("SHA3-512", 512);
        bitLengths.put("SHAKE256", 512);
    }

    /**
     * Get a digest algorithm.
     *
     * @param digestOid oid of the digest algorithm
     * @param provider the provider you want to use to create the hash
     *
     * @return MessageDigest object
     *
     * @throws NoSuchAlgorithmException thrown when a particular cryptographic algorithm is
     * requested but is not available in the environment
     * @throws NoSuchProviderException thrown when a particular security provider is
     * requested but is not available in the environment
     */
    public static MessageDigest getMessageDigestFromOid(String digestOid, String provider)
            throws NoSuchAlgorithmException, NoSuchProviderException {
        return getMessageDigest(getDigest(digestOid), provider);
    }

    /**
     * Creates a MessageDigest object that can be used to create a hash.
     *
     * @param hashAlgorithm	the algorithm you want to use to create a hash
     * @param provider	the provider you want to use to create the hash
     *
     * @return	a MessageDigest object
     *
     * @throws NoSuchAlgorithmException thrown when a particular cryptographic algorithm is
     * requested but is not available in the environment
     * @throws NoSuchProviderException thrown when a particular security provider is
     * requested but is not available in the environment
     */
    public static MessageDigest getMessageDigest(String hashAlgorithm, String provider)
            throws NoSuchAlgorithmException, NoSuchProviderException {
        return CryptoUtil.getMessageDigest(hashAlgorithm, provider);
    }

    /**
     * Creates a hash using a specific digest algorithm and a provider.
     *
     * @param data	the message of which you want to create a hash
     * @param hashAlgorithm	the algorithm used to create the hash
     * @param provider	the provider used to create the hash
     *
     * @return	the hash
     *
     * @throws GeneralSecurityException when requested cryptographic algorithm or security provider
     * is not available
     * @throws IOException signals that an I/O exception has occurred
     */
    public static byte[] digest(InputStream data, String hashAlgorithm, String provider)
            throws GeneralSecurityException, IOException {
        MessageDigest messageDigest = getMessageDigest(hashAlgorithm, provider);
        return digest(data, messageDigest);
    }

    /**
     * Create a digest based on the input stream.
     *
     * @param data data to be digested
     * @param messageDigest algorithm to be used
     *
     * @return digest of the data
     *
     * @throws IOException signals that an I/O exception has occurred
     */
    public static byte[] digest(InputStream data, MessageDigest messageDigest)
            throws IOException {
        byte[] buf = new byte[8192];
        int n;
        while ((n = data.read(buf)) > 0) {
            messageDigest.update(buf, 0, n);
        }
        return messageDigest.digest();
    }

    /**
     * Gets the digest name for a certain id.
     *
     * @param oid an id (for instance "1.2.840.113549.2.5")
     *
     * @return a digest name (for instance "MD5")
     */
    public static String getDigest(String oid) {
        String ret = digestNames.get(oid);
        if (ret == null) {
            try {
                String digest = getMessageDigest(oid, BOUNCY_CASTLE_FACTORY.getProviderName()).getAlgorithm();
                LOGGER.warn(KernelLogMessageConstant.ALGORITHM_NOT_FROM_SPEC);
                return digest;
            } catch (Exception e) {
                return oid;
            }
        } else {
            return ret;
        }
    }

    /**
     * Normalize the digest name.
     *
     * @param algo the name to be normalized
     *
     * @return normalized name
     */
    public static String normalizeDigestName(String algo) {
        if (fixNames.containsKey(algo)) {
            return fixNames.get(algo);
        }
        return algo;
    }

    /**
     * Returns the id of a digest algorithms that is allowed in PDF,
     * or null if it isn't allowed.
     *
     * @param name the name of the digest algorithm
     *
     * @return an oid
     */
    public static String getAllowedDigest(String name) {
        if (name == null) {
            throw new IllegalArgumentException(
                    KernelExceptionMessageConstant.THE_NAME_OF_THE_DIGEST_ALGORITHM_IS_NULL);
        }
        String allowedDigest = allowedDigests.get(name.toUpperCase());
        if (allowedDigest != null) {
            return allowedDigest;
        }
        allowedDigest = BOUNCY_CASTLE_FACTORY.getDigestAlgorithmOid(name.toUpperCase());
        if (allowedDigest != null) {
            LOGGER.warn(KernelLogMessageConstant.ALGORITHM_NOT_FROM_SPEC);
        }
        return allowedDigest;
    }

    /**
     * Retrieve the output length in bits of the given digest algorithm.
     *
     * @param name the name of the digest algorithm
     *
     * @return the length of the output of the algorithm in bits
     */
    public static int getOutputBitLength(String name) {
        if (name == null) {
            throw new IllegalArgumentException(
                    KernelExceptionMessageConstant.THE_NAME_OF_THE_DIGEST_ALGORITHM_IS_NULL);
        }
        return bitLengths.get(name).intValue();
    }
}
