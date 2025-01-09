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
package com.itextpdf.kernel.pdf.canvas.parser.util;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfLiteral;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses the page or form XObject content.
 */
public class PdfCanvasParser {

    /**
     * Holds value of property tokeniser.
     */
    private PdfTokenizer tokeniser;

    private PdfResources currentResources;

    /**
     * Creates a new instance of PdfContentParser
     * @param tokeniser the tokeniser with the content
     */
    public PdfCanvasParser(PdfTokenizer tokeniser) {
        this.tokeniser = tokeniser;
    }

    /**
     * Creates a new instance of PdfContentParser
     * @param tokeniser the tokeniser with the content
     * @param currentResources current resources of the content stream.
     *                         It is optional parameter, which is used for performance improvements of specific cases of
     *                         inline images parsing.
     */
    public PdfCanvasParser(PdfTokenizer tokeniser, PdfResources currentResources) {
        this.tokeniser = tokeniser;
        this.currentResources = currentResources;
    }

    /**
     * Parses a single command from the content. Each command is output as an array of arguments
     * having the command itself as the last element. The returned array will be empty if the
     * end of content was reached.
     * <br>
     * A specific behaviour occurs when inline image is encountered (BI command):
     * in that case, parser would continue parsing until it meets EI - end of the inline image;
     * as a result in this case it will return an array with inline image dictionary and image bytes
     * encapsulated in PdfStream object as first element and EI command as second element.
     * @param ls an <CODE>ArrayList</CODE> to use. It will be cleared before using. If it's
     * <CODE>null</CODE> will create a new <CODE>ArrayList</CODE>
     * @return the same <CODE>ArrayList</CODE> given as argument or a new one
     * @throws IOException on error
     */
    public List<PdfObject> parse(List<PdfObject> ls) throws IOException {
        if (ls == null)
            ls = new ArrayList<>();
        else
            ls.clear();
        PdfObject ob = null;
        while ((ob = readObject()) != null) {
            ls.add(ob);
            if (tokeniser.getTokenType() == PdfTokenizer.TokenType.Other) {
                if ("BI".equals(ob.toString())) {
                    PdfStream inlineImageAsStream = InlineImageParsingUtils.parse(this, currentResources.getResource(PdfName.ColorSpace));
                    ls.clear();
                    ls.add(inlineImageAsStream);
                    ls.add(new PdfLiteral("EI"));
                }
                break;
            }
        }
        return ls;
    }

    /**
     * Gets the tokeniser.
     * @return the tokeniser.
     */
    public PdfTokenizer getTokeniser() {
        return this.tokeniser;
    }

    /**
     * Sets the tokeniser.
     * @param tokeniser the tokeniser
     */
    public void setTokeniser(PdfTokenizer tokeniser) {
        this.tokeniser = tokeniser;
    }

    /**
     * Reads a dictionary. The tokeniser must be positioned past the "&lt;&lt;" token.
     * @return the dictionary
     * @throws IOException on error
     */
    public PdfDictionary readDictionary() throws IOException {
        PdfDictionary dic = new PdfDictionary();
        while (true) {
            if (!nextValidToken())
                throw new PdfException(KernelExceptionMessageConstant.UNEXPECTED_END_OF_FILE);
            if (tokeniser.getTokenType() == PdfTokenizer.TokenType.EndDic)
                break;
            if (tokeniser.getTokenType() != PdfTokenizer.TokenType.Name)
                tokeniser.throwError(
                        KernelExceptionMessageConstant.THIS_DICTIONARY_KEY_IS_NOT_A_NAME, tokeniser.getStringValue());
            PdfName name = new PdfName(tokeniser.getStringValue());
            PdfObject obj = readObject();
            dic.put(name, obj);
        }
        return dic;
    }

    /**
     * Reads an array. The tokeniser must be positioned past the "[" token.
     * @return an array
     * @throws IOException on error
     */
    public PdfArray readArray() throws IOException {
        PdfArray array = new PdfArray();
        while (true) {
            PdfObject obj = readObject();
            if (!obj.isArray() && tokeniser.getTokenType() == PdfTokenizer.TokenType.EndArray) {
                break;
            }
            if (tokeniser.getTokenType() == PdfTokenizer.TokenType.EndDic && obj.getType() != PdfObject.DICTIONARY) {
                tokeniser.throwError(MessageFormatUtil.format(KernelExceptionMessageConstant.UNEXPECTED_TOKEN, ">>"));
            }
            array.add(obj);
        }
        return array;
    }

    /**
     * Reads a pdf object.
     * @return the pdf object
     * @throws IOException on error
     */
    public PdfObject readObject() throws IOException {
        if (!nextValidToken())
            return null;
        final PdfTokenizer.TokenType type = tokeniser.getTokenType();
        switch (type) {
            case StartDic: {
                PdfDictionary dic = readDictionary();
                return dic;
            }
            case StartArray:
                return readArray();
            case String:
                PdfString str = new PdfString(tokeniser.getDecodedStringContent()).setHexWriting(tokeniser.isHexString());
                return str;
            case Name:
                return new PdfName(tokeniser.getByteContent());
            case Number:
                //use PdfNumber(byte[]) here, as in this case number parsing won't happen until it's needed.
                return new PdfNumber(tokeniser.getByteContent());
            default:
                return new PdfLiteral(tokeniser.getByteContent());
        }
    }

    /**
     * Reads the next token skipping over the comments.
     * @return <CODE>true</CODE> if a token was read, <CODE>false</CODE> if the end of content was reached
     * @throws IOException on error
     */
    public boolean nextValidToken() throws IOException {
        while (tokeniser.nextToken()) {
            if (tokeniser.getTokenType() == PdfTokenizer.TokenType.Comment)
                continue;
            return true;
        }
        return false;
    }
}
