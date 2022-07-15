package com.itextpdf.bouncycastle.asn1.cms;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1SetBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttribute;

import org.bouncycastle.asn1.cms.Attribute;

/**
 * Wrapper class for {@link Attribute}.
 */
public class AttributeBC extends ASN1EncodableBC implements IAttribute {
    /**
     * Creates new wrapper instance for {@link Attribute}.
     *
     * @param attribute {@link Attribute} to be wrapped
     */
    public AttributeBC(Attribute attribute) {
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
        return new ASN1SetBC(getAttribute().getAttrValues());
    }
}
