package com.itextpdf.bouncycastle.asn1.cms;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.cms.IContentInfo;

import org.bouncycastle.asn1.cms.ContentInfo;

/**
 * Wrapper class for {@link ContentInfo}.
 */
public class ContentInfoBC extends ASN1EncodableBC implements IContentInfo {
    /**
     * Creates new wrapper instance for {@link ContentInfo}.
     *
     * @param contentInfo {@link ContentInfo} to be wrapped
     */
    public ContentInfoBC(ContentInfo contentInfo) {
        super(contentInfo);
    }

    /**
     * Creates new wrapper instance for {@link ContentInfo}.
     *
     * @param objectIdentifier ASN1ObjectIdentifier wrapper
     * @param encodable        ASN1Encodable wrapper
     */
    public ContentInfoBC(IASN1ObjectIdentifier objectIdentifier, IASN1Encodable encodable) {
        super(new ContentInfo(((ASN1ObjectIdentifierBC) objectIdentifier).getASN1ObjectIdentifier(),
                ((ASN1EncodableBC) encodable).getEncodable()));
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
