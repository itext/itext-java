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
