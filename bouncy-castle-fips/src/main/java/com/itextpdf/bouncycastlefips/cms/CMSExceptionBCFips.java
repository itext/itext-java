package com.itextpdf.bouncycastlefips.cms;

import com.itextpdf.commons.bouncycastle.cms.AbstractCMSException;

import org.bouncycastle.cms.CMSException;

public class CMSExceptionBCFips extends AbstractCMSException {
    private final CMSException exception;

    public CMSExceptionBCFips(CMSException exception) {
        this.exception = exception;
    }

    public CMSException getCMSException() {
        return exception;
    }
}
