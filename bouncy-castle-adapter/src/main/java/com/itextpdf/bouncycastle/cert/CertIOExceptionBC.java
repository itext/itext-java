package com.itextpdf.bouncycastle.cert;

import com.itextpdf.commons.bouncycastle.cert.AbstractCertIOException;

import java.util.Objects;
import org.bouncycastle.cert.CertIOException;

public class CertIOExceptionBC extends AbstractCertIOException {
    private final CertIOException exception;

    public CertIOExceptionBC(CertIOException exception) {
        this.exception = exception;
    }

    public CertIOException getException() {
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
        CertIOExceptionBC that = (CertIOExceptionBC) o;
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

    @Override
    public String getMessage() {
        return exception.getMessage();
    }
}
