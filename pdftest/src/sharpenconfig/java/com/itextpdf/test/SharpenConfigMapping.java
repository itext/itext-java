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
package com.itextpdf.test;

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
        return 18;
    }

    @Override
    public String getModuleName() {
        return "pdftest";
    }

    @Override
    public void applyMappingConfiguration(MappingConfigurator configurator) {
        configurator.mapNamespace("com.itextpdf", "iText");
        configurator.removeMethod("com.itextpdf.test.ITextTest.removeCryptographyRestrictions");
        configurator.removeMethod("com.itextpdf.test.ITextTest.restoreCryptographyRestrictions");

        configurator.mapType("com.itextpdf.test.LoggerHelper", "iText.Test.LogListenerHelper");
        configurator.mapMethod("com.itextpdf.test.AssertUtil.doesNotThrow", "NUnit.Framework.Assert.DoesNotThrow", false);

        // getCurrentTimeDate is mapped to the GetCurrentUtcTime because the returned value is ultimately used for the
        // date comparison, however dates comparison must be done using UTC time in c#
        configurator.mapMethodParametersOrder("com.itextpdf.test.signutils.Pkcs12FileHelper.initStore(java.lang.String,char[],java.security.Provider)", "1,2");

        configurator.mapAnnotationParameter("com.itextpdf.test.annotations.LogMessage", "messageTemplate", "");
        configurator.mapType("com.itextpdf.test.annotations.LogMessage", "iText.Test.Attributes.LogMessage");
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
