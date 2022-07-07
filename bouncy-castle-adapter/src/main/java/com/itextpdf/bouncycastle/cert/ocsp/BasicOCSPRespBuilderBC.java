package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.bouncycastle.asn1.x509.ExtensionsBC;
import com.itextpdf.bouncycastle.cert.X509CertificateHolderBC;
import com.itextpdf.bouncycastle.operator.ContentSignerBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtensions;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPRespBuilder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateStatus;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IRespID;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;

import java.util.Date;
import java.util.Objects;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPRespBuilder;
import org.bouncycastle.cert.ocsp.OCSPException;

public class BasicOCSPRespBuilderBC implements IBasicOCSPRespBuilder {
    private final BasicOCSPRespBuilder basicOCSPRespBuilder;

    public BasicOCSPRespBuilderBC(BasicOCSPRespBuilder basicOCSPRespBuilder) {
        this.basicOCSPRespBuilder = basicOCSPRespBuilder;
    }

    public BasicOCSPRespBuilderBC(IRespID respID) {
        this(new BasicOCSPRespBuilder(((RespIDBC) respID).getRespID()));
    }

    public BasicOCSPRespBuilder getBasicOCSPRespBuilder() {
        return basicOCSPRespBuilder;
    }

    @Override
    public IBasicOCSPRespBuilder setResponseExtensions(IExtensions extensions) {
        basicOCSPRespBuilder.setResponseExtensions(((ExtensionsBC) extensions).getExtensions());
        return this;
    }

    @Override
    public IBasicOCSPRespBuilder addResponse(ICertificateID certID, ICertificateStatus certificateStatus, Date time,
            Date time1, IExtensions extensions) {
        basicOCSPRespBuilder.addResponse(
                ((CertificateIDBC) certID).getCertificateID(),
                ((CertificateStatusBC) certificateStatus).getCertificateStatus(), time, time1,
                ((ExtensionsBC) extensions).getExtensions());
        return this;
    }

    @Override
    public IBasicOCSPResp build(IContentSigner signer, IX509CertificateHolder[] chain, Date time)
            throws OCSPExceptionBC {
        try {
            X509CertificateHolder[] certificateHolders = new X509CertificateHolder[chain.length];
            for (int i = 0; i < chain.length; ++i) {
                certificateHolders[i] = ((X509CertificateHolderBC) chain[i]).getCertificateHolder();
            }
            return new BasicOCSPRespBC(basicOCSPRespBuilder.build(
                    ((ContentSignerBC) signer).getContentSigner(), certificateHolders, time));
        } catch (OCSPException e) {
            throw new OCSPExceptionBC(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicOCSPRespBuilderBC that = (BasicOCSPRespBuilderBC) o;
        return Objects.equals(basicOCSPRespBuilder, that.basicOCSPRespBuilder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(basicOCSPRespBuilder);
    }

    @Override
    public String toString() {
        return basicOCSPRespBuilder.toString();
    }
}
