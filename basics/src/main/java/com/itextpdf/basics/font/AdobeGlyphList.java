package com.itextpdf.basics.font;

import com.itextpdf.basics.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.StringTokenizer;

public class AdobeGlyphList {

    private static HashMap<Integer, String> unicode2names = new HashMap<Integer, String>();
    private static HashMap<String, int[]> names2unicode = new HashMap<String, int[]>();

    static {
        InputStream is = null;
        try {
            is = Utilities.getResourceStream(FontsResourceAnchor.ResourcePath + "AdobeGlyphList.txt", FontsResourceAnchor.class.getClassLoader());
            if (is == null) {
                String msg = "AdobeGlyphList.txt not found as resource. (It must exist as resource in the package com.itextpdf.text.pdf.fonts)";
                throw new Exception(msg);
            }
            byte buf[] = new byte[1024];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while (true) {
                int size = is.read(buf);
                if (size < 0)
                    break;
                out.write(buf, 0, size);
            }
            is.close();
            is = null;
            String s = PdfEncodings.convertToString(out.toByteArray(), null);
            StringTokenizer tk = new StringTokenizer(s, "\r\n");
            while (tk.hasMoreTokens()) {
                String line = tk.nextToken();
                if (line.startsWith("#"))
                    continue;
                StringTokenizer t2 = new StringTokenizer(line, " ;\r\n\t\f");
                String name = null;
                String hex = null;
                if (!t2.hasMoreTokens())
                    continue;
                name = t2.nextToken();
                if (!t2.hasMoreTokens())
                    continue;
                hex = t2.nextToken();
                Integer num = Integer.valueOf(hex, 16);
                unicode2names.put(num, name);
                names2unicode.put(name, new int[]{num.intValue()});
            }
        }
        catch (Exception e) {
            System.err.println("AdobeGlyphList.txt loading error: " + e.getMessage());
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (Exception e) {
                    // empty on purpose
                }
            }
        }
    }

    public static int[] nameToUnicode(String name) {
        int[] v = names2unicode.get(name);
        if (v == null && name.length() == 7 && name.toLowerCase().startsWith("uni")) {
            try {
                return new int[]{Integer.parseInt(name.substring(3), 16)};
            }
            catch (Exception ex) {
            }
        }
        return v;
    }

    public static String unicodeToName(int num) {
        return unicode2names.get(Integer.valueOf(num));
    }
}