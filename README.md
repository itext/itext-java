**[iText 7 Community][itext]** consists of several jars.

The iText 7 Core/Community release contains:

- ```kernel-x.y.z.jar```: low-level functionality
- ```io-x.y.z.jar```:  low-level functionality
- ```layout-x.y.z.jar```: high-level functionality
- ```forms-x.y.z.jar```: AcroForms
- ```pdfa-x.y.z.jar```: PDF/A-specific functionality
- ```pdftest-x.y.z.jar```: test helper classes
- ```barcode-x.y.z.jar```: use this if you want to create bar codes
- ```hyph-x.y.z.jar```: use this if you want text to be hyphenated
- ```font-asian-x.y.z.jar```: use this is you need CJK functionality (Chinese / Japanese / Korean)
- ```sign-x.y.z.jar```: use this if you need support for digital signatures
- ```styled-xml-parser-x.y.z.jar```: use this if you need support for SVG or html2pdf
- ```svg-x.y.z.jar```: SVG support

The **iText 7 Community** source code is hosted on [Github][github], where you can also [download the latest releases][latest].

You can also [build iText 7 Community from source][building].

We also provide opensource add-ons and tools to complement the core functionality:
- [pdfHTML][pdfhtml] — allows you to easily convert HTML to PDF or iText objects
- [pdfSweep][pdfsweep] — a highly efficient PDF tool to merge, split and redact data
- [RUPS][rups] — a Java tool that can help you debug PDFs

If you have an idea on how to improve **iText 7 Community** and you want to submit code,
please read our [Contribution Guidelines][contributing].

**iText 7** is dual licensed as [AGPL][agpl]/[Commercial software][sales].

AGPL is a free / open source software license.

This doesn't mean the software is [gratis][gratis]!

Buying a license is mandatory as soon as you develop commercial activities
distributing the iText software inside your product or deploying it on a network
without disclosing the source code of your own applications under the AGPL license.
These activities include:

- offering paid services to customers as an ASP
- serving PDFs on the fly in the cloud or in a web application
- shipping iText with a closed source product

Contact [sales] for more info.

[agpl]: LICENSE.md
[building]: BUILDING.md
[contributing]: CONTRIBUTING.md
[itext]: https://itextpdf.com/
[github]: https://github.com/itext/itext7
[latest]: https://github.com/itext/itext7/releases/latest
[sales]: https://itextpdf.com/sales
[gratis]: https://en.wikipedia.org/wiki/Gratis_versus_libre
[rups]: https://github.com/itext/i7j-rups
[pdfhtml]: https://github.com/itext/i7j-pdfhtml
[pdfsweep]: https://github.com/itext/i7j-pdfsweep
