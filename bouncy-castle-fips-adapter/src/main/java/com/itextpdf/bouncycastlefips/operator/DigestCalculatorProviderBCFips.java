package com.itextpdf.bouncycastlefips.operator;

import com.itextpdf.bouncycastlefips.asn1.x509.AlgorithmIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculator;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculatorProvider;

import java.util.Objects;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

/**
 * Wrapper class for {@link DigestCalculatorProvider}.
 */
public class DigestCalculatorProviderBCFips implements IDigestCalculatorProvider {
    private final DigestCalculatorProvider calculatorProvider;

    /**
     * Creates new wrapper instance for {@link DigestCalculatorProvider}.
     *
     * @param calculatorProvider {@link DigestCalculatorProvider} to be wrapped
     */
    public DigestCalculatorProviderBCFips(DigestCalculatorProvider calculatorProvider) {
        this.calculatorProvider = calculatorProvider;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link DigestCalculatorProvider}.
     */
    public DigestCalculatorProvider getCalculatorProvider() {
        return calculatorProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDigestCalculator get(IAlgorithmIdentifier algorithmIdentifier) throws OperatorCreationExceptionBCFips {
        try {
            return new DigestCalculatorBCFips(calculatorProvider.get(
                    ((AlgorithmIdentifierBCFips) algorithmIdentifier).getAlgorithmIdentifier()));
        } catch (OperatorCreationException e) {
            throw new OperatorCreationExceptionBCFips(e);
        }
    }

    /**
     * Indicates whether some other object is "equal to" this one. Compares wrapped objects.
     */
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

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(calculatorProvider);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return calculatorProvider.toString();
    }
}
