package com.itextpdf.bouncycastlefips.asn1.tsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.AlgorithmIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.tsp.IMessageImprint;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import org.bouncycastle.asn1.tsp.MessageImprint;

/**
 * Wrapper class for {@link MessageImprint}.
 */
public class MessageImprintBCFips extends ASN1EncodableBCFips implements IMessageImprint {
    /**
     * Creates new wrapper instance for {@link MessageImprint}.
     *
     * @param messageImprint {@link MessageImprint} to be wrapped
     */
    public MessageImprintBCFips(MessageImprint messageImprint) {
        super(messageImprint);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link MessageImprint}.
     */
    public MessageImprint getMessageImprint() {
        return (MessageImprint) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getHashedMessage() {
        return getMessageImprint().getHashedMessage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAlgorithmIdentifier getHashAlgorithm() {
        return new AlgorithmIdentifierBCFips(getMessageImprint().getHashAlgorithm());
    }
}
