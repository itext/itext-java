package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralName;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralNames;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;

public class GeneralNamesBCFips extends ASN1EncodableBCFips implements IGeneralNames {
    public GeneralNamesBCFips(GeneralNames generalNames) {
        super(generalNames);
    }

    public GeneralNames getGeneralNames() {
        return (GeneralNames) getEncodable();
    }

    @Override
    public IGeneralName[] getNames() {
        GeneralName[] generalNames = getGeneralNames().getNames();
        IGeneralName[] generalNamesBC = new GeneralNameBCFips[generalNames.length];

        for (int i = 0; i < generalNames.length; ++i) {
            generalNamesBC[i] = new GeneralNameBCFips(generalNames[i]);
        }

        return generalNamesBC;
    }
}
