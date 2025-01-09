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
package com.itextpdf.bouncycastlefips.asn1.cms;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.cms.IKeyTransRecipientInfo;
import com.itextpdf.commons.bouncycastle.asn1.cms.IRecipientInfo;

import org.bouncycastle.asn1.cms.RecipientInfo;

/**
 * Wrapper class for {@link RecipientInfo}.
 */
public class RecipientInfoBCFips extends ASN1EncodableBCFips implements IRecipientInfo {
    /**
     * Creates new wrapper instance for {@link RecipientInfo}.
     *
     * @param recipientInfo {@link RecipientInfo} to be wrapped
     */
    public RecipientInfoBCFips(RecipientInfo recipientInfo) {
        super(recipientInfo);
    }

    /**
     * Creates new wrapper instance for {@link RecipientInfo}.
     *
     * @param keyTransRecipientInfo KeyTransRecipientInfo to create {@link RecipientInfo}
     */
    public RecipientInfoBCFips(IKeyTransRecipientInfo keyTransRecipientInfo) {
        super(new RecipientInfo(((KeyTransRecipientInfoBCFips) keyTransRecipientInfo).getKeyTransRecipientInfo()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link RecipientInfo}.
     */
    public RecipientInfo getRecipientInfo() {
        return (RecipientInfo) getEncodable();
    }
}
