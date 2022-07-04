package com.itextpdf.commons.bouncycastle.tsp;

import java.io.IOException;

import com.itextpdf.commons.bouncycastle.cms.ISignerInformationVerifier;

public interface ITimeStampToken {
    ITimeStampTokenInfo getTimeStampInfo();

    void validate(ISignerInformationVerifier verifier) throws AbstractTSPException;

    byte[] getEncoded() throws IOException;
}
