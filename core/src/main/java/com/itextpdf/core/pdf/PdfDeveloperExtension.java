package com.itextpdf.core.pdf;

/**
 * Beginning with BaseVersion 1.7, the extensions dictionary lets developers
 * designate that a given document contains extensions to PDF. The presence
 * of the extension dictionary in a document indicates that it may contain
 * developer-specific PDF properties that extend a particular base version
 * of the PDF specification.
 * The extensions dictionary enables developers to identify their own extensions
 * relative to a base version of PDF. Additionally, the convention identifies
 * extension levels relative to that base version. The intent of this dictionary
 * is to enable developers of PDF-producing applications to identify company-specific
 * specifications (such as this one) that PDF-consuming applications use to
 * interpret the extensions.
 */
/* TODO: There is one more entry in PDF 2.0 (URL entry), so probably this class
 * should be refactored to be more flexible in the future */
public class PdfDeveloperExtension {

    /** An instance of this class for Adobe 1.7 Extension level 3. */
    public static final PdfDeveloperExtension ADOBE_1_7_EXTENSIONLEVEL3 =
            new PdfDeveloperExtension(PdfName.ADBE, PdfName.Pdf_Version_1_7, 3);

    /** An instance of this class for ETSI 1.7 Extension level 2. */
    public static final PdfDeveloperExtension ESIC_1_7_EXTENSIONLEVEL2 =
            new PdfDeveloperExtension(PdfName.ESIC, PdfName.Pdf_Version_1_7, 2);

    /** An instance of this class for ETSI 1.7 Extension level 5. */
    public static final PdfDeveloperExtension ESIC_1_7_EXTENSIONLEVEL5 =
            new PdfDeveloperExtension(PdfName.ESIC, PdfName.Pdf_Version_1_7, 5);

    /** The prefix used in the Extensions dictionary added to the Catalog. */
    protected PdfName prefix;

    /** The base version. */
    protected PdfName baseVersion;

    /** The extension level within the base version. */
    protected int extensionLevel;

    /**
     * Creates a PdfDeveloperExtension object.
     * @param prefix	the prefix referring to the developer
     * @param baseVersion	the number of the base version
     * @param extensionLevel	the extension level within the baseverion.
     */
    public PdfDeveloperExtension(PdfName prefix, PdfName baseVersion, int extensionLevel) {
        this.prefix = prefix;
        this.baseVersion = baseVersion;
        this.extensionLevel = extensionLevel;
    }

    /**
     * Gets the prefix name.
     * @return	a PdfName
     */
    public PdfName getPrefix() {
        return prefix;
    }

    /**
     * Gets the baseVersion name.
     * @return	a PdfName
     */
    public PdfName getBaseVersion() {
        return baseVersion;
    }

    /**
     * Gets the extension level within the baseVersion.
     * @return	an integer
     */
    public int getExtensionLevel() {
        return extensionLevel;
    }

    /**
     * Generations the developer extension dictionary corresponding
     * with the prefix.
     * @return	a PdfDictionary
     */
    public PdfDictionary getDeveloperExtensions() {
        PdfDictionary developerextensions = new PdfDictionary();
        developerextensions.put(PdfName.BaseVersion, baseVersion);
        developerextensions.put(PdfName.ExtensionLevel, new PdfNumber(extensionLevel));

        return developerextensions;
    }
}
