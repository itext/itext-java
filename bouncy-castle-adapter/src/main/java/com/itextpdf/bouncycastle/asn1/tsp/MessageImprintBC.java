package com.itextpdf.bouncycastle.asn1.tsp;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.x509.AlgorithmIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.tsp.IMessageImprint;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import org.bouncycastle.asn1.tsp.MessageImprint;

/**
 * Wrapper class for {@link MessageImprint}.
 */
public class MessageImprintBC extends ASN1EncodableBC implements IMessageImprint {
    /**
     * Creates new wrapper instance for {@link MessageImprint}.
     *
     * @param messageImprint {@link MessageImprint} to be wrapped
     */
    public MessageImprintBC(MessageImprint messageImprint) {
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
        return new AlgorithmIdentifierBC(getMessageImprint().getHashAlgorithm());
    }
}
