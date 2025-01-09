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
import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.cms.IContentInfo;

import org.bouncycastle.asn1.cms.ContentInfo;

/**
 * Wrapper class for {@link ContentInfo}.
 */
public class ContentInfoBCFips extends ASN1EncodableBCFips implements IContentInfo {
    /**
     * Creates new wrapper instance for {@link ContentInfo}.
     *
     * @param contentInfo {@link ContentInfo} to be wrapped
     */
    public ContentInfoBCFips(ContentInfo contentInfo) {
        super(contentInfo);
    }

    /**
     * Creates new wrapper instance for {@link ContentInfo}.
     *
     * @param objectIdentifier ASN1ObjectIdentifier wrapper
     * @param encodable        ASN1Encodable wrapper
     */
    public ContentInfoBCFips(IASN1ObjectIdentifier objectIdentifier, IASN1Encodable encodable) {
        super(new ContentInfo(((ASN1ObjectIdentifierBCFips) objectIdentifier).getASN1ObjectIdentifier(),
                ((ASN1EncodableBCFips) encodable).getEncodable()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ContentInfo}.
     */
    public ContentInfo getContentInfo() {
        return (ContentInfo) getEncodable();
    }
}
