package com.itextpdf.io.font.cmap;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CMapCodepointToCidTest extends ExtendedITextTest {
    @Test
    public void reverseConstructorTest() {
        CMapCidToCodepoint cidToCode = new CMapCidToCodepoint();
        cidToCode.addChar(new String(new byte[] {32, 17}), new CMapObject(CMapObject.NUMBER, 14));
        cidToCode.addChar(new String(new byte[] {32, 18}), new CMapObject(CMapObject.NUMBER, 15));

        CMapCodepointToCid codeToCid = new CMapCodepointToCid(cidToCode);
        Assert.assertEquals(14, codeToCid.lookup(8209));
        Assert.assertEquals(15, codeToCid.lookup(8210));
    }

    @Test
    public void addCharAndLookupTest() {
        CMapCodepointToCid codeToCid = new CMapCodepointToCid();
        Assert.assertEquals(0, codeToCid.lookup(8209));

        codeToCid.addChar(new String(new byte[] {32, 17}), new CMapObject(CMapObject.NUMBER, 14));
        codeToCid.addChar(new String(new byte[] {32, 19}), new CMapObject(CMapObject.STRING, "some text"));

        Assert.assertEquals(14, codeToCid.lookup(8209));
        Assert.assertEquals(0, codeToCid.lookup(1));
    }
}
