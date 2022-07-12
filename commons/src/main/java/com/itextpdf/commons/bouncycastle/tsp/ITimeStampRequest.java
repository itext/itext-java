package com.itextpdf.commons.bouncycastle.tsp;

import java.io.IOException;
import java.math.BigInteger;

public interface ITimeStampRequest {

    byte[] getEncoded() throws IOException;

    BigInteger getNonce();
}
