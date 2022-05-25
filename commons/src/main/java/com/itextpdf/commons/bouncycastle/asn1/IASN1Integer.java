package com.itextpdf.commons.bouncycastle.asn1;

import java.math.BigInteger;

public interface IASN1Integer extends IASN1Primitive {
    BigInteger getValue();
}
