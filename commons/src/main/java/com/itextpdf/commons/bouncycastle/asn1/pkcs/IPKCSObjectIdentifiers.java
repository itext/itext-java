package com.itextpdf.commons.bouncycastle.asn1.pkcs;

import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;

/**
 * This interface represents the wrapper for PKCSObjectIdentifiers that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IPKCSObjectIdentifiers {
    /**
     * Gets {@code id_aa_signatureTimeStampToken} constant for the wrapped PKCSObjectIdentifiers.
     *
     * @return PKCSObjectIdentifiers.id_aa_signatureTimeStampToken wrapper.
     */
    IASN1ObjectIdentifier getIdAaSignatureTimeStampToken();

    /**
     * Gets {@code id_aa_ets_sigPolicyId} constant for the wrapped PKCSObjectIdentifiers.
     *
     * @return PKCSObjectIdentifiers.id_aa_ets_sigPolicyId wrapper.
     */
    IASN1ObjectIdentifier getIdAaEtsSigPolicyId();

    /**
     * Gets {@code id_spq_ets_uri} constant for the wrapped PKCSObjectIdentifiers.
     *
     * @return PKCSObjectIdentifiers.id_spq_ets_uri wrapper.
     */
    IASN1ObjectIdentifier getIdSpqEtsUri();

    /**
     * Gets {@code envelopedData} constant for the wrapped PKCSObjectIdentifiers.
     *
     * @return PKCSObjectIdentifiers.envelopedData wrapper.
     */
    IASN1ObjectIdentifier getEnvelopedData();

    /**
     * Gets {@code data} constant for the wrapped PKCSObjectIdentifiers.
     *
     * @return PKCSObjectIdentifiers.data wrapper.
     */
    IASN1ObjectIdentifier getData();
}
