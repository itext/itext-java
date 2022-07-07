package com.itextpdf.bouncycastle.cms;

import com.itextpdf.commons.bouncycastle.cms.AbstractCMSException;

import java.util.Objects;
import org.bouncycastle.cms.CMSException;

public class CMSExceptionBC extends AbstractCMSException {
    private final CMSException exception;

    public CMSExceptionBC(CMSException exception) {
        this.exception = exception;
    }

    public CMSException getCMSException() {
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
        CMSExceptionBC that = (CMSExceptionBC) o;
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
