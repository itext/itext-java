package com.itextpdf.bouncycastle.tsp;

import com.itextpdf.commons.bouncycastle.tsp.AbstractTSPException;

import java.util.Objects;
import org.bouncycastle.tsp.TSPException;

public class TSPExceptionBC extends AbstractTSPException {
    private final TSPException tspException;

    public TSPExceptionBC(TSPException tspException) {
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
        TSPExceptionBC that = (TSPExceptionBC) o;
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

    @Override
    public String getMessage() {
        return tspException.getMessage();
    }
}
