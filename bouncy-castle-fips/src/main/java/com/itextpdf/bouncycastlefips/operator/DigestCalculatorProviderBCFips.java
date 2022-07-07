package com.itextpdf.bouncycastlefips.operator;

import com.itextpdf.bouncycastlefips.asn1.x509.AlgorithmIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculator;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculatorProvider;

import java.util.Objects;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

public class DigestCalculatorProviderBCFips implements IDigestCalculatorProvider {
    private final DigestCalculatorProvider calculatorProvider;

    public DigestCalculatorProviderBCFips(DigestCalculatorProvider calculatorProvider) {
        this.calculatorProvider = calculatorProvider;
    }

    public DigestCalculatorProvider getCalculatorProvider() {
        return calculatorProvider;
    }

    @Override
    public IDigestCalculator get(IAlgorithmIdentifier algorithmIdentifier) throws OperatorCreationExceptionBCFips {
        try {
            return new DigestCalculatorBCFips(calculatorProvider.get(
                    ((AlgorithmIdentifierBCFips) algorithmIdentifier).getAlgorithmIdentifier()));
        } catch (OperatorCreationException e) {
            throw new OperatorCreationExceptionBCFips(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DigestCalculatorProviderBCFips that = (DigestCalculatorProviderBCFips) o;
        return Objects.equals(calculatorProvider, that.calculatorProvider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(calculatorProvider);
    }

    @Override
    public String toString() {
        return calculatorProvider.toString();
    }
}
