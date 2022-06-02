package com.itextpdf.bouncycastle.asn1.cms;

import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttribute;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttributeTable;

import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.AttributeTable;

public class AttributeTableBC implements IAttributeTable {
    private final AttributeTable attributeTable;

    public AttributeTableBC(ASN1Set unat) {
        attributeTable = new AttributeTable(unat);
    }

    public AttributeTable getAttributeTable() {
        return attributeTable;
    }

    @Override
    public IAttribute get(IASN1ObjectIdentifier oid) {
        ASN1ObjectIdentifierBC asn1ObjectIdentifier = (ASN1ObjectIdentifierBC) oid;
        return new AttributeBC(attributeTable.get(asn1ObjectIdentifier.getObjectIdentifier()));
    }
}
