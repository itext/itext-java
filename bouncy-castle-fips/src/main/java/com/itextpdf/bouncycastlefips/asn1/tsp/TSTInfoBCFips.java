package com.itextpdf.bouncycastlefips.asn1.tsp;

import com.itextpdf.commons.bouncycastle.asn1.tsp.IMessageImprint;
import com.itextpdf.commons.bouncycastle.asn1.tsp.ITSTInfo;

import org.bouncycastle.asn1.tsp.TSTInfo;

public class TSTInfoBCFips implements ITSTInfo {
    private final TSTInfo tstInfo;

    public TSTInfoBCFips(TSTInfo tstInfo) {
        this.tstInfo = tstInfo;
    }

    public TSTInfo getTstInfo() {
        return tstInfo;
    }

    @Override
    public IMessageImprint getMessageImprint() {
        return new MessageImprintBCFips(tstInfo.getMessageImprint());
    }
}
