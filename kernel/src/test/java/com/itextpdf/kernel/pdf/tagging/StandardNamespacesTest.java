package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import static org.junit.Assert.*;

@Category(UnitTest.class)
public class StandardNamespacesTest extends ExtendedITextTest {

    @Test
    public void testHn01() {
        assertTrue(StandardNamespaces.isHnRole("H1"));
    }

    @Test
    public void testHn02() {
        assertFalse(StandardNamespaces.isHnRole("H1,2"));
    }
}