package com.itextpdf.bouncycastle.asn1.esf;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1OctetStringBC;
import com.itextpdf.bouncycastle.asn1.x509.AlgorithmIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.esf.IOtherHashAlgAndValue;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import org.bouncycastle.asn1.esf.OtherHashAlgAndValue;

/**
 * Wrapper class for {@link OtherHashAlgAndValue}.
 */
public class OtherHashAlgAndValueBC extends ASN1EncodableBC implements IOtherHashAlgAndValue {
    /**
     * Creates new wrapper instance for {@link OtherHashAlgAndValue}.
     *
     * @param otherHashAlgAndValue {@link OtherHashAlgAndValue} to be wrapped
     */
    public OtherHashAlgAndValueBC(OtherHashAlgAndValue otherHashAlgAndValue) {
        super(otherHashAlgAndValue);
    }

    /**
     * Creates new wrapper instance for {@link OtherHashAlgAndValue}.
     *
     * @param algorithmIdentifier AlgorithmIdentifier wrapper
     * @param octetString         ASN1OctetString wrapper
     */
    public OtherHashAlgAndValueBC(IAlgorithmIdentifier algorithmIdentifier, IASN1OctetString octetString) {
        this(new OtherHashAlgAndValue(
                ((AlgorithmIdentifierBC) algorithmIdentifier).getAlgorithmIdentifier(),
                ((ASN1OctetStringBC) octetString).getASN1OctetString()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link OtherHashAlgAndValue}.
     */
    public OtherHashAlgAndValue getOtherHashAlgAndValue() {
        return (OtherHashAlgAndValue) getEncodable();
    }
}
