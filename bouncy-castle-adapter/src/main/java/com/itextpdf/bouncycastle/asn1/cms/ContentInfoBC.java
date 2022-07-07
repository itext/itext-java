package com.itextpdf.bouncycastle.asn1.cms;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.cms.IContentInfo;

import org.bouncycastle.asn1.cms.ContentInfo;

public class ContentInfoBC extends ASN1EncodableBC implements IContentInfo {
    public ContentInfoBC(ContentInfo contentInfo) {
        super(contentInfo);
    }

    public ContentInfoBC(IASN1ObjectIdentifier objectIdentifier, IASN1Encodable encodable) {
        super(new ContentInfo(((ASN1ObjectIdentifierBC) objectIdentifier).getASN1ObjectIdentifier(),
                ((ASN1EncodableBC) encodable).getEncodable()));
    }

    public ContentInfo getContentInfo() {
        return (ContentInfo) getEncodable();
    }
}
