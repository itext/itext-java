package com.itextpdf.core.pdf;

import org.junit.Assert;
import org.junit.Test;

public class PdfNameTest {

    @Test
    public void specialCharactersTest(){
        String str1 = " %()<>";
        String str2 = "[]{}/#";
        PdfName name1 = new PdfName(str1);
        Assert.assertEquals(str1, createStringByEscaped(name1.getInternalContent()));
        PdfName name2 = new PdfName(str2);
        Assert.assertEquals(str2, createStringByEscaped(name2.getInternalContent()));
    }

    String createStringByEscaped(byte[] bytes) {
        //sample "/PdfName"
        String[] chars = (new String(bytes)).substring(1).split("#");
        StringBuilder buf = new StringBuilder(chars.length);
        for (String ch : chars) {
            if (ch.length() == 0) continue;
            Integer b = Integer.parseInt(ch, 16);
            buf.append((char)b.intValue());
        }
        return buf.toString();
    }
}
