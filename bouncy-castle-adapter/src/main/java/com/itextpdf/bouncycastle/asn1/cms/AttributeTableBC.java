package com.itextpdf.bouncycastle.asn1.cms;

import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttribute;
import com.itextpdf.commons.bouncycastle.asn1.cms.IAttributeTable;

import java.util.Objects;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.AttributeTable;

/**
 * Wrapper class for {@link AttributeTable}.
 */
public class AttributeTableBC implements IAttributeTable {
    private final AttributeTable attributeTable;

    /**
     * Creates new wrapper instance for {@link AttributeTable}.
     *
     * @param attributeTable {@link AttributeTable} to be wrapped
     */
    public AttributeTableBC(AttributeTable attributeTable) {
        this.attributeTable = attributeTable;
    }

    /**
     * Creates new wrapper instance for {@link AttributeTable}.
     *
     * @param set {@link ASN1Set} to create {@link AttributeTable}
     */
    public AttributeTableBC(ASN1Set set) {
        attributeTable = new AttributeTable(set);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link AttributeTable}.
     */
    public AttributeTable getAttributeTable() {
        return attributeTable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAttribute get(IASN1ObjectIdentifier oid) {
        ASN1ObjectIdentifierBC asn1ObjectIdentifier = (ASN1ObjectIdentifierBC) oid;
        return new AttributeBC(attributeTable.get(asn1ObjectIdentifier.getASN1ObjectIdentifier()));
    }

    /**
     * Indicates whether some other object is "equal to" this one. Compares wrapped objects.
     */
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

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(attributeTable);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return attributeTable.toString();
    }
}
