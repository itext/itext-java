package com.itextpdf.bouncycastle.asn1.cms;

import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttribute;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttributeTable;

import java.util.Objects;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.AttributeTable;

public class AttributeTableBC implements IAttributeTable {
    private final AttributeTable attributeTable;

    public AttributeTableBC(AttributeTable attributeTable) {
        this.attributeTable = attributeTable;
    }

    public AttributeTableBC(ASN1Set unat) {
        attributeTable = new AttributeTable(unat);
    }

    public AttributeTable getAttributeTable() {
        return attributeTable;
    }

    @Override
    public IAttribute get(IASN1ObjectIdentifier oid) {
        ASN1ObjectIdentifierBC asn1ObjectIdentifier = (ASN1ObjectIdentifierBC) oid;
        return new AttributeBC(attributeTable.get(asn1ObjectIdentifier.getASN1ObjectIdentifier()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AttributeTableBC that = (AttributeTableBC) o;
        return Objects.equals(attributeTable, that.attributeTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributeTable);
    }

    @Override
    public String toString() {
        return attributeTable.toString();
    }
}
