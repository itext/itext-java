package com.itextpdf.io.font.cmap;

public class CMapObject {

    protected static final int String = 1;
    protected static final int HexString = 2;
    protected static final int Name = 3;
    protected static final int Number = 4;
    protected static final int Literal = 5;
    protected static final int Array = 6;
    protected static final int Dictionary = 7;
    protected static final int Token = 8;

    private int type;
    private Object value;


    public CMapObject(int objectType, Object value) {
        this.type = objectType;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public int getType() {
        return type;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isString() {
        return type == String || type == HexString;
    }

    public boolean isHexString() {
        return type == HexString;
    }

    public boolean isName() {
        return type == Name;
    }

    public boolean isNumber() {
        return type == Number;
    }

    public boolean isLiteral() {
        return type == Literal;
    }

    public boolean isArray() {
        return type == Array;
    }

    public boolean isDictionary() {
        return type == Dictionary;
    }

    public boolean isToken() {
        return type == Token;
    }

    /**
     * Return String representation of {@code value} field.
     */
    @Override
    public String toString() {
        if (type == String || type == HexString) {
            byte[] content = (byte[]) value;
            StringBuilder str = new StringBuilder(content.length);
            for (byte b: content) {
                str.append((char)(b & 0xff));
            }
            return str.toString();
        }
        return value.toString();
    }
}
