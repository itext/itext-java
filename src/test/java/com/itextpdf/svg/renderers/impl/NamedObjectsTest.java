package com.itextpdf.svg.renderers.impl;

import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.svg.processors.ISvgProcessor;
import com.itextpdf.svg.processors.ISvgProcessorResult;
import com.itextpdf.svg.processors.impl.DefaultSvgProcessor;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class NamedObjectsTest {


    @Test
    public void addNamedObject() throws IOException {
        INode parsedSvg = SvgConverter.parse(new FileInputStream("./src/test/resources/com/itextpdf/svg/renderers/impl/NamedObjectsTest/names.svg"));
        ISvgProcessor processor = new DefaultSvgProcessor();
        ISvgProcessorResult result = processor.process( parsedSvg );

        Assert.assertTrue( result.getNamedObjects().get( "name_rect" ) instanceof RectangleSvgNodeRenderer );
    }
}