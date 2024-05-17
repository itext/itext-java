/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
import com.itextpdf.layout.properties.Property;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class GridUnitTest extends ExtendedITextTest {

    @Test
    public void getClosestTopNeighborTest() {
        Grid grid = new Grid(3, 3, false);
        grid.addCell(new GridCell(new TextRenderer(new Text("One"))));
        grid.addCell(new GridCell(new TextRenderer(new Text("Three"))));
        IRenderer value = new TextRenderer(new Text("Two"));
        value.setProperty(Property.GRID_COLUMN_START, 2);
        value.setProperty(Property.GRID_ROW_START, 2);
        GridCell cell = new GridCell(value);
        grid.addCell(cell);
        Assert.assertEquals(grid.getRows().get(0).get(0), grid.getClosestTopNeighbor(cell));
    }

    @Test
    public void getClosestLeftNeighborTest() {
        Grid grid = new Grid(3, 3, false);
        grid.addCell(new GridCell(new TextRenderer(new Text("One"))));
        IRenderer value = new TextRenderer(new Text("Two"));
        value.setProperty(Property.GRID_COLUMN_START, 2);
        value.setProperty(Property.GRID_ROW_START, 2);
        GridCell cell = new GridCell(value);
        grid.addCell(cell);
        Assert.assertEquals(grid.getRows().get(0).get(0), grid.getClosestLeftNeighbor(cell));
    }

    @Test
    public void getUniqueCellsTest() {
        Grid grid = new Grid(3, 3, false);
        grid.addCell(new GridCell(new TextRenderer(new Text("One"))));
        IRenderer twoRenderer = new TextRenderer(new Text("Two"));
        twoRenderer.setProperty(Property.GRID_COLUMN_START, 2);
        twoRenderer.setProperty(Property.GRID_COLUMN_END, 4);
        GridCell cell = new GridCell(twoRenderer);
        grid.addCell(cell);
        grid.addCell(new GridCell(new TextRenderer(new Text("Three"))));
        grid.addCell(new GridCell(new TextRenderer(new Text("Four"))));
        Assert.assertEquals(4, grid.getUniqueGridCells(Grid.ROW_ORDER).size());
    }

    @Test
    public void increaseRowHeightTest() {
        Grid grid = new Grid(3, 3, false);
        GridCell cell1 = new GridCell(new TextRenderer(new Text("One")));
        cell1.setLayoutArea(new Rectangle(50.0f, 50.0f));
        GridCell cell2 = new GridCell(new TextRenderer(new Text("Two")));
        cell2.setLayoutArea(new Rectangle(50.0f, 50.0f));
        GridCell cell3 = new GridCell(new TextRenderer(new Text("Three")));
        cell3.setLayoutArea(new Rectangle(50.0f, 50.0f));
        grid.addCell(cell1);
        grid.addCell(cell2);
        grid.addCell(cell3);
        grid.alignRow(0, 100.0f);
        Assert.assertEquals(100.0f, grid.getRows().get(0).get(0).getLayoutArea().getHeight(), 0.00001f);
        Assert.assertEquals(100.0f, grid.getRows().get(0).get(1).getLayoutArea().getHeight(), 0.00001f);
        Assert.assertEquals(100.0f, grid.getRows().get(0).get(2).getLayoutArea().getHeight(), 0.00001f);
    }

    @Test
    public void increaseColumnWidthTest() {
        Grid grid = new Grid(3, 3, false);
        GridCell cell1 = new GridCell(new TextRenderer(new Text("One")));
        cell1.setLayoutArea(new Rectangle(100.0f, 50.0f));
        GridCell cell2 = new GridCell(new TextRenderer(new Text("Two")));
        cell2.setLayoutArea(new Rectangle(30.0f, 50.0f));
        GridCell cell3 = new GridCell(new TextRenderer(new Text("Three")));
        cell3.setLayoutArea(new Rectangle(50.0f, 50.0f));
        GridCell cell4 = new GridCell(new TextRenderer(new Text("Three")));
        cell4.setLayoutArea(new Rectangle(100.0f, 50.0f));
        GridCell cell5 = new GridCell(new TextRenderer(new Text("Three")));
        cell5.setLayoutArea(new Rectangle(50.0f, 50.0f));
        grid.addCell(cell1);
        grid.addCell(cell2);
        grid.addCell(cell3);
        grid.addCell(cell4);
        grid.addCell(cell5);
        grid.alignColumn(1, 150.0f);
        Assert.assertEquals(100.0f, grid.getRows().get(0).get(0).getLayoutArea().getWidth(), 0.00001f);
        Assert.assertEquals(150.0f, grid.getRows().get(0).get(1).getLayoutArea().getWidth(), 0.00001f);
        Assert.assertEquals(150.0f, grid.getRows().get(1).get(1).getLayoutArea().getWidth(), 0.00001f);
        Assert.assertEquals(50.0f, grid.getRows().get(0).get(2).getLayoutArea().getWidth(), 0.00001f);
    }

    @Test
    public void sparsePackingTest() {
        Grid grid = new Grid(3, 3, false);
        GridCell cell1 = new GridCell(new TextRenderer(new Text("One")));
        grid.addCell(cell1);
        IRenderer renderer = new TextRenderer(new Text("Two"));
        renderer.setProperty(Property.GRID_COLUMN_START, 1);
        renderer.setProperty(Property.GRID_COLUMN_END, 6);
        GridCell wideCell = new GridCell(renderer);
        grid.addCell(wideCell);
        GridCell cell3 = new GridCell(new TextRenderer(new Text("Three")));
        GridCell cell4 = new GridCell(new TextRenderer(new Text("Four")));
        GridCell cell5 = new GridCell(new TextRenderer(new Text("Five")));
        GridCell cell6 = new GridCell(new TextRenderer(new Text("Six")));
        grid.addCell(cell3);
        grid.addCell(cell4);
        grid.addCell(cell5);
        grid.addCell(cell6);
        Assert.assertEquals(cell1, grid.getRows().get(0).get(0));
        Assert.assertEquals(wideCell, grid.getRows().get(1).get(0));
        Assert.assertEquals(cell3, grid.getRows().get(2).get(0));
        Assert.assertEquals(cell4, grid.getRows().get(2).get(1));
        Assert.assertEquals(cell5, grid.getRows().get(2).get(2));
        Assert.assertEquals(cell6, grid.getRows().get(2).get(3));
    }

    @Test
    public void densePackingTest() {
        Grid grid = new Grid(3, 3, true);
        GridCell cell1 = new GridCell(new TextRenderer(new Text("One")));
        grid.addCell(cell1);
        IRenderer renderer = new TextRenderer(new Text("Two"));
        renderer.setProperty(Property.GRID_COLUMN_START, 1);
        renderer.setProperty(Property.GRID_COLUMN_END, 6);
        GridCell wideCell = new GridCell(renderer);
        grid.addCell(wideCell);
        GridCell cell3 = new GridCell(new TextRenderer(new Text("Three")));
        GridCell cell4 = new GridCell(new TextRenderer(new Text("Four")));
        GridCell cell5 = new GridCell(new TextRenderer(new Text("Five")));
        GridCell cell6 = new GridCell(new TextRenderer(new Text("Six")));
        grid.addCell(cell3);
        grid.addCell(cell4);
        grid.addCell(cell5);
        grid.addCell(cell6);
        Assert.assertEquals(cell1, grid.getRows().get(0).get(0));
        Assert.assertEquals(cell3, grid.getRows().get(0).get(1));
        Assert.assertEquals(cell4, grid.getRows().get(0).get(2));
        Assert.assertEquals(cell5, grid.getRows().get(0).get(3));
        Assert.assertEquals(cell6, grid.getRows().get(0).get(4));
        Assert.assertEquals(wideCell, grid.getRows().get(1).get(0));
    }
}
