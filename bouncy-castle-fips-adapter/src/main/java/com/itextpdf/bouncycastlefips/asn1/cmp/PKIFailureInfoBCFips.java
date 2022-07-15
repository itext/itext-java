package com.itextpdf.bouncycastlefips.asn1.cmp;

import com.itextpdf.bouncycastlefips.asn1.ASN1PrimitiveBCFips;
import com.itextpdf.commons.bouncycastle.asn1.cmp.IPKIFailureInfo;

import org.bouncycastle.asn1.cmp.PKIFailureInfo;

/**
 * Wrapper class for {@link PKIFailureInfo}.
 */
public class PKIFailureInfoBCFips extends ASN1PrimitiveBCFips implements IPKIFailureInfo {
    /**
     * Creates new wrapper instance for {@link PKIFailureInfo}.
     *
     * @param pkiFailureInfo {@link PKIFailureInfo} to be wrapped
     */
    public PKIFailureInfoBCFips(PKIFailureInfo pkiFailureInfo) {
        super(pkiFailureInfo);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link PKIFailureInfo}.
     */
    public PKIFailureInfo getPkiFailureInfo() {
        return (PKIFailureInfo) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int intValue() {
        return getPkiFailureInfo().intValue();
    }
}
