package com.itextpdf.bouncycastlefips.asn1.tsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.tsp.IMessageImprint;
import com.itextpdf.commons.bouncycastle.asn1.tsp.ITSTInfo;

import org.bouncycastle.asn1.tsp.TSTInfo;

/**
 * Wrapper class for {@link TSTInfo}.
 */
public class TSTInfoBCFips extends ASN1EncodableBCFips implements ITSTInfo {
    /**
     * Creates new wrapper instance for {@link TSTInfo}.
     *
     * @param tstInfo {@link TSTInfo} to be wrapped
     */
    public TSTInfoBCFips(TSTInfo tstInfo) {
        super(tstInfo);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link TSTInfo}.
     */
    public TSTInfo getTstInfo() {
        return (TSTInfo) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IMessageImprint getMessageImprint() {
        return new MessageImprintBCFips(getTstInfo().getMessageImprint());
    }
}
