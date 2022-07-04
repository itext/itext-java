package com.itextpdf.bouncycastle.cms;

import com.itextpdf.commons.bouncycastle.cms.ICMSEnvelopedData;
import com.itextpdf.commons.bouncycastle.cms.IRecipientInformationStore;

import org.bouncycastle.cms.CMSEnvelopedData;

public class CMSEnvelopedDataBC implements ICMSEnvelopedData{

    private final CMSEnvelopedData cMSEnvelopedData;

    public CMSEnvelopedDataBC(CMSEnvelopedData cMSEnvelopedData) {
        this.cMSEnvelopedData = cMSEnvelopedData;
    }

    public CMSEnvelopedData getcMSEnvelopedData() {
        return cMSEnvelopedData;
    }

    @Override
    public IRecipientInformationStore getRecipientInfos() {
        return new RecipientInformationStoreBC(cMSEnvelopedData.getRecipientInfos());
    }
}
