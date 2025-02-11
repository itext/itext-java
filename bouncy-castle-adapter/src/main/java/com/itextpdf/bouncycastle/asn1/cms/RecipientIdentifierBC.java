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

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.cms.IIssuerAndSerialNumber;
import com.itextpdf.commons.bouncycastle.asn1.cms.IRecipientIdentifier;

import org.bouncycastle.asn1.cms.RecipientIdentifier;

/**
 * Wrapper class for {@link RecipientIdentifier}.
 */
public class RecipientIdentifierBC extends ASN1EncodableBC implements IRecipientIdentifier {
    /**
     * Creates new wrapper instance for {@link RecipientIdentifier}.
     *
     * @param recipientIdentifier {@link RecipientIdentifier} to be wrapped
     */
    public RecipientIdentifierBC(RecipientIdentifier recipientIdentifier) {
        super(recipientIdentifier);
    }

    /**
     * Creates new wrapper instance for {@link RecipientIdentifier}.
     *
     * @param issuerAndSerialNumber IssuerAndSerialNumber wrapper to create {@link RecipientIdentifier}
     */
    public RecipientIdentifierBC(IIssuerAndSerialNumber issuerAndSerialNumber) {
        super(new RecipientIdentifier(((IssuerAndSerialNumberBC) issuerAndSerialNumber).getIssuerAndSerialNumber()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link RecipientIdentifier}.
     */
    public RecipientIdentifier getRecipientIdentifier() {
        return (RecipientIdentifier) getEncodable();
    }
}
