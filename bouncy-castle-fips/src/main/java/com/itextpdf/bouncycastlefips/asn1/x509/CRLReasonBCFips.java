package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.ICRLReason;

import org.bouncycastle.asn1.x509.CRLReason;

public class CRLReasonBCFips extends ASN1EncodableBCFips implements ICRLReason {
    private static final CRLReasonBCFips INSTANCE = new CRLReasonBCFips(null);

    private static final int KEY_COMPROMISE = CRLReason.keyCompromise;

    public CRLReasonBCFips(CRLReason reason) {
        super(reason);
    }

    public static CRLReasonBCFips getInstance() {
        return INSTANCE;
    }

    public CRLReason getCRLReason() {
        return (CRLReason) getEncodable();
    }

    @Override
    public int getKeyCompromise() {
        return KEY_COMPROMISE;
    }
}
