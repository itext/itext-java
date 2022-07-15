package com.itextpdf.bouncycastle.asn1.cms;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.cms.IOriginatorInfo;

import org.bouncycastle.asn1.cms.OriginatorInfo;

/**
 * Wrapper class for {@link OriginatorInfo}.
 */
public class OriginatorInfoBC extends ASN1EncodableBC implements IOriginatorInfo {
    /**
     * Creates new wrapper instance for {@link OriginatorInfo}.
     *
     * @param originatorInfo {@link OriginatorInfo} to be wrapped
     */
    public OriginatorInfoBC(OriginatorInfo originatorInfo) {
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
