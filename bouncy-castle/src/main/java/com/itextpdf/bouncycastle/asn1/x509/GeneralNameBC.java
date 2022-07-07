package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralName;

import org.bouncycastle.asn1.x509.GeneralName;

public class GeneralNameBC extends ASN1EncodableBC implements IGeneralName {

    private static final GeneralNameBC INSTANCE = new GeneralNameBC(null);

    private static final int UNIFORM_RESOURCE_IDENTIFIER = GeneralName.uniformResourceIdentifier;

    public GeneralNameBC(GeneralName generalName) {
        super(generalName);
    }

    public static GeneralNameBC getInstance() {
        return INSTANCE;
    }

    public GeneralName getGeneralName() {
        return (GeneralName) getEncodable();
    }

    @Override
    public int getTagNo() {
        return getGeneralName().getTagNo();
    }

    @Override
    public int getUniformResourceIdentifier() {
        return UNIFORM_RESOURCE_IDENTIFIER;
    }
}
