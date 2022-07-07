package com.itextpdf.bouncycastle.cms;

import com.itextpdf.commons.bouncycastle.cms.ISignerInformationVerifier;

import java.util.Objects;
import org.bouncycastle.cms.SignerInformationVerifier;

public class SignerInformationVerifierBC implements ISignerInformationVerifier {
    private final SignerInformationVerifier verifier;

    public SignerInformationVerifierBC(SignerInformationVerifier verifier) {
        this.verifier = verifier;
    }

    public SignerInformationVerifier getVerifier() {
        return verifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SignerInformationVerifierBC that = (SignerInformationVerifierBC) o;
        return Objects.equals(verifier, that.verifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(verifier);
    }

    @Override
    public String toString() {
        return verifier.toString();
    }
}
