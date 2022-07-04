package com.itextpdf.bouncycastlefips.tsp;

import com.itextpdf.bouncycastlefips.cms.SignerInformationVerifierBCFips;
import com.itextpdf.commons.bouncycastle.cms.ISignerInformationVerifier;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampToken;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampTokenInfo;

import java.io.IOException;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;

public class TimeStampTokenBCFips implements ITimeStampToken {
    private final TimeStampToken timeStampToken;

    public TimeStampTokenBCFips(TimeStampToken timeStampToken) {
        this.timeStampToken = timeStampToken;
    }

    public TimeStampToken getTimeStampToken() {
        return timeStampToken;
    }

    @Override
    public ITimeStampTokenInfo getTimeStampInfo() {
        return new TimeStampTokenInfoBCFips(timeStampToken.getTimeStampInfo());
    }

    @Override
    public byte[] getEncoded() throws IOException {
        return timeStampToken.getEncoded();
    }

    @Override
    public void validate(ISignerInformationVerifier verifier) throws TSPExceptionBCFips {
        try {
            timeStampToken.validate(((SignerInformationVerifierBCFips) verifier).getVerifier());
        } catch (TSPException e) {
            throw new TSPExceptionBCFips(e);
        }
    }
}
