package com.itextpdf.bouncycastle.operator;

import com.itextpdf.commons.bouncycastle.operator.IDigestCalculator;

import java.util.Objects;
import org.bouncycastle.operator.DigestCalculator;

/**
 * Wrapper class for {@link DigestCalculator}.
 */
public class DigestCalculatorBC implements IDigestCalculator {
    private final DigestCalculator digestCalculator;

    /**
     * Creates new wrapper instance for {@link DigestCalculator}.
     *
     * @param digestCalculator {@link DigestCalculator} to be wrapped
     */
    public DigestCalculatorBC(DigestCalculator digestCalculator) {
        this.digestCalculator = digestCalculator;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link DigestCalculator}.
     */
    public DigestCalculator getDigestCalculator() {
        return digestCalculator;
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
        DigestCalculatorBC that = (DigestCalculatorBC) o;
        return Objects.equals(digestCalculator, that.digestCalculator);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(digestCalculator);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return digestCalculator.toString();
    }
}
