package com.itextpdf.signatures.validation;

import java.security.cert.Certificate;
import java.util.List;

interface IServiceContext {

    List<Certificate> getCertificates();

    void addCertificate(Certificate certificate);
}
