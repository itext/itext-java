package com.itextpdf.bouncycastle.cms;

import com.itextpdf.commons.bouncycastle.cms.ICMSEnvelopedData;
import com.itextpdf.commons.bouncycastle.cms.IRecipientInformationStore;

import java.util.Objects;
import org.bouncycastle.cms.CMSEnvelopedData;

/**
 * Wrapper class for {@link CMSEnvelopedData}.
 */
public class CMSEnvelopedDataBC implements ICMSEnvelopedData {
    private final CMSEnvelopedData cmsEnvelopedData;

    /**
     * Creates new wrapper instance for {@link CMSEnvelopedData}.
     *
     * @param cmsEnvelopedData {@link CMSEnvelopedData} to be wrapped
     */
    public CMSEnvelopedDataBC(CMSEnvelopedData cmsEnvelopedData) {
        this.cmsEnvelopedData = cmsEnvelopedData;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link CMSEnvelopedData}.
     */
    public CMSEnvelopedData getCmsEnvelopedData() {
        return cmsEnvelopedData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRecipientInformationStore getRecipientInfos() {
        return new RecipientInformationStoreBC(cmsEnvelopedData.getRecipientInfos());
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
        CMSEnvelopedDataBC that = (CMSEnvelopedDataBC) o;
        return Objects.equals(cmsEnvelopedData, that.cmsEnvelopedData);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(cmsEnvelopedData);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return cmsEnvelopedData.toString();
    }
}
