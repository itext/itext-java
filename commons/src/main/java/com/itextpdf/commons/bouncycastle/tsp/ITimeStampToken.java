package com.itextpdf.commons.bouncycastle.tsp;

import com.itextpdf.commons.bouncycastle.cms.ISignerInformationVerifier;

public interface ITimeStampToken {
    ITimeStampTokenInfo getTimeStampInfo();

    void validate(ISignerInformationVerifier verifier) throws AbstractTSPException;
}
