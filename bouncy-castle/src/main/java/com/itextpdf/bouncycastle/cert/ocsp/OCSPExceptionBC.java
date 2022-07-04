package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;
import org.bouncycastle.cert.ocsp.OCSPException;

public class OCSPExceptionBC extends AbstractOCSPException {
    private final OCSPException exception;

    public OCSPExceptionBC(OCSPException exception) {
        this.exception = exception;
    }

    public OCSPException getException() {
        return exception;
    }

    @Override
    public String toString() {
        return exception.toString();
    }
}
