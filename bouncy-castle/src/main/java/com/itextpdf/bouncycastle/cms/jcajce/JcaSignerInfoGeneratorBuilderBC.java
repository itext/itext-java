package com.itextpdf.bouncycastle.cms.jcajce;

import com.itextpdf.bouncycastle.cms.SignerInfoGeneratorBC;
import com.itextpdf.bouncycastle.operator.ContentSignerBC;
import com.itextpdf.bouncycastle.operator.DigestCalculatorProviderBC;
import com.itextpdf.bouncycastle.operator.OperatorCreationExceptionBC;
import com.itextpdf.commons.bouncycastle.cms.ISignerInfoGenerator;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJcaSignerInfoGeneratorBuilder;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculatorProvider;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Objects;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.OperatorCreationException;

public class JcaSignerInfoGeneratorBuilderBC implements IJcaSignerInfoGeneratorBuilder {
    private final JcaSignerInfoGeneratorBuilder jcaSignerInfoGeneratorBuilder;

    public JcaSignerInfoGeneratorBuilderBC(JcaSignerInfoGeneratorBuilder jcaSignerInfoGeneratorBuilder) {
        this.jcaSignerInfoGeneratorBuilder = jcaSignerInfoGeneratorBuilder;
    }

    public JcaSignerInfoGeneratorBuilderBC(IDigestCalculatorProvider digestCalcProviderProvider) {
        this(new JcaSignerInfoGeneratorBuilder(
                ((DigestCalculatorProviderBC) digestCalcProviderProvider).getCalculatorProvider()));
    }

    public JcaSignerInfoGeneratorBuilder getJcaSignerInfoGeneratorBuilder() {
        return jcaSignerInfoGeneratorBuilder;
    }

    @Override
    public ISignerInfoGenerator build(IContentSigner signer, X509Certificate cert)
            throws OperatorCreationExceptionBC, CertificateEncodingException {
        try {
            return new SignerInfoGeneratorBC(jcaSignerInfoGeneratorBuilder.build(
                    ((ContentSignerBC) signer).getContentSigner(), cert));
        } catch (OperatorCreationException e) {
            throw new OperatorCreationExceptionBC(e);
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
        JcaSignerInfoGeneratorBuilderBC that = (JcaSignerInfoGeneratorBuilderBC) o;
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
