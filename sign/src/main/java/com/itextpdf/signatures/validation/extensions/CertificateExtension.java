package com.itextpdf.signatures.validation.extensions;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.signatures.CertificateUtil;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Objects;

/**
 * Class representing certificate extension with all the information required for validation.
 */
public class CertificateExtension {

    private final String extensionOid;
    private final IASN1Primitive extensionValue;

    /**
     * Create new instance of {@link CertificateExtension} using provided extension OID and value.
     *
     * @param extensionOid {@link String}, which represents extension OID
     * @param extensionValue {@link IASN1Primitive}, which represents extension value
     */
    public CertificateExtension(String extensionOid, IASN1Primitive extensionValue) {
        this.extensionOid = extensionOid;
        this.extensionValue = extensionValue;
    }

    /**
     * Get extension value
     *
     * @return {@link IASN1Primitive}, which represents extension value
     */
    public IASN1Primitive getExtensionValue() {
        return extensionValue;
    }

    /**
     * Get extension OID
     *
     * @return {@link String}, which represents extension OID
     */
    public String getExtensionOid() {
        return extensionOid;
    }

    /**
     * Check if this extension is present in the provided certificate.
     * <p>
     * This method doesn't always require complete extension value equality,
     * instead whenever possible it checks that this extension is present in the certificate.
     *
     * @param certificate {@link X509Certificate} in which this extension shall be present
     *
     * @return {@code true} if extension if present, {@code false} otherwise
     */
    public boolean existsInCertificate(X509Certificate certificate) {
        IASN1Primitive providedExtensionValue;
        try {
            providedExtensionValue = CertificateUtil.getExtensionValue(certificate, extensionOid);
        } catch (IOException e) {
            return false;
        }
        return Objects.equals(providedExtensionValue, extensionValue);
    }
}
