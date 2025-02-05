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
package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PdfSoundAnnotation extends PdfMarkupAnnotation {


    /**
     * Creates a new Sound annotation.
     * There is a problem playing *.wav files via internal player in Acrobat.
     * The first byte of the audio stream data should be deleted, then wav file will be played correctly.
     * Otherwise it will be broken. Other supporting file types don't have such problem.
     * Sound annotations are deprecated in PDF 2.0.
     *
     * @param rect the rectangle that specifies annotation position and bounds on page
     * @param sound the {@link PdfStream} with sound
     */
    public PdfSoundAnnotation(Rectangle rect, PdfStream sound) {
        super(rect);
        put(PdfName.Sound, sound);
    }

    /**
     * Instantiates a new {@link PdfSoundAnnotation} instance based on {@link PdfDictionary}
     * instance, that represents existing annotation object in the document.
     *
     * @param pdfObject the {@link PdfDictionary} representing annotation object
     * @see PdfAnnotation#makeAnnotation(PdfObject)
     */
    protected PdfSoundAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Creates a sound annotation. Sound annotations are deprecated in PDF 2.0.
     *
     * @param document the {@link PdfDocument} to which annotation will be added
     * @param rect the rectangle that specifies annotation position and bounds on page
     * @param soundStream the {@link PdfStream} with sound
     * @param sampleRate the sampling rate, in samples per second
     * @param encoding the encoding format for the sample data
     * @param channels the number of sound channels
     * @param sampleSizeInBits the number of bits per sample value per channel
     * @throws IOException in case of corrupted data or source stream problems
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
        if ("RIFF".equals(header)) {
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
