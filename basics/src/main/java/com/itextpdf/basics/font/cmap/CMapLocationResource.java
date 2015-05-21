package com.itextpdf.basics.font.cmap;

import com.itextpdf.basics.PdfRuntimeException;
import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.io.PdfTokeniser;
import com.itextpdf.basics.io.RandomAccessFileOrArray;
import com.itextpdf.basics.io.RandomAccessSourceFactory;
import com.itextpdf.basics.io.StreamUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author psoares
 */
public class CMapLocationResource implements CMapLocation{

    public PdfTokeniser getLocation(String location) throws IOException {
        String fullName = FontConstants.RESOURCE_PATH + "cmaps/" + location;
        InputStream inp = StreamUtil.getResourceStream(fullName);
        if (inp == null) {
            throw new PdfRuntimeException("the.cmap.1.was.not.found").setMessageParams(fullName);
        }
        return new PdfTokeniser(new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(inp)));
    }
}
