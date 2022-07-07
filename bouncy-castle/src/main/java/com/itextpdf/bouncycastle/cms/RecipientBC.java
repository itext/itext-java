package com.itextpdf.bouncycastle.cms;

import com.itextpdf.commons.bouncycastle.cms.IRecipient;

import java.util.Objects;
import org.bouncycastle.cms.Recipient;

public class RecipientBC implements IRecipient {
    private final Recipient recipient;

    public RecipientBC(Recipient recipient) {
        this.recipient = recipient;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecipientBC that = (RecipientBC) o;
        return Objects.equals(recipient, that.recipient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipient);
    }

    @Override
    public String toString() {
        return recipient.toString();
    }
}
