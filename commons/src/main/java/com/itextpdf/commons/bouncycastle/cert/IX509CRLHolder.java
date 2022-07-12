package com.itextpdf.commons.bouncycastle.cert;

import java.io.IOException;

public interface IX509CRLHolder {
    byte[] getEncoded() throws IOException;
}
