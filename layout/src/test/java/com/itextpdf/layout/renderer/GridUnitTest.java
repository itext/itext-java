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
import com.itextpdf.layout.properties.grid.GridFlow;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class GridUnitTest extends ExtendedITextTest {

    @Test
    public void getUniqueCellsTest() {
        IRenderer twoRenderer = new TextRenderer(new Text("Two"));
        twoRenderer.setProperty(Property.GRID_COLUMN_START, 2);
        twoRenderer.setProperty(Property.GRID_COLUMN_END, 4);
        Grid grid = Grid.Builder.forItems(Arrays.asList(
                new TextRenderer(new Text("One")),
                twoRenderer,
                new TextRenderer(new Text("Three")),
                new TextRenderer(new Text("Four"))
        )).columns(3).rows(3).flow(GridFlow.ROW).build();
        Assertions.assertEquals(4, grid.getUniqueGridCells(Grid.GridOrder.ROW).size());
    }

    @Test
    public void sparsePackingTest() {
        IRenderer cell1 = new TextRenderer(new Text("One"));
        IRenderer wideCell = new TextRenderer(new Text("Two"));
        wideCell.setProperty(Property.GRID_COLUMN_START, 2);
        wideCell.setProperty(Property.GRID_COLUMN_END, 4);
        IRenderer cell3 = new TextRenderer(new Text("Three"));
        IRenderer cell4 = new TextRenderer(new Text("Four"));
        IRenderer cell5 = new TextRenderer(new Text("Five"));
        IRenderer cell6 = new TextRenderer(new Text("Six"));

        Grid grid = Grid.Builder.forItems(Arrays.asList(
                cell1,
                wideCell,
                cell3,
                cell4,
                cell5,
                cell6
        )).columns(3).rows(3).flow(GridFlow.ROW).build();
        Assertions.assertEquals(cell1, grid.getRows()[0][0].getValue());
        Assertions.assertEquals(wideCell, grid.getRows()[0][1].getValue());
        Assertions.assertEquals(wideCell, grid.getRows()[0][2].getValue());
        Assertions.assertEquals(cell3, grid.getRows()[1][0].getValue());
        Assertions.assertEquals(cell4, grid.getRows()[1][1].getValue());
        Assertions.assertEquals(cell5, grid.getRows()[1][2].getValue());
        Assertions.assertEquals(cell6, grid.getRows()[2][0].getValue());
    }

    @Test
    public void densePackingTest() {
        IRenderer cell1 = new TextRenderer(new Text("One"));
        IRenderer wideCell = new TextRenderer(new Text("Two"));
        wideCell.setProperty(Property.GRID_COLUMN_START, 2);
        wideCell.setProperty(Property.GRID_COLUMN_END, 4);
        IRenderer cell3 = new TextRenderer(new Text("Three"));
        IRenderer cell4 = new TextRenderer(new Text("Four"));
        IRenderer cell5 = new TextRenderer(new Text("Five"));
        IRenderer cell6 = new TextRenderer(new Text("Six"));

        Grid grid = Grid.Builder.forItems(Arrays.asList(
                cell1,
                wideCell,
                cell3,
                cell4,
                cell5,
                cell6
        )).columns(3).rows(3).flow(GridFlow.ROW_DENSE).build();
        Assertions.assertEquals(cell1, grid.getRows()[0][0].getValue());
        Assertions.assertEquals(wideCell, grid.getRows()[0][1].getValue());
        Assertions.assertEquals(wideCell, grid.getRows()[0][2].getValue());
        Assertions.assertEquals(cell3, grid.getRows()[1][0].getValue());
        Assertions.assertEquals(cell4, grid.getRows()[1][1].getValue());
        Assertions.assertEquals(cell5, grid.getRows()[1][2].getValue());
        Assertions.assertEquals(cell6, grid.getRows()[2][0].getValue());
    }

    @Test
    public void columnPackingTest() {
        IRenderer cell1 = new TextRenderer(new Text("One"));
        IRenderer cell2 = new TextRenderer(new Text("Two"));
        IRenderer cell3 = new TextRenderer(new Text("Three"));
        IRenderer cell4 = new TextRenderer(new Text("Four"));
        IRenderer cell5 = new TextRenderer(new Text("Five"));
        IRenderer cell6 = new TextRenderer(new Text("Six"));
        Grid grid = Grid.Builder.forItems(Arrays.asList(
                cell1,
                cell2,
                cell3,
                cell4,
                cell5,
                cell6
        )).columns(3).rows(3).flow(GridFlow.COLUMN).build();
        Assertions.assertEquals(cell1, grid.getRows()[0][0].getValue());
        Assertions.assertEquals(cell2, grid.getRows()[1][0].getValue());
        Assertions.assertEquals(cell3, grid.getRows()[2][0].getValue());
        Assertions.assertEquals(cell4, grid.getRows()[0][1].getValue());
        Assertions.assertEquals(cell5, grid.getRows()[1][1].getValue());
        Assertions.assertEquals(cell6, grid.getRows()[2][1].getValue());
    }

    @Test
    public void columnWithFixedWideCellPackingTest() {
        IRenderer cell1 = new TextRenderer(new Text("One"));
        IRenderer wideCell = new TextRenderer(new Text("Two"));
        wideCell.setProperty(Property.GRID_COLUMN_START, 1);
        wideCell.setProperty(Property.GRID_COLUMN_END, 3);
        IRenderer cell3 = new TextRenderer(new Text("Three"));
        IRenderer cell4 = new TextRenderer(new Text("Four"));
        IRenderer cell5 = new TextRenderer(new Text("Five"));
        IRenderer cell6 = new TextRenderer(new Text("Six"));

        Grid grid = Grid.Builder.forItems(Arrays.asList(
                cell1,
                wideCell,
                cell3,
                cell4,
                cell5,
                cell6
        )).columns(3).rows(3).flow(GridFlow.COLUMN).build();
        Assertions.assertEquals(wideCell, grid.getRows()[0][0].getValue());
        Assertions.assertEquals(cell1, grid.getRows()[1][0].getValue());
        Assertions.assertEquals(cell4, grid.getRows()[1][1].getValue());
        Assertions.assertEquals(cell3, grid.getRows()[2][0].getValue());
        Assertions.assertEquals(wideCell, grid.getRows()[0][1].getValue());
        Assertions.assertEquals(cell5, grid.getRows()[2][1].getValue());
        Assertions.assertEquals(cell6, grid.getRows()[0][2].getValue());
    }

    @Test
    public void columnWithFixedTallCellPackingTest() {
        IRenderer cell1 = new TextRenderer(new Text("One"));
        IRenderer tallCell = new TextRenderer(new Text("Two"));
        tallCell.setProperty(Property.GRID_ROW_START, 2);
        tallCell.setProperty(Property.GRID_ROW_END, 4);
        IRenderer cell3 = new TextRenderer(new Text("Three"));
        IRenderer cell4 = new TextRenderer(new Text("Four"));
        IRenderer cell5 = new TextRenderer(new Text("Five"));
        IRenderer cell6 = new TextRenderer(new Text("Six"));

        Grid grid = Grid.Builder.forItems(Arrays.asList(
                cell1,
                tallCell,
                cell3,
                cell4,
                cell5,
                cell6
        )).columns(3).rows(3).flow(GridFlow.COLUMN).build();
        Assertions.assertEquals(cell1, grid.getRows()[0][0].getValue());
        Assertions.assertEquals(tallCell, grid.getRows()[1][0].getValue());
        Assertions.assertEquals(tallCell, grid.getRows()[2][0].getValue());
        Assertions.assertEquals(cell3, grid.getRows()[0][1].getValue());
        Assertions.assertEquals(cell4, grid.getRows()[1][1].getValue());
        Assertions.assertEquals(cell5, grid.getRows()[2][1].getValue());
        Assertions.assertEquals(cell6, grid.getRows()[0][2].getValue());
    }
}
