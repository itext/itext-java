package com.itextpdf.core.image;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.codec.Jbig2SegmentReader;
import com.itextpdf.basics.image.Jbig2Image;
import com.itextpdf.basics.image.Image;
import com.itextpdf.basics.io.RandomAccessFileOrArray;
import com.itextpdf.basics.io.RandomAccessSource;
import com.itextpdf.basics.io.RandomAccessSourceFactory;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class Jbig2ImageHelper {

    private byte[] globals;

    /**
     * Gets a byte array that can be used as a /JBIG2Globals,
     * or null if not applicable to the given jbig2.
     * @param	ra	an random access file or array
     * @return	a byte array
     */
    public static byte[] getGlobalSegment(RandomAccessFileOrArray ra ) {
        try {
            Jbig2SegmentReader sr = new Jbig2SegmentReader(ra);
            sr.read();
            return sr.getGlobal(true);
        } catch (Exception e) {
            return null;
        }
    }

    public static void processImage(Image jbig2, PdfStream pdfStream) {
        if (jbig2.getOriginalType() != Image.JBIG2)
            throw new IllegalArgumentException("JBIG2 image expected");
        Jbig2Image image = (Jbig2Image)jbig2;

        if (pdfStream != null) {
            updatePdfStream(pdfStream, image);
        }
    }


    private static void updatePdfStream(PdfStream pdfStream, Jbig2Image image) {
        byte[] data;
        if (image.getUrl() != null) {
            InputStream is = null;
            try {
                is = image.getUrl().openStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int read;
                byte[] bytes = new byte[4096];
                while ((read = is.read(bytes)) != -1) {
                    baos.write(bytes, 0, read);
                }
                is.close();
                data = baos.toByteArray();
                baos.flush();
                baos.close();
            } catch (IOException e) {
                throw new PdfException(PdfException.Jbig2ImageException, e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ignored) { }
                }
            }
        } else {
            data = image.getData();
        }
        try {
            RandomAccessSource ras = new RandomAccessSourceFactory().createSource(data);
            RandomAccessFileOrArray raf = new RandomAccessFileOrArray(ras);
            Jbig2SegmentReader sr = new Jbig2SegmentReader(raf);
            sr.read();
            Jbig2SegmentReader.Jbig2Page p = sr.getPage(image.getPage());
            raf.close();

            image.setHeight(p.pageBitmapHeight);
            image.setWidth(p.pageBitmapWidth);
            image.setBpc(1);
            image.setColorSpace(1);
            //TODO JBIG2 globals caching
            byte[] globals = sr.getGlobal(true);


            //TODO due to the fact, that streams now may be transformed to indirect objects only on writing,
            //pdfStream.getDocument() cannot longer be the sign of inline/indirect images

            // in case inline image pdfStream.getDocument() will be null
            if (globals != null && pdfStream.getDocument() != null) {
                PdfDictionary decodeParms = new PdfDictionary();
                PdfStream globalsStream = new PdfStream().makeIndirect(pdfStream.getDocument());
                globalsStream.getOutputStream().write(globals);
                decodeParms.put(PdfName.JBIG2Globals, globalsStream.getIndirectReference());
                pdfStream.put(PdfName.DecodeParms, decodeParms);
            }

            pdfStream.put(PdfName.Filter, PdfName.JBIG2Decode);
            pdfStream.put(PdfName.ColorSpace, PdfName.DeviceGray);
            pdfStream.put(PdfName.BitsPerComponent, new PdfNumber(1));
            pdfStream.getOutputStream().write(p.getData(true));
        } catch (IOException e) {
            throw new PdfException(PdfException.Jbig2ImageException, e);
        }
    }
}
