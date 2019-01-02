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
package com.itextpdf.kernel.pdf.canvas.parser.util;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.io.source.PdfTokenizer;
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
 * @author Paulo Soares
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
                if (ob.toString().equals("BI")) {
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
                throw new PdfException(PdfException.UnexpectedEndOfFile);
            if (tokeniser.getTokenType() == PdfTokenizer.TokenType.EndDic)
                break;
            if (tokeniser.getTokenType() != PdfTokenizer.TokenType.Name)
                tokeniser.throwError(PdfException.DictionaryKey1IsNotAName, tokeniser.getStringValue());
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
            if (!obj.isArray() && tokeniser.getTokenType() == PdfTokenizer.TokenType.EndArray)
                break;
            if (tokeniser.getTokenType() == PdfTokenizer.TokenType.EndDic && obj.getType() != PdfObject.DICTIONARY)
                tokeniser.throwError(PdfException.UnexpectedGtGt);
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
