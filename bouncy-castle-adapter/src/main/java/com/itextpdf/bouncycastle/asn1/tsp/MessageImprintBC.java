package com.itextpdf.bouncycastle.asn1.tsp;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.tsp.IMessageImprint;

import org.bouncycastle.asn1.tsp.MessageImprint;

public class MessageImprintBC extends ASN1EncodableBC implements IMessageImprint {
    public MessageImprintBC(MessageImprint messageImprint) {
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
