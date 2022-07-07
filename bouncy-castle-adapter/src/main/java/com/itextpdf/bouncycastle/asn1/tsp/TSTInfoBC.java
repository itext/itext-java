package com.itextpdf.bouncycastle.asn1.tsp;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.tsp.IMessageImprint;
import com.itextpdf.commons.bouncycastle.asn1.tsp.ITSTInfo;

import org.bouncycastle.asn1.tsp.TSTInfo;

public class TSTInfoBC extends ASN1EncodableBC implements ITSTInfo {
    public TSTInfoBC(TSTInfo tstInfo) {
        super(tstInfo);
    }

    public TSTInfo getTstInfo() {
        return (TSTInfo) getEncodable();
    }

    @Override
    public IMessageImprint getMessageImprint() {
        return new MessageImprintBC(getTstInfo().getMessageImprint());
    }
}
