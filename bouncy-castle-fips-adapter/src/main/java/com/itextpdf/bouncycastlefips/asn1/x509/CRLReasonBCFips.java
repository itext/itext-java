package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.ICRLReason;

import org.bouncycastle.asn1.x509.CRLReason;

/**
 * Wrapper class for {@link CRLReason}.
 */
public class CRLReasonBCFips extends ASN1EncodableBCFips implements ICRLReason {
    private static final CRLReasonBCFips INSTANCE = new CRLReasonBCFips(null);

    private static final int KEY_COMPROMISE = CRLReason.keyCompromise;

    /**
     * Creates new wrapper instance for {@link CRLReason}.
     *
     * @param reason {@link CRLReason} to be wrapped
     */
    public CRLReasonBCFips(CRLReason reason) {
        super(reason);
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link CRLReasonBCFips} instance.
     */
    public static CRLReasonBCFips getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link CRLReason}.
     */
    public CRLReason getCRLReason() {
        return (CRLReason) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getKeyCompromise() {
        return KEY_COMPROMISE;
    }
}
