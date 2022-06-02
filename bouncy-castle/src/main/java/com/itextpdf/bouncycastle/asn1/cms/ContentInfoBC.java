package com.itextpdf.bouncycastle.asn1.cms;

import com.itextpdf.commons.bouncycastle.asn1.cms.IContentInfo;

import org.bouncycastle.asn1.cms.ContentInfo;

public class ContentInfoBC implements IContentInfo {
    private final ContentInfo contentInfo;

    public ContentInfoBC(ContentInfo contentInfo) {
        this.contentInfo = contentInfo;
    }

    public ContentInfo getContentInfo() {
        return contentInfo;
    }
}
