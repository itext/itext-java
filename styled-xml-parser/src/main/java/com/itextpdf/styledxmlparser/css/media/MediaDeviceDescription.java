/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.
    
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS
    
    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/
    
    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.
    
    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.
    
    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.
    
    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.styledxmlparser.css.media;

/**
 * Class that bundles all the values of a media device description.
 */
public class MediaDeviceDescription {

    private static final MediaDeviceDescription DEFAULT = createDefault();

    /** The type. */
    private String type;
    
    /** The bits per component. */
    private int bitsPerComponent = 0;
    
    /** The color index. */
    private int colorIndex = 0;
    
    /** The width in points. */
    private float width;
    
    /** The height in points. */
    private float height;
    
    /** Indicates if the media device is a grid. */
    private boolean isGrid;
    
    /** The scan value. */
    private String scan;
    
    /** The orientation. */
    private String orientation;
    
    /** The the number of bits per pixel on a monochrome (greyscale) device. */
    private int monochrome;
    
    /** The resolution in DPI. */
    // TODO change default units? If so, change CssUtils#parseResolution as well
    private float resolution;

    /**
     * See {@link MediaType} class constants for possible values.
     * @param type a type of the media to use.
     */
    public MediaDeviceDescription(String type) {
        this.type = type;
    }

    /**
     * Creates a new {@link MediaDeviceDescription} instance.
     *
     * @param type the type
     * @param width the width
     * @param height the height
     */
    public MediaDeviceDescription(String type, float width, float height) {
        this(type);
        this.width = width;
        this.height = height;
    }

    /**
     * Creates the default {@link MediaDeviceDescription}.
     *
     * @return the media device description
     */
    public static MediaDeviceDescription createDefault() {
        return new MediaDeviceDescription(MediaType.ALL);
    }

    /**
     * Gets default {@link MediaDeviceDescription} instance.
     * Do not modify any fields of the returned media device description because it may lead
     * to unpredictable results. Use {@link #createDefault()} if you want to modify device description.
     * @return the default media device description
     */
    public static MediaDeviceDescription getDefault() {
        return DEFAULT;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the bits per component.
     *
     * @return the bits per component
     */
    public int getBitsPerComponent() {
        return bitsPerComponent;
    }

    /**
     * Sets the bits per component.
     *
     * @param bitsPerComponent the bits per component
     * @return the media device description
     */
    public MediaDeviceDescription setBitsPerComponent(int bitsPerComponent) {
        this.bitsPerComponent = bitsPerComponent;
        return this;
    }

    /**
     * Gets the color index.
     *
     * @return the color index
     */
    public int getColorIndex() {
        return colorIndex;
    }

    /**
     * Sets the color index.
     *
     * @param colorIndex the color index
     * @return the media device description
     */
    public MediaDeviceDescription setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
        return this;
    }

    /**
     * Gets the width in points.
     *
     * @return the width
     */
    public float getWidth() {
        return width;
    }

    /**
     * Sets the width in points.
     *
     * @param width the width
     * @return the media device description
     */
    public MediaDeviceDescription setWidth(float width) {
        this.width = width;
        return this;
    }

    /**
     * Gets the height in points.
     *
     * @return the height
     */
    public float getHeight() {
        return height;
    }

    /**
     * Sets the height in points.
     *
     * @param height the height
     * @return the media device description
     */
    public MediaDeviceDescription setHeight(float height) {
        this.height = height;
        return this;
    }

    /**
     * Checks if the media device is a grid.
     *
     * @return true, if is grid
     */
    public boolean isGrid() {
        return isGrid;
    }

    /**
     * Sets the grid value.
     *
     * @param grid the grid value
     * @return the media device description
     */
    public MediaDeviceDescription setGrid(boolean grid) {
        isGrid = grid;
        return this;
    }

    /**
     * Gets the scan value.
     *
     * @return the scan value
     */
    public String getScan() {
        return scan;
    }

    /**
     * Sets the scan value.
     *
     * @param scan the scan value
     * @return the media device description
     */
    public MediaDeviceDescription setScan(String scan) {
        this.scan = scan;
        return this;
    }

    /**
     * Gets the orientation.
     *
     * @return the orientation
     */
    public String getOrientation() {
        return orientation;
    }

    /**
     * Sets the orientation.
     *
     * @param orientation the orientation
     * @return the media device description
     */
    public MediaDeviceDescription setOrientation(String orientation) {
        this.orientation = orientation;
        return this;
    }

    /**
     * Gets the number of bits per pixel on a monochrome (greyscale) device.
     *
     * @return the number of bits per pixel on a monochrome (greyscale) device
     */
    public int getMonochrome() {
        return monochrome;
    }

    /**
     * Sets the number of bits per pixel on a monochrome (greyscale) device.
     *
     * @param monochrome the number of bits per pixel on a monochrome (greyscale) device
     * @return the media device description
     */
    public MediaDeviceDescription setMonochrome(int monochrome) {
        this.monochrome = monochrome;
        return this;
    }

    /**
     * Gets the resolution in DPI.
     *
     * @return the resolution
     */
    public float getResolution() {
        return resolution;
    }

    /**
     * Sets the resolution in DPI.
     *
     * @param resolution the resolution
     * @return the media device description
     */
    public MediaDeviceDescription setResolution(float resolution) {
        this.resolution = resolution;
        return this;
    }
}
