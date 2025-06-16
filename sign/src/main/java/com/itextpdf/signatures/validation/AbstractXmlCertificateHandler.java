package com.itextpdf.signatures.validation;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509CertificateConverter;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.List;

abstract class AbstractXmlCertificateHandler extends DefaultHandler {

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final IJcaX509CertificateConverter X509_CERTIFICATE_CONVERTER = BOUNCY_CASTLE_FACTORY
            .createJcaX509CertificateConverter().setProvider(BOUNCY_CASTLE_FACTORY.getProvider());

    abstract IServiceContext getServiceContext(Certificate certificate);

    abstract List<Certificate> getCertificateList();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {}

    @Override
    public void endElement(String uri, String localName, String qName) {}

    @Override
    public void characters(char[] ch, int start, int length) {}

    Certificate getCertificateFromEncodedData(String certificateString) {
        try {
            byte[] bytes = Base64.getDecoder().decode(certificateString);
            IX509CertificateHolder certificateHolder = BOUNCY_CASTLE_FACTORY
                    .createX509CertificateHolder(bytes);
            return X509_CERTIFICATE_CONVERTER.getCertificate(certificateHolder);
        } catch (CertificateException | IOException e) {
            throw new PdfException(SignExceptionMessageConstant.FAILED_TO_RETRIEVE_CERTIFICATE, e);
        }
    }

    abstract void clear();
}
