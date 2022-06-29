package com.itextpdf.commons.bouncycastle.operator.jcajce;

import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculatorProvider;

public interface IJcaDigestCalculatorProviderBuilder {
    IDigestCalculatorProvider build() throws AbstractOperatorCreationException;
}
