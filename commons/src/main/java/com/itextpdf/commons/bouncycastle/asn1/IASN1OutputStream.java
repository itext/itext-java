package com.itextpdf.commons.bouncycastle.asn1;

import java.io.Closeable;
import java.io.IOException;

public interface IASN1OutputStream extends Closeable {
    void writeObject(IASN1Primitive primitive) throws IOException;

    void close() throws IOException;
}
