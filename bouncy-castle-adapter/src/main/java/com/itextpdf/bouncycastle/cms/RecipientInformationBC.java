package com.itextpdf.bouncycastle.cms;

import com.itextpdf.commons.bouncycastle.cms.IRecipient;
import com.itextpdf.commons.bouncycastle.cms.IRecipientId;
import com.itextpdf.commons.bouncycastle.cms.IRecipientInformation;

import java.util.Objects;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientInformation;

/**
 * Wrapper class for {@link RecipientInformation}.
 */
public class RecipientInformationBC implements IRecipientInformation {
    private final RecipientInformation recipientInformation;

    /**
     * Creates new wrapper instance for {@link RecipientInformation}.
     *
     * @param recipientInformation {@link RecipientInformation} to be wrapped
     */
    public RecipientInformationBC(RecipientInformation recipientInformation) {
        this.recipientInformation = recipientInformation;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link RecipientInformation}.
     */
    public RecipientInformation getRecipientInformation() {
        return recipientInformation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getContent(IRecipient recipient) throws CMSExceptionBC {
        try {
            return recipientInformation.getContent(((RecipientBC) recipient).getRecipient());
        } catch (CMSException e) {
            throw new CMSExceptionBC(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRecipientId getRID() {
        return new RecipientIdBC(recipientInformation.getRID());
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
        RecipientInformationBC that = (RecipientInformationBC) o;
        return Objects.equals(recipientInformation, that.recipientInformation);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(recipientInformation);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return recipientInformation.toString();
    }
}
