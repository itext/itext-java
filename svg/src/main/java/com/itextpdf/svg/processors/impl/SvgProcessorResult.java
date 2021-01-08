/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
