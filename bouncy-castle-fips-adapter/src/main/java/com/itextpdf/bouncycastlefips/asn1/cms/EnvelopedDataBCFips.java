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
import com.itextpdf.bouncycastlefips.asn1.ASN1SetBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;
import com.itextpdf.commons.bouncycastle.asn1.cms.IEncryptedContentInfo;
import com.itextpdf.commons.bouncycastle.asn1.cms.IEnvelopedData;
import com.itextpdf.commons.bouncycastle.asn1.cms.IOriginatorInfo;

import org.bouncycastle.asn1.cms.EnvelopedData;

/**
 * Wrapper class for {@link EnvelopedData}.
 */
public class EnvelopedDataBCFips extends ASN1EncodableBCFips implements IEnvelopedData {
    /**
     * Creates new wrapper instance for {@link EnvelopedData}.
     *
     * @param envelopedData {@link EnvelopedData} to be wrapped
     */
    public EnvelopedDataBCFips(EnvelopedData envelopedData) {
        super(envelopedData);
    }

    /**
     * Creates new wrapper instance for {@link EnvelopedData}.
     *
     * @param originatorInfo       OriginatorInfo wrapper to create {@link EnvelopedData}
     * @param set                  ASN1Set wrapper to create {@link EnvelopedData}
     * @param encryptedContentInfo EncryptedContentInfo wrapper to create {@link EnvelopedData}
     * @param set1                 ASN1Set wrapper to create {@link EnvelopedData}
     */
    public EnvelopedDataBCFips(IOriginatorInfo originatorInfo, IASN1Set set,
            IEncryptedContentInfo encryptedContentInfo, IASN1Set set1) {
        super(new EnvelopedData(
                ((OriginatorInfoBCFips) originatorInfo).getOriginatorInfo(),
                ((ASN1SetBCFips) set).getASN1Set(),
                ((EncryptedContentInfoBCFips) encryptedContentInfo).getEncryptedContentInfo(),
                ((ASN1SetBCFips) set1).getASN1Set()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link EnvelopedData}.
     */
    public EnvelopedData getEnvelopedData() {
        return (EnvelopedData) getEncodable();
    }
}
