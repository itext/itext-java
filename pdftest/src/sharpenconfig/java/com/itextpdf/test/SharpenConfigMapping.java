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
package com.itextpdf.test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import sharpen.config.DefaultPathMappings;
import sharpen.config.MappingConfiguration;
import sharpen.config.MappingConfigurator;
import sharpen.config.ModuleOption;
import sharpen.config.ModulesConfigurator;
import sharpen.config.NameMapping;
import sharpen.config.OptionsConfigurator;
import sharpen.config.PascalCaseNamingStrategy;
import sharpen.core.csharp.ast.CSExpression;
import sharpen.core.csharp.ast.CSInfixExpression;
import sharpen.core.csharp.ast.CSMemberReferenceExpression;
import sharpen.core.csharp.ast.CSMethodInvocationExpression;
import sharpen.core.csharp.ast.CSStringLiteralExpression;
import sharpen.core.csharp.ast.CSTypeReference;

public class SharpenConfigMapping implements MappingConfiguration {
    @Override
    public int getMappingPriority() {
        return 100 - 2;
    }

    @Override
    public String getModuleName() {
        return "pdftest";
    }

    @Override
    public void applyMappingConfiguration(MappingConfigurator configurator) {
        configurator.mapNamespace("com.itextpdf", "iText");

        configurator.addIgnoredAnnotations(Arrays.asList("org.junit.jupiter.params.ParameterizedTest"));
        configurator.addIfPreprocessorDirectiveCondition("NUnit.Framework.Timeout", "!NETSTANDARD2_0");

        configurator.removeMethod("com.itextpdf.test.ITextTest.removeCryptographyRestrictions");
        configurator.removeMethod("com.itextpdf.test.ITextTest.restoreCryptographyRestrictions");

        configurator.mapType("com.itextpdf.test.LoggerHelper", "iText.Test.LogListenerHelper");

        // getCurrentTimeDate is mapped to the GetCurrentUtcTime because the returned value is ultimately used for the
        // date comparison, however dates comparison must be done using UTC time in c#
        configurator.mapMethodParametersOrder(
                "com.itextpdf.test.signutils.Pkcs12FileHelper.initStore(java.lang.String,char[],java.security"
                        + ".Provider)",
                "1,2");

        configurator.mapAnnotationParameter("com.itextpdf.test.annotations.LogMessage", "messageTemplate", "");
        configurator.mapType("com.itextpdf.test.annotations.LogMessage", "iText.Test.Attributes.LogMessage");
        mapJunit(configurator);
    }

    @Override
    public void setConfigModuleSettings(ModulesConfigurator modulesConfigurator) {

    }

    @Override
    public void applySharpenOptions(OptionsConfigurator configurator) {
        PascalCaseNamingStrategy pascalCaseNamingStrategy = new PascalCaseNamingStrategy();
        pascalCaseNamingStrategy.addExceptionToPascalCaseNamespaces("iText");
        configurator.setNamingStrategy(pascalCaseNamingStrategy);
        configurator.setSharpenNamespace("nonamespace");
        configurator.setAutoImplementIEnumeratorEnabled(true);
        configurator.enableNativeTypeSystem();

        configurator.setPathMappings(new DefaultPathMappings() {
            @Override
            public Path convertPathToCSharp(Path ogPath) {
                final String ogPaths = ogPath.toString().replace(File.separator, "/");
                return Paths.get(ogPaths.replace("com/itextpdf/", "itext/"));
            }
        });
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

    private void mapJunit(MappingConfigurator configurator) {
        configurator.mapMethod("org.junit.jupiter.api.Assertions.assertEquals", "NUnit.Framework.Assert.AreEqual");
        configurator.mapMethod("org.junit.jupiter.api.Assertions.assertEquals(long,long,java.util.function.Supplier)",
                "iText.Test.AssertUtil.AreEqual");
        configurator.mapMethod("org.junit.jupiter.api.Assertions.assertEquals(long,long,java.util.function.Supplier)",
                "iText.Test.AssertUtil.AreEqual");
        configurator.mapMethod("org.junit.jupiter.api.Assertions.assertEquals(long,long,java.util.function.Supplier)",
                "iText.Test.AssertUtil.AreEqual");
        configurator.mapMethod("org.junit.jupiter.api.Assertions.assertEquals(long,long,java.util.function.Supplier)",
                "iText.Test.AssertUtil.AreEqual");
        configurator.mapMethod("org.junit.jupiter.api.Assertions.assertFalse", "NUnit.Framework.Assert.IsFalse");
        configurator.mapMethod("org.junit.jupiter.api.Assertions.assertNotNull", "NUnit.Framework.Assert.IsNotNull");
        configurator.mapMethod("org.junit.jupiter.api.Assertions.assertNull", "NUnit.Framework.Assert.IsNull");
        configurator.mapMethod("org.junit.jupiter.api.Assertions.assertSame", "NUnit.Framework.Assert.AreSame");
        configurator.mapMethod("org.junit.jupiter.api.Assertions.assertNotSame", "NUnit.Framework.Assert.AreNotSame");
        configurator.mapMethod("org.junit.jupiter.api.Assertions.fail", "NUnit.Framework.Assert.Fail");
        configurator.mapMethod("org.junit.jupiter.api.Assumptions.assumeTrue", "NUnit.Framework.Assume.That");
        configurator.mapMethod("org.junit.jupiter.api.Assertions.assertDoesNotThrow", "NUnit.Framework.Assert.DoesNotThrow");
        configurator.mapType("org.junit.jupiter.api.Assumptions", "NUnit.Framework.Assume");
        configurator.mapType("org.junit.jupiter.api.Assertions", "NUnit.Framework.Assert");
        configurator.mapType("org.junit.jupiter.api.BeforeEach", "NUnit.Framework.SetUp");
        configurator.mapType("org.junit.jupiter.api.AfterEach", "NUnit.Framework.TearDown");
        configurator.mapType("org.junit.jupiter.api.BeforeAll", "NUnit.Framework.OneTimeSetUp");
        configurator.mapType("org.junit.jupiter.api.AfterAll", "NUnit.Framework.OneTimeTearDown");
        configurator.mapType("org.junit.jupiter.api.Disabled", "NUnit.Framework.Ignore");
        configurator.mapType("org.junit.jupiter.params.provider.MethodSource", "NUnit.Framework.TestCaseSource");
        // Changes the case of the first letter for TestCaseSource in parameterized tests because when autoporting
        // the case of methods is changed to upper case
        configurator.addCustomMappingForAnnotation("NUnit.Framework.TestCaseSource",
                (annotation) -> {
                    for (CSExpression argument : annotation.arguments()) {
                        if (argument instanceof CSStringLiteralExpression) {
                            CSStringLiteralExpression literalExpression = (CSStringLiteralExpression) argument;
                            literalExpression.setEscapedValue(
                                    literalExpression.escapedValue().substring(0, 2).toUpperCase()
                                            + literalExpression.escapedValue().substring(2));
                        }
                    }
                });
        configurator.mapType("java.lang.reflect.AccessibleObject", "System.Reflection.MemberInfo");
        configurator.mapType("java.lang.reflect.Constructor<>", "System.Reflection.ConstructorInfo");
        configurator.addFullName("NUnit.Framework.Assert");
        configurator.addFullName("NUnit.Framework.SetUp");
        configurator.addFullName("NUnit.Framework.TestFixtureSetUp");
        configurator.addFullName("NUnit.Framework.Test");
        configurator.addFullName("NUnit.Framework.Ignore");
        configurator.addFullName("NUnit.Framework.TearDown");
        configurator.addFullName("NUnit.Framework.OneTimeSetUp");
        configurator.addFullName("NUnit.Framework.OneTimeTearDown");
        configurator.addFullName("NUnit.Framework.TestCaseSource");
        configurator.addFullName("NUnit.Framework.Timeout");
        configurator.removeNamedParameterFromAnnotation("NUnit.Framework.Timeout", "Unit");
        configurator.removeNamedParameterFromAnnotation("NUnit.Framework.Timeout", "ThreadMode");

        configurator.addFullName("NUnit.Framework.Category");
        configurator.mapMethodParametersOrder("org.junit.jupiter.api.Assertions.assertTrue(java.lang.String,boolean)",
                "2, 1");
        configurator.mapType("java.lang.AssertionError", "NUnit.Framework.AssertionException");

        List<NameMapping> defaultNamespaceMappings = new ArrayList<>();
        defaultNamespaceMappings.add(new NameMapping("org.junit.jupiter.api", "NUnit.Framework"));
        configurator.mapNamespaces(defaultNamespaceMappings);

        configurator.mapMethod("org.junit.jupiter.api.Assertions.assertTrue", "NUnit.Framework.Assert.IsTrue", false);
        configurator.mapMethod("org.junit.jupiter.api.Assertions.assertThrows", "NUnit.Framework.Assert.Catch");
        configurator.mapMethod("org.junit.jupiter.api.Assertions.assertArrayEquals", "NUnit.Framework.Assert.AreEqual",
                false);
        configurator.mapMethod("org.junit.jupiter.api.Assertions.assertSame", "NUnit.Framework.Assert.AreSame", false);
        configurator.mapMethod("org.junit.jupiter.api.Assertions.assertNotEquals", "NUnit.Framework.Assert.AreNotEqual",
                false);
        configurator.mapType("org.junit.jupiter.api.Tag", "NUnit.Framework.Category");
        configurator.mapAnnotationParameter("org.junit.Test", "NUnit.Framework.ExpectedException", "expected", "");
        configurator.mapAnnotationParameter("org.junit.Test", "NUnit.Framework.Timeout", "timeout", "");
        configurator.mapType("org.junit.jupiter.api.Timeout", "NUnit.Framework.Timeout");
        configurator.mapAnnotationParameter("org.junit.jupiter.api.Timeout", "value", "");

        final String pathJavaTestResources = "./src/test/resources/com/itextpdf/";
        final String csharpTargetTestResources = "/resources/itext/";
        configurator.mapDynamicStringLiteralModification((value) -> value.startsWith(pathJavaTestResources),
                (escaped) -> {
                    final String escapedValue = escaped.getEscapedValue();
                    final String adjustedPath = escapedValue.replace(pathJavaTestResources, csharpTargetTestResources);
                    CSMethodInvocationExpression projectDirectory = new CSMethodInvocationExpression(
                            new CSMemberReferenceExpression(new CSTypeReference("iText.Test.TestUtil"),
                                    "GetParentProjectDirectory"), new CSMemberReferenceExpression(
                            new CSMemberReferenceExpression(new CSTypeReference("NUnit.Framework.TestContext"),
                                    "CurrentContext"), "TestDirectory"));
                    return new CSInfixExpression("+", projectDirectory, new CSStringLiteralExpression(adjustedPath));
                });
        configurator.mapDynamicStringLiteralModification((value) -> value.startsWith("./target/test/"),

                (escaped) -> {
                    return new CSInfixExpression("+", new CSMemberReferenceExpression(
                            new CSMemberReferenceExpression(new CSTypeReference("NUnit.Framework.TestContext"),
                                    "CurrentContext"), "TestDirectory"), new CSStringLiteralExpression(
                            escaped.getEscapedValue().replace("/com/itextpdf/", "/itext/")
                                    .replace("./target","")
                    ));
                });
    }
}
