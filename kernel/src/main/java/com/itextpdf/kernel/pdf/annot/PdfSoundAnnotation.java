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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfStream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PdfSoundAnnotation extends PdfMarkupAnnotation {

    private static final long serialVersionUID = -2319779211858842136L;

    /**
     * Creates a new Sound annotation.
     * There is a problem playing *.wav files via internal player in Acrobat.
     * The first byte of the audio stream data should be deleted, then wav file will be played correctly.
     * Otherwise it will be broken. Other supporting file types don't have such problem.
     * Sound annotations are deprecated in PDF 2.0.
     * @param rect
     * @param sound
     */
    public PdfSoundAnnotation(Rectangle rect, PdfStream sound) {
        super(rect);
        put(PdfName.Sound, sound);
    }

    /**
     * see {@link PdfAnnotation#makeAnnotation(PdfObject)}
     */
    protected PdfSoundAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Creates a sound annotation. Sound annotations are deprecated in PDF 2.0.
     * @param document
     * @param rect
     * @param soundStream
     * @param sampleRate
     * @param encoding
     * @param channels
     * @param sampleSizeInBits
     * @throws IOException
     */
    public PdfSoundAnnotation(PdfDocument document, Rectangle rect, InputStream soundStream, float sampleRate, PdfName encoding, int channels, int sampleSizeInBits) throws IOException {
        super(rect);
        PdfStream sound = new PdfStream(document, correctWavFile(soundStream));
        sound.put(PdfName.R, new PdfNumber(sampleRate));
        sound.put(PdfName.E, encoding);
        sound.put(PdfName.B, new PdfNumber(sampleSizeInBits));
        sound.put(PdfName.C, new PdfNumber(channels));
        put(PdfName.Sound, sound);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Sound;
    }

    public PdfStream getSound() {
        return getPdfObject().getAsStream(PdfName.Sound);
    }

    private static InputStream correctWavFile(InputStream is) throws IOException {
        String header = "";
        InputStream bufferedIn = new BufferedInputStream(is);
        bufferedIn.mark(0);
        for (int i = 0; i < 4; i++) {
            header = header + (char) bufferedIn.read();
        }
        bufferedIn.reset();
        if (header.equals("RIFF")) {
            bufferedIn.read();
        }
        return bufferedIn;
    }

    /**
     * The name of an icon that is used in displaying the annotation. Possible values are different for different
     * annotation types. See {@link #setIconName(PdfName)}.
     * @return a {@link PdfName} that specifies the icon for displaying annotation, or null if icon name is not specified.
     */
    public PdfName getIconName() {
        return getPdfObject().getAsName(PdfName.Name);
    }

    /**
     * The name of an icon that is used in displaying the annotation.
     * @param name a {@link PdfName} that specifies the icon for displaying annotation. Possible values are different
     *             for different annotation types:
     *             <ul>
     *                  <li>Speaker
     *                  <li>Mic
     *             </ul>
     *              Additional names may be supported as well.
     * @return this {@link PdfSoundAnnotation} instance.
     */
    public PdfSoundAnnotation setIconName(PdfName name) {
        return (PdfSoundAnnotation) put(PdfName.Name, name);
    }
}
