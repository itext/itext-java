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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import sharpen.config.MappingConfiguration;
import sharpen.config.MappingConfigurator;
import sharpen.config.ModuleOption;
import sharpen.config.ModulesConfigurator;
import sharpen.config.OptionsConfigurator;

public class SharpenMapping implements MappingConfiguration {
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
