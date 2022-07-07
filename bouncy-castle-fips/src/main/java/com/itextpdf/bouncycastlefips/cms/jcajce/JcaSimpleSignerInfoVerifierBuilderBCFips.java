package com.itextpdf.bouncycastlefips.cms.jcajce;

import com.itextpdf.bouncycastlefips.cms.SignerInformationVerifierBCFips;
import com.itextpdf.bouncycastlefips.operator.OperatorCreationExceptionBCFips;
import com.itextpdf.commons.bouncycastle.cms.ISignerInformationVerifier;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJcaSimpleSignerInfoVerifierBuilder;

import java.security.cert.X509Certificate;
import java.util.Objects;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.OperatorCreationException;

public class JcaSimpleSignerInfoVerifierBuilderBCFips implements IJcaSimpleSignerInfoVerifierBuilder {
    private final JcaSimpleSignerInfoVerifierBuilder verifierBuilder;

    public JcaSimpleSignerInfoVerifierBuilderBCFips(JcaSimpleSignerInfoVerifierBuilder verifierBuilder) {
        this.verifierBuilder = verifierBuilder;
    }

    public JcaSimpleSignerInfoVerifierBuilder getVerifierBuilder() {
        return verifierBuilder;
    }

    @Override
    public IJcaSimpleSignerInfoVerifierBuilder setProvider(String provider) {
        verifierBuilder.setProvider(provider);
        return this;
    }

    @Override
    public ISignerInformationVerifier build(X509Certificate certificate) throws OperatorCreationExceptionBCFips {
        try {
            return new SignerInformationVerifierBCFips(verifierBuilder.build(certificate));
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
        JcaSimpleSignerInfoVerifierBuilderBCFips that = (JcaSimpleSignerInfoVerifierBuilderBCFips) o;
        return Objects.equals(verifierBuilder, that.verifierBuilder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(verifierBuilder);
    }

    @Override
    public String toString() {
        return verifierBuilder.toString();
    }
}
