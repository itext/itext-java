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

/**
 * Wrapper class for {@link JcaSignerInfoGeneratorBuilder}.
 */
public class JcaSignerInfoGeneratorBuilderBC implements IJcaSignerInfoGeneratorBuilder {
    private final JcaSignerInfoGeneratorBuilder jcaSignerInfoGeneratorBuilder;

    /**
     * Creates new wrapper instance for {@link JcaSignerInfoGeneratorBuilder}.
     *
     * @param jcaSignerInfoGeneratorBuilder {@link JcaSignerInfoGeneratorBuilder} to be wrapped
     */
    public JcaSignerInfoGeneratorBuilderBC(JcaSignerInfoGeneratorBuilder jcaSignerInfoGeneratorBuilder) {
        this.jcaSignerInfoGeneratorBuilder = jcaSignerInfoGeneratorBuilder;
    }

    /**
     * Creates new wrapper instance for {@link JcaSignerInfoGeneratorBuilder}.
     *
     * @param calculatorProvider DigestCalculatorProvider wrapper to create {@link JcaSignerInfoGeneratorBuilder}
     */
    public JcaSignerInfoGeneratorBuilderBC(IDigestCalculatorProvider calculatorProvider) {
        this(new JcaSignerInfoGeneratorBuilder(
                ((DigestCalculatorProviderBC) calculatorProvider).getCalculatorProvider()));
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
            throws OperatorCreationExceptionBC, CertificateEncodingException {
        try {
            return new SignerInfoGeneratorBC(jcaSignerInfoGeneratorBuilder.build(
                    ((ContentSignerBC) signer).getContentSigner(), cert));
        } catch (OperatorCreationException e) {
            throw new OperatorCreationExceptionBC(e);
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
        JcaSignerInfoGeneratorBuilderBC that = (JcaSignerInfoGeneratorBuilderBC) o;
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
