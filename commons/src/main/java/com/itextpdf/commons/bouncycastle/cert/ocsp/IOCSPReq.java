package com.itextpdf.commons.bouncycastle.cert.ocsp;

import java.io.IOException;

public interface IOCSPReq {
    byte[] getEncoded() throws IOException;
}
