package com.itextpdf.signatures.validation.extensions;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.x509.IKeyPurposeId;
import com.itextpdf.signatures.OID;

import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.List;

/**
 * Class representing "Extended Key Usage" extension.
 */
public class ExtendedKeyUsageExtension extends CertificateExtension {
    public static final String ANY_EXTENDED_KEY_USAGE_OID = "2.5.29.37.0";
    public static final String TIME_STAMPING = "1.3.6.1.5.5.7.3.8";
    public static final String OCSP_SIGNING = "1.3.6.1.5.5.7.3.9";
    public static final String CODE_SIGNING = "1.3.6.1.5.5.7.3.3";
    public static final String CLIENT_AUTH = "1.3.6.1.5.5.7.3.2";

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private final List<String> extendedKeyUsageOids;

    /**
     * Create new {@link ExtendedKeyUsageExtension} instance.
     *
     * @param extendedKeyUsageOids strings {@link List}, representing extended key usages OIDs
     */
    public ExtendedKeyUsageExtension(List<String> extendedKeyUsageOids) {
        super(OID.X509Extensions.EXTENDED_KEY_USAGE, FACTORY.createExtendedKeyUsage(
                createKeyPurposeIds(extendedKeyUsageOids)).toASN1Primitive());
        this.extendedKeyUsageOids = extendedKeyUsageOids;
    }

    /**
     * Check if this extension is present in the provided certificate. In case of {@link ExtendedKeyUsageExtension},
     * check if this extended key usage OIDs are present. Other values may be present as well.
     *
     * @param certificate {@link X509Certificate} in which this extension shall be present
     *
     * @return {@code true} if all OIDs are present in certificate extension, {@code false} otherwise
     */
    @Override
    public boolean existsInCertificate(X509Certificate certificate) {
        List<String> providedExtendedKeyUsage;
        try {
            providedExtendedKeyUsage = certificate.getExtendedKeyUsage();
        } catch (CertificateParsingException e) {
            return false;
        }
        if (providedExtendedKeyUsage == null) {
            return false;
        }
        return providedExtendedKeyUsage.contains(ANY_EXTENDED_KEY_USAGE_OID) ||
                new HashSet<>(providedExtendedKeyUsage).containsAll(extendedKeyUsageOids);
    }

    private static IKeyPurposeId[] createKeyPurposeIds(List<String> extendedKeyUsageOids) {
        IKeyPurposeId[] keyPurposeIds = new IKeyPurposeId[extendedKeyUsageOids.size()];
        for (int i = 0; i < extendedKeyUsageOids.size(); ++i) {
            keyPurposeIds[i] =
                    FACTORY.createKeyPurposeId(FACTORY.createASN1ObjectIdentifier(extendedKeyUsageOids.get(i)));
        }
        return keyPurposeIds;
    }
}
