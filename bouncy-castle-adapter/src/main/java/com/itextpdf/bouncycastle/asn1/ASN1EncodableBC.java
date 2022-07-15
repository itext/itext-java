package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;

import java.util.Objects;
import org.bouncycastle.asn1.ASN1Encodable;

/**
 * Wrapper class for {@link ASN1Encodable}.
 */
public class ASN1EncodableBC implements IASN1Encodable {
    private final ASN1Encodable encodable;

    /**
     * Creates new wrapper instance for {@link ASN1Encodable}.
     *
     * @param encodable {@link ASN1Encodable} to be wrapped
     */
    public ASN1EncodableBC(ASN1Encodable encodable) {
        this.encodable = encodable;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1Encodable}.
     */
    public ASN1Encodable getEncodable() {
        return encodable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Primitive toASN1Primitive() {
        return new ASN1PrimitiveBC(encodable.toASN1Primitive());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNull() {
        return encodable == null;
    }

    /**
     * Indicates whether some other object is "equal to" this one. Compares wrapped objects.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ASN1EncodableBC that = (ASN1EncodableBC) o;
        return Objects.equals(encodable, that.encodable);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(encodable);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return encodable.toString();
    }
}
