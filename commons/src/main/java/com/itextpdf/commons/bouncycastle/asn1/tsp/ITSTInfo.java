package com.itextpdf.commons.bouncycastle.asn1.tsp;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

public interface ITSTInfo extends IASN1Encodable {
    IMessageImprint getMessageImprint();
}
