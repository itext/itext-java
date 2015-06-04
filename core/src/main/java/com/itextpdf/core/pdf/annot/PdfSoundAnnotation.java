package com.itextpdf.core.pdf.annot;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;
import java.io.*;

public class PdfSoundAnnotation extends PdfMarkupAnnotation {

    /*
        There is a problem playing *.wav files via internal player in Acrobat.
        The first byte of the audio stream data should be deleted, then wav file will be played correctly.
        Otherwise it will be broken. Other supporting file types don't have such problem.
     */
    public PdfSoundAnnotation(PdfDocument document, Rectangle rect, PdfStream sound) {
        super(document, rect);
        put(PdfName.Sound, sound);
    }

    public PdfSoundAnnotation(PdfDictionary pdfObject, PdfDocument document) {
        super(pdfObject, document);
    }

    public PdfSoundAnnotation(PdfDocument document, Rectangle rect, InputStream soundStream, float sampleRate, PdfName encoding, int channels, int sampleSizeInBits) throws IOException {
        super(document, rect);
        PdfStream sound = new PdfStream(document, correctInputStreamForWavFile(soundStream));
        sound.put(PdfName.R, new PdfNumber(sampleRate));
        sound.put(PdfName.E, encoding);
        sound.put(PdfName.B, new PdfNumber(sampleSizeInBits));
        sound.put(PdfName.C, new PdfNumber(channels));
        put(PdfName.Sound, sound);
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

    @Override
    public PdfName getSubtype() {
        return PdfName.Sound;
    }

    public PdfStream getSound() {
        return getPdfObject().getAsStream(PdfName.Sound);
    }
}
