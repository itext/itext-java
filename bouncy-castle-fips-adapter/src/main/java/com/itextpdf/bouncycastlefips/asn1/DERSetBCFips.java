package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDERSet;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSet;

/**
 * Wrapper class for {@link DERSet}.
 */
public class DERSetBCFips extends ASN1SetBCFips implements IDERSet {
    /**
     * Creates new wrapper instance for {@link DERSet}.
     *
     * @param derSet {@link DERSet} to be wrapped
     */
    public DERSetBCFips(DERSet derSet) {
        super(derSet);
    }

    /**
     * Creates new wrapper instance for {@link DERSet}.
     *
     * @param vector {@link ASN1EncodableVector} to create {@link DERSet}
     */
    public DERSetBCFips(ASN1EncodableVector vector) {
        super(new DERSet(vector));
    }

    /**
     * Creates new wrapper instance for {@link DERSet}.
     *
     * @param encodable {@link ASN1Encodable} to create {@link DERSet}
     */
    public DERSetBCFips(ASN1Encodable encodable) {
        super(new DERSet(encodable));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link DERSet}.
     */
    public DERSet getDERSet() {
        return (DERSet) getEncodable();
    }
}
