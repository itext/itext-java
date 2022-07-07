package com.itextpdf.bouncycastlefips.asn1.tsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.tsp.IMessageImprint;
import com.itextpdf.commons.bouncycastle.asn1.tsp.ITSTInfo;

import org.bouncycastle.asn1.tsp.TSTInfo;

public class TSTInfoBCFips extends ASN1EncodableBCFips implements ITSTInfo {
    public TSTInfoBCFips(TSTInfo tstInfo) {
        super(tstInfo);
    }

    public TSTInfo getTstInfo() {
        return (TSTInfo) getEncodable();
    }

    @Override
    public IMessageImprint getMessageImprint() {
        return new MessageImprintBCFips(getTstInfo().getMessageImprint());
    }
}
