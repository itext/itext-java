package com.itextpdf.signatures.testutils;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.util.*;

/**
 * Mock implementation of X509Certificate. Setters have been provided for data that is being tested in tests.
 */
public class X509MockCertificate extends X509Certificate {

    private boolean hasUnsupportedCriticalExtension;
    private Set<String> criticalExtensionOIDs;
    private boolean[] keyUsage;
    private List<String> extendedKeyUsage;

    public X509MockCertificate() {
        this.criticalExtensionOIDs = new HashSet<>();
    }

    @Override
    public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {

    }

    @Override
    public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {

    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public BigInteger getSerialNumber() {
        return null;
    }

    @Override
    public Principal getIssuerDN() {
        return null;
    }

    @Override
    public Principal getSubjectDN() {
        return null;
    }

    @Override
    public Date getNotBefore() {
        return null;
    }

    @Override
    public Date getNotAfter() {
        return null;
    }

    @Override
    public byte[] getTBSCertificate() throws CertificateEncodingException {
        return new byte[0];
    }

    @Override
    public byte[] getSignature() {
        return new byte[0];
    }

    @Override
    public String getSigAlgName() {
        return null;
    }

    @Override
    public String getSigAlgOID() {
        return null;
    }

    @Override
    public byte[] getSigAlgParams() {
        return new byte[0];
    }

    @Override
    public boolean[] getIssuerUniqueID() {
        return new boolean[0];
    }

    @Override
    public boolean[] getSubjectUniqueID() {
        return new boolean[0];
    }

    public X509MockCertificate setKeyUsage(boolean...keyUsage) {
        this.keyUsage = keyUsage;
        return this;
    }

    @Override
    public boolean[] getKeyUsage() {
        return this.keyUsage;
    }

    @Override
    public int getBasicConstraints() {
        return 0;
    }

    @Override
    public byte[] getEncoded() throws CertificateEncodingException {
        return new byte[0];
    }

    @Override
    public void verify(PublicKey key) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {

    }

    @Override
    public void verify(PublicKey key, String sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {

    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public PublicKey getPublicKey() {
        return null;
    }

    public X509MockCertificate setHasUnsupportedCriticalExtension(boolean hasUnsupportedCriticalExtension) {
        this.hasUnsupportedCriticalExtension = hasUnsupportedCriticalExtension;
        return this;
    }

    @Override
    public boolean hasUnsupportedCriticalExtension() {
        return this.hasUnsupportedCriticalExtension;
    }

    @Override
    public Set<String> getCriticalExtensionOIDs() {
        return this.criticalExtensionOIDs;
    }

    public X509MockCertificate setCriticalExtensionOIDs(String...oids) {
        this.criticalExtensionOIDs = new HashSet<>(Arrays.asList(oids));;
        return this;
    }

    @Override
    public Set<String> getNonCriticalExtensionOIDs() {
        return null;
    }

    @Override
    public byte[] getExtensionValue(String oid) {
        return new byte[0];
    }

    public X509MockCertificate setExtendedKeyUsage(String...extendedKeyUsage) {
        this.extendedKeyUsage = Arrays.asList(extendedKeyUsage);
        return this;
    }

    @Override
    public List<String> getExtendedKeyUsage() throws CertificateParsingException {
        return this.extendedKeyUsage;
    }
}