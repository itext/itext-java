package com.itextpdf.styledxmlparser;

public class ITextSharpCustomMembers {
    public static final String getEnumerator;

    static {
        getEnumerator =
                "        /// <summary><inheritDoc/></summary>\n" +
                        "        IEnumerator IEnumerable.GetEnumerator() {\n" +
                        "            return GetEnumerator();\n" +
                        "        }\n";
    }
}
