package com.itextpdf.basics;


public  class LogMessageConstant {

    public static final String ELEMENT_DOESNOT_FIT_AREA  ="Element doesn't fit current area. KeepTogether property will be ignored";
    public static final String IMAGE_HAS_JBIG2DECODE_FILTER = "Image cannot be inline if it has JBIG2Decode filter. It will be added as an ImageXObject";
    public static final String IMAGE_SIZE_CANNOT_BE_MORE_4KB = "Inline image size cannot be more than 4KB. It will be added as an ImageXObject";
    public static final String IMAGE_HAS_MASK = "Image cannot be inline if it has a Mask";
    public static final String IMAGE_HAS_JPXDECODE_FILTER = "Image cannot be inline if it has JPXDecode filter. It will be added as an ImageXObject";
    public static final String INVALID_INDIRECT_REFERENCE = "Invalid indirect reference";
    public static final String START_MARKER_MISSING_IN_PDB_FILE = "start.marker.missing.in.pfb.file";
    public static final String SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY = "Source document has AcroForm dictionary. The pages you're going to copy may have FormFields, but they won't be copied, because you haven't used any IPdfPageExtraCopier.";

}
