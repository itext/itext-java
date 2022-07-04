package com.itextpdf.commons.bouncycastle.tsp;

import java.io.IOException;

public interface ITimeStampRequest {

    byte[] getEncoded() throws IOException;

}
