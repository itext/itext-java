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
package com.itextpdf.commons;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import sharpen.config.MappingConfiguration;
import sharpen.config.MappingConfigurator;
import sharpen.config.MemberKind;
import sharpen.config.ModuleOption;
import sharpen.config.ModulesConfigurator;
import sharpen.config.OptionsConfigurator;

public class SharpenConfigMapping implements MappingConfiguration {
    @Override
    public int getMappingPriority() {
        return 15;
    }

    @Override
    public String getModuleName() {
        return "commons";
    }

    @Override
    public void applyMappingConfiguration(MappingConfigurator configurator) {
        configurator.mapMethod("java.text.Normalizer.normalize", "iText.Commons.Utils.StringUtil.Normalize");
        configurator.mapType("java.util.regex.Matcher", "iText.Commons.Utils.Matcher");
        configurator.mapMethod("java.util.regex.Pattern.compile", "iText.Commons.Utils.StringUtil.RegexCompile");
        configurator.mapMethod("java.util.regex.Pattern.matcher", "iText.Commons.Utils.Matcher.Match");
        configurator.mapMethod("java.lang.String.replaceAll", "iText.Commons.Utils.StringUtil.ReplaceAll");
        configurator.mapMethod("java.lang.String.split", "iText.Commons.Utils.StringUtil.Split");
        configurator.mapMethod("java.util.regex.Pattern.split", "iText.Commons.Utils.StringUtil.Split");
        configurator.mapType("java.io.FileFilter", "iText.Commons.Utils.FileUtil.IFileFilter");
        configurator.mapType("java.util.EnumSet<>", "iText.Commons.Utils.Collections.EnumSet");
        configurator.addCustomUsingForMethodInvocation("java.util.Map.computeIfAbsent",  Collections.singletonList("iText.Commons.Utils.Collections"));
        configurator.addCustomUsingForMethodInvocation("java.util.Map.getOrDefault",  Collections.singletonList("iText.Commons.Utils.Collections"));
        configurator.mapMethod("java.lang.Integer.toHexString", "iText.Commons.Utils.JavaUtil.IntegerToHexString", false);
        configurator.mapMethod("java.lang.Integer.toOctalString", "iText.Commons.Utils.JavaUtil.IntegerToOctalString", false);
        configurator.mapMethod("java.lang.Integer.toString", "iText.Commons.Utils.JavaUtil.IntegerToString", false);
        configurator.mapMethod("java.lang.Integer.compare", "iText.Commons.Utils.JavaUtil.IntegerCompare", false);
        configurator.mapMethod("java.lang.Float.compare", "iText.Commons.Utils.JavaUtil.FloatCompare", false);
        configurator.mapMethod("java.lang.Double.compare", "iText.Commons.Utils.JavaUtil.DoubleCompare", false);
        configurator.mapType("java.util.Properties", "iText.Commons.Utils.Properties");
        configurator.mapType("java.util.LinkedHashMap<,>", "iText.Commons.Utils.LinkedDictionary");
        configurator.mapType("java.util.LinkedHashSet<>", "iText.Commons.Utils.LinkedHashSet");
        configurator.mapMethod("java.lang.Math.round", "iText.Commons.Utils.MathematicUtil.Round", false);
        configurator.mapMethod("java.lang.Math.random", "iText.Commons.Utils.JavaUtil.Random", false);
        configurator.mapMethod("java.util.Arrays.copyOf", "iText.Commons.Utils.JavaUtil.ArraysCopyOf", false);
        configurator.mapMethod("java.util.Arrays.copyOfRange", "iText.Commons.Utils.JavaUtil.ArraysCopyOfRange", false);
        configurator.mapMethod("java.lang.Character.digit", "iText.Commons.Utils.JavaUtil.CharacterDigit", false);
        configurator.mapMethod("java.lang.Character.UnicodeScript.of", "iText.Commons.Utils.UnicodeScriptUtil.Of", false);
        configurator.mapType("java.lang.Character.UnicodeScript", "iText.Commons.Utils.UnicodeScript?");
        configurator.mapType("java.util.concurrent.atomic.AtomicLong", "iText.Commons.Utils.AtomicLong");
        configurator.mapType("java.lang.FunctionalInterface", "iText.Commons.Utils.FunctionalInterfaceAttribute");
        configurator.mapMethod("java.util.Arrays.stream", "iText.Commons.Utils.JavaUtil.ArraysToEnumerable");
        configurator.mapType("java.util.IdentityHashMap<,>", "iText.Commons.Utils.IdentityDictionary");
        configurator.mapType("java.security.cert.X509CRLEntry", "iText.Commons.Bouncycastle.Cert.IX509CrlEntry");
        configurator.mapType("java.io.PrintStream", "iText.Commons.Utils.FormattingStreamWriter");
        configurator.mapType("java.io.PrintWriter", "iText.Commons.Utils.FormattingStreamWriter");
        configurator.mapMethod("java.nio.charset.Charset.forName", "iText.Commons.Utils.EncodingUtil.GetEncoding", false);
        configurator.mapField("java.nio.charset.StandardCharsets.ISO_8859_1", "iText.Commons.Utils.EncodingUtil.ISO_8859_1");
        configurator.mapType("java.io.PushbackReader", "iText.Commons.Utils.PushbackReader");
        configurator.mapType("java.io.FilterReader", "iText.Commons.Utils.FilterReader");
        configurator.mapMethod("java.lang.String.valueOf(char[])", "iText.Commons.Utils.JavaUtil.GetStringForChars", false);
        configurator.mapMethod("java.lang.String.valueOf(char[],int,int)", "iText.Commons.Utils.JavaUtil.GetStringForChars", false);
        configurator.mapMethod("java.lang.String.String(byte[])", "iText.Commons.Utils.JavaUtil.getStringForBytes", false);
        configurator.mapMethod("java.lang.String.String(byte[],int,int)", "iText.Commons.Utils.JavaUtil.getStringForBytes", false);
        configurator.mapMethod("java.lang.String.String(byte[],int,int,java.lang.String)", "iText.Commons.Utils.JavaUtil.getStringForBytes", false);
        configurator.mapMethod("java.lang.String.String(byte[],java.lang.String)", "iText.Commons.Utils.JavaUtil.getStringForBytes", false);
        configurator.mapMethod("java.lang.String.String(byte[],int,int,java.nio.charset.Charset)", "iText.Commons.Utils.JavaUtil.getStringForBytes", false);
        configurator.mapMethod("java.lang.String.String(byte[],java.nio.charset.Charset)", "iText.Commons.Utils.JavaUtil.getStringForBytes", false);
        configurator.mapMethod("java.lang.Float.intBitsToFloat(int)", "iText.Commons.Utils.JavaUtil.IntBitsToFloat", false);
        configurator.mapMethod("java.lang.Float.floatToIntBits(float)", "iText.Commons.Utils.JavaUtil.FloatToIntBits", false);
        configurator.mapMethod("java.lang.Double.longBitsToDouble(long)", "iText.Commons.Utils.JavaUtil.LongBitsToDouble", false);
        configurator.mapMethod("java.lang.Double.doubleToLongBits(double)", "iText.Commons.Utils.JavaUtil.DoubleToLongBits", false);
        configurator.mapType("java.util.StringTokenizer", "iText.Commons.Utils.StringTokenizer");
        configurator.mapMethod("java.util.Arrays.equals", "iText.Commons.Utils.JavaUtil.ArraysEquals", false);
        configurator.mapMethod("java.util.Objects.hash", "iText.Commons.Utils.JavaUtil.ArraysHashCode", false);
        configurator.mapMethod("java.util.Arrays.hashCode", "iText.Commons.Utils.JavaUtil.ArraysHashCode", false);
        configurator.mapMethod("java.util.Arrays.toString", "iText.Commons.Utils.JavaUtil.ArraysToString", false);
        configurator.mapMethod("java.util.Arrays.asList", "iText.Commons.Utils.JavaUtil.ArraysAsList", false);
        configurator.mapMethod("java.util.Arrays.<>asList", "iText.Commons.Utils.JavaUtil.ArraysAsList", false);
        configurator.mapMethod("java.util.Arrays.binarySearch", "iText.Commons.Utils.JavaUtil.ArraysBinarySearch", false);
        configurator.mapMethod("java.util.Arrays.fill", "iText.Commons.Utils.JavaUtil.Fill", false);
        configurator.mapMethod("java.util.Arrays.sort", "iText.Commons.Utils.JavaUtil.Sort", false);
        configurator.mapMethod("java.lang.Character.isValidCodePoint", "iText.Commons.Utils.JavaUtil.IsValidCodePoint", false);
        configurator.mapMethod("java.lang.Character.toCodePoint", "iText.Commons.Utils.JavaUtil.ToCodePoint", false);
        configurator.mapType("java.util.Collections", "iText.Commons.Utils.JavaCollectionsUtil");
        configurator.mapMethod("java.util.Collections.sort", "iText.Commons.Utils.JavaCollectionsUtil.Sort", false);
        configurator.mapMethod("java.util.AbstractMap.equals", "iText.Commons.Utils.JavaUtil.DictionariesEquals", false);
        configurator.mapMethod("java.util.AbstractMap.hashCode", "iText.Commons.Utils.JavaUtil.DictionaryHashCode", false);
        configurator.mapMethod("java.util.AbstractSet.equals", "iText.Commons.Utils.JavaUtil.SetEquals", false);
        configurator.mapMethod("java.util.AbstractSet.hashCode", "iText.Commons.Utils.JavaUtil.SetHashCode", false);
        configurator.mapMethodParametersOrder("com.itextpdf.commons.utils.JsonUtil.deserializeFromString", "1");
        configurator.mapMethodParametersOrder("com.itextpdf.commons.utils.JsonUtil.deserializeFromStream", "1");
        configurator.mapMethod("com.itextpdf.commons.utils.EncodingUtil.fromBase64", "System.Convert.FromBase64String", false);
        configurator.mapMethod("com.itextpdf.commons.utils.EncodingUtil.toBase64", "System.Convert.ToBase64String", false);
        configurator.mapType("com.itextpdf.commons.utils.ThrowingSupplier<>", "System.Func");
        configurator.mapType("com.itextpdf.commons.utils.Action", "System.Action");
        configurator.mapType("com.itextpdf.commons.utils.ThrowingAction", "System.Action");
        configurator.mapFunctionalInterfaceToDelegate("com.itextpdf.commons.utils.Action");
        configurator.mapFunctionalInterfaceToDelegate("com.itextpdf.commons.utils.ThrowingAction");
        configurator.mapFunctionalInterfaceToDelegate("com.itextpdf.commons.utils.IOThrowingAction");
        configurator.mapFunctionalInterfaceToDelegate("com.itextpdf.commons.utils.ThrowingSupplier");
        configurator.keepInternalProtected("com.itextpdf.commons.ecosystem.TestConfigurationEvent.doAction");
        configurator.addCustomUsingDeclaration("com.itextpdf.kernel.colors.DeviceRgb", Arrays.asList("iText.Commons.Utils"));
        configurator.mapMethod("com.itextpdf.commons.utils.SystemUtil.getPropertyOrEnvironmentVariable", "iText.Commons.Utils.SystemUtil.GetEnvironmentVariable", false);
        configurator.mapMethod("com.itextpdf.io.util.TextUtil.charToString", "iText.Commons.Utils.JavaUtil.CharToString", false);
        configurator.removeField("com.itextpdf.commons.actions.contexts.ContextManager.SECURITY_ERROR_LOGGING_INTERVAL");
        configurator.removeField("com.itextpdf.commons.actions.contexts.ContextManager.securityErrorLastLogged");
        configurator.mapType("org.slf4j.LoggerFactory", "iText.Commons.ITextLogManager");
        configurator.mapMethod("com.itextpdf.commons.utils.DateTimeUtil.getCurrentTimeCalendar", "GetCurrentTime");
        configurator.mapMethod("com.itextpdf.commons.utils.DateTimeUtil.addDaysToCalendar", "AddDays");
        configurator.mapMethod("com.itextpdf.commons.utils.DateTimeUtil.addDaysToDate", "AddDays");
        configurator.mapMethod("com.itextpdf.commons.utils.DateTimeUtil.addYearsToDate", "AddYears");
        configurator.mapMethod("com.itextpdf.commons.utils.DateTimeUtil.getCurrentTimeDate", "GetCurrentUtcTime");
        

        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.x509.ITBSCertificate", "iText.Commons.Bouncycastle.Asn1.X509.ITbsCertificateStructure");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1OutputStream", "iText.Commons.Bouncycastle.Asn1.IDerOutputStream");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.IX509CRLHolder", "iText.Commons.Bouncycastle.Cert.IX509Crl");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder", "iText.Commons.Bouncycastle.Cert.IX509Certificate");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.pkcs.IRSASSAPSSParams", "iText.Commons.Bouncycastle.Asn1.Pkcs.IRsassaPssParameters");
        configurator.mapMethod("com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream.close", "Dispose");
        configurator.mapMethod("com.itextpdf.commons.bouncycastle.asn1.IASN1OutputStream.close", "Dispose");
        configurator.mapMethod("com.itextpdf.commons.bouncycastle.IBouncyCastleFactory.createBasicOCSPResp", "CreateBasicOCSPResponse");
        configurator.mapMethod("com.itextpdf.commons.bouncycastle.IBouncyCastleFactory.createOCSPResp", "CreateOCSPResponse");
        configurator.mapMethod("com.itextpdf.commons.bouncycastle.IBouncyCastleFactory.createOCSPRespBuilderInstance", "CreateOCSPResponseStatus");
        configurator.unwrapInvocation("com.itextpdf.commons.bouncycastle.IBouncyCastleFactory.createJcaX509CertificateHolder");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream", "iText.Commons.Bouncycastle.Asn1.IAsn1InputStream");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cms.AbstractCMSException", "iText.Commons.Bouncycastle.Cms.AbstractCmsException");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException", "iText.Commons.Bouncycastle.Cert.Ocsp.AbstractOcspException");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509v3CertificateBuilder", "iText.Commons.Bouncycastle.Cert.IX509V3CertificateGenerator");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilderBC", "iText.Commons.Bouncycastle.Cert.X509V3CertificateGeneratorBC");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.IX509v2CRLBuilder", "iText.Commons.Bouncycastle.Cert.IX509V2CrlGenerator");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPRespBuilder", "iText.Commons.Bouncycastle.Cert.Ocsp.IBasicOcspRespGenerator");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReq", "iText.Commons.Bouncycastle.Cert.Ocsp.IOcspRequest");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReqBuilder", "iText.Commons.Bouncycastle.Cert.Ocsp.IOcspReqGenerator");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive", "iText.Commons.Bouncycastle.Asn1.IAsn1Object");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1String", "iText.Commons.Bouncycastle.Asn1.IDerStringBase");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID", "iText.Commons.Bouncycastle.Cert.Ocsp.ICertID");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateStatus", "iText.Commons.Bouncycastle.Cert.Ocsp.ICertStatus");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.ocsp.IUnknownStatus", "iText.Commons.Bouncycastle.Cert.Ocsp.IUnknownCertStatus");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.ocsp.IRevokedStatus", "iText.Commons.Bouncycastle.Cert.Ocsp.IRevokedCertStatus");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1Encoding", "iText.Commons.Bouncycastle.Asn1.IAsn1Encoding");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1UTCTime", "iText.Commons.Bouncycastle.Asn1.IDerUtcTime");
        configurator.mapType("com.itextpdf.commons.bouncycastle.openssl.IPEMParser", "iText.Commons.Bouncycastle.Openssl.IPemReader");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1BitString", "iText.Commons.Bouncycastle.Asn1.IDerBitString");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1Enumerated", "iText.Commons.Bouncycastle.Asn1.IDerEnumerated");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1GeneralizedTime", "iText.Commons.Bouncycastle.Asn1.IDerGeneralizedTime");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier", "iText.Commons.Bouncycastle.Asn1.IDerObjectIdentifier");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp", "iText.Commons.Bouncycastle.Cert.Ocsp.ISingleResponse");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.ocsp.IBasicOCSPResponse", "iText.Commons.Bouncycastle.Asn1.Ocsp.IBasicOcspResponse");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPObjectIdentifiers", "iText.Commons.Bouncycastle.Asn1.Ocsp.IOcspObjectIdentifiers");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponse", "iText.Commons.Bouncycastle.Asn1.Ocsp.IOcspResponse");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponseStatus", "iText.Commons.Bouncycastle.Asn1.Ocsp.IOcspResponseStatus");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.pkcs.IPKCSObjectIdentifiers", "iText.Commons.Bouncycastle.Asn1.Pkcs.IPkcsObjectIdentifiers");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable", "iText.Commons.Bouncycastle.Asn1.IAsn1Encodable");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableVector", "iText.Commons.Bouncycastle.Asn1.IAsn1EncodableVector");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence", "iText.Commons.Bouncycastle.Asn1.IAsn1Sequence");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1Set", "iText.Commons.Bouncycastle.Asn1.IAsn1Set");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString", "iText.Commons.Bouncycastle.Asn1.IAsn1OctetString");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1TaggedObject", "iText.Commons.Bouncycastle.Asn1.IAsn1TaggedObject");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.cmp.IPKIFailureInfo", "iText.Commons.Bouncycastle.Asn1.Cmp.IPkiFailureInfo");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IDERIA5String", "iText.Commons.Bouncycastle.Asn1.IDerIA5String");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IDERNull", "iText.Commons.Bouncycastle.Asn1.IDerNull");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IDEROctetString", "iText.Commons.Bouncycastle.Asn1.IDerOctetString");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IDERSequence", "iText.Commons.Bouncycastle.Asn1.IDerSequence");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IDERSet", "iText.Commons.Bouncycastle.Asn1.IDerSet");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IDERTaggedObject", "iText.Commons.Bouncycastle.Asn1.IDerTaggedObject");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertID", "iText.Commons.Bouncycastle.Asn1.Ess.IEssCertID");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertIDv2", "iText.Commons.Bouncycastle.Asn1.Ess.IEssCertIDv2");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.tsp.ITSTInfo", "iText.Commons.Bouncycastle.Asn1.Tsp.ITstInfo");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.util.IASN1Dump", "iText.Commons.Bouncycastle.Asn1.Util.IAsn1Dump");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1Integer", "iText.Commons.Bouncycastle.Asn1.IDerInteger");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.x509.IExtensions", "iText.Commons.Bouncycastle.Asn1.X509.IX509Extensions");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.x509.ICRLDistPoint", "iText.Commons.Bouncycastle.Asn1.X509.ICrlDistPoint");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.x509.ICRLReason", "iText.Commons.Bouncycastle.Asn1.X509.ICrlReason");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cms.ICMSEnvelopedData", "iText.Commons.Bouncycastle.Cms.ICmsEnvelopedData");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.x509.IKeyPurposeId", "iText.Commons.Bouncycastle.Asn1.X509.IKeyPurposeID");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cms.IRecipientId", "iText.Commons.Bouncycastle.Cms.IRecipientID");


        configurator.mapMethod("com.itextpdf.commons.utils.ZipFileReader.close", "Dispose");
        configurator.mapMethod("com.itextpdf.commons.utils.ZipFileWriter.close", "Dispose");
        configurator.mapMethod("com.itextpdf.kernel.pdf.annot.PdfSoundAnnotation.correctWavFile", "iText.Commons.Utils.JavaUtil.CorrectWavFile", false);
        configurator.addCustomUsingDeclaration("com.itextpdf.layout.renderer.TypographyUtils", Arrays.asList("System.IO", "System.Reflection", "Versions.Attributes", "Microsoft.Extensions.Logging", "iText.Commons"));
        configurator.mapType("org.junit.jupiter.api.condition.DisabledInNativeImage", "iText.Commons.Utils.NoopAnnotation");
        configurator.addFullName("iText.Commons.Utils.NoopAnnotation");

        configurator.mapType("java.security.cert.Certificate", "iText.Commons.Bouncycastle.Cert.IX509Certificate");
        configurator.mapType("java.security.cert.X509Certificate", "iText.Commons.Bouncycastle.Cert.IX509Certificate");
        configurator.mapType("java.security.InvalidAlgorithmParameterException", "System.Exception");
        configurator.mapType("java.security.cert.CRL", "iText.Commons.Bouncycastle.Cert.IX509Crl");
        configurator.mapType("java.security.cert.CRLException", "iText.Commons.Bouncycastle.Security.AbstractCrlException");
        configurator.mapType("java.security.cert.X509CRL", "iText.Commons.Bouncycastle.Cert.IX509Crl");
        configurator.mapType("java.security.cert.CRLReason", "iText.Commons.Bouncycastle.Cert.CRLReason");
        configurator.mapType("java.security.PrivateKey", "iText.Commons.Bouncycastle.Crypto.IPrivateKey");
        configurator.mapType("java.security.Signature", "iText.Commons.Bouncycastle.Crypto.ISigner");
        configurator.mapType("java.security.PublicKey", "iText.Commons.Bouncycastle.Crypto.IPublicKey");
        configurator.mapType("javax.security.auth.x500.X500Principal", "iText.Commons.Bouncycastle.Asn1.X500.IX500Name");
        configurator.mapType("java.security.Key", "iText.Commons.Bouncycastle.Crypto.IPrivateKey");
        configurator.mapType("java.security.KeyStore", "System.Collections.Generic.List<IX509Certificate>");
        configurator.mapType("java.security.GeneralSecurityException",
                "iText.Commons.Bouncycastle.Security.AbstractGeneralSecurityException");
        configurator.mapType("java.security.SignatureException",
                "iText.Commons.Bouncycastle.Security.AbstractGeneralSecurityException");
        configurator.mapType("java.security.cert.CertificateExpiredException",
                "iText.Commons.Bouncycastle.Security.AbstractCertificateExpiredException");
        configurator.mapType("java.security.cert.CertificateNotYetValidException",
                "iText.Commons.Bouncycastle.Security.AbstractCertificateNotYetValidException");
        configurator.mapType("java.security.cert.CertificateParsingException",
                "iText.Commons.Bouncycastle.Security.AbstractCertificateParsingException");
        configurator.mapType("java.security.KeyPairGenerator", "iText.Commons.Bouncycastle.Crypto.Generators.IRsaKeyPairGenerator");
        configurator.mapType("java.security.KeyPair", "iText.Commons.Bouncycastle.Crypto.IAsymmetricCipherKeyPair");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp",
                "iText.Commons.Bouncycastle.Asn1.Ocsp.IBasicOcspResponse");
        configurator.mapType("com.itextpdf.bouncycastle.cert.ocsp.BasicOCSPRespBC",
                "iText.Bouncycastle.Asn1.Ocsp.BasicOCSPResponseBC");
        configurator.mapType("com.itextpdf.bouncycastlefips.cert.ocsp.BasicOCSPRespBCFips",
                "iText.Bouncycastlefips.Asn1.Ocsp.BasicOCSPResponseBCFips");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPResp",
                "iText.Commons.Bouncycastle.Asn1.Ocsp.IOcspResponse");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.pkcs.IRSASSAPSSParams",
                "iText.Commons.Bouncycastle.Asn1.Pkcs.IRsassaPssParameters");


        configurator.mapMethod("java.math.BigInteger.intValue", "GetIntValue");
        configurator.mapMethod("java.security.cert.X509Certificate.getIssuerX500Principal", "GetIssuerDN");
        configurator.mapMethod("java.security.cert.X509Certificate.getSubjectX500Principal", "GetSubjectDN");
        configurator.mapMethod("java.security.cert.X509CRL.getIssuerX500Principal", "GetIssuerDN");
        configurator.mapMethod("java.security.cert.X509Extension.getCriticalExtensionOIDs", "GetCriticalExtensionOids");
        configurator.mapMethod("java.security.MessageDigest.getAlgorithm", "GetAlgorithmName");

        configurator.mapMethod("java.security.KeyPair.getPrivate", "GetPrivateKey");
        configurator.mapMethod("java.security.KeyPair.getPublic", "GetPublicKey");

        configurator.mapMethod("com.itextpdf.commons.bouncycastle.IBouncyCastleFactory.createOCSPRespBuilder", "");
        configurator.mapMethod("com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPRespBuilder.build", "CreateOCSPResponse");
        configurator.mapMethod("com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp.getCerts", "GetOcspCerts");
        configurator.mapMethod("com.itextpdf.commons.bouncycastle.IBouncyCastleFactory.createExtension()", "CreateExtensions");
        configurator.mapMethod("com.itextpdf.commons.bouncycastle.IBouncyCastleFactory.createX509CertificateHolder", "CreateX509Certificate");

        configurator.unwrapInvocation("com.itextpdf.commons.bouncycastle.IBouncyCastleFactory.createBasicOCSPResp"
                + "(com.itextpdf.commons.bouncycastle.asn1.ocsp.IBasicOCSPResponse)");
        configurator.unwrapInvocation("com.itextpdf.commons.bouncycastle.IBouncyCastleFactory.createOCSPResp"
                + "(com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponse)");

        configurator.mapMemberToInvocationsChain(
                "java.security.GeneralSecurityException.GeneralSecurityException(java.lang.String,java.lang.Throwable)",
                "iText.Bouncycastleconnector.BouncyCastleFactoryCreator.GetFactory().CreateGeneralSecurityException",
                MemberKind.Method);
        configurator.mapMemberToInvocationsChain(
                "java.security.GeneralSecurityException.GeneralSecurityException(java.lang.String)",
                "iText.Bouncycastleconnector.BouncyCastleFactoryCreator.GetFactory().CreateGeneralSecurityException",
                MemberKind.Method);
        configurator.mapMemberToInvocationsChain(
                "java.security.GeneralSecurityException.GeneralSecurityException()",
                "iText.Bouncycastleconnector.BouncyCastleFactoryCreator.GetFactory().CreateGeneralSecurityException",
                MemberKind.Method);
        configurator.mapMemberToInvocationsChain("java.math.BigInteger.valueOf(long)",
                "iText.Bouncycastleconnector.BouncyCastleFactoryCreator.GetFactory().CreateBigInteger().ValueOf",
                MemberKind.Method);
        configurator.mapMemberToInvocationsChain("java.math.BigInteger.BigInteger(int,byte[])",
                "iText.Bouncycastleconnector.BouncyCastleFactoryCreator.GetFactory().CreateBigInteger",
                MemberKind.Method);

        configurator.removeMethod("java.security.Security.addProvider");
        configurator.mapStringLiteral("com.itextpdf.bouncycastleconnector.logs.BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT", "Either itext7.bouncy-castle-adapter or itext7.bouncy-castle-fips-adapter dependency must be added in order to use BouncyCastleFactoryCreator");
    }

    @Override
    public void applySharpenOptions(OptionsConfigurator configurator) {

    }

    @Override
    public void applyConfigModuleSettings(ModulesConfigurator configurator) {

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
        return null;
    }

    @Override
    public Collection<String> getIgnoredResources() {
        return null;
    }

    @Override
    public List<SimpleImmutableEntry<String, String>> getOverwrittenResources() {
        return null;
    }
}
