package com.itextpdf.bouncycastlefips.asn1.cms;

import com.itextpdf.bouncycastlefips.asn1.ASN1SetBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttribute;

import org.bouncycastle.asn1.cms.Attribute;

public class AttributeBCFips implements IAttribute {
    private final final Attribute attribute;

    public AttributeBCFips(Attribute attribute) {
        this.attribute = attribute;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    @Override
    public IASN1Set getAttrValues() {
        return new ASN1SetBCFips(attribute.getAttrValues());
    }
}
