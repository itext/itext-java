/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.styledxmlparser.jsoup;

import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.parser.Parser;
import com.itextpdf.styledxmlparser.jsoup.safety.Cleaner;
import com.itextpdf.styledxmlparser.jsoup.safety.Whitelist;
import com.itextpdf.styledxmlparser.jsoup.helper.DataUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 The core public access point to the jsoup functionality.

 @author Jonathan Hedley */
public class Jsoup {
    private Jsoup() {}

    /**
     Parse HTML into a Document. The parser will make a sensible, balanced document tree out of any HTML.

     @param html    HTML to parse
     @param baseUri The URL where the HTML was retrieved from. Used to resolve relative URLs to absolute URLs, that occur
     before the HTML declares a {@code <base href>} tag.
     @return sane HTML
     */
    public static Document parse(String html, String baseUri) {
        return Parser.parse(html, baseUri);
    }


    /**
     Parse HTML into a Document, using the provided Parser. You can provide an alternate parser, such as a simple XML
     (non-HTML) parser.

     @param html    HTML to parse
     @param baseUri The URL where the HTML was retrieved from. Used to resolve relative URLs to absolute URLs, that occur
     before the HTML declares a {@code <base href>} tag.
     @param parser alternate {@link Parser#xmlParser() parser} to use.
     @return sane HTML
     */
    public static Document parse(String html, String baseUri, Parser parser) {
        return parser.parseInput(html, baseUri);
    }

    /**
     Parse HTML into a Document. As no base URI is specified, absolute URL detection relies on the HTML including a
     {@code <base href>} tag.

     @param html HTML to parse
     @return sane HTML

     @see #parse(String, String)
     */
    public static Document parse(String html) {
        return Parser.parse(html, "");
    }

    /**
     Parse XML into a Document. The parser will make a sensible, balanced document tree out of any HTML.

     @param xml    XML to parse
     @param baseUri The URL where the HTML was retrieved from. Used to resolve relative URLs to absolute URLs, that occur
     before the HTML declares a {@code <base href>} tag.
     @return sane XML
     */
    public static Document parseXML(String xml, String baseUri) {
        return Parser.parseXml(xml, baseUri);
    }

    /**
     Parse XML into a Document. The parser will make a sensible, balanced document tree out of any HTML.

     @param xml    XML to parse
     @return sane XML
     */
    public static Document parseXML(String xml) {
        return Parser.parseXml(xml, "");
    }

    /**
     Parse XML into a Document. The parser will make a sensible, balanced document tree out of any HTML.

     @param in          input stream to read. Make sure to close it after parsing.
     @param charsetName (optional) character set of file contents. Set to {@code null} to determine from {@code http-equiv} meta tag, if
     present, or fall back to {@code UTF-8} (which is often safe to do).
     @param baseUri     The URL where the HTML was retrieved from, to resolve relative links against.
     @throws IOException if the file could not be found, or read, or if the charsetName is invalid.
     @return sane XML
     */
    public static Document parseXML(InputStream in, String charsetName, String baseUri) throws IOException {
        return parse(in, charsetName, baseUri, Parser.xmlParser());
    }

    /**
     Parse XML into a Document. The parser will make a sensible, balanced document tree out of any HTML.

     @param in          input stream to read. Make sure to close it after parsing.
     @param charsetName (optional) character set of file contents. Set to {@code null} to determine from {@code http-equiv} meta tag, if
     present, or fall back to {@code UTF-8} (which is often safe to do).
     @throws IOException if the file could not be found, or read, or if the charsetName is invalid.
     @return sane XML
     */
    public static Document parseXML(InputStream in, String charsetName) throws IOException {
        return parseXML(in, charsetName, "");
    }

    /**
     Parse the contents of a file as HTML.

     @param in          file to load HTML from
     @param charsetName (optional) character set of file contents. Set to {@code null} to determine from {@code http-equiv} meta tag, if
     present, or fall back to {@code UTF-8} (which is often safe to do).
     @param baseUri     The URL where the HTML was retrieved from, to resolve relative links against.
     @return sane HTML

     @throws IOException if the file could not be found, or read, or if the charsetName is invalid.
     */
    public static Document parse(File in, String charsetName, String baseUri) throws IOException {
        return DataUtil.load(in, charsetName, baseUri);
    }

    /**
     Parse the contents of a file as HTML. The location of the file is used as the base URI to qualify relative URLs.

     @param in          file to load HTML from
     @param charsetName (optional) character set of file contents. Set to {@code null} to determine from {@code http-equiv} meta tag, if
     present, or fall back to {@code UTF-8} (which is often safe to do).
     @return sane HTML

     @throws IOException if the file could not be found, or read, or if the charsetName is invalid.
     @see #parse(File, String, String)
     */
    public static Document parse(File in, String charsetName) throws IOException {
        return DataUtil.load(in, charsetName, in.getAbsolutePath());
    }

     /**
     Read an input stream, and parse it to a Document.

     @param in          input stream to read. Make sure to close it after parsing.
     @param charsetName (optional) character set of file contents. Set to {@code null} to determine from {@code http-equiv} meta tag, if
     present, or fall back to {@code UTF-8} (which is often safe to do).
     @param baseUri     The URL where the HTML was retrieved from, to resolve relative links against.
     @return sane HTML

     @throws IOException if the file could not be found, or read, or if the charsetName is invalid.
     */
    public static Document parse(InputStream in, String charsetName, String baseUri) throws IOException {
        return DataUtil.load(in, charsetName, baseUri);
    }

    /**
     Read an input stream, and parse it to a Document. You can provide an alternate parser, such as a simple XML
     (non-HTML) parser.

     @param in          input stream to read. Make sure to close it after parsing.
     @param charsetName (optional) character set of file contents. Set to {@code null} to determine from {@code http-equiv} meta tag, if
     present, or fall back to {@code UTF-8} (which is often safe to do).
     @param baseUri     The URL where the HTML was retrieved from, to resolve relative links against.
     @param parser alternate {@link Parser#xmlParser() parser} to use.
     @return sane HTML

     @throws IOException if the file could not be found, or read, or if the charsetName is invalid.
     */
    public static Document parse(InputStream in, String charsetName, String baseUri, Parser parser) throws IOException {
        return DataUtil.load(in, charsetName, baseUri, parser);
    }

    /**
     Parse a fragment of HTML, with the assumption that it forms the {@code body} of the HTML.

     @param bodyHtml body HTML fragment
     @param baseUri  URL to resolve relative URLs against.
     @return sane HTML document

     @see Document#body()
     */
    public static Document parseBodyFragment(String bodyHtml, String baseUri) {
        return Parser.parseBodyFragment(bodyHtml, baseUri);
    }

    /**
     Parse a fragment of HTML, with the assumption that it forms the {@code body} of the HTML.

     @param bodyHtml body HTML fragment
     @return sane HTML document

     @see Document#body()
     */
    public static Document parseBodyFragment(String bodyHtml) {
        return Parser.parseBodyFragment(bodyHtml, "");
    }

    /**
     Get safe HTML from untrusted input HTML, by parsing input HTML and filtering it through a white-list of permitted
     tags and attributes.

     @param bodyHtml  input untrusted HTML (body fragment)
     @param baseUri   URL to resolve relative URLs against
     @param whitelist white-list of permitted HTML elements
     @return safe HTML (body fragment)

     @see Cleaner#clean(Document)
     */
    public static String clean(String bodyHtml, String baseUri, Whitelist whitelist) {
        Document dirty = parseBodyFragment(bodyHtml, baseUri);
        Cleaner cleaner = new Cleaner(whitelist);
        Document clean = cleaner.clean(dirty);
        return clean.body().html();
    }

    /**
     Get safe HTML from untrusted input HTML, by parsing input HTML and filtering it through a white-list of permitted
     tags and attributes.

     @param bodyHtml  input untrusted HTML (body fragment)
     @param whitelist white-list of permitted HTML elements
     @return safe HTML (body fragment)

     @see Cleaner#clean(Document)
     */
    public static String clean(String bodyHtml, Whitelist whitelist) {
        return clean(bodyHtml, "", whitelist);
    }

    /**
     * Get safe HTML from untrusted input HTML, by parsing input HTML and filtering it through a white-list of
     * permitted
     * tags and attributes.
     *
     * @param bodyHtml input untrusted HTML (body fragment)
     * @param baseUri URL to resolve relative URLs against
     * @param whitelist white-list of permitted HTML elements
     * @param outputSettings document output settings; use to control pretty-printing and entity escape modes
     * @return safe HTML (body fragment)
     * @see Cleaner#clean(Document)
     */
    public static String clean(String bodyHtml, String baseUri, Whitelist whitelist, Document.OutputSettings outputSettings) {
        Document dirty = parseBodyFragment(bodyHtml, baseUri);
        Cleaner cleaner = new Cleaner(whitelist);
        Document clean = cleaner.clean(dirty);
        clean.outputSettings(outputSettings);
        return clean.body().html();
    }

    /**
     Test if the input HTML has only tags and attributes allowed by the Whitelist. Useful for form validation. The input HTML should
     still be run through the cleaner to set up enforced attributes, and to tidy the output.
     @param bodyHtml HTML to test
     @param whitelist whitelist to test against
     @return true if no tags or attributes were removed; false otherwise
     @see #clean(String, Whitelist)
     */
    public static boolean isValid(String bodyHtml, Whitelist whitelist) {
        Document dirty = parseBodyFragment(bodyHtml, "");
        Cleaner cleaner = new Cleaner(whitelist);
        return cleaner.isValid(dirty);
    }
    
}
