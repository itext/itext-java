package com.itextpdf.bouncycastlefips.cms;

import com.itextpdf.commons.bouncycastle.cms.IRecipient;

import java.util.Objects;
import org.bouncycastle.cms.Recipient;

/**
 * Wrapper class for {@link Recipient}.
 */
public class RecipientBCFips implements IRecipient {
    private final Recipient recipient;

    /**
     * Creates new wrapper instance for {@link Recipient}.
     *
     * @param recipient {@link Recipient} to be wrapped
     */
    public RecipientBCFips(Recipient recipient) {
        this.recipient = recipient;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link Recipient}.
     */
    public Recipient getRecipient() {
        return recipient;
    }

    /**
     * Indicates whether some other object is "equal to" this one. Compares wrapped objects.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecipientBCFips that = (RecipientBCFips) o;
        return Objects.equals(recipient, that.recipient);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(recipient);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return recipient.toString();
    }
}
