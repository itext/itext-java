package com.itextpdf.kernel.counter.event;

import com.itextpdf.kernel.counter.ContextManager;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CoreEventTest extends ExtendedITextTest {

    @Test
    public void coreNamespaceTest() {
        Assert.assertTrue(ContextManager.getInstance().getTopContext().allow(CoreEvent.PROCESS));
    }
}
