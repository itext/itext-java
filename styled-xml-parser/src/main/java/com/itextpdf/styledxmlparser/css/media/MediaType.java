/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.
    
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
package com.itextpdf.styledxmlparser.css.media;

import java.util.HashSet;
import java.util.Set;

/**
 * Class that bundles all the media types and allows you to registered valid media types in a {@link Set}.
 */
public final class MediaType {

    /** The Constant registeredMediaTypes. */
    private static final Set<String> registeredMediaTypes = new HashSet<>();

    /** The Constant ALL. */
    public static final String ALL = registerMediaType("all");
    
    /** The Constant AURAL. */
    public static final String AURAL = registerMediaType("aural");
    
    /** The Constant BRAILLE. */
    public static final String BRAILLE = registerMediaType("braille");
    
    /** The Constant EMBOSSED. */
    public static final String EMBOSSED = registerMediaType("embossed");
    
    /** The Constant HANDHELD. */
    public static final String HANDHELD = registerMediaType("handheld");
    
    /** The Constant PRINT. */
    public static final String PRINT = registerMediaType("print");
    
    /** The Constant PROJECTION. */
    public static final String PROJECTION = registerMediaType("projection");
    
    /** The Constant SCREEN. */
    public static final String SCREEN = registerMediaType("screen");
    
    /** The Constant SPEECH. */
    public static final String SPEECH = registerMediaType("speech");
    
    /** The Constant TTY. */
    public static final String TTY = registerMediaType("tty");
    
    /** The Constant TV. */
    public static final String TV = registerMediaType("tv");

    /**
     * Creates a new {@link MediaType} instance.
     */
    private MediaType() {
    }

    /**
     * Checks if a media type is registered as a valid media type.
     *
     * @param mediaType the media type
     * @return true, if it's a valid media type
     */
    public static boolean isValidMediaType(String mediaType) {
        return registeredMediaTypes.contains(mediaType);
    }

    /**
     * Registers a media type.
     *
     * @param mediaType the media type
     * @return the string
     */
    private static String registerMediaType(String mediaType) {
        registeredMediaTypes.add(mediaType);
        return mediaType;
    }

}
