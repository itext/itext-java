package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDERSequence;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSequence;

/**
 * Wrapper class for {@link DERSequence}.
 */
public class DERSequenceBC extends ASN1SequenceBC implements IDERSequence {
    /**
     * Creates new wrapper instance for {@link DERSequence}.
     *
     * @param derSequence {@link DERSequence} to be wrapped
     */
    public DERSequenceBC(DERSequence derSequence) {
        super(derSequence);
    }

    /**
     * Creates new wrapper instance for {@link DERSequence}.
     *
     * @param vector {@link ASN1EncodableVector} to create {@link DERSequence}
     */
    public DERSequenceBC(ASN1EncodableVector vector) {
        super(new DERSequence(vector));
    }

    /**
     * Creates new wrapper instance for {@link DERSequence}.
     *
     * @param encodable {@link ASN1Encodable} to create {@link DERSequence}
     */
    public DERSequenceBC(ASN1Encodable encodable) {
        super(new DERSequence(encodable));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link DERSequence}.
     */
    public DERSequence getDERSequence() {
        return (DERSequence) getEncodable();
    }
}
