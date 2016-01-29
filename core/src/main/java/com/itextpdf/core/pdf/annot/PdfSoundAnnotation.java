package com.itextpdf.core.pdf.annot;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfStream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PdfSoundAnnotation extends PdfMarkupAnnotation {

    /*
        There is a problem playing *.wav files via internal player in Acrobat.
        The first byte of the audio stream data should be deleted, then wav file will be played correctly.
        Otherwise it will be broken. Other supporting file types don't have such problem.
     */
    public PdfSoundAnnotation(Rectangle rect, PdfStream sound) {
        super(rect);
        put(PdfName.Sound, sound);
    }

    public PdfSoundAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfSoundAnnotation(PdfDocument document, Rectangle rect, InputStream soundStream, float sampleRate, PdfName encoding, int channels, int sampleSizeInBits) throws IOException {
        super(rect);
        PdfStream sound = new PdfStream(document, correctInputStreamForWavFile(soundStream));
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

    private InputStream correctInputStreamForWavFile(InputStream is) throws IOException {
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
}
