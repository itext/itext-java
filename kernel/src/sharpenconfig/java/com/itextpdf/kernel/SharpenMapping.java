package com.itextpdf.kernel;

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
        return 18;
    }

    @Override
    public String getModuleName() {
        return "kernel";
    }

    @Override
    public void applyMappingConfiguration(MappingConfigurator configurator) {
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
