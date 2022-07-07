package com.itextpdf.bouncycastle.cms;

import com.itextpdf.commons.bouncycastle.cms.ISignerInfoGenerator;

import java.util.Objects;
import org.bouncycastle.cms.SignerInfoGenerator;

public class SignerInfoGeneratorBC implements ISignerInfoGenerator {
    private final SignerInfoGenerator signerInfoGenerator;

    public SignerInfoGeneratorBC(SignerInfoGenerator signerInfoGenerator) {
        this.signerInfoGenerator = signerInfoGenerator;
    }

    public SignerInfoGenerator getSignerInfoGenerator() {
        return signerInfoGenerator;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(signerInfoGenerator);
    }

    @Override
    public String toString() {
        return signerInfoGenerator.toString();
    }
}
