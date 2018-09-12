# SVG for iText 7

A project that attempts to draw SVG vector images onto a PDF document,
converting the vector instructions from SVG to PDF.
The project uses the DOM builder from [Batik](https://xmlgraphics.apache.org/batik/) for the input file,
and writes to an output file with [iText 7](https://itextpdf.com).

## TODOs

* implement tag attribute parsers for elements
* create PdfCanvas drawing implementations for all tags
* parse CSS & style attribute
* tests :-)

## Details


## Inspiration


## Dependencies

* logging framework: logback, a slf4j implementation
* batik-transcoder
* itext7 layout

Transitive dependencies are managed with Maven