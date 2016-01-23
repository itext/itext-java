package com.itextpdf.basics;

/**
 * Class containing constants to be used in logging.
 */
public class LogMessageConstant {

    /**
     * Log message.
     */
    public static final String DOCUMENT_ALREADY_HAS_FIELD = "The document already has field {0}. Annotations of the fields with this name will be added to the existing one as children. If you want to have separate fields, please, rename them manually before copying.";

    /**
     * Log message.
     */
    public static final String ELEMENT_DOES_NOT_FIT_AREA = "Element does not fit current area. {0}";

    /**
     * Log message.
     */
    public static final String FONT_HAS_INVALID_GLYPH = "Font {0} has invalid glyph: {1}";

    /**
     * Log message.
     */
    public static final String IMAGE_HAS_AMBIGUOUS_SCALE = "The image cannot be auto scaled and scaled by a certain parameter simultaneously";

    /**
     * Log message.
     */
    public static final String IMAGE_HAS_JBIG2DECODE_FILTER = "Image cannot be inline if it has JBIG2Decode filter. It will be added as an ImageXObject";

    /**
     * Log message.
     */
    public static final String IMAGE_HAS_MASK = "Image cannot be inline if it has a Mask";

    /**
     * Log message.
     */
    public static final String IMAGE_HAS_JPXDECODE_FILTER = "Image cannot be inline if it has JPXDecode filter. It will be added as an ImageXObject";

    /**
     * Log message.
     */
    public static final String IMAGE_SIZE_CANNOT_BE_MORE_4KB = "Inline image size cannot be more than 4KB. It will be added as an ImageXObject";

    /**
     * Log message.
     */
    public static final String INVALID_INDIRECT_REFERENCE = "Invalid indirect reference";

    /**
     * Log message.
     */
    public static final String ONLY_ONE_OF_ARTBOX_OR_TRIMBOX_CAN_EXIST_IN_THE_PAGE = "Only one of artbox or trimbox can exist on the page. The trimbox will be deleted";

    /**
     * Log message.
     */
    public static final String REGISTERING_DIRECTORY = "Registering directory";

    /**
     * Log message.
     */
    public static final String SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY = "Source document has AcroForm dictionary. The pages you are going to copy may have FormFields, but they will not be copied, because you have not used any IPdfPageExtraCopier";

    /**
     * Log message.
     */
    public static final String START_MARKER_MISSING_IN_PFB_FILE = "Start marker is missing in the pfb file";

    /**
     * Log message.
     */
    public static final String DOCUMENT_ALREADY_HAS_FIELD = "The document already has field {0}. Annotations of the fields with this name will be added to the existing one as children. If you want to have separate fields, please, rename them manually before copying.";

    /**
     * Log message.
     */
    public static final String UNKNOWN_ERROR_WHILE_PROCESSING_CMAP = "Unknown error while processing CMap.";

    /**
     * Log message.
     */
    public static final String TOUNICODE_CMAP_MORE_THAN_2_BYTES_NOT_SUPPORTED = "ToUnicode CMap more than 2 bytes not supported.";


}
