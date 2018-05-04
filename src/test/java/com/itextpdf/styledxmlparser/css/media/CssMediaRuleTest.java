package com.itextpdf.styledxmlparser.css.media;

import com.itextpdf.styledxmlparser.css.CssRuleSet;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupHtmlParser;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)

public class CssMediaRuleTest extends ExtendedITextTest {
    @Test
    public void matchMediaDeviceTest() {
        MediaDeviceDescription deviceDescription = new MediaDeviceDescription("all");
        deviceDescription.setHeight(450);
        deviceDescription.setWidth(600);
        CssMediaRule rule = new CssMediaRule("@media all and (min-width: 600px) and (min-height: 600px)");
        Assert.assertTrue(rule.matchMediaDevice(deviceDescription));
    }

    @Test
    public void getCssRuleSetsTest() {
        MediaDeviceDescription deviceDescription = new MediaDeviceDescription("all");
        String html = "<a id=\"123\" class=\"baz = 'bar'\" style = media= all and (min-width: 600px) />";
        IDocumentNode node = new JsoupHtmlParser().parse(html);
        List<CssRuleSet> ruleSets = new CssMediaRule("only all and (min-width: 600px) and (min-height: 600px)").getCssRuleSets(node, deviceDescription);
        Assert.assertNotNull(ruleSets);
    }
}
