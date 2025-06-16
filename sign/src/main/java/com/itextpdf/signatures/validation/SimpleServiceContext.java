package com.itextpdf.signatures.validation;

import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;

class SimpleServiceContext implements IServiceContext {

    private List<Certificate> certificates;

    SimpleServiceContext(Certificate certificate) {
        this.certificates = new ArrayList<>();
        certificates.add(certificate);
    }

    @Override
    public List<Certificate> getCertificates() {
        return new ArrayList<>(certificates);
    }

    @Override
    public void addCertificate(Certificate certificate) {
        if (certificates == null) {
            certificates = new ArrayList<>();
        }

        certificates.add(certificate);
    }
}
