package com.itextpdf.bouncycastle.operator;

import com.itextpdf.bouncycastle.asn1.x509.AlgorithmIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculator;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculatorProvider;

import java.util.Objects;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

public class DigestCalculatorProviderBC implements IDigestCalculatorProvider {
    private final DigestCalculatorProvider calculatorProvider;

    public DigestCalculatorProviderBC(DigestCalculatorProvider calculatorProvider) {
        this.calculatorProvider = calculatorProvider;
    }

    public DigestCalculatorProvider getCalculatorProvider() {
        return calculatorProvider;
    }

    @Override
    public IDigestCalculator get(IAlgorithmIdentifier algorithmIdentifier) throws OperatorCreationExceptionBC {
        try {
            return new DigestCalculatorBC(calculatorProvider.get(
                    ((AlgorithmIdentifierBC) algorithmIdentifier).getAlgorithmIdentifier()));
        } catch (OperatorCreationException e) {
            throw new OperatorCreationExceptionBC(e);
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
        DigestCalculatorProviderBC that = (DigestCalculatorProviderBC) o;
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
