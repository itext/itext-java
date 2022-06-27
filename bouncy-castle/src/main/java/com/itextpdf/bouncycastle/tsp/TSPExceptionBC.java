package com.itextpdf.bouncycastle.tsp;

import com.itextpdf.commons.bouncycastle.tsp.AbstractTSPException;

import org.bouncycastle.tsp.TSPException;

public class TSPExceptionBC extends AbstractTSPException {
    private final TSPException tspException;

    public TSPExceptionBC(TSPException tspException) {
        this.tspException = tspException;
    }

    public TSPException getTSPException() {
        return tspException;
    }
}
