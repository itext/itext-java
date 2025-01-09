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
package com.itextpdf.kernel.pdf;

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
 * specifications that PDF-consuming applications use to interpret the extensions.
 */
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

    /** An instance of this class for ISO/TS 32001. */
    public static final PdfDeveloperExtension ISO_32001 = new PdfDeveloperExtension(
            PdfName.ISO_,
            PdfName.Pdf_Version_2_0,
            32001,
            "https://www.iso.org/standard/45874.html",
            ":2022",
            true);

    /** An instance of this class for ISO/TS 32002. */
    public static final PdfDeveloperExtension ISO_32002 = new PdfDeveloperExtension(
            PdfName.ISO_,
            PdfName.Pdf_Version_2_0,
            32002,
            "https://www.iso.org/standard/45875.html",
            ":2022",
            true);

    /** An instance of this class for ISO/TS 32004. */
    public static final PdfDeveloperExtension ISO_32004 = new PdfDeveloperExtension(
            PdfName.ISO_,
            PdfName.Pdf_Version_2_0,
            32004,
            "https://www.iso.org/standard/45877.html",
            ":2024",
            true);

    /** An instance of this class for ISO/TS 32003. */
    public static final PdfDeveloperExtension ISO_32003 = new PdfDeveloperExtension(
            PdfName.ISO_,
            PdfName.Pdf_Version_2_0,
            32003,
            "https://www.iso.org/standard/45876.html",
            ":2023",
            true);

    /** The prefix used in the Extensions dictionary added to the Catalog. */
    protected PdfName prefix;

    /** The base version. */
    protected PdfName baseVersion;

    /** The extension level within the base version. */
    protected int extensionLevel;

    /** The extension URL (ISO 32000-2:2020). */
    private final String url;

    /** The extension revision (ISO 32000-2:2020). */
    private final String extensionRevision;

    /** Whether the extension prefix is multivalued (ISO 32000-2:2020). */
    private final boolean isMultiValued;

    /**
     * Creates a PdfDeveloperExtension object.
     * @param prefix	the prefix referring to the developer
     * @param baseVersion	the number of the base version
     * @param extensionLevel	the extension level within the base version
     */
    public PdfDeveloperExtension(PdfName prefix, PdfName baseVersion, int extensionLevel) {
        this(prefix, baseVersion, extensionLevel, null, null, false);
    }

    /**
     * Creates a PdfDeveloperExtension object.
     * @param prefix	the prefix referring to the developer
     * @param baseVersion	the number of the base version
     * @param extensionLevel	the extension level within the base version
     * @param extensionRevision  the extension revision identifier
     * @param url  the URL specifying where to find more information about the extension
     * @param isMultiValued  flag indicating whether the extension prefix can have multiple values
     */
    public PdfDeveloperExtension(PdfName prefix, PdfName baseVersion, int extensionLevel,
                                 String url, String extensionRevision, boolean isMultiValued) {
        this.prefix = prefix;
        this.baseVersion = baseVersion;
        this.extensionLevel = extensionLevel;
        this.url = url;
        this.extensionRevision = extensionRevision;
        this.isMultiValued = isMultiValued;
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
     * Indicates whether the extension prefix is multivalued (ISO 32000-2:2020).
     *
     * @return true if multivalued
     */
    public boolean isMultiValued() {
        return isMultiValued;
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
        if (url != null) {
            developerextensions.put(PdfName.URL, new PdfString(url));
        }
        if (extensionRevision != null) {
            developerextensions.put(PdfName.ExtensionRevision, new PdfString(extensionRevision));
        }
        return developerextensions;
    }
}
