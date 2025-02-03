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
package com.itextpdf.kernel;

import sharpen.config.MappingConfiguration;
import sharpen.config.MappingConfigurator;
import sharpen.config.ModuleOption;
import sharpen.config.ModulesConfigurator;
import sharpen.config.OptionsConfigurator;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SharpenMapping implements MappingConfiguration {
    @Override
    public int getMappingPriority() {
        return 18;
    }

    @Override
    public String getModuleName() {
        return "kernel";
    }

    @Override
    public void applyMappingConfiguration(MappingConfigurator configurator) {
        configurator.addFullName("iText.Kernel.Events.IEventHandler");
        configurator.mapMethod("com.itextpdf.kernel.pdf.PdfObject.getType", "GetObjectType");
        configurator.mapMethod("com.itextpdf.kernel.events.Event.getType", "GetEventType");
        configurator.mapMethod("com.itextpdf.kernel.geom.AffineTransform.getType", "GetTransformType");
        configurator.mapMethod("com.itextpdf.kernel.pdf.canvas.wmf.MetaObject.getType", "GetObjectType");
        configurator.mapMethod("com.itextpdf.kernel.pdf.function.Function.getType", "GetFunctionType");
        configurator.mapMethod("com.itextpdf.kernel.pdf.tagging.PdfStructElem.getType", "GetStructElementType");
        configurator.mapMethod("com.itextpdf.kernel.pdf.canvas.parser.clipper.Paths.getBounds", "iText.Kernel.Pdf.Canvas.Parser.ClipperLib.Clipper.GetBounds");
        configurator.keepInternalProtected("com.itextpdf.kernel.actions.events.AddFingerPrintEvent.doAction");
        configurator.keepInternalProtected("com.itextpdf.kernel.actions.events.FlushPdfDocumentEvent.doAction");
        configurator.keepInternalProtected("com.itextpdf.kernel.actions.events.LinkDocumentIdEvent.doAction");
        configurator.keepInternalProtected("com.itextpdf.kernel.crypto.securityhandler.CToolNoDeveloperExtension.compareObjects");
        configurator.removeMethod("com.itextpdf.kernel.colors.DeviceRgb.DeviceRgb(java.awt.Color)", true);
        configurator.mapStringLiteral("com.itextpdf.kernel.pdf.PdfXrefTable.writeKeyInfo.platform", " for .NET");
        configurator.mapStringLiteral("com.itextpdf.kernel.font.FontUtil.UNIVERSAL_CMAP_DIR", "ToUnicode.");
        configurator.mapType("com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper", "iText.Kernel.Pdf.Canvas.Parser.ClipperLib.Clipper");
        configurator.mapType("com.itextpdf.kernel.pdf.canvas.parser.clipper.Point.LongPoint", "iText.Kernel.Pdf.Canvas.Parser.ClipperLib.IntPoint");
        configurator.mapType("com.itextpdf.kernel.pdf.canvas.parser.clipper.LongRect", "iText.Kernel.Pdf.Canvas.Parser.ClipperLib.IntRect");
        configurator.mapType("com.itextpdf.kernel.pdf.canvas.parser.clipper.PolyTree", "iText.Kernel.Pdf.Canvas.Parser.ClipperLib.PolyTree");
        configurator.mapType("com.itextpdf.kernel.pdf.canvas.parser.clipper.ClipperBridge", "iText.Kernel.Pdf.Canvas.Parser.ClipperLib.ClipperBridge");
        configurator.mapType("com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper.PolyType", "iText.Kernel.Pdf.Canvas.Parser.ClipperLib.PolyType");
        configurator.mapType("com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper.PolyFillType", "iText.Kernel.Pdf.Canvas.Parser.ClipperLib.PolyFillType");
        configurator.mapType("com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper.JoinType", "iText.Kernel.Pdf.Canvas.Parser.ClipperLib.JoinType");
        configurator.mapType("com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper.EndType", "iText.Kernel.Pdf.Canvas.Parser.ClipperLib.EndType");
        configurator.mapType("com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper.ClipType", "iText.Kernel.Pdf.Canvas.Parser.ClipperLib.ClipType");
        configurator.mapType("com.itextpdf.kernel.pdf.canvas.parser.clipper.DefaultClipper", "iText.Kernel.Pdf.Canvas.Parser.ClipperLib.Clipper");
        configurator.mapType("com.itextpdf.kernel.pdf.canvas.parser.clipper.Path", "System.Collections.Generic.List<IntPoint>");
        configurator.mapMethod("com.itextpdf.kernel.pdf.canvas.parser.clipper.Paths.makePolyTreeToPaths", "iText.Kernel.Pdf.Canvas.Parser.ClipperLib.Clipper.PolyTreeToPaths");
        configurator.mapProperty("com.itextpdf.kernel.pdf.canvas.parser.clipper.PolyNode.getContour", "Contour");
        configurator.mapProperty("com.itextpdf.kernel.pdf.canvas.parser.clipper.PolyNode.isOpen", "IsOpen");
        configurator.mapProperty("com.itextpdf.kernel.pdf.canvas.parser.clipper.Point.LongPoint.getX", "X");
        configurator.mapProperty("com.itextpdf.kernel.pdf.canvas.parser.clipper.Point.LongPoint.getY", "Y");
        configurator.addFullName("com.itextpdf.kernel.geom.Path");
        configurator.removeMethodOverload("com.itextpdf.kernel.pdf.PdfWriter.write(int)");
        configurator.removeMethodOverload("com.itextpdf.kernel.pdf.PdfWriter.write(byte[])");
        configurator.removeMethodOverload("com.itextpdf.kernel.pdf.PdfWriter.write(byte[],int,int)");
        configurator.removeMethodOverload("com.itextpdf.kernel.pdf.PdfWriter.close");
        configurator.removeMethod("com.itextpdf.kernel.pdf.xobject.PdfImageXObject.getBufferedImage");
        configurator.removeMethod("com.itextpdf.kernel.pdf.canvas.PdfCanvasTest.awtImagesTest01");
        configurator.removeMethod("com.itextpdf.kernel.pdf.PdfReaderTest.getMemoryUse");
        configurator.removeMethod("com.itextpdf.kernel.pdf.PdfReaderTest.garbageCollect");
        configurator.mapMethod("com.itextpdf.kernel.pdf.function.PdfFunction.getType", "GetFunctionType");
        configurator.mapVariableType("com.itextpdf.kernel.pdf.PdfPage.setPageLabel.numberingStyle", "PageLabelNumberingStyle?");
        configurator.ignoreVarForNullableGenericsConversion("com.itextpdf.kernel.pdf.PdfNumTree.items");
        configurator.ignoreVarForNullableGenericsConversion("com.itextpdf.kernel.pdf.tagging.ParentTreeHandler.registerAllMcrs.parentTreeEntries");
        configurator.ignoreVarForNullableGenericsConversion("com.itextpdf.kernel.pdf.tagging.ParentTreeHandler.registerAllMcrs.entry");
        configurator.ignoreVarForNullableGenericsConversion("com.itextpdf.kernel.pdf.tagging.ParentTreeHandler.registerAllMcrs.entry");
        configurator.ignoreVarForNullableGenericsConversion("com.itextpdf.kernel.pdf.PdfDocument.getPageLabels.pageLabels");
        configurator.mapMethodReturnType("com.itextpdf.kernel.pdf.tagging.ParentTreeHandler.structParentIndexIntoKey", "int");
        configurator.mapMethodReturnType("com.itextpdf.kernel.pdf.PdfNumTree.getNumbers", "IDictionary<int?, PdfObject>");
        configurator.keepInternalProtected("com.itextpdf.kernel.font.Type3Font.setFontName");
        configurator.keepInternalProtected("com.itextpdf.kernel.font.Type3Font.setFontFamily");
        configurator.keepInternalProtected("com.itextpdf.kernel.font.Type3Font.setFontWeight");
        configurator.keepInternalProtected("com.itextpdf.kernel.font.Type3Font.setFontStretch");
        configurator.keepInternalProtected("com.itextpdf.kernel.font.Type3Font.setItalicAngle");
        configurator.keepInternalProtected("com.itextpdf.kernel.font.Type3Font.setCapHeight");
        configurator.keepInternalProtected("com.itextpdf.kernel.font.Type3Font.setTypoAscender");
        configurator.keepInternalProtected("com.itextpdf.kernel.font.Type3Font.setTypoDescender");
        configurator.keepInternalProtected("com.itextpdf.kernel.font.PdfType3FontTest" + ".DisableEnsureUnderlyingObjectHasIndirectReference.ensureUnderlyingObjectHasIndirectReference");
        configurator.ignoreSuperInterface("java.lang.Cloneable", "com.itextpdf.kernel.geom.Rectangle");
        configurator.ignoreSuperInterface("java.lang.Cloneable", "com.itextpdf.kernel.geom.PageSize");
        configurator.ignoreSuperInterface("java.lang.Cloneable", "com.itextpdf.kernel.geom.AffineTransform");
        configurator.mapFunctionalInterfaceToDelegate("com.itextpdf.kernel.pdf.function.BaseInputOutPutConvertors$IInputConversionFunction");
        configurator.mapFunctionalInterfaceToDelegate("com.itextpdf.kernel.pdf.function.BaseInputOutPutConvertors$IOutputConversionFunction");
        configurator.mapFunctionalInterfaceToDelegate("com.itextpdf.kernel.pdf.function.IPdfFunctionFactory");
        configurator.mapMethod("com.itextpdf.kernel.pdf.PdfArray.iterator", "GetEnumerator");
        configurator.addCustomUsingDeclaration("com.itextpdf.kernel.pdf.PdfArray", Arrays.asList("System.Collections"));
        configurator.removeMethod("com.itextpdf.kernel.pdf.PdfEncryptor.getContent");
        configurator.removeField("com.itextpdf.kernel.pdf.ReaderProperties.externalDecryptionProcess");
        configurator.removeField("com.itextpdf.kernel.pdf.ReaderProperties.certificateKeyProvider");
        configurator.mapMethodParametersOrder("com.itextpdf.kernel.pdf.PdfEncryption.PdfEncryption(" + "com.itextpdf.kernel.pdf.PdfDictionary,java.security.Key,java.security.cert.Certificate," + "java.lang.String,com.itextpdf.kernel.security.IExternalDecryptionProcess)", "1, 2, 3");
        configurator.mapMethodParametersOrder("com.itextpdf.kernel.pdf.PdfEncryption.PdfEncryption(" + "com.itextpdf.kernel.pdf.PdfDictionary,java.security.Key,java.security.cert.Certificate," + "java.lang.String,com.itextpdf.kernel.security.IExternalDecryptionProcess,com.itextpdf.kernel.mac.MacIntegrityProtector)", "1, 2, 3, 6");
        configurator.mapMethodParametersOrder("com.itextpdf.kernel.pdf.PdfEncryption.PdfEncryption(" + "com.itextpdf.kernel.pdf.PdfDictionary,java.security.Key,java.security.cert.Certificate," + "java.lang.String,com.itextpdf.kernel.security.IExternalDecryptionProcess,com.itextpdf.kernel.mac.AbstractMacIntegrityProtector)", "1, 2, 3, 6");
        configurator.mapMethodParametersOrder("com.itextpdf.kernel.crypto.securityhandler.EncryptionUtils.fetchEnvelopedData", "1, 2, 5");
        configurator.mapMethodParametersOrder("com.itextpdf.kernel.crypto.securityhandler.PubKeySecurityHandler.computeGlobalKeyOnReading", "1, 2, 3, 6, 7");
        configurator.mapMethodParametersOrder("com.itextpdf.kernel.crypto.securityhandler.PubKeySecurityHandler.initKeyAndReadDictionary", "1, 2, 3, 6");
        configurator.mapMethodParametersOrder("com.itextpdf.kernel.crypto.securityhandler.PubSecHandlerUsingAes128.PubSecHandlerUsingAes128(" + "com.itextpdf.kernel.pdf.PdfDictionary,java.security.Key,java.security.cert.Certificate,java.lang.String," + "com.itextpdf.kernel.security.IExternalDecryptionProcess,boolean)", "1, 2, 3, 6");
        configurator.mapMethodParametersOrder("com.itextpdf.kernel.crypto.securityhandler.PubSecHandlerUsingStandard40.PubSecHandlerUsingStandard40(" + "com.itextpdf.kernel.pdf.PdfDictionary,java.security.Key,java.security.cert.Certificate,java.lang.String," + "com.itextpdf.kernel.security.IExternalDecryptionProcess,boolean)", "1, 2, 3, 6");
        configurator.mapMethodParametersOrder("com.itextpdf.kernel.pdf.ReaderProperties.setPublicKeySecurityParams(" + "java.security.cert.Certificate,java.security.Key,java.lang.String," + "com.itextpdf.kernel.security.IExternalDecryptionProcess)", "1, 2");
        configurator.mapMethodParametersOrder("com.itextpdf.kernel.pdf.ReaderProperties.setPublicKeySecurityParams(" + "java.security.cert.Certificate,com.itextpdf.kernel.security.IExternalDecryptionProcess)", "1");
        configurator.mapMethod("com.itextpdf.kernel.crypto.CryptoUtil.readPublicCertificate", "iText.Kernel.Crypto.CryptoUtil.ReadPublicCertificate", false);
        configurator.mapMethodParametersOrder("com.itextpdf.kernel.crypto.DigestAlgorithms.getMessageDigestFromOid", "1");
        configurator.mapMethodParametersOrder("com.itextpdf.kernel.crypto.DigestAlgorithms.getMessageDigest", "1");
        configurator.mapMethodParametersOrder("com.itextpdf.kernel.crypto.DigestAlgorithms.digest" + "(java.io.InputStream,java.lang.String,java.lang.String)", "1, 2");
        configurator.mapMethodParametersOrder("com.itextpdf.signatures.PdfPKCS7.PdfPKCS7(byte[],com.itextpdf.kernel.pdf.PdfName,java.lang.String)", "1, 2");
        configurator.removeMethod("com.itextpdf.kernel.crypto.DigestAlgorithms.normalizeDigestName");
        configurator.mapMethodParametersOrder("com.itextpdf.signatures.LtvVerifier.LtvVerifier(com.itextpdf.kernel.pdf.PdfDocument,java.lang.String)", "1");
        configurator.removeMethod("com.itextpdf.signatures.LtvVerifier.LtvVerifier(com.itextpdf.kernel.pdf.PdfDocument,java.lang.String)");
        configurator.mapMethodParametersOrder("com.itextpdf.signatures.LtvVerification.LtvVerification(com.itextpdf.kernel.pdf.PdfDocument,java.lang.String)", "1");
        configurator.removeMethod("com.itextpdf.signatures.LtvVerification.LtvVerification(com.itextpdf.kernel.pdf.PdfDocument,java.lang.String)");
        configurator.mapMethod("com.itextpdf.kernel.pdf.PdfWriter.close", "Dispose");
        configurator.removeMethod("com.itextpdf.kernel.pdf.annot.PdfSoundAnnotation.correctWavFile", true);
        configurator.removeMethod("com.itextpdf.kernel.xmp.properties.XMPPropertyInfo.getValue");
        configurator.removeMethod("com.itextpdf.kernel.xmp.properties.XMPPropertyInfo.getOptions");
        configurator.mapMethod("com.itextpdf.kernel.xmp.impl.Utils.normalizeLangValue", "iText.Kernel.XMP.Impl.Utils.NormalizeLangValue");
        configurator.mapMethodToCustomMember("com.itextpdf.kernel.pdf.canvas.PdfCanvas.iteratorToList", "EnumeratorToList", ITextSharpCustomMembers.enumeratorToList);
        configurator.mapMethodToCustomMember("com.itextpdf.kernel.pdf.EncryptionProperties.randomBytes", "RandomBytes", ITextSharpCustomMembers.encryptionPropertiesRandomBytes);
        configurator.addCustomUsingDeclaration("com.itextpdf.kernel.pdf.EncryptionProperties", Arrays.asList("System.Security.Cryptography"));
        configurator.mapVariableType("com.itextpdf.kernel.colors.DeviceRgbTest.colorByAWTColorTest.color", "System.Drawing.Color");
        configurator.mapMethodToCustomMember("com.itextpdf.kernel.geom.Rectangle.clone", "Clone", ITextSharpCustomMembers.cloneRectangle);
        configurator.mapMethodToCustomMember("com.itextpdf.kernel.geom.AffineTransform.clone", "Clone", ITextSharpCustomMembers.cloneAffineTransform);

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
