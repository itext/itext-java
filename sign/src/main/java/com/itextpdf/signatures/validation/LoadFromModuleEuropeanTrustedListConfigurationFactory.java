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
package com.itextpdf.signatures.validation;

import com.itextpdf.commons.utils.RuntimeUtil;
import com.itextpdf.eutrustedlistsresources.EuropeanTrustedListConfiguration;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;

import java.security.cert.Certificate;
import java.util.List;

class LoadFromModuleEuropeanTrustedListConfigurationFactory extends EuropeanTrustedListConfigurationFactory {
    private static final String CLASS_FOR_RESOURCES = "com.itextpdf.eutrustedlistsresources" +
            ".EuropeanTrustedListConfiguration";


    EuropeanTrustedListConfiguration config;

    public LoadFromModuleEuropeanTrustedListConfigurationFactory() {
        if (!RuntimeUtil.isClassLoaded(CLASS_FOR_RESOURCES)) {
            throw new PdfException(SignExceptionMessageConstant.EU_RESOURCES_NOT_LOADED);
        }
        config = new EuropeanTrustedListConfiguration();
    }

    public String getTrustedListUri() {
        return config.getTrustedListUri();
    }

    public String getCurrentlySupportedPublication() {
        return config.getCurrentlySupportedPublication();
    }

    public List<Certificate> getCertificates() {
        EuropeanTrustedCertificatesResourceLoader loader = new EuropeanTrustedCertificatesResourceLoader(config);
        return loader.loadCertificates();

    }
}
