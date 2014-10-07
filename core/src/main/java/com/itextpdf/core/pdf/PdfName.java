package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;
import com.itextpdf.io.streams.ByteBuffer;
import com.itextpdf.io.streams.OutputStream;

public class PdfName extends PdfPrimitiveObject implements Comparable<PdfName> {

    private static final byte[] space = OutputStream.getIsoBytes("#20");                //  ' '
    private static final byte[] percent = OutputStream.getIsoBytes("#25");              //  '%'
    private static final byte[] leftParenthesis = OutputStream.getIsoBytes("#28");      //  '('
    private static final byte[] rightParenthesis = OutputStream.getIsoBytes("#29");     //  ')'
    private static final byte[] lessThan = OutputStream.getIsoBytes("#3c");             //  '<'
    private static final byte[] greaterThan = OutputStream.getIsoBytes("#3e");          //  '>'
    private static final byte[] leftSquare = OutputStream.getIsoBytes("#5b");           //  '['
    private static final byte[] rightSquare = OutputStream.getIsoBytes("#5d");          //  ']'
    private static final byte[] leftCurlyBracket = OutputStream.getIsoBytes("#7b");     //  '{'
    private static final byte[] rightCurlyBracket = OutputStream.getIsoBytes("#7d");    //  '}'
    private static final byte[] solidus = OutputStream.getIsoBytes("#2f");              //  '/'
    private static final byte[] numberSign = OutputStream.getIsoBytes("#23");           //  '#'

    public static final PdfName Action = new PdfName("Action");
    public static final PdfName Author = new PdfName("Author");
    public static final PdfName BaseFont = new PdfName("BaseFont");
    public static final PdfName Catalog = new PdfName("Catalog");
    public static final PdfName Contents = new PdfName("Contents");
    public static final PdfName Count = new PdfName("Count");
    public static final PdfName Creator = new PdfName("Creator");
    public static final PdfName D = new PdfName("D");
    public static final PdfName Encoding = new PdfName("Encoding");
    public static final PdfName Extends = new PdfName("Extends");
    public static final PdfName F = new PdfName("F");
    public static final PdfName First = new PdfName("First");
    public static final PdfName Fit = new PdfName("Fit");
    public static final PdfName FitB = new PdfName("FitB");
    public static final PdfName FitBH = new PdfName("FitBH");
    public static final PdfName FitBV = new PdfName("FitBV");
    public static final PdfName FitH = new PdfName("FitH");
    public static final PdfName FitR = new PdfName("FitR");
    public static final PdfName FitV = new PdfName("FitV");
    public static final PdfName Font = new PdfName("Font");
    public static final PdfName GoTo = new PdfName("GoTo");
    public static final PdfName GoToR = new PdfName("GoToR");
    public static final PdfName ID = new PdfName("ID");
    public static final PdfName Index = new PdfName("Index");
    public static final PdfName Info = new PdfName("Info");
    public static final PdfName IsMap = new PdfName("IsMap");
    public static final PdfName Keywords = new PdfName("Keywords");
    public static final PdfName Kids = new PdfName("Kids");
    public static final PdfName Length = new PdfName("Length");
    public static final PdfName MediaBox = new PdfName("MediaBox");
    public static final PdfName N = new PdfName("N");
    public static final PdfName NewWindow = new PdfName("NewWindow");
    public static final PdfName Next = new PdfName("Next");
    public static final PdfName ObjStm = new PdfName("ObjStm");
    public static final PdfName Page = new PdfName("Page");
    public static final PdfName Pages = new PdfName("Pages");
    public static final PdfName Parent = new PdfName("Parent");
    public static final PdfName Resources = new PdfName("Resources");
    public static final PdfName Root = new PdfName("Root");
    public static final PdfName S = new PdfName("S");
    public static final PdfName Subtype = new PdfName("Subtype");
    public static final PdfName Size = new PdfName("Size");
    public static final PdfName Subject = new PdfName("Subject");
    public static final PdfName Title = new PdfName("Title");
    public static final PdfName Type = new PdfName("Type");
    public static final PdfName Type1 = new PdfName("Type1");
    public static final PdfName URI = new PdfName("URI");
    public static final PdfName W = new PdfName("W");
    public static final PdfName WinAnsiEncoding = new PdfName("WinAnsiEncoding");
    public static final PdfName XRef = new PdfName("XRef");
    public static final PdfName XYZ = new PdfName("XYZ");

    protected String value = null;

    public PdfName(String value) {
        super();
        this.value = value;
    }

    public PdfName(byte[] content) {
        super(content);
    }

    private PdfName() {
        super();
    }

    @Override
    public byte getType() {
        return Name;
    }

    public String getValue() {
        if (value == null)
            generateValue();
        return value;
    }

    @Override
    public int compareTo(PdfName o) {
        return value.compareTo(o.value);
    }

    protected void generateValue() {
        StringBuilder buf = new StringBuilder();
        try {
            for (int k = 1; k < content.length; ++k) {
                char c = (char)content[k];
                if (c == '#') {
                    byte c1 = content[k + 1];
                    byte c2 = content[k + 2];
                    c = (char)((ByteBuffer.getHex(c1) << 4) + ByteBuffer.getHex(c2));
                    k += 2;
                }
                buf.append(c);
            }
        } catch (IndexOutOfBoundsException e) {
            // empty on purpose
        }
        value = buf.toString();
    }

    @Override
    protected void generateContent() {
        int length = value.length();
        ByteBuffer buf = new ByteBuffer(length + 20);
        buf.append('/');
        char c;
        char chars[] = value.toCharArray();
        for (int k = 0; k < length; k++) {
            c = (char)(chars[k] & 0xff);
            // Escape special characters
            switch (c) {
                case ' ':
                    buf.append(space);
                    break;
                case '%':
                    buf.append(percent);
                    break;
                case '(':
                    buf.append(leftParenthesis);
                    break;
                case ')':
                    buf.append(rightParenthesis);
                    break;
                case '<':
                    buf.append(lessThan);
                    break;
                case '>':
                    buf.append(greaterThan);
                    break;
                case '[':
                    buf.append(leftSquare);
                    break;
                case ']':
                    buf.append(rightSquare);
                    break;
                case '{':
                    buf.append(leftCurlyBracket);
                    break;
                case '}':
                    buf.append(rightCurlyBracket);
                    break;
                case '/':
                    buf.append(solidus);
                    break;
                case '#':
                    buf.append(numberSign);
                    break;
                default:
                    if (c >= 32 && c <= 126)
                        buf.append(c);
                    else {
                        buf.append('#');
                        if (c < 16)
                            buf.append('0');
                        buf.append(Integer.toString(c, 16));
                    }
                    break;
            }
        }
        content = buf.toByteArray();
    }

    @Override
    public String toString() {
        return "/" + getValue();
    }

    @Override
    protected PdfName newInstance() {
        return new PdfName();
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) throws PdfException {
        super.copyContent(from, document);
        PdfName name = (PdfName)from;
        value = name.value;
    }
}
