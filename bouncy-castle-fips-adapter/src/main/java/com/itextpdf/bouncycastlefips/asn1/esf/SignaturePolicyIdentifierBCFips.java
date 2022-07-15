package com.itextpdf.bouncycastlefips.asn1.esf;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyId;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyIdentifier;

import org.bouncycastle.asn1.esf.SignaturePolicyIdentifier;

/**
 * Wrapper class for {@link SignaturePolicyIdentifier}.
 */
public class SignaturePolicyIdentifierBCFips extends ASN1EncodableBCFips implements ISignaturePolicyIdentifier {
    /**
     * Creates new wrapper instance for {@link SignaturePolicyIdentifier}.
     *
     * @param signaturePolicyIdentifier {@link SignaturePolicyIdentifier} to be wrapped
     */
    public SignaturePolicyIdentifierBCFips(SignaturePolicyIdentifier signaturePolicyIdentifier) {
        super(signaturePolicyIdentifier);
    }

    /**
     * Creates new wrapper instance for {@link SignaturePolicyIdentifier}.
     *
     * @param signaturePolicyId SignaturePolicyId wrapper
     */
    public SignaturePolicyIdentifierBCFips(ISignaturePolicyId signaturePolicyId) {
        this(new SignaturePolicyIdentifier(((SignaturePolicyIdBCFips) signaturePolicyId).getSignaturePolicyId()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link SignaturePolicyIdentifier}.
     */
    public SignaturePolicyIdentifier getSignaturePolicyIdentifier() {
        return (SignaturePolicyIdentifier) getEncodable();
    }
}
