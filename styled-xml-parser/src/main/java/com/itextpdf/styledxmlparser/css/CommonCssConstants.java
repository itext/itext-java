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
package com.itextpdf.styledxmlparser.css;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class containing possible CSS property keys and values, pseudo element keys,
 * units of measurement, and so on.
 */
public class CommonCssConstants {

    static {
        Map<String, String> keywordValues = new HashMap<>();
        keywordValues.put(CommonCssConstants.XX_SMALL, "9px");
        keywordValues.put(CommonCssConstants.X_SMALL, "10px");
        keywordValues.put(CommonCssConstants.SMALL, "13px");
        keywordValues.put(CommonCssConstants.MEDIUM, "16px");
        keywordValues.put(CommonCssConstants.LARGE, "18px");
        keywordValues.put(CommonCssConstants.X_LARGE, "24px");
        keywordValues.put(CommonCssConstants.XX_LARGE, "32px");
        FONT_ABSOLUTE_SIZE_KEYWORDS_VALUES = Collections.unmodifiableMap(keywordValues);
    }

    // properties

    /**
     * The Constant ALIGN_CONTENT.
     */
    public static final String ALIGN_CONTENT = "align-content";

    /**
     * The Constant ALIGN_ITEMS.
     */
    public static final String ALIGN_ITEMS = "align-items";

    /**
     * The Constant ALIGN_SELF.
     */
    public static final String ALIGN_SELF = "align-self";

    /**
     * The constant ATTRIBUTE.
     */
    public static final String ATTRIBUTE = "attr";

    /**
     * The Constant BACKGROUND.
     */
    public static final String BACKGROUND = "background";

    /**
     * The Constant BACKGROUND_ATTACHMENT.
     */
    public static final String BACKGROUND_ATTACHMENT = "background-attachment";

    /**
     * The Constant BACKGROUND_BLEND_MODE.
     */
    public static final String BACKGROUND_BLEND_MODE = "background-blend-mode";

    /**
     * The Constant BACKGROUND_CLIP.
     */
    public static final String BACKGROUND_CLIP = "background-clip";

    /**
     * The Constant BACKGROUND_COLOR.
     */
    public static final String BACKGROUND_COLOR = "background-color";

    /**
     * The Constant BACKGROUND_IMAGE.
     */
    public static final String BACKGROUND_IMAGE = "background-image";

    /**
     * The Constant BACKGROUND_ORIGIN.
     */
    public static final String BACKGROUND_ORIGIN = "background-origin";

    /**
     * The Constant BACKGROUND_POSITION.
     */
    public static final String BACKGROUND_POSITION = "background-position";

    /**
     * The Constant BACKGROUND_POSITION_X.
     */
    public static final String BACKGROUND_POSITION_X = "background-position-x";

    /**
     * The Constant BACKGROUND_POSITION_Y.
     */
    public static final String BACKGROUND_POSITION_Y = "background-position-y";

    /**
     * The Constant BACKGROUND_REPEAT.
     */
    public static final String BACKGROUND_REPEAT = "background-repeat";

    /**
     * The Constant BACKGROUND_SIZE.
     */
    public static final String BACKGROUND_SIZE = "background-size";

    /**
     * The Constant BORDER.
     */
    public static final String BORDER = "border";

    /**
     * The Constant BORDER_BOTTOM.
     */
    public static final String BORDER_BOTTOM = "border-bottom";

    /**
     * The Constant BORDER_BOTTOM_COLOR.
     */
    public static final String BORDER_BOTTOM_COLOR = "border-bottom-color";

    /**
     * The Constant BORDER_BOTTOM_LEFT_RADIUS.
     */
    public static final String BORDER_BOTTOM_LEFT_RADIUS = "border-bottom-left-radius";

    /**
     * The Constant BORDER_BOTTOM_RIGHT_RADIUS.
     */
    public static final String BORDER_BOTTOM_RIGHT_RADIUS = "border-bottom-right-radius";

    /**
     * The Constant BORDER_BOTTOM_STYLE.
     */
    public static final String BORDER_BOTTOM_STYLE = "border-bottom-style";

    /**
     * The Constant BORDER_BOTTOM_WIDTH.
     */
    public static final String BORDER_BOTTOM_WIDTH = "border-bottom-width";

    /**
     * The Constant BORDER_COLLAPSE.
     */
    public static final String BORDER_COLLAPSE = "border-collapse";

    /**
     * The Constant BORDER_COLOR.
     */
    public static final String BORDER_COLOR = "border-color";

    /**
     * The Constant BORDER_IMAGE.
     */
    public static final String BORDER_IMAGE = "border-image";

    /**
     * The Constant BORDER_LEFT.
     */
    public static final String BORDER_LEFT = "border-left";

    /**
     * The Constant BORDER_LEFT_COLOR.
     */
    public static final String BORDER_LEFT_COLOR = "border-left-color";

    /**
     * The Constant BORDER_LEFT_STYLE.
     */
    public static final String BORDER_LEFT_STYLE = "border-left-style";

    /**
     * The Constant BORDER_LEFT_WIDTH.
     */
    public static final String BORDER_LEFT_WIDTH = "border-left-width";

    /**
     * The Constant BORDER_RADIUS.
     */
    public static final String BORDER_RADIUS = "border-radius";

    /**
     * The Constant BORDER_RIGHT.
     */
    public static final String BORDER_RIGHT = "border-right";

    /**
     * The Constant BORDER_RIGHT_COLOR.
     */
    public static final String BORDER_RIGHT_COLOR = "border-right-color";

    /**
     * The Constant BORDER_RIGHT_STYLE.
     */
    public static final String BORDER_RIGHT_STYLE = "border-right-style";

    /**
     * The Constant BORDER_RIGHT_WIDTH.
     */
    public static final String BORDER_RIGHT_WIDTH = "border-right-width";

    /**
     * The Constant BORDER_SPACING.
     */
    public static final String BORDER_SPACING = "border-spacing";

    /**
     * The Constant BORDER_STYLE.
     */
    public static final String BORDER_STYLE = "border-style";

    /**
     * The Constant BORDER_TOP.
     */
    public static final String BORDER_TOP = "border-top";

    /**
     * The Constant BORDER_TOP_COLOR.
     */
    public static final String BORDER_TOP_COLOR = "border-top-color";

    /**
     * The Constant BORDER_TOP_LEFT_RADIUS.
     */
    public static final String BORDER_TOP_LEFT_RADIUS = "border-top-left-radius";

    /**
     * The Constant BORDER_TOP_RIGHT_RADIUS.
     */
    public static final String BORDER_TOP_RIGHT_RADIUS = "border-top-right-radius";

    /**
     * The Constant BORDER_TOP_STYLE.
     */
    public static final String BORDER_TOP_STYLE = "border-top-style";

    /**
     * The Constant BORDER_TOP_WIDTH.
     */
    public static final String BORDER_TOP_WIDTH = "border-top-width";

    /**
     * The Constant BORDER_WIDTH.
     */
    public static final String BORDER_WIDTH = "border-width";

    /**
     * The Constant BOX_SHADOW.
     */
    public static final String BOX_SHADOW = "box-shadow";

    /**
     * The Constant BREAK_ALL.
     */
    public static final String BREAK_ALL = "break-all";

    /**
     * The Constant CAPTION_SIDE.
     */
    public static final String CAPTION_SIDE = "caption-side";

    /**
     * The Constant COLOR.
     */
    public static final String COLOR = "color";

    /**
     * The Constant COLOR_DODGE.
     */
    public static final String COLOR_DODGE = "color-dodge";

    /**
     * The Constant COLOR_BURN.
     */
    public static final String COLOR_BURN = "color-burn";

    /**
     * The Constant COLUMN_GAP.
     */
    public static final String COLUMN_GAP = "column-gap";

    /**
     * The Constant COLUMN_RULE_WIDTH.
     */
    public static final String COLUMN_RULE_WIDTH = "column-rule-width";

    /**
     * The Constant COLUMN_RULE_STYLE.
     */
    public static final String COLUMN_RULE_STYLE = "column-rule-style";

    /**
     * The Constant COLUMN_RULE_COLOR.
     */
    public static final String COLUMN_RULE_COLOR = "column-rule-color";
    /**
     * The Constant COLUMNS
     */
    public static final String COLUMNS = "columns";

    /**
     * The Constant COLUMNS
     */
    public static final String COLUMN_RULE = "column-rule";


    /**
     * The Constant DARKEN.
     */
    public static final String DARKEN = "darken";

    /**
     * The Constant DIFFERENCE.
     */
    public static final String DIFFERENCE = "difference";

    /**
     * The Constant DIRECTION.
     */
    public static final String DIRECTION = "direction";

    /**
     * The Constant DISPLAY.
     */
    public static final String DISPLAY = "display";

    /**
     * The Constant EMPTY_CELLS.
     */
    public static final String EMPTY_CELLS = "empty-cells";

    /**
     * The Constant EXCLUSION.
     */
    public static final String EXCLUSION = "exclusion";

    /**
     * The Constant FLEX.
     */
    public static final String FLEX = "flex";

    /**
     * The Constant FLEX_BASIS.
     */
    public static final String FLEX_BASIS = "flex-basis";

    /**
     * The Constant FLEX_DIRECTION.
     */
    public static final String FLEX_DIRECTION = "flex-direction";

    /**
     * The Constant FLEX_GROW.
     */
    public static final String FLEX_GROW = "flex-grow";

    /**
     * The Constant FLEX_FLOW.
     */
    public static final String FLEX_FLOW = "flex-flow";

    /**
     * The Constant FLEX_SHRINK.
     */
    public static final String FLEX_SHRINK = "flex-shrink";

    /**
     * The Constant FLEX_WRAP.
     */
    public static final String FLEX_WRAP = "flex-wrap";

    /**
     * The Constant FLOAT.
     */
    public static final String FLOAT = "float";

    /**
     * The Constant FONT.
     */
    public static final String FONT = "font";

    /**
     * The Constant FONT_FAMILY.
     */
    public static final String FONT_FAMILY = "font-family";

    /**
     * The Constant FONT_FEATURE_SETTINGS.
     */
    public static final String FONT_FEATURE_SETTINGS = "font-feature-settings";

    /**
     * The Constant FONT_KERNING.
     */
    public static final String FONT_KERNING = "font-kerning";

    /**
     * The Constant FONT_LANGUAGE_OVERRIDE.
     */
    public static final String FONT_LANGUAGE_OVERRIDE = "font-language-override";

    /**
     * The Constant FONT_SIZE.
     */
    public static final String FONT_SIZE = "font-size";

    /**
     * The Constant FONT_SIZE_ADJUST.
     */
    public static final String FONT_SIZE_ADJUST = "font-size-adjust";

    /**
     * The Constant FONT_STRETCH.
     */
    public static final String FONT_STRETCH = "font-stretch";

    /**
     * The Constant FONT_STYLE.
     */
    public static final String FONT_STYLE = "font-style";

    /**
     * The Constant FONT_SYNTHESIS.
     */
    public static final String FONT_SYNTHESIS = "font-synthesis";

    /**
     * The Constant FONT_VARIANT.
     */
    public static final String FONT_VARIANT = "font-variant";

    /**
     * The Constant FONT_VARIANT_ALTERNATES.
     */
    public static final String FONT_VARIANT_ALTERNATES = "font-variant-alternates";

    /**
     * The Constant FONT_VARIANT_CAPS.
     */
    public static final String FONT_VARIANT_CAPS = "font-variant-caps";

    /**
     * The Constant FONT_VARIANT_EAST_ASIAN.
     */
    public static final String FONT_VARIANT_EAST_ASIAN = "font-variant-east-asian";

    /**
     * The Constant FONT_VARIANT_LIGATURES.
     */
    public static final String FONT_VARIANT_LIGATURES = "font-variant-ligatures";

    /**
     * The Constant FONT_VARIANT_NUMERIC.
     */
    public static final String FONT_VARIANT_NUMERIC = "font-variant-numeric";

    /**
     * The Constant FONT_VARIANT_POSITION.
     */
    public static final String FONT_VARIANT_POSITION = "font-variant-position";

    /**
     * The Constant FONT_WEIGHT.
     */
    public static final String FONT_WEIGHT = "font-weight";

    /**
     * The Constant GAP.
     */
    public static final String GAP = "gap";

    /**
     * The Constant HANGING_PUNCTUATION.
     */
    public static final String HANGING_PUNCTUATION = "hanging-punctuation";

    /**
     * The Constant HARD_LIGHT.
     */
    public static final String HARD_LIGHT = "hard-light";

    /**
     * The Constant HUE.
     */
    public static final String HUE = "hue";

    /**
     * The Constant HYPHENS.
     */
    public static final String HYPHENS = "hyphens";

    /**
     * The Constant INLINE-BLOCK
     */
    public static final String INLINE_BLOCK = "inline-block";

    /** The Constant JUSTIFY_CONTENT. */
    public static final String JUSTIFY_CONTENT = "justify-content";

    /**
     * The Constant JUSTIFY_ITEMS.
     */
    public static final String JUSTIFY_ITEMS = "justify-items";

    /**
     * The Constant KEEP_ALL.
     */
    public static final String KEEP_ALL = "keep-all";

    /**
     * The Constant LETTER_SPACING.
     */
    public static final String LETTER_SPACING = "letter-spacing";

    /**
     * The Constant LINE_HEIGHT.
     */
    public static final String LINE_HEIGHT = "line-height";

    /**
     * The Constant LIST_STYLE.
     */
    public static final String LIST_STYLE = "list-style";

    /**
     * The Constant LIST_STYLE_IMAGE.
     */
    public static final String LIST_STYLE_IMAGE = "list-style-image";

    /**
     * The Constant LIST_STYLE_POSITION.
     */
    public static final String LIST_STYLE_POSITION = "list-style-position";

    /**
     * The Constant LIST_STYLE_TYPE.
     */
    public static final String LIST_STYLE_TYPE = "list-style-type";

    /**
     * The Constant MARGIN.
     */
    public static final String MARGIN = "margin";

    /**
     * The Constant MARGIN_BOTTOM.
     */
    public static final String MARGIN_BOTTOM = "margin-bottom";

    /**
     * The Constant MARGIN_LEFT.
     */
    public static final String MARGIN_LEFT = "margin-left";

    /**
     * The Constant MARGIN_RIGHT.
     */
    public static final String MARGIN_RIGHT = "margin-right";

    /**
     * The Constant MARGIN_TOP.
     */
    public static final String MARGIN_TOP = "margin-top";

    /**
     * The Constant MIN_HEIGHT.
     */
    public static final String MIN_HEIGHT = "min-height";

    /**
     * The Constant MULTIPLY.
     */
    public static final String MULTIPLY = "multiply";

    /**
     * The Constant OPACITY.
     */
    public static final String OPACITY = "opacity";

    /**
     * The Constant ORDER.
     */
    public static final String ORDER = "order";

    /**
     * The Constant OPRPHANS.
     */
    public static final String ORPHANS = "orphans";

    /**
     * The Constant OUTLINE.
     */
    public static final String OUTLINE = "outline";

    /**
     * The Constant OUTLINE_COLOR.
     */
    public static final String OUTLINE_COLOR = "outline-color";

    /**
     * The Constant OUTLINE_STYLE.
     */
    public static final String OUTLINE_STYLE = "outline-style";

    /**
     * The Constant OUTLINE_WIDTH.
     */
    public static final String OUTLINE_WIDTH = "outline-width";

    /**
     * The Constant OVERFLOW_WRAP.
     */
    public static final String OVERFLOW_WRAP = "overflow-wrap";

    /**
     * The Constant OVERFLOW.
     */
    public static final String OVERFLOW = "overflow";

    /**
     * The Constant PADDING.
     */
    public static final String PADDING = "padding";

    /**
     * The Constant PADDING_BOTTOM.
     */
    public static final String PADDING_BOTTOM = "padding-bottom";

    /**
     * The Constant PADDING_LEFT.
     */
    public static final String PADDING_LEFT = "padding-left";

    /**
     * The Constant PADDING_RIGHT.
     */
    public static final String PADDING_RIGHT = "padding-right";

    /**
     * The Constant PADDING_TOP.
     */
    public static final String PADDING_TOP = "padding-top";

    /**
     * The Constant PAGE_BREAK_AFTER.
     */
    public static final String PAGE_BREAK_AFTER = "page-break-after";

    /**
     * The Constant PAGE_BREAK_BEFORE.
     */
    public static final String PAGE_BREAK_BEFORE = "page-break-before";

    /**
     * The Constant PAGE_BREAK_INSIDE.
     */
    public static final String PAGE_BREAK_INSIDE = "page-break-inside";

    /**
     * The Constant PLACE_ITEMS.
     */
    public static final String PLACE_ITEMS = "place-items";

    /**
     * The Constant POSITION.
     */
    public static final String POSITION = "position";

    /**
     * The Constant QUOTES.
     */
    public static final String QUOTES = "quotes";

    /**
     * The Constant TAB_SIZE.
     */
    public static final String TAB_SIZE = "tab-size";

    /**
     * The Constant TEXT_ALIGN.
     */
    public static final String TEXT_ALIGN = "text-align";

    /**
     * The Constant TEXT_ALIGN_LAST.
     */
    public static final String TEXT_ALIGN_LAST = "text-align-last";

    /**
     * The Constant TEXT_COMBINE_UPRIGHT.
     */
    public static final String TEXT_COMBINE_UPRIGHT = "text-combine-upright";

    /**
     * The Constant TEXT_DECORATION.
     */
    public static final String TEXT_DECORATION = "text-decoration";

    /**
     * The Constant TEXT_DECORATION_LINE.
     */
    public static final String TEXT_DECORATION_LINE = "text-decoration-line";

    /**
     * The Constant TEXT_DECORATION_STYLE.
     */
    public static final String TEXT_DECORATION_STYLE = "text-decoration-style";

    /**
     * The Constant TEXT_DECORATION_COLOR.
     */
    public static final String TEXT_DECORATION_COLOR = "text-decoration-color";

    /**
     * The Constant TEXT_INDENT.
     */
    public static final String TEXT_INDENT = "text-indent";

    /**
     * The Constant TEXT_JUSTIFY.
     */
    public static final String TEXT_JUSTIFY = "text-justify";

    /**
     * The Constant TEXT_ORIENTATION.
     */
    public static final String TEXT_ORIENTATION = "text-orientation";

    /**
     * The Constant TEXT_SHADOW.
     */
    public static final String TEXT_SHADOW = "text-shadow";

    /**
     * The Constant TEXT_TRANSFORM.
     */
    public static final String TEXT_TRANSFORM = "text-transform";

    /**
     * The Constant TEXT_UNDERLINE_POSITION.
     */
    public static final String TEXT_UNDERLINE_POSITION = "text-underline-position";

    /**
     * The Constant TRANSFORM.
     */
    public static final String TRANSFORM = "transform";

    /**
     * The Constant UNICODE_BIDI.
     */
    public static final String UNICODE_BIDI = "unicode-bidi";

    /**
     * The Constant VISIBILITY.
     */
    public static final String VISIBILITY = "visibility";

    /**
     * The Constant WHITE_SPACE.
     */
    public static final String WHITE_SPACE = "white-space";

    /**
     * The Constant WIDOWS.
     */
    public static final String WIDOWS = "widows";

    /**
     * The Constant WIDTH.
     */
    public static final String WIDTH = "width";

    /**
     * The Constant HEIGHT.
     */
    public static final String HEIGHT = "height";

    /**
     * The Constant WORDWRAP.
     */
    public static final String WORDWRAP = "word-wrap";

    /**
     * The Constant WORD_BREAK.
     */
    public static final String WORD_BREAK = "word-break";

    /**
     * The Constant WORD_SPACING.
     */
    public static final String WORD_SPACING = "word-spacing";

    /**
     * The Constant WRITING_MODE.
     */
    public static final String WRITING_MODE = "writing-mode";

    // property values

    /**
     * The Constant ANYWHERE.
     */
    public static final String ANYWHERE = "anywhere";

    /**
     * The Constant ALWAYS.
     */
    public static final String ALWAYS = "always";

    /**
     * The Constant ARMENIAN.
     */
    public static final String ARMENIAN = "armenian";

    /**
     * The Constant AVOID.
     */
    public static final String AVOID = "avoid";

    /**
     * The Constant AUTO.
     */
    public static final String AUTO = "auto";

    /**
     * The Constant BASELINE.
     */
    public static final String BASELINE = "baseline";

    /**
     * The Constant BLINK.
     */
    public static final String BLINK = "blink";

    /**
     * The Constant BOLD.
     */
    public static final String BOLD = "bold";

    /**
     * The Constant BOLDER.
     */
    public static final String BOLDER = "bolder";

    /**
     * The Constant BORDER_BOX.
     */
    public static final String BORDER_BOX = "border-box";

    /**
     * The Constant BOTTOM.
     */
    public static final String BOTTOM = "bottom";

    /** The Constant BREAK_WORD. */
    public static final String BREAK_WORD = "break-word";

    /**
     * The Constant CAPTION.
     */
    public static final String CAPTION = "caption";

    /**
     * The Constant CENTER.
     */
    public static final String CENTER = "center";

    /**
     * The Constant CIRCLE.
     */
    public static final String CIRCLE = "circle";

    /**
     * The Constant CJK_IDEOGRAPHIC.
     */
    public static final String CJK_IDEOGRAPHIC = "cjk-ideographic";

    /**
     * The Constant CLOSE_QUOTE.
     */
    public static final String CLOSE_QUOTE = "close-quote";

    /**
     * The Constant COLUMN.
     */
    public static final String COLUMN = "column";

    /**
     * The Constant COLUMN_REVERSE.
     */
    public static final String COLUMN_REVERSE = "column-reverse";

    /**
     * The Constant COLUMN_COUNT.
     */
    public static final String COLUMN_COUNT = "column-count";

    /**
     * The Constant COLUMN_WIDTH.
     */
    public static final String COLUMN_WIDTH = "column-width";

    /**
     * The Constant CONTAIN.
     */
    public static final String CONTAIN = "contain";

    /**
     * The Constant CONTENT.
     */
    public static final String CONTENT = "content";

    /**
     * The Constant CONTENT_BOX.
     */
    public static final String CONTENT_BOX = "content-box";

    /**
     * The Constant COVER.
     */
    public static final String COVER = "cover";

    /**
     * The Constant CURRENTCOLOR.
     */
    public static final String CURRENTCOLOR = "currentcolor";

    /**
     * The Constant DASHED.
     */
    public static final String DASHED = "dashed";

    /**
     * The Constant DECIMAL.
     */
    public static final String DECIMAL = "decimal";

    /**
     * The Constant DECIMAL_LEADING_ZERO.
     */
    public static final String DECIMAL_LEADING_ZERO = "decimal-leading-zero";

    /**
     * The Constant DEG.
     */
    public static final String DEG = "deg";

    /** The Constant DISC. */
    public static final String DISC = "disc";

    /**
     * The Constant DOTTED.
     */
    public static final String DOTTED = "dotted";

    /**
     * The Constant DOUBLE.
     */
    public static final String DOUBLE = "double";

    /**
     * The Constant EACH_LINE.
     */
    public static final String EACH_LINE = "each-line";

    /**
     * The Constant END.
     */
    public static final String END = "end";

    /**
     * The Constant FIRST.
     */
    public static final String FIRST = "first";

    /**
     * The Constant FIT_CONTENT.
     */
    public static final String FIT_CONTENT = "fit-content";

    /**
     * The Constant FIXED.
     */
    public static final String FIXED = "fixed";

    /**
     * The Constant FLEX_END.
     */
    public static final String FLEX_END = "flex-end";

    /**
     * The Constant FLEX_START.
     */
    public static final String FLEX_START = "flex-start";

    /**
     * The Constant GEORGIAN.
     */
    public static final String GEORGIAN = "georgian";

    /** The Constant GRAD. */
    public static final String GRAD = "grad";

    /** The Constant GROOVE. */
    public static final String GROOVE = "groove";

    /**
     * The Constant HANGING.
     */
    public static final String HANGING = "hanging";

    /**
     * The Constant HEBREW.
     */
    public static final String HEBREW = "hebrew";

    /**
     * The Constant HIDDEN.
     */
    public static final String HIDDEN = "hidden";

    /**
     * The Constant HIRAGANA.
     */
    public static final String HIRAGANA = "hiragana";

    /**
     * The Constant HIRAGANA_IROHA.
     */
    public static final String HIRAGANA_IROHA = "hiragana-iroha";

    /**
     * The Constant ICON.
     */
    public static final String ICON = "icon";

    /**
     * The Constant INHERIT.
     */
    public static final String INHERIT = "inherit";

    /**
     * The Constant INITIAL.
     */
    public static final String INITIAL = "initial";

    /**
     * The Constant INSET.
     */
    public static final String INSET = "inset";

    /**
     * The Constant INSIDE.
     */
    public static final String INSIDE = "inside";

    /**
     * The Constant ITALIC.
     */
    public static final String ITALIC = "italic";

    /**
     * The Constant LARGE.
     */
    public static final String LARGE = "large";

    /**
     * The Constant LARGER.
     */
    public static final String LARGER = "larger";

    /**
     * The Constant LAST.
     */
    public static final String LAST = "last";

    /**
     * The Constant value LEGACY.
     */
    public static final String LEGACY = "legacy";

    /**
     * The Constant LEFT.
     */
    public static final String LEFT = "left";

    /**
     * The Constant LIGHTEN.
     */
    public static final String LIGHTEN = "lighten";

    /**
     * The Constant LIGHTER.
     */
    public static final String LIGHTER = "lighter";

    /**
     * The Constant value LINE_THROUGH.
     */
    public static final String LINE_THROUGH = "line-through";

    /**
     * The Constant LOCAL.
     */
    public static final String LOCAL = "local";

    /**
     * The Constant LOWER_ALPHA.
     */
    public static final String LOWER_ALPHA = "lower-alpha";

    /**
     * The Constant LOWER_GREEK.
     */
    public static final String LOWER_GREEK = "lower-greek";

    /**
     * The Constant LOWER_LATIN.
     */
    public static final String LOWER_LATIN = "lower-latin";

    /**
     * The Constant LOWER_ROMAN.
     */
    public static final String LOWER_ROMAN = "lower-roman";

    /**
     * The Constant LUMINOSITY.
     */
    public static final String LUMINOSITY = "luminosity";

    /**
     * The Constant MANUAL.
     */
    public static final String MANUAL = "manual";

    /**
     * The Constant MATRIX.
     */
    public static final String MATRIX = "matrix";

    /**
     * The Constant MEDIUM.
     */
    public static final String MEDIUM = "medium";

    /**
     * The Constant MENU.
     */
    public static final String MENU = "menu";

    /**
     * The Constant MAX_CONTENT.
     */
    public static final String MAX_CONTENT = "max-content";

    /**
     * The Constant MIN_CONTENT.
     */
    public static final String MIN_CONTENT = "min-content";

    /**
     * The Constant MESSAGE_BOX.
     */
    public static final String MESSAGE_BOX = "message-box";

    /**
     * The Constant NOWRAP.
     */
    public static final String NOWRAP = "nowrap";

    /**
     * The Constant NO_OPEN_QUOTE.
     */
    public static final String NO_OPEN_QUOTE = "no-open-quote";

    /**
     * The Constant NO_CLOSE_QUOTE.
     */
    public static final String NO_CLOSE_QUOTE = "no-close-quote";

    /**
     * The Constant NO_REPEAT.
     */
    public static final String NO_REPEAT = "no-repeat";

    /**
     * The Constant NONE.
     */
    public static final String NONE = "none";

    /**
     * The Constant NORMAL.
     */
    public static final String NORMAL = "normal";

    /**
     * The Constant OBLIQUE.
     */
    public static final String OBLIQUE = "oblique";

    /**
     * The Constant OPEN_QUOTE.
     */
    public static final String OPEN_QUOTE = "open-quote";

    /**
     * The Constant OUTSIDE.
     */
    public static final String OUTSIDE = "outside";

    /**
     * The Constant OUTSET.
     */
    public static final String OUTSET = "outset";

    /**
     * The Constant value OVERLAY.
     */
    public static final String OVERLAY = "overlay";

    /**
     * The Constant value OVERLINE.
     */
    public static final String OVERLINE = "overline";

    /**
     * The Constant PADDING_BOX.
     */
    public static final String PADDING_BOX = "padding-box";

    /**
     * The Constant RAD.
     */
    public static final String RAD = "rad";

    /**
     * The Constant REPEAT.
     */
    public static final String REPEAT = "repeat";

    /**
     * The Constant REPEAT_X.
     */
    public static final String REPEAT_X = "repeat-x";

    /**
     * The Constant REPEAT_Y.
     */
    public static final String REPEAT_Y = "repeat-y";

    /**
     * The Constant RIDGE.
     */
    public static final String RIDGE = "ridge";

    /**
     * The Constant RIGHT.
     */
    public static final String RIGHT = "right";

    /**
     * The Constant ROTATE.
     */
    public static final String ROTATE = "rotate";

    /**
     * The Constant ROUND.
     */
    public static final String ROUND = "round";

    /**
     * The Constant ROW.
     */
    public static final String ROW = "row";

    /**
     * The Constant ROW_GAP.
     */
    public static final String ROW_GAP = "row-gap";

    /**
     * The Constant ROW_REVERSE.
     */
    public static final String ROW_REVERSE = "row-reverse";

    /**
     * The Constant SAFE.
     */
    public static final String SAFE = "safe";

    /**
     * The Constant SATURATION.
     */
    public static final String SATURATION = "saturation";

    /**
     * The Constant SCALE.
     */
    public static final String SCALE = "scale";

    /**
     * The Constant SCALE_X.
     */
    public static final String SCALE_X = "scalex";

    /**
     * The Constant SCALE_Y.
     */
    public static final String SCALE_Y = "scaley";

    /**
     * The Constant SCREEN.
     */
    public static final String SCREEN = "screen";

    /**
     * The Constant SCROLL.
     */
    public static final String SCROLL = "scroll";


    /**
     * The Constant value SELF_END.
     */
    public static final String SELF_END = "self-end";

    /**
     * The Constant SELF_START.
     */
    public static final String SELF_START = "self-start";

    /**
     * The Constant SKEW.
     */
    public static final String SKEW = "skew";

    /**
     * The Constant SKEW_X.
     */
    public static final String SKEW_X = "skewx";

    /**
     * The Constant SKEW_Y.
     */
    public static final String SKEW_Y = "skewy";

    /**
     * The Constant SMALL.
     */
    public static final String SMALL = "small";

    /**
     * The Constant SMALL_CAPS.
     */
    public static final String SMALL_CAPS = "small-caps";

    /**
     * The Constant SMALL_CAPTION.
     */
    public static final String SMALL_CAPTION = "small-caption";

    /**
     * The Constant SMALLER.
     */
    public static final String SMALLER = "smaller";

    /**
     * The Constant SOFT_LIGHT.
     */
    public static final String SOFT_LIGHT = "soft-light";

    /**
     * The Constant SOLID.
     */
    public static final String SOLID = "solid";

    /**
     * The Constant SPACE.
     */
    public static final String SPACE = "space";

    /**
     * The Constant SPACE_AROUND.
     */
    public static final String SPACE_AROUND = "space-around";

    /**
     * The Constant SPACE_BETWEEN.
     */
    public static final String SPACE_BETWEEN = "space-between";

    /**
     * The Constant SPACE_EVENLY.
     */
    public static final String SPACE_EVENLY = "space-evenly";

    /**
     * The Constant SQUARE.
     */
    public static final String SQUARE = "square";

    /**
     * The Constant START.
     */
    public static final String START = "start";

    /**
     * The Constant STATIC.
     */
    public static final String STATIC = "static";

    /**
     * The Constant STATUS_BAR.
     */
    public static final String STATUS_BAR = "status-bar";

    /**
     * The Constant STRETCH.
     */
    public static final String STRETCH = "stretch";

    /**
     * The Constant STRING.
     */
    public static final String STRING = "string";

    /**
     * The Constant THICK.
     */
    public static final String THICK = "thick";

    /**
     * The Constant THIN.
     */
    public static final String THIN = "thin";

    /**
     * The Constant TOP.
     */
    public static final String TOP = "top";

    /**
     * The Constant TRANSLATE.
     */
    public static final String TRANSLATE = "translate";

    /**
     * The Constant TRANSLATE_X.
     */
    public static final String TRANSLATE_X = "translatex";

    /**
     * The Constant TRANSLATE_Y.
     */
    public static final String TRANSLATE_Y = "translatey";

    /**
     * The Constant TRANSPARENT.
     */
    public static final String TRANSPARENT = "transparent";

    /**
     * The Constant UNDEFINED_NAME.
     */
    public static final String UNDEFINED_NAME = "undefined";

    /**
     * The Constant value UNDERLINE
     */
    public static final String UNDERLINE = "underline";

    /**
     * The Constant UNSAFE.
     */
    public static final String UNSAFE = "unsafe";

    /**
     * The Constant value UNSET.
     */
    public static final String UNSET = "unset";

    /**
     * The Constant UPPER_ALPHA.
     */
    public static final String UPPER_ALPHA = "upper-alpha";

    /**
     * The Constant UPPER_LATIN.
     */
    public static final String UPPER_LATIN = "upper-latin";

    /**
     * The Constant UPPER_ROMAN.
     */
    public static final String UPPER_ROMAN = "upper-roman";

    /**
     * The Constant value VISIBLE.
     */
    public static final String VISIBLE = "visible";

    /**
     * The Constant value WAVY.
     */
    public static final String WAVY = "wavy";

    /**
     * The Constant WRAP.
     */
    public static final String WRAP = "wrap";

    /**
     * The Constant WRAP_REVERSE.
     */
    public static final String WRAP_REVERSE = "wrap-reverse";

    /**
     * The Constant X_LARGE.
     */
    public static final String X_LARGE = "x-large";

    /**
     * The Constant X_SMALL.
     */
    public static final String X_SMALL = "x-small";

    /**
     * The Constant XX_LARGE.
     */
    public static final String XX_LARGE = "xx-large";

    /**
     * The Constant XX_SMALL.
     */
    public static final String XX_SMALL = "xx-small";


    // properties possible values

    /**
     * The Constant BACKGROUND_SIZE_VALUES.
     */
    public static final Set<String> BACKGROUND_SIZE_VALUES = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList(AUTO, COVER, CONTAIN)));

    /**
     * The Constant BACKGROUND_ORIGIN_OR_CLIP_VALUES.
     */
    public static final Set<String> BACKGROUND_ORIGIN_OR_CLIP_VALUES = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList(PADDING_BOX, BORDER_BOX, CONTENT_BOX)));

    /**
     * The Constant BACKGROUND_REPEAT_VALUES.
     */
    public static final Set<String> BACKGROUND_REPEAT_VALUES = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList(REPEAT, NO_REPEAT, REPEAT_X, REPEAT_Y, ROUND, SPACE)));

    /**
     * The Constant BACKGROUND_ATTACHMENT_VALUES.
     */
    public static final Set<String> BACKGROUND_ATTACHMENT_VALUES = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList(FIXED, SCROLL, LOCAL)));

    /**
     * The Constant BACKGROUND_POSITION_VALUES.
     */
    public static final Set<String> BACKGROUND_POSITION_VALUES = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList(LEFT, CENTER, BOTTOM, TOP, RIGHT)));

    /**
     * The Constant BACKGROUND_POSITION_X_VALUES.
     */
    public static final Set<String> BACKGROUND_POSITION_X_VALUES = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList(LEFT, CENTER, RIGHT)));

    /**
     * The Constant BACKGROUND_POSITION_Y_VALUES.
     */
    public static final Set<String> BACKGROUND_POSITION_Y_VALUES = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList(CENTER, BOTTOM, TOP)));

    /**
     * The Constant BORDER_WIDTH_VALUES.
     */
    public static final Set<String> BORDER_WIDTH_VALUES = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList(new String[]{THIN, MEDIUM, THICK})));

    /**
     * The Constant BORDER_STYLE_VALUES.
     */
    public static final Set<String> BORDER_STYLE_VALUES = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList(new String[]{NONE, HIDDEN, DOTTED, DASHED, SOLID, DOUBLE, GROOVE, RIDGE, INSET, OUTSET})));

    /**
     * The Constant BLEND_MODE_VALUES.
     */
    public static final Set<String> BLEND_MODE_VALUES = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList(new String[]{NORMAL, MULTIPLY, SCREEN, OVERLAY, DARKEN, LIGHTEN,
                    COLOR_DODGE, COLOR_BURN, HARD_LIGHT, SOFT_LIGHT, DIFFERENCE, EXCLUSION, HUE,
                    SATURATION, COLOR, LUMINOSITY})));

    /**
     * The Constant FONT_ABSOLUTE_SIZE_KEYWORDS.
     */
    public static final Map<String, String> FONT_ABSOLUTE_SIZE_KEYWORDS_VALUES;

    /**
     * The Constant METRIC_MEASUREMENTS.
     */
    public static final String[] METRIC_MEASUREMENTS_VALUES = new String[] {CommonCssConstants.PX, CommonCssConstants.IN,
            CommonCssConstants.CM, CommonCssConstants.MM, CommonCssConstants.PC, CommonCssConstants.PT, CommonCssConstants.Q};

    // pseudo-classes

    /**
     * The Constant ACTIVE.
     */
    public static final String ACTIVE = "active";

    /**
     * The Constant CHECKED.
     */
    public static final String CHECKED = "checked";

    /**
     * The Constant DISABLED.
     */
    public static final String DISABLED = "disabled";

    /**
     * The Constant EMPTY.
     */
    public static final String EMPTY = "empty";

    /**
     * The Constant ENABLED.
     */
    public static final String ENABLED = "enabled";

    /**
     * The Constant FIRST_CHILD.
     */
    public static final String FIRST_CHILD = "first-child";

    /**
     * The Constant FIRST_OF_TYPE.
     */
    public static final String FIRST_OF_TYPE = "first-of-type";

    /**
     * The Constant FOCUS.
     */
    public static final String FOCUS = "focus";

    /**
     * The Constant HOVER.
     */
    public static final String HOVER = "hover";

    /**
     * The Constant IN_RANGE.
     */
    public static final String IN_RANGE = "in-range";

    /**
     * The Constant INVALID.
     */
    public static final String INVALID = "invalid";

    /**
     * The Constant LANG.
     */
    public static final String LANG = "lang";

    /**
     * The Constant LAST_CHILD.
     */
    public static final String LAST_CHILD = "last-child";

    /**
     * The Constant LAST_OF_TYPE.
     */
    public static final String LAST_OF_TYPE = "last-of-type";

    /**
     * The Constant LINK.
     */
    public static final String LINK = "link";

    /**
     * The Constant NTH_CHILD.
     */
    public static final String NTH_CHILD = "nth-child";

    /**
     * The Constant NOT.
     */
    public static final String NOT = "not";

    /**
     * The Constant NTH_LAST_CHILD.
     */
    public static final String NTH_LAST_CHILD = "nth-last-child";

    /**
     * The Constant NTH_LAST_OF_TYPE.
     */
    public static final String NTH_LAST_OF_TYPE = "nth-last-of-type";

    /**
     * The Constant NTH_OF_TYPE.
     */
    public static final String NTH_OF_TYPE = "nth-of-type";

    /**
     * The Constant ONLY_OF_TYPE.
     */
    public static final String ONLY_OF_TYPE = "only-of-type";

    /**
     * The Constant ONLY_CHILD.
     */
    public static final String ONLY_CHILD = "only-child";

    /**
     * The Constant OPTIONAL.
     */
    public static final String OPTIONAL = "optional";

    /**
     * The Constant OUT_OF_RANGE.
     */
    public static final String OUT_OF_RANGE = "out-of-range";

    /**
     * The Constant READ_ONLY.
     */
    public static final String READ_ONLY = "read-only";

    /**
     * The Constant READ_WRITE.
     */
    public static final String READ_WRITE = "read-write";

    /**
     * The Constant REQUIRED.
     */
    public static final String REQUIRED = "required";

    /**
     * The Constant ROOT.
     */
    public static final String ROOT = "root";

    /**
     * The Constant TARGET.
     */
    public static final String TARGET = "target";

    /**
     * The Constant URL.
     */
    public static final String URL = "url";

    /**
     * The Constant VALID.
     */
    public static final String VALID = "valid";

    /**
     * The Constant VISITED.
     */
    public static final String VISITED = "visited";

    // units of measurement

    /**
     * The Constant CM.
     */
    public static final String CM = "cm";

    /**
     * The Constant EM.
     */
    public static final String EM = "em";

    /**
     * The Constant EX.
     */
    public static final String EX = "ex";

    /**
     * The Constant IN.
     */
    public static final String IN = "in";

    /**
     * The Constant MM.
     */
    public static final String MM = "mm";

    /**
     * The Constant PC.
     */
    public static final String PC = "pc";

    /**
     * The Constant PERCENTAGE.
     */
    public static final String PERCENTAGE = "%";

    /**
     * The Constant PT.
     */
    public static final String PT = "pt";

    /**
     * The Constant PX.
     */
    public static final String PX = "px";

    /**
     * The Constant REM.
     */
    public static final String REM = "rem";

    /**
     * The Constant Q.
     */
    public static final String Q = "q";

    // units of resolution

    /**
     * The Constant DPCM.
     */
    public static final String DPCM = "dpcm";

    /**
     * The Constant DPI.
     */
    public static final String DPI = "dpi";

    /**
     * The Constant DPPX.
     */
    public static final String DPPX = "dppx";
}
