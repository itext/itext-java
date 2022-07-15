package com.itextpdf.bouncycastlefips.asn1.esf;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.esf.IOtherHashAlgAndValue;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISigPolicyQualifiers;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyId;

import org.bouncycastle.asn1.esf.SignaturePolicyId;

/**
 * Wrapper class for {@link SignaturePolicyId}.
 */
public class SignaturePolicyIdBCFips extends ASN1EncodableBCFips implements ISignaturePolicyId {
    /**
     * Creates new wrapper instance for {@link SignaturePolicyId}.
     *
     * @param signaturePolicyId {@link SignaturePolicyId} to be wrapped
     */
    public SignaturePolicyIdBCFips(SignaturePolicyId signaturePolicyId) {
        super(signaturePolicyId);
    }

    /**
     * Creates new wrapper instance for {@link SignaturePolicyId}.
     *
     * @param objectIdentifier ASN1ObjectIdentifier wrapper
     * @param algAndValue      OtherHashAlgAndValue wrapper
     * @param policyQualifiers SigPolicyQualifiers wrapper
     */
    public SignaturePolicyIdBCFips(IASN1ObjectIdentifier objectIdentifier,
            IOtherHashAlgAndValue algAndValue, ISigPolicyQualifiers policyQualifiers) {
        this(new SignaturePolicyId(
                ((ASN1ObjectIdentifierBCFips) objectIdentifier).getASN1ObjectIdentifier(),
                ((OtherHashAlgAndValueBCFips) algAndValue).getOtherHashAlgAndValue(),
                ((SigPolicyQualifiersBCFips) policyQualifiers).getSigPolityQualifiers()));
    }

    /**
     * Creates new wrapper instance for {@link SignaturePolicyId}.
     *
     * @param objectIdentifier ASN1ObjectIdentifier wrapper
     * @param algAndValue      OtherHashAlgAndValue wrapper
     */
    public SignaturePolicyIdBCFips(IASN1ObjectIdentifier objectIdentifier,
            IOtherHashAlgAndValue algAndValue) {
        this(new SignaturePolicyId(
                ((ASN1ObjectIdentifierBCFips) objectIdentifier).getASN1ObjectIdentifier(),
                ((OtherHashAlgAndValueBCFips) algAndValue).getOtherHashAlgAndValue()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link SignaturePolicyId}.
     */
    public SignaturePolicyId getSignaturePolicyId() {
        return (SignaturePolicyId) getEncodable();
    }
}
