/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.svg.renderers.factories;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.NoDrawOperationSvgNodeRenderer;

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

    private Map<String, Class<? extends ISvgNodeRenderer>> rendererMap = new HashMap<>();
    private Collection<String> ignoredTags = new HashSet<>();

    /**
     * Default constructor which uses the default {@link ISvgNodeRendererMapper}
     * implementation.
     */
    public DefaultSvgNodeRendererFactory() {
        this(new DefaultSvgNodeRendererMapper());
    }

    /**
     * Constructor which allows injecting a custom
     * {@link ISvgNodeRendererMapper} implementation.
     *
     * @param mapper the custom mapper implementation - if null, then we fall
     * back to the {@link DefaultSvgNodeRendererMapper}
     */
    public DefaultSvgNodeRendererFactory(ISvgNodeRendererMapper mapper) {
        if (mapper != null) {
            rendererMap.putAll(mapper.getMapping());
            ignoredTags.addAll(mapper.getIgnoredTags());
        } else {
            ISvgNodeRendererMapper defaultMapper = new DefaultSvgNodeRendererMapper();
            rendererMap.putAll(defaultMapper.getMapping());
            ignoredTags.addAll(defaultMapper.getIgnoredTags());
        }
    }

    @Override
    public ISvgNodeRenderer createSvgNodeRendererForTag(IElementNode tag, ISvgNodeRenderer parent) {
        ISvgNodeRenderer result;

        if (tag == null) {
            throw new SvgProcessingException(SvgLogMessageConstant.TAGPARAMETERNULL);
        }

        try {
            Class<? extends ISvgNodeRenderer> clazz = rendererMap.get(tag.name());

            if (clazz == null) {
                Logger logger = LoggerFactory.getLogger(this.getClass());
                logger.warn(MessageFormatUtil.format(SvgLogMessageConstant.UNMAPPEDTAG, tag.name()));
                return null;
            }

            result = (ISvgNodeRenderer) rendererMap.get(tag.name()).newInstance();
        } catch (ReflectiveOperationException ex) {
            LoggerFactory.getLogger(DefaultSvgNodeRendererFactory.class)
                    .error(DefaultSvgNodeRendererFactory.class.getName(), ex);
            throw new SvgProcessingException(SvgLogMessageConstant.COULDNOTINSTANTIATE, ex).setMessageParams(tag.name());
        }

        if (parent != null && !(parent instanceof NoDrawOperationSvgNodeRenderer )) {
            result.setParent(parent);
        }

        return result;
    }

    @Override
    public boolean isTagIgnored(IElementNode tag){
        return ignoredTags.contains(tag.name());
    }
}
