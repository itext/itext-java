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
package com.itextpdf.svg.renderers.factories;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.INoDrawSvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.factories.DefaultSvgNodeRendererMapper.ISvgNodeRendererCreator;
import com.itextpdf.svg.renderers.impl.DefsSvgNodeRenderer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default implementation of {@link ISvgNodeRendererFactory} that will be
 * used by default by the entry points defined by this project.
 */
public class DefaultSvgNodeRendererFactory implements ISvgNodeRendererFactory {

    private final Map<String, ISvgNodeRendererCreator> rendererMap = new HashMap<>();
    private final Collection<String> ignoredTags = new HashSet<>();

    /**
     * Default constructor with default {@link ISvgNodeRenderer} creation logic.
     */
    public DefaultSvgNodeRendererFactory() {
        final DefaultSvgNodeRendererMapper defaultMapper = new DefaultSvgNodeRendererMapper();
        rendererMap.putAll(defaultMapper.getMapping());
        ignoredTags.addAll(defaultMapper.getIgnoredTags());
    }

    @Override
    public ISvgNodeRenderer createSvgNodeRendererForTag(IElementNode tag, ISvgNodeRenderer parent) {
        ISvgNodeRenderer result;

        if (tag == null) {
            throw new SvgProcessingException(SvgExceptionMessageConstant.TAG_PARAMETER_NULL);
        }

        final ISvgNodeRendererCreator svgNodeRendererCreator = rendererMap.get(tag.name());

        if (svgNodeRendererCreator == null) {
            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.warn(MessageFormatUtil.format(SvgLogMessageConstant.UNMAPPED_TAG, tag.name()));
            return null;
        }

        result = svgNodeRendererCreator.create();

        // DefsSvgNodeRenderer should not have parental relationship with any renderer, it only serves as a storage
        if (parent != null && !(result instanceof INoDrawSvgNodeRenderer) && !(parent instanceof DefsSvgNodeRenderer)) {
            result.setParent(parent);
        }

        return result;
    }

    @Override
    public boolean isTagIgnored(IElementNode tag) {
        return ignoredTags.contains(tag.name());
    }
}
