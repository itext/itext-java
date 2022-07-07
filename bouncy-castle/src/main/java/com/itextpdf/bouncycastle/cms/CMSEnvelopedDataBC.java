package com.itextpdf.bouncycastle.cms;

import com.itextpdf.commons.bouncycastle.cms.ICMSEnvelopedData;
import com.itextpdf.commons.bouncycastle.cms.IRecipientInformationStore;

import java.util.Objects;
import org.bouncycastle.cms.CMSEnvelopedData;

public class CMSEnvelopedDataBC implements ICMSEnvelopedData {

    private final CMSEnvelopedData cmsEnvelopedData;

    public CMSEnvelopedDataBC(CMSEnvelopedData cmsEnvelopedData) {
        this.cmsEnvelopedData = cmsEnvelopedData;
    }

    public CMSEnvelopedData getCmsEnvelopedData() {
        return cmsEnvelopedData;
    }

    @Override
    public IRecipientInformationStore getRecipientInfos() {
        return new RecipientInformationStoreBC(cmsEnvelopedData.getRecipientInfos());
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(cmsEnvelopedData);
    }

    @Override
    public String toString() {
        return cmsEnvelopedData.toString();
    }
}
