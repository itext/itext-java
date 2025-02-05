/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
