package com.itextpdf.bouncycastlefips.asn1.cms;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.cms.IContentInfo;

import org.bouncycastle.asn1.cms.ContentInfo;

public class ContentInfoBCFips extends ASN1EncodableBCFips implements IContentInfo {
    public ContentInfoBCFips(ContentInfo contentInfo) {
        super(contentInfo);
    }

    public ContentInfoBCFips(IASN1ObjectIdentifier objectIdentifier, IASN1Encodable encodable) {
        super(new ContentInfo(((ASN1ObjectIdentifierBCFips) objectIdentifier).getObjectIdentifier(),
                ((ASN1EncodableBCFips) encodable).getEncodable()));
    }

    public ContentInfo getContentInfo() {
        return (ContentInfo) getEncodable();
    }
}
