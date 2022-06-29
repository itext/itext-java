package com.itextpdf.bouncycastle.operator;

import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import org.bouncycastle.operator.OperatorCreationException;

public class OperatorCreationExceptionBC extends AbstractOperatorCreationException {
    private final OperatorCreationException exception;

    public OperatorCreationExceptionBC(OperatorCreationException exception) {
        this.exception = exception;
    }

    public OperatorCreationException getException() {
        return exception;
    }
}
