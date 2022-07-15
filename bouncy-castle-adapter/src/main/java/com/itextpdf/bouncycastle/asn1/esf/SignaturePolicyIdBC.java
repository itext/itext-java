package com.itextpdf.bouncycastle.asn1.esf;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.esf.IOtherHashAlgAndValue;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISigPolicyQualifiers;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyId;

import org.bouncycastle.asn1.esf.SignaturePolicyId;

/**
 * Wrapper class for {@link SignaturePolicyId}.
 */
public class SignaturePolicyIdBC extends ASN1EncodableBC implements ISignaturePolicyId {
    /**
     * Creates new wrapper instance for {@link SignaturePolicyId}.
     *
     * @param signaturePolicyId {@link SignaturePolicyId} to be wrapped
     */
    public SignaturePolicyIdBC(SignaturePolicyId signaturePolicyId) {
        super(signaturePolicyId);
    }

    /**
     * Creates new wrapper instance for {@link SignaturePolicyId}.
     *
     * @param objectIdentifier ASN1ObjectIdentifier wrapper
     * @param algAndValue      OtherHashAlgAndValue wrapper
     * @param policyQualifiers SigPolicyQualifiers wrapper
     */
    public SignaturePolicyIdBC(IASN1ObjectIdentifier objectIdentifier,
            IOtherHashAlgAndValue algAndValue, ISigPolicyQualifiers policyQualifiers) {
        this(new SignaturePolicyId(
                ((ASN1ObjectIdentifierBC) objectIdentifier).getASN1ObjectIdentifier(),
                ((OtherHashAlgAndValueBC) algAndValue).getOtherHashAlgAndValue(),
                ((SigPolicyQualifiersBC) policyQualifiers).getSigPolityQualifiers()));
    }

    /**
     * Creates new wrapper instance for {@link SignaturePolicyId}.
     *
     * @param objectIdentifier ASN1ObjectIdentifier wrapper
     * @param algAndValue      OtherHashAlgAndValue wrapper
     */
    public SignaturePolicyIdBC(IASN1ObjectIdentifier objectIdentifier, IOtherHashAlgAndValue algAndValue) {
        this(new SignaturePolicyId(
                ((ASN1ObjectIdentifierBC) objectIdentifier).getASN1ObjectIdentifier(),
                ((OtherHashAlgAndValueBC) algAndValue).getOtherHashAlgAndValue()));
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
