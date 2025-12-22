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
package com.itextpdf.signatures.validation.events;

import com.itextpdf.kernel.crypto.OID;

import java.util.HashMap;
import java.util.Map;

/**
 * This event triggers everytime an algorithm is used during signature validation.
 */
public class AlgorithmUsageEvent implements IValidationEvent {

    private static final Map<String, String> allowedDigests = new HashMap<>();
    private static final Map<String, String> allowedSignatureAlgorithms = new HashMap<>();
    private final String name;
    private final String oid;
    private final String usageLocation;

    static {
        allowedDigests.put("SHA224", OID.SHA_224);
        allowedDigests.put("SHA-224", OID.SHA_224);
        allowedDigests.put("SHA256", OID.SHA_256);
        allowedDigests.put("SHA-256", OID.SHA_256);
        allowedDigests.put("SHA384", OID.SHA_384);
        allowedDigests.put("SHA-384", OID.SHA_384);
        allowedDigests.put("SHA-512", OID.SHA_512);
        allowedDigests.put("SHA512", OID.SHA_512);
        allowedDigests.put("SHA-512/256", OID.SHA_512_256);
        allowedDigests.put("SHA512/256", OID.SHA_512_256);
        allowedDigests.put("SHA3-256", OID.SHA3_256);
        allowedDigests.put("SHA3-384", OID.SHA3_384);
        allowedDigests.put("SHA3-512", OID.SHA3_512);

        //Elliptical curve

        allowedSignatureAlgorithms.put("FRP256v1", "1.2.250.1.223.101.256.1");

        allowedSignatureAlgorithms.put("brainpoolP256r1", "1.3.36.3.3.2.8.1.1.7" );
        allowedSignatureAlgorithms.put("brainpoolP384r1", "1.3.36.3.3.2.8.1.1.11" );
        allowedSignatureAlgorithms.put("brainpoolP512r1", "1.3.36.3.3.2.8.1.1.13" );

        allowedSignatureAlgorithms.put("P-256", "1.2.840.10045.3.1.7" );
        allowedSignatureAlgorithms.put("secp256r1", "1.2.840.10045.3.1.7" );
        allowedSignatureAlgorithms.put("P-384", "1.3.132.0.34" );
        allowedSignatureAlgorithms.put("secp384r1", "1.3.132.0.34" );
        allowedSignatureAlgorithms.put("P-521", "1.3.132.0.35" );
        allowedSignatureAlgorithms.put("secp521r1", "1.3.132.0.35" );

        // signature algorithms
        allowedSignatureAlgorithms.put( "RSAES-PKCS1-v1_5", "1.2.840.113549.1.1.1");
        allowedSignatureAlgorithms.put( "rsaEncryption", "1.2.840.113549.1.1.1");

        allowedSignatureAlgorithms.put( "DSA", "1.2.840.10040.4.1");
        allowedSignatureAlgorithms.put( "id-dsa", "1.2.840.10040.4.1");
        allowedSignatureAlgorithms.put( "ECDSA", "1.2.840.10045.2.1");

        //signature suites
        allowedSignatureAlgorithms.put( "sha224WithRsaEncryption", "1.2.840.113549.1.1.14");
        allowedSignatureAlgorithms.put( "sha256WithRsaEncryption", "1.2.840.113549.1.1.11");
        allowedSignatureAlgorithms.put( "sha384WithRsaEncryption", "1.2.840.113549.1.1.13");
        allowedSignatureAlgorithms.put( "sha512WithRsaEncryption", "1.2.840.113549.1.1.12");
        // IETF RFC 4055 [8] defined a hash-independent OID for the RSASSA-PSS signature algorithm. The OID for the
        // specific hash function used in these algorithms is included in the algorithm parameters.
        // So it is applicable for SHA2 and SHA3.
        allowedSignatureAlgorithms.put( "id-RSASSA-PSS", "1.2.840.113549.1.1.10");

        //SHA
        allowedSignatureAlgorithms.put( "id-dsa-with-sha224", "2.16.840.1.101.3.4.3.1");
        allowedSignatureAlgorithms.put( "id-dsa-with-sha256", "2.16.840.1.101.3.4.3.2");
        //SHA-2
        allowedSignatureAlgorithms.put( "ecdsa-with-SHA224", "1.2.840.10045.4.3.1");
        allowedSignatureAlgorithms.put( "ecdsa-with-SHA256", "1.2.840.10045.4.3.2");
        allowedSignatureAlgorithms.put( "ecdsa-with-SHA384", "1.2.840.10045.4.3.3");
        allowedSignatureAlgorithms.put( "ecdsa-with-SHA512", "1.2.840.10045.4.3.4");
        //SHA3
        allowedSignatureAlgorithms.put( "id-ecdsa-with-sha3-224", "2.16.840.1.101.3.4.3.9");
        allowedSignatureAlgorithms.put( "id-ecdsa-with-sha3-256", "2.16.840.1.101.3.4.3.10");
        allowedSignatureAlgorithms.put( "id-ecdsa-with-sha3-384", "2.16.840.1.101.3.4.3.11");
        allowedSignatureAlgorithms.put( "id-ecdsa-with-sha3-512", "2.16.840.1.101.3.4.3.12");
        //ISO/IEC 14888-3 [4] defined hash-independent OIDs for the EC-XDSA algorithms. So the OID for
        //EC-SDSA-opt algorithm is applicable for SHA2 and SHA3.
        allowedSignatureAlgorithms.put("id-dswa-dl-EC-SDSA-opt", "1.0.14888.3.0.13" );

    }

    /**
     * Instantiates a new event.
     *
     * @param name          the algorithm name
     * @param oid           the algorithm oid
     * @param usageLocation the location the algorithm was used
     */
    public AlgorithmUsageEvent(String name, String oid, String usageLocation) {
        this.name = name;
        this.oid = oid;
        this.usageLocation = usageLocation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventType getEventType() {
        return EventType.ALGORITHM_USAGE;
    }

    /**
     * Returns whether the algorithm is allowed according to ETSI TS 119 312.
     *
     * @return whether the algorithm is allowed according to ETSI TS 119 312
     */
    public boolean isAllowedAccordingToEtsiTs119_312() {
        if (oid != null) {
            return allowedDigests.containsValue(oid) || allowedSignatureAlgorithms.containsValue(oid);
        }
        return allowedDigests.containsKey(name)
                || allowedSignatureAlgorithms.containsKey(name);
    }

   /**
     * Returns whether the algorithm is allowed according to ETSI TS 319 142-1.
     *
     * @return whether the algorithm is allowed according to ETSI TS 319 142-1
     */
    public boolean isAllowedAccordingToAdES() {
        if (oid != null) {
            return !OID.MD5.equals(oid);
        }
        return !"MD5".equals(name.toUpperCase());
    }

    /**
     * Returns the name of the algorithm if known.
     *
     * @return the name of the algorithm if known
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the location where or purpose the algorithm us uses for.
     *
     * @return the location where or purpose the algorithm us uses for
     */
    public String getUsageLocation() {
        return usageLocation;
    }

    /**
     * Returns the OID of the algorithm if known.
     *
     * @return the OID of the algorithm if known
     */
    public String getOid() {
        return oid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Algorithm Usage:\n\tname: ");
        sb.append(name)
                .append("\n\t oid:").append(oid)
                .append("\n\tusage: ").append(usageLocation);
        return sb.toString();
    }
}
