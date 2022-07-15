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

/**
 * Wrapper class for {@link JcaSignerInfoGeneratorBuilder}.
 */
public class JcaSignerInfoGeneratorBuilderBCFips implements IJcaSignerInfoGeneratorBuilder {
    private final JcaSignerInfoGeneratorBuilder jcaSignerInfoGeneratorBuilder;

    /**
     * Creates new wrapper instance for {@link JcaSignerInfoGeneratorBuilder}.
     *
     * @param jcaSignerInfoGeneratorBuilder {@link JcaSignerInfoGeneratorBuilder} to be wrapped
     */
    public JcaSignerInfoGeneratorBuilderBCFips(JcaSignerInfoGeneratorBuilder jcaSignerInfoGeneratorBuilder) {
        this.jcaSignerInfoGeneratorBuilder = jcaSignerInfoGeneratorBuilder;
    }

    /**
     * Creates new wrapper instance for {@link JcaSignerInfoGeneratorBuilder}.
     *
     * @param calculatorProvider DigestCalculatorProvider wrapper to create {@link JcaSignerInfoGeneratorBuilder}
     */
    public JcaSignerInfoGeneratorBuilderBCFips(IDigestCalculatorProvider calculatorProvider) {
        this(new JcaSignerInfoGeneratorBuilder(
                ((DigestCalculatorProviderBCFips) calculatorProvider).getCalculatorProvider()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link JcaSignerInfoGeneratorBuilder}.
     */
    public JcaSignerInfoGeneratorBuilder getJcaSignerInfoGeneratorBuilder() {
        return jcaSignerInfoGeneratorBuilder;
    }

    /**
     * {@inheritDoc}
     */
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
        JcaSignerInfoGeneratorBuilderBCFips that = (JcaSignerInfoGeneratorBuilderBCFips) o;
        return Objects.equals(jcaSignerInfoGeneratorBuilder, that.jcaSignerInfoGeneratorBuilder);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(jcaSignerInfoGeneratorBuilder);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return jcaSignerInfoGeneratorBuilder.toString();
    }
}
