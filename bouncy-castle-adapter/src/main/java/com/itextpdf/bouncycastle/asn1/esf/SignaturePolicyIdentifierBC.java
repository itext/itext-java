package com.itextpdf.bouncycastle.asn1.esf;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyId;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyIdentifier;

import org.bouncycastle.asn1.esf.SignaturePolicyIdentifier;

/**
 * Wrapper class for {@link SignaturePolicyIdentifier}.
 */
public class SignaturePolicyIdentifierBC extends ASN1EncodableBC implements ISignaturePolicyIdentifier {
    /**
     * Creates new wrapper instance for {@link SignaturePolicyIdentifier}.
     *
     * @param signaturePolicyIdentifier {@link SignaturePolicyIdentifier} to be wrapped
     */
    public SignaturePolicyIdentifierBC(SignaturePolicyIdentifier signaturePolicyIdentifier) {
        super(signaturePolicyIdentifier);
    }

    /**
     * Creates new wrapper instance for {@link SignaturePolicyIdentifier}.
     *
     * @param signaturePolicyId SignaturePolicyId wrapper
     */
    public SignaturePolicyIdentifierBC(ISignaturePolicyId signaturePolicyId) {
        this(new SignaturePolicyIdentifier(((SignaturePolicyIdBC) signaturePolicyId).getSignaturePolicyId()));
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
