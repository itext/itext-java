package com.itextpdf.bouncycastlefips.asn1.esf;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1OctetStringBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.AlgorithmIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.esf.IOtherHashAlgAndValue;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import org.bouncycastle.asn1.esf.OtherHashAlgAndValue;

/**
 * Wrapper class for {@link OtherHashAlgAndValue}.
 */
public class OtherHashAlgAndValueBCFips extends ASN1EncodableBCFips implements IOtherHashAlgAndValue {
    /**
     * Creates new wrapper instance for {@link OtherHashAlgAndValue}.
     *
     * @param otherHashAlgAndValue {@link OtherHashAlgAndValue} to be wrapped
     */
    public OtherHashAlgAndValueBCFips(OtherHashAlgAndValue otherHashAlgAndValue) {
        super(otherHashAlgAndValue);
    }

    /**
     * Creates new wrapper instance for {@link OtherHashAlgAndValue}.
     *
     * @param algorithmIdentifier AlgorithmIdentifier wrapper
     * @param octetString         ASN1OctetString wrapper
     */
    public OtherHashAlgAndValueBCFips(IAlgorithmIdentifier algorithmIdentifier, IASN1OctetString octetString) {
        this(new OtherHashAlgAndValue(
                ((AlgorithmIdentifierBCFips) algorithmIdentifier).getAlgorithmIdentifier(),
                ((ASN1OctetStringBCFips) octetString).getOctetString()));
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
