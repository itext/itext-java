package com.itextpdf.forms;

import com.itextpdf.forms.xfdf.FitObject;
import com.itextpdf.forms.xfdf.XfdfException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class XfdfUnitTest extends ExtendedITextTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void fitObjectWithEmptyPageTest(){
        junitExpectedException.expect(XfdfException.class);
        junitExpectedException.expectMessage(XfdfException.PAGE_IS_MISSING);

        FitObject fitObject = new FitObject(null);

        Assert.fail();
    }

}
