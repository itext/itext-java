package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralName;

import org.bouncycastle.asn1.x509.GeneralName;

public class GeneralNameBCFips extends ASN1EncodableBCFips implements IGeneralName {

    private static final GeneralNameBCFips INSTANCE = new GeneralNameBCFips(null);

    private static final int UNIFORM_RESOURCE_IDENTIFIER = GeneralName.uniformResourceIdentifier;

    public GeneralNameBCFips(GeneralName generalName) {
        super(generalName);
    }

    public static GeneralNameBCFips getInstance() {
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
