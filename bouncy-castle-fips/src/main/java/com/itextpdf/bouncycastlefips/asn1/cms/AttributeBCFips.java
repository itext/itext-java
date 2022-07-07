package com.itextpdf.bouncycastlefips.asn1.cms;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1SetBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttribute;

import org.bouncycastle.asn1.cms.Attribute;

public class AttributeBCFips extends ASN1EncodableBCFips implements IAttribute {
    public AttributeBCFips(Attribute attribute) {
        super(attribute);
    }

    public Attribute getAttribute() {
        return (Attribute) getEncodable();
    }

    @Override
    public IASN1Set getAttrValues() {
        return new ASN1SetBCFips(getAttribute().getAttrValues());
    }
}
