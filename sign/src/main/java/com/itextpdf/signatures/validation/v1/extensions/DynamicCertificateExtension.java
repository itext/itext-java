package com.itextpdf.signatures.validation.v1.extensions;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;

/**
 * Certificate extension which is populated with additional dynamically changing validation related information.
 */
public class DynamicCertificateExtension extends CertificateExtension {

    private int certificateChainSize;

    /**
     * Create new instance of {@link CertificateExtension} using provided extension OID and value.
     *
     * @param extensionOid   {@link String}, which represents extension OID
     * @param extensionValue {@link IASN1Primitive}, which represents extension value
     */
    public DynamicCertificateExtension(String extensionOid, IASN1Primitive extensionValue) {
        super(extensionOid, extensionValue);
    }

    /**
     * Sets amount of certificates currently present in the chain.
     *
     * @param certificateChainSize amount of certificates currently present in the chain
     *
     * @return this {@link DynamicCertificateExtension} instance
     */
    public DynamicCertificateExtension withCertificateChainSize(int certificateChainSize) {
        this.certificateChainSize = certificateChainSize;
        return this;
    }

    /**
     * Gets amount of certificates currently present in the chain.
     *
     * @return amount of certificates currently present in the chain
     */
    public int getCertificateChainSize() {
        return certificateChainSize;
    }
}
