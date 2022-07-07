package com.itextpdf.commons.bouncycastle.asn1.cms;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;

public interface IAttribute extends IASN1Encodable {
    IASN1Set getAttrValues();
}
