package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDERTaggedObject;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;

/**
 * Wrapper class for {@link DERTaggedObject}.
 */
public class DERTaggedObjectBCFips extends ASN1TaggedObjectBCFips implements IDERTaggedObject {
    /**
     * Creates new wrapper instance for {@link DERTaggedObject}.
     *
     * @param derTaggedObject {@link DERTaggedObject} to be wrapped
     */
    public DERTaggedObjectBCFips(DERTaggedObject derTaggedObject) {
        super(derTaggedObject);
    }

    /**
     * Creates new wrapper instance for {@link DERTaggedObject}.
     *
     * @param i         int value to create {@link DERTaggedObject} to be wrapped
     * @param encodable {@link ASN1Encodable} to create {@link DERTaggedObject} to be wrapped
     */
    public DERTaggedObjectBCFips(int i, ASN1Encodable encodable) {
        super(new DERTaggedObject(i, encodable));
    }

    /**
     * Creates new wrapper instance for {@link DERTaggedObject}.
     *
     * @param b         boolean to create {@link DERTaggedObject} to be wrapped
     * @param i         int value to create {@link DERTaggedObject} to be wrapped
     * @param encodable {@link ASN1Encodable} to create {@link DERTaggedObject} to be wrapped
     */
    public DERTaggedObjectBCFips(boolean b, int i, ASN1Encodable encodable) {
        super(new DERTaggedObject(b, i, encodable));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link DERTaggedObject}.
     */
    public DERTaggedObject getDERTaggedObject() {
        return (DERTaggedObject) getEncodable();
    }
}
