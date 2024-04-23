package com.itextpdf.signatures.validation.v1;

import com.itextpdf.signatures.IssuingCertificateRetriever;

import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class MockIssuingCertificateRetriever extends IssuingCertificateRetriever {
    public List<Certificate[]> retrieveMissingCertificatesCalls = new ArrayList<>();
    public List<CRL> getCrlIssuerCertificatesCalls = new ArrayList<>();
    public List<Collection<Certificate>> setTrustedCertificatesCalls = new ArrayList<>();
    public List<Collection<Certificate>> addKnownCertificatesCalls = new ArrayList<>();
    public List<Certificate> isCertificateTrustedDoCalls = new ArrayList<>();

    private Function<Certificate[], Certificate[]> retrieveMissingCertificatesHandler;
    private Function<CRL, Certificate[]> getCrlIssuerCertificatesHandler;
    private Consumer<Collection<Certificate>> setTrustedCertificatesHandler;
    private Consumer<Collection<Certificate>> addKnownCertificatesHandler;
    private Function<Certificate, Boolean> isCertificateTrustedDoHandler;

    @Override
    public Certificate[] retrieveMissingCertificates(Certificate[] chain) {
        retrieveMissingCertificatesCalls.add(chain);
        if (retrieveMissingCertificatesHandler != null) {
            return retrieveMissingCertificatesHandler.apply(chain);
        }
        return new Certificate[0];
    }

    @Override
    public Certificate[] getCrlIssuerCertificates(CRL crl) {
        getCrlIssuerCertificatesCalls.add(crl);
        if (getCrlIssuerCertificatesHandler != null) {
            return getCrlIssuerCertificatesHandler.apply(crl);
        }
        return new Certificate[0];
    }

    @Override
    public void setTrustedCertificates(Collection<Certificate> certificates) {
        setTrustedCertificatesCalls.add(certificates);
        if (setTrustedCertificatesHandler != null) {
            setTrustedCertificatesHandler.accept(certificates);
        }
    }

    @Override
    public void addKnownCertificates(Collection<Certificate> certificates) {
        addKnownCertificatesCalls.add(certificates);
        if (addKnownCertificatesHandler != null) {
            addKnownCertificatesHandler.accept(certificates);
        }
    }

    @Override
    public boolean isCertificateTrusted(Certificate certificate) {
        isCertificateTrustedDoCalls.add(certificate);
        if (isCertificateTrustedDoHandler != null) {
            return isCertificateTrustedDoHandler.apply(certificate);
        }
        return true;
    }

    public MockIssuingCertificateRetriever onRetrieveMissingCertificatesDo(Function<Certificate[], Certificate[]> callback) {
        retrieveMissingCertificatesHandler = callback;
        return this;
    }

    public MockIssuingCertificateRetriever ongetCrlIssuerCertificatesDo(Function<CRL, Certificate[]> callback) {
        getCrlIssuerCertificatesHandler = callback;
        return this;
    }

    public MockIssuingCertificateRetriever onSetTrustedCertificatesDo(Consumer<Collection<Certificate>> callback) {
        setTrustedCertificatesHandler = callback;
        return this;
    }

    public MockIssuingCertificateRetriever onAddKnownCertificatesDo(Consumer<Collection<Certificate>> callback) {
        addKnownCertificatesHandler = callback;
        return this;
    }

    public MockIssuingCertificateRetriever onIsCertificateTrustedDo(Function<Certificate, Boolean> callback) {
        isCertificateTrustedDoHandler = callback;
        return this;
    }
}
