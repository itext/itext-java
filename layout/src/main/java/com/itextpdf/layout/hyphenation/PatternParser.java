/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itextpdf.layout.hyphenation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.xml.parsers.SAXParserFactory;

import com.itextpdf.io.util.ResourceUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>A SAX document handler to read and parse hyphenation patterns
 * from a XML file.</p>
 *
 * <p>This work was authored by Carlos Villegas (cav@uniscope.co.jp).</p>
 */
public class PatternParser extends DefaultHandler {

    private XMLReader parser;
    private int currElement;
    private IPatternConsumer consumer;
    private StringBuilder token;
    private ArrayList exception;
    private char hyphenChar;
    private String errMsg;
    private boolean hasClasses;

    static final int ELEM_CLASSES = 1;
    static final int ELEM_EXCEPTIONS = 2;
    static final int ELEM_PATTERNS = 3;
    static final int ELEM_HYPHEN = 4;

    /**
     * Construct a pattern parser.
     * @throws HyphenationException if a hyphenation exception is raised
     */
    private PatternParser() throws HyphenationException {
        token = new StringBuilder();
        parser = createParser();
        parser.setContentHandler(this);
        parser.setErrorHandler(this);
        hyphenChar = '-';    // default
    }

    /**
     * Construct a pattern parser.
     * @param consumer a pattern consumer
     * @throws HyphenationException if a hyphenation exception is raised
     */
    public PatternParser(IPatternConsumer consumer) throws HyphenationException {
        this();
        this.consumer = consumer;
    }

    /**
     * Parses a hyphenation pattern file.
     * @param filename the filename
     * @throws HyphenationException In case of an exception while parsing
     */
    public void parse(String filename) throws HyphenationException, FileNotFoundException {
        parse(new FileInputStream(filename), filename);
    }

    /**
     * Parses a hyphenation pattern file.
     * @param stream the InputStream for the file
     * @param name unique key representing country-language combination
     *
     * @throws HyphenationException In case of an exception while parsing
     */
    public void parse(InputStream stream, String name) throws HyphenationException {
        InputSource source = new InputSource(stream);
        source.setSystemId(name);
        try {
            parser.parse(source);
        } catch (FileNotFoundException fnfe) {
            throw new HyphenationException("File not found: " + fnfe.getMessage());
        } catch (IOException ioe) {
            throw new HyphenationException(ioe.getMessage());
        } catch (SAXException e) {
            throw new HyphenationException(errMsg);
        }
    }

    /**
     * Creates a SAX parser using JAXP
     * @return the created SAX parser
     */
    static XMLReader createParser() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            return factory.newSAXParser().getXMLReader();
        } catch (Exception e) {
            throw new RuntimeException("Couldn't create XMLReader: " + e.getMessage());
        }
    }

    private String readToken(StringBuilder chars) {
        String word;
        boolean space = false;
        int i;
        for (i = 0; i < chars.length(); i++) {
            if (Character.isWhitespace(chars.charAt(i))) {
                space = true;
            } else {
                break;
            }
        }
        if (space) {
            // chars.delete(0,i);
            for (int countr = i; countr < chars.length(); countr++) {
                chars.setCharAt(countr - i, chars.charAt(countr));
            }
            chars.setLength(chars.length() - i);
            if (token.length() > 0) {
                word = token.toString();
                token.setLength(0);
                return word;
            }
        }
        space = false;
        for (i = 0; i < chars.length(); i++) {
            if (Character.isWhitespace(chars.charAt(i))) {
                space = true;
                break;
            }
        }
        token.append(chars.toString().substring(0, i));
        // chars.delete(0,i);
        for (int countr = i; countr < chars.length(); countr++) {
            chars.setCharAt(countr - i, chars.charAt(countr));
        }
        chars.setLength(chars.length() - i);
        if (space) {
            word = token.toString();
            token.setLength(0);
            return word;
        }
        token.append(chars);
        return null;
    }

    private static String getPattern(String word) {
        StringBuilder pat = new StringBuilder();
        int len = word.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isDigit(word.charAt(i))) {
                pat.append(word.charAt(i));
            }
        }
        return pat.toString();
    }

    private ArrayList normalizeException(ArrayList ex) {
        ArrayList res = new ArrayList();
        for (int i = 0; i < ex.size(); i++) {
            Object item = ex.get(i);
            if (item instanceof String) {
                String str = (String)item;
                StringBuilder buf = new StringBuilder();
                for (int j = 0; j < str.length(); j++) {
                    char c = str.charAt(j);
                    if (c != hyphenChar) {
                        buf.append(c);
                    } else {
                        res.add(buf.toString());
                        buf.setLength(0);
                        char[] h = new char[1];
                        h[0] = hyphenChar;
                        // we use here hyphenChar which is not necessarily
                        // the one to be printed
                        res.add(new Hyphen(new String(h), null, null));
                    }
                }
                if (buf.length() > 0) {
                    res.add(buf.toString());
                }
            } else {
                res.add(item);
            }
        }
        return res;
    }

    private String getExceptionWord(ArrayList ex) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < ex.size(); i++) {
            Object item = ex.get(i);
            if (item instanceof String) {
                res.append((String)item);
            } else {
                if (((Hyphen)item).noBreak != null) {
                    res.append(((Hyphen)item).noBreak);
                }
            }
        }
        return res.toString();
    }

    private static String getInterletterValues(String pat) {
        StringBuilder il = new StringBuilder();
        String word = pat + "a";    // add dummy letter to serve as sentinel
        int len = word.length();
        for (int i = 0; i < len; i++) {
            char c = word.charAt(i);
            if (Character.isDigit(c)) {
                il.append(c);
                i++;
            } else {
                il.append('0');
            }
        }
        return il.toString();
    }

    /** @throws SAXException if not caught */
    protected void getExternalClasses() throws SAXException {
        XMLReader mainParser = parser;
        parser = createParser();
        parser.setContentHandler(this);
        parser.setErrorHandler(this);
        InputStream stream = ResourceUtil.getResourceStream(HyphenationConstants.HYPHENATION_DEFAULT_RESOURCE + "external/classes.xml");
        InputSource source = new InputSource(stream);
        try {
            parser.parse(source);
        } catch (IOException ioe) {
            throw new SAXException(ioe.getMessage());
        } finally {
            parser = mainParser;
        }
    }

    //
    // ContentHandler methods
    //

    /**
     * {@inheritDoc}
     * @throws SAXException
     */
    public void startElement(String uri, String local, String raw,
                             Attributes attrs) throws SAXException {
        if (local.equals("hyphen-char")) {
            String h = attrs.getValue("value");
            if (h != null && h.length() == 1) {
                hyphenChar = h.charAt(0);
            }
        } else if (local.equals("classes")) {
            currElement = ELEM_CLASSES;
        } else if (local.equals("patterns")) {
            if (!hasClasses) {
                getExternalClasses();
            }
            currElement = ELEM_PATTERNS;
        } else if (local.equals("exceptions")) {
            if (!hasClasses) {
                getExternalClasses();
            }
            currElement = ELEM_EXCEPTIONS;
            exception = new ArrayList();
        } else if (local.equals("hyphen")) {
            if (token.length() > 0) {
                exception.add(token.toString());
            }
            exception.add(new Hyphen(attrs.getValue("pre"),
                                            attrs.getValue("no"),
                                            attrs.getValue("post")));
            currElement = ELEM_HYPHEN;
        }
        token.setLength(0);
    }

    /**
     * {@inheritDoc}
     */
    public void endElement(String uri, String local, String raw) {

        if (token.length() > 0) {
            String word = token.toString();
            switch (currElement) {
            case ELEM_CLASSES:
                consumer.addClass(word);
                break;
            case ELEM_EXCEPTIONS:
                exception.add(word);
                exception = normalizeException(exception);
                consumer.addException(getExceptionWord(exception),
                                      (ArrayList)exception.clone());
                break;
            case ELEM_PATTERNS:
                consumer.addPattern(getPattern(word),
                                    getInterletterValues(word));
                break;
            case ELEM_HYPHEN:
                // nothing to do
                break;
            default:
                break;
            }
            if (currElement != ELEM_HYPHEN) {
                token.setLength(0);
            }
        }
        if (currElement == ELEM_CLASSES) {
            hasClasses = true;
        }
        if (currElement == ELEM_HYPHEN) {
            currElement = ELEM_EXCEPTIONS;
        } else {
            currElement = 0;
        }

    }

    /**
     * {@inheritDoc}
     */
    public void characters(char[] ch, int start, int length) {
        StringBuilder chars = new StringBuilder(length);
        chars.append(ch, start, length);
        String word = readToken(chars);
        while (word != null) {
            // System.out.println("\"" + word + "\"");
            switch (currElement) {
            case ELEM_CLASSES:
                consumer.addClass(word);
                break;
            case ELEM_EXCEPTIONS:
                exception.add(word);
                exception = normalizeException(exception);
                consumer.addException(getExceptionWord(exception),
                                      (ArrayList)exception.clone());
                exception.clear();
                break;
            case ELEM_PATTERNS:
                consumer.addPattern(getPattern(word),
                                    getInterletterValues(word));
                break;
            default:
                break;
            }
            word = readToken(chars);
        }

    }

    //
    // ErrorHandler methods
    //

    /**
     * {@inheritDoc}
     */
    public void warning(SAXParseException ex) {
        errMsg = "[Warning] " + getLocationString(ex) + ": "
                 + ex.getMessage();
    }

    /**
     * {@inheritDoc}
     */
    public void error(SAXParseException ex) {
        errMsg = "[Error] " + getLocationString(ex) + ": " + ex.getMessage();
    }

    /**
     * {@inheritDoc}
     */
    public void fatalError(SAXParseException ex) throws SAXException {
        errMsg = "[Fatal Error] " + getLocationString(ex) + ": "
                 + ex.getMessage();
        throw ex;
    }

    /**
     * Returns a string of the location.
     */
    private String getLocationString(SAXParseException ex) {
        StringBuilder str = new StringBuilder();

        String systemId = ex.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1) {
                systemId = systemId.substring(index + 1);
            }
            str.append(systemId);
        }
        str.append(':');
        str.append(ex.getLineNumber());
        str.append(':');
        str.append(ex.getColumnNumber());

        return str.toString();

    }    // getLocationString(SAXParseException):String


}
