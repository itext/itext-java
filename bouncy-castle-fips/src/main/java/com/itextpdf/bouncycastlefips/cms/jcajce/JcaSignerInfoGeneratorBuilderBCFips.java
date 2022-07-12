package com.itextpdf.bouncycastlefips.cms.jcajce;

import com.itextpdf.bouncycastlefips.cms.SignerInfoGeneratorBCFips;
import com.itextpdf.bouncycastlefips.operator.ContentSignerBCFips;
import com.itextpdf.bouncycastlefips.operator.DigestCalculatorProviderBCFips;
import com.itextpdf.bouncycastlefips.operator.OperatorCreationExceptionBCFips;
import com.itextpdf.commons.bouncycastle.cms.ISignerInfoGenerator;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJcaSignerInfoGeneratorBuilder;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculatorProvider;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Objects;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.OperatorCreationException;

public class JcaSignerInfoGeneratorBuilderBCFips implements IJcaSignerInfoGeneratorBuilder {
    private final JcaSignerInfoGeneratorBuilder jcaSignerInfoGeneratorBuilder;

    public JcaSignerInfoGeneratorBuilderBCFips(JcaSignerInfoGeneratorBuilder jcaSignerInfoGeneratorBuilder) {
        this.jcaSignerInfoGeneratorBuilder = jcaSignerInfoGeneratorBuilder;
    }

    public JcaSignerInfoGeneratorBuilderBCFips(IDigestCalculatorProvider digestCalcProviderProvider) {
        this(new JcaSignerInfoGeneratorBuilder(
                ((DigestCalculatorProviderBCFips) digestCalcProviderProvider).getCalculatorProvider()));
    }

    public JcaSignerInfoGeneratorBuilder getJcaSignerInfoGeneratorBuilder() {
        return jcaSignerInfoGeneratorBuilder;
    }

    @Override
    public ISignerInfoGenerator build(IContentSigner signer, X509Certificate cert)
            throws OperatorCreationExceptionBCFips, CertificateEncodingException {
        try {
            return new SignerInfoGeneratorBCFips(jcaSignerInfoGeneratorBuilder.build(
                    ((ContentSignerBCFips) signer).getContentSigner(), cert));
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
        JcaSignerInfoGeneratorBuilderBCFips that = (JcaSignerInfoGeneratorBuilderBCFips) o;
        return Objects.equals(jcaSignerInfoGeneratorBuilder, that.jcaSignerInfoGeneratorBuilder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jcaSignerInfoGeneratorBuilder);
    }

    @Override
    public String toString() {
        return jcaSignerInfoGeneratorBuilder.toString();
    }
}
