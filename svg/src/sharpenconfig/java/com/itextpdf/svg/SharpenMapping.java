/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.svg;

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
        return 11;
    }

    @Override
    public String getModuleName() {
        return "svg";
    }

    @Override
    public void applyMappingConfiguration(MappingConfigurator configurator) {
        configurator.mapFunctionalInterfaceToDelegate("com.itextpdf.svg.renderers.factories.DefaultSvgNodeRendererMapper$ISvgNodeRendererCreator");
        configurator.mapStringLiteral("com.itextpdf.svg.css.impl.DefaultSvgStyleResolver.DEFAULT_CSS_PATH", "iText.Svg.default.css");
        configurator.mapStringLiteral("com.itextpdf.svg.css.impl.SvgStyleResolver.DEFAULT_CSS_PATH", "iText.Svg.default.css");

        configurator.keepInternalProtected(
                "com.itextpdf.svg.customization.CustomizeTextLeafSvgNodeRendererTest.CustomTextLeafSvgNodeRenderer"
                        + ".doDraw");
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
