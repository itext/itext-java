package com.itextpdf.commons.bouncycastle.asn1;

import java.io.Closeable;
import java.io.IOException;

public interface IASN1InputStream extends Closeable {
    IASN1Primitive readObject() throws IOException;

    void close() throws IOException;
}
