package com.itextpdf.bouncycastle.asn1.tsp;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.tsp.IMessageImprint;
import com.itextpdf.commons.bouncycastle.asn1.tsp.ITSTInfo;

import org.bouncycastle.asn1.tsp.TSTInfo;

/**
 * Wrapper class for {@link TSTInfo}.
 */
public class TSTInfoBC extends ASN1EncodableBC implements ITSTInfo {
    /**
     * Creates new wrapper instance for {@link TSTInfo}.
     *
     * @param tstInfo {@link TSTInfo} to be wrapped
     */
    public TSTInfoBC(TSTInfo tstInfo) {
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
        return new MessageImprintBC(getTstInfo().getMessageImprint());
    }
}
