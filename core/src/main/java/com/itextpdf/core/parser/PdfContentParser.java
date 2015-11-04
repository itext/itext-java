package com.itextpdf.core.parser;

import com.itextpdf.basics.io.PdfTokenizer;
import com.itextpdf.core.pdf.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Parses the page or template content.
 * @author Paulo Soares
 */
public class PdfContentParser {

    /**
     * Commands have this type.
     */
    public static final int COMMAND_TYPE = 100;
    /**
     * Holds value of property tokeniser.
     */
    private PdfTokenizer tokeniser;

    /**
     * Creates a new instance of PdfContentParser
     * @param tokeniser the tokeniser with the content
     */
    public PdfContentParser(PdfTokenizer tokeniser) {
        this.tokeniser = tokeniser;
    }

    /**
     * Parses a single command from the content. Each command is output as an array of arguments
     * having the command itself as the last element. The returned array will be empty if the
     * end of content was reached.
     * @param ls an <CODE>ArrayList</CODE> to use. It will be cleared before using. If it's
     * <CODE>null</CODE> will create a new <CODE>ArrayList</CODE>
     * @return the same <CODE>ArrayList</CODE> given as argument or a new one
     * @throws IOException on error
     */
    public ArrayList<PdfObject> parse(ArrayList<PdfObject> ls) throws IOException {
        if (ls == null)
            ls = new ArrayList<PdfObject>();
        else
            ls.clear();
        PdfObject ob = null;
        while ((ob = readPRObject()) != null) {
            ls.add(ob);
            if (ob.getType() == COMMAND_TYPE)
                break;
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
                throw new IOException(/*MessageLocalization.getComposedMessage(*/"unexpected.end.of.file"/*)*/); // TODO: fix the message localization
            if (tokeniser.getTokenType() == PdfTokenizer.TokenType.EndDic)
                break;
            if (tokeniser.getTokenType() == PdfTokenizer.TokenType.Other && "def".equals(tokeniser.getStringValue()))
                continue;
            if (tokeniser.getTokenType() != PdfTokenizer.TokenType.Name)
                throw new IOException(/*MessageLocalization.getComposedMessage(*/"dictionary.key.1.is.not.a.name"/*, tokeniser.getStringValue())*/); // TODO: fix the message localization
            PdfName name = new PdfName(tokeniser.getStringValue());
            PdfObject obj = readPRObject();
            int type = obj.getType();
            if (-type == PdfTokenizer.TokenType.EndDic.ordinal())
                throw new IOException(/*MessageLocalization.getComposedMessage(*/"unexpected.gt.gt"/*)*/); // TODO: fix the message localization
            if (-type == PdfTokenizer.TokenType.EndArray.ordinal())
                throw new IOException(/*MessageLocalization.getComposedMessage(*/"unexpected.close.bracket"/*)*/); // TODO: fix the message localization
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
            PdfObject obj = readPRObject();
            int type = obj.getType();
            if (-type == PdfTokenizer.TokenType.EndArray.ordinal())
                break;
            if (-type == PdfTokenizer.TokenType.EndDic.ordinal())
                throw new IOException(/*MessageLocalization.getComposedMessage(*/"unexpected.gt.gt"/*)*/);
            array.add(obj);
        }
        return array;
    }

    /**
     * Reads a pdf object.
     * @return the pdf object
     * @throws IOException on error
     */
    public PdfObject readPRObject() throws IOException {
        if (!nextValidToken())
            return null;
        final PdfTokenizer.TokenType type = tokeniser.getTokenType();
        final int ordinal = type.ordinal();
        switch (type) {
            case StartDic: {
                PdfDictionary dic = readDictionary();
                return dic;
            }
            case StartArray:
                return readArray();
            case String:
                PdfString str = new PdfString(tokeniser.getStringValue(), null).setHexWriting(tokeniser.isHexString());
                return str;
            case Name:
                return new PdfName(tokeniser.getStringValue());
            case Number:
                return new PdfNumber(Double.parseDouble(tokeniser.getStringValue()));
            case Other:
                return new PdfLiteral(tokeniser.getStringValue()) {
                    @Override
                    public int getType() {
                        return COMMAND_TYPE;
                    }
                }; // TODO: correct this
            default:
                return new PdfLiteral(tokeniser.getStringValue()) {
                    @Override
                    public int getType() {
                        return -ordinal;
                    } // TODO: correct this
                };
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