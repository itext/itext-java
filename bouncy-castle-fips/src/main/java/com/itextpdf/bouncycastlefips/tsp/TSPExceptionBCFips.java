package com.itextpdf.bouncycastlefips.tsp;

import com.itextpdf.commons.bouncycastle.tsp.AbstractTSPException;

import org.bouncycastle.tsp.TSPException;

public class TSPExceptionBCFips extends AbstractTSPException {
    private final TSPException tspException;

    public TSPExceptionBCFips(TSPException tspException) {
        this.tspException = tspException;
    }

    public TSPException getTSPException() {
        return tspException;
    }
}
