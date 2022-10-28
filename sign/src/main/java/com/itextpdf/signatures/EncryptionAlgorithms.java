/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
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
package com.itextpdf.signatures;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that contains a map with the different signature algorithms.
 *
 * <p>
 * This class is named {@code EncryptionAlgorithms} for historical reasons
 */
public class EncryptionAlgorithms {

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

        // EdDSA
        algorithmNames.put(SecurityIDs.ID_ED25519, "Ed25519");
        algorithmNames.put(SecurityIDs.ID_ED448, "Ed448");

        rsaOidsByDigest.put("SHA224", "1.2.840.113549.1.1.14");
        rsaOidsByDigest.put("SHA256", "1.2.840.113549.1.1.11");
        rsaOidsByDigest.put("SHA384", "1.2.840.113549.1.1.12");
        rsaOidsByDigest.put("SHA512", "1.2.840.113549.1.1.13");
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
        switch (signatureAlgorithmName) {
            case "RSA":
                // always return the generic RSASSA-with-PKCS #1 v1.5 padding OID
                // since there are comparison tests that depend on the generic OID being present
                // TODO fix those tests, and replace with rsaOidsByDigest.get(digestAlgorithmName, SecurityIDs.ID_RSA)
                return SecurityIDs.ID_RSA;
            case "DSA":
                return dsaOidsByDigest.get(digestAlgorithmName);
            case "ECDSA":
                return ecdsaOidsByDigest.get(digestAlgorithmName);
            case "Ed25519":
                return SecurityIDs.ID_ED25519;
            case "Ed448":
                return SecurityIDs.ID_ED448;
            default:
                return null;
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
}
