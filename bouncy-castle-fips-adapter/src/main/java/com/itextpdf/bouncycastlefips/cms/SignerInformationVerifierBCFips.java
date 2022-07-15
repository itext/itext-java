package com.itextpdf.bouncycastlefips.cms;

import com.itextpdf.commons.bouncycastle.cms.ISignerInformationVerifier;

import java.util.Objects;
import org.bouncycastle.cms.SignerInformationVerifier;

/**
 * Wrapper class for {@link SignerInformationVerifier}.
 */
public class SignerInformationVerifierBCFips implements ISignerInformationVerifier {
    private final SignerInformationVerifier verifier;

    /**
     * Creates new wrapper instance for {@link SignerInformationVerifier}.
     *
     * @param verifier {@link SignerInformationVerifier} to be wrapped
     */
    public SignerInformationVerifierBCFips(SignerInformationVerifier verifier) {
        this.verifier = verifier;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link SignerInformationVerifier}.
     */
    public SignerInformationVerifier getVerifier() {
        return verifier;
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
        SignerInformationVerifierBCFips that = (SignerInformationVerifierBCFips) o;
        return Objects.equals(verifier, that.verifier);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(verifier);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return verifier.toString();
    }
}
