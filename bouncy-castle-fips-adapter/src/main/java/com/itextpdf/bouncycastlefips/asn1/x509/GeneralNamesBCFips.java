package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralName;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralNames;

import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;

/**
 * Wrapper class for {@link GeneralNames}.
 */
public class GeneralNamesBCFips extends ASN1EncodableBCFips implements IGeneralNames {
    /**
     * Creates new wrapper instance for {@link GeneralNames}.
     *
     * @param generalNames {@link GeneralNames} to be wrapped
     */
    public GeneralNamesBCFips(GeneralNames generalNames) {
        super(generalNames);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link GeneralNames}.
     */
    public GeneralNames getGeneralNames() {
        return (GeneralNames) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
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
