/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.io;

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
        return 100 - 6;
    }

    @Override
    public String getModuleName() {
        return "io";
    }

    @Override
    public void applyMappingConfiguration(MappingConfigurator configurator) {
        configurator.removeMethod("com.itextpdf.io.source.RandomAccessFileOrArray.readUTF");
        configurator.mapMethod("com.itextpdf.io.codec.TIFFField.getType", "GetFieldType");
        configurator.mapMethod("com.itextpdf.io.font.cmap.CMapObject.getType", "GetObjectType");
        configurator.mapMethod("com.itextpdf.io.image.AwtImageDataFactory.create", "iText.IO.Image.DrawingImageFactory.getImage", false);
        configurator.mapStringLiteral("com.itextpdf.io.font.PdfEncodings.CP1252", "Windows-1252");
        configurator.mapStringLiteral("com.itextpdf.io.font.PdfEncodings.CP1250", "Windows-1250");
        configurator.mapStringLiteral("com.itextpdf.io.font.PdfEncodings.CP1253", "Windows-1253");
        configurator.mapStringLiteral("com.itextpdf.io.font.PdfEncodings.CP1257", "Windows-1257");
        configurator.mapStringLiteral("com.itextpdf.io.font.PdfEncodings.WINANSI", "Windows-1252");
        configurator.mapStringLiteral("com.itextpdf.io.font.FontConstants.RESOURCE_PATH", "iText.IO.Font.");
        configurator.mapStringLiteral("com.itextpdf.io.font.FontConstants.AFM_RESOURCE_PATH", "iText.IO.Font.Afm.");
        configurator.mapStringLiteral("com.itextpdf.io.font.FontConstants.CMAP_RESOURCE_PATH", "iText.IO.Font.Cmap.");
        configurator.mapStringLiteral("com.itextpdf.io.font.constants.FontResources.ADOBE_GLYPH_LIST", "iText.IO.Font.AdobeGlyphList.txt");
        configurator.mapStringLiteral("com.itextpdf.io.font.constants.FontResources.AFMS", "iText.IO.Font.Afm.");
        configurator.mapStringLiteral("com.itextpdf.io.font.constants.FontResources.CMAPS", "iText.IO.Font.Cmap.");
        configurator.addFullName("com.itextpdf.io.image.Image");
        configurator.removeField("com.itextpdf.io.logs.IoLogMessageConstant.FILE_CHANNEL_CLOSING_FAILED");
        configurator.mapMethod("com.itextpdf.io.source.HighPrecisionOutputStream.close", "Dispose");
        configurator.mapMethod("com.itextpdf.io.source.DeflaterOutputStream.close", "Dispose");
        configurator.addCustomUsingDeclaration("com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest", Collections.singletonList("System.IO"));
        configurator.mapMethod("com.itextpdf.io.codec.brotli.dec.BrotliInputStream.read()", "ReadByte");


        configurator.addFullName("iText.IO.Logs.IoLogMessageConstant");
        configurator.addFullName("iText.IO.Util.TextUtil");
        configurator.mapMethod("java.lang.Math.toRadians", "iText.IO.Util.MathUtil.ToRadians", false);
        configurator.addFullName("iText.IO.IOException");
        configurator.addFullName("iText.IO.Exceptions.IOException");
        configurator.mapField("java.lang.Character.MIN_SUPPLEMENTARY_CODE_POINT", "iText.IO.Util.TextUtil.CHARACTER_MIN_SUPPLEMENTARY_CODE_POINT");
        configurator.mapMethod("java.lang.Character.toChars", "iText.IO.Util.TextUtil.ToChars");
        configurator.mapMethod("java.lang.Character.charCount", "iText.IO.Util.TextUtil.CharCount");
        configurator.mapMethod("java.lang.Character.isIdentifierIgnorable", "iText.IO.Util.TextUtil.IsIdentifierIgnorable");
        configurator.mapMethod("java.nio.charset.Charset.newEncoder", "iText.IO.Util.TextUtil.NewEncoder");
        configurator.mapMethod("java.io.DataInputStreamReadFully", "iText.IO.Util.StreamUtil.ReadFully", false);
        configurator.mapMethod("java.net.URL.openStream", "iText.IO.Util.UrlUtil.OpenStream", false);
        configurator.mapMethod("java.lang.Character.isWhitespace", "iText.IO.Util.TextUtil.IsWhiteSpace", false);
        configurator.mapType("java.io.FilterOutputStream", "iText.Commons.Utils.FilterOutputStream");

        configurator.addIfPreprocessorDirectiveCondition("com.itextpdf.io.image.ImageDataFactory.create(java.awt.Image,java.awt.Color)", "!NETSTANDARD2_0");
        configurator.addIfPreprocessorDirectiveCondition("com.itextpdf.io.image.ImageDataFactory.create(java.awt.Image,java.awt.Color,boolean)", "!NETSTANDARD2_0");
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

    @Override
    public void setConfigModuleSettings(ModulesConfigurator modulesConfigurator) {

    }
}
