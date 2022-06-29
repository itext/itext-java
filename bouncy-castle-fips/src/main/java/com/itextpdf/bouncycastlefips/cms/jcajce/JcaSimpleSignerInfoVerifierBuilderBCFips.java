package com.itextpdf.bouncycastlefips.cms.jcajce;

import com.itextpdf.bouncycastlefips.cms.SignerInformationVerifierBCFips;
import com.itextpdf.bouncycastlefips.operator.OperatorCreationExceptionBCFips;
import com.itextpdf.commons.bouncycastle.cms.ISignerInformationVerifier;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.OperatorCreationException;

import java.security.cert.X509Certificate;

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
}
