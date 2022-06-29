package com.itextpdf.bouncycastlefips.operator;

import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import org.bouncycastle.operator.OperatorCreationException;

public class OperatorCreationExceptionBCFips extends AbstractOperatorCreationException {
    private final OperatorCreationException exception;
    
    public OperatorCreationExceptionBCFips(OperatorCreationException exception) {
        this.exception = exception;
    }

    public OperatorCreationException getException() {
        return exception;
    }
}
