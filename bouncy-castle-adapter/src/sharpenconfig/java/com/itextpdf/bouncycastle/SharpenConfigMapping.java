/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.bouncycastle;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import sharpen.config.MappingConfiguration;
import sharpen.config.MappingConfigurator;
import sharpen.config.ModuleOption;
import sharpen.config.ModulesConfigurator;
import sharpen.config.OptionsConfigurator;

public class SharpenConfigMapping implements MappingConfiguration {
    @Override
    public int getMappingPriority() {
        return 100 - 4;
    }

    @Override
    public String getModuleName() {
        return "bouncycastle";
    }

    @Override
    public void applyMappingConfiguration(MappingConfigurator configurator) {
        configurator.mapType("com.itextpdf.bouncycastle.asn1.DEROctetStringBC", "iText.Bouncycastle.Asn1.DerOctetStringBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.ASN1OctetStringBC", "iText.Bouncycastle.Asn1.Asn1OctetStringBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.ASN1SetBC", "iText.Bouncycastle.Asn1.Asn1SetBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.ASN1IntegerBC", "iText.Bouncycastle.Asn1.DerIntegerBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.ASN1PrimitiveBC", "iText.Bouncycastle.Asn1.Asn1ObjectBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.ASN1EncodableBC", "iText.Bouncycastle.Asn1.Asn1EncodableBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.ASN1SequenceBC", "iText.Bouncycastle.Asn1.Asn1SequenceBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.ASN1InputStreamBC", "iText.Bouncycastle.Asn1.Asn1InputStreamBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.x509.TBSCertificateBC", "Org.BouncyCastle.Asn1.X509.TbsCertificateStructureBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.cmp.PKIFailureInfoBC", "iText.Bouncycastle.Asn1.Cmp.PkiFailureInfoBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC", "iText.Bouncycastle.Asn1.DerObjectIdentifierBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.ASN1TaggedObjectBC", "iText.Bouncycastle.Asn1.Asn1TaggedObjectBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.ASN1EncodableVectorBC", "iText.Bouncycastle.Asn1.Asn1EncodableVectorBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.DERIA5StringBC", "iText.Bouncycastle.Asn1.DerIA5StringBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.ASN1BitStringBC", "iText.Bouncycastle.Asn1.DerBitStringBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.ASN1GeneralizedTimeBC", "iText.Bouncycastle.Asn1.DerGeneralizedTimeBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.ASN1OutputStreamBC", "iText.Bouncycastle.Asn1.DerOutputStreamBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.DERNullBC", "iText.Bouncycastle.Asn1.DerNullBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.ASN1EnumeratedBC", "iText.Bouncycastle.Asn1.DerEnumeratedBC");
        configurator.mapType("org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiersBC", "iText.Bouncycastle.Asn1.Ocsp.OcspObjectIdentifiersBC");
        configurator.mapType("com.itextpdf.bouncycastle.cert.ocsp.OCSPExceptionBC", "iText.Bouncycastle.Cert.Ocsp.OcspExceptionBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.pkcs.PKCSObjectIdentifiersBC", "iText.Bouncycastle.Asn1.Pkcs.PkcsObjectIdentifiersBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.pcks.PKCSObjectIdentifiersBC", "iText.Bouncycastle.Asn1.Pcks.PkcsObjectIdentifiersBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.ocsp.OCSPResponseStatusBC", "iText.Bouncycastle.Asn1.Ocsp.OcspResponseStatusBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.x509.ExtensionBC", "iText.Bouncycastle.Asn1.X509.X509ExtensionBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.x509.ExtensionsBC", "iText.Bouncycastle.Asn1.X509.X509ExtensionsBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.x500.X500NameBC", "iText.Bouncycastle.Asn1.X509.X509NameBC");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.ASN1InputStreamBC.close", "Dispose");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.ASN1OutputStreamBC.close", "Dispose");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.ocsp.BasicOCSPResponseBC.getBasicOCSPResponse", "GetBasicOcspResponse");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.ocsp.OCSPObjectIdentifiersBC.getOCSPObjectIdentifiers", "GetOcspObjectIdentifiers");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.pcks.PKCSObjectIdentifiersBC.getPKCSObjectIdentifiers", "GetPkcsObjectIdentifiers");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.x509.CRLReasonBC.getCRLReason", "GetCrlReason");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.x509.KeyPurposeIdBC.getKeyPurposeId", "GetKeyPurposeID");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.ASN1BitStringBC.getASN1BitString", "GetDerBitString");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.ASN1GeneralizedTimeBC.getASN1GeneralizedTime", "GetDerGeneralizedTime");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.DERSetBC.getDERSet", "GetDerSet");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.DERTaggedObjectBC.getDERTaggedObject", "GetDerTaggedObject");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.ASN1UTCTimeBC.getASN1UTCTime", "GetDerUtcTime");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.ASN1InputStreamBC.getASN1InputStream", "GetAsn1InputStream");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.ASN1SequenceBC.getASN1Sequence", "GetAsn1Sequence");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.ASN1SetBC.getASN1Set", "GetAsn1Set");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.ASN1TaggedObjectBC.getASN1TaggedObject", "GetAsn1TaggedObject");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC.getASN1ObjectIdentifier", "GetASN1ObjectIdentifier");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.ASN1BitStringBC.getASN1BitString", "GetDerBitString");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.ASN1GeneralizedTimeBC.getASN1BitString", "GetDerGeneralizedTime");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.DERNullBC.getDERNull", "GetDerNull");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC.getASN1ObjectIdentifier", "GetDerObjectIdentifier");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.DEROctetStringBC.getDEROctetString", "GetDerOctetString");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.ASN1OutputStreamBC.getASN1OutputStream", "GetDerOutputStream");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.DERSequenceBC.getDERSequence", "GetDerSequence");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.DERSetBC.getDERSet", "GetDerSet");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.DERTaggedObjectBC.getDERTaggedObject", "GetDerTaggedObject");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.x509.TBSCertificateBC.getTBSCertificate", "GetTbsCertificateStructure");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.ASN1OctetStringBC.GetASN1OctetString", "GetAsn1OctetString");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.ASN1InputStreamBC.getASN1InputStream", "GetAsn1InputStream");
        configurator.mapMethod("com.itextpdf.bouncycastle.cms.CMSExceptionBC.getCMSException", "GetCmsException");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.x509.CRLReasonBC.getCRLReason", "GetCrlReason");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.ASN1OctetStringBC.getASN1OctetString", "GetAsn1OctetString");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.ASN1EnumeratedBC.getASN1Enumerated", "GetDerEnumerated");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.ASN1StringBC.getASN1String", "GetDerStringBase");
        configurator.mapMethod("com.itextpdf.bouncycastle.asn1.x500.X500NameBC.getX500Name", "GetX509Name");
        configurator.removeMethod("com.itextpdf.bouncycastle.asn1.x509.SubjectPublicKeyInfoBC.SubjectPublicKeyInfoBC(java.lang.Object)");
        configurator.mapType("com.itextpdf.bouncycastle.cms.CMSExceptionBC", "iText.Bouncycastle.Cms.CmsExceptionBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.ASN1StringBC", "iText.Bouncycastle.Asn1.DerStringBaseBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.ASN1UTCTimeBC", "iText.Bouncycastle.Asn1.DerUtcTimeBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.ocsp.OCSPObjectIdentifiersBC", "iText.Bouncycastle.Asn1.Ocsp.OcspObjectIdentifiersBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.DERSequenceBC", "iText.Bouncycastle.Asn1.DerSequenceBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.DERSetBC", "iText.Bouncycastle.Asn1.DerSetBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.DERTaggedObjectBC", "iText.Bouncycastle.Asn1.DerTaggedObjectBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.tsp.TSTInfoBC", "iText.Bouncycastle.Asn1.Tsp.TstInfoBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.util.ASN1DumpBC", "iText.Bouncycastle.Asn1.Util.Asn1DumpBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.x509.CRLDistPointBC", "iText.Bouncycastle.Asn1.X509.CrlDistPointBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.x509.CRLReasonBC", "iText.Bouncycastle.Asn1.X509.CrlReasonBC");
        configurator.mapType("com.itextpdf.bouncycastle.asn1.x509.KeyPurposeIdBC", "iText.Bouncycastle.Asn1.X509.KeyPurposeIDBC");
    }

    @Override
    public void applySharpenOptions(OptionsConfigurator configurator) {

    }

    @Override
    public void applyConfigModuleSettings(ModulesConfigurator configurator) {

    }

    @Override
    public void setConfigModuleSettings(ModulesConfigurator modulesConfigurator) {

    }

    @Override
    public Collection<ModuleOption> getAvailableModuleSettings() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Collection<String> getDependencies() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<String> getIgnoredSourceFiles() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<String> getIgnoredResources() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<SimpleImmutableEntry<String, String>> getOverwrittenResources() {
        return Collections.EMPTY_LIST;
    }
}
