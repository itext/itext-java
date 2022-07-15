package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.x509.IDistributionPointName;

import org.bouncycastle.asn1.x509.DistributionPointName;

/**
 * Wrapper class for {@link DistributionPointName}.
 */
public class DistributionPointNameBCFips extends ASN1EncodableBCFips implements IDistributionPointName {
    private static final DistributionPointNameBCFips INSTANCE = new DistributionPointNameBCFips(null);

    private static final int FULL_NAME = DistributionPointName.FULL_NAME;

    /**
     * Creates new wrapper instance for {@link DistributionPointName}.
     *
     * @param distributionPointName {@link DistributionPointName} to be wrapped
     */
    public DistributionPointNameBCFips(DistributionPointName distributionPointName) {
        super(distributionPointName);
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link DistributionPointNameBCFips} instance.
     */
    public static DistributionPointNameBCFips getInstance() {
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
        return new ASN1EncodableBCFips(getDistributionPointName().getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFullName() {
        return FULL_NAME;
    }
}
