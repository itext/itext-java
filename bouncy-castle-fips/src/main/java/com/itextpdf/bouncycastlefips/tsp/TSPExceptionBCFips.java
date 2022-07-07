package com.itextpdf.bouncycastlefips.tsp;

import com.itextpdf.commons.bouncycastle.tsp.AbstractTSPException;

import java.util.Objects;
import org.bouncycastle.tsp.TSPException;

public class TSPExceptionBCFips extends AbstractTSPException {
    private final TSPException tspException;

    public TSPExceptionBCFips(TSPException tspException) {
        this.tspException = tspException;
    }

    public TSPException getTSPException() {
        return tspException;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TSPExceptionBCFips that = (TSPExceptionBCFips) o;
        return Objects.equals(tspException, that.tspException);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tspException);
    }

    @Override
    public String toString() {
        return tspException.toString();
    }
}
