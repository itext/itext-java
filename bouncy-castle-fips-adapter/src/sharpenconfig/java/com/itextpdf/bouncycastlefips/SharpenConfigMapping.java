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
package com.itextpdf.bouncycastlefips;

import sharpen.config.MappingConfiguration;
import sharpen.config.MappingConfigurator;
import sharpen.config.ModuleOption;
import sharpen.config.ModulesConfigurator;
import sharpen.config.OptionsConfigurator;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SharpenConfigMapping implements MappingConfiguration {
    @Override
    public int getMappingPriority() {
        return 100 - 7;
    }

    @Override
    public String getModuleName() {
        return "bouncycastlefips";
    }

    @Override
    public void applyMappingConfiguration(MappingConfigurator configurator) {
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.ASN1OctetStringBCFips", "iText.Bouncycastlefips.Asn1.Asn1OctetStringBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.DEROctetStringBCFips", "iText.Bouncycastlefips.Asn1.DerOctetStringBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.ASN1SetBCFips", "iText.Bouncycastlefips.Asn1.Asn1SetBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.ASN1IntegerBCFips", "iText.Bouncycastlefips.Asn1.DerIntegerBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.ASN1PrimitiveBCFips", "iText.Bouncycastlefips.Asn1.Asn1ObjectBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips", "iText.Bouncycastlefips.Asn1.Asn1EncodableBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.ASN1InputStreamBCFips", "iText.Bouncycastlefips.Asn1.Asn1InputStreamBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.x509.TBSCertificateBCFips", "iText.Bouncycastlefips.Asn1.X509.TbsCertificateStructureBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.cmp.PKIFailureInfoBCFips", "iText.Bouncycastlefips.Asn1.Cmp.PkiFailureInfoBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips", "iText.Bouncycastlefips.Asn1.DerObjectIdentifierBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.ASN1TaggedObjectBCFips", "iText.Bouncycastlefips.Asn1.Asn1TaggedObjectBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.ASN1EncodableVectorBCFips", "iText.Bouncycastlefips.Asn1.Asn1EncodableVectorBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.DERIA5StringBCFips", "iText.Bouncycastlefips.Asn1.DerIA5StringBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.ASN1BitStringBCFips", "iText.Bouncycastlefips.Asn1.DerBitStringBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.ASN1GeneralizedTimeBCFips", "iText.Bouncycastlefips.Asn1.DerGeneralizedTimeBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.ASN1OutputStreamBCFips", "iText.Bouncycastlefips.Asn1.Asn1OutputStreamBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.DERNullBCFips", "iText.Bouncycastlefips.Asn1.DerNullBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.ASN1EnumeratedBCFips", "iText.Bouncycastlefips.Asn1.DerEnumeratedBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.ocsp.OCSPObjectIdentifiersBCFips", "iText.Bouncycastlefips.Asn1.Ocsp.OcspObjectIdentifiersBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.cert.ocsp.OCSPExceptionBCFips", "iText.Bouncycastlefips.Cert.Ocsp.OcspExceptionBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.pcks.PKCSObjectIdentifiersBCFips", "iText.Bouncycastlefips.Asn1.Pcks.PkcsObjectIdentifiersBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.pkcs.PKCSObjectIdentifiersBCFips", "iText.Bouncycastlefips.Asn1.Pkcs.PkcsObjectIdentifiersBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.pcks.RSASSAPSSParamsBCFips", "iText.Bouncycastlefips.Asn1.Pkcs.RsassaPssParameters");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.ocsp.OCSPResponseStatusBCFips", "iText.Bouncycastlefips.Asn1.Ocsp.OcspResponseStatusBCFips");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.ASN1InputStreamBCFips.close", "Dispose");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.ASN1OutputStreamBCFips.close", "Dispose");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.x509.KeyPurposeIdBCFips.getKeyPurposeId", "GetKeyPurposeID");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips.getASN1ObjectIdentifier", "GetDerObjectIdentifier");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.DERSetBCFips.getDERSet", "GetDerSet");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.DERTaggedObjectBCFips.getDERTaggedObject", "GetDerTaggedObject");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.ASN1UTCTimeBCFips.getASN1UTCTime", "GetDerUtcTime");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.ASN1InputStreamBC.getASN1InputStream", "GetAsn1InputStream");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.ASN1SequenceBCFips.getASN1Sequence", "GetAsn1Sequence");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.ASN1SetBCFips.getASN1Set", "GetAsn1Set");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.ASN1TaggedObjectBCFips.getASN1TaggedObject", "GetAsn1TaggedObject");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips.getASN1ObjectIdentifier", "GetASN1ObjectIdentifier");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.ASN1BitStringBCFips.getASN1BitString", "GetDerBitString");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.ASN1GeneralizedTimeBCFips.getASN1BitString", "GetDerGeneralizedTime");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.DERNullBCFips.getDERNull", "GetDerNull");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips.getASN1ObjectIdentifier", "GetDerObjectIdentifier");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.DEROctetStringBCFips.getDEROctetString", "GetDerOctetString");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.ASN1OutputStreamBCFips.getASN1OutputStream", "GetAsn1OutputStream");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.DERSequenceBCFips.getDERSequence", "GetDerSequence");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.DERSetBCFips.getDERSet", "GetDerSet");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.DERTaggedObjectBCFips.getDERTaggedObject", "GetDerTaggedObject");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.x509.TBSCertificateBCFips.getTBSCertificate", "GetTbsCertificateStructure");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.ASN1OctetStringBCFips.GetASN1OctetString", "GetAsn1OctetString");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.ASN1InputStreamBCFips.getASN1InputStream", "GetAsn1InputStream");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.cms.CMSExceptionBCFips.getCMSException", "GetCmsException");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.x509.CRLReasonBCFips.getCRLReason", "GetCrlReason");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.ASN1GeneralizedTimeBCFips.getASN1GeneralizedTime", "GetDerGeneralizedTime");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.ASN1OctetStringBCFips.getASN1OctetString", "GetAsn1OctetString");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.ASN1EnumeratedBCFips.getASN1Enumerated", "GetDerEnumerated");
        configurator.mapMethod("com.itextpdf.bouncycastlefips.asn1.ASN1StringBCFips.getAsn1String", "GetDerStringBase");
        configurator.removeMethod("com.itextpdf.bouncycastlefips.asn1.x509.SubjectPublicKeyInfoBCFips.SubjectPublicKeyInfoBCFips(java.lang.Object)");
        configurator.mapType("com.itextpdf.bouncycastlefips.cms.CMSExceptionBCFips", "iText.Bouncycastlefips.Cms.CmsExceptionBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.ASN1StringBCFips", "iText.Bouncycastlefips.Asn1.DerStringBaseBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.ASN1UTCTimeBCFips", "iText.Bouncycastlefips.Asn1.DerUtcTimeBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.DERSequenceBCFips", "iText.Bouncycastlefips.Asn1.DerSequenceBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.ASN1SequenceBCFips", "iText.Bouncycastlefips.Asn1.Asn1SequenceBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.DERSetBCFips", "iText.Bouncycastlefips.Asn1.DerSetBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.DERTaggedObjectBCFips", "iText.Bouncycastlefips.Asn1.DerTaggedObjectBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.tsp.TSTInfoBCFips", "iText.Bouncycastlefips.Asn1.Tsp.TstInfoBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.util.ASN1DumpBCFips", "iText.Bouncycastlefips.Asn1.Util.Asn1DumpBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.x509.CRLDistPointBCFips", "iText.Bouncycastlefips.Asn1.X509.CrlDistPointBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.x509.CRLReasonBCFips", "iText.Bouncycastlefips.Asn1.X509.CrlReasonBCFips");
        configurator.mapType("com.itextpdf.bouncycastlefips.asn1.x509.KeyPurposeIdBCFips", "iText.Bouncycastlefips.Asn1.X509.KeyPurposeIDBCFips");
        configurator.mapProperty("org.bouncycastle.asn1.x509.qualified.QCStatement.getStatementId", "StatementId");
        configurator.mapProperty("org.bouncycastle.asn1.x509.qualified.QCStatement.getStatementInfo", "StatementInfo");
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
