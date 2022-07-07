package com.itextpdf.bouncycastlefips.asn1.cms;

import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttribute;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttributeTable;

import java.util.Objects;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.AttributeTable;

public class AttributeTableBCFips implements IAttributeTable {
    private final AttributeTable attributeTable;

    public AttributeTableBCFips(AttributeTable attributeTable) {
        this.attributeTable = attributeTable;
    }

    public AttributeTableBCFips(ASN1Set unat) {
        attributeTable = new AttributeTable(unat);
    }

    public AttributeTable getAttributeTable() {
        return attributeTable;
    }

    @Override
    public IAttribute get(IASN1ObjectIdentifier oid) {
        ASN1ObjectIdentifierBCFips asn1ObjectIdentifier = (ASN1ObjectIdentifierBCFips) oid;
        return new AttributeBCFips(attributeTable.get(asn1ObjectIdentifier.getASN1ObjectIdentifier()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AttributeTableBCFips that = (AttributeTableBCFips) o;
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
