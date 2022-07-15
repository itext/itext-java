package com.itextpdf.bouncycastlefips.asn1.cms;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.cms.IOriginatorInfo;

import org.bouncycastle.asn1.cms.OriginatorInfo;

/**
 * Wrapper class for {@link OriginatorInfo}.
 */
public class OriginatorInfoBCFips extends ASN1EncodableBCFips implements IOriginatorInfo {
    /**
     * Creates new wrapper instance for {@link OriginatorInfo}.
     *
     * @param originatorInfo {@link OriginatorInfo} to be wrapped
     */
    public OriginatorInfoBCFips(OriginatorInfo originatorInfo) {
        super(originatorInfo);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link OriginatorInfo}.
     */
    public OriginatorInfo getOriginatorInfo() {
        return (OriginatorInfo) getEncodable();
    }
}
