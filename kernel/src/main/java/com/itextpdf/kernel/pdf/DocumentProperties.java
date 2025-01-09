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
package com.itextpdf.kernel.pdf;

import com.itextpdf.commons.actions.contexts.IMetaInfo;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;

import java.util.HashMap;

/**
 * Class with additional properties for {@link PdfDocument} processing.
 * Needs to be passed at document initialization.
 */
public class DocumentProperties {


    protected IMetaInfo metaInfo = null;

    HashMap<Class<?>, Object> dependencies = new HashMap<>();

    /**
     * Default constructor, use provided setters for configuration options.
     */
    public DocumentProperties() {
    }

    /**
     * Creates a copy of class instance.
     *
     * @param other the base for new class instance
     */
    public DocumentProperties(DocumentProperties other) {
        this.metaInfo = other.metaInfo;
    }

    /**
     * Sets document meta info.
     *
     * @param metaInfo meta info to set
     * @return this {@link DocumentProperties} instance
     */
    public DocumentProperties setEventCountingMetaInfo(IMetaInfo metaInfo) {
        this.metaInfo = metaInfo;
        return this;
    }

    /**
     * Checks if the document event counting meta info was already set.
     *
     * @return true if the document event counting meta info is set, false otherwise.
     */
    public boolean isEventCountingMetaInfoSet() {
        return this.metaInfo != null;
    }

    /**
     * Register custom dependency for the document.
     *
     * @param clazz    Type of the dependency.
     * @param instance The instance of the dependency.
     * @return this {@link DocumentProperties} instance
     */
    public DocumentProperties registerDependency(Class<?> clazz, Object instance) {
        if (clazz == null) {
            throw new IllegalArgumentException(KernelExceptionMessageConstant.TYPE_SHOULD_NOT_BE_NULL);
        }
        if (instance == null) {
            throw new IllegalArgumentException(KernelExceptionMessageConstant.INSTANCE_SHOULD_NOT_BE_NULL);
        }
        dependencies.put(clazz, instance);
        return this;
    }
}
