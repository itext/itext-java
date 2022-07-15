package com.itextpdf.bouncycastlefips.asn1.esf;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.bouncycastlefips.asn1.DERIA5StringBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IDERIA5String;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISigPolicyQualifierInfo;

import org.bouncycastle.asn1.esf.SigPolicyQualifierInfo;

/**
 * Wrapper class for {@link SigPolicyQualifierInfo}.
 */
public class SigPolicyQualifierInfoBCFips extends ASN1EncodableBCFips implements ISigPolicyQualifierInfo {
    /**
     * Creates new wrapper instance for {@link SigPolicyQualifierInfo}.
     *
     * @param qualifierInfo {@link SigPolicyQualifierInfo} to be wrapped
     */
    public SigPolicyQualifierInfoBCFips(SigPolicyQualifierInfo qualifierInfo) {
        super(qualifierInfo);
    }

    /**
     * Creates new wrapper instance for {@link SigPolicyQualifierInfo}.
     *
     * @param objectIdentifier ASN1ObjectIdentifier wrapper
     * @param string           DERIA5String wrapper
     */
    public SigPolicyQualifierInfoBCFips(IASN1ObjectIdentifier objectIdentifier, IDERIA5String string) {
        this(new SigPolicyQualifierInfo(
                ((ASN1ObjectIdentifierBCFips) objectIdentifier).getASN1ObjectIdentifier(),
                ((DERIA5StringBCFips) string).getDerIA5String()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link SigPolicyQualifierInfo}.
     */
    public SigPolicyQualifierInfo getQualifierInfo() {
        return (SigPolicyQualifierInfo) getEncodable();
    }
}
