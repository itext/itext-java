# Webp image support for iText

## Overview

This module extends iText functionality by adding support for WebP image format, 
enabling seamless integration of WebP images into PDF documents.

## Features

- Loads and embeds WebP images into PDF documents
- Works with modern WebP encodings (lossy & lossless)
- Easy integration with existing iText workflows

## Installation

Add the following dependency to your Maven project:

```xml
<dependency>
  <groupId>com.itextpdf</groupId>
  <artifactId>webp-image-support</artifactId>
  <version>{itext.core.version}</version>
</dependency>
```

## Usage

To add WebP image to your document use ImageDataFactory as with any other image type:

```java
// Create a pdf document with WebP image
PdfDocument pdfDocument = new PdfDocument(new PdfWriter(resultFileLocation));
byte[] imageBytes;
try (InputStream fis = FileUtil.getInputStreamForFile(webpFileLocation)) {
    imageBytes = StreamUtil.inputStreamToArray(fis);
}

PdfPage page = pdfDocument.addNewPage();
PdfCanvas canvas = new PdfCanvas(page);
ImageData image = ImageDataFactory.create(imageBytes);
canvas.addImageAt(image, 0, 0, false);
canvas.release();

pdfDocument.close();
// Done!
```