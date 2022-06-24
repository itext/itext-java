package com.itextpdf.bouncycastle.cms;

import com.itextpdf.commons.bouncycastle.cms.AbstractCMSException;

import org.bouncycastle.cms.CMSException;

public class CMSExceptionBC extends AbstractCMSException {
    private final CMSException exception;

    public CMSExceptionBC(CMSException exception) {
        this.exception = exception;
    }

    public CMSException getCMSException() {
        return exception;
    }
}
