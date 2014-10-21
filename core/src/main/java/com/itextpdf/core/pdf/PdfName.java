package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.ByteBuffer;
import com.itextpdf.basics.io.OutputStream;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

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

    public static final PdfName Action = createDirectName("Action");
    public static final PdfName Author = createDirectName("Author");
    public static final PdfName BaseFont = createDirectName("BaseFont");
    public static final PdfName Catalog = createDirectName("Catalog");
    public static final PdfName Contents = createDirectName("Contents");
    public static final PdfName Count = createDirectName("Count");
    public static final PdfName Creator = createDirectName("Creator");
    public static final PdfName D = createDirectName("D");
    public static final PdfName Encoding = createDirectName("Encoding");
    public static final PdfName Extends = createDirectName("Extends");
    public static final PdfName F = createDirectName("F");
    public static final PdfName First = createDirectName("First");
    public static final PdfName Fit = createDirectName("Fit");
    public static final PdfName FitB = createDirectName("FitB");
    public static final PdfName FitBH = createDirectName("FitBH");
    public static final PdfName FitBV = createDirectName("FitBV");
    public static final PdfName FitH = createDirectName("FitH");
    public static final PdfName FitR = createDirectName("FitR");
    public static final PdfName FitV = createDirectName("FitV");
    public static final PdfName Font = createDirectName("Font");
    public static final PdfName GoTo = createDirectName("GoTo");
    public static final PdfName GoToR = createDirectName("GoToR");
    public static final PdfName CreationDate = createDirectName("CreationDate");
    public static final PdfName ID = createDirectName("ID");
    public static final PdfName Index = createDirectName("Index");
    public static final PdfName Info = createDirectName("Info");
    public static final PdfName IsMap = createDirectName("IsMap");
    public static final PdfName Keywords = createDirectName("Keywords");
    public static final PdfName Kids = createDirectName("Kids");
    public static final PdfName Length = createDirectName("Length");
    public static final PdfName MediaBox = createDirectName("MediaBox");
    public static final PdfName Metadata = createDirectName("Metadata");
    public static final PdfName ModDate = createDirectName("ModDate");
    public static final PdfName N = createDirectName("N");
    public static final PdfName Name = createDirectName("Name");
    public static final PdfName NewWindow = createDirectName("NewWindow");
    public static final PdfName Next = createDirectName("Next");
    public static final PdfName ObjStm = createDirectName("ObjStm");
    public static final PdfName Page = createDirectName("Page");
    public static final PdfName Pages = createDirectName("Pages");
    public static final PdfName Parent = createDirectName("Parent");
    public static final PdfName Prev = createDirectName("Prev");
    public static final PdfName Producer = createDirectName("Producer");
    public static final PdfName Resources = createDirectName("Resources");
    public static final PdfName Root = createDirectName("Root");
    public static final PdfName S = createDirectName("S");
    public static final PdfName Subtype = createDirectName("Subtype");
    public static final PdfName Size = createDirectName("Size");
    public static final PdfName Subject = createDirectName("Subject");
    public static final PdfName Title = createDirectName("Title");
    public static final PdfName Type = createDirectName("Type");
    public static final PdfName Type1 = createDirectName("Type1");
    public static final PdfName URI = createDirectName("URI");
    public static final PdfName W = createDirectName("W");
    public static final PdfName WinAnsiEncoding = createDirectName("WinAnsiEncoding");
    public static final PdfName XML = createDirectName("XML");
    public static final PdfName XRef = createDirectName("XRef");
    public static final PdfName XRefStm = createDirectName("XRefStm");
    public static final PdfName XYZ = createDirectName("XYZ");

    protected String value = null;

    /**
     * map strings to all known static names
     */
    public static Map<String, PdfName> staticNames;

    /**
     * Use reflection to cache all the static public final names so
     * future <code>PdfName</code> additions don't have to be "added twice".
     * A bit less efficient (around 50ms spent here on a 2.2ghz machine),
     *  but Much Less error prone.
     */

    static {
        Field fields[] = PdfName.class.getDeclaredFields();
        staticNames = new HashMap<String, PdfName>( fields.length );
        final int flags = Modifier.STATIC | Modifier.PUBLIC | Modifier.FINAL;
        try {
            for (int fldIdx = 0; fldIdx < fields.length; ++fldIdx) {
                Field curFld = fields[fldIdx];
                if ((curFld.getModifiers() & flags) == flags &&
                        curFld.getType().equals( PdfName.class )) {
                    PdfName name = (PdfName)curFld.get(null);
                    staticNames.put(name.getValue(), name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static PdfName createDirectName(String name){
        return new PdfName(name, true);
    }

    public PdfName(String value) {
        super();
        this.value = value;
    }

    private PdfName(String value, boolean directOnly) {
        super(directOnly);
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
        return PdfObject.Name;
    }

    public String getValue() {
        if (value == null)
            generateValue();
        return value;
    }

    @Override
    public int compareTo(PdfName o) {
        return getValue().compareTo(o.getValue());
    }

    protected void generateValue() {
        StringBuilder buf = new StringBuilder();
        try {
            for (int k = 0; k < content.length; ++k) {
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
