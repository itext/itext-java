package com.itextpdf.svg.renderers;

import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.impl.CircleSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.EllipseSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.GroupSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.RectangleSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.SvgTagSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.UseSvgNodeRenderer;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class DeepCopyIntegrationTest {

    @Test
    public void deepCopyTest(){
        //Deep copy of tree with nested svg and group and set attributes
        UseSvgNodeRenderer nestedUse = new UseSvgNodeRenderer();
        nestedUse.setAttribute(SvgConstants.Attributes.HREF,"#c1");

        GroupSvgNodeRenderer nestedGroup = new GroupSvgNodeRenderer();
        nestedGroup.setAttribute(SvgConstants.Attributes.FILL,"blue");
        nestedGroup.addChild(nestedUse);

        CircleSvgNodeRenderer nestedCircle = new CircleSvgNodeRenderer();
        nestedCircle.setAttribute(SvgConstants.Attributes.R,"100");

        SvgTagSvgNodeRenderer nestedSvg = new SvgTagSvgNodeRenderer();
        nestedSvg.setAttribute(SvgConstants.Attributes.X,"200");
        nestedSvg.setAttribute(SvgConstants.Attributes.Y,"200");
        nestedSvg.setAttribute(SvgConstants.Attributes.XMLNS,SvgConstants.Values.SVGNAMESPACEURL);
        nestedSvg.setAttribute(SvgConstants.Attributes.VERSION,SvgConstants.Values.VERSION1_1);
        nestedSvg.addChild(nestedCircle);
        nestedSvg.addChild(nestedGroup);

        RectangleSvgNodeRenderer nestedRectangle = new RectangleSvgNodeRenderer();
        nestedRectangle.setAttribute(SvgConstants.Attributes.WIDTH, "100");
        nestedRectangle.setAttribute(SvgConstants.Attributes.HEIGHT, "50");

        GroupSvgNodeRenderer topGroup = new GroupSvgNodeRenderer();
        topGroup.setAttribute(SvgConstants.Attributes.FILL,"red");
        topGroup.addChild(nestedRectangle);

        CircleSvgNodeRenderer topCircle = new CircleSvgNodeRenderer();
        topCircle.setAttribute(SvgConstants.Attributes.R,"80");
        topCircle.setAttribute(SvgConstants.Attributes.X,"100");
        topCircle.setAttribute(SvgConstants.Attributes.Y,"100");
        topCircle.setAttribute(SvgConstants.Attributes.STROKE,"red");
        topCircle.setAttribute(SvgConstants.Attributes.FILL,"green");

        SvgTagSvgNodeRenderer topSvg = new SvgTagSvgNodeRenderer();
        topSvg.setAttribute(SvgConstants.Attributes.WIDTH,"800");
        topSvg.setAttribute(SvgConstants.Attributes.HEIGHT,"800");
        topSvg.setAttribute(SvgConstants.Attributes.XMLNS,SvgConstants.Values.SVGNAMESPACEURL);
        topSvg.setAttribute(SvgConstants.Attributes.VERSION,SvgConstants.Values.VERSION1_1);
        topSvg.addChild(topCircle);
        topSvg.addChild(topGroup);

        EllipseSvgNodeRenderer ellipse = new EllipseSvgNodeRenderer();
        ellipse.setAttribute(SvgConstants.Attributes.CX,"10");
        ellipse.setAttribute(SvgConstants.Attributes.CY,"20");
        ellipse.setAttribute(SvgConstants.Attributes.RX,"30");
        ellipse.setAttribute(SvgConstants.Attributes.RX,"40");

        topSvg.addChild(ellipse);

        ISvgNodeRenderer copy = topSvg.createDeepCopy();

        Assert.assertEquals(topSvg,copy);

    }
}
