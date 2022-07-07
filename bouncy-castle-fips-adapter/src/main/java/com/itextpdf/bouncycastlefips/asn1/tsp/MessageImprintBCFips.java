package com.itextpdf.bouncycastlefips.asn1.tsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.tsp.IMessageImprint;

import org.bouncycastle.asn1.tsp.MessageImprint;

public class MessageImprintBCFips extends ASN1EncodableBCFips implements IMessageImprint {
    public MessageImprintBCFips(MessageImprint messageImprint) {
        super(messageImprint);
    }

    public MessageImprint getMessageImprint() {
        return (MessageImprint) getEncodable();
    }

    @Override
    public byte[] getHashedMessage() {
        return getMessageImprint().getHashedMessage();
    }
}
