/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

    /**
     * @deprecated  Will be removed in 7.2.
     */
    @Deprecated
    private final FontProvider fontProvider;

    /**
     * @deprecated  Will be removed in 7.2.
     */
    @Deprecated
    private final FontSet tempFonts;
    private final SvgProcessorContext context;

    /**
     * Creates new {@link SvgProcessorResult} entity.
     * @param namedObjects a map of named-objects with their id's as {@link String} keys and
     *                     the {@link ISvgNodeRenderer} objects as values.
     * @param root a wrapped {@link ISvgNodeRenderer} root renderer.
     * @param fontProvider a {@link FontProvider} instance.
     * @param tempFonts a {@link FontSet} containing temporary fonts.
     * @deprecated use {@link SvgProcessorResult#SvgProcessorResult(Map, ISvgNodeRenderer, SvgProcessorContext)} instead.
     * Will be removed in 7.2.
     */
    @Deprecated
    public SvgProcessorResult(Map<String, ISvgNodeRenderer> namedObjects, ISvgNodeRenderer root,
                              FontProvider fontProvider, FontSet tempFonts) {
        this.namedObjects = namedObjects;
        this.root = root;
        this.fontProvider = fontProvider;
        this.tempFonts = tempFonts;
        this.context = new SvgProcessorContext(new SvgConverterProperties());
    }

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
        this.context = context;
        this.fontProvider = context.getFontProvider();
        this.tempFonts = context.getTempFonts();
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
        return fontProvider;
    }

    @Override
    public FontSet getTempFonts() {
        return tempFonts;
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
