package com.itextpdf.signatures.validation.v1.context;

import java.util.EnumSet;

/**
 * Container class, which contains set of single {@link CertificateSource} values.
 */
public final class CertificateSources {
    private final EnumSet<CertificateSource> set;

    private CertificateSources(EnumSet<CertificateSource> set) {
        this.set = set;
    }

    /**
     * Creates {@link CertificateSources} container from several {@link CertificateSource} values.
     *
     * @param first an element that the set is to contain initially
     * @param rest the remaining elements the set is to contain
     *
     * @return {@link CertificateSources} container, containing provided elements
     */
    public static CertificateSources of(CertificateSource first,  CertificateSource ... rest) {
        return new CertificateSources(EnumSet.<CertificateSource>of(first, rest));
    }

    /**
     * Creates {@link CertificateSources} containing all {@link CertificateSource} values.
     *
     * @return {@link CertificateSources} container containing all {@link CertificateSource} values
     */
    public static CertificateSources all() {
        return new CertificateSources(EnumSet.<CertificateSource>allOf(CertificateSource.class));
    }

    /**
     * Creates {@link CertificateSources} containing all the elements of this type
     * that are not contained in the specified set.
     *
     * @param other another {@link CertificateSources} from whose complement to initialize this container
     *
     * @return the complement of the specified {@link CertificateSources}.
     */
    public static CertificateSources complementOf(CertificateSources other) {
        EnumSet<CertificateSource> result = EnumSet.<CertificateSource>complementOf(other.set);
        if (result.isEmpty()) {
            throw new IllegalArgumentException("CertificateSources all has no valid complement.");
        }
        return new CertificateSources(result);
    }

    /**
     * Gets encapsulated {@link EnumSet} containing {@link CertificateSource} elements.
     *
     * @return encapsulated {@link EnumSet} containing {@link CertificateSource} elements
     */
    public EnumSet<CertificateSource> getSet() {
        return set;
    }
}
