package com.itextpdf.bouncycastle.operator.jcajce;

import com.itextpdf.bouncycastle.operator.ContentSignerBC;
import com.itextpdf.bouncycastle.operator.OperatorCreationExceptionBC;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;
import com.itextpdf.commons.bouncycastle.operator.jcajce.IJcaContentSignerBuilder;

import java.security.PrivateKey;
import java.util.Objects;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class JcaContentSignerBuilderBC implements IJcaContentSignerBuilder {
    private final JcaContentSignerBuilder jcaContentSignerBuilder;

    public JcaContentSignerBuilderBC(JcaContentSignerBuilder jcaContentSignerBuilder) {
        this.jcaContentSignerBuilder = jcaContentSignerBuilder;
    }

    public JcaContentSignerBuilder getJcaContentSignerBuilder() {
        return jcaContentSignerBuilder;
    }

    @Override
    public IContentSigner build(PrivateKey pk) throws OperatorCreationExceptionBC {
        try {
            return new ContentSignerBC(jcaContentSignerBuilder.build(pk));
        } catch (OperatorCreationException e) {
            throw new OperatorCreationExceptionBC(e);
        }
    }

    @Override
    public IJcaContentSignerBuilder setProvider(String providerName) {
        jcaContentSignerBuilder.setProvider(providerName);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JcaContentSignerBuilderBC that = (JcaContentSignerBuilderBC) o;
        return Objects.equals(jcaContentSignerBuilder, that.jcaContentSignerBuilder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jcaContentSignerBuilder);
    }

    @Override
    public String toString() {
        return jcaContentSignerBuilder.toString();
    }
}
