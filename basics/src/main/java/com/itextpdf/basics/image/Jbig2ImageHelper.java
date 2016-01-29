package com.itextpdf.basics.image;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.codec.Jbig2SegmentReader;
import com.itextpdf.basics.source.ByteArrayOutputStream;
import com.itextpdf.basics.source.RandomAccessFileOrArray;
import com.itextpdf.basics.source.RandomAccessSource;
import com.itextpdf.basics.source.RandomAccessSourceFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


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

    public static void processImage(Image jbig2, ByteArrayOutputStream stream) {
        if (jbig2.getOriginalType() != Image.JBIG2)
            throw new IllegalArgumentException("JBIG2 image expected");
        Jbig2Image image = (Jbig2Image)jbig2;

        if (stream != null) {
            updateStream(stream, image);
        }
    }


    private static void updateStream(ByteArrayOutputStream stream, Jbig2Image image) {
        byte[] data;
        if (image.getUrl() != null) {
            InputStream is = null;
            try {
                is = image.getUrl().openStream();
                int read;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
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
            if (globals != null /*&& stream.getDocument() != null*/) {
                Map<String, Object> decodeParms = new HashMap<>();
//                PdfStream globalsStream = new PdfStream().makeIndirect(pdfStream.getDocument());
//                globalsStream.getOutputStream().write(globals);
                decodeParms.put("JBIG2Globals", globals);
                image.decodeParms = decodeParms;
            }

            image.setFilter("JBIG2Decode");
            image.setColorSpace(1);
            image.setBpc(1);

            stream.write(p.getData(true));
        } catch (IOException e) {
            throw new PdfException(PdfException.Jbig2ImageException, e);
        }
    }
}
