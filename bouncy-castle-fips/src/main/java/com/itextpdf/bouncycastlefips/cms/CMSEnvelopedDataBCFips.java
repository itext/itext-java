package com.itextpdf.bouncycastlefips.cms;

import com.itextpdf.commons.bouncycastle.cms.ICMSEnvelopedData;
import com.itextpdf.commons.bouncycastle.cms.IRecipientInformationStore;

import org.bouncycastle.cms.CMSEnvelopedData;

public class CMSEnvelopedDataBCFips implements ICMSEnvelopedData {

    private final CMSEnvelopedData cMSEnvelopedData;

    public CMSEnvelopedDataBCFips(CMSEnvelopedData cMSEnvelopedData) {
        this.cMSEnvelopedData = cMSEnvelopedData;
    }

    public CMSEnvelopedData getcMSEnvelopedData() {
        return cMSEnvelopedData;
    }

    @Override
    public IRecipientInformationStore getRecipientInfos() {
        return new RecipientInformationStoreBCFips(cMSEnvelopedData.getRecipientInfos());
    }
}
