package com.itextpdf.layout.renderer;

import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class GridCellUnitTest extends ExtendedITextTest {
    @Test
    public void cellWithOnlyGridRowStartTest() {
        IRenderer renderer = new TextRenderer(new Text("test"));
        renderer.setProperty(Property.GRID_ROW_START, 3);
        GridCell cell = new GridCell(renderer);
        Assert.assertEquals(2, cell.getRowStart());
        Assert.assertEquals(3, cell.getRowEnd());
    }

    @Test
    public void cellWithOnlyGridRowEndTest() {
        IRenderer renderer = new TextRenderer(new Text("test"));
        renderer.setProperty(Property.GRID_ROW_END, 5);
        GridCell cell = new GridCell(renderer);
        Assert.assertEquals(3, cell.getRowStart());
        Assert.assertEquals(4, cell.getRowEnd());
    }

    @Test
    public void cellWithGridRowStartAndEndTest() {
        IRenderer renderer = new TextRenderer(new Text("test"));
        renderer.setProperty(Property.GRID_ROW_START, 2);
        renderer.setProperty(Property.GRID_ROW_END, 4);
        GridCell cell = new GridCell(renderer);
        Assert.assertEquals(1, cell.getRowStart());
        Assert.assertEquals(3, cell.getRowEnd());
    }

    @Test
    public void cellWithOnlyGridColumnStartTest() {
        IRenderer renderer = new TextRenderer(new Text("test"));
        renderer.setProperty(Property.GRID_COLUMN_START, 3);
        GridCell cell = new GridCell(renderer);
        Assert.assertEquals(2, cell.getColumnStart());
        Assert.assertEquals(3, cell.getColumnEnd());
    }

    @Test
    public void cellWithOnlyGridColumnEndTest() {
        IRenderer renderer = new TextRenderer(new Text("test"));
        renderer.setProperty(Property.GRID_COLUMN_END, 8);
        GridCell cell = new GridCell(renderer);
        Assert.assertEquals(6, cell.getColumnStart());
        Assert.assertEquals(7, cell.getColumnEnd());
    }

    @Test
    public void cellWithGridColumnStartAndEndTest() {
        IRenderer renderer = new TextRenderer(new Text("test"));
        renderer.setProperty(Property.GRID_COLUMN_START, 4);
        renderer.setProperty(Property.GRID_COLUMN_END, 7);
        GridCell cell = new GridCell(renderer);
        Assert.assertEquals(3, cell.getColumnStart());
        Assert.assertEquals(6, cell.getColumnEnd());
    }

    @Test
    public void cellWithReversedColumnStartAndEndTest() {
        IRenderer renderer = new TextRenderer(new Text("test"));
        renderer.setProperty(Property.GRID_COLUMN_START, 7);
        renderer.setProperty(Property.GRID_COLUMN_END, 4);
        GridCell cell = new GridCell(renderer);
        Assert.assertEquals(3, cell.getColumnStart());
        Assert.assertEquals(6, cell.getColumnEnd());
    }

    @Test
    public void cellWithReversedRowStartAndEndTest() {
        IRenderer renderer = new TextRenderer(new Text("test"));
        renderer.setProperty(Property.GRID_ROW_START, 4);
        renderer.setProperty(Property.GRID_ROW_END, 2);
        GridCell cell = new GridCell(renderer);
        Assert.assertEquals(1, cell.getRowStart());
        Assert.assertEquals(3, cell.getRowEnd());
    }
}
