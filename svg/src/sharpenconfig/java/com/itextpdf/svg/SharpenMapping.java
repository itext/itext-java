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
