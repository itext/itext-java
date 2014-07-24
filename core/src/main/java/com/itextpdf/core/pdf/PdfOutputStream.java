package com.itextpdf.core.pdf;

import com.itextpdf.core.pdf.objects.*;
import com.itextpdf.io.streams.OutputStream;

import java.io.IOException;
import java.util.Map;

public class PdfOutputStream extends OutputStream {

    public PdfOutputStream(java.io.OutputStream outputStream) {
        super(outputStream);
    }

    public void write(PdfObject object) throws IOException {
        switch (object.getType()) {
            case PdfObject.Array:
                write((PdfArray) object);
                break;
            case PdfObject.Boolean:
                write((PdfBoolean) object);
                break;
            case PdfObject.Dictionary:
                write((PdfDictionary) object);
                break;
            case PdfObject.IndirectReference:
                write((PdfIndirectReference) object);
                break;
            case PdfObject.Name:
                write((PdfName) object);
                break;
            case PdfObject.Number:
                write((PdfNumber) object);
                break;
            case PdfObject.Stream:
                write((PdfStream) object);
                break;
            case PdfObject.String:
                write((PdfString) object);
                break;
            default:
                break;
        }
    }

    protected void write(PdfArray array) throws IOException {
        writeChar('[');
        for (int i = 0; i < array.size(); i++) {
            PdfObject value = array.get(i);
            PdfIndirectReference indirectReference = value.getIndirectReference();
            if (indirectReference != null) {
                write(indirectReference);
            } else {
                write(value);
            }
            if (i < array.size() - 1)
                writeChar(' ');
        }
        writeChar(']');
    }

    protected void write(PdfBoolean bool) throws IOException {
        writeBoolean(bool.getValue());
    }

    protected void write(PdfDictionary dictionary) throws IOException {
        writeString("<<");
        for (Map.Entry<PdfName, PdfObject> entry : dictionary.entrySet()) {
            write(entry.getKey());
            writeChar(' ');
            PdfObject value = entry.getValue();
            PdfIndirectReference indirectReference = value.getIndirectReference();
            if (indirectReference != null) {
                write(indirectReference);
            } else {
                write(value);
            }
        }
        writeString(">>");
    }

    protected void write(PdfIndirectReference indirectReference) throws IOException {
        writeInteger(indirectReference.getObjNr()).
                writeChar(' ').
                writeInteger(indirectReference.getGenNr()).
                writeString(" R");
    }

    protected void write(PdfName name) throws IOException {
        writeChar('/').writeString(name.getValue());
    }

    protected void write(PdfNumber number) throws IOException {
        write(number.getContent());
    }

    protected void write(PdfStream stream) throws IOException {
        byte[] bytes = stream.getBytes();
        stream.put(PdfName.Length, new PdfNumber(bytes.length));
        write((PdfDictionary) stream);
        writeString("stream\n").
                writeBytes(bytes).
                writeString("\nendstream");
    }

    protected void write(PdfString string) throws IOException {
        writeChar('(').
                writeString(string.getValue()).
                writeChar(')');
    }

}
