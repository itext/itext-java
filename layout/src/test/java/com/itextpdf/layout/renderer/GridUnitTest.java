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

import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.GridFlow;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.Grid.GridOrder;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class GridUnitTest extends ExtendedITextTest {

    @Test
    public void getUniqueCellsTest() {
        Grid grid = new Grid(3, 3,  GridFlow.ROW);
        grid.addCell(new GridCell(new TextRenderer(new Text("One"))));
        IRenderer twoRenderer = new TextRenderer(new Text("Two"));
        twoRenderer.setProperty(Property.GRID_COLUMN_START, 2);
        twoRenderer.setProperty(Property.GRID_COLUMN_END, 4);
        GridCell cell = new GridCell(twoRenderer);
        grid.addCell(cell);
        grid.addCell(new GridCell(new TextRenderer(new Text("Three"))));
        grid.addCell(new GridCell(new TextRenderer(new Text("Four"))));
        Assert.assertEquals(4, grid.getUniqueGridCells(Grid.GridOrder.ROW).size());
    }

    @Test
    public void getUniqueCellsInColumnTest() {
        Grid grid = new Grid(3, 3,  GridFlow.ROW);
        grid.addCell(new GridCell(new TextRenderer(new Text("One"))));
        IRenderer twoRenderer = new TextRenderer(new Text("Two"));
        twoRenderer.setProperty(Property.GRID_ROW_START, 2);
        twoRenderer.setProperty(Property.GRID_ROW_END, 4);
        GridCell cell = new GridCell(twoRenderer);
        grid.addCell(cell);
        grid.addCell(new GridCell(new TextRenderer(new Text("Three"))));
        grid.addCell(new GridCell(new TextRenderer(new Text("Four"))));
        Assert.assertEquals(1, grid.getUniqueCellsInTrack(GridOrder.COLUMN, 1).size());
    }

    @Test
    public void invalidColumnForGetColCellsTest() {
        Grid grid = new Grid(3, 3,  GridFlow.ROW);
        Assert.assertThrows(IndexOutOfBoundsException.class, () -> grid.getUniqueCellsInTrack(GridOrder.COLUMN, 4));
        Assert.assertThrows(IndexOutOfBoundsException.class, () -> grid.getUniqueCellsInTrack(GridOrder.COLUMN, -1));
        AssertUtil.doesNotThrow(() -> grid.getUniqueCellsInTrack(GridOrder.COLUMN, 2));
    }

    @Test
    public void getUniqueCellsInRowTest() {
        Grid grid = new Grid(3, 3,  GridFlow.ROW);
        grid.addCell(new GridCell(new TextRenderer(new Text("One"))));
        IRenderer twoRenderer = new TextRenderer(new Text("Two"));
        twoRenderer.setProperty(Property.GRID_COLUMN_START, 2);
        twoRenderer.setProperty(Property.GRID_COLUMN_END, 4);
        GridCell cell = new GridCell(twoRenderer);
        grid.addCell(cell);
        grid.addCell(new GridCell(new TextRenderer(new Text("Three"))));
        grid.addCell(new GridCell(new TextRenderer(new Text("Four"))));
        Assert.assertEquals(2, grid.getUniqueCellsInTrack(GridOrder.ROW, 0).size());
    }

    @Test
    public void invalidRowForGetRowCellsTest() {
        Grid grid = new Grid(3, 3,  GridFlow.ROW);
        Assert.assertThrows(IndexOutOfBoundsException.class, () -> grid.getUniqueCellsInTrack(GridOrder.ROW, 4));
        Assert.assertThrows(IndexOutOfBoundsException.class, () -> grid.getUniqueCellsInTrack(GridOrder.ROW, -1));
        AssertUtil.doesNotThrow(() -> grid.getUniqueCellsInTrack(GridOrder.ROW, 2));
    }

    @Test
    public void sparsePackingTest() {
        Grid grid = new Grid(3, 3,  GridFlow.ROW);
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
        Assert.assertEquals(cell1, grid.getRows()[0][0]);
        Assert.assertEquals(wideCell, grid.getRows()[1][0]);
        Assert.assertEquals(cell3, grid.getRows()[2][0]);
        Assert.assertEquals(cell4, grid.getRows()[2][1]);
        Assert.assertEquals(cell5, grid.getRows()[2][2]);
        Assert.assertEquals(cell6, grid.getRows()[2][3]);
    }

    @Test
    public void densePackingTest() {
        Grid grid = new Grid(3, 3, GridFlow.ROW_DENSE);
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
        Assert.assertEquals(cell1, grid.getRows()[0][0]);
        Assert.assertEquals(cell3, grid.getRows()[0][1]);
        Assert.assertEquals(cell4, grid.getRows()[0][2]);
        Assert.assertEquals(cell5, grid.getRows()[0][3]);
        Assert.assertEquals(cell6, grid.getRows()[0][4]);
        Assert.assertEquals(wideCell, grid.getRows()[1][0]);
    }

    @Test
    public void columnPackingTest() {
        Grid grid = new Grid(3, 3,  GridFlow.COLUMN);
        GridCell cell1 = new GridCell(new TextRenderer(new Text("One")));
        GridCell cell2 = new GridCell(new TextRenderer(new Text("Two")));
        GridCell cell3 = new GridCell(new TextRenderer(new Text("Three")));
        GridCell cell4 = new GridCell(new TextRenderer(new Text("Four")));
        GridCell cell5 = new GridCell(new TextRenderer(new Text("Five")));
        GridCell cell6 = new GridCell(new TextRenderer(new Text("Six")));
        grid.addCell(cell1);
        grid.addCell(cell2);
        grid.addCell(cell3);
        grid.addCell(cell4);
        grid.addCell(cell5);
        grid.addCell(cell6);
        Assert.assertEquals(cell1, grid.getRows()[0][0]);
        Assert.assertEquals(cell2, grid.getRows()[1][0]);
        Assert.assertEquals(cell3, grid.getRows()[2][0]);
        Assert.assertEquals(cell4, grid.getRows()[0][1]);
        Assert.assertEquals(cell5, grid.getRows()[1][1]);
        Assert.assertEquals(cell6, grid.getRows()[2][1]);
    }

    @Test
    public void columnWithFixedWideCellPackingTest() {
        Grid grid = new Grid(3, 3,  GridFlow.COLUMN);
        GridCell cell1 = new GridCell(new TextRenderer(new Text("One")));
        IRenderer renderer = new TextRenderer(new Text("Two"));
        renderer.setProperty(Property.GRID_COLUMN_START, 1);
        renderer.setProperty(Property.GRID_COLUMN_END, 3);
        GridCell wideCell = new GridCell(renderer);
        GridCell cell3 = new GridCell(new TextRenderer(new Text("Three")));
        GridCell cell4 = new GridCell(new TextRenderer(new Text("Four")));
        GridCell cell5 = new GridCell(new TextRenderer(new Text("Five")));
        GridCell cell6 = new GridCell(new TextRenderer(new Text("Six")));
        grid.addCell(cell1);
        grid.addCell(wideCell);
        grid.addCell(cell3);
        grid.addCell(cell4);
        grid.addCell(cell5);
        grid.addCell(cell6);
        Assert.assertEquals(cell1, grid.getRows()[0][0]);
        Assert.assertEquals(wideCell, grid.getRows()[1][0]);
        Assert.assertEquals(wideCell, grid.getRows()[1][1]);
        Assert.assertEquals(cell3, grid.getRows()[2][0]);
        Assert.assertEquals(cell4, grid.getRows()[0][1]);
        Assert.assertEquals(cell5, grid.getRows()[2][1]);
        Assert.assertEquals(cell6, grid.getRows()[0][2]);
    }

    @Test
    public void columnWithFixedTallCellPackingTest() {
        Grid grid = new Grid(3, 3,  GridFlow.COLUMN);
        GridCell cell1 = new GridCell(new TextRenderer(new Text("One")));
        IRenderer renderer = new TextRenderer(new Text("Two"));
        renderer.setProperty(Property.GRID_ROW_START, 2);
        renderer.setProperty(Property.GRID_ROW_END, 4);
        GridCell tallCell = new GridCell(renderer);
        GridCell cell3 = new GridCell(new TextRenderer(new Text("Three")));
        GridCell cell4 = new GridCell(new TextRenderer(new Text("Four")));
        GridCell cell5 = new GridCell(new TextRenderer(new Text("Five")));
        GridCell cell6 = new GridCell(new TextRenderer(new Text("Six")));
        grid.addCell(cell1);
        grid.addCell(tallCell);
        grid.addCell(cell3);
        grid.addCell(cell4);
        grid.addCell(cell5);
        grid.addCell(cell6);
        Assert.assertEquals(cell1, grid.getRows()[0][0]);
        Assert.assertEquals(tallCell, grid.getRows()[1][0]);
        Assert.assertEquals(tallCell, grid.getRows()[2][0]);
        Assert.assertEquals(cell3, grid.getRows()[0][1]);
        Assert.assertEquals(cell4, grid.getRows()[1][1]);
        Assert.assertEquals(cell5, grid.getRows()[2][1]);
        Assert.assertEquals(cell6, grid.getRows()[0][2]);
    }
}
