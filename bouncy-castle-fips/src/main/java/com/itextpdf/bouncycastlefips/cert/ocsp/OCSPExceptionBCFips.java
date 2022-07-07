package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;

import java.util.Objects;
import org.bouncycastle.cert.ocsp.OCSPException;

public class OCSPExceptionBCFips extends AbstractOCSPException {
    private final OCSPException exception;

    public OCSPExceptionBCFips(OCSPException exception) {
        this.exception = exception;
    }

    public OCSPException getException() {
        return exception;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OCSPExceptionBCFips that = (OCSPExceptionBCFips) o;
        return Objects.equals(exception, that.exception);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exception);
    }

    @Override
    public String toString() {
        return exception.toString();
    }
}
