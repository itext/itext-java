package com.itextpdf.signatures;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509CertificateConverter;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;


class XmlCertificateRetriever {

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    XmlCertificateRetriever() {
        //empty constructor
    }

    static List<Certificate> getCertificates(String path) {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        XmlCertificateHandler handler;
        try {
            handler = new XmlCertificateHandler();
            saxParserFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true); // Android-Conversion-Skip-Line (this feature is not supported in Android SDK)
            SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(new File(path), handler);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new PdfException(MessageFormatUtil.format(
                    SignExceptionMessageConstant.FAILED_TO_READ_CERTIFICATE_BYTES_FROM_XML, path), e);
        }

        List<byte[]> certificateBytes = handler.getCertificatesBytes();
        List<Certificate> certificates = new ArrayList<>();
        IJcaX509CertificateConverter converter =
                BOUNCY_CASTLE_FACTORY.createJcaX509CertificateConverter().setProvider(BOUNCY_CASTLE_FACTORY.getProvider());
        for (byte[] certificateByte : certificateBytes) {
            try {
                IX509CertificateHolder certificateHolder = BOUNCY_CASTLE_FACTORY.createX509CertificateHolder(certificateByte);
                certificates.add(converter.getCertificate(certificateHolder));
            } catch (CertificateException | IOException e) {
                throw new PdfException(SignExceptionMessageConstant.FAILED_TO_RETRIEVE_CERTIFICATE, e);
            }

        }

        return certificates;
    }
}