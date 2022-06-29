package com.itextpdf.bouncycastle.cms.jcajce;

import com.itextpdf.bouncycastle.cms.SignerInformationVerifierBC;
import com.itextpdf.bouncycastle.operator.OperatorCreationExceptionBC;
import com.itextpdf.commons.bouncycastle.cms.ISignerInformationVerifier;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.OperatorCreationException;

import java.security.cert.X509Certificate;

public class JcaSimpleSignerInfoVerifierBuilderBC implements IJcaSimpleSignerInfoVerifierBuilder {
    private final JcaSimpleSignerInfoVerifierBuilder verifierBuilder;
    
    public JcaSimpleSignerInfoVerifierBuilderBC(JcaSimpleSignerInfoVerifierBuilder verifierBuilder) {
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
    public ISignerInformationVerifier build(X509Certificate certificate) throws OperatorCreationExceptionBC {
        try {
            return new SignerInformationVerifierBC(verifierBuilder.build(certificate));
        } catch (OperatorCreationException e) {
            throw new OperatorCreationExceptionBC(e);
        }
    }
}
