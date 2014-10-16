package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;
import com.itextpdf.io.streams.ras.RandomAccessFileOrArray;
import com.itextpdf.io.streams.ras.RandomAccessSource;
import com.itextpdf.io.streams.ras.RandomAccessSourceFactory;
import com.itextpdf.io.streams.ras.WindowRandomAccessSource;

import java.io.IOException;
import java.io.InputStream;

public class PdfReader {

    protected PdfTokeniser tokens;
    protected char pdfVersion;
    protected long lastXref;
    protected long eofPos;
    protected PdfDictionary trailer;
    protected PdfDocument pdfDocument;

    /**
     * Streams are closed automatically.
     */
    protected boolean closeStream = true;

    public PdfReader(InputStream is) throws IOException, PdfException {
        this(new RandomAccessSourceFactory().createSource(is));
    }

    /**
     * Constructs a new PdfReader.  This is the master constructor.
     *
     * @param byteSource source of bytes for the reader
     *                   TODO param closeSourceOnConstructorError if true, the byteSource will be closed if there is an error during construction of this reader
     */
    public PdfReader(RandomAccessSource byteSource) throws IOException, PdfException {
        tokens = getOffsetTokeniser(byteSource);
    }

    /**
     * Parses the entire PDF
     */
    protected void readPdf() throws IOException, PdfException {
        readXref();
    }

    protected PdfObject readObject(PdfIndirectReference reference) throws PdfException {
        try {
            tokens.seek(reference.getOffset());
            tokens.nextValidToken();
            if (tokens.getTokenType() != PdfTokeniser.TokenType.Obj
                    || tokens.getObjNr() != reference.getObjNr()
                    || tokens.getGenNr() != reference.getGenNr()) {
                tokens.throwError(PdfException.InvalidOffsetForObject1, reference.toString());
            }
            return readObject(false).setIndirectReference(reference);
        } catch (IOException e) {
            throw new PdfException(PdfException.CannotReadPdfObject, e);
        }
    }

    protected PdfObject readObject(boolean readAsDirect) throws IOException, PdfException {
        tokens.nextValidToken();
        PdfTokeniser.TokenType type = tokens.getTokenType();
        switch (type) {
            case StartDic: {
                PdfDictionary dic = readDictionary();
                long pos = tokens.getFilePointer();
                // be careful in the trailer. May not be a "next" token.
                boolean hasNext;
                do {
                    hasNext = tokens.nextToken();
                } while (hasNext && tokens.getTokenType() == PdfTokeniser.TokenType.Comment);

                if (hasNext && tokens.tokenValueEqualsTo(PdfTokeniser.Stream)) {
                    //skip whitespaces
                    int ch;
                    do {
                        ch = tokens.read();
                    } while (ch == 32 || ch == 9 || ch == 0 || ch == 12);
                    if (ch != '\n')
                        ch = tokens.read();
                    if (ch != '\n')
                        tokens.backOnePosition(ch);
                    PdfStream stream = new PdfStream(pdfDocument);
                    stream.putAll(dic);
                    return stream;
                } else {
                    tokens.seek(pos);
                    return dic;
                }
            }
            case StartArray:
                return readArray();
            case Number:
                return new PdfNumber(tokens.getByteContent());
            case String:
                return new PdfString(tokens.getByteContent()).setHexWriting(tokens.isHexString());
            case Name:
                return getPdfName(readAsDirect);
            case Ref:
                int num = tokens.getObjNr();
                PdfXRefTable table = pdfDocument.getXRef();
                if (table.get(num) != null) {
                    return table.get(num);
                } else {
                    PdfIndirectReference ref = new PdfIndirectReference(pdfDocument, num, tokens.getGenNr(), -1);
                    table.add(ref);
                    return ref;
                }
            case EndOfFile:
                throw new PdfException(PdfException.UnexpectedEndOfFile);
            default:
                if (tokens.tokenValueEqualsTo(PdfTokeniser.Null)) {
                    if (readAsDirect) {
                        return PdfNull.PdfNull;
                    } else {
                        return new PdfNull();
                    }
                } else if (tokens.tokenValueEqualsTo(PdfTokeniser.True)) {
                    if (readAsDirect) {
                        return PdfBoolean.PdfTrue;
                    } else {
                        return new PdfBoolean(true);
                    }
                } else if (tokens.tokenValueEqualsTo(PdfTokeniser.False)) {
                    if (readAsDirect) {
                        return PdfBoolean.PdfFalse;
                    } else {
                        return new PdfBoolean(false);
                    }
                }
                return null;
        }
    }

    protected PdfName getPdfName(boolean readAsDirect) {
        if (readAsDirect) {
            PdfName cachedName = PdfName.staticNames.get(tokens.getStringValue());
            if (cachedName != null)
                return cachedName;
        }
        // an indirect name (how odd...), or a non-standard one
        return new PdfName(tokens.getByteContent());
    }

    protected PdfDictionary readDictionary() throws IOException, PdfException {
        PdfDictionary dic = new PdfDictionary();
        while (true) {
            tokens.nextValidToken();
            if (tokens.getTokenType() == PdfTokeniser.TokenType.EndDic)
                break;
            if (tokens.getTokenType() != PdfTokeniser.TokenType.Name)
                tokens.throwError(PdfException.DictionaryKey1IsNotAName, tokens.getStringValue());
            PdfName name = getPdfName(true);
            PdfObject obj = readObject(true);
            if (obj == null) {
                if (tokens.getTokenType() == PdfTokeniser.TokenType.EndDic)
                    tokens.throwError(PdfException.UnexpectedGtGt);
                if (tokens.getTokenType() == PdfTokeniser.TokenType.EndArray)
                    tokens.throwError(PdfException.UnexpectedCloseBracket);
            }
            dic.put(name, obj);
        }
        return dic;
    }

    protected PdfArray readArray() throws IOException, PdfException {
        PdfArray array = new PdfArray();
        while (true) {
            PdfObject obj = readObject(true);
            if (obj == null) {
                if (tokens.getTokenType() == PdfTokeniser.TokenType.EndArray)
                    break;
                if (tokens.getTokenType() == PdfTokeniser.TokenType.EndDic)
                    tokens.throwError(PdfException.UnexpectedGtGt);
            }
            array.add(obj);
        }
        return array;
    }

    /**
     * Utility method that checks the provided byte source to see if it has junk bytes at the beginning.  If junk bytes
     * are found, construct a tokeniser that ignores the junk.  Otherwise, construct a tokeniser for the byte source as it is
     *
     * @param byteSource the source to check
     * @return a tokeniser that is guaranteed to start at the PDF header
     * @throws IOException if there is a problem reading the byte source
     */
    private static PdfTokeniser getOffsetTokeniser(RandomAccessSource byteSource) throws IOException, PdfException {
        PdfTokeniser tok = new PdfTokeniser(new RandomAccessFileOrArray(byteSource));
        int offset = tok.getHeaderOffset();
        if (offset != 0) {
            RandomAccessSource offsetSource = new WindowRandomAccessSource(byteSource, offset);
            tok = new PdfTokeniser(new RandomAccessFileOrArray(offsetSource));
        }
        return tok;
    }

    protected void readXref() throws IOException, PdfException {
        tokens.seek(tokens.getStartxref());
        tokens.nextToken();
        if (!tokens.tokenValueEqualsTo(PdfTokeniser.Startxref))
            throw new PdfException(PdfException.PdfStartxrefNotFound, tokens);
        tokens.nextToken();
        if (tokens.getTokenType() != PdfTokeniser.TokenType.Number)
            throw new PdfException(PdfException.PdfStartxrefIsNotFollowedByANumber, tokens);
        long startxref = tokens.longValue();
        lastXref = startxref;
        eofPos = tokens.getFilePointer();
        //TODO Read XRef Stream
        //readXRefStream(startxref)
        tokens.seek(startxref);
        trailer = readXrefSection();
        //  Prev key - integer value
        //  (Present only if the file has more than one cross-reference section; shall be an indirect reference)
        // The byte offset in the decoded stream from the beginning of the file
        // to the beginning of the previous cross-reference section.
        PdfDictionary trailer2 = trailer;
        while (true) {
            PdfNumber prev = (PdfNumber) trailer2.get(PdfName.Prev);
            if (prev == null)
                break;
            tokens.seek(prev.getLongValue());
            trailer2 = readXrefSection();
        }
    }

    protected PdfDictionary readXrefSection() throws IOException, PdfException {
        tokens.nextValidToken();
        if (!tokens.tokenValueEqualsTo(PdfTokeniser.Xref))
            tokens.throwError(PdfException.XrefSubsectionNotFound);
        PdfXRefTable xref = pdfDocument.getXRef();
        while (true) {
            tokens.nextValidToken();
            if (tokens.tokenValueEqualsTo(PdfTokeniser.Trailer))
                break;
            if (tokens.getTokenType() != PdfTokeniser.TokenType.Number)
                tokens.throwError(PdfException.ObjectNumberOfTheFirstObjectInThisXrefSubsectionNotFound);
            int start = tokens.intValue();
            tokens.nextValidToken();
            if (tokens.getTokenType() != PdfTokeniser.TokenType.Number)
                tokens.throwError(PdfException.NumberOfEntriesInThisXrefSubsectionNotFound);
            int end = tokens.intValue() + start;
            for (int num = start; num < end; ++num) {
                tokens.nextValidToken();
                int pos = tokens.intValue();
                tokens.nextValidToken();
                int gen = tokens.intValue();
                tokens.nextValidToken();
                PdfIndirectReference reference = xref.get(num);
                if (reference == null) {
                    reference = new PdfIndirectReference(pdfDocument, num, gen, pos);
                } else if (reference.getOffset() == -1 && reference.getGenNr() == gen) {
                    reference.setOffset(pos);
                } else {
                    tokens.throwError(PdfException.XrefTableDoesntHaveSuitableItemForObject1, reference.toString());
                }
                if (tokens.tokenValueEqualsTo(PdfTokeniser.N)) {
                    if (xref.get(num) == null) {
                        if (pos == 0)
                            tokens.throwError(PdfException.FilePosition0CrossReferenceEntryInThisXrefSubsection);
                        xref.add(reference);
                    }
                } else if (tokens.tokenValueEqualsTo(PdfTokeniser.F)) {
                    reference.setOffset(0);
                    if (xref.get(num) == null)
                        xref.add(reference);
                } else
                    tokens.throwError(PdfException.InvalidCrossReferenceEntryInThisXrefSubsection);
            }
        }
        PdfDictionary trailer = (PdfDictionary) readObject(false);
        PdfNumber xrefSize = (PdfNumber) trailer.get(PdfName.Size);
        xref.setCapacity(xrefSize.getIntValue());

        PdfObject xrs = trailer.get(PdfName.XRefStm);
//        if (xrs != null && xrs.getType() == PdfObject.Number) {
//            //xref stream
//        }
        return trailer;
    }

    protected void setPdfDocument(PdfDocument pdfDocument) {
        this.pdfDocument = pdfDocument;
    }

    public void close() {

    }

    public boolean isCloseStream() {
        return closeStream;
    }

    public void setCloseStream(boolean closeStream) {
        this.closeStream = closeStream;
    }
}
