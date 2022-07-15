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
