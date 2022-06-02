package com.itextpdf.bouncycastle.asn1.tsp;

import com.itextpdf.commons.bouncycastle.asn1.tsp.IMessageImprint;

import org.bouncycastle.asn1.tsp.MessageImprint;

public class MessageImprintBC implements IMessageImprint {
    private final MessageImprint messageImprint;

    public MessageImprintBC(MessageImprint messageImprint) {
        this.messageImprint = messageImprint;
    }

    public MessageImprint getMessageImprint() {
        return messageImprint;
    }

    @Override
    public byte[] getHashedMessage() {
        return messageImprint.getHashedMessage();
    }
}
