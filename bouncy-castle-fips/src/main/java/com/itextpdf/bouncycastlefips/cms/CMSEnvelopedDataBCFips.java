package com.itextpdf.bouncycastlefips.cms;

import com.itextpdf.commons.bouncycastle.cms.ICMSEnvelopedData;
import com.itextpdf.commons.bouncycastle.cms.IRecipientInformationStore;

import java.util.Objects;
import org.bouncycastle.cms.CMSEnvelopedData;

public class CMSEnvelopedDataBCFips implements ICMSEnvelopedData {

    private final CMSEnvelopedData cmsEnvelopedData;

    public CMSEnvelopedDataBCFips(CMSEnvelopedData cmsEnvelopedData) {
        this.cmsEnvelopedData = cmsEnvelopedData;
    }

    public CMSEnvelopedData getCmsEnvelopedData() {
        return cmsEnvelopedData;
    }

    @Override
    public IRecipientInformationStore getRecipientInfos() {
        return new RecipientInformationStoreBCFips(cmsEnvelopedData.getRecipientInfos());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CMSEnvelopedDataBCFips that = (CMSEnvelopedDataBCFips) o;
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
