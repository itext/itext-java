package com.itextpdf.commons.bouncycastle.asn1;

import java.io.IOException;

public interface IASN1InputStream {
    IASN1Primitive readObject() throws IOException;
}
