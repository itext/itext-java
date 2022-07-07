package com.itextpdf.bouncycastlefips.cms;

import com.itextpdf.commons.bouncycastle.cms.ISignerInformationVerifier;

import java.util.Objects;
import org.bouncycastle.cms.SignerInformationVerifier;

public class SignerInformationVerifierBCFips implements ISignerInformationVerifier {
    private final SignerInformationVerifier verifier;

    public SignerInformationVerifierBCFips(SignerInformationVerifier verifier) {
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
        SignerInformationVerifierBCFips that = (SignerInformationVerifierBCFips) o;
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
