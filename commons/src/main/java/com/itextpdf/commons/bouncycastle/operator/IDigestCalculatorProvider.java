package com.itextpdf.commons.bouncycastle.operator;

import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

public interface IDigestCalculatorProvider {
    IDigestCalculator get(IAlgorithmIdentifier algorithmIdentifier) throws AbstractOperatorCreationException;
}
