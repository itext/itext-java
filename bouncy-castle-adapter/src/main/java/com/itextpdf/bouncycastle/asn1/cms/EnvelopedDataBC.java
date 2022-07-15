package com.itextpdf.bouncycastle.asn1.cms;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1SetBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;
import com.itextpdf.commons.bouncycastle.asn1.cms.IEncryptedContentInfo;
import com.itextpdf.commons.bouncycastle.asn1.cms.IEnvelopedData;
import com.itextpdf.commons.bouncycastle.asn1.cms.IOriginatorInfo;

import org.bouncycastle.asn1.cms.EnvelopedData;

/**
 * Wrapper class for {@link EnvelopedData}.
 */
public class EnvelopedDataBC extends ASN1EncodableBC implements IEnvelopedData {
    /**
     * Creates new wrapper instance for {@link EnvelopedData}.
     *
     * @param envelopedData {@link EnvelopedData} to be wrapped
     */
    public EnvelopedDataBC(EnvelopedData envelopedData) {
        super(envelopedData);
    }

    /**
     * Creates new wrapper instance for {@link EnvelopedData}.
     *
     * @param originatorInfo       OriginatorInfo wrapper to create {@link EnvelopedData}
     * @param set                  ASN1Set wrapper to create {@link EnvelopedData}
     * @param encryptedContentInfo EncryptedContentInfo wrapper to create {@link EnvelopedData}
     * @param set1                 ASN1Set wrapper to create {@link EnvelopedData}
     */
    public EnvelopedDataBC(IOriginatorInfo originatorInfo, IASN1Set set,
            IEncryptedContentInfo encryptedContentInfo, IASN1Set set1) {
        super(new EnvelopedData(
                ((OriginatorInfoBC) originatorInfo).getOriginatorInfo(),
                ((ASN1SetBC) set).getASN1Set(),
                ((EncryptedContentInfoBC) encryptedContentInfo).getEncryptedContentInfo(),
                ((ASN1SetBC) set1).getASN1Set()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link EnvelopedData}.
     */
    public EnvelopedData getEnvelopedData() {
        return (EnvelopedData) getEncodable();
    }
}