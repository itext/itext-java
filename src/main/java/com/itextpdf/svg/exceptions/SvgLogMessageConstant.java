package com.itextpdf.svg.exceptions;

public final class SvgLogMessageConstant {

    private SvgLogMessageConstant(){};

    public static final String LOOP ="Loop detected";
    public static final String NOROOT = "No root found";
    public static final String INODEROOTISNULL = "Input root value is null";
    public static final String TAGPARAMETERNULL = "Tag parameter must not be null";
    public static final String UNMAPPEDTAG = "Could not find implementation for tag {0}";
    public static final String COULDNOTINSTANTIATE = "Could not instantiate Renderer for tag {0}";
}
