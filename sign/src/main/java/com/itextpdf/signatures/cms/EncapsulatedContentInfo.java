/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.signatures.cms;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;
import com.itextpdf.commons.bouncycastle.asn1.IASN1TaggedObject;
import com.itextpdf.kernel.crypto.OID;

/**
 * This class represents the signed content.
 */
public class EncapsulatedContentInfo {

    private static final IBouncyCastleFactory BC_FACTORY = BouncyCastleFactoryCreator.getFactory();

    /**
     * Object identifier of the content field
     */
    private String eContentType = OID.PKCS7_DATA;
    /**
     * Optional.
     *
     * <p>
     * The actual content as an octet string. Does not have to be DER encoded.
     */
    private IASN1OctetString eContent;

    /**
     * Creates an EncapsulatedContentInfo with contenttype and content.
     *
     * @param eContentType the content type Oid (object id)
     * @param eContent     the content
     */
    public EncapsulatedContentInfo(String eContentType, IASN1OctetString eContent) {
        this.eContentType = eContentType;
        this.eContent = eContent;
    }

    /**
     * Creates an EncapsulatedContentInfo with contenttype.
     *
     * @param eContentType the content type Oid (object id)
     */
    public EncapsulatedContentInfo(String eContentType) {
        this.eContentType = eContentType;
    }

    /**
     * Creates a default EncapsulatedContentInfo.
     */
    public EncapsulatedContentInfo() {
        // Empty constructor.
    }

    EncapsulatedContentInfo(IASN1Sequence lencapContentInfo) {
        IASN1ObjectIdentifier eContentTypeOid = BC_FACTORY
                .createASN1ObjectIdentifier(lencapContentInfo.getObjectAt(0));
        IASN1OctetString eContentElem = null;
        if (lencapContentInfo.size() > 1) {
            IASN1TaggedObject taggedElement = BC_FACTORY.createASN1TaggedObject(lencapContentInfo.getObjectAt(1));
            eContentElem = BC_FACTORY.createASN1OctetString(taggedElement.getObject());
            if (eContentElem != null) {
                eContent = eContentElem;
            }
        }
        eContentType = eContentTypeOid.getId();
    }

    /**
     * Returns the contenttype oid.
     *
     * @return the contenttype oid.
     */
    public String getContentType() {
        return eContentType;
    }

    /**
     * Returns the content.
     *
     * @return the content.
     */
    public IASN1OctetString getContent() {
        return eContent;
    }
}
