package com.itextpdf.commons.exceptions;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ITextExceptionTest extends ExtendedITextTest {
    @Test
    public void noParametersConstructorTest() {
        Exception exception = Assert.assertThrows(ITextException.class, () -> {
            throw new ITextException();
        });
        Assert.assertNull(exception.getMessage());
    }

    @Test
    public void stringConstructorTest() {
        Exception exception = Assert.assertThrows(ITextException.class, () -> {
            throw new ITextException("message");
        });
        Assert.assertEquals("message", exception.getMessage());
    }

    @Test
    public void throwableConstructorTest() {
        RuntimeException cause = new RuntimeException("cause");
        Exception exception = Assert.assertThrows(ITextException.class, () -> {
            throw new ITextException(cause);
        });

        Assert.assertEquals(CommonsExceptionMessageConstant.UNKNOWN_ITEXT_EXCEPTION, exception.getMessage());
        Assert.assertEquals(cause, exception.getCause());
    }
}
