package com.itextpdf.signatures.validation.v1.extensions;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.OID;

import java.io.IOException;
import java.security.cert.X509Certificate;

/**
 * Class representing "Basic Constraints" certificate extension,
 * which uses provided amount of certificates in chain during the comparison.
 */
public class DynamicBasicConstraintsExtension extends DynamicCertificateExtension {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    /**
     * Create new instance of {@link DynamicBasicConstraintsExtension}.
     */
    public DynamicBasicConstraintsExtension() {
        super(OID.X509Extensions.BASIC_CONSTRAINTS, FACTORY.createBasicConstraints(true).toASN1Primitive());
    }

    /**
     * Check if this extension is present in the provided certificate.
     * In case of {@link DynamicBasicConstraintsExtension}, check if path length for this extension is less or equal
     * to the path length, specified in the certificate.
     *
     * @param certificate {@link X509Certificate} in which this extension shall be present
     *
     * @return {@code true} if this path length is less or equal to a one from the certificate, {@code false} otherwise
     */
    @Override
    public boolean existsInCertificate(X509Certificate certificate) {
        try {
            if (CertificateUtil.getExtensionValue(certificate, OID.X509Extensions.BASIC_CONSTRAINTS) == null) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        return certificate.getBasicConstraints() >= getCertificateChainSize();
    }
}
