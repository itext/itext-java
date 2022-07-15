package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralName;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralNames;

import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;

/**
 * Wrapper class for {@link GeneralNames}.
 */
public class GeneralNamesBC extends ASN1EncodableBC implements IGeneralNames {
    /**
     * Creates new wrapper instance for {@link GeneralNames}.
     *
     * @param generalNames {@link GeneralNames} to be wrapped
     */
    public GeneralNamesBC(GeneralNames generalNames) {
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
        IGeneralName[] generalNamesBC = new GeneralNameBC[generalNames.length];

        for (int i = 0; i < generalNames.length; ++i) {
            generalNamesBC[i] = new GeneralNameBC(generalNames[i]);
        }

        return generalNamesBC;
    }
}
