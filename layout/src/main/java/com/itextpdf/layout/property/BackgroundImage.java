/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.layout.property;

import com.itextpdf.kernel.colors.gradients.AbstractLinearGradientBuilder;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.layout.property.BackgroundRepeat.BackgroundRepeatValue;

public class BackgroundImage {

    private static final BlendMode DEFAULT_BLEND_MODE = BlendMode.NORMAL;

    protected PdfXObject image;

    /**
     * Whether the background-repeat value is not {@link BackgroundRepeatValue#NO_REPEAT} for X axis.
     *
     * @deprecated replace this field with {@link BackgroundRepeat} instance
     */
    @Deprecated
    protected boolean repeatX;

    /**
     * Whether the background-repeat value is not {@link BackgroundRepeatValue#NO_REPEAT} for Y axis.
     *
     * @deprecated replace this field with {@link BackgroundRepeat} instance
     */
    @Deprecated
    protected boolean repeatY;

    protected AbstractLinearGradientBuilder linearGradientBuilder;

    private BlendMode blendMode = DEFAULT_BLEND_MODE;

    private BackgroundRepeat repeat;

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
        repeatX = backgroundImage.isRepeatX();
        repeatY = backgroundImage.isRepeatY();
    }

    /**
     * Creates a new {@link BackgroundImage} instance.
     *
     * @param image     background image property. {@link PdfImageXObject} instance.
     * @param repeat    background repeat property. {@link BackgroundRepeat} instance.
     * @param blendMode the image's blend mode. {@link BlendMode} instance.
     * @deprecated Remove this constructor in 7.2.
     */
    @Deprecated
    public BackgroundImage(final PdfImageXObject image, final BackgroundRepeat repeat, final BlendMode blendMode) {
        this(image, repeat, new BackgroundPosition(), new BackgroundSize(), null, blendMode,
                BackgroundBox.BORDER_BOX, BackgroundBox.PADDING_BOX);
    }

    /**
     * Creates a new {@link BackgroundImage} instance.
     *
     * @param image     background image property. {@link PdfFormXObject} instance.
     * @param repeat    background repeat property. {@link BackgroundRepeat} instance.
     * @param blendMode the image's blend mode. {@link BlendMode} instance.
     * @deprecated Remove this constructor in 7.2.
     */
    @Deprecated
    public BackgroundImage(final PdfFormXObject image, final BackgroundRepeat repeat, final BlendMode blendMode) {
        this(image, repeat, new BackgroundPosition(), new BackgroundSize(), null, blendMode,
                BackgroundBox.BORDER_BOX, BackgroundBox.PADDING_BOX);
    }

    /**
     * Creates a new {@link BackgroundImage} instance.
     *
     * @param image  background-image property. {@link PdfImageXObject} instance.
     * @param repeat background-repeat property. {@link BackgroundRepeat} instance.
     * @deprecated Remove this constructor in 7.2.
     */
    @Deprecated
    public BackgroundImage(final PdfImageXObject image, final BackgroundRepeat repeat) {
        this(image, repeat, new BackgroundPosition(), new BackgroundSize(), null, DEFAULT_BLEND_MODE,
                BackgroundBox.BORDER_BOX, BackgroundBox.PADDING_BOX);
    }

    /**
     * Creates a new {@link BackgroundImage} instance.
     *
     * @param image  background-image property. {@link PdfFormXObject} instance.
     * @param repeat background-repeat property. {@link BackgroundRepeat} instance.
     * @deprecated Remove this constructor in 7.2.
     */
    @Deprecated
    public BackgroundImage(final PdfFormXObject image, final BackgroundRepeat repeat) {
        this(image, repeat, new BackgroundPosition(), new BackgroundSize(), null, DEFAULT_BLEND_MODE,
                BackgroundBox.BORDER_BOX, BackgroundBox.PADDING_BOX);
    }

    /**
     * Creates a new {@link BackgroundImage} instance.
     *
     * @param image background-image property. {@link PdfImageXObject} instance.
     * @deprecated Remove this constructor in 7.2.
     */
    @Deprecated
    public BackgroundImage(final PdfImageXObject image) {
        this(image, new BackgroundRepeat(), new BackgroundPosition(), new BackgroundSize(), null, DEFAULT_BLEND_MODE,
                BackgroundBox.BORDER_BOX, BackgroundBox.PADDING_BOX);
    }

    /**
     * Creates a new {@link BackgroundImage} instance.
     *
     * @param image background-image property. {@link PdfFormXObject} instance.
     * @deprecated Remove this constructor in 7.2.
     */
    @Deprecated
    public BackgroundImage(final PdfFormXObject image) {
        this(image, new BackgroundRepeat(), new BackgroundPosition(), new BackgroundSize(), null, DEFAULT_BLEND_MODE,
                BackgroundBox.BORDER_BOX, BackgroundBox.PADDING_BOX);
    }

    /**
     * Creates a new {@link BackgroundImage} instance.
     *
     * @param image   background-image property. {@link PdfImageXObject} instance.
     * @param repeatX defines whether background is repeated in x dimension.
     * @param repeatY defines whether background is repeated in y dimension.
     * @deprecated Remove this constructor in 7.2.
     */
    @Deprecated
    public BackgroundImage(final PdfImageXObject image, final boolean repeatX, final boolean repeatY) {
        this(image, new BackgroundRepeat(repeatX ? BackgroundRepeatValue.REPEAT : BackgroundRepeatValue.NO_REPEAT,
                        repeatY ? BackgroundRepeatValue.REPEAT : BackgroundRepeatValue.NO_REPEAT),
                new BackgroundPosition(), new BackgroundSize(), null, DEFAULT_BLEND_MODE,
                BackgroundBox.BORDER_BOX, BackgroundBox.PADDING_BOX);
    }

    /**
     * Creates a new {@link BackgroundImage} instance.
     *
     * @param image   background-image property. {@link PdfFormXObject} instance.
     * @param repeatX defines whether background is repeated in x dimension.
     * @param repeatY defines whether background is repeated in y dimension.
     * @deprecated Remove this constructor in 7.2.
     */
    @Deprecated
    public BackgroundImage(final PdfFormXObject image, final boolean repeatX, final boolean repeatY) {
        this(image, new BackgroundRepeat(repeatX ? BackgroundRepeatValue.REPEAT : BackgroundRepeatValue.NO_REPEAT,
                        repeatY ? BackgroundRepeatValue.REPEAT : BackgroundRepeatValue.NO_REPEAT),
                new BackgroundPosition(), new BackgroundSize(), null, DEFAULT_BLEND_MODE,
                BackgroundBox.BORDER_BOX, BackgroundBox.PADDING_BOX);
    }

    /**
     * Creates a new {@link BackgroundImage} instance with linear gradient.
     *
     * @param linearGradientBuilder the linear gradient builder representing the background image.
     *                              {@link AbstractLinearGradientBuilder} instance.
     * @deprecated Remove this constructor in 7.2.
     */
    @Deprecated
    public BackgroundImage(final AbstractLinearGradientBuilder linearGradientBuilder) {
        this(linearGradientBuilder, DEFAULT_BLEND_MODE);
    }

    /**
     * Creates a new {@link BackgroundImage} instance with linear gradient and custom blending mode.
     *
     * @param linearGradientBuilder the linear gradient builder representing the background image.
     *                              {@link AbstractLinearGradientBuilder} instance.
     * @param blendMode             the image's blend mode. {@link BlendMode} instance.
     * @deprecated Remove this constructor in 7.2.
     */
    @Deprecated
    public BackgroundImage(final AbstractLinearGradientBuilder linearGradientBuilder, final BlendMode blendMode) {
        this(null, new BackgroundRepeat(BackgroundRepeatValue.NO_REPEAT), new BackgroundPosition(),
                new BackgroundSize(), linearGradientBuilder, blendMode,
                BackgroundBox.BORDER_BOX, BackgroundBox.PADDING_BOX);
    }

    public PdfImageXObject getImage() {
        return image instanceof PdfImageXObject ? (PdfImageXObject) image : null;
    }

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
        if (repeat != null) {
            this.repeatX = !repeat.isNoRepeatOnXAxis();
            this.repeatY = !repeat.isNoRepeatOnYAxis();
        }
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

    public AbstractLinearGradientBuilder getLinearGradientBuilder() {
        return this.linearGradientBuilder;
    }

    public boolean isBackgroundSpecified() {
        return image instanceof PdfFormXObject || image instanceof PdfImageXObject || linearGradientBuilder != null;
    }

    @Deprecated
    public boolean isRepeatX() {
        return repeatX;
    }

    @Deprecated
    public boolean isRepeatY() {
        return repeatY;
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
     * Gets initial image width.
     *
     * @return the initial image width
     * @deprecated To be removed in 7.2. Use {@link BackgroundImage#getImageWidth()} instead.
     */
    @Deprecated
    public float getWidth() {
        return (float) image.getWidth();
    }

    /**
     * Gets initial image height.
     *
     * @return the initial image height
     * @deprecated To be removed in 7.2. Use {@link BackgroundImage#getImageHeight()} instead.
     */
    @Deprecated
    public float getHeight() {
        return (float) image.getHeight();
    }

    /**
     * Gets image {@link BackgroundRepeat} instance.
     *
     * @return the image background repeat
     */
    public BackgroundRepeat getRepeat() {
        // Remove this if-blocks after removing repeatX and repeatY
        if (repeatX == repeat.isNoRepeatOnXAxis()) {
            repeat = new BackgroundRepeat(repeatX ? BackgroundRepeatValue.REPEAT : BackgroundRepeatValue.NO_REPEAT,
                    repeat.getYAxisRepeat());
        }
        if (repeatY == repeat.isNoRepeatOnYAxis()) {
            repeat = new BackgroundRepeat(repeat.getXAxisRepeat(),
                    repeatY ? BackgroundRepeatValue.REPEAT : BackgroundRepeatValue.NO_REPEAT);
        }

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
