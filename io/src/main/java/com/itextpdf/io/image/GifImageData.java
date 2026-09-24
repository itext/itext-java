/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.io.image;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.util.UrlUtil;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the logical screen properties and decoded frames of a GIF image.
 */
public class GifImageData {

    private float logicalHeight;
    private float logicalWidth;
    private List<ImageData> frames = new ArrayList<>();
    private byte[] data;
    private URL url;

    /**
     * Creates GIF data to be loaded from a URL.
     *
     * @param url source URL, not {@code null}
     */
    protected GifImageData(URL url) {
        this.url = url;
    }

    /**
     * Creates GIF data from encoded bytes.
     *
     * @param data encoded GIF bytes; the array is retained
     */
    protected GifImageData(byte[] data) {
        this.data = data;
    }

    /**
     * Gets the logical screen height.
     *
     * @return height in pixels
     */
    public float getLogicalHeight() {
        return logicalHeight;
    }

    /**
     * Sets the logical screen height.
     *
     * @param logicalHeight height in pixels
     */
    public void setLogicalHeight(float logicalHeight) {
        this.logicalHeight = logicalHeight;
    }

    /**
     * Gets the logical screen width.
     *
     * @return width in pixels
     */
    public float getLogicalWidth() {
        return logicalWidth;
    }

    /**
     * Sets the logical screen width.
     *
     * @param logicalWidth width in pixels
     */
    public void setLogicalWidth(float logicalWidth) {
        this.logicalWidth = logicalWidth;
    }

    /**
     * Gets the decoded GIF frames.
     *
     * @return list of frames in source order
     */
    public List<ImageData> getFrames() {
        return frames;
    }

    /**
     * Gets the encoded GIF bytes.
     *
     * @return retained bytes, or {@code null} until loaded
     */
    protected byte[] getData() {
        return data;
    }

    /**
     * Gets the source URL.
     *
     * @return source URL, or {@code null} when data was supplied directly
     */
    protected URL getUrl() {
        return url;
    }

    /**
     * Appends a decoded frame.
     *
     * @param frame decoded frame to append
     */
    protected void addFrame(ImageData frame) {
        frames.add(frame);
    }

    /**
     * Load data by URL. url must be not null.
     * Note, this method doesn't check if data or url is null.
     * @throws java.io.IOException
     */
    void loadData() throws java.io.IOException {
        InputStream input = null;
        try {
            input = UrlUtil.openStream(url);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            StreamUtil.transferBytes(UrlUtil.openStream(url), stream);
            data = stream.toByteArray();
        } finally {
            if (input != null) {
                input.close();
            }
        }


    }
}
