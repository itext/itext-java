package com.itextpdf.bouncycastle.cms;

import com.itextpdf.commons.bouncycastle.cms.ISignerInfoGenerator;

import java.util.Objects;
import org.bouncycastle.cms.SignerInfoGenerator;

/**
 * Wrapper class for {@link SignerInfoGenerator}.
 */
public class SignerInfoGeneratorBC implements ISignerInfoGenerator {
    private final SignerInfoGenerator signerInfoGenerator;

    /**
     * Creates new wrapper instance for {@link SignerInfoGenerator}.
     *
     * @param signerInfoGenerator {@link SignerInfoGenerator} to be wrapped
     */
    public SignerInfoGeneratorBC(SignerInfoGenerator signerInfoGenerator) {
        this.signerInfoGenerator = signerInfoGenerator;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link SignerInfoGenerator}.
     */
    public SignerInfoGenerator getSignerInfoGenerator() {
        return signerInfoGenerator;
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
        SignerInfoGeneratorBC that = (SignerInfoGeneratorBC) o;
        return Objects.equals(signerInfoGenerator, that.signerInfoGenerator);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(signerInfoGenerator);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return signerInfoGenerator.toString();
    }
}
