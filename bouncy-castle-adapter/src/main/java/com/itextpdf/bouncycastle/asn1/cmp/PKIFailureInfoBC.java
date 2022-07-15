package com.itextpdf.bouncycastle.asn1.cmp;

import com.itextpdf.bouncycastle.asn1.ASN1PrimitiveBC;
import com.itextpdf.commons.bouncycastle.asn1.cmp.IPKIFailureInfo;

import org.bouncycastle.asn1.cmp.PKIFailureInfo;

/**
 * Wrapper class for {@link PKIFailureInfo}.
 */
public class PKIFailureInfoBC extends ASN1PrimitiveBC implements IPKIFailureInfo {
    /**
     * Creates new wrapper instance for {@link PKIFailureInfo}.
     *
     * @param pkiFailureInfo {@link PKIFailureInfo} to be wrapped
     */
    public PKIFailureInfoBC(PKIFailureInfo pkiFailureInfo) {
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
