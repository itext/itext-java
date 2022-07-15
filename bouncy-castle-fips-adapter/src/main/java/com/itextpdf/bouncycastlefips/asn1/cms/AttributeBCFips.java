package com.itextpdf.bouncycastlefips.asn1.cms;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1SetBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttribute;

import org.bouncycastle.asn1.cms.Attribute;

/**
 * Wrapper class for {@link Attribute}.
 */
public class AttributeBCFips extends ASN1EncodableBCFips implements IAttribute {
    /**
     * Creates new wrapper instance for {@link Attribute}.
     *
     * @param attribute {@link Attribute} to be wrapped
     */
    public AttributeBCFips(Attribute attribute) {
        super(attribute);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link Attribute}.
     */
    public Attribute getAttribute() {
        return (Attribute) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Set getAttrValues() {
        return new ASN1SetBCFips(getAttribute().getAttrValues());
    }
}
