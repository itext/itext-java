package com.itextpdf.bouncycastle.asn1.esf;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.bouncycastle.asn1.DERIA5StringBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IDERIA5String;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISigPolicyQualifierInfo;

import org.bouncycastle.asn1.esf.SigPolicyQualifierInfo;

/**
 * Wrapper class for {@link SigPolicyQualifierInfo}.
 */
public class SigPolicyQualifierInfoBC extends ASN1EncodableBC implements ISigPolicyQualifierInfo {
    /**
     * Creates new wrapper instance for {@link SigPolicyQualifierInfo}.
     *
     * @param qualifierInfo {@link SigPolicyQualifierInfo} to be wrapped
     */
    public SigPolicyQualifierInfoBC(SigPolicyQualifierInfo qualifierInfo) {
        super(qualifierInfo);
    }

    /**
     * Creates new wrapper instance for {@link SigPolicyQualifierInfo}.
     *
     * @param objectIdentifier ASN1ObjectIdentifier wrapper
     * @param string           DERIA5String wrapper
     */
    public SigPolicyQualifierInfoBC(IASN1ObjectIdentifier objectIdentifier, IDERIA5String string) {
        this(new SigPolicyQualifierInfo(
                ((ASN1ObjectIdentifierBC) objectIdentifier).getASN1ObjectIdentifier(),
                ((DERIA5StringBC) string).getDerIA5String()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link SigPolicyQualifierInfo}.
     */
    public SigPolicyQualifierInfo getSigPolicyQualifierInfo() {
        return (SigPolicyQualifierInfo) getEncodable();
    }
}
