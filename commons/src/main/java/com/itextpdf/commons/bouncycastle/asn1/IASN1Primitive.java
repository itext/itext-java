package com.itextpdf.commons.bouncycastle.asn1;

import java.io.IOException;

public interface IASN1Primitive extends IASN1Encodable {
    byte[] getEncoded() throws IOException;

    byte[] getEncoded(String encoding) throws IOException;
}
