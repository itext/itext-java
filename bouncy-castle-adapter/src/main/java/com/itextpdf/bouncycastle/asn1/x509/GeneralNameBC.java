package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralName;

import org.bouncycastle.asn1.x509.GeneralName;

/**
 * Wrapper class for {@link GeneralName}.
 */
public class GeneralNameBC extends ASN1EncodableBC implements IGeneralName {
    private static final GeneralNameBC INSTANCE = new GeneralNameBC(null);

    private static final int UNIFORM_RESOURCE_IDENTIFIER = GeneralName.uniformResourceIdentifier;

    /**
     * Creates new wrapper instance for {@link GeneralName}.
     *
     * @param generalName {@link GeneralName} to be wrapped
     */
    public GeneralNameBC(GeneralName generalName) {
        super(generalName);
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link GeneralNameBC} instance.
     */
    public static GeneralNameBC getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link GeneralName}.
     */
    public GeneralName getGeneralName() {
        return (GeneralName) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTagNo() {
        return getGeneralName().getTagNo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUniformResourceIdentifier() {
        return UNIFORM_RESOURCE_IDENTIFIER;
    }
}
