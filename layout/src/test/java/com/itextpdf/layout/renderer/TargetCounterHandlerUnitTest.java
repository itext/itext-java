/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
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
        Assertions.assertNull(page);
    }

    @Test
    public void isValueDefinedForThisIdNotDocumentRendererTest() {
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

        IRenderer renderer = new TextRenderer(new Text("renderer")) {
            @Override
            public LayoutArea getOccupiedArea() {
                return null;
            }
        };
        renderer.setParent(documentRenderer);
        renderer.setProperty(Property.ID, id);

        Assertions.assertFalse(TargetCounterHandler.isValueDefinedForThisId(renderer, id));
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
        Assertions.assertNull(page);
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

        documentRenderer.getTargetCounterHandler().prepareHandlerToRelayout();
        Integer page = TargetCounterHandler.getPageByID(renderer, id);
        Assertions.assertEquals((Integer) 8, page);
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

        documentRenderer.getTargetCounterHandler().prepareHandlerToRelayout();
        Integer page = TargetCounterHandler.getPageByID(renderer, id);
        Assertions.assertEquals((Integer) 2, page);
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

        documentRenderer.getTargetCounterHandler().prepareHandlerToRelayout();
        Integer page = TargetCounterHandler.getPageByID(renderer, id);
        Assertions.assertEquals((Integer) expectedPage, page);

        IRenderer anotherRenderer = new TextRenderer(new Text("another_renderer"));
        anotherRenderer.setParent(documentRenderer);
        page = TargetCounterHandler.getPageByID(anotherRenderer, id);
        Assertions.assertEquals((Integer) expectedPage, page);
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
        Assertions.assertFalse(documentRenderer.isRelayoutRequired());
        TargetCounterHandler.addPageByID(renderer);
        Assertions.assertTrue(documentRenderer.isRelayoutRequired());
        documentRenderer.getTargetCounterHandler().prepareHandlerToRelayout();
        Assertions.assertFalse(documentRenderer.isRelayoutRequired());
    }

    @Test
    public void copyConstructorTest() {
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
        TargetCounterHandler copy = new TargetCounterHandler(documentRenderer.getTargetCounterHandler());
        Assertions.assertTrue(copy.isRelayoutRequired());
    }

    @Test
    public void isValueDefinedForThisId() {
        DocumentRenderer documentRenderer = new DocumentRenderer(null);
        String id = "id";
        String notAddedId = "not added id";

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
        Assertions.assertTrue(TargetCounterHandler.isValueDefinedForThisId(renderer, id));
        Assertions.assertFalse(TargetCounterHandler.isValueDefinedForThisId(renderer, notAddedId));
    }
}
