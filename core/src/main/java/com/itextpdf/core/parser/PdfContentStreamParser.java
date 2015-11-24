package com.itextpdf.core.parser;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.PdfTokenizer;
import com.itextpdf.core.pdf.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Parses the page or form XObject content.
 * @author Paulo Soares
 */
public class PdfContentStreamParser {

    /**
     * Commands have this type.
     */
    public static final int COMMAND_TYPE = 100;
    /**
     * Holds value of property tokeniser.
     */
    private PdfTokenizer tokeniser;

    private PdfDictionary currentResources;

    /**
     * Creates a new instance of PdfContentParser
     * @param tokeniser the tokeniser with the content
     */
    public PdfContentStreamParser(PdfTokenizer tokeniser) {
        this.tokeniser = tokeniser;
    }

    /**
     * Creates a new instance of PdfContentParser
     * @param tokeniser the tokeniser with the content
     * @param currentResources current resources of the content stream.
     *                         It is optional parameter, which is used for performance improvements of specific cases of
     *                         inline images parsing.
     */
    public PdfContentStreamParser(PdfTokenizer tokeniser, PdfDictionary currentResources) {
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
    public ArrayList<PdfObject> parse(ArrayList<PdfObject> ls) throws IOException {
        if (ls == null)
            ls = new ArrayList<PdfObject>();
        else
            ls.clear();
        PdfObject ob = null;
        while ((ob = readObject()) != null) {
            ls.add(ob);
            if (ob.getType() == COMMAND_TYPE) {
                if (ob.toString().equals("BI")) {
                    PdfStream inlineImageAsStream = InlineImageParsingUtils.parse(this, currentResources);
                    ls.clear();
                    ls.add(inlineImageAsStream);
                    ls.add(new PdfLiteral("EI")); //TODO
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
            int type = obj.getType(); //TODO see pdfReader and try to do the same
            if (-type == PdfTokenizer.TokenType.EndDic.ordinal())
                tokeniser.throwError(PdfException.UnexpectedGtGt);
            if (-type == PdfTokenizer.TokenType.EndArray.ordinal())
                tokeniser.throwError(PdfException.UnexpectedCloseBracket);
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
    public PdfObject readObject() throws IOException {
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
                return new PdfName(tokeniser.getByteContent());
            case Number:
                //TODO would be nice to use PdfNumber(byte[]) here, as in this case number parsing won't happen until it's needed.
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