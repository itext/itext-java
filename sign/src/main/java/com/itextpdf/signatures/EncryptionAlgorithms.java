/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import java.util.HashMap;
import java.util.Map;

/**
 * Class that contains a map with the different encryption algorithms.
 */
public class EncryptionAlgorithms {

    /** Maps IDs of encryption algorithms with its human-readable name. */
    static final Map<String, String> algorithmNames = new HashMap<>();

    static {
        algorithmNames.put("1.2.840.113549.1.1.1", "RSA");
        algorithmNames.put("1.2.840.10040.4.1", "DSA");
        algorithmNames.put("1.2.840.113549.1.1.2", "RSA");
        algorithmNames.put("1.2.840.113549.1.1.4", "RSA");
        algorithmNames.put("1.2.840.113549.1.1.5", "RSA");
        algorithmNames.put("1.2.840.113549.1.1.14", "RSA");
        algorithmNames.put("1.2.840.113549.1.1.11", "RSA");
        algorithmNames.put("1.2.840.113549.1.1.12", "RSA");
        algorithmNames.put("1.2.840.113549.1.1.13", "RSA");
        algorithmNames.put("1.2.840.10040.4.3", "DSA");
        algorithmNames.put("2.16.840.1.101.3.4.3.1", "DSA");
        algorithmNames.put("2.16.840.1.101.3.4.3.2", "DSA");
        algorithmNames.put("1.3.14.3.2.29", "RSA");
        algorithmNames.put("1.3.36.3.3.1.2", "RSA");
        algorithmNames.put("1.3.36.3.3.1.3", "RSA");
        algorithmNames.put("1.3.36.3.3.1.4", "RSA");
        algorithmNames.put("1.2.643.2.2.19", "ECGOST3410");

        algorithmNames.put("1.2.840.10045.2.1", "ECDSA"); //Elliptic curve public key cryptography.
        algorithmNames.put("1.2.840.10045.4.1", "ECDSA"); //Elliptic curve Digital Signature Algorithm (DSA) coupled with the Secure Hashing Algorithm (SHA) algorithm.
        algorithmNames.put("1.2.840.10045.4.3", "ECDSA"); //Elliptic curve Digital Signature Algorithm (DSA).
        algorithmNames.put("1.2.840.10045.4.3.2", "ECDSA"); //Elliptic curve Digital Signature Algorithm (DSA) coupled with the Secure Hashing Algorithm (SHA256) algorithm.
        algorithmNames.put("1.2.840.10045.4.3.3", "ECDSA"); //Elliptic curve Digital Signature Algorithm (DSA) coupled with the Secure Hashing Algorithm (SHA384) algorithm.
        algorithmNames.put("1.2.840.10045.4.3.4", "ECDSA"); //Elliptic curve Digital Signature Algorithm (DSA) coupled with the Secure Hashing Algorithm (SHA512) algorithm.

    }

    /**
     * Gets the algorithm name for a certain id.
     * @param oid	an id (for instance "1.2.840.113549.1.1.1")
     * @return	an algorithm name (for instance "RSA")
     */
    public static String getAlgorithm(String oid) {
        String ret = algorithmNames.get(oid);
        if (ret == null)
            return oid;
        else
            return ret;
    }
}
