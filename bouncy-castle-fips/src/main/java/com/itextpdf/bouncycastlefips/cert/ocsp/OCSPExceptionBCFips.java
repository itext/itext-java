package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;
import org.bouncycastle.cert.ocsp.OCSPException;

public class OCSPExceptionBCFips extends AbstractOCSPException {
    private final OCSPException exception;
    
    public OCSPExceptionBCFips(OCSPException exception) {
        this.exception = exception;
    }

    public OCSPException getException() {
        return exception;
    }
}
