package com.itextpdf.commons.bouncycastle.cert.jcajce;

import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;

/**
 * This interface represents the wrapper for JcaX509CertificateHolder that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IJcaX509CertificateHolder extends IX509CertificateHolder {
}
