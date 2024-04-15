package com.itextpdf.kernel.xmp.impl;

import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.options.PropertyOptions;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class XMPNodeTest extends ExtendedITextTest {
    @Test
    public void test() throws XMPException {
        XMPNode node = new XMPNode("rdf:RDF", "idk", new PropertyOptions());
        node.addChild(new XMPNode("rdf:Description", "idk", new PropertyOptions()));
        for (Object object : node.getUnmodifiableChildren()) {
            AssertUtil.doesNotThrow(() ->
                    node.addChild(new XMPNode("xmp:Authors", "itext", new PropertyOptions().setArrayAlternate(true))));
        }
    }
}
