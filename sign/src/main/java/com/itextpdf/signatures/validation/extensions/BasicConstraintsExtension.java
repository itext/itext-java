package com.itextpdf.signatures.validation.extensions;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.OID;

import java.io.IOException;
import java.security.cert.X509Certificate;

/**
 * Class representing "Basic Constraints" certificate extension.
 */
public class BasicConstraintsExtension extends CertificateExtension {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private final int pathLength;

    /**
     * Create new {@link BasicConstraintsExtension} instance using provided {@code boolean} value.
     *
     * @param ca {@code boolean} value, which represents if this certificate is a "Certificate Authority"
     */
    public BasicConstraintsExtension(boolean ca) {
        super(OID.X509Extensions.BASIC_CONSTRAINTS, FACTORY.createBasicConstraints(ca).toASN1Primitive());
        if (ca) {
            this.pathLength = Integer.MAX_VALUE;
        } else {
            this.pathLength = -1;
        }
    }

    /**
     * Create new {@link BasicConstraintsExtension} instance using provided {@code int} path length.
     *
     * @param pathLength {@code int} value, which represents acceptable path length for this certificate as a "CA"
     */
    public BasicConstraintsExtension(int pathLength) {
        super(OID.X509Extensions.BASIC_CONSTRAINTS, FACTORY.createBasicConstraints(pathLength).toASN1Primitive());
        this.pathLength = pathLength;
    }

    /**
     * Check if this extension is present in the provided certificate. In case of {@link BasicConstraintsExtension},
     * check if path length for this extension is less or equal to the path length, specified in the certificate.
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
        if (pathLength >= 0) {
            return certificate.getBasicConstraints() >= pathLength;
        }
        return certificate.getBasicConstraints() < 0;
    }
}
