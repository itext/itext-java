package com.itextpdf.io.font.cmap;

import com.itextpdf.io.IOException;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.io.source.StreamUtil;

import java.io.InputStream;

/**
 * @author psoares
 */
public class CMapLocationResource implements CMapLocation{

    public PdfTokenizer getLocation(String location) throws java.io.IOException {
        String fullName = FontConstants.RESOURCE_PATH + "cmap/" + location;
        InputStream inp = StreamUtil.getResourceStream(fullName);
        if (inp == null) {
            throw new IOException("the.cmap.1.was.not.found").setMessageParams(fullName);
        }
        return new PdfTokenizer(new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(inp)));
    }
}
