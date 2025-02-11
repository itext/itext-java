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
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.kernel.crypto.OID;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that contains OID mappings to extract a signature algorithm name
 * from a signature mechanism OID, and conversely, to retrieve the appropriate
 * signature mechanism OID given a signature algorithm and a digest function.
 */
public class SignatureMechanisms {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignatureMechanisms.class);
    
    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    /** Maps IDs of signature algorithms with its human-readable name. */
    static final Map<String, String> algorithmNames = new HashMap<>();
    static final Map<String, String> rsaOidsByDigest = new HashMap<>();
    static final Map<String, String> dsaOidsByDigest = new HashMap<>();
    static final Map<String, String> ecdsaOidsByDigest = new HashMap<>();

    static {
        algorithmNames.put("1.2.840.113549.1.1.1", "RSA");
        algorithmNames.put("1.2.840.10040.4.1", "DSA");
        algorithmNames.put("1.2.840.113549.1.1.2", "RSA");
        algorithmNames.put("1.2.840.113549.1.1.4", "RSA");
        algorithmNames.put("1.2.840.113549.1.1.5", "RSA");
        algorithmNames.put("1.2.840.113549.1.1.11", "RSA");
        algorithmNames.put("1.2.840.113549.1.1.12", "RSA");
        algorithmNames.put("1.2.840.113549.1.1.13", "RSA");
        algorithmNames.put("1.2.840.113549.1.1.14", "RSA");
        algorithmNames.put("1.2.840.10040.4.3", "DSA");
        algorithmNames.put("2.16.840.1.101.3.4.3.1", "DSA");
        algorithmNames.put("2.16.840.1.101.3.4.3.2", "DSA");
        algorithmNames.put("1.3.14.3.2.29", "RSA");
        algorithmNames.put("1.3.36.3.3.1.2", "RSA");
        algorithmNames.put("1.3.36.3.3.1.3", "RSA");
        algorithmNames.put("1.3.36.3.3.1.4", "RSA");
        algorithmNames.put("1.2.643.2.2.19", "ECGOST3410");

        // Elliptic curve public key cryptography
        algorithmNames.put("1.2.840.10045.2.1", "ECDSA");
        // Elliptic curve Digital Signature Algorithm (DSA) coupled with the Secure Hashing Algorithm (SHA) algorithm
        algorithmNames.put("1.2.840.10045.4.1", "ECDSA");
        // Elliptic curve Digital Signature Algorithm (DSA)
        algorithmNames.put("1.2.840.10045.4.3", "ECDSA");
        // Elliptic curve Digital Signature Algorithm (DSA) coupled with the Secure Hashing Algorithm (SHA256) algorithm
        algorithmNames.put("1.2.840.10045.4.3.2", "ECDSA");
        // Elliptic curve Digital Signature Algorithm (DSA) coupled with the Secure Hashing Algorithm (SHA384) algorithm
        algorithmNames.put("1.2.840.10045.4.3.3", "ECDSA");
        // Elliptic curve Digital Signature Algorithm (DSA) coupled with the Secure Hashing Algorithm (SHA512) algorithm
        algorithmNames.put("1.2.840.10045.4.3.4", "ECDSA");

        // Signing algorithms with SHA-3 digest functions (from NIST CSOR)
        algorithmNames.put("2.16.840.1.101.3.4.3.5", "DSA");
        algorithmNames.put("2.16.840.1.101.3.4.3.6", "DSA");
        algorithmNames.put("2.16.840.1.101.3.4.3.7", "DSA");
        algorithmNames.put("2.16.840.1.101.3.4.3.8", "DSA");
        algorithmNames.put("2.16.840.1.101.3.4.3.9", "ECDSA");
        algorithmNames.put("2.16.840.1.101.3.4.3.10", "ECDSA");
        algorithmNames.put("2.16.840.1.101.3.4.3.11", "ECDSA");
        algorithmNames.put("2.16.840.1.101.3.4.3.12", "ECDSA");
        algorithmNames.put("2.16.840.1.101.3.4.3.13", "RSA");
        algorithmNames.put("2.16.840.1.101.3.4.3.14", "RSA");
        algorithmNames.put("2.16.840.1.101.3.4.3.15", "RSA");
        algorithmNames.put("2.16.840.1.101.3.4.3.16", "RSA");

        /*
         * We tolerate two naming conventions for RSASSA-PSS:
         *
         *  - RSASSA-PSS
         *  - <digest>withRSA/PSS
         *
         * The former is considered the canonical one because it's the standard name in JCA,
         * the digest is required to be specified in the algorithm params anyway,
         * and the OID does not depend on the digest. BouncyCastle accepts both.
         */
        algorithmNames.put(OID.RSASSA_PSS, "RSASSA-PSS");

        // EdDSA
        algorithmNames.put(OID.ED25519, "Ed25519");
        algorithmNames.put(OID.ED448, "Ed448");

        rsaOidsByDigest.put("SHA224", "1.2.840.113549.1.1.14");
        rsaOidsByDigest.put("SHA256", "1.2.840.113549.1.1.11");
        rsaOidsByDigest.put("SHA384", "1.2.840.113549.1.1.12");
        rsaOidsByDigest.put("SHA512", "1.2.840.113549.1.1.13");
        rsaOidsByDigest.put("SHA-224", "1.2.840.113549.1.1.14");
        rsaOidsByDigest.put("SHA-256", "1.2.840.113549.1.1.11");
        rsaOidsByDigest.put("SHA-384", "1.2.840.113549.1.1.12");
        rsaOidsByDigest.put("SHA-512", "1.2.840.113549.1.1.13");
        rsaOidsByDigest.put("SHA3-224", "2.16.840.1.101.3.4.3.13");
        rsaOidsByDigest.put("SHA3-256", "2.16.840.1.101.3.4.3.14");
        rsaOidsByDigest.put("SHA3-384", "2.16.840.1.101.3.4.3.15");
        rsaOidsByDigest.put("SHA3-512", "2.16.840.1.101.3.4.3.16");

        dsaOidsByDigest.put("SHA224", "2.16.840.1.101.3.4.3.1");
        dsaOidsByDigest.put("SHA256", "2.16.840.1.101.3.4.3.2");
        dsaOidsByDigest.put("SHA384", "2.16.840.1.101.3.4.3.3");
        dsaOidsByDigest.put("SHA512", "2.16.840.1.101.3.4.3.4");
        dsaOidsByDigest.put("SHA3-224", "2.16.840.1.101.3.4.3.5");
        dsaOidsByDigest.put("SHA3-256", "2.16.840.1.101.3.4.3.6");
        dsaOidsByDigest.put("SHA3-384", "2.16.840.1.101.3.4.3.7");
        dsaOidsByDigest.put("SHA3-512", "2.16.840.1.101.3.4.3.8");

        ecdsaOidsByDigest.put("SHA1", "1.2.840.10045.4.1");
        ecdsaOidsByDigest.put("SHA224", "1.2.840.10045.4.3.1");
        ecdsaOidsByDigest.put("SHA256", "1.2.840.10045.4.3.2");
        ecdsaOidsByDigest.put("SHA384", "1.2.840.10045.4.3.3");
        ecdsaOidsByDigest.put("SHA512", "1.2.840.10045.4.3.4");
        ecdsaOidsByDigest.put("SHA3-224", "2.16.840.1.101.3.4.3.9");
        ecdsaOidsByDigest.put("SHA3-256", "2.16.840.1.101.3.4.3.10");
        ecdsaOidsByDigest.put("SHA3-384", "2.16.840.1.101.3.4.3.11");
        ecdsaOidsByDigest.put("SHA3-512", "2.16.840.1.101.3.4.3.12");
    }

    /**
     * Attempt to look up the most specific OID for a given signature-digest combination.
     *
     * @param signatureAlgorithmName  the name of the signature algorithm
     * @param digestAlgorithmName     the name of the digest algorithm, if any
     * @return an OID string, or {@code null} if none was found.
     */
    public static String getSignatureMechanismOid(String signatureAlgorithmName, String digestAlgorithmName) {
        String resultingOId;
        switch (signatureAlgorithmName) {
            case "RSA":
                final String oId = rsaOidsByDigest.get(digestAlgorithmName);
                resultingOId = oId == null ? OID.RSA : oId;
                break;
            case "DSA":
                resultingOId = dsaOidsByDigest.get(digestAlgorithmName);
                break;
            case "ECDSA":
                resultingOId = ecdsaOidsByDigest.get(digestAlgorithmName);
                break;
            case "Ed25519":
                resultingOId = OID.ED25519;
                break;
            case "Ed448":
                resultingOId = OID.ED448;
                break;
            case "RSASSA-PSS":
            case "RSA/PSS":
                resultingOId = OID.RSASSA_PSS;
                break;
            default:
                resultingOId = null;
        }
        if (resultingOId != null) {
            return resultingOId;
        }
        LOGGER.warn(KernelLogMessageConstant.ALGORITHM_NOT_FROM_SPEC);
        resultingOId = BOUNCY_CASTLE_FACTORY.getAlgorithmOid(digestAlgorithmName + "with" + signatureAlgorithmName);
        if (resultingOId == null) {
            return BOUNCY_CASTLE_FACTORY.getAlgorithmOid(signatureAlgorithmName);
        } else {
            return resultingOId;
        }
    }

    /**
     * Gets the algorithm name for a certain id.
     * @param oid	an id (for instance "1.2.840.113549.1.1.1")
     * @return	an algorithm name (for instance "RSA")
     */
    public static String getAlgorithm(String oid) {
        String ret = algorithmNames.get(oid);
        if (ret == null) {
            return oid;
        } else {
            return ret;
        }
    }

    /**
     * Get the signing mechanism name for a certain id and digest.
     * 
     * @param oid an id of an algorithm
     * @param digest digest of an algorithm
     * 
     * @return name of the mechanism
     */
    public static String getMechanism(String oid, String digest) {
        String algorithm = getAlgorithm(oid);
        if (!algorithm.equals(oid)) {
            return digest + "with" + algorithm;
        }
        LOGGER.warn(KernelLogMessageConstant.ALGORITHM_NOT_FROM_SPEC);
        return BOUNCY_CASTLE_FACTORY.getAlgorithmName(oid);
    }
}
