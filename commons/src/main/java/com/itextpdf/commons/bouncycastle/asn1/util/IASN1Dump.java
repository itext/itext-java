package com.itextpdf.commons.bouncycastle.asn1.util;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

public interface IASN1Dump {
    String dumpAsString(Object obj, boolean b);

    String dumpAsString(Object obj);
}
