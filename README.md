<p align="center">
    <img src="./assets/iText_Logo_Small.png" alt="Logo iText">
</p>

[![Maven Central](https://img.shields.io/maven-central/v/com.itextpdf/itext-core)](https://mvnrepository.com/artifact/com.itextpdf/itext-core)
[![AGPL License](https://img.shields.io/badge/license-AGPL-blue.svg)](https://github.com/itext/itext7/blob/master/LICENSE.md)
[![GitHub all releases](https://img.shields.io/github/downloads/itext/itext7/total)](https://github.com/itext/itext7/releases/latest)
![GitHub commit activity (branch)](https://img.shields.io/github/commit-activity/m/itext/itext7)

iText Core/Community is a high-performance, battle-tested library that allows you to create, adapt,
inspect and maintain PDF documents, allowing you to add PDF
functionality to your software projects with ease. It is also available for .[NET][itext7net] (C#).

### The key features of iText Core/Community are:

* Core library:
  * PDF creation with the use of our layout engine
  * PDF manipulation, e.g. merging multiple PDFs into one, adding new content, ...
  * PDF digital signing 
  * PDF form creation and manipulation
  * Working with PDF/A documents
  * Working with PDF/UA documents
  * FIPS-compliant cryptography
  * Barcode generation
  * SVG support
* [Addons:][all products]
  * Converting XML/HTML & CSS to PDF [repo][pdfhtml], [info][pdfhtmlproduct]
  * Redacting sensitive information in PDF documents [repo][pdfsweep], [info][pdfsweepproduct]
  * Support for international character sets (e.g. Arabic, Chinese, Hebrew, Thai, ...) [info][calligraph]
  * Optimize PDF documents for reduced file size, and increased performance [info][optimizer]
  * Flattening XFA documents [info][xfa]
  * PDF debugging [repo][rups], [info][rupsproduct]

Want to discover what's possible? Head over to our [Demo Lab](https://itextpdf.com/demos)! It contains a collection of
demo applications ready to use online!

### Getting started

The easiest way to get started is to use Maven, just add the following entries to your pom.xml file:

```html

<properties>
  <itext.version>REPLACE_WITH_DESIRED_ITEXT_VERSION</itext.version>
</properties>
<dependencies>
  <dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext-core</artifactId>
    <version>${itext.version}</version>
    <type>pom</type>
  </dependency>
  <dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>bouncy-castle-adapter</artifactId>
    <version>${itext.version}</version>
  </dependency>
</dependencies>
```

For more advanced use cases, please refer to
the [Installation guidelines](https://kb.itextpdf.com/home/it7kb/installation-guidelines).
You can also [build iText Community from source][building].

### Hello PDF!

The following example shows how easy it is to create a simple PDF document:

```java
package com.itextpdf.hellopdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.FileNotFoundException;

public class HelloPdfApp {

    public static void main(String[] args) throws FileNotFoundException {
      try (Document document = new Document(new PdfDocument(new PdfWriter("./hello-pdf.pdf")))) {
            document.add(new Paragraph("Hello PDF!"));
        }
    }
}
```

### Examples

This is a small subset of examples to get you started. For more advanced examples, refer to our [Knowledge Base](https://kb.itextpdf.com/home/it7kb/examples) or the following links:
[Examples repo](https://github.com/itext/i7js-examples), [Signing examples](https://github.com/itext/i7js-signing-examples). 

Some of the output PDF files will be incorrectly displayed by the GitHub previewer, so be sure to download them to see the correct
results.

| Description                                | Link                                                                                                                                                                                                                                                            |
|--------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Basic layout**                           |                                                                                                                                                                                                                                                                 |
| Change text properties                     | [Java](https://github.com/itext/i7js-examples/blob/master/src/main/java/com/itextpdf/samples/sandbox/layout/ParagraphTextWithStyle.java), [PDF](https://github.com/itext/i7js-examples/blob/master/cmpfiles/sandbox/layout/cmp_paragraphTextWithStyle.pdf)      |
| Creating a simple table                    | [Java](https://github.com/itext/i7js-examples/blob/master/src/main/java/com/itextpdf/samples/sandbox/tables/SimpleTable9.java),  [PDF](https://github.com/itext/i7js-examples/blob/master/cmpfiles/sandbox/tables/cmp_simple_table9.pdf)                        |
| Add an image to a PDF document             | [Java](https://github.com/itext/i7js-examples/blob/master/src/main/java/com/itextpdf/samples/sandbox/images/MultipleImages.java), [PDF](https://github.com/itext/i7js-examples/blob/master/cmpfiles/sandbox/images/cmp_multiple_images.pdf)                     |
| Create a list                              | [Java](https://github.com/itext/i7js-examples/blob/master/src/main/java/com/itextpdf/samples/sandbox/objects/NestedLists.java), [PDF](https://github.com/itext/i7js-examples/blob/master/cmpfiles/sandbox/objects/cmp_nested_list.pdf)                          |                                                                                                                                                                                                      
| Add a watermark                            | [Java](https://github.com/itext/i7js-examples/blob/master/src/main/java/com/itextpdf/samples/sandbox/events/Watermarking.java),  [PDF](https://github.com/itext/i7js-examples/blob/master/cmpfiles/sandbox/events/cmp_watermarkings.pdf)                        |
| Add links to navigate within a document    | [Java](https://github.com/itext/i7js-examples/blob/master/src/main/java/com/itextpdf/samples/sandbox/annotations/AddLinkAnnotation5.java),  [PDF](https://github.com/itext/i7js-examples/blob/master/cmpfiles/sandbox/annotations/cmp_add_link_annotation5.pdf) |
| Create a popup annotation                  | [Java](https://github.com/itext/i7js-examples/blob/master/src/main/java/com/itextpdf/samples/sandbox/annotations/MovePopup.java),  [PDF](https://github.com/itext/i7js-examples/blob/master/cmpfiles/sandbox/annotations/cmp_move_popup.pdf)                    |
| Change font                                | [Java](https://github.com/itext/i7js-examples/blob/master/src/main/java/com/itextpdf/samples/sandbox/layout/ParagraphTextWithStyle.java)                                                                                                                        |
| Add form fields                            | [Java](https://kb.itextpdf.com/home/it7kb/examples/forms-in-itext-core-8-0-0)                                                                                                                                                                                   |
 <br>                                       |                                                                                                                                                                                                                                                                 |
| **General document settings**              |                                                                                                                                                                                                                                                                 |
| Change page size and margin                | [Java](https://github.com/itext/i7js-examples/blob/master/src/main/java/com/itextpdf/samples/sandbox/layout/PageSizeAndMargins.java),  [PDF](https://github.com/itext/i7js-examples/blob/master/cmpfiles/sandbox/layout/cmp_pageSizeAndMargins.pdf)             |
| Write PDF to byte array instead of to disk | [Java](https://stackoverflow.com/a/67411657/10015628)                                                                                                                                                                                                           |
| Change page rotation                       | [Java](https://github.com/itext/i7js-examples/blob/master/src/main/java/com/itextpdf/samples/sandbox/events/PageRotation.java),  [PDF](https://github.com/itext/i7js-examples/blob/master/cmpfiles/sandbox/events/cmp_page_rotation.pdf)                        |
| Add header and footer                      | [Java](https://github.com/itext/i7js-examples/blob/master/src/main/java/com/itextpdf/samples/sandbox/events/TextFooter.java),  [PDF](https://github.com/itext/i7js-examples/blob/master/cmpfiles/sandbox/events/cmp_text_footer.pdf)                            |
| Merge documents                            | [Java](https://github.com/itext/i7js-examples/blob/master/src/main/java/com/itextpdf/samples/sandbox/merge/AddCover1.java),  [PDF](https://github.com/itext/i7js-examples/blob/master/cmpfiles/sandbox/merge/cmp_add_cover.pdf)                                 |
| Flatten annotations                        | [Java](https://kb.itextpdf.com/home/it7kb/examples/high-level-annotation-flattening)                                                                                                                                                                            |
| <br>                                       |                                                                                                                                                                                                                                                                 |
| **PDF/UA, PDF/A**                          |                                                                                                                                                                                                                                                                 |
| Create PDF/UA document                     | [Java](https://github.com/itext/i7js-examples/blob/master/src/main/java/com/itextpdf/samples/sandbox/pdfua/PdfUA.java),  [PDF](https://github.com/itext/i7js-examples/blob/master/cmpfiles/sandbox/pdfua/cmp_pdf_ua.pdf)                                        |
| Create PDF/A-3 document                    | [Java](https://github.com/itext/i7js-examples/blob/master/src/main/java/com/itextpdf/samples/sandbox/pdfa/PdfA3.java)                                                                                                                                           |
| Create PDF/A-4 document                    | [Java](https://github.com/itext/itext-publications-examples-java/blob/master/src/main/java/com/itextpdf/samples/sandbox/pdfa/PdfA4.java)                                                                                                                                           |
| Create WTPDF document                      | [Java](https://github.com/itext/itext-publications-examples-java/blob/master/src/main/java/com/itextpdf/samples/sandbox/pdfua/Wtpdf.java)                                                                                                                                          |
| Create ZUGFeRD/Factur-X document           | [Java](https://github.com/itext/itext-publications-examples-java/blob/master/src/main/java/com/itextpdf/samples/sandbox/zugferd/BasicSample.java), [PDF](https://github.com/itext/itext-publications-examples-java/blob/master/cmpfiles/sandbox/zugferd/cmp_invoice_with_zugferd.pdf)|
| <br>                                       |                                                                                                                                                                                                                                                                 |
| **FIPS**                                   |                                                                                                                                                                                                                                                                 |
| Enable FIPS                                | [Java](https://kb.itextpdf.com/home/it7kb/releases/release-itext-core-8-0-0/breaking-changes-for-itext-core-8-0-0/bouncy-castle-changes)                                                                                                                        |
| FIPS SHA3  example                         | [Java](https://kb.itextpdf.com/home/it7kb/examples/fips-sha3-examples-for-itext-core-8-0-0)                                                                                                                                                                     |
| <br>                                       |                                                                                                                                                                                                                                                                 |
| **Convert HTML and CSS to PDF**            | [Link to repo](https://github.com/itext/i7j-pdfhtml)                                                                                                                                                                                                            |
| Convert simple HTML doc to PDF             | [Java](https://kb.itextpdf.com/home/it7kb/ebooks/itext-7-converting-html-to-pdf-with-pdfhtml)                                                                                                                                                                   |
| <br>                                       |                                                                                                                                                                                                                                                                 |
| **Secure redaction of content**            | [Link to repo](https://github.com/itext/i7j-pdfsweep)                                                                                                                                                                                                           |
| Redacting content                          | [Java](https://kb.itextpdf.com/home/it7kb/examples/removing-content-with-pdfsweep)                                                                                                                                                                              |
| Redact based on regex                      | [Java](https://itextpdf.com/products/pdf-redaction-pdfsweep)                                                                                                                                                                                                    |
| <br>                                       |                                                                                                                                                                                                                                                                 |
| **Support complex writing systems**        | [Link to docs](https://itextpdf.com/products/pdfcalligraph)                                                                                                                                                                                                     |
| Add Arabic text                            | [Java](https://github.com/itext/i7js-examples/blob/master/src/main/java/com/itextpdf/samples/sandbox/typography/arabic/ArabicWordSpacing.java), [PDF](https://github.com/itext/i7js-examples/blob/master/cmpfiles/sandbox/typography/cmp_ArabicWordSpacing.pdf) |
| <br>                                       |                                                                                                                                                                                                                                                                 |
| **Optimizing PDFs**                        | [Link to docs](https://itextpdf.com/products/compress-pdf-pdfoptimizer)                                                                                                                                                                                         |
| Reduce size of PDF                         | [Java](https://itextpdf.com/products/compress-pdf-pdfoptimizer)                                                                                                                                                                                                 |
| <br>                                       |                                                                                                                                                                                                                                                                 |
| **XFA flattening**                         | [Link to docs](https://itextpdf.com/products/flatten-pdf-pdfxfa)                                                                                                                                                                                                |
| Flatten an XFA document                    | [Java](https://itextpdf.com/products/flatten-pdf-pdfxfa)                                                                                                                                                                                                        |
| <br>                                       |                                                                                                                                                                                                                                                                 |
| **RUPS**                                   | [Link to repo](https://github.com/itext/i7j-rups)                                                                                                                                                                                                               |
| Debug a PDF                                | [Java](https://github.com/itext/i7j-rups/releases/latest)                                                                                                                                                                                                       |

### FAQs, tutorials, etc. ###
Check out the [iText Knowledge Base](https://kb.itextpdf.com) for the [iText Jump-start tutorial](https://kb.itextpdf.com/home/it7kb/ebooks/itext-jump-start-tutorial-for-java) and other tutorials, [FAQs](https://kb.itextpdf.com/home/it7kb/faq) and more. For specific information and examples relating to digital signatures and iText, make sure to check the [Digital Signatures Hub](https://kb.itextpdf.com/home/it7kb/digital-signatures-hub).

Many common questions have already been answered
on [Stack Overflow](https://stackoverflow.com/questions/tagged/itext+itext7), so make sure to also check there.

### Contributing

Many people have contributed to **iText Core/Community** over the years. If you've found a bug, a mistake in documentation, or have a hot new feature you want to implement, we welcome your contributions.

Small changes or fixes can be submitted as a [Pull Request](https://github.com/itext/itext7/pulls), while for major changes we request you contact us at community@apryse.com so we can better coordinate our efforts and prevent duplication of work.

Please read our [Contribution Guidelines][contributing] for details on code submissions, coding rules, and more.

### Licensing

**iText** is dual licensed as [AGPL][agpl]/[Commercial software][sales].

AGPL is a free/open-source software license, however, this doesn't mean the software is [gratis][gratis]!

The AGPL is a copyleft license, which means that any derivative work must also be licensed under the same terms. If you’re using iText in software or a service which cannot comply with the AGPL terms, we have a commercial license available that exempts you from such obligations.

Contact [Sales] for more info.

[agpl]: LICENSE.md

[building]: BUILDING.md

[contributing]: CONTRIBUTING.md

[layoutMd]: layout/README.md

[itext]: https://itextpdf.com/

[github]: https://github.com/itext/itext7

[latest]: https://github.com/itext/itext7/releases/latest

[sales]: https://itextpdf.com/sales

[gratis]: https://en.wikipedia.org/wiki/Gratis_versus_libre

[rups]: https://github.com/itext/i7j-rups

[pdfhtml]: https://github.com/itext/i7j-pdfhtml

[pdfsweep]: https://github.com/itext/i7j-pdfsweep

[itext7net]: https://github.com/itext/itext7-dotnet

[pdfsweepproduct]: https://itextpdf.com/products/pdf-redaction-pdfsweep

[optimizer]: https://itextpdf.com/products/compress-pdf-pdfoptimizer

[all products]: https://itextpdf.com/products

[pdfhtmlproduct]: https://itextpdf.com/products/itext-pdf-html

[xfa]: https://itextpdf.com/products/flatten-pdf-pdfxfa

[rupsproduct]: https://itextpdf.com/products/rups

[calligraph]: https://itextpdf.com/products/pdfcalligraph
