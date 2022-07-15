package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/**
 * Wrapper class for {@link AlgorithmIdentifier}.
 */
public class AlgorithmIdentifierBCFips extends ASN1EncodableBCFips implements IAlgorithmIdentifier {
    /**
     * Creates new wrapper instance for {@link AlgorithmIdentifier}.
     *
     * @param algorithmIdentifier {@link AlgorithmIdentifier} to be wrapped
     */
    public AlgorithmIdentifierBCFips(AlgorithmIdentifier algorithmIdentifier) {
        super(algorithmIdentifier);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link AlgorithmIdentifier}.
     */
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return (AlgorithmIdentifier) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getAlgorithm() {
        return new ASN1ObjectIdentifierBCFips(getAlgorithmIdentifier().getAlgorithm());
    }
}
