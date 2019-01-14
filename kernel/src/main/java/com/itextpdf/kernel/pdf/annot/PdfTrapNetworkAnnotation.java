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
package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDate;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

import java.util.List;

/**
 * A {@link PdfTrapNetworkAnnotation} may be used to define the trapping characteristics for a page
 * of a PDF document. Trapping is the process of adding marks to a page along colour boundaries
 * to avoid unwanted visual artifacts resulting from misregistration of colorants when the page is printed.
 * TrapNet annotations are deprecated in PDF 2.0.
 * <p>
 * See ISO-320001 14.11.6 "Trapping Support" and 14.11.6.2 "Trap Network Annotations" in particular.
 */
public class PdfTrapNetworkAnnotation extends PdfAnnotation {

    private static final long serialVersionUID = 5118904991630303608L;

    /**
     * Creates a {@link PdfTrapNetworkAnnotation} instance. Note that there shall be at most one trap network annotation
     * per page, which shall be the last element in the page’s Annots array.
     * TrapNet annotations are deprecated in PDF 2.0.
     *
     * @param rect             the annotation rectangle, defining the location of the annotation on the page
     *                         in default user space units. See {@link PdfAnnotation#setRectangle(PdfArray)}.
     * @param appearanceStream the form XObject defining a trap network which body contains the graphics objects needed
     *                         to paint the traps making up the trap network. Process colour model shall be defined for the
     *                         appearance stream (see {@link PdfFormXObject#setProcessColorModel(PdfName)}.
     *                         See also ISO-320001 Table 367 "Additional entries specific to a trap network appearance stream".
     */
    public PdfTrapNetworkAnnotation(Rectangle rect, PdfFormXObject appearanceStream) {
        super(rect);
        if (appearanceStream.getProcessColorModel() == null) {
            throw new PdfException("Process color model must be set in appearance stream for Trap Network annotation!");
        }
        setNormalAppearance(appearanceStream.getPdfObject());
        setFlags(PdfAnnotation.PRINT | PdfAnnotation.READ_ONLY);
    }

    /**
     * Creates a {@link PdfLineAnnotation} instance from the given {@link PdfDictionary}
     * that represents annotation object. This method is useful for property reading in reading mode or
     * modifying in stamping mode.
     * TrapNet annotations are deprecated in PDF 2.0.
     *
     * @param pdfObject a {@link PdfDictionary} that represents existing annotation in the document.
     * see {@link PdfAnnotation#makeAnnotation(PdfObject)}
     */
    protected PdfTrapNetworkAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PdfName getSubtype() {
        return PdfName.TrapNet;
    }

    /**
     * The date and time when the trap network was most recently modified.
     * <p>
     * This entry is required if /Version ({@link #getVersion()}) and /AnnotStates ({@link #getAnnotStates()})
     * entries are absent; shall be absent if /Version and /AnnotStates entries are present.
     *
     * @param lastModified a {@link PdfDate} wrapper with the specified date.
     * @return this {@link PdfTrapNetworkAnnotation} instance.
     */
    public PdfTrapNetworkAnnotation setLastModified(PdfDate lastModified) {
        return (PdfTrapNetworkAnnotation) put(PdfName.LastModified, lastModified.getPdfObject());
    }

    /**
     * The date and time when the trap network was most recently modified.
     *
     * @return a {@link PdfString} with date. The format should be a date string as described
     *             in ISO-320001 7.9.4, "Dates". See also {@link PdfDate#decode(String)}.
     */
    public PdfString getLastModified() {
        return getPdfObject().getAsString(PdfName.LastModified);
    }

    /**
     * An unordered array of all objects present in the page description at the time the trap networks
     * were generated and that, if changed, could affect the appearance of the page.
     * <p>
     * <p>
     * This entry is required if /AnnotStates ({@link #getAnnotStates()}) is present;
     * shall be absent if /LastModified ({@link #getLastModified()}) is present.
     *
     * @param version an unordered {@link PdfArray} of all objects present in the page description at the time the trap networks
     *                were generated. If present, the array shall include the following objects:
     *                <ul>
     *                    <li>all page content streams;</li>
     *                    <li>all page resource objects (other than procedure sets);</li>
     *                    <li>all resource objects (other than procedure sets) of any form XObjects on the page;</li>
     *                    <li>all OPI dictionaries associated with XObjects on the page (see ISO-320001 14.11.7, "Open Prepress Interface (OPI)")</li>
     *                 </ul>
     * @return this {@link PdfTrapNetworkAnnotation} instance.
     */
    public PdfTrapNetworkAnnotation setVersion(PdfArray version) {
        return (PdfTrapNetworkAnnotation) put(PdfName.Version, version);
    }

    /**
     * An unordered array of all objects present in the page description at the time the trap networks were generated
     * and that, if changed, could affect the appearance of the page.
     *
     * @return an unordered {@link PdfArray} of all objects present in the page description at the time the trap networks
     * were generated.
     */
    public PdfArray getVersion() {
        return getPdfObject().getAsArray(PdfName.Version);
    }

    /**
     * An array of name objects representing the appearance states (value of the /AS entry {@link PdfAnnotation#getAppearanceState()})
     * for annotations associated with the page. The appearance states shall be listed in the same order as the annotations
     * in the page’s /Annots array. For an annotation with no /AS entry, the corresponding array element
     * should be {@link com.itextpdf.kernel.pdf.PdfNull}.
     * No appearance state shall be included for the trap network annotation itself.
     * <p>
     * <p>
     * Required if /Version ({@link #getVersion()}) is present; shall be absent if /LastModified {@link #getLastModified()} is present.
     *
     * @param annotStates a {@link PdfArray} of name objects representing the appearance states for annotations associated with the page.
     * @return this {@link PdfTrapNetworkAnnotation} instance.
     */
    public PdfTrapNetworkAnnotation setAnnotStates(PdfArray annotStates) {
        return (PdfTrapNetworkAnnotation) put(PdfName.AnnotStates, annotStates);
    }

    /**
     * An array of name objects representing the appearance states for annotations associated with the page.
     * See also {@link #setAnnotStates(PdfArray)}.
     *
     * @return a {@link PdfArray} of name objects representing the appearance states for annotations associated with the page,
     */
    public PdfArray getAnnotStates() {
        return getPdfObject().getAsArray(PdfName.AnnotStates);
    }

    /**
     * An array of font dictionaries representing fonts that were fauxed (replaced by substitute fonts) during the
     * generation of trap networks for the page.
     *
     * @param fauxedFonts a {@link PdfArray} of {@link PdfDictionary} each of which represent font in the document.
     * @return this {@link PdfTrapNetworkAnnotation} instance.
     */
    public PdfTrapNetworkAnnotation setFauxedFonts(PdfArray fauxedFonts) {
        return (PdfTrapNetworkAnnotation) put(PdfName.FontFauxing, fauxedFonts);
    }

    /**
     * A list of font dictionaries representing fonts that were fauxed (replaced by substitute fonts) during the
     * generation of trap networks for the page.
     *
     * @param fauxedFonts a {@link List} of {@link PdfFont} objects.
     * @return this {@link PdfTrapNetworkAnnotation} instance.
     */
    public PdfTrapNetworkAnnotation setFauxedFonts(List<PdfFont> fauxedFonts) {
        PdfArray arr = new PdfArray();
        for (PdfFont f : fauxedFonts)
            arr.add(f.getPdfObject());
        return setFauxedFonts(arr);
    }

    /**
     * An array of font dictionaries representing fonts that were fauxed (replaced by substitute fonts) during the
     * generation of trap networks for the page.
     *
     * @return a {@link PdfArray} of {@link PdfDictionary} each of which represent font in the document.
     */
    public PdfArray getFauxedFonts() {
        return getPdfObject().getAsArray(PdfName.FontFauxing);
    }
}
