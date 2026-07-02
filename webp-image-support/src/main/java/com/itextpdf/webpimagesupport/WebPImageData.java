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
package com.itextpdf.webpimagesupport;

import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.image.ImageType;

import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

/**
 * This class is a wrapper around WebP image format.
 */
public class WebPImageData extends ImageData {

    /**
     * Creates an {@link ImageData} instance from a WebP image raw bytes.
     *
     * @param bytes raw bytes to create WebP image data from
     */
    public WebPImageData(byte[] bytes) {
        super(bytes, ImageType.WEBP);
        processImage();
    }

    /**
     * Creates an {@link ImageData} instance from a WebP image URL.
     *
     * @param url URL to create WebP image data from
     */
    public WebPImageData(URL url) {
        super(url, ImageType.WEBP);
        processImage();
    }

    /**
     * Processes the ImageData as a WebP image.
     */
    private void processImage() {
        try {
            if (this.getData() == null) {
                this.loadData();
            }

            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(this.getData()));
            if (bufferedImage == null) {
                throw new com.itextpdf.io.exceptions.IOException(IoExceptionMessageConstant.WEBP_IMAGE_EXCEPTION);
            }

            BufferedImage abgr = new BufferedImage(
                    bufferedImage.getWidth(),
                    bufferedImage.getHeight(),
                    BufferedImage.TYPE_4BYTE_ABGR
            );

            Graphics2D g = abgr.createGraphics();
            g.setComposite(AlphaComposite.Src);
            // Whatever image type was there originally, we redraw it as ABGR
            g.drawImage(bufferedImage, 0, 0, null);
            g.dispose();

            byte[] abgrRaster = ((DataBufferByte) abgr.getRaster().getDataBuffer()).getData();
            byte[] alpha = new byte[abgrRaster.length / 4];
            this.data = new  byte[abgrRaster.length / 4 * 3];
            // Out of ABGR raster we create RGB raster and alpha array
            for (int i = 0, j = 0, t = 0; i < abgrRaster.length; i += 4, t++) {
                this.data[j] = abgrRaster[i + 3];
                ++j;
                this.data[j] = abgrRaster[i + 2];
                ++j;
                this.data[j] = abgrRaster[i + 1];
                ++j;
                alpha[t] = abgrRaster[i];
            }

            this.imageSize = abgrRaster.length;
            this.setHeight(bufferedImage.getHeight());
            this.setWidth(bufferedImage.getWidth());
            this.setBpc(bufferedImage.getColorModel().getComponentSize(0));
            this.setColorEncodingComponentsNumber(3);
            if (bufferedImage.getColorModel().getTransparency() != Transparency.OPAQUE) {
                ImageData mask = ImageDataFactory.create(
                        bufferedImage.getWidth(), bufferedImage.getHeight(), 1, 8, alpha, null);
                mask.makeMask();
                this.setImageMask(mask);
            }
        } catch (IOException e) {
            throw new com.itextpdf.io.exceptions.IOException(IoExceptionMessageConstant.WEBP_IMAGE_EXCEPTION, e);
        }
    }
}
