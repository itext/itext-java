package com.itextpdf.basics.font.cmap;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.io.PdfTokenizer;
import com.itextpdf.basics.io.RandomAccessFileOrArray;
import com.itextpdf.basics.io.RandomAccessSourceFactory;
import com.itextpdf.basics.io.StreamUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author psoares
 */
public class CMapLocationResource implements CMapLocation{

    public PdfTokenizer getLocation(String location) throws IOException {
        String fullName = FontConstants.RESOURCE_PATH + "cmap/" + location;
        InputStream inp = StreamUtil.getResourceStream(fullName);
        if (inp == null) {
            throw new PdfException("the.cmap.1.was.not.found").setMessageParams(fullName);
        }
        return new PdfTokenizer(new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(inp)));
    }
}
