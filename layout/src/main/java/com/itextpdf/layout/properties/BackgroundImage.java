/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
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
package com.itextpdf.layout.properties;

import com.itextpdf.kernel.colors.gradients.AbstractLinearGradientBuilder;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.layout.properties.BackgroundRepeat.BackgroundRepeatValue;

/**
 * Class to hold background-image property.
 */
public class BackgroundImage {

    private static final BlendMode DEFAULT_BLEND_MODE = BlendMode.NORMAL;

    protected PdfXObject image;

    protected AbstractLinearGradientBuilder linearGradientBuilder;

    private BlendMode blendMode = DEFAULT_BLEND_MODE;

    private final BackgroundRepeat repeat;

    private final BackgroundPosition position;

    private final BackgroundSize backgroundSize;

    private final BackgroundBox backgroundClip;

    private final BackgroundBox backgroundOrigin;

    /**
     * Creates a copy of passed {@link BackgroundImage} instance.
     *
     * @param backgroundImage {@link BackgroundImage} for cloning
     */
    public BackgroundImage(BackgroundImage backgroundImage) {
        this(backgroundImage.getImage() == null ? (PdfXObject) backgroundImage.getForm() : backgroundImage.getImage(),
                backgroundImage.getRepeat(),
                backgroundImage.getBackgroundPosition(),
                backgroundImage.getBackgroundSize(),
                backgroundImage.getLinearGradientBuilder(),
                backgroundImage.getBlendMode(),
                backgroundImage.getBackgroundClip(),
                backgroundImage.getBackgroundOrigin());
    }

    /**
     * Gets initial image if it is instanceof {@link PdfImageXObject}, otherwise returns null.
     *
     * @return {@link PdfImageXObject}
     */
    public PdfImageXObject getImage() {
        return image instanceof PdfImageXObject ? (PdfImageXObject) image : null;
    }

    /**
     * Gets initial image if it is instanceof {@link PdfFormXObject}, otherwise returns null.
     *
     * @return {@link PdfFormXObject}
     */
    public PdfFormXObject getForm() {
        return image instanceof PdfFormXObject ? (PdfFormXObject) image : null;
    }

    /**
     * Creates a new {@link BackgroundImage} instance.
     *
     * @param image                 background-image property. {@link PdfXObject} instance.
     * @param repeat                background-repeat property. {@link BackgroundRepeat} instance.
     * @param position              background-position property. {@link BackgroundPosition} instance.
     * @param backgroundSize        background-size property. {@link BackgroundSize} instance.
     * @param linearGradientBuilder background-image property. {@link AbstractLinearGradientBuilder} instance.
     * @param blendMode             the image's blend mode. {@link BlendMode} instance.
     * @param clip                  background-clip property. {@link BackgroundBox} instance.
     * @param origin                background-origin property. {@link BackgroundBox} instance.
     */
    private BackgroundImage(PdfXObject image, BackgroundRepeat repeat, BackgroundPosition position,
            BackgroundSize backgroundSize, AbstractLinearGradientBuilder linearGradientBuilder,
            BlendMode blendMode, BackgroundBox clip, BackgroundBox origin) {
        this.image = image;
        this.repeat = repeat;
        this.position = position;
        this.backgroundSize = backgroundSize;
        this.linearGradientBuilder = linearGradientBuilder;
        if (blendMode != null) {
            this.blendMode = blendMode;
        }
        this.backgroundClip = clip;
        this.backgroundOrigin = origin;
    }

    /**
     * Gets background-position.
     *
     * @return {@link BackgroundPosition}
     */
    public BackgroundPosition getBackgroundPosition() {
        return position;
    }

    /**
     * Gets linearGradientBuilder.
     *
     * @return {@link AbstractLinearGradientBuilder}
     */
    public AbstractLinearGradientBuilder getLinearGradientBuilder() {
        return this.linearGradientBuilder;
    }

    /**
     * Returns is background specified.
     *
     * @return {@code true} if background is specified, otherwise false
     */
    public boolean isBackgroundSpecified() {
        return image instanceof PdfFormXObject || image instanceof PdfImageXObject || linearGradientBuilder != null;
    }

    /**
     * Gets the background size property.
     *
     * @return {@link BackgroundSize} instance
     */
    public BackgroundSize getBackgroundSize() {
        return backgroundSize;
    }

    /**
     * Gets initial image width.
     *
     * @return the initial image width
     */
    public float getImageWidth() {
        return (float) image.getWidth();
    }

    /**
     * Gets initial image height.
     *
     * @return the initial image height
     */
    public float getImageHeight() {
        return (float) image.getHeight();
    }

    /**
     * Gets image {@link BackgroundRepeat} instance.
     *
     * @return the image background repeat
     */
    public BackgroundRepeat getRepeat() {
        return repeat;
    }

    /**
     * Get the image's blend mode.
     *
     * @return the {@link BlendMode} representation of the image's blend mode
     */
    public BlendMode getBlendMode() {
        return blendMode;
    }

    /**
     * Gets background-clip.
     *
     * @return {@link BackgroundBox}
     */
    public BackgroundBox getBackgroundClip() {
        return backgroundClip;
    }

    /**
     * Gets background-origin.
     *
     * @return {@link BackgroundBox}
     */
    public BackgroundBox getBackgroundOrigin() {
        return backgroundOrigin;
    }

    /**
     * {@link BackgroundImage} builder class.
     */
    public static class Builder {

        private PdfXObject image;
        private AbstractLinearGradientBuilder linearGradientBuilder;
        private BackgroundPosition position = new BackgroundPosition();
        private BackgroundRepeat repeat = new BackgroundRepeat();
        private BlendMode blendMode = DEFAULT_BLEND_MODE;
        private BackgroundSize backgroundSize = new BackgroundSize();
        private BackgroundBox clip = BackgroundBox.BORDER_BOX;
        private BackgroundBox origin = BackgroundBox.PADDING_BOX;

        /**
         * Creates a new {@link Builder} instance.
         */
        public Builder() {
        }

        /**
         * Sets image.
         * <p>
         * Makes linearGradientBuilder null as far as we can't have them both.
         *
         * @param image {@link PdfXObject} to be set.
         * @return this {@link Builder}.
         */
        public Builder setImage(PdfXObject image) {
            this.image = image;
            this.linearGradientBuilder = null;
            return this;
        }

        /**
         * Sets linearGradientBuilder.
         * <p>
         * Makes image null as far as we can't have them both. It also makes background-repeat: no-repeat.
         *
         * @param linearGradientBuilder {@link AbstractLinearGradientBuilder} to be set.
         * @return this {@link Builder}.
         */
        public Builder setLinearGradientBuilder(AbstractLinearGradientBuilder linearGradientBuilder) {
            this.linearGradientBuilder = linearGradientBuilder;
            this.repeat = new BackgroundRepeat(BackgroundRepeatValue.NO_REPEAT);
            this.image = null;
            return this;
        }

        /**
         * Sets background-repeat.
         *
         * @param repeat {@link BackgroundRepeat} to be set.
         * @return this {@link Builder}.
         */
        public Builder setBackgroundRepeat(BackgroundRepeat repeat) {
            this.repeat = repeat;
            return this;
        }

        /**
         * Sets background-position.
         *
         * @param position {@link BackgroundPosition} to be set.
         * @return this {@link Builder}.
         */
        public Builder setBackgroundPosition(BackgroundPosition position) {
            this.position = position;
            return this;
        }

        /**
         * Set the image's blend mode.
         *
         * @param blendMode {@link BlendMode} to be set.
         * @return this {@link Builder}.
         */
        public Builder setBackgroundBlendMode(BlendMode blendMode) {
            if (blendMode != null) {
                this.blendMode = blendMode;
            }
            return this;
        }

        /**
         * Set the image's backgroundSize.
         *
         * @param backgroundSize {@link BackgroundSize} to be set.
         * @return this {@link Builder}.
         */
        public Builder setBackgroundSize(BackgroundSize backgroundSize) {
            if (backgroundSize != null) {
                this.backgroundSize = backgroundSize;
            }
            return this;
        }

        /**
         * Sets background-clip.
         *
         * @param clip {@link BackgroundBox} to be set.
         * @return this {@link Builder}.
         */
        public Builder setBackgroundClip(BackgroundBox clip) {
            this.clip = clip;
            return this;
        }

        /**
         * Sets background-origin.
         *
         * @param origin {@link BackgroundBox} to be set.
         * @return this {@link Builder}.
         */
        public Builder setBackgroundOrigin(BackgroundBox origin) {
            this.origin = origin;
            return this;
        }

        /**
         * Builds new {@link BackgroundImage} using set fields.
         *
         * @return new {@link BackgroundImage}.
         */
        public BackgroundImage build() {
            return new BackgroundImage(image, repeat, position, backgroundSize, linearGradientBuilder, blendMode, clip,
                    origin);
        }
    }
}
