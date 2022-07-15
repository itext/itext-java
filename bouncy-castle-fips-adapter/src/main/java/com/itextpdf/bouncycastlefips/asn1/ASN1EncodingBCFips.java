package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encoding;

import java.util.Objects;
import org.bouncycastle.asn1.ASN1Encoding;

/**
 * Wrapper class for {@link ASN1Encoding}.
 */
public class ASN1EncodingBCFips implements IASN1Encoding {
    private static final ASN1EncodingBCFips INSTANCE = new ASN1EncodingBCFips(null);

    private final ASN1Encoding asn1Encoding;

    /**
     * Creates new wrapper instance for {@link ASN1Encoding}.
     *
     * @param asn1Encoding {@link ASN1Encoding} to be wrapped
     */
    public ASN1EncodingBCFips(ASN1Encoding asn1Encoding) {
        this.asn1Encoding = asn1Encoding;
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link ASN1EncodingBCFips} instance.
     */
    public static ASN1EncodingBCFips getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1Encoding}.
     */
    public ASN1Encoding getAsn1Encoding() {
        return asn1Encoding;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDer() {
        return ASN1Encoding.DER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDl() {
        return ASN1Encoding.DL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBer() {
        return ASN1Encoding.BER;
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
        ASN1EncodingBCFips that = (ASN1EncodingBCFips) o;
        return Objects.equals(asn1Encoding, that.asn1Encoding);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(asn1Encoding);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return asn1Encoding.toString();
    }
}
