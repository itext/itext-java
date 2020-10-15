package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.Property;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class TargetCounterHandlerUnitTest extends ExtendedITextTest {

    @Test
    public void addAndGetPageByDestinationNotDocumentRendererTest() {
        RootRenderer documentRenderer = new RootRenderer() {

            @Override
            public IRenderer getNextRenderer() {
                return null;
            }

            @Override
            protected void flushSingleRenderer(IRenderer resultRenderer) {
            }

            @Override
            protected LayoutArea updateCurrentArea(LayoutResult overflowResult) {
                return null;
            }
        };
        final String id = "id";
        final int expectedPage = 5;

        IRenderer renderer = new TextRenderer(new Text("renderer")) {
            @Override
            public LayoutArea getOccupiedArea() {
                return new LayoutArea(expectedPage, new Rectangle(50, 50));
            }
        };
        renderer.setParent(documentRenderer);
        renderer.setProperty(Property.ID, id);
        TargetCounterHandler.addPageByID(renderer);

        Integer page = TargetCounterHandler.getPageByID(renderer, id);
        Assert.assertNull(page);
    }

    @Test
    public void addAndGetPageByDestinationNullOccupiedAreaTest() {
        DocumentRenderer documentRenderer = new DocumentRenderer(null);
        String id = "id";

        IRenderer renderer = new TextRenderer(new Text("renderer")) {
            @Override
            public LayoutArea getOccupiedArea() {
                return null;
            }
        };
        renderer.setParent(documentRenderer);
        renderer.setProperty(Property.ID, id);
        TargetCounterHandler.addPageByID(renderer);

        Integer page = TargetCounterHandler.getPageByID(renderer, id);
        Assert.assertNull(page);
    }

    @Test
    public void addAndGetPageByDestinationDoubleAddIncreasedTest() {
        DocumentRenderer documentRenderer = new DocumentRenderer(null);
        String id = "id";

        IRenderer renderer = new TextRenderer(new Text("renderer")) {
            int expectedPage = 5;
            @Override
            public LayoutArea getOccupiedArea() {
                return new LayoutArea(expectedPage++, new Rectangle(50, 50));
            }
        };
        renderer.setParent(documentRenderer);
        renderer.setProperty(Property.ID, id);
        TargetCounterHandler.addPageByID(renderer);
        TargetCounterHandler.addPageByID(renderer);

        Integer page = TargetCounterHandler.getPageByID(renderer, id);
        Assert.assertEquals((Integer) 8, page);
    }

    @Test
    public void addAndGetPageByDestinationDoubleAddDecreasedTest() {
        DocumentRenderer documentRenderer = new DocumentRenderer(null);
        String id = "id";

        IRenderer renderer = new TextRenderer(new Text("renderer")) {
            int expectedPage = 5;
            @Override
            public LayoutArea getOccupiedArea() {
                return new LayoutArea(expectedPage--, new Rectangle(50, 50));
            }
        };
        renderer.setParent(documentRenderer);
        renderer.setProperty(Property.ID, id);
        TargetCounterHandler.addPageByID(renderer);
        TargetCounterHandler.addPageByID(renderer);

        Integer page = TargetCounterHandler.getPageByID(renderer, id);
        Assert.assertEquals((Integer) 4, page);
    }

    @Test
    public void addAndGetPageByDestinationTest() {
        DocumentRenderer documentRenderer = new DocumentRenderer(null);
        String id = "id";
        final int expectedPage = 5;

        IRenderer renderer = new TextRenderer(new Text("renderer")) {
            @Override
            public LayoutArea getOccupiedArea() {
                return new LayoutArea(expectedPage, new Rectangle(50, 50));
            }
        };
        renderer.setParent(documentRenderer);
        renderer.setProperty(Property.ID, id);
        TargetCounterHandler.addPageByID(renderer);

        Integer page = TargetCounterHandler.getPageByID(renderer, id);
        Assert.assertEquals((Integer) expectedPage, page);

        IRenderer anotherRenderer = new TextRenderer(new Text("another_renderer"));
        anotherRenderer.setParent(documentRenderer);
        page = TargetCounterHandler.getPageByID(anotherRenderer, id);
        Assert.assertEquals((Integer) expectedPage, page);
    }

    @Test
    public void isRelayoutRequiredTest() {
        DocumentRenderer documentRenderer = new DocumentRenderer(null);
        String id = "id";

        IRenderer renderer = new TextRenderer(new Text("renderer")) {

            @Override
            public LayoutArea getOccupiedArea() {
                int page = 4;
                return new LayoutArea(page, new Rectangle(50, 50));
            }
        };
        renderer.setParent(documentRenderer);
        renderer.setProperty(Property.ID, id);
        TargetCounterHandler.addPageByID(renderer);
        Assert.assertTrue(documentRenderer.isRelayoutRequired());
    }
}
