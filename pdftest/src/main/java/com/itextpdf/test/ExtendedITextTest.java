package com.itextpdf.test;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;


public abstract class ExtendedITextTest extends ITextTest {

    @Rule
    public LogListener logListener = new LogListener();

    @Before
    public void beforeTestMethodAction(){
    }

    @After
    public void afterTestMethodAction(){
    }



}
