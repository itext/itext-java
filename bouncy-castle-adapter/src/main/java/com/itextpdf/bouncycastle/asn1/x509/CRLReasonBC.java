package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.ICRLReason;

import org.bouncycastle.asn1.x509.CRLReason;

/**
 * Wrapper class for {@link CRLReason}.
 */
public class CRLReasonBC extends ASN1EncodableBC implements ICRLReason {
    private static final CRLReasonBC INSTANCE = new CRLReasonBC(null);

    private static final int KEY_COMPROMISE = CRLReason.keyCompromise;

    /**
     * Creates new wrapper instance for {@link CRLReason}.
     *
     * @param reason {@link CRLReason} to be wrapped
     */
    public CRLReasonBC(CRLReason reason) {
        super(reason);
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link CRLReasonBC} instance.
     */
    public static CRLReasonBC getInstance() {
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
