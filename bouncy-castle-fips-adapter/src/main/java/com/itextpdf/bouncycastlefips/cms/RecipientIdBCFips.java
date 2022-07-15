package com.itextpdf.bouncycastlefips.cms;

import com.itextpdf.bouncycastlefips.cert.X509CertificateHolderBCFips;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cms.IRecipientId;

import java.util.Objects;
import org.bouncycastle.cms.RecipientId;

/**
 * Wrapper class for {@link RecipientId}.
 */
public class RecipientIdBCFips implements IRecipientId {
    private final RecipientId recipientId;

    /**
     * Creates new wrapper instance for {@link RecipientId}.
     *
     * @param recipientId {@link RecipientId} to be wrapped
     */
    public RecipientIdBCFips(RecipientId recipientId) {
        this.recipientId = recipientId;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link RecipientId}.
     */
    public RecipientId getRecipientId() {
        return recipientId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean match(IX509CertificateHolder holder) {
        return recipientId.match(((X509CertificateHolderBCFips) holder).getCertificateHolder());
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
        RecipientIdBCFips that = (RecipientIdBCFips) o;
        return Objects.equals(recipientId, that.recipientId);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(recipientId);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return recipientId.toString();
    }
}
