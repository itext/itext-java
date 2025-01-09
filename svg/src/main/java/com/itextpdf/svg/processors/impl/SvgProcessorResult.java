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
package com.itextpdf.svg.processors.impl;

import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.processors.ISvgProcessor;
import com.itextpdf.svg.processors.ISvgProcessorResult;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;

import java.util.Map;

/**
 * A wrapper class that encapsulates processing results of {@link ISvgProcessor} objects.
 */
public class SvgProcessorResult implements ISvgProcessorResult {

    private final Map<String, ISvgNodeRenderer> namedObjects;
    private final ISvgNodeRenderer root;
    private final SvgProcessorContext context;

    /**
     * Creates new {@link SvgProcessorResult} entity.
     * @param namedObjects a map of named-objects with their id's as {@link String} keys and
     *                     the {@link ISvgNodeRenderer} objects as values.
     * @param root a wrapped {@link ISvgNodeRenderer} root renderer.
     * @param context a {@link SvgProcessorContext} instance.
     */
    public SvgProcessorResult(Map<String, ISvgNodeRenderer> namedObjects, ISvgNodeRenderer root, SvgProcessorContext context) {
        this.namedObjects = namedObjects;
        this.root = root;
        if (context == null) {
            throw new IllegalArgumentException(SvgExceptionMessageConstant.PARAMETER_CANNOT_BE_NULL);
        }
        this.context = context;
    }

    @Override
    public Map<String, ISvgNodeRenderer> getNamedObjects() {
        return namedObjects;
    }

    @Override
    public ISvgNodeRenderer getRootRenderer() {
        return root;
    }

    @Override
    public FontProvider getFontProvider() {
        return context.getFontProvider();
    }

    @Override
    public FontSet getTempFonts() {
        return context.getTempFonts();
    }

    /**
     * Gets processor context, containing {@link FontProvider} and {@link FontSet} of temporary fonts inside.
     * @return {@link SvgProcessorContext} instance
     */
    public SvgProcessorContext getContext() {
        return context;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || (!o.getClass().equals(this.getClass()))) {
            return false;
        }
        SvgProcessorResult otherResult = (SvgProcessorResult) o;
        return otherResult.getNamedObjects().equals(this.getNamedObjects()) && otherResult.getRootRenderer().equals(this.getRootRenderer());
    }

    @Override
    public int hashCode() {
        return getNamedObjects().hashCode() + 43 * getRootRenderer().hashCode();
    }
}
