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
package com.itextpdf.layout;

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
        return 100 - 11;
    }

    @Override
    public String getModuleName() {
        return "layout";
    }

    @Override
    public void applyMappingConfiguration(MappingConfigurator configurator) {

        configurator.mapFunctionalInterfaceToDelegate("com.itextpdf.layout.AlignmentTest$IInlineTestObjectModifier");
        configurator.mapMethod("com.itextpdf.layout.borders.Border.getType", "GetBorderType");
        configurator.mapMethod("com.itextpdf.layout.element.AreaBreak.getType", "GetAreaType");
        configurator.mapMethod("com.itextpdf.layout.properties.Leading.getType", "GetLeadingType");
        configurator.addFullName("com.itextpdf.layout.element.Image");
        configurator.mapType("com.itextpdf.layout.properties.VerticalAlignment", "iText.Layout.Properties.VerticalAlignment?");
        configurator.mapType("com.itextpdf.layout.properties.InlineVerticalAlignmentType", "iText.Layout.Properties.InlineVerticalAlignmentType?");
        configurator.mapType("com.itextpdf.layout.properties.HorizontalAlignment", "iText.Layout.Properties.HorizontalAlignment?");
        configurator.mapType("com.itextpdf.layout.properties.TextAlignment", "iText.Layout.Properties.TextAlignment?");
        configurator.mapType("com.itextpdf.layout.properties.AreaBreakType", "iText.Layout.Properties.AreaBreakType?");
        configurator.mapType("com.itextpdf.layout.properties.BaseDirection", "iText.Layout.Properties.BaseDirection?");
        configurator.mapType("com.itextpdf.layout.properties.FloatPropertyValue", "iText.Layout.Properties.FloatPropertyValue?");
        configurator.mapType("com.itextpdf.layout.properties.ClearPropertyValue", "iText.Layout.Properties.ClearPropertyValue?");
        configurator.mapType("com.itextpdf.layout.properties.BoxSizingPropertyValue", "iText.Layout.Properties.BoxSizingPropertyValue?");
        configurator.mapType("com.itextpdf.layout.properties.OverflowPropertyValue", "iText.Layout.Properties.OverflowPropertyValue?");
        configurator.mapType("com.itextpdf.layout.properties.OverflowWrapPropertyValue", "iText.Layout.Properties.OverflowWrapPropertyValue?");
        configurator.mapType("com.itextpdf.layout.properties.RenderingMode", "iText.Layout.Properties.RenderingMode?");
        configurator.mapVariableType("com.itextpdf.layout.renderer.LineRenderer.getNextTabStop.nextTabStopEntry", "KeyValuePair<float, TabStop>?");
        configurator.mapVariableType("com.itextpdf.layout.renderer.TextRenderer.applyOtf.glyphScript", "UnicodeScript");
        configurator.mapStringLiteral("com.itextpdf.layout.renderer.TypographyUtils.TYPOGRAPHY_PACKAGE", "iText.Typography.");
        configurator.mapStringLiteral("com.itextpdf.layout.renderer.TypographyUtils.TYPOGRAPHY_APPLIER", "Shaping.TypographyApplier,iText.Typography");
        configurator.mapStringLiteral("com.itextpdf.layout.renderer.TypographyUtils.TYPOGRAPHY_APPLIER_INITIALIZE", "RegisterForLayout");
        configurator.mapStringLiteral("com.itextpdf.layout.hyphenation.HyphenationConstants.HYPHENATION_DEFAULT_RESOURCE", "iText.Hyph.");
        configurator.ignoreSuperInterface("java.lang.Cloneable", "com.itextpdf.layout.layout.LayoutArea");
        configurator.ignoreSuperInterface("java.lang.Cloneable", "com.itextpdf.layout.layout.RootLayoutArea");
        configurator.ignoreSuperInterface("java.lang.Cloneable", "com.itextpdf.layout.margincollapse.MarginsCollapse");
        configurator.mapMethodToCustomMember("com.itextpdf.layout.renderer.TypographyUtils.getTypographyClass", "GetTypographyClass", SharpenConfigCustomMembers.getTypographyClass);
        configurator.mapMethodToCustomMember("com.itextpdf.layout.layout.LayoutArea.clone", "Clone", SharpenConfigCustomMembers.cloneLayoutArea);
        configurator.mapMethodToCustomMember("com.itextpdf.layout.margincollapse.MarginsCollapse.clone", "Clone", SharpenConfigCustomMembers.cloneMarginsCollapse);
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
