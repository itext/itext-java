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
package com.itextpdf.io.image;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.util.UrlUtil;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GifImageData {

    private float logicalHeight;
    private float logicalWidth;
    private List<ImageData> frames = new ArrayList<>();
    private byte[] data;
    private URL url;

    protected GifImageData(URL url) {
        this.url = url;
    }

    protected GifImageData(byte[] data) {
        this.data = data;
    }

    public float getLogicalHeight() {
        return logicalHeight;
    }

    public void setLogicalHeight(float logicalHeight) {
        this.logicalHeight = logicalHeight;
    }

    public float getLogicalWidth() {
        return logicalWidth;
    }

    public void setLogicalWidth(float logicalWidth) {
        this.logicalWidth = logicalWidth;
    }

    public List<ImageData> getFrames() {
        return frames;
    }

    protected byte[] getData() {
        return data;
    }

    protected URL getUrl() {
        return url;
    }

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
