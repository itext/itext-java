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
package com.itextpdf.styledxmlparser.css.resolve;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.parse.CssDeclarationValueTokenizer;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Helper class to deal with quoted values in strings.
 */
public class CssQuotes {

    /**
     * The empty quote value.
     */
    private static final String EMPTY_QUOTE = "";

    /**
     * The open quotes.
     */
    private ArrayList<String> openQuotes;

    /**
     * The close quotes.
     */
    private ArrayList<String> closeQuotes;

    /**
     * Creates a new {@link CssQuotes} instance.
     *
     * @param openQuotes  the open quotes
     * @param closeQuotes the close quotes
     */
    private CssQuotes(ArrayList<String> openQuotes, ArrayList<String> closeQuotes) {
        this.openQuotes = new ArrayList<>(openQuotes);
        this.closeQuotes = new ArrayList<>(closeQuotes);
    }

    /**
     * Creates a {@link CssQuotes} instance.
     *
     * @param quotesString      the quotes string
     * @param fallbackToDefault indicates whether it's OK to fall back to the default
     * @return the resulting {@link CssQuotes} instance
     */
    public static CssQuotes createQuotes(String quotesString, boolean fallbackToDefault) {
        boolean error = false;
        ArrayList<ArrayList<String>> quotes = new ArrayList<>(2);
        quotes.add(new ArrayList<String>());
        quotes.add(new ArrayList<String>());
        if (quotesString != null) {
            if (quotesString.equals(CommonCssConstants.NONE)) {
                quotes.get(0).add(EMPTY_QUOTE);
                quotes.get(1).add(EMPTY_QUOTE);
                return new CssQuotes(quotes.get(0), quotes.get(1));
            }
            CssDeclarationValueTokenizer tokenizer = new CssDeclarationValueTokenizer(quotesString);
            CssDeclarationValueTokenizer.Token token;
            for (int i = 0; ((token = tokenizer.getNextValidToken()) != null); ++i) {
                if (token.isString()) {
                    quotes.get(i % 2).add(token.getValue());
                } else {
                    error = true;
                    break;
                }
            }
            if (quotes.get(0).size() == quotes.get(1).size() && !quotes.get(0).isEmpty() && !error) {
                return new CssQuotes(quotes.get(0), quotes.get(1));
            } else {
                LoggerFactory.getLogger(CssQuotes.class).error(MessageFormatUtil.format(
                        StyledXmlParserLogMessageConstant.QUOTES_PROPERTY_INVALID, quotesString));
            }
        }
        return fallbackToDefault ? createDefaultQuotes() : null;
    }

    /**
     * Creates the default {@link CssQuotes} instance.
     *
     * @return the {@link CssQuotes} instance
     */
    public static CssQuotes createDefaultQuotes() {
        ArrayList<String> openQuotes = new ArrayList<>();
        ArrayList<String> closeQuotes = new ArrayList<>();
        openQuotes.add("\u00ab");
        closeQuotes.add("\u00bb");
        return new CssQuotes(openQuotes, closeQuotes);
    }

    /**
     * Resolves quotes.
     *
     * @param value   the value
     * @param context the CSS context
     * @return the quote string
     */
    public String resolveQuote(String value, AbstractCssContext context) {
        int depth = context.getQuotesDepth();
        if (CommonCssConstants.OPEN_QUOTE.equals(value)) {
            increaseDepth(context);
            return getQuote(depth, openQuotes);
        } else if (CommonCssConstants.CLOSE_QUOTE.equals(value)) {
            decreaseDepth(context);
            return getQuote(depth - 1, closeQuotes);
        } else if (CommonCssConstants.NO_OPEN_QUOTE.equals(value)) {
            increaseDepth(context);
            return EMPTY_QUOTE;
        } else if (CommonCssConstants.NO_CLOSE_QUOTE.equals(value)) {
            decreaseDepth(context);
            return EMPTY_QUOTE;
        }
        return null;
    }

    /**
     * Increases the quote depth.
     *
     * @param context the context
     */
    private void increaseDepth(AbstractCssContext context) {
        context.setQuotesDepth(context.getQuotesDepth() + 1);
    }

    /**
     * Decreases the quote depth.
     *
     * @param context the context
     */
    private void decreaseDepth(AbstractCssContext context) {
        if (context.getQuotesDepth() > 0) {
            context.setQuotesDepth(context.getQuotesDepth() - 1);
        }
    }

    /**
     * Gets the quote.
     *
     * @param depth  the depth
     * @param quotes the quotes
     * @return the requested quote string
     */
    private String getQuote(int depth, ArrayList<String> quotes) {
        if (depth >= quotes.size()) {
            return quotes.get(quotes.size() - 1);
        }
        if (depth < 0) {
            return EMPTY_QUOTE;
        }
        return quotes.get(depth);
    }
}
