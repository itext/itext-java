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
package com.itextpdf.barcodes;

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
        return 15;
    }

    @Override
    public String getModuleName() {
        return "barcodes";
    }

    @Override
    public void applyMappingConfiguration(MappingConfigurator configurator) {
        configurator.removeMethod("com.itextpdf.barcodes.BarcodePDF417Test.barcode417CreateAWTImageTest");
        configurator.removeMethod("com.itextpdf.barcodes.Barcode128.createAwtImage");
        configurator.removeMethod("com.itextpdf.barcodes.Barcode1D.createAwtImage");
        configurator.removeMethod("com.itextpdf.barcodes.BarcodeMSI.createAwtImage");
        configurator.removeMethod("com.itextpdf.barcodes.Barcode39.createAwtImage");
        configurator.removeMethod("com.itextpdf.barcodes.BarcodeCodabar.createAwtImage");
        configurator.removeMethod("com.itextpdf.barcodes.BarcodeDataMatrix.createAwtImage");
        configurator.removeMethod("com.itextpdf.barcodes.BarcodeEAN.createAwtImage");
        configurator.removeMethod("com.itextpdf.barcodes.BarcodeEANSUPP.createAwtImage");
        configurator.removeMethod("com.itextpdf.barcodes.BarcodeInter25.createAwtImage");
        configurator.removeMethod("com.itextpdf.barcodes.BarcodePDF417.createAwtImage");
        configurator.removeMethod("com.itextpdf.barcodes.BarcodePostnet.createAwtImage");
        configurator.removeMethod("com.itextpdf.barcodes.BarcodeQRCode.createAwtImage");
        configurator.removeField("com.itextpdf.barcodes.Barcode1D.DEFAULT_BAR_FOREGROUND_COLOR");
        configurator.removeField("com.itextpdf.barcodes.Barcode1D.DEFAULT_BAR_BACKGROUND_COLOR");
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
