package com.itextpdf.commons.bouncycastle.asn1.tsp;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

public interface IMessageImprint extends IASN1Encodable {
    byte[] getHashedMessage();
}
