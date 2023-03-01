package com.itextpdf.forms.form.renderer;

import com.itextpdf.forms.form.element.AbstractSelectField;
import com.itextpdf.forms.form.element.ListBoxField;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class SelectFieldListBoxRendererTest extends ExtendedITextTest {
    
    @Test
    public void getNextRendererTest() {
        SelectFieldListBoxRenderer listBoxRenderer = new SelectFieldListBoxRenderer(new ListBoxField("", 0, false));
        IRenderer nextRenderer = listBoxRenderer.getNextRenderer();
        
        Assert.assertTrue(nextRenderer instanceof SelectFieldListBoxRenderer);
    }
    
    @Test
    public void allowLastYLineRecursiveExtractionTest() {
        CustomSelectFieldListBoxRenderer listBoxRenderer =
                new CustomSelectFieldListBoxRenderer(new ListBoxField("", 0, false));
        boolean lastY = listBoxRenderer.callAllowLastYLineRecursiveExtraction();
        
        Assert.assertFalse(lastY);
    }
    
    private static class CustomSelectFieldListBoxRenderer extends SelectFieldListBoxRenderer {
        public CustomSelectFieldListBoxRenderer(AbstractSelectField modelElement) {
            super(modelElement);
        }
        
        public boolean callAllowLastYLineRecursiveExtraction() {
            return this.allowLastYLineRecursiveExtraction();
        }
    }
}
