package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.ICRLReason;

import org.bouncycastle.asn1.x509.CRLReason;

public class CRLReasonBC extends ASN1EncodableBC implements ICRLReason {
    private static final CRLReasonBC INSTANCE = new CRLReasonBC(null);
    
    private static final int KEY_COMPROMISE = CRLReason.keyCompromise;
    
    public CRLReasonBC(CRLReason reason) {
        super(reason);
    }
    
    public static CRLReasonBC getInstance() {
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
