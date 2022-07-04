package com.itextpdf.bouncycastle.tsp;

import com.itextpdf.bouncycastle.cms.SignerInformationVerifierBC;
import com.itextpdf.commons.bouncycastle.cms.ISignerInformationVerifier;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampToken;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampTokenInfo;

import java.io.IOException;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;

public class TimeStampTokenBC implements ITimeStampToken {
    private final TimeStampToken timeStampToken;

    public TimeStampTokenBC(TimeStampToken timeStampToken) {
        this.timeStampToken = timeStampToken;
    }

    public TimeStampToken getTimeStampToken() {
        return timeStampToken;
    }

    @Override
    public ITimeStampTokenInfo getTimeStampInfo() {
        return new TimeStampTokenInfoBC(timeStampToken.getTimeStampInfo());
    }

    @Override
    public byte[] getEncoded() throws IOException {
        return timeStampToken.getEncoded();
    }

    @Override
    public void validate(ISignerInformationVerifier verifier) throws TSPExceptionBC {
        try {
            timeStampToken.validate(((SignerInformationVerifierBC) verifier).getVerifier());
        } catch (TSPException e) {
            throw new TSPExceptionBC(e);
        }
    }
}
