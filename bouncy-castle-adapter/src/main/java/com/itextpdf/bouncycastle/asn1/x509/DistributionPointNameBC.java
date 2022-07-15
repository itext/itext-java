package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.x509.IDistributionPointName;

import org.bouncycastle.asn1.x509.DistributionPointName;

/**
 * Wrapper class for {@link DistributionPointName}.
 */
public class DistributionPointNameBC extends ASN1EncodableBC implements IDistributionPointName {
    private static final DistributionPointNameBC INSTANCE = new DistributionPointNameBC(null);

    private static final int FULL_NAME = DistributionPointName.FULL_NAME;

    /**
     * Creates new wrapper instance for {@link DistributionPointName}.
     *
     * @param distributionPointName {@link DistributionPointName} to be wrapped
     */
    public DistributionPointNameBC(DistributionPointName distributionPointName) {
        super(distributionPointName);
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link DistributionPointNameBC} instance.
     */
    public static DistributionPointNameBC getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link DistributionPointName}.
     */
    public DistributionPointName getDistributionPointName() {
        return (DistributionPointName) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getType() {
        return getDistributionPointName().getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Encodable getName() {
        return new ASN1EncodableBC(getDistributionPointName().getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFullName() {
        return FULL_NAME;
    }
}
