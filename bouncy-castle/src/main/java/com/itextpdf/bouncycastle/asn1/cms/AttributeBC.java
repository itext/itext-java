package com.itextpdf.bouncycastle.asn1.cms;

import com.itextpdf.bouncycastle.asn1.ASN1SetBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttribute;

import org.bouncycastle.asn1.cms.Attribute;

public class AttributeBC implements IAttribute {
    private final Attribute attribute;

    public AttributeBC(Attribute attribute) {
        this.attribute = attribute;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    @Override
    public IASN1Set getAttrValues() {
        return new ASN1SetBC(attribute.getAttrValues());
    }
}
