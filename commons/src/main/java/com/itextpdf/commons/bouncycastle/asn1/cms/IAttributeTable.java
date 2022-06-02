package com.itextpdf.commons.bouncycastle.asn1.cms;

import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;

public interface IAttributeTable {
    IAttribute get(IASN1ObjectIdentifier oid);
}
