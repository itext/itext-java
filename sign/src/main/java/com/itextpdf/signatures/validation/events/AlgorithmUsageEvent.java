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
        allowedDigests.put(OID.SHA_224, "SHA224");
        allowedDigests.put(OID.SHA_224, "SHA-224");
        allowedDigests.put(OID.SHA_256, "SHA256");
        allowedDigests.put(OID.SHA_256, "SHA-256");
        allowedDigests.put(OID.SHA_384, "SHA384");
        allowedDigests.put(OID.SHA_384, "SHA-384");
        allowedDigests.put(OID.SHA_512, "SHA512");
        allowedDigests.put(OID.SHA_512, "SHA-512");
        allowedDigests.put(OID.SHA3_224, "SHA3-224");
        allowedDigests.put(OID.SHA3_256, "SHA3-256");
        allowedDigests.put(OID.SHA3_384, "SHA3-384");
        allowedDigests.put(OID.SHA3_512, "SHA3-512");
        allowedDigests.put(OID.SHAKE_256, "SHAKE256");

        allowedSignatureAlgorithms.put("1.2.840.113549.1.1.1", "RSAES-PKCS1-v1_5");
        allowedSignatureAlgorithms.put("1.2.840.10040.4.1", "DSA");
        allowedSignatureAlgorithms.put("1.2.840.113549.1.1.11", "sha256WithRsaEncryption");
        allowedSignatureAlgorithms.put("1.2.840.113549.1.1.12", "sha384WithRsaEncryption");
        allowedSignatureAlgorithms.put("1.2.840.113549.1.1.13", "sha512WithRsaEncryption");
        allowedSignatureAlgorithms.put("1.2.840.113549.1.1.14", "sha224WithRsaEncryption  ");
        allowedSignatureAlgorithms.put("2.16.840.1.101.3.4.3.1", "dsaWithSha224");
        allowedSignatureAlgorithms.put("2.16.840.1.101.3.4.3.2", "dsaWithSha256");

        allowedSignatureAlgorithms.put("1.2.840.10045.2.1", "ECDSA");
        allowedSignatureAlgorithms.put("1.2.840.10045.4.3.1", "ecdsaWithSha224");
        allowedSignatureAlgorithms.put("1.2.840.10045.4.3.2", "ecdsaWithSha256");
        allowedSignatureAlgorithms.put("1.2.840.10045.4.3.3", "ecdsaWithSha384");
        allowedSignatureAlgorithms.put("1.2.840.10045.4.3.4", "ecdsaWithSha512");

        allowedSignatureAlgorithms.put("2.16.840.1.101.3.4.3.9", "id_ecdsa_with_sha3_244");
        allowedSignatureAlgorithms.put("2.16.840.1.101.3.4.3.10", "id_ecdsa_with_sha3_256");
        allowedSignatureAlgorithms.put("2.16.840.1.101.3.4.3.11", "id_ecdsa_with_sha3_384");
        allowedSignatureAlgorithms.put("2.16.840.1.101.3.4.3.12", "id_ecdsa_with_sha3_512");
        allowedSignatureAlgorithms.put("2.16.840.1.101.3.4.3.13", "id_rsassa_pkcs1_v1_5_with_sha3_224");
        allowedSignatureAlgorithms.put("2.16.840.1.101.3.4.3.14", "id_rsassa_pkcs1_v1_5_with_sha3_256");
        allowedSignatureAlgorithms.put("2.16.840.1.101.3.4.3.15", "id_rsassa_pkcs1_v1_5_with_sha3_384");
        allowedSignatureAlgorithms.put("2.16.840.1.101.3.4.3.16", "id_rsassa_pkcs1_v1_5_with_sha3_512");

        allowedSignatureAlgorithms.put(OID.RSASSA_PSS, "RSASSA-PSS");

        // EdDSA
        allowedSignatureAlgorithms.put(OID.ED25519, "Ed25519");
        allowedSignatureAlgorithms.put(OID.ED448, "Ed448");
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
            return allowedDigests.containsKey(oid) || allowedSignatureAlgorithms.containsKey(oid);
        }
        return allowedDigests.values().contains(name)
                || allowedSignatureAlgorithms.values().contains(name);
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
