package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralName;

import org.bouncycastle.asn1.x509.GeneralName;

/**
 * Wrapper class for {@link GeneralName}.
 */
public class GeneralNameBCFips extends ASN1EncodableBCFips implements IGeneralName {
    private static final GeneralNameBCFips INSTANCE = new GeneralNameBCFips(null);

    private static final int UNIFORM_RESOURCE_IDENTIFIER = GeneralName.uniformResourceIdentifier;

    /**
     * Creates new wrapper instance for {@link GeneralName}.
     *
     * @param generalName {@link GeneralName} to be wrapped
     */
    public GeneralNameBCFips(GeneralName generalName) {
        super(generalName);
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link GeneralNameBCFips} instance.
     */
    public static GeneralNameBCFips getInstance() {
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
