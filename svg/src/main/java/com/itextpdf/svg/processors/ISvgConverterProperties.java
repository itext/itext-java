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
package com.itextpdf.svg.processors;

import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.svg.renderers.factories.ISvgNodeRendererFactory;
import java.nio.charset.Charset;

/**
 * Interface for the configuration classes used by {@link ISvgProcessor}
 */
public interface ISvgConverterProperties {

    /**
     * Retrieve the factory responsible for creating
     * {@link com.itextpdf.svg.renderers.ISvgNodeRenderer}
     *
     * @return A {@link ISvgNodeRendererFactory} implementation
     */
    ISvgNodeRendererFactory getRendererFactory();

    /**
     * Gets the font provider.
     *
     * @return the font provider
     */
    FontProvider getFontProvider();

    /**
     * Get the name of the Charset to be used when decoding an InputStream. This
     * method is allowed to return null, in which case {@code UTF-8} will
     * be used (by JSoup).
     * <p>
     * Please be aware that this method is NOT used when handling a
     * {@code String} variable in the {@link SvgConverter}.
     *
     * @return the String name of the {@link Charset} used for decoding
     */
    String getCharset();


    /**
     * Gets the base URI.
     *
     * @return the base URI
     */
    String getBaseUri();

    /**
     * Gets the media device description.
     *
     * @return the media device description
     */
    MediaDeviceDescription getMediaDeviceDescription();
}
