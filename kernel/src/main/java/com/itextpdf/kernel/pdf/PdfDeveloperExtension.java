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
