package com.itextpdf.bouncycastle.tsp;

import com.itextpdf.bouncycastle.asn1.tsp.MessageImprintBC;
import com.itextpdf.commons.bouncycastle.asn1.tsp.IMessageImprint;
import com.itextpdf.commons.bouncycastle.asn1.tsp.ITSTInfo;

import org.bouncycastle.asn1.tsp.TSTInfo;

public class TSTInfoBC implements ITSTInfo {
    private final TSTInfo tstInfo;

    public TSTInfoBC(TSTInfo tstInfo) {
        this.tstInfo = tstInfo;
    }

    public TSTInfo getTstInfo() {
        return tstInfo;
    }

    @Override
    public IMessageImprint getMessageImprint() {
        return new MessageImprintBC(tstInfo.getMessageImprint());
    }
}
