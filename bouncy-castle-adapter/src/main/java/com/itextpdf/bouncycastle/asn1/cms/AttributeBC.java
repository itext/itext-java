package com.itextpdf.bouncycastle.asn1.cms;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1SetBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttribute;

import org.bouncycastle.asn1.cms.Attribute;

public class AttributeBC extends ASN1EncodableBC implements IAttribute {
    public AttributeBC(Attribute attribute) {
        super(attribute);
    }

    public Attribute getAttribute() {
        return (Attribute) getEncodable();
    }

    @Override
    public IASN1Set getAttrValues() {
        return new ASN1SetBC(getAttribute().getAttrValues());
    }
}
